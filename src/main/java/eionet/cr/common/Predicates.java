/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 2.0.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Jaanus Heinlaid, Tieto Eesti
 */
package eionet.cr.common;

public interface Predicates {

    // TODO: The DC 1.1 elements have had "deprecated" status since 2007. We should change to DCTERMS.
    String DC_TITLE = "http://purl.org/dc/elements/1.1/title";
    String DC_DATE = "http://purl.org/dc/elements/1.1/date";
    String DC_COVERAGE = "http://purl.org/dc/elements/1.1/coverage";
    String DC_IDENTIFIER = "http://purl.org/dc/elements/1.1/identifier";
    String DC_SOURCE = "http://purl.org/dc/elements/1.1/source";
    String DC_SUBJECT = "http://purl.org/dc/elements/1.1/subject";
    String DC_CREATOR = "http://purl.org/dc/elements/1.1/creator";
    String DC_DESCRIPTION = "http://purl.org/dc/elements/1.1/description";
    String DC_PUBLISHER = "http://purl.org/dc/elements/1.1/publisher";
    String DC_CONTRIBUTOR = "http://purl.org/dc/elements/1.1/contributor";
    String DC_RELATION = "http://purl.org/dc/elements/1.1/relation";
    String DC_LANGUAGE = "http://purl.org/dc/elements/1.1/language";
    String DC_FORMAT = "http://purl.org/dc/elements/1.1/format";
    String DC_TYPE = "http://purl.org/dc/elements/1.1/type";

    String DC_MITYPE_TEXT = "http://purl.org/dc/dcmitype/Text";
    String DC_MITYPE_DATASET = "http://purl.org/dc/dcmitype/Dataset";
    String DC_MITYPE_IMAGE = "http://purl.org/dc/dcmitype/Image";

    String DCTERMS_ALTERNATIVE = "http://purl.org/dc/terms/alternative";
    String DCTERMS_TITLE = "http://purl.org/dc/terms/title";

    String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    String RDF_VALUE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#value";
    String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
    String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";
    String RDFS_SUBPROPERTY_OF = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";
    String RDFS_SUBCLASS_OF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
    String RDFS_COMMENT = "http://www.w3.org/2000/01/rdf-schema#comment";

    String SKOS_PREF_LABEL = "http://www.w3.org/2004/02/skos/core#prefLabel";

    String ROD_OBLIGATION_PROPERTY = "http://rod.eionet.europa.eu/schema.rdf#obligation";
    String ROD_INSTRUMENT_PROPERTY = "http://rod.eionet.europa.eu/schema.rdf#instrument";
    String ROD_LOCALITY_PROPERTY = "http://rod.eionet.europa.eu/schema.rdf#locality";
    String ROD_ISSUE_PROPERTY = "http://rod.eionet.europa.eu/schema.rdf#issue";
    String ROD_HAS_FILE = "http://rod.eionet.europa.eu/schema.rdf#hasFile";
    String ROD_PRODUCT_OF = "http://rod.eionet.europa.eu/schema.rdf#productOf";
    String ROD_PERIOD = "http://rod.eionet.europa.eu/schema.rdf#period";
    String ROD_RELEASED = "http://rod.eionet.europa.eu/schema.rdf#released";
    String ROD_COVERAGE_NOTE = "http://rod.eionet.europa.eu/schema.rdf#coverageNote";

    String CR_FIRSTSEEN_TIME = "urn:eionet:contreg:firstseen-time";
    String CR_URL = "urn:eionet:contreg:url";
    String CR_SCHEMA = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#xmlSchema";
    String CR_LAST_MODIFIED = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#contentLastModified";
    String CR_BYTE_SIZE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#byteSize";
    String CR_MEDIA_TYPE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#mediaType";
    String CR_CHARSET = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#characterSet";
    String CR_LAST_REFRESHED = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#lastRefreshed";
    String CR_BOOKMARK = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#userBookmark";
    String CR_BOOKMARK_TYPE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#Bookmark";
    String CR_SAVETIME = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#userSaveTime";
    String CR_HISTORY = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#userHistory";
    String CR_TAG = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#tag";
    String CR_COMMENT = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#comment";
    String CR_HAS_SOURCE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#hasSource";
    String CR_HAS_FEEDBACK = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#hasFeedback";
    String CR_FEEDBACK_FOR = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#feedbackFor";
    String CR_USER = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#user";
    String CR_USER_REVIEW_LAST_NUMBER =
        "http://cr.eionet.europa.eu/ontologies/contreg.rdf#userReviewLastNumber";
    String CR_HAS_ATTACHMENT = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#hasAttachment";
    String CR_HAS_FILE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#hasFile";
    String CR_FILE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#File";
    String CR_REDIRECTED_TO = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#redirectedTo";
    String CR_ERROR_MESSAGE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#errorMessage";
    String CR_FIRST_SEEN = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#firstSeen";
    String CR_SPARQL_QUERY = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#sparqlQuery";
    String CR_USE_INFERENCE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#useInference";
    String CR_HAS_FOLDER = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#hasFolder";
    String CR_ALLOW_SUBOBJECT_TYPE = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#allowSubObjectType";
    String CR_HARVESTED_STATEMENTS = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#harvestedStatements";
    String CR_COMPILED_DATASET = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#CompiledDataset";
    String CR_GENERATED_FROM = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#generatedFrom";

    String WGS_LAT = "http://www.w3.org/2003/01/geo/wgs84_pos#lat";
    String WGS_LONG = "http://www.w3.org/2003/01/geo/wgs84_pos#long";

    String AMP_ONTOLOGY_CODE = "http://rdfdata.eionet.europa.eu/amp/ontology/code";
    String AMP_ONTOLOGY_EEAPROJECT = "http://rdfdata.eionet.europa.eu/amp/ontology/eeaproject";
    String AMP_ONTOLOGY_FORCODE = "http://rdfdata.eionet.europa.eu/amp/ontology/forCode";
    String AMP_ONTOLOGY_FORYEAR = "http://rdfdata.eionet.europa.eu/amp/ontology/forYear";
}
