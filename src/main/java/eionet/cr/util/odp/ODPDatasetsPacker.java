package eionet.cr.util.odp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import eionet.cr.common.Namespace;
import eionet.cr.common.Predicates;
import eionet.cr.dao.DAOException;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HelperDAO;
import eionet.cr.dao.ScoreboardSparqlDAO;
import eionet.cr.dao.SearchDAO;
import eionet.cr.dto.SubjectDTO;
import eionet.cr.util.URIUtil;
import eionet.cr.util.Util;

/**
 * Generates ODP (Open Data Portal, http://open-data.europa.eu) datasets' metadata packages from the metadata of
 * a selected set of indicators. The output generated into a given stream, and is a ZIP file consisting of one RDF/XML formatted
 * metadata file per indicator.
 *
 * @author Jaanus
 */
public class ODPDatasetsPacker {

    /** URI of the "main" dataset, as opposed to the "virtual" datasets we generate for eacg indicator. */
    private static final String MAIN_DATASET_URI =
            "http://semantic.digital-agenda-data.eu/dataset/digital-agenda-scoreboard-key-indicators";

    /** Expected charset encoding of the generated output. */
    private static final String ENCODING = "UTF-8";

    /** Default namespace of the generated RDF/XML files that will be zipped. */
    private static final Namespace DEFAULT_NAMESPACE = Namespace.ECODP;

    /** Namespaces used in the generated RDF/XML files about the datasets. */
    private static final List<Namespace> DATASET_FILE_NAMESPACES = buildDatasetFileNamespaces();

    /** Namespaces used in the generated manifest file. */
    private static final List<Namespace> MANIFEST_FILE_NAMESPACES = buildManifestFileNamespaces();

    /** URIs of indicators for which the RDF/XML formatted metadata shall be generated. */
    private List<String> indicatorUris;

    /** List of {@link SubjectDTO} where each member represents an indicator from {@link #indicatorUris}. */
    List<SubjectDTO> indicatorSubjects;

    /** A {@link SubjectDTO} representing the "main" dataset identified by {@link #MAIN_DATASET_URI}. */
    private SubjectDTO mainDstSubject;

    /** A boolean indicating if {@link #prepare()} has already been called. */
    private boolean isPrepareCalled;

    /** */
    private String datasetUri;

    /** */
    private HashMap<String, List<String>> indicatorToRefAreas = new HashMap<String, List<String>>();

    /** */
    private HashMap<String, SubjectDTO> indicatorSources = new HashMap<String, SubjectDTO>();

    /** */
    private ODPAction odpAction;

    /**
     * Main constructor for generating ODP dataset metadata package for the given indicators.
     *
     * @param datasetUri
     * @param indicatorUris The URIs of the indicators whose metadata is to be packaged.
     * @param odpAction
     */
    public ODPDatasetsPacker(String datasetUri, List<String> indicatorUris, ODPAction odpAction) {

        if (StringUtils.isBlank(datasetUri)) {
            throw new IllegalArgumentException("The given dataset URIs must not be blank!");
        }
        if (CollectionUtils.isEmpty(indicatorUris)) {
            throw new IllegalArgumentException("The given list of indicatior URIs must not be empty!");
        }
        if (odpAction == null) {
            throw new IllegalArgumentException("The given ODP action must not be null!");
        }

        this.datasetUri = datasetUri;
        this.indicatorUris = indicatorUris;
        this.odpAction = odpAction;
    }

