package eionet.cr.web.action.admin.odp;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.log4j.Logger;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.ScoreboardSparqlDAO;
import eionet.cr.dto.SkosItemDTO;
import eionet.cr.web.action.AbstractActionBean;
import eionet.cr.web.action.admin.AdminWelcomeActionBean;
import eionet.cr.web.action.factsheet.FactsheetActionBean;

/**
 * Action bean for generating ODP (Open Data Portal, http://open-data.europa.eu) datasets' metadata packages from the metadata of
 * a selected set of indicators.
 *
 * @author Jaanus
 */
@UrlBinding("/admin/odpPackaging.action")
public class ODPDatasetsPackagingActionBean extends AbstractActionBean {

    /** Static logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ODPDatasetsPackagingActionBean.class);

    /** The JSP listing the available indicator and enabling their packaging into ODP datasets. */
    private static final String INDICATORS_JSP = "/pages/admin/odp/indicators.jsp";

    /** User-submitted values of the "indicator group" filter. */
    List<String> filterIndGroup;

    /** User-submitted values of the "indicator source" filter. */
    List<String> filterIndSource;

    /** Available indicator groups to filter by. */
    List<SkosItemDTO> indGroups;

    /** Available indicator sources to filter by. */
    List<SkosItemDTO> indSources;

    /** The list of indicators matching the applied filters. */
    List<SkosItemDTO> filteredIndicators;

    /** The list of URIs of indicators selected by the user for the submitted bulk operation. */
    List<String> selectedIndicators;

    /**
     * Default event: lists indicators by the given filters.
     *
     * @return Resolution to go to.
     * @throws DAOException In case data access error occurs.
     */
    @DefaultHandler
    public Resolution listIndicators() throws DAOException {
        return new ForwardResolution(INDICATORS_JSP);
    }

    /**
     * Event handler for zipping the metadata of the user-selected indicators.
     *
     * @return Resolution to go to.
     * @throws DAOException In case data access error occurs.
     */
    public Resolution zipSelected() throws DAOException {
        LOGGER.trace("zipSelected(), selected indicators: " + selectedIndicators);
        addCautionMessage("Action not implemented yet!");
        return new ForwardResolution(INDICATORS_JSP);
    }

    /**
     * Event handler for zipping the metadata of *all* indicators matching the given filters.
     *
     * @return Resolution to go to.
     * @throws DAOException In case data access error occurs.
     */
    public Resolution zipAll() throws DAOException {
        LOGGER.trace("zipAll, selected indicators: " + selectedIndicators);
        LOGGER.trace("zipAll, filtered indicators: " + getFilteredIndicators());
        addCautionMessage("Action not implemented yet!");
        return new ForwardResolution(INDICATORS_JSP);
    }

    /**
     * @return the filterIndGroup
     */
    public List<String> getFilterIndGroup() {
        return filterIndGroup;
    }

    /**
     * @param filterIndGroup the filterIndGroup to set
     */
    public void setFilterIndGroup(List<String> filterIndGroup) {
        this.filterIndGroup = filterIndGroup;
    }

    /**
     * @return the filterIndSource
     */
    public List<String> getFilterIndSource() {
        return filterIndSource;
    }

    /**
     * @param filterIndSource the filterIndSource to set
     */
    public void setFilterIndSource(List<String> filterIndSource) {
        this.filterIndSource = filterIndSource;
    }

    /**
     * @return the indGroups
     * @throws DAOException If data access error occurs.
     */
    public List<SkosItemDTO> getIndGroups() throws DAOException {

        // Lazy initialization.
        if (indGroups == null) {
            indGroups = DAOFactory.get().getDao(ScoreboardSparqlDAO.class).getCodelistItems(ScoreboardSparqlDAO.IND_GROUP_CODELIST_URI);
        }
        return indGroups;
    }

    /**
     * @return the indSources
     * @throws DAOException If data access error occurs
     */
    public List<SkosItemDTO> getIndSources() throws DAOException {

        // Lazy initialization.
        if (indSources == null) {
            indSources = DAOFactory.get().getDao(ScoreboardSparqlDAO.class).getCodelistItems(ScoreboardSparqlDAO.IND_SOURCE_CODELIST_URI);
        }
        return indSources;
    }

    /**
     * @return the filteredIndicators
     * @throws DAOException If data access error occurs.
     */
    public List<SkosItemDTO> getFilteredIndicators() throws DAOException {

        if (filteredIndicators == null) {
            filteredIndicators = DAOFactory.get().getDao(ScoreboardSparqlDAO.class).getIndicators(filterIndGroup, filterIndSource);
        }
        return filteredIndicators;
    }

    /**
     * Returns the class representing {@link FactsheetActionBean}. Handy for use in JSP.
     *
     * @return The class.
     */
    public Class<FactsheetActionBean> getFactsheetActionBeanClass() {
        return FactsheetActionBean.class;
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
     * @param selectedIndicators the selectedIndicators to set
     */
    public void setSelectedIndicators(List<String> selectedIndicators) {
        this.selectedIndicators = selectedIndicators;
    }
}
