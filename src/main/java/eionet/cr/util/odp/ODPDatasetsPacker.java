package eionet.cr.util.odp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import eionet.cr.common.Namespace;

/**
 *
 * Type definition ...
 *
 * @author Jaanus
 */
public class ODPDatasetsPacker {

    /** */
    private static final String ENCODING = "UTF-8";

    /** */
    private static final Namespace DEFAULT_NAMESPACE = Namespace.ECODP;

    /** */
    private static final Map<String, String> NAMESPACES_ = buildNamespacesMap();

    private static final List<Namespace> NAMESPACES = buildNamespacesList();

    /**
     * @throws IOException
     * @throws XMLStreamException
     *
     */
    private void execute() throws IOException, XMLStreamException {

        ZipArchiveOutputStream zipOutput = null;
        try {

            zipOutput = new ZipArchiveOutputStream(new File("/tmp/tryzip/try.zip"));

            for (int i = 1; i <= 3; i++) {
                String entryName = "entry" + i + ".txt";
                String entryContent = "võimalik" + i;
                // createAndWriteEntry(zipOutput, entryName, entryContent);
                createAndWriteEntry(zipOutput, i);
            }
        } finally {
            IOUtils.closeQuietly(zipOutput);
        }
    }

    /**
     *
     * @param zipOut
     * @param entryName
     * @param entryContent
     * @throws IOException
     */
    private void createAndWriteEntry(ZipArchiveOutputStream zipOutput, String entryName, String entryContent) throws IOException {

        ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
        zipOutput.putArchiveEntry(entry);

        byte[] bytes = entryContent.getBytes("UTF-8");
        zipOutput.write(bytes, 0, bytes.length);
        zipOutput.closeArchiveEntry();
    }

    /**
     *
     * @param zipOutput
     * @param index
     * @throws IOException
     * @throws XMLStreamException
     */
    private void createAndWriteEntry(ZipArchiveOutputStream zipOutput, int index) throws IOException, XMLStreamException {

        ZipArchiveEntry entry = new ZipArchiveEntry("entry" + index + ".xml");
        zipOutput.putArchiveEntry(entry);
        writeEntry(zipOutput, index);
        zipOutput.closeArchiveEntry();
    }

    /**
     *
     * @param zipOutput
     * @param index
     * @throws XMLStreamException
     */
    private void writeEntry(ZipArchiveOutputStream zipOutput, int index) throws XMLStreamException {

        XMLStreamWriter xmlWriterOrig = XMLOutputFactory.newInstance().createXMLStreamWriter(zipOutput, ENCODING);

        IndentingXMLStreamWriter xmlWriter = new IndentingXMLStreamWriter(xmlWriterOrig);
        xmlWriter.writeStartDocument(ENCODING, "1.0");
        registerNamespaces(xmlWriter);
        xmlWriter.writeStartElement(Namespace.RDF.getUri(), "RDF");
        xmlWriter.writeDefaultNamespace(DEFAULT_NAMESPACE.getUri());

        for (Namespace namespace : NAMESPACES) {
            xmlWriter.writeNamespace(namespace.getPrefix(), namespace.getUri());
        }

        xmlWriter.writeStartElement(Namespace.DCAT.getUri(), "Dataset");
        xmlWriter.writeAttribute(Namespace.RDF.getUri(), "about", "http://testime.ee/url" + index);
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement(Namespace.DCT.getUri(), "title");
        xmlWriter.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        xmlWriter.writeCharacters("pealkiri_" + index);
        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement();
        xmlWriter.writeEndDocument();
    }

    /**
     *
     * @param xmlWriter
     * @throws XMLStreamException
     */
    private void registerNamespaces(XMLStreamWriter xmlWriter) throws XMLStreamException {

        for (Namespace namespace : NAMESPACES) {
            xmlWriter.setPrefix(namespace.getPrefix(), namespace.getUri());
        }
    }

    /**
     *
     * @return
     */
    private static List<Namespace> buildNamespacesList() {

        ArrayList<Namespace> list = new ArrayList<Namespace>();
        list.add(Namespace.RDF);
        list.add(Namespace.RDFS);
        list.add(Namespace.OWL);
        list.add(Namespace.XSD);
        list.add(Namespace.DC);
        list.add(Namespace.DCT);
        list.add(Namespace.DCAM);
        list.add(Namespace.DCAT);
        list.add(Namespace.ECODP);
        list.add(Namespace.FOAF);
        list.add(Namespace.SKOS);
        list.add(Namespace.SKOS_XL);
        return list;
    }

    /**
     *
     * @return
     */
    private static Map<String, String> buildNamespacesMap() {

        HashMap<String, String> nss = new HashMap<String, String>();
        nss.put("dc", "http://purl.org/dc/elements/1.1/");
        nss.put("dcam", "http://purl.org/dc/dcam/");
        nss.put("dcat", "http://www.w3.org/ns/dcat#");
        nss.put("dct", "http://purl.org/dc/terms/");
        nss.put("ecodp", "http://open-data.europa.eu/ontologies/ec-odp#");
        nss.put("foaf", "http://xmlns.com/foaf/0.1/");
        nss.put("owl", "http://www.w3.org/2002/07/owl#");
        nss.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        nss.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        nss.put("skos", "http://www.w3.org/2004/02/skos/core#");
        nss.put("skos-xl", "http://www.w3.org/2008/05/skos-xl#");
        nss.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        return nss;
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws XMLStreamException
     */
    public static void main(String[] args) throws IOException, XMLStreamException {

        ODPDatasetsPacker packer = new ODPDatasetsPacker();
        packer.execute();
        System.out.println("Done!");
    }
}
