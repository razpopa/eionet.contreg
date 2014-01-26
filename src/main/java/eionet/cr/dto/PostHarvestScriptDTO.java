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
 * Agency. Portions created by Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Jaanus Heinlaid
 */

package eionet.cr.dto;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * A DTO for post-harvest scripts.
 *
 * @author Jaanus Heinlaid
 */
public class PostHarvestScriptDTO {

    /** Target type of a post-harvest script. */
    public enum TargetType {

        /** Source-specific post-harvest script. */
        SOURCE,
        /** Type-specific post-harvest script. */
        TYPE
    };

    /** The target type. */
    private TargetType targetType;

    /** The target url. */
    private String targetUrl;

    /** The title. */
    private String title;

    /** The script. */
    private String script;

    /** The position. */
    private int position;

    /** The active. */
    private boolean active;

    /** The run once. */
    private boolean runOnce = true;

    /** The id. */
    private int id;

    /** The last modified. */
    private Date lastModified;

    /**
     * Checks if is active.
     *
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the target type.
     *
     * @return the targetType
     */
    public TargetType getTargetType() {
        return targetType;
    }

    /**
     * Gets the target url.
     *
     * @return the targetUrl
     */
    public String getTargetUrl() {
        return targetUrl;
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
     * Gets the script.
     *
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
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
     * Sets the target type.
     *
     * @param targetType the targetType to set
     */
    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    /**
     * Sets the target url.
     *
     * @param targetUrl the targetUrl to set
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Sets the title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the script.
     *
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Sets the position.
     *
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Checks if is run once.
     *
     * @return the runOnce
     */
    public boolean isRunOnce() {
        return runOnce;
    }

    /**
     * Sets the run once.
     *
     * @param runOnce the runOnce to set
     */
    public void setRunOnce(boolean runOnce) {
        this.runOnce = runOnce;
    }

    /**
     * Gets the last modified.
     *
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Sets the last modified.
     *
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return title;
    }
}
