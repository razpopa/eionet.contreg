package eionet.cr.web.action;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.ScoreboardSparqlDAO;
import eionet.cr.dto.SkosItemDTO;
import eionet.cr.util.Pair;
import eionet.cr.web.action.factsheet.FactsheetActionBean;

/**
 * An action bean for browsing codelists starting with a particular URI prefix.
 *
 * @author jaanus
 */
@UrlBinding("/codelists")
public class BrowseCodelistsActionBean extends AbstractActionBean {

    /** */
    private static final Logger LOGGER = Logger.getLogger(BrowseCodelistsActionBean.class);

    /** */
    public static final String CODELISTS_PREFIX = "http://semantic.digital-agenda-data.eu/codelist/";

    /** */
    private static final String JSP = "/pages/browseCodelists.jsp";

    /** */
    private List<Pair<String, String>> codelists;

    /** */
    private List<SkosItemDTO> codelistItems;

    /** */
    private String codelistUri;

    /**
     *
     * @return
     */
    @DefaultHandler
    public Resolution defaultEvent() {

        ScoreboardSparqlDAO dao = DAOFactory.get().getDao(ScoreboardSparqlDAO.class);
        try {
            codelists = dao.getCodelists(CODELISTS_PREFIX);
        } catch (DAOException e) {
            LOGGER.error("Error when retrieving codelists whose URI starts with " + CODELISTS_PREFIX, e);
            addWarningMessage("A technical error occurred when when retrieving available codelists" + e.getMessage());
        }

        if (StringUtils.isBlank(codelistUri) && codelists != null && !codelists.isEmpty()) {
            codelistUri = codelists.iterator().next().getLeft();
        }

        if (StringUtils.isNotBlank(codelistUri)) {
            try {
                codelistItems = dao.getCodelistItems(codelistUri);
            } catch (DAOException e) {
                LOGGER.error("Error when retrieving items of this codelist: " + codelistUri, e);
                addWarningMessage("A technical error occurred when when retrieving items of the selected codelist"
                        + e.getMessage());
            }
        }

        return new ForwardResolution(JSP);
    }

    /**
     *
     * @return
     */
    public Resolution metadata() {
        if (StringUtils.isNotBlank(codelistUri)) {
        return new RedirectResolution(FactsheetActionBean.class).addParameter("uri", codelistUri);
        }
        else{
            addWarningMessage("No codelist selected!");
            return new ForwardResolution(JSP);
        }
    }

    /**
     * @return the codelistUri
     */
    public String getCodelistUri() {
        return codelistUri;
    }

    /**
     * @param codelistUri
     *            the codelistUri to set
     */
    public void setCodelistUri(String codelistUri) {
        this.codelistUri = codelistUri;
    }

    /**
     * @return the codelists
     */
    public List<Pair<String, String>> getCodelists() {
        return codelists;
    }

    /**
     * @return the codelistItems
     */
    public List<SkosItemDTO> getCodelistItems() {
        return codelistItems;
    }

    /**
     *
     * @return
     */
    public Class getFactsheetActionBeanClass() {
        return FactsheetActionBean.class;
    }
}
