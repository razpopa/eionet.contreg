package eionet.cr.util.xlwrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.openrdf.OpenRDFException;

import at.jku.xlwrap.common.XLWrapException;
import at.jku.xlwrap.exec.XLWrapMaterializer;
import at.jku.xlwrap.map.MappingParser;
import at.jku.xlwrap.map.XLWrapMapping;

import com.hp.hpl.jena.rdf.model.Model;

import eionet.cr.common.TempFilePathGenerator;
import eionet.cr.util.FileDeletionJob;
import eionet.cr.util.jena.JenaUtil;

/**
 * Utility class for importing an MS Excel or OpenDocument spreadsheet into the RDF model and triple store, using a given
 * Spreadsheet-to-RDF mapping. The library used is XLWrap (http://xlwrap.sourceforge.net/) and the mapping must be in TriG syntax:
 * http://wifo5-03.informatik.uni-mannheim.de/bizer/trig/.
 *
 * @author jaanus
 */
public class XLWrapUtil {

    /** */
    private static final String FILE_URL_PLACEHOLDER = "@FILE_URL@";

    /**
     *
     * @param uploadType
     * @param spreadsheetFile
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws XLWrapException
     * @throws OpenRDFException
     */
    public static int importMapping(XLWrapUploadType uploadType, File spreadsheetFile) throws IOException, XLWrapException, OpenRDFException {

        File template = uploadType.getMappingTemplate();
        File target = TempFilePathGenerator.generate(XLWrapUploadType.MAPPING_FILE_EXTENSION);

        try {
            Properties properties = new Properties();
            properties.setProperty(FILE_URL_PLACEHOLDER, spreadsheetFile.toURI().toURL().toString());

            createMappingFile(template, target, properties);
            return importMapping(target, uploadType.getGraphUri());
        } finally {
            FileDeletionJob.register(target);
        }
    }

    /**
     *
     * @param mappingFile
     * @param graphUri
     * @return
     * @throws IOException
     * @throws IOException
     * @throws XLWrapException
     * @throws OpenRDFException
     */
    public static int importMapping(File mappingFile, String graphUri) throws IOException, XLWrapException, OpenRDFException {

        return importMapping(mappingFile.toURI().toURL(), graphUri);
    }

    /**
     *
     * @param mappingFileURL
     * @param graphUri
     * @return
     * @throws IOException
     * @throws XLWrapException
     * @throws OpenRDFException
     */
    public static int importMapping(URL mappingFileURL, String graphUri) throws IOException, XLWrapException, OpenRDFException {

        Model model = null;
        try {
            XLWrapMapping mapping = MappingParser.parse(mappingFileURL.toString());
            XLWrapMaterializer materializer = new XLWrapMaterializer();
            model = materializer.generateModel(mapping);
            return JenaUtil.saveModel(model, graphUri);
        } finally {
            JenaUtil.close(model);
        }
    }

    /**
     *
     * @param template
     * @param target
     * @param replacements
     * @return
     * @throws IOException
     */
    private static File createMappingFile(File template, File target, Properties replacements) throws IOException {

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(template));
            writer = new BufferedWriter(new FileWriter(target));

            String line = null;
            boolean replaceTokens = replacements != null && !replacements.isEmpty();
            while ((line = reader.readLine()) != null) {
                if (replaceTokens) {
                    for (Entry<Object, Object> entry : replacements.entrySet()) {
                        line = line.replace(entry.getKey().toString(), entry.getValue().toString());
                    }
                }

                writer.write(line);
                writer.write(IOUtils.LINE_SEPARATOR);
            }
        } finally {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(reader);
        }

        return target;
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws XLWrapException
     * @throws OpenRDFException
     */
    public static void main(String[] args) throws IOException, XLWrapException, OpenRDFException {

        File mappingFile = new File("C:/dev/projects/DigitalAgendaScoreboard/apphome/harvests/eionet.cr.tempfile-1363623700877-029a7842-71bd-45ee-a984-90109c56f4a3.trig");
        int stmtCount = XLWrapUtil.importMapping(mappingFile, "http://semantic.digital-agenda-data.eu/testGraphs/xlwrap");
        System.out.println("Done. " + stmtCount + " triples added!");
    }
}
