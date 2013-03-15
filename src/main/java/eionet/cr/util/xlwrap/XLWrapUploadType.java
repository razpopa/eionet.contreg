package eionet.cr.util.xlwrap;

import java.io.File;
import java.net.URL;

import eionet.cr.common.TempFilePathGenerator;

public enum XLWrapUploadType {

    INDICATORS("Indicators metadata", "File containing metadata of the Digital Agenda Scoreboard indicators", "http://semantic.digital-agenda-data.eu/metadata/indicators"),
    BREAKDOWNS("Breakdowns metadata", "File containing metadata of the Digital Agenda Scoreboard breakdowns", "http://semantic.digital-agenda-data.eu/metadata/breakdowns"),
    UNITS("Units metadata", "File containing metadata of the Digital Agenda Scoreboard units", "http://semantic.digital-agenda-data.eu/metadata/units");

    /** */
    private String title;
    private String hint;
    private String graphUri;

    /** */
    private File xlsFile;
    private URL mappingFileURL;

    /**
     * Constructor.
     * @param title
     * @param hint
     * @param xlsFileName
     * @param trigFileName
     * @param graphUri
     */
    private XLWrapUploadType(String title, String hint, String graphUri) {

        this.title = title;
        this.hint = hint;
        this.graphUri = graphUri;

        String lowerName = name().toLowerCase();
        this.xlsFile = TempFilePathGenerator.generate(lowerName + ".xls");
        this.mappingFileURL = getClass().getClassLoader().getResource(lowerName + ".trig");
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
     * @return the xlsFile
     */
    public File getXlsFile() {
        return xlsFile;
    }

    /**
     * @return the mappingFileURL
     */
    public URL getMappingFileURL() {
        return mappingFileURL;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name();
    }
}
