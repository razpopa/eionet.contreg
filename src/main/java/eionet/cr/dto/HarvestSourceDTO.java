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
/**
 *
 */
package eionet.cr.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * A DTO representing a harvest source.
 *
 * @author altnyris
 */
public class HarvestSourceDTO implements Serializable, Cloneable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant COUNT_UNAVAIL_THRESHOLD. */
    public static final int COUNT_UNAVAIL_THRESHOLD = 5;

    /** The Constant DEFAULT_REFERRALS_INTERVAL. */
    public static final int DEFAULT_REFERRALS_INTERVAL = 60480;

    /** The source id. */
    private Integer sourceId;

    /** The url. */
    private String url;

    /** The emails. */
    private String emails;

    /** The time created. */
    private Date timeCreated;

    /** The statements. */
    private Integer statements;

    /** The count unavail. */
    private Integer countUnavail;

    /** The last harvest. */
    private Date lastHarvest;

    /** The last harvest failed. */
    private boolean lastHarvestFailed;

    /** The interval minutes. */
    private Integer intervalMinutes;

    /** The url hash. */
    private Long urlHash;

    /** The priority source. */
    private boolean prioritySource;

    /** The owner. */
    private String owner;

    /** The permanent error. */
    private boolean permanentError;

    /** The media type. */
    private String mediaType;

    /** The last harvest id. */
    private Integer lastHarvestId;

    /** The is sparql endpoint. */
    private boolean isSparqlEndpoint;

    /**
     * Instantiates a new harvest source dto.
     */
    public HarvestSourceDTO() {
    }

    /**
     * Gets the source id.
     *
     * @return the sourceId
     */
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * Sets the source id.
     *
     * @param sourceId            the sourceId to set
     */
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the emails.
     *
     * @return the emails
     */
    public String getEmails() {
        return emails;
    }

    /**
     * Sets the emails.
     *
     * @param emails            the emails to set
     */
    public void setEmails(String emails) {
        this.emails = emails;
    }

    /**
     * Gets the time created.
     *
     * @return the timeCreated
     */
    public Date getTimeCreated() {
        return timeCreated;
    }

    /**
     * Sets the time created.
     *
     * @param timeCreated            the timeCreated to set
     */
    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    /**
     * Gets the statements.
     *
     * @return the statements
     */
    public Integer getStatements() {
        return statements;
    }

    /**
     * Sets the statements.
     *
     * @param statements            the statements to set
     */
    public void setStatements(Integer statements) {
        this.statements = statements;
    }

    /**
     * Gets the count unavail.
     *
     * @return the countUnavail
     */
    public Integer getCountUnavail() {
        return countUnavail;
    }

    /**
     * Sets the count unavail.
     *
     * @param countUnavail            the countUnavail to set
     */
    public void setCountUnavail(Integer countUnavail) {
        this.countUnavail = countUnavail;
    }

    /**
     * Checks if is unavailable.
     *
     * @return boolean
     */
    public boolean isUnavailable() {

        return countUnavail != null && countUnavail.intValue() >= COUNT_UNAVAIL_THRESHOLD;
    }

    /**
     * Gets the interval minutes.
     *
     * @return Integer
     */
    public Integer getIntervalMinutes() {
        return intervalMinutes;
    }

    /**
     * Sets the interval minutes.
     *
     * @param intervalMinutes the new interval minutes
     */
    public void setIntervalMinutes(Integer intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    /**
     * Gets the last harvest.
     *
     * @return the lastHarvest
     */
    public Date getLastHarvest() {
        return lastHarvest;
    }

    /**
     * Sets the last harvest.
     *
     * @param lastHarvest            the lastHarvest to set
     */
    public void setLastHarvest(Date lastHarvest) {
        this.lastHarvest = lastHarvest;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuffer().append("Harvest source ").append(url).toString();
    }

    /**
     * Checks if is last harvest failed.
     *
     * @return the lastHarvestFailed
     */
    public boolean isLastHarvestFailed() {
        return lastHarvestFailed;
    }

    /**
     * Sets the last harvest failed.
     *
     * @param lastHarvestFailed            the lastHarvestFailed to set
     */
    public void setLastHarvestFailed(boolean lastHarvestFailed) {
        this.lastHarvestFailed = lastHarvestFailed;
    }

    /**
     * Gets the url hash.
     *
     * @return the urlHash
     */
    public Long getUrlHash() {
        return urlHash;
    }

    /**
     * Sets the url hash.
     *
     * @param urlHash            the urlHash to set
     */
    public void setUrlHash(Long urlHash) {
        this.urlHash = urlHash;
    }

    /**
     * Checks if is priority source.
     *
     * @return true, if is priority source
     */
    public boolean isPrioritySource() {
        return prioritySource;
    }

    /**
     * Sets the priority source.
     *
     * @param prioritySource the new priority source
     */
    public void setPrioritySource(boolean prioritySource) {
        this.prioritySource = prioritySource;
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Checks if is permanent error.
     *
     * @return true, if is permanent error
     */
    public boolean isPermanentError() {
        return permanentError;
    }

    /**
     * Sets the permanent error.
     *
     * @param permanentError the new permanent error
     */
    public void setPermanentError(boolean permanentError) {
        this.permanentError = permanentError;
    }

    /**
     * Gets the media type.
     *
     * @return the media type
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Sets the media type.
     *
     * @param mediaType the new media type
     */
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Gets the last harvest id.
     *
     * @return the lastHarvestId
     */
    public Integer getLastHarvestId() {
        return lastHarvestId;
    }

    /**
     * Sets the last harvest id.
     *
     * @param lastHarvestId            the lastHarvestId to set
     */
    public void setLastHarvestId(Integer lastHarvestId) {
        this.lastHarvestId = lastHarvestId;
    }

    /**
     * Creates a harvest source with given inputs.
     *
     * @param url the url
     * @param prioritySource the priority source
     * @param intervalMinutes the interval minutes
     * @param owner the owner
     * @return the harvest source dto
     */
    public static HarvestSourceDTO create(String url, boolean prioritySource, int intervalMinutes, String owner) {

        HarvestSourceDTO result = new HarvestSourceDTO();
        result.setUrl(url);
        result.setPrioritySource(prioritySource);
        result.setIntervalMinutes(intervalMinutes);
        result.setOwner(owner);
        return result;
    }

    /**
     * Gets the harvest urgency score.
     *
     * @return the harvest urgency score
     */
    public double getHarvestUrgencyScore() {

        // if harvest interval is set to 0, then so is its urgency score
        if (intervalMinutes == null || intervalMinutes.intValue() <= 0) {
            return 0.0d;
        }

        // urgency score can only be calculated if at least the last harvest
        // or creation time is known (and interval is >0, as already assured above)
        if (lastHarvest == null && timeCreated == null) {
            return 0.0d;
        }

        Date lastTime = lastHarvest == null ? null : new Date(lastHarvest.getTime());
        if (lastTime == null) {

            // if last time is not known, then last time to pseudo-value
            // which is (creation time - harvest interval)

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeCreated);
            calendar.add(Calendar.SECOND, -1 * intervalMinutes.intValue() * 60);
            lastTime = calendar.getTime();
        }

        long secondsSinceLastTime = (System.currentTimeMillis() / 1000L) - (lastTime.getTime() / 1000L);
        long intervalSeconds = intervalMinutes.longValue() * 60L;
        return ((double) secondsSinceLastTime) / ((double) intervalSeconds);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public HarvestSourceDTO clone() {
        try {
            return (HarvestSourceDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported");
        }
    }

    /**
     * Checks if is sparql endpoint.
     *
     * @return the isSparqlEndpoint
     */
    public boolean isSparqlEndpoint() {
        return isSparqlEndpoint;
    }

    /**
     * Sets the sparql endpoint.
     *
     * @param isSparqlEndpoint the isSparqlEndpoint to set
     */
    public void setSparqlEndpoint(boolean isSparqlEndpoint) {
        this.isSparqlEndpoint = isSparqlEndpoint;
    }
}
