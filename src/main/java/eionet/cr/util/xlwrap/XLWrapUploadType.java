package eionet.cr.util.xlwrap;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import eionet.cr.common.CRRuntimeException;

public enum XLWrapUploadType {

    INDICATOR("Indicators metadata", "File containing metadata of the Digital Agenda Scoreboard indicators"),
    INDICATOR_GROUP("Indicator groups metadata", "File containing metadata of the Digital Agenda Scoreboard indicator groups"),
    BREAKDOWN("Breakdowns metadata", "File containing metadata of the Digital Agenda Scoreboard breakdowns"),
    BREAKDOWN_GROUP("Breakdown groups metadata", "File containing metadata of the Digital Agenda Scoreboard breakdown groups"),
    UNIT_MEASURE("Units metadata", "File containing metadata of the Digital Agenda Scoreboard units"),
    SOURCE("Data sources metadata", "File containing metadata of the Digital Agenda Scoreboard data sources"),
    OBSERVATION("Observations", "File containing Digital Agenda Scoreboard observations");

    /** */
    private static final String GRAPH_TEMPLATE = "http://semantic.digital-agenda-data.eu/codelist/@type@/";
    public static final String MAPPING_FILE_EXTENSION = "trig";

    /** */
    private String title;
    private String hint;

    /** */
    private String graphUri;
    private File mappingTemplate;

    /**
     * Constructor.
     * @param title
     * @param hint
     */
    private XLWrapUploadType(String title, String hint) {

        this.title = title;
        this.hint = hint;

        String normalizedName = name().toLowerCase().replace('_', '-');
        this.graphUri = GRAPH_TEMPLATE.replace("@type@", normalizedName);

        String mappingTemplateFileName = normalizedName + "." + MAPPING_FILE_EXTENSION;
        URL mappingTemplateURL = getClass().getClassLoader().getResource(mappingTemplateFileName);
        if (mappingTemplateURL == null) {
            throw new CRRuntimeException("Could not locate mapping template by the name of " + mappingTemplateFileName);
        }

        try {
            this.mappingTemplate = new File(mappingTemplateURL.toURI());
        } catch (URISyntaxException e) {
            throw new CRRuntimeException("Invalid mapping template URI: " + mappingTemplateURL, e);
        }
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the hint
     */
    public String getHint() {
        return hint;
    }

    /**
     * @return the graphUri
     */
    public String getGraphUri() {
        return graphUri;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name();
    }

    /**
     * @return the mappingTemplate
     */
    public File getMappingTemplate() {
        return mappingTemplate;
    }
}