    /**
     * Does preparations for the {@link #execute(OutputStream)} method, so it should be called before tha latter, otherwise the
     * latter will throw {@link IllegalStateException}.
     *
     * The reason for this method is that we can do preparations (e.g. get various stuff from database and triplestore) before
     * we start streaming the output. This is convenient for exception handling in Stripes action bean events that return a
     * streaming resolution.
     *
     * @throws DAOException If data access error occurs.
     */
    public void prepare() throws DAOException {

        isPrepareCalled = true;

        indicatorSubjects = DAOFactory.get().getDao(SearchDAO.class).getSubjectsData(indicatorUris, null);
        if (CollectionUtils.isEmpty(indicatorSubjects)) {
            throw new DAOException("Could not find any metadata about the given indicators!");
        }

        HelperDAO helperDao = DAOFactory.get().getDao(HelperDAO.class);
        mainDstSubject = helperDao.getSubject(datasetUri);
        if (mainDstSubject == null || mainDstSubject.getPredicateCount() == 0) {
            throw new DAOException("Could not find any metadata about the main (i.e. parent) dataset!");
        }

        ScoreboardSparqlDAO ssDao = DAOFactory.get().getDao(ScoreboardSparqlDAO.class);
        for (SubjectDTO indSubj : indicatorSubjects) {

            String indUri = indSubj.getUri();
            List<String> refAreas = ssDao.getDistinctUsedRefAreas(datasetUri, indUri);
            indicatorToRefAreas.put(indUri, refAreas);

            String indSourceUri = indSubj.getObjectValue(Predicates.DCTERMS_SOURCE);
            if (StringUtils.isNotBlank(indSourceUri) && !indicatorSources.containsKey(indSourceUri)) {
                SubjectDTO indSourceDTO = helperDao.getSubject(indSourceUri);
                indicatorSources.put(indSourceUri, indSourceDTO);
            }
        }
    }

    /**
     * The main execution method.
     *
     * @param outputStream Output stream where the zipped file should be written into.
     *
     * @throws IOException If any sort of output stream writing error occurs.
     * @throws XMLStreamException Thrown by methods from the {@link XMLStreamWriter} that is used by called methods.
     */
    public void execute(OutputStream outputStream) throws IOException, XMLStreamException {

        if (!isPrepareCalled) {
            throw new IllegalStateException("Prepare has not been called yet!");
        }

        int i = 0;
        ZipArchiveOutputStream zipOutput = null;
        try {
            zipOutput = new ZipArchiveOutputStream(outputStream);
            for (SubjectDTO indicatorSubject : indicatorSubjects) {
                createAndWriteDatasetEntry(zipOutput, indicatorSubject, i++);
            }
            createAndWriteManifestEntry(zipOutput);
        } finally {
            IOUtils.closeQuietly(zipOutput);
        }
    }

    /**
     * Creates and writes a ZIP archive entry file for the given indicator.
     *
     * @param zipOutput ZIP output where the entry goes into.
     * @param indSubject The indicator whose for whom the entry is written.
     * @param index 0-based index of the indicator (in the indicator list received from dataabse) that is being written.
     *
     * @throws IOException If any sort of output stream writing error occurs.
     * @throws XMLStreamException Thrown by methods from the {@link XMLStreamWriter} that is used by called methods.
     */
    private void createAndWriteDatasetEntry(ZipArchiveOutputStream zipOutput, SubjectDTO indSubject, int index)
            throws IOException, XMLStreamException {

        String id = indSubject.getObjectValue(Predicates.SKOS_NOTATION);
        if (StringUtils.isEmpty(id)) {
            id = URIUtil.extractURILabel(indSubject.getUri());
        }

        ZipArchiveEntry entry = new ZipArchiveEntry("datasets/" + id + ".rdf");
        zipOutput.putArchiveEntry(entry);
        writeDatasetEntry(zipOutput, indSubject, index);
        zipOutput.closeArchiveEntry();
    }

