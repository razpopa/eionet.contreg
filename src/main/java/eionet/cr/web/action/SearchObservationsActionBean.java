package eionet.cr.web.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eionet.cr.common.Predicates;
import eionet.cr.common.Subjects;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HelperDAO;
import eionet.cr.dao.SearchDAO;
import eionet.cr.dto.SearchResultDTO;
import eionet.cr.dto.SubjectDTO;
import eionet.cr.util.Pair;
import eionet.cr.util.SortingRequest;
import eionet.cr.util.pagination.PagingRequest;
import eionet.cr.web.action.factsheet.FactsheetActionBean;
import eionet.cr.web.util.CustomPaginatedList;

/**
 * An action bean for browsing the available DataCube observations (i.e. resources of type
 * http://purl.org/linked-data/cube#Observation).
 *
 * @author jaanus
 */
@UrlBinding("/searchObservations")
public class SearchObservationsActionBean extends DisplaytagSearchActionBean {

    /** */
    private static final Logger LOGGER = Logger.getLogger(SearchObservationsActionBean.class);

    /** */
    private static final String[] LABEL_PREDICATES = {};

    /** */
    private static final String FILTER_VALUES_ATTR_NAME_TEMPLATE = SearchObservationsActionBean.class.getSimpleName()
            + ".alias.values";

    /** */
    private static final String JSP = "/pages/searchObservations.jsp";

    /** */
    private static final String FILTER_PARAM_PREFIX = "";

    /** */
    private static final List<HashMap<String, String>> AVAIL_FILTERS = createAvailFilters();

    /** */
    private static final List<HashMap<String, String>> AVAIL_COLUMNS = createAvailColumns();

    /** */
    private CustomPaginatedList<SubjectDTO> observations;

    /** */
    private HashMap<String, String> filtersFromRequest;

    /**
     *
     * @return
     */
    @DefaultHandler
    public Resolution init() {
        try {
            loadFilterValues();
        } catch (DAOException e) {
            addWarningMessage("A technical error occurred when loading available search filter values: " + e.getMessage());
        }
        return new ForwardResolution(JSP);
    }

    /**
     *
     * @return
     */
    public Resolution search() {

        ForwardResolution resolution = new ForwardResolution(JSP);

        if (getContext().getRequestParameter("loadFilters") != null) {
            try {
                loadFilterValues();
            } catch (DAOException e) {
                addWarningMessage("A technical error occurred when loading available search filter values: " + e.getMessage());
            }
        }

        HashMap<String, String> filters = getFiltersFromRequest();
        if (filters.isEmpty()) {
            return resolution;
        }
        filters.put(Predicates.RDF_TYPE, Subjects.DATACUBE_OBSERVATION);

        SearchResultDTO<SubjectDTO> searchResult = null;
        String sortPredicate = getColumnPredicateByAlias(sort);
        SortingRequest sortRequest = StringUtils.isBlank(sortPredicate) ? null : SortingRequest.create(sortPredicate, dir);
        PagingRequest pageRequest = PagingRequest.create(page);

        try {
            SearchDAO dao = DAOFactory.get().getDao(SearchDAO.class);
            searchResult = dao.searchByFilters(filters, false, pageRequest, sortRequest, null, false);
        } catch (DAOException e) {
            LOGGER.error("Observation search error", e);
            addWarningMessage("A technical error occurred when searching by the given filters" + e.getMessage());
        }

        observations = new CustomPaginatedList<SubjectDTO>(this, searchResult, pageRequest.getItemsPerPage());
        return resolution;
    }

    /**
     *
     */
    @ValidationMethod(on = {"search"})
    public void validateSearch() {

        if (getFiltersFromRequest().isEmpty()) {
            addWarningMessage("At least one filter must be supplied!");
            getContext().setSourcePageResolution(new ForwardResolution(JSP));
        }
    }

    /**
     *
     * @return
     */
    public Resolution reloadFilters() {

        try {
            loadFilterValues();
        } catch (DAOException e) {
            addWarningMessage("A technical error occurred when reloading observation filters: " + e.getMessage());
        }

        Map params = getContext().getRequest().getParameterMap();
        HashMap paramsWrapped = params == null ? new HashMap() : new HashMap(params);
        paramsWrapped.remove("reloadFilters");
        return new RedirectResolution(getClass(), "search").addParameters(paramsWrapped);
    }

    /**
     * @throws DAOException
     *
     */
    private void loadFilterValues() throws DAOException {

        HelperDAO dao = DAOFactory.get().getDao(HelperDAO.class);
        for (HashMap<String, String> filterConf : AVAIL_FILTERS) {

            String alias = filterConf.get("alias");
            String range = filterConf.get("range");
            String predicate = filterConf.get("predicate");
            List<Pair<String, String>> values = null;

            try {
                if (StringUtils.isNotBlank(range)) {
                    values = dao.getUriLabels(range, null, null, LABEL_PREDICATES).getItems();
                } else {
                    values = dao.getDistinctObjectLabels(predicate, null, null, LABEL_PREDICATES).getItems();
                }
            } catch (DAOException e) {
                LOGGER.error("Error when loading values fo filter: " + alias, e);
                throw e;
            }

            if (values != null && !values.isEmpty()) {
                String sessionAttrName = StringUtils.replace(FILTER_VALUES_ATTR_NAME_TEMPLATE, "alias", alias);
                getContext().setSessionAttribute(sessionAttrName, values);
            }
        }
    }

    /**
     * @return the observations
     */
    public CustomPaginatedList<SubjectDTO> getObservations() {
        return observations;
    }

