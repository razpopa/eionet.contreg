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
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        jaanus
 */

package eionet.cr.staging.exp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openrdf.model.vocabulary.XMLSchema;

/**
 * Type definition ...
 *
 * @author jaanus
 */
public class ObjectTypes {

    /** */
    private static final LinkedHashMap<String, ObjectType> TYPES_BY_URI = load();

    /**
     * Disable utility class constructor.
     */
    private ObjectTypes() {
        // Empty constructor.
    }

    /**
     * Gets the by uri.
     *
     * @param uri the uri
     * @return the by uri
     */
    public static ObjectType getByUri(String uri) {
        return TYPES_BY_URI.get(uri);
    }

    /**
     * Gets the map.
     *
     * @return the map
     */
    public static Map<String, ObjectType> getMap() {
        return TYPES_BY_URI;
    }

    /**
     * Load.
     *
     * @return the linked hash map
     */
    private static LinkedHashMap<String, ObjectType> load() {

        // FIXME load from a configuration file

        LinkedHashMap<String, ObjectType> result = new LinkedHashMap<String, ObjectType>();

        ObjectType qbObservation = createObservationType();
        result.put(qbObservation.getUri(), qbObservation);

        return result;
    }

    /**
     * Creates the observation type2.
     *
     * @return the object type
     */
    private static ObjectType createObservationType() {

        ObjectType qbObservation = new ObjectType("http://purl.org/linked-data/cube#Observation", "Data Cube observation");
        String ss = "http://semantic.digital-agenda-data.eu/data/<dataset>/<indicator>/<breakdown>/<unit>/<refArea>/<timePeriod>";
        qbObservation.setObjectUriTemplate(ss);

        // Indicator.
        ObjectProperty property =
                new ObjectProperty("http://semantic.digital-agenda-data.eu/def/property/indicator", "indicator",
                        "Indicator (code)", ObjectProperty.Range.RESOURCE);
        property.setValueTemplate("http://semantic.digital-agenda-data.eu/codelist/indicator/<value>");
        property.setHint("Expects a Eurostat indicator code. e.g. p_siscall, p_cuse2, etc.");
        qbObservation.addProperty(property, false, "indicator", "indicatorCode", "variable", "variableCode", "indic");

        // Breakdown.
        property =
                new ObjectProperty("http://semantic.digital-agenda-data.eu/def/property/breakdown", "breakdown",
                        "Breakdown (code)", ObjectProperty.Range.RESOURCE);
        property.setValueTemplate("http://semantic.digital-agenda-data.eu/codelist/breakdown/<value>");
        property.setHint("Expects a Eurostat breakdown code. e.g. 10_bb, 10_c10, etc.");
        qbObservation.addProperty(property, false, "breakdown", "brkDown", "brkdwn", "breakdownCode", "brkDownCode", "brkdwnCode");

        // Unit.
        property =
                new ObjectProperty("http://semantic.digital-agenda-data.eu/def/property/unit-measure", "unit", "Unit (code)",
                        ObjectProperty.Range.RESOURCE);
        property.setValueTemplate("http://semantic.digital-agenda-data.eu/codelist/unit-measure/<value>");
        property.setHint("Expects a Eurostat measurement unit code. e.g. pc_emp, pc_ent, pc_turn, etc.");
        qbObservation.addProperty(property, true, "unit", "unitMeasure", "unitCode");

        // Reference area.
        property =
                new ObjectProperty("http://semantic.digital-agenda-data.eu/def/property/ref-area", "refArea",
                        "Reference area (ISO-2 country code)", ObjectProperty.Range.RESOURCE);
        property.setValueTemplate("http://eurostat.linked-statistics.org/dic/geo#<value>");
        property.setHint("Expects a two letter country code as in ISO 3166-1 alpha-2 standard. e.g. AT, BE, DE, etc.");
        qbObservation.addProperty(property, true, "refArea", "country", "countryCode");

        // Time period.
        property =
                new ObjectProperty("http://semantic.digital-agenda-data.eu/def/property/time-period", "timePeriod",
                        "Time period (year)", ObjectProperty.Range.RESOURCE);
        property.setValueTemplate("http://reference.data.gov.uk/id/gregorian-year/<value>");
        property.setHint("Expects a 4-digit notation of a calendar year. e.g. 1999, 2000, 2001, etc.");
        qbObservation.addProperty(property, true, "timePeriod", "year", "time");

        // Observed value.
        property =
                new ObjectProperty("http://purl.org/linked-data/sdmx/2009/measure#obsValue", "obsValue",
                        "Observed value (a number)", ObjectProperty.Range.LITERAL);
        property.setDataType(XMLSchema.DOUBLE.stringValue());
        property.setHint("Expects an Observation's measured value, as a number. e.g. 0.789, 0.018, 1000, 4.324, etc.");
        qbObservation.addProperty(property, true, "value", "observedValue", "obsValue");

        // Note.
        property =
                new ObjectProperty("http://semantic.digital-agenda-data.eu/def/property/note", "note", "Note (any text)",
                        ObjectProperty.Range.LITERAL);
        property.setDataType(XMLSchema.STRING.stringValue());
        property.setHint("Expects any text that servers as a comment/note to the observation.");
        qbObservation.addProperty(property, false, "note", "notes", "comment", "comments");

        // Flag.
        property =
                new ObjectProperty("http://semantic.digital-agenda-data.eu/def/property/flag", "flag", "Flag (status flag)",
                        ObjectProperty.Range.RESOURCE);
        property.setValueTemplate("http://eurostat.linked-statistics.org/dic/flags#<value>");
        property.setHint("Expects a flag indicating the obsevration's status as in "
                + "http://eurostat.linked-statistics.org/dic/flags. e.g. u, n, p. r, etc.");
        qbObservation.addProperty(property, false, "flag", "status", "statusFlag", "flagStatus", "flags");

        return qbObservation;
    }

    /**
     * Adds the dummy types.
     *
     * @param map the map
     * @param noOfDummyTypes the no of dummy types
     */
    private static void addDummyTypes(LinkedHashMap<String, ObjectType> map, int noOfDummyTypes) {

        for (int i = 1; i <= noOfDummyTypes; i++) {

            String typeLabel = "DummyType" + i;
            ObjectType type = new ObjectType("http://dummy.org/type#" + typeLabel, typeLabel);

            ArrayList<ObjectProperty> properties = new ArrayList<ObjectProperty>();
            ArrayList<String> propLabels = new ArrayList<String>();
            for (int j = 1; j <= 12; j++) {

                String propLabel = typeLabel + "_prop" + j;
                propLabels.add(propLabel);

                ObjectProperty property =
                        new ObjectProperty("http://dummy.org/properties#" + propLabel, "prop_" + j, propLabel,
                                ObjectProperty.Range.LITERAL);
                type.addProperty(property, true, "col" + j);
                properties.add(property);
            }

            map.put(type.getUri(), type);
        }
    }
}
