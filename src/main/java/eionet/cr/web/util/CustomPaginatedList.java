package eionet.cr.web.util;

import java.util.List;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

import eionet.cr.dto.SearchResultDTO;
import eionet.cr.util.SortOrder;
import eionet.cr.web.action.DisplaytagSearchActionBean;

public class CustomPaginatedList<T> implements PaginatedList {

    /** */
    private int fullListSize;
    private List<T> list;
    private int objectsPerPage;
    private int pageNumber;
    private String searchId;
    private String sortCriterion;
    private SortOrderEnum sortDirection;

    /**
     * Default constructor, that simply calls super().
     */
    public CustomPaginatedList() {
        super();
    }

    /**
     * Constructs from the given {@link SearchResultDTO}
     *
     * @param searchResult
     * @param pageSize
     */
    public CustomPaginatedList(DisplaytagSearchActionBean actionBean, SearchResultDTO<T> searchResult, int pageSize) {

        List<T> items = searchResult.getItems();
        int itemsSize = items == null ? 0 : items.size();
        int totalMatchCount = searchResult.getMatchCount();

        fullListSize = totalMatchCount > 0 ? totalMatchCount : itemsSize;
        list = items;
        objectsPerPage = pageSize;
        pageNumber = actionBean.getPage();
        sortCriterion = actionBean.getSort();
        sortDirection = SortOrder.parse(actionBean.getDir()).toDisplayTagEnum();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.displaytag.pagination.PaginatedList#getFullListSize()
     */
    @Override
    public int getFullListSize() {
        return fullListSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.displaytag.pagination.PaginatedList#getList()
     */
    @Override
    public List<T> getList() {
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.displaytag.pagination.PaginatedList#getObjectsPerPage()
     */
    @Override
    public int getObjectsPerPage() {
        return objectsPerPage;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.displaytag.pagination.PaginatedList#getPageNumber()
     */
    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.displaytag.pagination.PaginatedList#getSearchId()
     */
    @Override
    public String getSearchId() {
        return searchId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.displaytag.pagination.PaginatedList#getSortCriterion()
     */
    @Override
    public String getSortCriterion() {
        return sortCriterion;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.displaytag.pagination.PaginatedList#getSortDirection()
     */
    @Override
    public SortOrderEnum getSortDirection() {
        return sortDirection;
    }

    /**
     * @param fullListSize
     */
    public void setFullListSize(int fullListSize) {
        this.fullListSize = fullListSize;
    }

    /**
     * @param list
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * @param objectsPerPage
     */
    public void setObjectsPerPage(int objectsPerPage) {
        this.objectsPerPage = objectsPerPage;
    }

    /**
     * @param pageNumber
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * @param searchId
     */
    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    /**
     * @param sortCriterion
     */
    public void setSortCriterion(String sortCriterion) {
        this.sortCriterion = sortCriterion;
    }

    /**
     * @param sortDirection
     */
    public void setSortDirection(SortOrderEnum sortDirection) {
        this.sortDirection = sortDirection;
    }

}
