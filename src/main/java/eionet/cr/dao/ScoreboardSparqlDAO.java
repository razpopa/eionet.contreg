package eionet.cr.dao;

import java.util.List;

import eionet.cr.common.Subjects;
import eionet.cr.dto.SkosItemDTO;
import eionet.cr.util.Pair;

/**
 * A DAO interface for the SPARQL queries specific to the DG Connect's Digital Agenda Scoreboard project.
 *
 * @author jaanus
 */
public interface ScoreboardSparqlDAO extends DAO {

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
}
