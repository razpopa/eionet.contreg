package eionet.cr.dao.virtuoso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;

import eionet.cr.common.Predicates;
import eionet.cr.dao.CompiledDatasetDAO;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.readers.DeliveryFilesReader;
import eionet.cr.dao.readers.ResultSetReaderException;
import eionet.cr.dao.readers.UploadDTOReader;
import eionet.cr.dto.DeliveryFilesDTO;
import eionet.cr.dto.SubjectDTO;
import eionet.cr.dto.UploadDTO;
import eionet.cr.harvest.BaseHarvest;
import eionet.cr.util.Bindings;
import eionet.cr.util.URIUtil;
import eionet.cr.util.sesame.SPARQLQueryUtil;
import eionet.cr.util.sesame.SPARQLResultSetBaseReader;
import eionet.cr.util.sesame.SesameConnectionProvider;
import eionet.cr.util.sesame.SesameUtil;
import eionet.cr.util.sql.SingleObjectReader;
import eionet.cr.web.security.CRUser;

/**
 * DAO methods for compiled datasets in Virtuoso.
 *
 * @author altnyris
 */
public class VirtuosoCompiledDatasetDAO extends VirtuosoBaseDAO implements CompiledDatasetDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DeliveryFilesDTO> getDeliveryFiles(List<String> deliveryUris) throws DAOException {
        List<DeliveryFilesDTO> ret = new ArrayList<DeliveryFilesDTO>();
        if (deliveryUris != null && deliveryUris.size() > 0) {
            StringBuffer query = new StringBuffer();
            query.append("select ?s ?o ?title count(?s1) ?triplesCnt where {");
            query.append("?s <").append(Predicates.ROD_HAS_FILE).append("> ?o . ");
            query.append("filter(?s IN (").append(SPARQLQueryUtil.urisToCSV(deliveryUris)).append("))");
            query.append("OPTIONAL {?o <").append(Predicates.CR_HARVESTED_STATEMENTS).append("> ?triplesCnt}");
            query.append("OPTIONAL {");
            query.append("?o ?p ?title .");
            query.append("filter (?p IN (<").append(Predicates.DC_TITLE).append(">,");
            query.append("<").append(Predicates.DCTERMS_TITLE).append(">,");
            query.append("<").append(Predicates.RDFS_LABEL).append(">))");
            query.append("}");
            query.append("graph ?o {");
            query.append("?s1 ?p1 ?o1");
            query.append("}} ORDER BY ?s");

            ret = executeSPARQL(query.toString(), new DeliveryFilesReader());
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getCompiledDatasets(String homeFolder) throws DAOException {
        List<String> ret = new ArrayList<String>();
        if (!StringUtils.isBlank(homeFolder)) {
            StringBuffer query = new StringBuffer();
            query.append("select distinct(?s) where {");
            query.append("graph ?g { ");
            query.append("?s ?p <").append(Predicates.CR_COMPILED_DATASET).append("> .");
            query.append("filter (?g = <").append(homeFolder).append(">)");
            query.append("}}");

            ret = executeSPARQL(query.toString(), new SingleObjectReader<String>());
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDatasetFiles(String dataset) throws DAOException {
        List<String> ret = new ArrayList<String>();
        if (!StringUtils.isBlank(dataset)) {
            StringBuffer query = new StringBuffer();
            query.append("select distinct(?o) where {");
            query.append("<").append(dataset).append("> <").append(Predicates.CR_GENERATED_FROM).append("> ?o");
            query.append("}");

            ret = executeSPARQL(query.toString(), new SingleObjectReader<String>());
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SubjectDTO> getDetailedDatasetFiles(String dataset) throws DAOException {
        List<SubjectDTO> ret = new ArrayList<SubjectDTO>();
        if (!StringUtils.isBlank(dataset)) {
            StringBuffer query = new StringBuffer();
            query.append("select ?source, ?lastModified where {");
            query.append("<").append(dataset).append("> <").append(Predicates.CR_GENERATED_FROM).append("> ?source . ");
            query.append("?source").append("<" + Predicates.CR_LAST_MODIFIED + ">").append(" ?lastModified");
            query.append("}");


            SPARQLResultSetBaseReader<SubjectDTO> reader = new SPARQLResultSetBaseReader<SubjectDTO>() {
                @Override
                public void readRow(BindingSet bindingSet) throws ResultSetReaderException {
                    if (bindingSet != null && bindingSet.size() > 0) {
                        String sourceUri = bindingSet.getValue("source").stringValue();
                        String lastModifiedDate = bindingSet.getValue("lastModified").stringValue();
                        Date lastModified = null;
                        try {
                            lastModified = BaseHarvest.DATE_FORMATTER.parse(lastModifiedDate);
                        } catch (ParseException e) {
                            logger.warn("Failed to parse date", e);
                        }

                        SubjectDTO dto = new SubjectDTO(sourceUri, false);
                        dto.setLastModifiedDate(lastModified);
                        resultList.add(dto);
                    }
                }
            };

            ret = executeSPARQL(query.toString(), reader);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveDataset(List<String> selectedFiles, String datasetUri, boolean overwrite) throws DAOException {

        RepositoryConnection con = null;
        try {
            con = SesameConnectionProvider.getRepositoryConnection();

            if (overwrite) {
                clearGraph(datasetUri);
            }

            StringBuffer query = new StringBuffer();
            query.append("INSERT INTO GRAPH ?graphUri { ?s ?p ?o } ");
            query.append("WHERE {graph ?g { ?s ?p ?o . ");
            query.append("filter (?g IN (").append(SPARQLQueryUtil.urisToCSV(selectedFiles)).append("))");
            query.append("}}");

            Bindings bindings = new Bindings();
            bindings.setURI("graphUri", datasetUri);
            executeUpdateQuerySPARQL(query.toString(), bindings, con);

        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        } finally {
            SesameUtil.close(con);
        }
    }

    /**
     * SPARQL for user compiled datasets.
     */
    private static final String USER_COMPILED_DATASETS_QUERY = "select ?s ?p ?o where { ?s ?p ?o. { "
        + "select distinct ?s where { "
        + "graph ?g { "
        + "?s a ?compDataset . "
        + "filter (?g = ?useFolder) "
        + "}}}} order by ?s ?p ?o";

    /**
     * User compiled datasets.
     *
     * @param crUser CR user
     * @see eionet.cr.dao.CompiledDatasetDAOgetUserCompiledDatasets(eionet.cr.web.security.CRUser)
     * @throws DAOException if query fails.
     * @return List of user compiled datasets.
     */
    @Override
    public Collection<UploadDTO> getUserCompiledDatasets(CRUser crUser) throws DAOException {

        if (crUser == null) {
            throw new IllegalArgumentException("User object must not be null");
        }

        if (StringUtils.isBlank(crUser.getUserName())) {
            throw new IllegalArgumentException("User name must not be blank");
        }
        Bindings bindings = new Bindings();
        bindings.setURI("useFolder", crUser.getHomeUri());
        bindings.setURI("compDataset", Predicates.CR_COMPILED_DATASET);

        UploadDTOReader reader = new UploadDTOReader();
        executeSPARQL(USER_COMPILED_DATASETS_QUERY, bindings, reader);

        // loop through all the found datasets and make sure they all have the label set
        Collection<UploadDTO> datasets = reader.getResultList();
        for (UploadDTO uploadDTO : datasets) {

            String currentLabel = uploadDTO.getLabel();
            String subjectUri = uploadDTO.getSubjectUri();
            String uriLabel = URIUtil.extractURILabel(subjectUri, SubjectDTO.NO_LABEL);
            uriLabel = StringUtils.replace(uriLabel, "%20", " ");

            if (StringUtils.isBlank(currentLabel) && !StringUtils.isBlank(uriLabel))
                uploadDTO.setLabel(uriLabel);
            else
                uploadDTO.setLabel(uriLabel + " (" + currentLabel + ")");
        }

        return datasets;
    }

    /**
     * SPARQL for detecting if dataset exists.
     */
    private static final String DATASET_EXISTS_QUERY = "ASK {?graphUri a ?datasetType}";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean datasetExists(String uri) throws DAOException {

        boolean ret = false;

        if (uri == null) {
            throw new IllegalArgumentException("Dataset URI must not be null");
        }

        RepositoryConnection con = null;

        try {
            con = SesameConnectionProvider.getReadOnlyRepositoryConnection();

            Bindings bindings = new Bindings();
            bindings.setURI("graphUri", uri);
            bindings.setURI("datasetType", Predicates.CR_COMPILED_DATASET);

            BooleanQuery booleanQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, DATASET_EXISTS_QUERY);
            bindings.applyTo(booleanQuery, con.getValueFactory());
            Boolean result = booleanQuery.evaluate();
            if (result != null) {
                ret = result.booleanValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        } finally {
            SesameUtil.close(con);
        }

        return ret;
    }

    /**
     * SPARQL for detecting if user owns the dataset
     */
    private static final String IS_USER_DATASET_QUERY = "ASK { ?userHome ?pred ?dataset }";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUsersDataset(String dataset, String userHome) throws DAOException {

        boolean ret = false;

        if (dataset == null) {
            throw new IllegalArgumentException("Dataset URI must not be null");
        }

        if (userHome == null) {
            throw new IllegalArgumentException("User home URI must not be null");
        }

        RepositoryConnection con = null;

        try {
            con = SesameConnectionProvider.getReadOnlyRepositoryConnection();

            Bindings bindings = new Bindings();
            bindings.setURI("userHome", userHome);
            bindings.setURI("pred", Predicates.CR_HAS_FILE);
            bindings.setURI("dataset", dataset);

            BooleanQuery booleanQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, IS_USER_DATASET_QUERY);
            bindings.applyTo(booleanQuery, con.getValueFactory());
            Boolean result = booleanQuery.evaluate();
            if (result != null) {
                ret = result.booleanValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        } finally {
            SesameUtil.close(con);
        }

        return ret;
    }


}
