package eionet.cr.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.cr.dto.SubjectDTO;
import eionet.cr.search.util.SortOrder;
import eionet.cr.search.util.SubjectHashesReader;
import eionet.cr.search.util.SubjectsDataReader;
import eionet.cr.util.sql.ConnectionUtil;
import eionet.cr.util.sql.MySQLUtil;
import eionet.cr.util.sql.SQLUtil;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public abstract class AbstractSubjectSearch {
	
	/** */
	private static Log logger = LogFactory.getLog(AbstractSubjectSearch.class);
	
	/** */
	protected int pageNumber = 0;
	protected SortOrder sortOrder = SortOrder.ASCENDING;
	protected String sortPredicate = null;	
	
	/** */
	protected Collection<SubjectDTO> resultList = new ArrayList<SubjectDTO>();
	protected int totalMatchCount = 0;
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void execute() throws SearchException{
		
		List<Object> inParameters = new ArrayList<Object>();
		
		String subjectSelectSQL = getSubjectSelectSQL(inParameters);
		if (subjectSelectSQL!=null && subjectSelectSQL.length()>0){
			
			Connection conn = null;
			try{
				conn = getConnection();
				
				SubjectHashesReader subjectHashesReader = new SubjectHashesReader();
				
				logger.debug("Executing subject select query: " + subjectSelectSQL);
				long time = System.currentTimeMillis();
				
				SQLUtil.executeQuery(subjectSelectSQL, inParameters, subjectHashesReader, conn);
				
				logger.debug("subject select query took " + (System.currentTimeMillis()-time) + " ms");
				
				if (subjectHashesReader.getResultCount()>0){
					
					totalMatchCount = MySQLUtil.getTotalRowCount(conn); // TODO - maybe do it without directly pointing to MySQL
					LinkedHashMap<String, SubjectDTO> subjectsMap = subjectHashesReader.getResultMap();
					
					SubjectsDataReader subjectsDataReader = new SubjectsDataReader(subjectsMap);
					
					logger.debug("Executing subject data select query");
					time = System.currentTimeMillis();

					SQLUtil.executeQuery(
							getSubjectDataSelectSQL(subjectHashesReader.getSubjectHashesCommaSeparated()), subjectsDataReader, conn);
				
					logger.debug("subject data select query took " + (System.currentTimeMillis()-time) + " ms");
					
					this.resultList = subjectsMap.values();
				}
			}
			catch (SQLException e){
				throw new SearchException(e.toString(), e);
			}
			finally{
				SQLUtil.close(conn);
			}
		}
	}

	/**
	 * 
	 * @param inParameters
	 * @return
	 */
	protected abstract String getSubjectSelectSQL(List<Object> inParameters);
	
	/**
	 * 
	 * @param sortP
	 * @param sortO
	 */
	public void setSorting(String sortPredicate, String sortOrder) {
		
		this.sortPredicate= sortPredicate;
		this.sortOrder = SortOrder.parse(sortOrder);
	}

	/**
	 * 
	 * @param pageN
	 */
	public void setPageNumber(int pageNumber) {
		
		this.pageNumber = pageNumber;
	}

	/**
	 * @return the resultList
	 */
	public Collection<SubjectDTO> getResultList() {
		return resultList;
	}

	/**
	 * @return the matchCount
	 */
	public int getTotalMatchCount() {
		return totalMatchCount;
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	protected Connection getConnection() throws SQLException{
		return ConnectionUtil.getConnection();
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getSubjectDataSelectSQL(String subjectHashes){
		
		StringBuffer buf = new StringBuffer().
		append("select SUBJECT, SUBJ_RESOURCE.URI as SUBJECT_URI, PRED_RESOURCE.URI as PREDICATE_URI, ").
		append("OBJECT, OBJECT_HASH, ANON_SUBJ, ANON_OBJ, LIT_OBJ, OBJ_LANG, ").
		append("SRC_RESOURCE.URI as SOURCE_URI, DSRC_RESOURCE.URI as DERIV_SOURCE_URI from SPO ").
		append("left join RESOURCE as SUBJ_RESOURCE on (SUBJECT=SUBJ_RESOURCE.URI_HASH) ").
		append("left join RESOURCE as PRED_RESOURCE on (PREDICATE=PRED_RESOURCE.URI_HASH) ").
		append("left join RESOURCE as SRC_RESOURCE on (SOURCE=SRC_RESOURCE.URI_HASH) ").
		append("left join RESOURCE as DSRC_RESOURCE on (OBJ_DERIV_SOURCE=DSRC_RESOURCE.URI_HASH) ").
		append("where SUBJECT in (").append(subjectHashes).append(") ").
		append("order by SUBJECT, PREDICATE, OBJECT");
		
		return buf.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public int resultSize(){
		return resultList==null ? 0 : resultList.size();
	}
}