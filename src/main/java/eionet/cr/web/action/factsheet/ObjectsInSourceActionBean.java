package eionet.cr.web.action.factsheet;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;

import eionet.cr.common.Predicates;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HarvestSourceDAO;
import eionet.cr.dao.HelperDAO;
import eionet.cr.dao.SearchDAO;
import eionet.cr.dto.SubjectDTO;
import eionet.cr.util.Pair;
import eionet.cr.util.SortOrder;
import eionet.cr.util.SortingRequest;
import eionet.cr.util.pagination.PagingRequest;
import eionet.cr.web.action.AbstractSearchActionBean;
import eionet.cr.web.util.columns.SearchResultColumn;
import eionet.cr.web.util.columns.SubjectPredicateColumn;
import eionet.cr.web.util.tabs.FactsheetTabMenuHelper;
import eionet.cr.web.util.tabs.TabElement;
import eionet.cr.web.util.tabs.TabId;

/**
 * 
 * @author <a href="mailto:jaak.kapten@tieto.com">Jaak Kapten</a>
 * 
 */
@UrlBinding("/objectsInSource.action")
public class ObjectsInSourceActionBean extends AbstractSearchActionBean<SubjectDTO> {

    /** */
    private String uri;
    private long uriHash;
    private long anonHash;
    private boolean noCriteria;

    /** */
    private boolean skipAnonymous = true;

    /** */
    private List<TabElement> tabs;

    /** */
    private String factsheetUri;

    /**
     * 
     * @return
     * @throws DAOException
     */
    @DefaultHandler
    public Resolution init() throws DAOException {
        return search();
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.cr.web.action.AbstractSearchActionBean#search()
     */
    public Resolution search() throws DAOException {

        if (resultList == null || resultList.size() == 0) {
            Pair<Integer, List<SubjectDTO>> result =
                    DAOFactory
                            .get()
                            .getDao(SearchDAO.class)
                            .searchBySource(uri, skipAnonymous, PagingRequest.create(getPageN()),
                                    new SortingRequest(getSortP(), SortOrder.parse(getSortO())));

            resultList = result.getRight();
            matchCount = result.getLeft();
        }

        if (StringUtils.isBlank(factsheetUri)) {
            factsheetUri = uri;
        }
        HelperDAO helperDAO = DAOFactory.get().getDao(HelperDAO.class);
        SubjectDTO subject = helperDAO.getSubject(factsheetUri);

        FactsheetTabMenuHelper helper = new FactsheetTabMenuHelper(factsheetUri, subject, factory.getDao(HarvestSourceDAO.class));
        tabs = helper.getTabs(TabId.OBJECTS_IN_SOURCE);

        return new ForwardResolution("/pages/factsheet/objectsInSource.jsp");
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.cr.web.action.AbstractSearchActionBean#getColumns()
     */
    public List<SearchResultColumn> getColumns() {

        ArrayList<SearchResultColumn> list = new ArrayList<SearchResultColumn>();

        SubjectPredicateColumn col = new SubjectPredicateColumn();
        col.setPredicateUri(Predicates.RDF_TYPE);
        col.setTitle("Type");
        col.setSortable(true);
        list.add(col);

        col = new SubjectPredicateColumn();
        col.setPredicateUri(Predicates.RDFS_LABEL);
        col.setTitle("Label");
        col.setSortable(true);
        list.add(col);

        return list;
    }

    /**
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return
     */
    public long getUriHash() {
        return uriHash;
    }

    /**
     * @param uriHash
     */
    public void setUriHash(long uriHash) {
        this.uriHash = uriHash;
    }

    /**
     * @return
     */
    public long getAnonHash() {
        return anonHash;
    }

    /**
     * @param anonHash
     */
    public void setAnonHash(long anonHash) {
        this.anonHash = anonHash;
    }

    /**
     * @return
     */
    public boolean isNoCriteria() {
        return noCriteria;
    }

    /**
     * @param noCriteria
     */
    public void setNoCriteria(boolean noCriteria) {
        this.noCriteria = noCriteria;
    }

    /**
     * @return the tabs
     */
    /**
     * @return
     */
    public List<TabElement> getTabs() {
        return tabs;
    }

    /**
     * @param tabs the tabs to set
     */
    /**
     * @param tabs
     */
    public void setTabs(List<TabElement> tabs) {
        this.tabs = tabs;
    }

    /**
     * @return the skipAnonymous
     */
    /**
     * @return
     */
    public boolean isSkipAnonymous() {
        return skipAnonymous;
    }

    /**
     * @param skipAnonymous the skipAnonymous to set
     */
    /**
     * @param includeAnonymous
     */
    public void setSkipAnonymous(boolean includeAnonymous) {
        this.skipAnonymous = includeAnonymous;
    }

    /**
     * @return the factsheetUri
     */
    public String getFactsheetUri() {
        return factsheetUri;
    }

    /**
     * @param factsheetUri the factsheetUri to set
     */
    public void setFactsheetUri(String factsheetUri) {
        this.factsheetUri = factsheetUri;
    }

}
