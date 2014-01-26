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

package eionet.cr.dto;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import eionet.cr.staging.imp.ImportStatus;

// TODO: Auto-generated Javadoc
/**
 * The DTO object representing a staging database.
 *
 * @author jaanus
 */
public class StagingDatabaseDTO {

    /** The id. */
    private int id;

    /** The name. */
    private String name;

    /** The creator. */
    private String creator;

    /** The created. */
    private Date created;

    /** The description. */
    private String description;

    /** The default query. */
    private String defaultQuery;

    /** The import status. */
    private ImportStatus importStatus;

    /** The import log. */
    private String importLog;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the creator.
     *
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the creator.
     *
     * @param creator the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Gets the created.
     *
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets the created.
     *
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).toString();
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the import status.
     *
     * @return the importStatus
     */
    public ImportStatus getImportStatus() {
        return importStatus;
    }

    /**
     * Sets the import status.
     *
     * @param importStatus the importStatus to set
     */
    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    /**
     * Gets the import log.
     *
     * @return the importLog
     */
    public String getImportLog() {
        return importLog;
    }

    /**
     * Sets the import log.
     *
     * @param importLog the importLog to set
     */
    public void setImportLog(String importLog) {
        this.importLog = importLog;
    }

    /**
     * Gets the default query.
     *
     * @return the defaultQuery
     */
    public String getDefaultQuery() {
        return defaultQuery;
    }

    /**
     * Sets the default query.
     *
     * @param defaultQuery the defaultQuery to set
     */
    public void setDefaultQuery(String defaultQuery) {
        this.defaultQuery = defaultQuery;
    }
}
