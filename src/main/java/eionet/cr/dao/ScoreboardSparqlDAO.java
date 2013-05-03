package eionet.cr.dao;

import java.io.File;
import java.util.List;
import java.util.Map;

import eionet.cr.common.Subjects;
import eionet.cr.dto.SkosItemDTO;
import eionet.cr.util.Pair;
import eionet.cr.web.util.ObservationFilter;

/**
 * A DAO interface for the SPARQL queries specific to the DG Connect's Digital Agenda Scoreboard project.
 *
 * @author jaanus
 */
public interface ScoreboardSparqlDAO extends DAO {

    /** */
    public static final String OBSERVATION_URI_PREFIX = "http://semantic.digital-agenda-data.eu/data/";

    /** */
    public static final String DATASET_URI_PREFIX = "http://semantic.digital-agenda-data.eu/dataset/";

    /** */
    public static final String DATASET_STRUCTURE_PREFIX = "http://semantic.digital-agenda-data.eu/def/dsd/";

    /** */
    public static final String DEFAULT_DSD_URI = "http://semantic.digital-agenda-data.eu/def/dsd/scoreboard";

    /**
     * Return URI-label pairs of codelists that have type {@link Subjects.SKOS_CONCEPT_SCHEME} and whose URI starts with the given
     * input string.
     *
     * @param uriStartsWith The codelist URI must start with this string.
     * @return The URI-label pairs of codelists.
     * @throws DAOException If database access error happens.
     */
    List<Pair<String, String>> getCodelists(String uriStartsWith) throws DAOException;

    /**
     * Return all items in the codelist by the given URI.
     *
     * @param codelistUri Codelist URI.
     * @return List of items in the given codelist.
     * @throws DAOException If database access error happens.
     */
    List<SkosItemDTO> getCodelistItems(String codelistUri) throws DAOException;

    /**
     *
     * @param selections
     * @param filter
     * @return
     * @throws DAOException
     */
    List<Pair<String, String>> getFilterValues(Map<ObservationFilter, String> selections, ObservationFilter filter)
            throws DAOException;

    /**
     *
     * @param identifier
     * @param dctermsTitle
     * @param dctermsDescription
     * @return
     * @throws DAOException
     */
    String createDataCubeDataset(String identifier, String dctermsTitle, String dctermsDescription) throws DAOException;

    /**
     *
     * @param identifier
     * @return
     * @throws DAOException
     */
    boolean datasetExists(String identifier) throws DAOException;

    /**
     * Exports codelist items of given RDF type into the given spreadsheet template file, using the given properties-to-columns map.
     *
     * @param itemType The RDF type of codelist items to export.
     * @param templateFile Reference to the spreadsheet template file to export into.
     * @param mappings Maps the properties of codelist items to corresponding spreadsheet columns (e.g. 0,1,2 ...)
     * @param targetFile The target spreadsheet file where the exported workbook will be saved to.
     * @return The number of items exported.
     * @throws DAOException Any sort of exception is wrapped into this one.
     */
    int exportCodelistItems(String itemType, File templateFile, Map<String, Integer> mappings, File targetFile)
            throws DAOException;
}
