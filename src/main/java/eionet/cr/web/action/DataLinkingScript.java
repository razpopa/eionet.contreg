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
 *        Juhan Voolaid
 */

package eionet.cr.web.action;

/**
 * Web form bean that holds information about column and script.
 *
 * @author Juhan Voolaid
 */
public class DataLinkingScript {

    /** Script id. */
    private String scriptId;

    /** Selected column for the script. */
    private String column;

    /**
     * @return the scriptId
     */
    public String getScriptId() {
        return scriptId;
    }

    /**
     * @param scriptId
     *            the scriptId to set
     */
    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    /**
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column
     *            the column to set
     */
    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * Convenience method for creating {@link DataLinkingScript} with a one-liner.
     *
     * @param column The script's target column.
     * @param scriptId The script's id.
     * @return The created script POJO.
     */
    public static DataLinkingScript create(String column, String scriptId) {

        DataLinkingScript script = new DataLinkingScript();
        script.setColumn(column);
        script.setScriptId(scriptId);
        return script;
    }
}