    /**
     * Writes a ZIP archive entry file for the given indicator.
     *
     * @param zipOutput ZIP output where the entry goes into.
     * @param indSubject The indicator whose for whom the entry is written.
     * @param index 0-based index of the indicator (in the indicator list received from dataabse) that is being written.
     *
     * @throws XMLStreamException Thrown by methods from the {@link XMLStreamWriter} that is used by called methods.
     */
    private void writeDatasetEntry(ZipArchiveOutputStream zipOutput, SubjectDTO indSubject, int index) throws XMLStreamException {

        // Prepare indicator URI.
        String uri = indSubject.getUri();

        // Prepare indicator skos:notation.
        String skosNotation = indSubject.getObjectValue(Predicates.SKOS_NOTATION);
        if (StringUtils.isBlank(skosNotation)) {
            skosNotation = URIUtil.extractURILabel(uri);
        }

        // Prepare indicator skos:prefLabel.
        String skosPrefLabel = indSubject.getObjectValue(Predicates.SKOS_PREF_LABEL);
        if (StringUtils.isBlank(skosPrefLabel)) {
            skosPrefLabel = skosNotation;
        }

        // Prepare indicator skos:altLabel.
        String skosAltLabel = indSubject.getObjectValue(Predicates.SKOS_ALT_LABEL);
        if (StringUtils.isBlank(skosAltLabel)) {
            skosAltLabel = skosNotation;
        }

        // Prepare indicator skos:definition.
        String skosDefinition = indSubject.getObjectValue(Predicates.SKOS_DEFINITION);
        if (StringUtils.isBlank(skosDefinition)) {
            skosDefinition = skosNotation;
        }

        // Prepare indicator skos:notes.
        String skosNotes = indSubject.getObjectValue(Predicates.SKOS_NOTES);

        // Prepare indicator source description.
        String sourceDescription = StringUtils.EMPTY;
        String indSourceUri = indSubject.getObjectValue(Predicates.DCTERMS_SOURCE);
        SubjectDTO sourceDTO = indicatorSources.get(indSourceUri);
        if (sourceDTO != null) {

            String sourcePrefLabel = sourceDTO.getObjectValue(Predicates.SKOS_PREF_LABEL);
            if (StringUtils.isNotBlank(sourcePrefLabel)) {
                sourceDescription += sourcePrefLabel;
            }

            String sourceDefinition = sourceDTO.getObjectValue(Predicates.SKOS_DEFINITION);
            if (StringUtils.isNotBlank(sourceDefinition)) {
                sourceDescription += "\n" + sourceDefinition;
            }

            String sourceNotes = sourceDTO.getObjectValue(Predicates.SKOS_NOTES);
            if (StringUtils.isNotBlank(sourceNotes)) {
                sourceDescription += "\n" + sourceNotes;
            }

            sourceDescription = sourceDescription.trim();
            if (StringUtils.isNotBlank(sourceDescription)) {
                sourceDescription = "The original source of this dataset is:\n" + sourceDescription;
            }
        }

        // Prepare issued date from the main dataset.
        String dctIssued = mainDstSubject.getObjectValue(Predicates.DCTERMS_ISSUED);

        // Prepare modification date from the main dataset.
        List<String> modifiedDates = mainDstSubject.getObjectValues(Predicates.DCTERMS_MODIFIED);
        String dctModified = StringUtils.EMPTY;
        if (CollectionUtils.isNotEmpty(modifiedDates)) {
            Collections.sort(modifiedDates);
            dctModified = modifiedDates.get(modifiedDates.size() - 1).trim();
        }
        if (StringUtils.isBlank(dctModified)) {
            dctModified = Util.virtuosoDateToString(new Date());
        }

        // Prepare the main dataset's identifier.
        String mainDstIdentifier = URIUtil.extractURILabel(mainDstSubject.getUri());

        // Prepare the main dataset's status.
        String datasetStatus = mainDstSubject.getObjectValue(Predicates.ADMS_STATUS);
        if (StringUtils.isBlank(datasetStatus)) {
            datasetStatus = "http://purl.org/adms/status/UnderDevelopment";
        }

        // Prepare the main dataset's accrual periodicity.
        String accrualPeriodicity = mainDstSubject.getObjectValue(Predicates.DCTERMS_ACCRUAL_PERIODICITY);

        // Prepare STAX indenting writer based on a Java XMLStreamWriter that is based on the given zipped output.
        XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(zipOutput, ENCODING);
        IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(xmlWriter);

        // Start the XML document
        writer.writeStartDocument(ENCODING, "1.0");

        // Register all relevant namespaces.
        registerNamespaces(DATASET_FILE_NAMESPACES, writer);

        // Write root element start tag + default namespace
        writer.writeStartElement(Namespace.RDF.getUri(), "RDF");
        writer.writeDefaultNamespace(DEFAULT_NAMESPACE.getUri());

        // Write all other namespace prefixes.
        for (Namespace namespace : DATASET_FILE_NAMESPACES) {
            writer.writeNamespace(namespace.getPrefix(), namespace.getUri());
        }

        // Start the dataset tag.
        writer.writeStartElement(Namespace.DCAT.getUri(), "Dataset");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", uri);

        // Write dct:title
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters(skosPrefLabel);
        writer.writeEndElement();

        // Write dct:alternative
        writer.writeStartElement(Namespace.DCT.getUri(), "alternative");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters(skosAltLabel);
        writer.writeEndElement();

        // Write dct:description from skos:definition
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters(skosDefinition);
        writer.writeEndElement();

        // Write dct:description from the indicator source description
        if (StringUtils.isNotBlank(sourceDescription)) {
            writer.writeStartElement(Namespace.DCT.getUri(), "description");
            writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
            writer.writeCharacters(sourceDescription.trim());
            writer.writeEndElement();
        }

        // Write dct:description about the main dataset
        if (mainDstSubject != null) {
            writer.writeStartElement(Namespace.DCT.getUri(), "description");
            writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
            writer.writeCharacters("This dataset is part of another dataset:\nhttp://digital-agenda-data.eu/datasets/"
                    + mainDstIdentifier.replace('-', '_'));
            writer.writeEndElement();
        }

        // Write dct:description from skos:notes
        if (StringUtils.isNotBlank(skosNotes)) {
            writer.writeStartElement(Namespace.DCT.getUri(), "description");
            writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
            writer.writeCharacters(skosNotes);
            writer.writeEndElement();
        }

        // Write dct:identifier
        writer.writeStartElement(Namespace.DCT.getUri(), "identifier");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters(skosNotation);
        writer.writeEndElement();

        // Write ecodp:interoperabilityLevel
        writer.writeStartElement(Namespace.ECODP.getUri(), "interoperabilityLevel");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", "http://open-data.europa.eu/kos/interoperability-level/Legal");
        writer.writeEndElement();

        // Write ecodp:datasetType
        writer.writeStartElement(Namespace.ECODP.getUri(), "datasetType");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", "http://open-data.europa.eu/kos/dataset-type/Statistical");
        writer.writeEndElement();

        // Write ecodp:isDocumentedBy for the main home page about the main dataset
        writer.writeStartElement(Namespace.ECODP.getUri(), "isDocumentedBy");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.ECODP.getUri(), "documentationType");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about",
                "http://open-data.europa.eu/kos/documentation-type/MainDocumentation");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        // writer.writeCharacters("http://digital-agenda-data.eu/datasets/digital_agenda_scoreboard_key_indicators");
        writer.writeCharacters("http://digital-agenda-data.eu/datasets/" + mainDstIdentifier.replace('-', '_'));
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("The dataset homepage");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("The main information about the dataset, with its key metadata and further links to downloads.");
        writer.writeEndElement();
        writer.writeEndElement();

        // Write ecodp:isDocumentedBy for the Scoreboard documentation page
        writer.writeStartElement(Namespace.ECODP.getUri(), "isDocumentedBy");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.ECODP.getUri(), "documentationType");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about",
                "http://open-data.europa.eu/kos/documentation-type/RelatedDocumentation");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        writer.writeCharacters("http://digital-agenda-data.eu/documentation");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("Reports and notes about the technical characteristics.");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("This page presents some reports and notes about the technical characteristics of the project"
                + "developing the dataset's present visualisation tool and semantic repository.");
        writer.writeEndElement();
        writer.writeEndElement();

        // Write ecodp:isDocumentedBy for the main dataset's visualisation page
        writer.writeStartElement(Namespace.ECODP.getUri(), "isDocumentedBy");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.ECODP.getUri(), "documentationType");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about",
                "http://open-data.europa.eu/kos/documentation-type/RelatedDocumentation");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        // writer.writeCharacters("http://digital-agenda-data.eu/datasets/digital_agenda_scoreboard_key_indicators/visualizations");
        writer.writeCharacters("http://digital-agenda-data.eu/datasets/" + mainDstIdentifier.replace('-', '_') + "/visualizations");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("The dataset's visualisations");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("Various dynamically generated visualisations (i.e. charts, diagrams) of the dataset contents.");
        writer.writeEndElement();
        writer.writeEndElement();

        // Write ecodp:isDocumentedBy for the list of main dataset's indicators
        writer.writeStartElement(Namespace.ECODP.getUri(), "isDocumentedBy");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.ECODP.getUri(), "documentationType");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about",
                "http://open-data.europa.eu/kos/documentation-type/RelatedDocumentation");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        // writer.writeCharacters("http://digital-agenda-data.eu/datasets/digital_agenda_scoreboard_key_indicators/indicators");
        writer.writeCharacters("http://digital-agenda-data.eu/datasets/" + mainDstIdentifier.replace('-', '_') + "/indicators");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("List and facts about all key indicators related.");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("A page that lists metadata about all key indicators related to this dataset.");
        writer.writeEndElement();
        writer.writeEndElement();

        // Write ecodp:isDocumentedBy for the Digital Agenda Scoreboard home page
        writer.writeStartElement(Namespace.ECODP.getUri(), "isDocumentedBy");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.ECODP.getUri(), "documentationType");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", "http://open-data.europa.eu/kos/documentation-type/RelatedWebPage");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        writer.writeCharacters("http://ec.europa.eu/digital-agenda/en/scoreboard");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("Home page of the Digital Agenda Scoreboard");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("Digital Agenda Scoreboard home page to which the visualisation page subordinates.");
        writer.writeEndElement();
        writer.writeEndElement();

        // Write ecodp:isDocumentedBy for the Digital Agenda's home page
        writer.writeStartElement(Namespace.ECODP.getUri(), "isDocumentedBy");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.ECODP.getUri(), "documentationType");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", "http://open-data.europa.eu/kos/documentation-type/RelatedWebPage");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        writer.writeCharacters("http://ec.europa.eu/digital-agenda");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("Home page of the Digital Agenda for Europe");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("Home page of the EU initiative in the context of which the dataset has been produced.");
        writer.writeEndElement();
        writer.writeEndElement();

        // Write dcat:distribution for the download link
        writer.writeStartElement(Namespace.DCAT.getUri(), "distribution");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("Download options for the entire dataset.");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        // writer.writeCharacters("http://digital-agenda-data.eu/datasets/digital_agenda_scoreboard_key_indicators");
        writer.writeCharacters("http://digital-agenda-data.eu/datasets/" + mainDstIdentifier.replace('-', '_'));
        writer.writeEndElement();
        writer.writeEmptyElement(Namespace.RDF.getUri(), "type");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "resource",
                "http://www.w3.org/TR/vocab-dcat#Download");
        writer.writeStartElement(Namespace.ECODP.getUri(), "distributionFormat");
        writer.writeCharacters("text/csv");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "distributionFormat");
        writer.writeCharacters("text/n3");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "distributionFormat");
        writer.writeCharacters("application/rdf+xml");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("The database with the selected indicators of the Digital Agenda Scoreboard is made of three tables: "
                + "a) a data table, with codes for the indicators, countries, years and values; "
                + "b) an indicators table with labels for indicators' codes, definition and scope, and a source code; "
                + "c) a sources table, with details about sources and links to more methodological information. "
                + "The codes allow the creation of relations between the 3 tables. ");
        writer.writeEndElement();
        if (StringUtils.isNotEmpty(dctModified)) {
            writer.writeStartElement(Namespace.DCT.getUri(), "modified");
            writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                    "http://www.w3.org/2001/XMLSchema#dateTime");
            writer.writeCharacters(dctModified);
            writer.writeEndElement();
        }
        if (StringUtils.isNotEmpty(dctIssued)) {
            writer.writeStartElement(Namespace.DCT.getUri(), "issued");
            writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                    "http://www.w3.org/2001/XMLSchema#dateTime");
            writer.writeCharacters(dctIssued);
            writer.writeEndElement();
        }
        writer.writeEndElement();

        // Write dcat:distribution for the SPARQL endpoint
        writer.writeStartElement(Namespace.DCAT.getUri(), "distribution");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.DCT.getUri(), "title");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("SPARQL endpoint of the dataset.");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "accessURL");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#anyURI");
        writer.writeCharacters("http://digital-agenda-data.eu/data/sparql");
        writer.writeEndElement();
        writer.writeEmptyElement(Namespace.RDF.getUri(), "type");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "resource",
                "http://www.w3.org/TR/vocab-dcat#WebService");
        writer.writeStartElement(Namespace.ECODP.getUri(), "distributionFormat");
        writer.writeCharacters("webservice/sparql");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.DCT.getUri(), "description");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("This SPARQL endpoint (for machines regarding the SPARQL protocol for RDF) offers a public "
                + "service to the statistical data allowing anyone to build applications based on the most recent data.");
        writer.writeEndElement();
        writer.writeEndElement();

        // Write reference areas.
        List<String> refAreas = indicatorToRefAreas.get(uri);
        if (CollectionUtils.isNotEmpty(refAreas)) {
            for (String refArea : refAreas) {

                writer.writeStartElement(Namespace.DCT.getUri(), "spatial");
                writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
                writer.writeAttribute(Namespace.RDF.getUri(), "about", refArea);
                writer.writeEndElement();
            }
        }

        // Write dct:publisher
        writer.writeStartElement(Namespace.DCT.getUri(), "publisher");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about",
                "http://publications.europa.eu/resource/authority/corporate-body/CNECT");
        writer.writeEndElement();

        // Write ecodp:contactPoint
        writer.writeStartElement(Namespace.ECODP.getUri(), "contactPoint");
        writer.writeStartElement(Namespace.FOAF.getUri(), "agent");
        writer.writeAttribute(Namespace.RDF.getUri(), "about",
                "http://publications.europa.eu/resource/authority/corporate-body/CNECT/C4");
        writer.writeEmptyElement(Namespace.FOAF.getUri(), "mbox");
        writer.writeAttribute(Namespace.RDF.getUri(), "resource", "mailto:CNECT-F4@ec.europa.eu");
        writer.writeEmptyElement(Namespace.FOAF.getUri(), "workplaceHomepage");
        writer.writeAttribute(Namespace.RDF.getUri(), "resource", "http://digital-agenda-data.eu/");
        writer.writeStartElement(Namespace.FOAF.getUri(), "name");
        writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
        writer.writeCharacters("DG CONNECT Unit F4 Knowledge Base");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();

        // Write dct:issued
        if (StringUtils.isNotEmpty(dctIssued)) {
            writer.writeStartElement(Namespace.DCT.getUri(), "issued");
            writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                    "http://www.w3.org/2001/XMLSchema#dateTime");
            writer.writeCharacters(dctIssued);
            writer.writeEndElement();
        }

        // Write dct:modified (mandatory, so don't even check if empty)
        writer.writeStartElement(Namespace.DCT.getUri(), "modified");
        writer.writeAttribute(Namespace.RDF.getPrefix(), Namespace.RDF.getUri(), "datatype",
                "http://www.w3.org/2001/XMLSchema#dateTime");
        writer.writeCharacters(dctModified);
        writer.writeEndElement();

        // Write dct:license
        writer.writeStartElement(Namespace.DCT.getUri(), "license");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", "http://open-data.europa.eu/kos/licence/EuropeanCommission");
        writer.writeEndElement();

        // Write ecodp:datasetStatus
        // TODO: get from the main dataset object actually
        writer.writeStartElement(Namespace.ECODP.getUri(), "datasetStatus");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", datasetStatus);
        writer.writeEndElement();

        // Write dct:language
        writer.writeStartElement(Namespace.DCT.getUri(), "language");
        writer.writeEmptyElement(Namespace.SKOS.getUri(), "Concept");
        writer.writeAttribute(Namespace.RDF.getUri(), "about", "http://publications.europa.eu/resource/authority/language/ENG");
        writer.writeEndElement();

        // Write ecodp:accrualPeriodicity
        if (StringUtils.isNotBlank(accrualPeriodicity)) {
            writer.writeStartElement(Namespace.ECODP.getUri(), "accrualPeriodicity");
            writer.writeAttribute(Namespace.XML.getPrefix(), Namespace.XML.getUri(), "lang", "en");
            writer.writeCharacters(accrualPeriodicity);
            writer.writeEndElement();
        }

        // Write dct:temporal
        // TODO: Currently not in main dataset object, but should it be there?
        writer.writeStartElement(Namespace.DCT.getUri(), "temporal");
        writer.writeAttribute(Namespace.RDF.getUri(), "parseType", "Resource");
        writer.writeStartElement(Namespace.ECODP.getUri(), "periodStart");
        writer.writeCharacters("2001-01-01");
        writer.writeEndElement();
        writer.writeStartElement(Namespace.ECODP.getUri(), "periodEnd");
        writer.writeCharacters("2013-12-31");
        writer.writeEndElement();
        writer.writeEndElement();

        // End the dataset tag.
        writer.writeEndElement();

        // End the root tag.
        writer.writeEndElement();

        // End the document
        writer.writeEndDocument();
    }

    /**
     *
     * @param zipOutput
     * @throws XMLStreamException
     * @throws IOException
     */
    private void createAndWriteManifestEntry(ZipArchiveOutputStream zipOutput) throws XMLStreamException, IOException {

        ZipArchiveEntry entry = new ZipArchiveEntry("manifest.xml");
        zipOutput.putArchiveEntry(entry);

        // Prepare STAX indenting writer based on a Java XMLStreamWriter that is based on the given zipped output.
        XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(zipOutput, ENCODING);
        IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(xmlWriter);

        int i = 1;
        writeManifestHeader(writer);
        for (SubjectDTO indicatorSubject : indicatorSubjects) {
            writeOdpAction(writer, indicatorSubject, i++);
        }
        writeManifestFooter(writer);

        zipOutput.closeArchiveEntry();
    }

    /**
     *
     * @param writer
     * @param indicatorSubject
     * @param index
     * @throws XMLStreamException
     */
    private void writeOdpAction(IndentingXMLStreamWriter writer, SubjectDTO indicatorSubject, int index) throws XMLStreamException {

        String indicatorUri = indicatorSubject.getUri();
        String indicatorNotation = indicatorSubject.getObjectValue(Predicates.SKOS_NOTATION);
        if (StringUtils.isBlank(indicatorNotation)) {
            indicatorNotation = URIUtil.extractURILabel(indicatorUri);
        }

        // Start the ecodp:action tag.
        writer.writeStartElement(Namespace.ECODP.getUri(), "action");

        // Write attributes of the ecodp:action tag.
        writer.writeAttribute(Namespace.ECODP.getUri(), "id", odpAction.getNameCamelCase() + index);
        writer.writeAttribute(Namespace.ECODP.getUri(), "object-ckan-name", indicatorNotation);
        writer.writeAttribute(Namespace.ECODP.getUri(), "object-type", "dataset");
        writer.writeAttribute(Namespace.ECODP.getUri(), "object-uri", indicatorUri);

        // Start the ecodp:action refinement tag.
        if (ODPAction.ADD_DRAFT.equals(odpAction) || ODPAction.ADD_PUBLISHED.equals(odpAction)) {

            writer.writeEmptyElement(Namespace.ECODP.getUri(), "add-replace");
            writer.writeAttribute(Namespace.ECODP.getUri(), "object-status", ODPAction.ADD_DRAFT.equals(odpAction) ? "draft"
                    : "published");
            writer.writeAttribute(Namespace.ECODP.getUri(), "package-path", "/datasets/" + indicatorNotation + ".rdf");

        } else if (ODPAction.SET_DRAFT.equals(odpAction) || ODPAction.SET_PUBLISHED.equals(odpAction)) {

            writer.writeEmptyElement(Namespace.ECODP.getUri(), "change-status");
            writer.writeAttribute(Namespace.ECODP.getUri(), "object-status", ODPAction.SET_DRAFT.equals(odpAction) ? "draft"
                    : "published");

        } else if (ODPAction.REMOVE.equals(odpAction)) {
            writer.writeEmptyElement(Namespace.ECODP.getUri(), "remove");
        } else {
            throw new IllegalArgumentException("Unsupported ODP action: " + odpAction);
        }

        // Close the ecodp:action tag.
        writer.writeEndElement();
    }

    /**
     * @param writer
     * @throws XMLStreamException
     */
    private void writeManifestHeader(XMLStreamWriter writer) throws XMLStreamException {

        // Start the XML document
        writer.writeStartDocument(ENCODING, "1.0");

        // Register all relevant namespaces.
        registerNamespaces(MANIFEST_FILE_NAMESPACES, writer);

        // Write root element start tag (i.e. <ecodp:manifest>)
        writer.writeStartElement(Namespace.ECODP.getUri(), "manifest");

        // Write namespace prefixes in the root element start tag.
        for (Namespace namespace : MANIFEST_FILE_NAMESPACES) {
            writer.writeNamespace(namespace.getPrefix(), namespace.getUri());
        }

        String generationDateTime = Util.virtuosoDateToString(new Date());
        writer.writeAttribute(Namespace.ECODP.getUri(), "creation-date-time", generationDateTime);
        writer.writeAttribute(Namespace.ECODP.getUri(), "package-id", generationDateTime);
        writer.writeAttribute(Namespace.ECODP.getUri(), "priority", "normal");
        writer.writeAttribute(Namespace.ECODP.getUri(), "publisher",
                "http://publications.europa.eu/resource/authority/corporate-body/CNECT");
        writer.writeAttribute(Namespace.ECODP.getUri(), "version", "1.0");
        writer.writeAttribute(Namespace.XSI.getUri(), "schemaLocation",
                "http://open-data.europa.eu/ontologies/protocol-v1.0/odp-protocol.xsd");
    }

    /**
     *
     * @param writer
     * @throws XMLStreamException
     */
    private void writeManifestFooter(XMLStreamWriter writer) throws XMLStreamException {

        // Close root element tag
        writer.writeEndElement();
    }

    /**
     * Registers the given namespaces in the given {@link XMLStreamWriter}, by calling setPrefix(...) of the latter for each.
     *
     * @param xmlWriter The namespaces to register.
     * @param xmlWriter The writer to register in.
     * @throws XMLStreamException In case the write throws exception.
     */
    private void registerNamespaces(List<Namespace> namespaces, XMLStreamWriter xmlWriter) throws XMLStreamException {

        for (Namespace namespace : namespaces) {
            xmlWriter.setPrefix(namespace.getPrefix(), namespace.getUri());
        }
    }

    /**
     * Build a list of namespaces used in the generated RDF/XML files about the datasets.
     *
     * @return The list.
     */
    private static List<Namespace> buildDatasetFileNamespaces() {

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
     * Build a list of namespaces used in the generated manifest file.
     *
     * @return The list.
     */
    private static List<Namespace> buildManifestFileNamespaces() {

        ArrayList<Namespace> list = new ArrayList<Namespace>();
        list.add(Namespace.ECODP);
        list.add(Namespace.XSI);
        return list;
    }

}
