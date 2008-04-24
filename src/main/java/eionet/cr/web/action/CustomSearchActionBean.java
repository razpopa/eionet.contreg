package eionet.cr.web.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import eionet.cr.common.Identifiers;
import eionet.cr.search.SearchException;
import eionet.cr.search.Searcher;
import eionet.cr.util.Util;
import eionet.cr.web.util.search.CustomSearchFilter;
import eionet.cr.web.util.search.SearchResultRow;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
@UrlBinding("/customSearch.action")
public class CustomSearchActionBean extends AbstractSearchActionBean{
	
	/** */
	private static final String SELECTED_FILTERS_SESSION_ATTR_NAME = CustomSearchActionBean.class + ".selectedFilters";
	
	/** */
	private static final String SELECTED_VALUE_PREFIX = "value_";
	private static final String SHOW_PICKLIST_VALUE_PREFIX = "showPicklist_";
	private static final String REMOVE_FILTER_VALUE_PREFIX = "removeFilter_";
	
	/** */
	private static final String ASSOCIATED_JSP = "/pages/customSearch.jsp";
	
	/** */
	private static Map<String,CustomSearchFilter> availableFilters;
	private String addedFilter;
	private String picklistFilter;
	private String removedFilter;
	private Collection<String> picklist;
	
	/**
	 * 
	 * @return
	 */
	@DefaultHandler
	public Resolution unspecifiedEvent(){
		
		if (isShowPicklist())
			populateSelectedFilters();
		else if (isRemoveFilter()){
			populateSelectedFilters();
			getSelectedFilters().remove(getRemovedFilter());
		}
		else
			getContext().getRequest().getSession().removeAttribute(SELECTED_FILTERS_SESSION_ATTR_NAME);
		
		return new ForwardResolution(ASSOCIATED_JSP);
	}
	
	/**
	 * 
	 * @return
	 * @throws SearchException 
	 */
	public Resolution search() throws SearchException{
		
		populateSelectedFilters();
		
		resultList = SearchResultRow.convert(Searcher.customSearch(buildSearchCriteria()));
		
		return new ForwardResolution(ASSOCIATED_JSP);
	}
	
	/**
	 * 
	 * @return
	 */
	public Resolution addFilter(){
		
		populateSelectedFilters();
		
		if (addedFilter!=null)
			getSelectedFilters().put(addedFilter, "");
		
		return new ForwardResolution(ASSOCIATED_JSP);
	}
	
	/**
	 * 
	 * @return
	 * @throws SearchException 
	 */
	public Collection<String> getPicklist() throws SearchException{

		if (!isShowPicklist())
			return null;
		else if (!getAvailableFilters().containsKey(getPicklistFilter()))
			return null;
		
		if (picklist==null){
			picklist = Searcher.getLiteralFieldValues(getAvailableFilters().get(getPicklistFilter()).getUri());
			if (picklist==null)
				picklist = new ArrayList<String>();
		}
		
		return picklist;
	}
	
	/**
	 * @return the selectedFilter
	 */
	public String getAddedFilter() {
		return addedFilter;
	}