    /**
     *
     * @return
     */
    private HashMap<String, String> getFiltersFromRequest() {

        if (filtersFromRequest == null) {
            filtersFromRequest = new HashMap<String, String>();
            for (HashMap<String, String> filterConf : AVAIL_FILTERS) {

                String value = getContext().getRequestParameter(FILTER_PARAM_PREFIX + filterConf.get("alias"));
                if (StringUtils.isNotBlank(value)) {
                    filtersFromRequest.put(filterConf.get("predicate"), value);
                }
            }
        }

        return filtersFromRequest;
    }

    /**
     *
     * @param alias
     * @return
     */
    private String getColumnPredicateByAlias(String alias) {

        String predicate = null;
        if (StringUtils.isNotBlank(alias)) {
            for (HashMap<String, String> columnConf : AVAIL_COLUMNS) {
                if (alias.equals(columnConf.get("alias"))) {
                    predicate = columnConf.get("predicate");
                    break;
                }
            }
        }

        return predicate;
    }

    /**
     *
     * @return
     */
    public Class getFactsheetActionBeanClass() {
        return FactsheetActionBean.class;
    }

    /**
     * @return the availFilters
     */
    public List<HashMap<String, String>> getAvailFilters() {
        return AVAIL_FILTERS;
    }

    /**
     * @return the availColumns
     */
    public List<HashMap<String, String>> getAvailColumns() {
        return AVAIL_COLUMNS;
    }

    /**
     *
     * @return
     */
    private static List<HashMap<String, String>> createAvailFilters() {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("alias", "dataSet");
        map.put("title", "Dataset");
        map.put("hint", "Dataset");
        map.put("predicate", Predicates.DATACUBE_DATA_SET);
        // map.put("range", Subjects.DATACUBE_DATASET);
        map.put("sessionAttrName", StringUtils.replace(FILTER_VALUES_ATTR_NAME_TEMPLATE, "alias", map.get("alias")));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "indicator");
        map.put("title", "Indicator");
        map.put("hint", "Indicator");
        map.put("predicate", Predicates.DAS_INDICATOR);
        // map.put("range", Subjects.DAS_INDICATOR);
        map.put("sessionAttrName", StringUtils.replace(FILTER_VALUES_ATTR_NAME_TEMPLATE, "alias", map.get("alias")));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "breakdown");
        map.put("title", "Breakdown");
        map.put("hint", "Indicator");
        map.put("predicate", Predicates.DAS_BREAKDOWN);
        // map.put("range", Subjects.DAS_BREAKDOWN);
        map.put("sessionAttrName", StringUtils.replace(FILTER_VALUES_ATTR_NAME_TEMPLATE, "alias", map.get("alias")));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "refArea");
        map.put("title", "Ref. area");
        map.put("hint", "Reference area");
        map.put("predicate", Predicates.DAS_REFAREA);
        map.put("sessionAttrName", StringUtils.replace(FILTER_VALUES_ATTR_NAME_TEMPLATE, "alias", map.get("alias")));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "timePeriod");
        map.put("title", "Time period");
        map.put("hint", "Time period");
        map.put("predicate", Predicates.DAS_TIMEPERIOD);
        map.put("sessionAttrName", StringUtils.replace(FILTER_VALUES_ATTR_NAME_TEMPLATE, "alias", map.get("alias")));
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "unit");
        map.put("title", "Unit");
        map.put("hint", "Unit");
        map.put("predicate", Predicates.DAS_UNITMEASURE);
        // map.put("range", Subjects.DAS_UNIT);
        map.put("sessionAttrName", StringUtils.replace(FILTER_VALUES_ATTR_NAME_TEMPLATE, "alias", map.get("alias")));
        list.add(map);

        return Collections.unmodifiableList(list);
    }

    /**
     *
     * @return
     */
    private static List<HashMap<String, String>> createAvailColumns() {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = new HashMap<String, String>();
        map = new HashMap<String, String>();
        map.put("alias", "indicator");
        map.put("predicate", Predicates.DAS_INDICATOR);
        map.put("title", "Indicator");
        map.put("hint", "Indicator");
        map.put("sortable", Boolean.TRUE.toString());
        map.put("width", "16%");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "breakdown");
        map.put("predicate", Predicates.DAS_BREAKDOWN);
        map.put("title", "Breakdown");
        map.put("hint", "Breakdown");
        map.put("sortable", Boolean.TRUE.toString());
        map.put("width", "16%");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "refArea");
        map.put("predicate", Predicates.DAS_REFAREA);
        map.put("title", "Ref. area");
        map.put("hint", "Reference area");
        map.put("sortable", Boolean.TRUE.toString());
        map.put("width", "16%");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "timePeriod");
        map.put("predicate", Predicates.DAS_TIMEPERIOD);
        map.put("title", "Time period");
        map.put("hint", "Time period");
        map.put("sortable", Boolean.TRUE.toString());
        map.put("width", "16%");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "unit");
        map.put("predicate", Predicates.DAS_UNITMEASURE);
        map.put("title", "Unit");
        map.put("hint", "Unit of measure");
        map.put("sortable", Boolean.TRUE.toString());
        map.put("width", "16%");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "obsValue");
        map.put("predicate", Predicates.SDMX_OBSVALUE);
        map.put("title", "Value");
        map.put("hint", "Observed value");
        map.put("sortable", Boolean.FALSE.toString());
        map.put("width", "16%");
        list.add(map);

        return Collections.unmodifiableList(list);
    }
}
