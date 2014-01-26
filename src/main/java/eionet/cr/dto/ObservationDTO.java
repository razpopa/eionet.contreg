package eionet.cr.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eionet.cr.common.Predicates;

// TODO: Auto-generated Javadoc
/**
 * A DTO for representing a DataCube observation.
 *
 * @author jaanus
 */
public class ObservationDTO {

    /** The Constant PREDICATE_ALIASES. */
    public static final Map<String, String> PREDICATE_ALIASES = createPredicateAliases();

    /** The uri. */
    private String uri;

    /** The data set. */
    private String dataSet;

    /** The indicator. */
    private String indicator;

    /** The breakdown. */
    private String breakdown;

    /** The ref area. */
    private String refArea;

    /** The time period. */
    private String timePeriod;

    /** The unit. */
    private String unit;

    /** The note. */
    private String note;

    /** The flag. */
    private String flag;

    /** The obs value. */
    private Double obsValue;

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri.
     *
     * @param uri            the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Gets the data set.
     *
     * @return the dataSet
     */
    public String getDataSet() {
        return dataSet;
    }

    /**
     * Sets the data set.
     *
     * @param dataSet            the dataSet to set
     */
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Gets the indicator.
     *
     * @return the indicator
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * Sets the indicator.
     *
     * @param indicator            the indicator to set
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    /**
     * Gets the breakdown.
     *
     * @return the breakdown
     */
    public String getBreakdown() {
        return breakdown;
    }

    /**
     * Sets the breakdown.
     *
     * @param breakdown            the breakdown to set
     */
    public void setBreakdown(String breakdown) {
        this.breakdown = breakdown;
    }

    /**
     * Gets the ref area.
     *
     * @return the refArea
     */
    public String getRefArea() {
        return refArea;
    }

    /**
     * Sets the ref area.
     *
     * @param refArea            the refArea to set
     */
    public void setRefArea(String refArea) {
        this.refArea = refArea;
    }

    /**
     * Gets the time period.
     *
     * @return the timePeriod
     */
    public String getTimePeriod() {
        return timePeriod;
    }

    /**
     * Sets the time period.
     *
     * @param timePeriod            the timePeriod to set
     */
    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    /**
     * Gets the unit.
     *
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit.
     *
     * @param unit            the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Gets the obs value.
     *
     * @return the obsValue
     */
    public Double getObsValue() {
        return obsValue;
    }

    /**
     * Sets the obs value.
     *
     * @param obsValue            the obsValue to set
     */
    public void setObsValue(Double obsValue) {
        this.obsValue = obsValue;
    }

    /**
     * Gets the note.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note.
     *
     * @param note            the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets the flag.
     *
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Sets the flag.
     *
     * @param flag            the flag to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * Creates and returns filters map.
     *
     * @return The map.
     */
    public Map<String, String> createFilterMap() {

        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        if (StringUtils.isNotBlank(dataSet)) {
            map.put(Predicates.DATACUBE_DATA_SET, "dataSet");
        }
        if (StringUtils.isNotBlank(indicator)) {
            map.put(Predicates.DAS_INDICATOR, "indicator");
        }
        if (StringUtils.isNotBlank(breakdown)) {
            map.put(Predicates.DAS_BREAKDOWN, "breakdown");
        }
        if (StringUtils.isNotBlank(refArea)) {
            map.put(Predicates.DAS_REFAREA, "refArea");
        }
        if (StringUtils.isNotBlank(timePeriod)) {
            map.put(Predicates.DAS_TIMEPERIOD, "timePeriod");
        }
        if (StringUtils.isNotBlank(unit)) {
            map.put(Predicates.DAS_UNITMEASURE, "unit");
        }

        return map;
    }

    /**
     * Creates predicate aliases map.
     *
     * @return The map.
     */
    private static Map<String, String> createPredicateAliases() {

        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        map.put(Predicates.DATACUBE_DATA_SET, "dataSet");
        map.put(Predicates.DAS_INDICATOR, "indicator");
        map.put(Predicates.DAS_BREAKDOWN, "breakdown");
        map.put(Predicates.DAS_REFAREA, "refArea");
        map.put(Predicates.DAS_TIMEPERIOD, "timePeriod");
        map.put(Predicates.DAS_UNITMEASURE, "unit");
        map.put(Predicates.DAS_FLAG, "flag");
        map.put(Predicates.DAS_NOTE, "note");
        map.put(Predicates.SDMX_OBSVALUE, "obsValue");

        return map;
    }
}
