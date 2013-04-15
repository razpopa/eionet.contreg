package eionet.cr.web.action.admin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import at.jku.xlwrap.common.XLWrapException;
import eionet.cr.common.Predicates;
import eionet.cr.common.Subjects;
import eionet.cr.common.TempFilePathGenerator;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HarvestSourceDAO;
import eionet.cr.dao.HelperDAO;
import eionet.cr.dto.HarvestSourceDTO;
import eionet.cr.dto.SearchResultDTO;
import eionet.cr.harvest.OnDemandHarvester;
import eionet.cr.util.FileDeletionJob;
import eionet.cr.util.Pair;
import eionet.cr.util.xlwrap.XLWrapUploadType;
import eionet.cr.util.xlwrap.XLWrapUtil;
import eionet.cr.web.action.AbstractActionBean;
import eionet.cr.web.action.factsheet.ObjectsInSourceActionBean;
import eionet.cr.web.security.CRUser;

/**
 * Action bean for uploading an MS Excel or OpenDocument spreadsheet file into the RDF model and triple store. Pre-configured types
 * of files are supported, e.g. a file containing metadata of Digital Agenda Scoreboard indicators, a file containing metadata of
 * Digital Agenda Scoreboard breakdowns, etc.
 *
 * {@link XLWrapUtil} is used for performing the parsing and storage into triple store.
 *
 * @author jaanus
 */
@UrlBinding("/admin/xlwrapUpload.action")
public class XLWrapUploadActionBean extends AbstractActionBean {

    /** */
    private static final String UPLOADED_GRAPH_ATTR = XLWrapUploadActionBean.class.getSimpleName() + ".uploadedGraph";

    /** */
    private static final Logger LOGGER = Logger.getLogger(XLWrapUploadActionBean.class);

    /** */
    public static final String JSP = "/pages/admin/staging/xlwUpload.jsp";

    /** */
    private XLWrapUploadType uploadType = XLWrapUploadType.INDICATOR;

    /** */
    private FileBean fileBean;

    /** */
    private String uploadedGraphUri;

    /** */
    private boolean clearGraph = true;

    /** */
    private List<Pair<String, String>> datasets;

    /** */
    private String targetDataset;

    /** */
    private boolean clearDataset;

