package eionet.cr.dao.virtuoso;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eionet.cr.dao.DAOException;
import eionet.cr.dao.ScoreboardSparqlDAO;
import eionet.cr.dao.readers.SkosItemsReader;
import eionet.cr.dto.SkosItemDTO;
import eionet.cr.util.Bindings;
import eionet.cr.util.Pair;
import eionet.cr.util.URIUtil;
import eionet.cr.util.sql.PairReader;
import eionet.cr.web.util.ObservationFilter;

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

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.dao.ScoreboardSparqlDAO#getFilterValues(java.util.Map, eionet.cr.web.util.ObservationFilter)
     */
    @Override
    public List<Pair<String, String>> getFilterValues(Map<ObservationFilter, String> selections, ObservationFilter filter)
            throws DAOException {

        if (filter == null) {
            throw new IllegalArgumentException("Filter for which the values are being asked, must not be null!");
        }

        ObservationFilter[] filters = ObservationFilter.values();
        // int belowIndex = forFilter == null ? -1 : forFilter.ordinal();
        // if (belowIndex >= filters.length - 1) {
        // throw new IllegalArgumentException("The \"below filter\" is out of bounds!");
        // }

        // ObservationFilter nextFilter = filters[belowIndex + 1];
        // String nextFilterAlias = nextFilter.getAlias();
        String filterAlias = filter.getAlias();
        int filterIndex = filter.ordinal();

        StringBuilder sb = new StringBuilder();
        sb.append("PREFIX dad-prop: <http://semantic.digital-agenda-data.eu/def/property/>\n");
        sb.append("PREFIX cube: <http://purl.org/linked-data/cube#>\n");
        sb.append("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n");
        sb.append("\n");
        sb.append("select\n");
        sb.append("  ?").append(filterAlias).append(" min(str(coalesce(?prefLabel, ?").append(filterAlias).append("))) as ?label\n");
        sb.append("where {\n");
        sb.append("  ?s a cube:Observation.\n");

        if (selections != null && !selections.isEmpty()) {
            // for (int i = 0; i <= belowIndex && i < filters.length; i++) {
            for (int i = 0; i < filterIndex && i < filters.length; i++) {

                ObservationFilter availFilter = filters[i];
                String selValue = selections.get(availFilter);
                if (StringUtils.isNotBlank(selValue)) {
                    if (URIUtil.isSchemedURI(selValue)) {
                        sb.append("  ?s <").append(availFilter.getPredicate()).append("> <").append(selValue).append(">.\n");
                    } else {
                        sb.append("  ?s <").append(availFilter.getPredicate()).append("> \"").append(selValue).append("\".\n");
                    }
                } else {
                    sb.append("  ?s <").append(availFilter.getPredicate()).append("> ?").append(availFilter.getAlias())
                            .append(".\n");
                }
            }
        }

        // sb.append("  ?s <").append(nextFilter.getPredicate()).append("> ?").append(forFilterAlias).append(".\n");
        sb.append("  ?s <").append(filter.getPredicate()).append("> ?").append(filterAlias).append(".\n");
        sb.append("  optional {?").append(filterAlias).append(" skos:prefLabel ?prefLabel filter(lang(?prefLabel) in ('en',''))}\n");
        sb.append("}\n");
        sb.append("group by ?").append(filterAlias).append("\n");
        sb.append("order by ?label");

        System.out.println("-- --------------------------------------------------------------");
        System.out.println("sparql");
        System.out.println(sb + ";");

        PairReader<String, String> reader = new PairReader<String, String>(filterAlias, "label");
        List<Pair<String, String>> resultList = executeSPARQL(sb.toString(), reader);
        return resultList;
    }

    public static void main(String[] args) throws DAOException {

        // LinkedHashMap<ObservationFilter, String> selections = new LinkedHashMap<ObservationFilter, String>();
        // selections.put(ObservationFilter.INDICATOR, "http://semantic.digital-agenda-data.eu/codelist/indicator/e_cuse");
        // selections.put(ObservationFilter.BREAKDOWN, "http://semantic.digital-agenda-data.eu/codelist/breakdown/10_65");
        // selections.put(ObservationFilter.TIME_PERIOD, "http://reference.data.gov.uk/id/year/2007");
        // selections.put(ObservationFilter.REF_AREA, "http://eurostat.linked-statistics.org/dic/geo#at");
        // selections.put(ObservationFilter.UNIT_MEASURE, "http://semantic.digital-agenda-data.eu/codelist/unit-measure/pc_ent");

        LinkedHashMap<ObservationFilter, String> selections = new LinkedHashMap<ObservationFilter, String>();
        selections.put(ObservationFilter.INDICATOR, "");
        selections.put(ObservationFilter.BREAKDOWN, "");
        selections.put(ObservationFilter.TIME_PERIOD, "");
        selections.put(ObservationFilter.REF_AREA, "");
        selections.put(ObservationFilter.UNIT_MEASURE, "");

        ObservationFilter forFilter = ObservationFilter.BREAKDOWN;

        // LinkedHashMap<ObservationFilter, String> selections = new LinkedHashMap<ObservationFilter, String>();
        // ObservationFilter below = null;

        VirtuosoScoreboardSparqlDAO dao = new VirtuosoScoreboardSparqlDAO();
        dao.getFilterValues(selections, forFilter);

    }
}
