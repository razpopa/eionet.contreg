package eionet.cr.dao.virtuoso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryConnection;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.helpers.SearchHelper;
import eionet.cr.dao.readers.SubjectDataReader;
import eionet.cr.dto.SubjectDTO;
import eionet.cr.util.Bindings;
import eionet.cr.util.Hashes;
import eionet.cr.util.sesame.SPARQLQueryUtil;
import eionet.cr.util.sesame.SPARQLResultSetReader;
import eionet.cr.util.sesame.SesameUtil;
import eionet.cr.util.sql.SQLResultSetReader;
import eionet.cr.util.sql.SQLUtil;
import eionet.cr.util.sql.SingleObjectReader;

/**
 *
 * @author jaanus
 *
 */
public abstract class VirtuosoBaseDAO {

    /** */
    // public static final String BNODE_URI_PREFIX = "nodeID://";
    public static final String BNODE_URI_PREFIX = "_:";

    /** */
    protected Logger logger = Logger.getLogger(VirtuosoBaseDAO.class);

    /**
     *
     * @return
     */
    protected Connection getSQLConnection() throws SQLException {

        return SesameUtil.getSQLConnection();
    }

    /**
     *
     * @param <T>
     * @param sparql
     * @param bindings
     * @param reader
     * @return
     * @throws DAOException
     */
    protected <T> List<T> executeSPARQL(String sparql, Bindings bindings, SPARQLResultSetReader<T> reader) throws DAOException {

        RepositoryConnection conn = null;
        try {
            conn = SesameUtil.getRepositoryConnection();
            SesameUtil.executeQuery(sparql, bindings, reader, conn);
            return reader.getResultList();
        } catch (Exception e) {
            throw new DAOException(e.toString(), e);
        } finally {
            SesameUtil.close(conn);
        }
    }

    /**
     *
     * @param <T>
     * @param sparql
     * @param reader
     * @return
     * @throws DAOException
     */
    // TODO - rename it to executeStaticSPARQL() ?
    protected <T> List<T> executeSPARQL(String sparql, SPARQLResultSetReader<T> reader) throws DAOException {

        return executeSPARQL(sparql, null, reader);
    }

    /**
     * Executes SPARQL that updates data.
     *
     * @param sparql
     *            SPARQL
     * @param conn
     *            Virtuoso repository connection
     * @param bindings
     *            Query bindings, if no bindings, null is accepted as the value
     * @throws DAOException
     *             if update fails
     */
    protected void executeUpdateSPARQL(final String sparql, final RepositoryConnection conn, final Bindings bindings)
    throws DAOException {
        try {
            SesameUtil.executeUpdateQuery(sparql, conn, bindings);
        } catch (Exception e) {
            throw new DAOException(e.toString(), e);
        }
    }

    /**
     * Executes SPARQL with no bindings that is expected to have only one result and returns the unique value.
     *
     * @param <T>
     * @param sql
     * @param params
     * @param reader
     * @return
     * @throws DAOException
     *             if query fails
     */
    protected <T> T executeUniqueResultSPARQL(String sql, SPARQLResultSetReader<T> reader) throws DAOException {

        return executeUniqueResultSPARQL(sql, null, reader);
    }

    /**
     * Executes SPARQL that is expected to have only one result and returns the unique value.
     *
     * @param <T>
     * @param sql
     * @param bindings
     *            Binding values for the prepared SPARQL
     * @param params
     * @param reader
     * @return
     * @throws DAOException
     *             if query fails
     */
    protected <T> T executeUniqueResultSPARQL(String sql, Bindings bindings, SPARQLResultSetReader<T> reader) throws DAOException {

        List<T> result = executeSPARQL(sql, bindings, reader);
        return (result == null || result.isEmpty()) ? null : result.get(0);
    }

    /**
     * @param subjectUris
     *            subject URIs
     * @param predicateUris
     *            array of needed predicate URIs
     * @param reader
     *            subject reader
     * @return List<SubjectDTO> list of Subject data objects
     * @throws DAOException
     *             Default call of getSubjectsData() - SubjectDTO are created if not existing
     */
    protected List<SubjectDTO> getSubjectsData(Collection<String> subjectUris, String[] predicateUris,
            SubjectDataReader reader) throws DAOException {

        boolean createMissingDTOs = true;
        return getSubjectsData(subjectUris, predicateUris, reader, createMissingDTOs);
    }