    /**
     *
     * @return
     */
    @DefaultHandler
    public Resolution get() {
        HttpSession session = getContext().getRequest().getSession();
        if (session != null) {
            uploadedGraphUri = ObjectUtils.toString(session.getAttribute(UPLOADED_GRAPH_ATTR), null);
            session.removeAttribute(UPLOADED_GRAPH_ATTR);
        }
        return new ForwardResolution(JSP);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Resolution upload() {

        boolean isObservationsUpload = false;
        if (uploadType == null) {
            addGlobalValidationError("Missing upload type!");
            return new ForwardResolution(JSP);
        } else if (fileBean == null || fileBean.getSize() == 0) {
            addGlobalValidationError("Uploaded file missing or empty!");
            return new ForwardResolution(JSP);
        }
        else if (XLWrapUploadType.OBSERVATION.equals(uploadType)) {
            if (StringUtils.isBlank(targetDataset)) {
                addGlobalValidationError("Target dataset must be selected!");
                return new ForwardResolution(JSP);
            }
            else {
                isObservationsUpload = true;
            }
        }

        File spreadsheetFile = TempFilePathGenerator.generate("xls");
        try {
            fileBean.save(spreadsheetFile);
        } catch (IOException e) {
            addCautionMessage("Failed saving the upload file to a temporary location!");
            LOGGER.error("Failed saving " + fileBean.getFileName() + " to " + spreadsheetFile);
            return new ForwardResolution(JSP);
        }

        try {
            String dataset = isObservationsUpload ? targetDataset : null;
            dataset = StringUtils.replace(dataset, "/dataset/", "/data/");
            boolean clear = isObservationsUpload ? clearDataset : clearGraph;
            StatementListener stmtListener = new StatementListener();

            int resourceCount = XLWrapUtil.importMapping(uploadType, spreadsheetFile, dataset, clear, stmtListener);
            startPostHarvests(stmtListener.getHarvestUris());

            addSystemMessage(resourceCount
                    + " resources successfully imported!\n Click on on the below link to explore them further.");

            getContext().setSessionAttribute(UPLOADED_GRAPH_ATTR, isObservationsUpload ? dataset : uploadType.getGraphUri());
            return new RedirectResolution(getClass());

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            addCautionMessage("An I/O error occurred!");
            return new ForwardResolution(JSP);
        } catch (XLWrapException e) {
            LOGGER.error(e.getMessage(), e);
            addCautionMessage("A mapping failure occurred!");
            return new ForwardResolution(JSP);
        } catch (OpenRDFException e) {
            LOGGER.error(e.getMessage(), e);
            addCautionMessage("A repository access error occurred!");
            return new ForwardResolution(JSP);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            addCautionMessage("An unexpected technical error occurred!");
            return new ForwardResolution(JSP);
        } finally {
            FileDeletionJob.register(spreadsheetFile);
        }
    }

    /**
     *
     * @return
     */
    public Resolution cancel() {
        return new RedirectResolution(AdminWelcomeActionBean.class);
    }

    /**
     * Validates the the user is authorised for any operations on this action bean. If user not authorised, redirects to the
     * {@link AdminWelcomeActionBean} which displays a proper error message. Will be run on any events, since no specific events
     * specified in the {@link ValidationMethod} annotation.
     */
    @ValidationMethod(priority = 1)
    public void validateUserAuthorised() {

        if (getUser() == null || !getUser().isAdministrator()) {
            addGlobalValidationError("You are not authorized for this operation!");
            getContext().setSourcePageResolution(new RedirectResolution(AdminWelcomeActionBean.class));
        }
    }

    /**
     * @param uploadType
     *            the uploadType to set
     */
    public void setUploadType(XLWrapUploadType uploadType) {
        this.uploadType = uploadType;
    }

    /**
     * @param fileBean
     *            the fileBean to set
     */
    public void setFileBean(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    /**
     *
     * @return
     */
    public List<XLWrapUploadType> getUploadTypes() {
        return Arrays.asList(XLWrapUploadType.values());
    }

    /**
     *
     * @return
     */
    public Class getObjectsInSourceActionBeanClass() {
        return ObjectsInSourceActionBean.class;
    }

    /**
     * @return the uploadType
     */
    public XLWrapUploadType getUploadType() {
        return uploadType;
    }

    /**
     * @return the uploadedGraphUri
     */
    public String getUploadedGraphUri() {
        return uploadedGraphUri;
    }

    /**
     * @param clearGraph the clearGraph to set
     */
    public void setClearGraph(boolean clearGraph) {
        this.clearGraph = clearGraph;
    }

    /**
     * @return the clearGraph
     */
    public boolean isClearGraph() {
        return clearGraph;
    }

    /**
     * Lazy getter for the datasets.
     *
     * @return the datasets
     * @throws DAOException
     */
    public List<Pair<String, String>> getDatasets() throws DAOException {

        if (datasets == null) {
            String[] labels = {Predicates.DCTERMS_TITLE, Predicates.RDFS_LABEL, Predicates.FOAF_NAME};
            HelperDAO dao = DAOFactory.get().getDao(HelperDAO.class);
            SearchResultDTO<Pair<String, String>> searchResult = dao.getUriLabels(Subjects.DATACUBE_DATA_SET, null, null, labels);
            if (searchResult != null) {
                datasets = searchResult.getItems();
            }
        }
        return datasets;
    }

    /**
     * @param clearDataset the clearDataset to set
     */
    public void setClearDataset(boolean clearDataset) {
        this.clearDataset = clearDataset;
    }

    /**
     * @param targetDataset the targetDataset to set
     */
    public void setTargetDataset(String targetDataset) {
        this.targetDataset = targetDataset;
    }

    /**
     * @return the targetDataset
     */
    public String getTargetDataset() {
        return targetDataset;
    }

    /**
     * @return the clearDataset
     */
    public boolean isClearDataset() {
        return clearDataset;
    }

    /**
     *
     * @param harvestUris
     */
    private void startPostHarvests(Set<String> harvestUris) {

        if (harvestUris == null || harvestUris.isEmpty()) {
            return;
        }

        LOGGER.debug("Starting post-harvests...");
        HarvestSourceDAO dao = DAOFactory.get().getDao(HarvestSourceDAO.class);
        for (String uri : harvestUris) {
            LOGGER.debug("Going to harvest " + uri);
            startPostHarvest(uri, dao);
        }
    }

    /**
     *
     * @param uri
     * @param dao
     */
    private void startPostHarvest(String uri, HarvestSourceDAO dao) {

        HarvestSourceDTO dto = new HarvestSourceDTO();
        dto.setUrl(StringUtils.substringBefore(uri, "#"));
        dto.setEmails("");
        dto.setIntervalMinutes(0);
        dto.setPrioritySource(false);
        dto.setOwner(null);
        try {
            dao.addSourceIgnoreDuplicate(dto);
            OnDemandHarvester.harvest(dto.getUrl(), CRUser.APPLICATION.getUserName());
        } catch (Exception e) {
            LOGGER.error("Failed to harvest " + uri, e);
        }
    }

    /**
     * @author jaanus
     */
    private static final class StatementListener implements RDFHandler {

        /** */
        private HashSet<String> harvestUris = new HashSet<String>();

        /*
         * (non-Javadoc)
         *
         * @see org.openrdf.rio.RDFHandler#endRDF()
         */
        @Override
        public void endRDF() throws RDFHandlerException {
            // Auto-generated method stub
        }

        /*
         * (non-Javadoc)
         *
         * @see org.openrdf.rio.RDFHandler#handleComment(java.lang.String)
         */
        @Override
        public void handleComment(String arg0) throws RDFHandlerException {
            // Auto-generated method stub
        }

        /*
         * (non-Javadoc)
         *
         * @see org.openrdf.rio.RDFHandler#handleNamespace(java.lang.String, java.lang.String)
         */
        @Override
        public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
            // Auto-generated method stub
        }

        /*
         * (non-Javadoc)
         *
         * @see org.openrdf.rio.RDFHandler#handleStatement(org.openrdf.model.Statement)
         */
        @Override
        public void handleStatement(Statement stmt) throws RDFHandlerException {

            URI predicateURI = stmt.getPredicate();
            if (Predicates.DAS_TIMEPERIOD.equals(predicateURI.stringValue())) {
                Value object = stmt.getObject();
                if (object instanceof URI) {
                    harvestUris.add(object.stringValue());
                }
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see org.openrdf.rio.RDFHandler#startRDF()
         */
        @Override
        public void startRDF() throws RDFHandlerException {
            // Auto-generated method stub
        }

        /**
         * @return the harvestUris
         */
        public HashSet<String> getHarvestUris() {
            return harvestUris;
        }

    }
}
