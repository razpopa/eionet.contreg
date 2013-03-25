package eionet.cr.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eionet.cr.common.Predicates;

/**
 * @author jaanus
 */
public class ObservationDTO {

    /** */
    public static final Map<String, String> PREDICATE_ALIASES = createPredicateAliases();

    /** */
    private String uri;
    private String dataSet;
    private String indicator;
    private String breakdown;
    private String refArea;
    private String timePeriod;
    private String unit;
    private String note;
    private String flag;
    private Double obsValue;

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri
     *            the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the dataSet
     */
    public String getDataSet() {
        return dataSet;
    }

    /**
     * @param dataSet
     *            the dataSet to set
     */
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * @return the indicator
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * @param indicator
     *            the indicator to set
     */
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    /**
     * @return the breakdown
     */
    public String getBreakdown() {
        return breakdown;
    }

    /**
     * @param breakdown
     *            the breakdown to set
     */
    public void setBreakdown(String breakdown) {
        this.breakdown = breakdown;
    }

    /**
     * @return the refArea
     */
    public String getRefArea() {
        return refArea;
    }

    /**
     * @param refArea
     *            the refArea to set
     */
    public void setRefArea(String refArea) {
        this.refArea = refArea;
    }

    /**
     * @return the timePeriod
     */
    public String getTimePeriod() {
        return timePeriod;
    }

    /**
     * @param timePeriod
     *            the timePeriod to set
     */
    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     *            the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the obsValue
     */
    public Double getObsValue() {
        return obsValue;
    }

    /**
     * @param obsValue
     *            the obsValue to set
     */
    public void setObsValue(Double obsValue) {
        this.obsValue = obsValue;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note
     *            the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @param flag
     *            the flag to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     *
     * @return
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
     *
     * @return
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