    /**
     * Returns list of Data objects of given subjects for given predicates.
     *
     * @param subjectUris
     *            list of subject URIs
     * @param predicateUris
     *            array of predicates which data is requested
     * @param reader
     *            bindingset reader
     * @param createMissingDTOs
     *            indicates if to create a SubjectDTO object if it does not exist
     * @return List<SubjectDTO> list of Subject data objects
     * @throws DAOException
     *             if query fails
     */
    protected List<SubjectDTO> getSubjectsData(Collection<String> subjectUris, String[] predicateUris,
            SubjectDataReader reader, boolean createMissingDTOs) throws DAOException {

        if (subjectUris == null || subjectUris.isEmpty()) {
            throw new IllegalArgumentException("Subjects collection must not be null or empty!");
        }

        Bindings bindings = new Bindings();
        String query = getSubjectsDataQuery(subjectUris, predicateUris, bindings);
        executeSPARQL(query, bindings, reader);

        Map<Long, SubjectDTO> subjectsMap = reader.getSubjectsMap();
        if (subjectsMap != null && !subjectsMap.isEmpty()) {
            for (String subjectUri : subjectUris) {

                Long subjectHash = Long.valueOf(Hashes.spoHash(subjectUri));
                if (subjectsMap.get(subjectHash) == null && createMissingDTOs) {

                    // TODO: don't hardcode isAnonymous to false
                    SubjectDTO subjectDTO = new SubjectDTO(subjectUri, false);
                    subjectsMap.put(subjectHash, subjectDTO);
                }
            }
        }

        return reader.getResultList();
    }

    /**
     * Returns a SPARQL query that will retrieve the given subjects' given predicates.
     * Predicates and graphs are optional.
     *
     * @param subjectUris
     *            - collection of subjects whose data is being queried
     * @param predicateUris
     *            - array of predicates that are queried (if null or empty, no predicate filter is applied)
     * @param bindings
     *            - SPARQL variable bindings to fill when building the returned query
     * @return String the SPARQL query
     */
    private String getSubjectsDataQuery(Collection<String> subjectUris, String[] predicateUris, Bindings bindings) {

        if (subjectUris == null || subjectUris.isEmpty()) {
            throw new IllegalArgumentException("Subjects collection must not be null or empty!");
        }

        String commaSeparatedSubjects = SPARQLQueryUtil.urisToCSV(subjectUris, "subjectValue", bindings);
        String query = "select * where {graph ?g {?s ?p ?o. filter (?s IN (" + commaSeparatedSubjects + ")) ";

        // if only certain predicates needed, add relevant filter
        if (predicateUris != null && predicateUris.length > 0) {
            String commaSeparatedPredicates = SPARQLQueryUtil.urisToCSV(Arrays.asList(predicateUris), "predicateValue", bindings);
            query += "filter (?p IN (" + commaSeparatedPredicates + ")) ";
        }

        query += "}} ORDER BY ?s ?p";
        return query;
    }

    /**
     * Count the total number of rows retrieved by the query constructed in SearchHelper.
     *
     * @param helper
     *            SearchHelper object.
     * @return number of rows
     * @throws DAOException
     */
    protected int getExactRowCount(SearchHelper helper) throws DAOException {

        String query = helper.getCountQuery(new ArrayList<Object>());
        Bindings bindings = helper.getQueryBindings();
        Object resultObject = executeUniqueResultSPARQL(query, bindings, new SingleObjectReader<Long>());
        return Integer.valueOf(resultObject.toString());
    }

    /**
     * helper method to execute sql queries. Handles connection init, close. Wraps Exceptions into {@link DAOException}
     *
     * @param <T>
     *            - type of the returned object
     * @param sql
     *            - sql string
     * @param params
     *            - parameters to insert into sql string
     * @param reader
     *            - reader, to convert resultset
     * @return result of the sql query
     * @throws DAOException
     */
    protected <T> List<T> executeSQL(String sql, List<?> params, SQLResultSetReader<T> reader) throws DAOException {
        Connection conn = null;
        try {
            conn = getSQLConnection();
            SQLUtil.executeQuery(sql, params, reader, conn);
            List<T> list = reader.getResultList();
            return list;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            SQLUtil.close(conn);
        }
    }

    /**
     * executes insert or update with given sql and parameters.
     *
     * @param sql
     *            - sql string to execute
     * @param params
     *            - sql params
     * @throws DAOException
     */
    protected void executeSQL(String sql, List<?> params) throws DAOException {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = getSQLConnection();
            if (params != null && !params.isEmpty()) {
                statement = SQLUtil.prepareStatement(sql, params, conn);
                statement.execute();
            } else {
                SQLUtil.executeUpdate(sql, conn);
            }
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            SQLUtil.close(conn);
            SQLUtil.close(statement);
        }
    }

    /**
     *
     * @param <T>
     * @param sql
     * @param params
     * @param reader
     * @return
     * @throws DAOException
     */
    protected <T> T executeUniqueResultSQL(String sql, List<?> params, SQLResultSetReader<T> reader) throws DAOException {
        List<T> result = executeSQL(sql, params, reader);
        return result == null || result.isEmpty() ? null : result.get(0);
    }
}
