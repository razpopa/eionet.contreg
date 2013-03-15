package eionet.cr.util.xlwrap;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.openrdf.OpenRDFException;

import at.jku.xlwrap.common.XLWrapException;
import at.jku.xlwrap.exec.XLWrapMaterializer;
import at.jku.xlwrap.map.MappingParser;
import at.jku.xlwrap.map.XLWrapMapping;

import com.hp.hpl.jena.rdf.model.Model;

import eionet.cr.util.jena.JenaUtil;

/**
 * Utility class for importing an MS Excel or OpenDocument spreadsheet into the RDF model and triple store, using a given
 * Spreadsheet-to-RDF mapping. The library used is XLWrap (http://xlwrap.sourceforge.net/) and the mapping must be in TriG syntax:
 * http://wifo5-03.informatik.uni-mannheim.de/bizer/trig/.
 *
 * @author jaanus
 */
public class XLWrapUtil {

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
     * @param args
     * @throws IOException
     * @throws XLWrapException
     * @throws OpenRDFException
     */
    public static void main(String[] args) throws IOException, XLWrapException, OpenRDFException {

        File mappingFile = new File("C:/dev/projects/DigitalAgendaScoreboard/tmp/XLWrap/Andrei/units/unit.trig");
        int stmtCount = XLWrapUtil.importMapping(mappingFile, "http://semantic.digital-agenda-data.eu/testGraphs/xlwrap");
        System.out.println("Done. " + stmtCount + " triples added!");
    }
}
