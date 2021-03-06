/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 2.0.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Jaanus Heinlaid, Tieto Eesti
 */
package eionet.cr.harvest.scheduled;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openrdf.rio.RDFParseException;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import eionet.cr.common.JobScheduler;
import eionet.cr.config.GeneralConfig;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HarvestSourceDAO;
import eionet.cr.dto.HarvestSourceDTO;
import eionet.cr.dto.UrgentHarvestQueueItemDTO;
import eionet.cr.harvest.CurrentHarvests;
import eionet.cr.harvest.Harvest;
import eionet.cr.harvest.HarvestException;
import eionet.cr.harvest.PullHarvest;
import eionet.cr.harvest.PushHarvest;
import eionet.cr.web.security.CRUser;

/**
 *
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public class HarvestingJob implements StatefulJob, ServletContextListener {

    /** Enum for the names of attributes used for propagating job state specifics across its runs. */
    public enum JobStateAttrs {
        /** The date-time of the job's last execution's finish. */
        LAST_FINISH
    };

    /** Static logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(HarvestingJob.class);

    /** Max number of urgent harvests executed per one interval. */
    private static final int DEFAULT_URGENT_HARVEST_LIMIT = 20;

    /** Simple name of this class. */
    public static final String NAME = HarvestingJob.class.getSimpleName();

    /** Number of minutes in an hour. */
    private static final int MINUTES = 60;

    /** Upper limit for the number of urgent harvests performed at one interval. */
    public static final int URGENT_HARVEST_LIMIT = GeneralConfig.getIntProperty(
            GeneralConfig.HARVESTER_URGENT_HARVESTS_PER_INTERVAL, DEFAULT_URGENT_HARVEST_LIMIT);

    /** The batch harvesting queue as retrieved from database. */
    private static List<HarvestSourceDTO> batchQueue;

    /** Hours when the batch harvesting should be active. */
    private static List<HourSpan> batchHarvestingHours;

    /** Interval (in seconds) at which this job (i.e. represented by this class) runs. */
    private static Integer intervalSeconds;

    /** Number of sources that can be batch-harvested during one interval. */
    private static Integer batchHarvestLimit;

    /** Total number of minutes per day when the batch-harvesting is active. */
    private static Integer dailyActiveMinutes;

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext jobExecContext) throws JobExecutionException {

        // Ensure that the job rests the configured amount of time after last run, before proceeding.
        ensureRestingTime(jobExecContext);

        try {
            // Harvest urgent queue.
            handleUrgentQueue();

            // Harvest batch queue.
            if (isBatchHarvestingEnabled()) {
                handleBatchQueue();
            }
        } catch (Exception e) {
            throw new JobExecutionException(e.toString(), e);
        } finally {
            // State that no harvest is currently queued.
            CurrentHarvests.setQueuedHarvest(null);
            // Reset batch-harvesting queue
            batchQueue = null;
        }
    }

    /**
     *
     */
    protected void handleUrgentQueue() {

        try {
            int counter = 0;
            UrgentHarvestQueueItemDTO queueItem = null;
            for (queueItem = UrgentHarvestQueue.poll(); queueItem != null; queueItem = UrgentHarvestQueue.poll()) {

                if (counter++ == URGENT_HARVEST_LIMIT) {
                    // Just a security measure to avoid an infinite loop here.
                    LOGGER.info("Handled " + URGENT_HARVEST_LIMIT + " urgent harvests, resting until next interval");
                    break;
                }

                String url = queueItem.getUrl();
                if (!StringUtils.isBlank(url)) {

                    if (queueItem.isPushHarvest()) {
                        pushHarvest(url, queueItem.getPushedContent(), queueItem.getUserName());
                    } else {
                        HarvestSourceDTO src = DAOFactory.get().getDao(HarvestSourceDAO.class).getHarvestSourceByUrl(url);
                        if (src != null) {
                            pullHarvest(src, true, queueItem.getUserName());
                        } else {
                            LOGGER.warn("Urgent harvest URL could not be found in harvest source:" + url);
                        }
                    }
                }
            }
        } catch (DAOException e) {
            LOGGER.error(e.toString(), e);
        }
    }

    /**
     *
     * @throws DAOException
     */
    private void handleBatchQueue() throws DAOException {

        // Even if it is not currently a batch harvesting hour, we shall proceed to getting the list of next scheduled sources, and
        // looping over them, as there are specific sources for which the batch-harvesting hours should be ignored. Currently these
        // are sources whose harvest interval is less than 8 hours.

        if (isBatchHarvestingHour()) {
            LOGGER.trace("Handling batch queue...");
        }

        // Initialize batch queue collection.
        batchQueue = Collections.synchronizedList(new ArrayList<HarvestSourceDTO>());

        // Initialize collection for sources that will have to be deleted.
        HashSet<String> sourcesToDelete = new HashSet<String>();

        // Initialize harvest source DAO.
        HarvestSourceDAO sourceDao = DAOFactory.get().getDao(HarvestSourceDAO.class);

        // Get next scheduled sources.
        List<HarvestSourceDTO> nextScheduledSources = getNextScheduledSources();
        if (isBatchHarvestingHour()) {
            LOGGER.trace(nextScheduledSources.size() + " next scheduled sources found");
        }

        // Loop over next scheduled sources.
        for (HarvestSourceDTO sourceDTO : nextScheduledSources) {

            // If source is marked with permanent error then increase its unavailability count if it's a
            // priority source, or simply delete it if it's not a priority source.
            // If source not marked with permanent and its unavailability count is >=5 and it's a
            // non-priority source then delete it.
            // In all other cases, add the harvest source to the batch-harvest queue.
            if (sourceDTO.isPermanentError()) {
                if (sourceDTO.isPrioritySource()) {
                    LOGGER.trace("Increasing unavailability count of permanent-error priority source " + sourceDTO.getUrl());
                    sourceDao.increaseUnavailableCount(sourceDTO.getUrl());
                } else {
                    LOGGER.debug(sourceDTO.getUrl() + "  will be deleted as a non-priority source with permanent error");
                    sourcesToDelete.add(sourceDTO.getUrl());
                }
            } else if (sourceDTO.getCountUnavail() >= 5) {
                if (!sourceDTO.isPrioritySource()) {
                    LOGGER.debug(sourceDTO.getUrl() + "  will be deleted as a non-priority source with unavailability >= 5");
                    sourcesToDelete.add(sourceDTO.getUrl());
                }
            } else {
                batchQueue.add(sourceDTO);
            }
        }

        // Harvest the batch harvest queue (if anything added to it).
        for (Iterator<HarvestSourceDTO> iter = batchQueue.iterator(); iter.hasNext();) {

            HarvestSourceDTO sourceDTO = iter.next();

            // For sources where interval is less than 8 hours, the batch harvesting hours doesn't apply.
            // They are always harvested.
            boolean ignoreBatchHarvestingHour = sourceDTO.getIntervalMinutes().intValue() < 480;
            if (isBatchHarvestingHour() || ignoreBatchHarvestingHour) {

                // Remove source from batch harvest queue before starting its harvest.
                iter.remove();

                LOGGER.trace("Going to batch-harvest " + sourceDTO.getUrl());
                pullHarvest(sourceDTO, false, CRUser.BATCH_HARVEST.getUserName());
            }
        }

        // Delete sources that were found necessary to delete (if any).
        if (!sourcesToDelete.isEmpty()) {

            LOGGER.debug("Deleting " + sourcesToDelete.size() + " sources found above");
            for (Iterator<String> iter = sourcesToDelete.iterator(); iter.hasNext();) {

                String sourceUrl = iter.next();
                if (CurrentHarvests.contains(sourceUrl)) {
                    iter.remove();
                    LOGGER.debug("Skipping deletion of " + sourceUrl + " because it is currently being harvested");
                }
            }
            sourceDao.removeHarvestSources(sourcesToDelete);
        }
    }

    /**
     *
     * @return List<HarvestSourceDTO>
     * @throws DAOException
     */
    public static List<HarvestSourceDTO> getNextScheduledSources() throws DAOException {

        if (isBatchHarvestingEnabled()) {
            return DAOFactory.get().getDao(HarvestSourceDAO.class).getNextScheduledSources(getSourcesLimitForInterval());
        } else {
            return new ArrayList<HarvestSourceDTO>();
        }
    }

    /**
     * This method should be used classes other than {@link HarvestingJob} itself. It returns list of harvest source currently in
     * batch queue. The method returns clone of the original queue, to prevent clients from corrupting the original.
     *
     * @return List<HarvestSourceDTO>
     */
    public static List<HarvestSourceDTO> getBatchQueue() {

        ArrayList<HarvestSourceDTO> clone = new ArrayList<HarvestSourceDTO>();
        if (batchQueue != null) {
            clone.addAll(batchQueue);
        }
        return clone;
    }

    /**
     * Returns hours when the harvester is allowed to do batch harvesting. The clock hours when batch harvesting should be active,
     * are given as comma separated from-to spans (e.g 10-15, 19-23) in the configuration file. In every span both "from" and "to"
     * are inclusive, and "from" must be sooner than "to". So, to say from 18.00 to 9.00, the configuration value must be 18-23,0-8.
     * (leave the field completely empty to disable batch harvesting)
     *
     * @return list containing the activeHours
     */
    public static synchronized List<HourSpan> getBatchHarvestingHours() {

        if (batchHarvestingHours == null) {

            batchHarvestingHours = new ArrayList<HourSpan>();
            String hoursString = GeneralConfig.getProperty(GeneralConfig.HARVESTER_BATCH_HARVESTING_HOURS);
            if (!StringUtils.isBlank(hoursString)) {

                String[] spans = hoursString.trim().split(",");
                for (int i = 0; i < spans.length; i++) {

                    String span = spans[i].trim();
                    if (span.length() > 0) {

                        String[] spanBoundaries = span.split("-");

                        int from = Integer.parseInt(spanBoundaries[0].trim());
                        int to = Integer.parseInt(spanBoundaries[1].trim());

                        from = Math.max(0, Math.min(23, from));
                        to = Math.max(0, Math.min(23, to));
                        if (to < from) {
                            to = from;
                        }

                        batchHarvestingHours.add(new HourSpan(from, to));
                    }
                }
            }
        }

        return batchHarvestingHours;
    }

    /**
     * Returns the interval in seconds where the harvester checks for checks for new urgent or scheduled tasks. The interval can't
     * be more than 3600 seconds or less than 5 seconds. The value is retrieved from the general configuration file.
     *
     * @return the interval in seconds
     */
    public static synchronized Integer getIntervalSeconds() {

        if (intervalSeconds == null) {
            intervalSeconds = new Integer(GeneralConfig.getHarvestingJobIntervalSeconds());
        }

        return intervalSeconds;
    }

    /**
     * Returns the the number of sources that can be batch-harvested during one interval. The value is retrieved from the general
     * configuration file. The default is 5.
     *
     * @return the upper limit
     */
    public static Integer getBatchHarvestLimit() {

        if (batchHarvestLimit == null) {

            int value = GeneralConfig.getIntProperty(GeneralConfig.HARVESTER_SOURCES_UPPER_LIMIT, 5);
            batchHarvestLimit = Integer.valueOf(value);
        }

        return batchHarvestLimit;
    }

    /**
     * Returns the interval in minutes where the harvester checks for checks for new urgent or scheduled tasks. Value can be less
     * than 1.0.
     *
     * @return interval in minutes
     */
    public static float getIntervalMinutes() {

        return getIntervalSeconds().floatValue() / MINUTES;
    }

    /**
     * Calculates how many minutes a day the batch harvester is active. If the batch harvesting is from 5-6, then the return value
     * is 120.
     *
     * @return the dailyActiveMinutes
     */
    public static Integer getDailyActiveMinutes() {

        if (dailyActiveMinutes == null) {

            /* determine the amount of total active minutes in a day */

            int minutes = 0;
            List<HourSpan> activeHours = getBatchHarvestingHours();
            for (HourSpan hourSpan : activeHours) {
                minutes += ((hourSpan.length()) + 1) * MINUTES;
            }

            dailyActiveMinutes = minutes > 1440 ? new Integer(1440) : new Integer(minutes);
        }

        return dailyActiveMinutes;
    }

    /**
     * Executes push harvest of the given pushed content under the given URL by the given user.
     *
     * @param url Source URL under which the content should be pushed.
     * @param pushedContent The content to harvest.
     * @param userName The user who is "pushing". May be null, in which case the harvester assumes default.
     */
    private void pushHarvest(String url, String pushedContent, String userName) {

        // if the source is currently being harvested then return
        if (url != null && CurrentHarvests.contains(url)) {
            LOGGER.debug("The source is currently being harvested, so skipping it");
            return;
        }

        try {
            HarvestSourceDAO harvestSourceDAO = DAOFactory.get().getDao(HarvestSourceDAO.class);
            HarvestSourceDTO harvestSource = harvestSourceDAO.getHarvestSourceByUrl(url);
            if (harvestSource == null) {
                harvestSource = new HarvestSourceDTO();
                harvestSource.setUrl(url);
                harvestSourceDAO.addSource(harvestSource);
            }

            Harvest harvest = new PushHarvest(pushedContent, url);
            harvest.setHarvestUser(CRUser.PUSH_HARVEST.getUserName());
            executeHarvest(harvest);
        } catch (DAOException e) {
            LOGGER.error(e.toString(), e);
        } catch (HarvestException e) {
            LOGGER.error(e.toString(), e);
        }
    }

    /**
     * Executes pull harvest of the given harvest source.
     *
     * @param harvestSource The harvest source.
     * @param isUrgentHarvest True if this is an urgent harvest.
     * @param userName Initiating user. Might be null, in which case harvester is expected to assume default.
     * @throws DAOException When problem with creating new harvest record.
     */
    private void pullHarvest(HarvestSourceDTO harvestSource, boolean isUrgentHarvest, String userName) throws DAOException {

        if (harvestSource != null) {

            // if the source is currently being harvested then return
            if (CurrentHarvests.contains(harvestSource.getUrl())) {
                LOGGER.debug("The source is currently being harvested, so skipping it");
                return;
            }

            PullHarvest harvest = new PullHarvest(harvestSource);
            harvest.setOnDemandHarvest(isUrgentHarvest);
            harvest.setHarvestUser(userName);
            executeHarvest(harvest);
        }
    }

    /**
     *
     * @param harvest
     */
    private void executeHarvest(Harvest harvest) {

        if (harvest != null) {
            CurrentHarvests.setQueuedHarvest(harvest);
            try {
                harvest.execute();
            } catch (HarvestException e) {
                if (e.getCause() instanceof RDFParseException) {
                    LOGGER.warn("Got exception from " + harvest.getClass().getSimpleName() + " [" + harvest.getContextUrl()
                            + "] - " + e.toString());
                } else {
                    LOGGER.error(
                            "Got exception from " + harvest.getClass().getSimpleName() + " [" + harvest.getContextUrl() + "]", e);
                }
            } finally {
                CurrentHarvests.setQueuedHarvest(null);
            }
        }
    }

    /**
     * Calculates how many harvesting segments there is in a day. If the harvester is active 120 minutes and the interval is 15
     * seconds (i.e. 1/4 minute, then there are 480 harvesting segments in the day.
     *
     * @return the number of harvesting segments
     */
    private static int getNumberOfSegments() {

        return Math.round(getDailyActiveMinutes().floatValue() / getIntervalMinutes());
    }

    /**
     * Calculates how many sources we need to harvest in this round, but if the amount is over the limit we lower it to the limit.
     * The purpose is to avoid tsunamis of harvesting.
     * <p>
     * Example: If there are 4320 time segments, and there are 216 sources with a score of 1.0 or above, the number of sources to
     * harvest in this round is 216 / 4320 = 0.05. This we then round up to one.
     *
     * @return the limit of sources returned
     */
    private static int getSourcesLimitForInterval() {
        int limit = 0;
        try {
            int numOfSegments = getNumberOfSegments();
            Long numberOfSources = DAOFactory.get().getDao(HarvestSourceDAO.class).getUrgencySourcesCount();
            int upperLimit = getBatchHarvestLimit();

            // Round up to 1 if there is something at all to harvest
            limit = (int) Math.ceil((double) numberOfSources / (double) numOfSegments);
            if (upperLimit > 0 && limit > upperLimit) {
                limit = upperLimit;
            }
        } catch (DAOException e) {
            LOGGER.error(e.toString(), e);
        }

        if (limit < 1) {
            limit = 1;
        }

        return limit;
    }

    /**
     * Returns true if batch the current hour is a batch harvesting hour. Otherwise returns false.
     *
     * @return
     */
    private boolean isBatchHarvestingHour() {

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        List<HourSpan> activeHours = getBatchHarvestingHours();
        for (HourSpan hourSpan : activeHours) {
            if (hourSpan.includes(currentHour)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if batch harvesting is enabled, otherwise returns false.
     *
     * @return true if batch harvesting is enabled
     */
    private static boolean isBatchHarvestingEnabled() {
        return getDailyActiveMinutes().intValue() > 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        try {
            JobDetail jobDetails = new JobDetail(HarvestingJob.NAME, JobScheduler.class.getName(), HarvestingJob.class);

            HarvestingJobListener listener = new HarvestingJobListener();
            jobDetails.addJobListener(listener.getName());
            JobScheduler.registerJobListener(listener);

            JobScheduler.scheduleIntervalJob((long) getIntervalSeconds().intValue() * (long) 1000, jobDetails);

            LOGGER.debug(getClass().getSimpleName() + " scheduled with interval seconds " + getIntervalSeconds()
                    + ", batch harvesting hours = " + getBatchHarvestingHours());
        } catch (Exception e) {
            LOGGER.fatal("Error when scheduling " + getClass().getSimpleName() + " with interval seconds " + getIntervalSeconds(),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    /**
     * Ensure that the job rests the configured amount of time after last run, before proceeding.
     *
     * @param jobExecContext Job execution context as provided by Quartz.
     */
    private void ensureRestingTime(JobExecutionContext jobExecContext) {

        JobDataMap jobDataMap = jobExecContext.getJobDetail().getJobDataMap();
        Object o = jobDataMap.get(HarvestingJob.JobStateAttrs.LAST_FINISH.toString());

        Date lastFinish = o instanceof Date ? (Date) o : null;
        if (lastFinish != null) {

            long lastFinishMillisAgo = Math.max((System.currentTimeMillis() - lastFinish.getTime()), 0L);
            long millisToRest = (getIntervalSeconds().longValue() * 1000L) - lastFinishMillisAgo;
            if (millisToRest > 0) {
                try {
                    Thread.sleep(millisToRest);
                } catch (InterruptedException e) {
                    LOGGER.warn("Resting interrupted: " + e);
                }
            }
        }
    }
}
