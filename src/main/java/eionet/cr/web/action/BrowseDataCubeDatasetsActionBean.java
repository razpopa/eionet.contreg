package eionet.cr.web.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eionet.cr.common.Predicates;
import eionet.cr.common.Subjects;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HelperDAO;
import eionet.cr.dto.SearchResultDTO;
import eionet.cr.util.Pair;
import eionet.cr.util.SortingRequest;
import eionet.cr.util.pagination.PagingRequest;
import eionet.cr.util.sql.PairReader;
import eionet.cr.web.action.factsheet.FactsheetActionBean;
import eionet.cr.web.util.CustomPaginatedList;

/**
 * An action bean enabling to browse subjects whose rdf:type is that of {@link Subjects.DATACUBE_DATA_SET}.
 *
 * @author jaanus
 */
@UrlBinding("/dataCubeDatasets")
public class BrowseDataCubeDatasetsActionBean extends DisplaytagSearchActionBean {

    /** */
    private static final Logger LOGGER = Logger.getLogger(BrowseDataCubeDatasetsActionBean.class);

    /** */
    private static final String[] LABEL_PREDICATES = {Predicates.DCTERMS_TITLE, Predicates.RDFS_LABEL, Predicates.DC_TITLE, Predicates.FOAF_NAME};

    /** */
    private static final List<HashMap<String, String>> AVAIL_COLUMNS = createAvailColumns();

    /** */
    private static final String JSP = "/pages/browseDataCubeDatasets.jsp";

    /** */
    private CustomPaginatedList<Pair<String, String>> datasets;

    /**
     *
     * @return
     */
    @DefaultHandler
    public Resolution defaultEvent() {

        String sortColumn = getSortColumnByAlias(sort);
        SortingRequest sortRequest = StringUtils.isBlank(sortColumn) ? null : SortingRequest.create(sortColumn, dir);
        PagingRequest pageRequest = PagingRequest.create(page);

        SearchResultDTO<Pair<String, String>> searchResult = null;
        HelperDAO dao = DAOFactory.get().getDao(HelperDAO.class);
        try {
            searchResult = dao.getUriLabels(Subjects.DATACUBE_DATA_SET, pageRequest, sortRequest, LABEL_PREDICATES);
        } catch (DAOException e) {
            LOGGER.error("DataCube datasets search error", e);
            addWarningMessage("A technical error occurred when searching for the available datasets" + e.getMessage());
        }

        datasets = new CustomPaginatedList<Pair<String, String>>(this, searchResult, pageRequest.getItemsPerPage());
        return new ForwardResolution(JSP);
    }

    /**
     * @return the datasets
     */
    public CustomPaginatedList<Pair<String, String>> getDatasets() {
        return datasets;
    }

    /**
     *
     * @param alias
     * @return
     */
    private String getSortColumnByAlias(String alias) {

        String predicate = null;
        if (StringUtils.isNotBlank(alias)) {
            for (HashMap<String, String> columnConf : AVAIL_COLUMNS) {
                if (alias.equals(columnConf.get("alias"))) {
                    predicate = columnConf.get("sortColumn");
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
     *
     * @return
     */
    public List<HashMap<String, String>> getAvailColumns() {
        return AVAIL_COLUMNS;
    }

    /**
     *
     * @return
     */
    private static List<HashMap<String, String>> createAvailColumns() {

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("alias", "uri");
        map.put("isFactsheetLink", "true");
        map.put("title", "URI");
        map.put("hint", "The URI of the dataset");
        map.put("sortable", Boolean.TRUE.toString());
        map.put("sortColumn", PairReader.LEFTCOL);
        map.put("width", "60%");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("alias", "label");
        map.put("title", "Label");
        map.put("hint", "The label of the dataset");
        map.put("sortable", Boolean.TRUE.toString());
        map.put("sortColumn", PairReader.RIGHTCOL);
        map.put("width", "40%");
        list.add(map);

        return Collections.unmodifiableList(list);
    }
}
