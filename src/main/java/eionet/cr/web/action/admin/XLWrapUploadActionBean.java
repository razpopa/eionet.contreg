package eionet.cr.web.action.admin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;

import at.jku.xlwrap.common.XLWrapException;
import eionet.cr.common.TempFilePathGenerator;
import eionet.cr.util.FileDeletionJob;
import eionet.cr.util.xlwrap.XLWrapUploadType;
import eionet.cr.util.xlwrap.XLWrapUtil;
import eionet.cr.web.action.AbstractActionBean;
import eionet.cr.web.action.factsheet.ObjectsInSourceActionBean;

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

        if (uploadType == null) {
            addGlobalValidationError("Missing upload type!");
            return new ForwardResolution(JSP);
        } else if (fileBean == null || fileBean.getSize() == 0) {
            addGlobalValidationError("Uploaded file missing or empty!");
            return new ForwardResolution(JSP);
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
            int resourceCount = XLWrapUtil.importMapping(uploadType, spreadsheetFile, true);
            addSystemMessage(resourceCount + " objects successfully imported!\n Please click on the link below to explore them.");
            getContext().setSessionAttribute(UPLOADED_GRAPH_ATTR, uploadType.getGraphUri());
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
}