	/**
	 * @param selectedFilter the selectedFilter to set
	 */
	public void setAddedFilter(String selectedFilter) {
		this.addedFilter = selectedFilter;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String,String> getSelectedFilters(){
		
		HttpSession session = getContext().getRequest().getSession();
		Map<String,String> selectedFilters =
			(Map<String,String>)session.getAttribute(SELECTED_FILTERS_SESSION_ATTR_NAME);
		if (selectedFilters==null){
			selectedFilters = new LinkedHashMap<String,String>();
			session.setAttribute(SELECTED_FILTERS_SESSION_ATTR_NAME, selectedFilters);
		}
		
		return selectedFilters;
	}

	/**
	 * 
	 */
	private void populateSelectedFilters(){
		
		Map<String,String> selected = getSelectedFilters();
		if (!selected.isEmpty()){
			Enumeration paramNames = this.getContext().getRequest().getParameterNames();
			while (paramNames!=null && paramNames.hasMoreElements()){
				String paramName = (String)paramNames.nextElement();
				if (paramName.startsWith(SELECTED_VALUE_PREFIX)){
					String key = paramName.substring(SELECTED_VALUE_PREFIX.length());
					if (key.length()>0 && selected.containsKey(key))
						selected.put(key, getContext().getRequest().getParameter(paramName));
				}
			}
		}		
	}

	/**
	 * @return the availableFilters
	 */
	public Map<String,CustomSearchFilter> getAvailableFilters() {
		
		if (availableFilters==null){
			
			ArrayList<CustomSearchFilter> list = new ArrayList<CustomSearchFilter>();
			
			CustomSearchFilter filter = new CustomSearchFilter();
			filter.setUri(Identifiers.RDF_TYPE);
			filter.setTitle("Type");
			filter.setDescription("");
			filter.setProvideValues(true);
			list.add(filter);

			filter = new CustomSearchFilter();
			filter.setUri(Identifiers.RDFS_LABEL);
			filter.setTitle("Label");
			filter.setDescription("");
			filter.setProvideValues(false);
			list.add(filter);

			filter = new CustomSearchFilter();
			filter.setUri(Identifiers.DC_COVERAGE);
			filter.setTitle("Coverage");
			filter.setDescription("");
			filter.setProvideValues(true);
			list.add(filter);

			filter = new CustomSearchFilter();
			filter.setUri(Identifiers.DC_SUBJECT);
			filter.setTitle("Subject");
			filter.setDescription("");
			filter.setProvideValues(true);
			list.add(filter);
			
			availableFilters = new LinkedHashMap<String,CustomSearchFilter>();
			for (int i=0; i<list.size(); i++)
				availableFilters.put(String.valueOf(i+1), list.get(i));
		}
		
		return availableFilters;
	}

	/**
	 * @return the picklistFilter
	 */
	public String getPicklistFilter() {
		
		if (picklistFilter==null){
			picklistFilter = "";
			Enumeration paramNames = this.getContext().getRequest().getParameterNames();
			while (paramNames!=null && paramNames.hasMoreElements()){
				String paramName = (String)paramNames.nextElement();
				if (paramName.startsWith(SHOW_PICKLIST_VALUE_PREFIX)){
					int i = paramName.indexOf('.')<0 ? paramName.length() : paramName.indexOf('.');
					String key = paramName.substring(SHOW_PICKLIST_VALUE_PREFIX.length(), i);
					if (key.length()>0 && getSelectedFilters().containsKey(key)){
						picklistFilter = key;
						break;
					}
				}
			}

		}
		return picklistFilter;
	}

	/**
	 * @return the removedFilter
	 */
	public String getRemovedFilter() {
		
		if (removedFilter==null){
			removedFilter = "";
			Enumeration paramNames = this.getContext().getRequest().getParameterNames();
			while (paramNames!=null && paramNames.hasMoreElements()){
				String paramName = (String)paramNames.nextElement();
				if (paramName.startsWith(REMOVE_FILTER_VALUE_PREFIX)){
					int i = paramName.indexOf('.')<0 ? paramName.length() : paramName.indexOf('.');
					String key = paramName.substring(REMOVE_FILTER_VALUE_PREFIX.length(), i);
					if (key.length()>0 && getSelectedFilters().containsKey(key)){
						removedFilter = key;
						break;
					}
				}
			}

		}
		return removedFilter;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isShowPicklist(){
		return !Util.isNullOrEmpty(getPicklistFilter());
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRemoveFilter(){
		return !Util.isNullOrEmpty(getRemovedFilter());
	}
	
	/**
	 * 
	 * @return
	 */
	private Map<String,String> buildSearchCriteria(){
		
		Map<String,String> result = new HashMap<String,String>();
		
		Map<String,String> selected = getSelectedFilters();
		for (Iterator<String> keys=selected.keySet().iterator(); keys.hasNext();){
			String key = keys.next();
			CustomSearchFilter filter = getAvailableFilters().get(key);
			if (filter!=null)
				result.put(filter.getUri(), selected.get(key));
		}
		
		return result;
	}
}
