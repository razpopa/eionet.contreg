package eionet.cr.dao.virtuoso;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.ScoreboardSparqlDAO;
import eionet.cr.dao.readers.SkosItemsReader;
import eionet.cr.dto.SkosItemDTO;
import eionet.cr.util.Bindings;
import eionet.cr.util.Pair;
import eionet.cr.util.sql.PairReader;

/**
 * A Virtuoso-specific implementation of {@link ScoreboardSparqlDAO}.
 *
 * @author jaanus
 */
public class VirtuosoScoreboardSparqlDAO extends VirtuosoBaseDAO implements ScoreboardSparqlDAO {

    // @formatter:off
    private static final String GET_CODELISTS_SPARQL = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
    		"select\n" +
    		"  ?uri as ?" + PairReader.LEFTCOL + " min(?prefLabel) as ?" + PairReader.RIGHTCOL + "\n" +
    		"where {\n" +
    		"  ?uri a skos:ConceptScheme.\n" +
    		"  filter (strStarts(str(?uri), ?uriStartsWith))\n" +
    		"  optional {?uri skos:prefLabel ?prefLabel}\n" +
    		"}\n" +
    		"group by ?uri";
    // @formatter:on

    // @formatter:off
    private static final String GET_CODELIST_ITEMS_SPARQL = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
    		"select\n" +
    		"  ?uri min(?notation) as ?skosNotation min(?prefLabel) as ?skosPrefLabel\n" +
    		"where {\n" +
    		"  ?uri a skos:Concept.\n" +
    		"  filter (strStarts(str(?uri), ?codelistUri))\n" +
    		"  optional {?uri skos:notation ?notation}\n" +
    		"  optional {?uri skos:prefLabel ?prefLabel}\n" +
    		"}\n" +
    		"group by ?uri\n" +
    		"order by ?uri";
    // @formatter:on

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.dao.ScoreboardSparqlDAO#getCodelists(java.lang.String)
     */
    @Override
    public List<Pair<String, String>> getCodelists(String uriStartsWith) throws DAOException {

        if (StringUtils.isBlank(uriStartsWith)) {
            throw new IllegalArgumentException("URI start-prefix must not be blank!");
        }

        Bindings bindings = new Bindings();
        bindings.setString("uriStartsWith", uriStartsWith);

        List<Pair<String, String>> list = executeSPARQL(GET_CODELISTS_SPARQL, bindings, new PairReader<String, String>());
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.dao.ScoreboardSparqlDAO#getCodelistItems(java.lang.String)
     */
    @Override
    public List<SkosItemDTO> getCodelistItems(String codelistUri) throws DAOException {

        if (StringUtils.isBlank(codelistUri)) {
            throw new IllegalArgumentException("Codelist URI must not be blank!");
        }

        Bindings bindings = new Bindings();
        bindings.setString("codelistUri", codelistUri.endsWith("/") ? codelistUri : codelistUri + "/");

        List<SkosItemDTO> list = executeSPARQL(GET_CODELIST_ITEMS_SPARQL, bindings, new SkosItemsReader());
        return list;
    }
}
