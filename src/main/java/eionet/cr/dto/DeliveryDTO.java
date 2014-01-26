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

import eionet.cr.util.URIUtil;

// TODO: Auto-generated Javadoc
/**
 * The DTO for deliveries.
 *
 * @author altnyris
 */
public class DeliveryDTO extends HarvestBaseDTO implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The title. */
    private String title;

    /** The subject uri. */
    private String subjectUri;

    /** The file cnt. */
    private int fileCnt;

    /** The period. */
    private String period;

    /** The start year. */
    private String startYear;

    /** The end year. */
    private String endYear;

    /** The locality. */
    private String locality;

    /** The date. */
    private String date;

    /** The coverage note. */
    private String coverageNote;

    /**
     * Instantiates a new delivery dto.
     */
    public DeliveryDTO() {
    }

    /**
     * Instantiates a new delivery dto.
     *
     * @param subjecUri the subjec uri
     */
    public DeliveryDTO(String subjecUri) {
        this.subjectUri = subjecUri;
        title = URIUtil.extractURILabel(subjecUri, SubjectDTO.NO_LABEL);
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the file cnt.
     *
     * @return the file cnt
     */
    public int getFileCnt() {
        return fileCnt;
    }

    /**
     * Sets the file cnt.
     *
     * @param fileCnt the new file cnt
     */
    public void setFileCnt(int fileCnt) {
        this.fileCnt = fileCnt;
    }

    /**
     * Gets the period.
     *
     * @return the period
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     *
     * @param period the new period
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * Gets the locality.
     *
     * @return the locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * Sets the locality.
     *
     * @param locality the new locality
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the subject uri.
     *
     * @return the subject uri
     */
    public String getSubjectUri() {
        return subjectUri;
    }

    /**
     * Sets the subject uri.
     *
     * @param subjectUri the new subject uri
     */
    public void setSubjectUri(String subjectUri) {
        this.subjectUri = subjectUri;
    }

    /**
     * Gets the start year.
     *
     * @return the start year
     */
    public String getStartYear() {
        return startYear;
    }

    /**
     * Sets the start year.
     *
     * @param startYear the new start year
     */
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    /**
     * Gets the end year.
     *
     * @return the end year
     */
    public String getEndYear() {
        return endYear;
    }

    /**
     * Sets the end year.
     *
     * @param endYear the new end year
     */
    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    /**
     * Gets the coverage note.
     *
     * @return the coverage note
     */
    public String getCoverageNote() {
        return coverageNote;
    }

    /**
     * Sets the coverage note.
     *
     * @param coverageNote the new coverage note
     */
    public void setCoverageNote(String coverageNote) {
        this.coverageNote = coverageNote;
    }
}
