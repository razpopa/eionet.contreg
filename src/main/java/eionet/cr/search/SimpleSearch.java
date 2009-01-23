package eionet.cr.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import eionet.cr.dto.SubjectDTOTemp;
import eionet.cr.search.util.SearchExpression;
import eionet.cr.util.sql.ConnectionUtil;
import eionet.cr.util.sql.SQLUtil;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public class SimpleSearch extends SubjectSearch{

	/** */
	private SearchExpression searchExpression;

	/*
	 * (non-Javadoc)
	 * @see eionet.cr.search.SubjectSearch#getSQL(java.util.List)
	 */
	protected String getSubjectSelectSQL(List<Object> inParameters){
		
		if (searchExpression==null || searchExpression.isEmpty())
			return null;
		
		StringBuffer sqlBuf = new StringBuffer("select sql_calc_found_rows distinct SPO.SUBJECT from SPO ");
		if (sortPredicate!=null){
			sqlBuf.append("left join SPO as ORDERING on (SPO.SUBJECT=ORDERING.SUBJECT and ORDERING.PREDICATE=?) ");
			inParameters.add(Integer.valueOf(sortPredicate));
		}
		
		if (searchExpression.isExactPhrase())
			sqlBuf.append(" where SPO.ANON_SUBJ='N' and SPO.OBJECT like ?");
		else
			sqlBuf.append(" where SPO.ANON_SUBJ='N' and match(SPO.OBJECT) against (?)");
		inParameters.add(searchExpression.toString());
		
		if (sortPredicate!=null)
			sqlBuf.append(" order by ORDERING.OBJECT ").append(sortOrder==null ? "" : sortOrder.toSQL());
			
		if (getPageLength()>0){
			sqlBuf.append(" limit ");
			if (pageNumber>0){
				sqlBuf.append("?,");
				inParameters.add(new Integer((pageNumber-1)*getPageLength()));
			}
			sqlBuf.append(getPageLength());
		}
		
		return sqlBuf.toString();
	}

	/**
	 * @param searchExpression the searchExpression to set
	 */
	public void setSearchString(SearchExpression searchExpression) {
		this.searchExpression = searchExpression;
	}

	/**
	 * 
	 * @param string
	 */
	public void setSearchString(String string) {
		this.searchExpression = new SearchExpression(string);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		
		ConnectionUtil.setReturnSimpleConnection(true);
		SimpleSearch simpleSearch = new SimpleSearch();
		try{
			simpleSearch.setSearchString("soil");
			simpleSearch.execute();
			Collection<SubjectDTOTemp> coll = simpleSearch.getResultList();
			
			if (coll!=null){
				System.out.println("coll.size() = " + coll.size());
				
				for (Iterator<SubjectDTOTemp> i=coll.iterator(); i.hasNext(); ){
					SubjectDTOTemp subject = i.next();
					System.out.println(subject);
				}
			}
			else
				System.out.println("coll is null");
		}
		catch (Exception e){
			e.printStackTrace();
		}		
	}
}