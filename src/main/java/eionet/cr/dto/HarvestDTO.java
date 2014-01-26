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
package eionet.cr.dto;

import java.util.Date;

import org.apache.commons.lang.time.DurationFormatUtils;

import eionet.cr.web.util.WebConstants;

// TODO: Auto-generated Javadoc
/**
 * A DTO representing a harvest.
 *
 * @author heinljab
 *
 */
public class HarvestDTO extends HarvestBaseDTO implements java.io.Serializable {

    /** The harvest id. */
    private Integer harvestId;

    /** The harvest source id. */
    private Integer harvestSourceId;

    /** The harvest type. */
    private String harvestType;

    /** The user. */
    private String user;

    /** The status. */
    private String status;

    /** The datetime started. */
    private java.util.Date datetimeStarted;

    /** The datetime finished. */
    private java.util.Date datetimeFinished;

    /** The encoding schemes. */
    private Integer encodingSchemes;

    /** The total statements. */
    private Integer totalStatements;

    /** The lit obj statements. */
    private Integer litObjStatements;

    /** The date string. */
    private String dateString;

    /** HTTP response code. */
    private int responseCode;

    /**
     * Instantiates a new harvest dto.
     */
    public HarvestDTO() {
    }

    /**
     * Gets the duration string.
     *
     * @return the duration string
     */
    public String getDurationString() {
        if (datetimeStarted == null) {
            return WebConstants.NOT_AVAILABLE;
        }
        if (datetimeFinished == null) {
            Date now = new Date();
            return DurationFormatUtils.formatDuration(now.getTime() - datetimeStarted.getTime(), "HH:mm:ss");
        }
        return DurationFormatUtils.formatDuration(datetimeFinished.getTime() - datetimeStarted.getTime(), "HH:mm:ss");
    }

    /**
     * Gets the harvest id.
     *
     * @return the harvestId
     */
    public Integer getHarvestId() {
        return harvestId;
    }

    /**
     * Sets the harvest id.
     *
     * @param harvestId the harvestId to set
     */
    public void setHarvestId(Integer harvestId) {
        this.harvestId = harvestId;
    }

    /**
     * Gets the harvest source id.
     *
     * @return the harvestSourceId
     */
    public Integer getHarvestSourceId() {
        return harvestSourceId;
    }

    /**
     * Sets the harvest source id.
     *
     * @param harvestSourceId the harvestSourceId to set
     */
    public void setHarvestSourceId(Integer harvestSourceId) {
        this.harvestSourceId = harvestSourceId;
    }

    /**
     * Gets the harvest type.
     *
     * @return the harvestType
     */
    public String getHarvestType() {
        return harvestType;
    }

    /**
     * Sets the harvest type.
     *
     * @param harvestType the harvestType to set
     */
    public void setHarvestType(String harvestType) {
        this.harvestType = harvestType;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the datetime started.
     *
     * @return the datetimeStarted
     */
    public java.util.Date getDatetimeStarted() {
        return datetimeStarted;
    }

    /**
     * Sets the datetime started.
     *
     * @param datetimeStarted the datetimeStarted to set
     */
    public void setDatetimeStarted(java.util.Date datetimeStarted) {
        this.datetimeStarted = datetimeStarted;
    }

    /**
     * Gets the datetime finished.
     *
     * @return the datetimeFinished
     */
    public java.util.Date getDatetimeFinished() {
        return datetimeFinished;
    }

    /**
     * Sets the datetime finished.
     *
     * @param datetimeFinished the datetimeFinished to set
     */
    public void setDatetimeFinished(java.util.Date datetimeFinished) {
        this.datetimeFinished = datetimeFinished;
    }

    /**
     * Gets the total statements.
     *
     * @return the totalStatements
     */
    public Integer getTotalStatements() {
        return totalStatements;
    }

    /**
     * Sets the total statements.
     *
     * @param totalStatements the totalStatements to set
     */
    public void setTotalStatements(Integer totalStatements) {
        this.totalStatements = totalStatements;
    }

    /**
     * Gets the lit obj statements.
     *
     * @return the litObjStatements
     */
    public Integer getLitObjStatements() {
        return litObjStatements;
    }

    /**
     * Sets the lit obj statements.
     *
     * @param litObjStatements the litObjStatements to set
     */
    public void setLitObjStatements(Integer litObjStatements) {
        this.litObjStatements = litObjStatements;
    }

    /**
     * Gets the encoding schemes.
     *
     * @return the encodingSchemes
     */
    public Integer getEncodingSchemes() {
        return encodingSchemes;
    }

    /**
     * Sets the encoding schemes.
     *
     * @param encodingSchemes the encodingSchemes to set
     */
    public void setEncodingSchemes(Integer encodingSchemes) {
        this.encodingSchemes = encodingSchemes;
    }

    /**
     * Gets the date string.
     *
     * @return the dateString
     */
    public String getDateString() {
        return dateString;
    }

    /**
     * Sets the date string.
     *
     * @param dateString the dateString to set
     */
    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    /**
     * HTTP Response Code output.
     *
     * @return HTTP Response Code String representation
     */
    public String getResponseCodeString() {
        return (responseCode == 0 ? "N/A" : String.valueOf(responseCode));
    }

    /**
     * Gets the response code.
     *
     * @return the response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the response code.
     *
     * @param responseCode the new response code
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

}
