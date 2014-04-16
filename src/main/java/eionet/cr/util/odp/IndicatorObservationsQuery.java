package eionet.cr.util.odp;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class for generating a SPARQL queries that return observations of a given indicator in a given dataset.
 * Supported queries:
 * - a SELECT that returns results in a flat format where each row is one observation and columns are its dimensions/attributes.
 * - a CONSTRUCT that returns all observations' triples as RDF graph
 *
 * How to use: construct an instance with dataset and indicator URIs, and call one of the query generation methods.
 *
 * @author Jaanus
 */
public class IndicatorObservationsQuery {

    /** Dataset URI placeholder in query template. */
    private static final String DATASET_URI_PLACEHOLDER = "@datasetUri@";

    /** Indicator URI placeholder in query template. */
    private static final String INDICATOR_URI_PLACEHOLDER = "@indicatorUri@";

    // @formatter:off
    /** SELECT query template. */
    private static final String SELECT_TEMPLATE = "PREFIX sdmx-measure: <http://purl.org/linked-data/sdmx/2009/measure#>\n" +
    		"PREFIX dad-prop: <http://semantic.digital-agenda-data.eu/def/property/>\n" +
    		"PREFIX cube: <http://purl.org/linked-data/cube#>\n" +
    		"\n" +
    		"SELECT\n" +
    		"  bif:subseq(str(?time), bif:strrchr(bif:replace(str(?time), '/', '#'), '#') + 1) as ?time_period\n" +
    		"  bif:subseq(str(?refArea), bif:strrchr(bif:replace(str(?refArea), '/', '#'), '#') + 1) as ?ref_area\n" +
    		"  bif:subseq(str(?indic), bif:strrchr(bif:replace(str(?indic), '/', '#'), '#') + 1) as ?indicator\n" +
    		"  bif:subseq(str(?brkdwn), bif:strrchr(bif:replace(str(?brkdwn), '/', '#'), '#') + 1) as ?breakdown\n" +
    		"  bif:subseq(str(?unit), bif:strrchr(bif:replace(str(?unit), '/', '#'), '#') + 1) as ?unit_measure\n" +
    		"  str(?val) as ?value\n" +
    		"WHERE {\n" +
    		"  ?s a cube:Observation .\n" +
    		"  ?s dad-prop:time-period ?time .\n" +
    		"  ?s dad-prop:ref-area ?refArea .\n" +
    		"  ?s dad-prop:indicator ?indic .\n" +
    		"  ?s dad-prop:breakdown ?brkdwn .\n" +
    		"  ?s dad-prop:unit-measure ?unit .\n" +
    		"  ?s sdmx-measure:obsValue ?val .\n" +
    		"  ?s cube:dataSet <@datasetUri@> .\n" +
    		"  filter (?indic = <@indicatorUri@>)\n" +
    		"}\n" +
    		"order by ?time_period ?ref_area ?indicator ?breakdown ?unit_measure";

    /** CONSTRUCT query template. */
    private static final String CONSTRUCT_TEMPLATE = "PREFIX dad-prop: <http://semantic.digital-agenda-data.eu/def/property/>\n" +
    		"PREFIX cube: <http://purl.org/linked-data/cube#>\n" +
    		"\n" +
    		"CONSTRUCT { ?s ?p ?o } WHERE {\n" +
    		"  ?s ?p ?o .\n" +
    		"  ?s a cube:Observation .\n" +
    		"  ?s dad-prop:indicator <@indicatorUri@> .\n" +
    		"  ?s cube:dataSet <@datasetUri@>\n" +
    		"}";
    // @formatter:on

    /** The dataset URI. */
    private String datasetUri;

    /** The indicator URI. */
    private String indicatorUri;

    /**
     * Use given indicator and dataset.
     *
     * @param datasetUri the dataset URI
     * @param indicatorUri the indicator URI
     */
    public IndicatorObservationsQuery(String datasetUri, String indicatorUri) {

        if (StringUtils.isBlank(datasetUri) || StringUtils.isBlank(indicatorUri)) {
            throw new IllegalArgumentException("The URIs of dataset and indicator must not be blank!");
        }

        this.datasetUri = datasetUri;
        this.indicatorUri = indicatorUri;
    }

    /**
     * Returns SELECT query.
     *
     * @return The query.
     */
    public String asSelect() {

        String result = new String(SELECT_TEMPLATE);
        result = result.replace(DATASET_URI_PLACEHOLDER, datasetUri);
        result = result.replace(INDICATOR_URI_PLACEHOLDER, indicatorUri);
        return result;
    }

    /**
     * Returns CONSTRUCT query.
     *
     * @return The query.
     */
    public String asConstruct() {

        String result = new String(CONSTRUCT_TEMPLATE);
        result = result.replace(DATASET_URI_PLACEHOLDER, datasetUri);
        result = result.replace(INDICATOR_URI_PLACEHOLDER, indicatorUri);
        return result;
    }

}
