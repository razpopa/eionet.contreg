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

package eionet.cr.dto;

// TODO: Auto-generated Javadoc
/**
 * Folder item object that is used for selecting, renaming and deleting.
 *
 * @author Juhan Voolaid
 */
public class RenameFolderItemDTO {

    /** The uri. */
    private String uri;

    /** The name. */
    private String name;

    /** The new name. */
    private String newName;

    /** The type. */
    private FolderItemDTO.Type type;

    /** The selected. */
    private boolean selected;

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
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

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
     * Gets the new name.
     *
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Sets the new name.
     *
     * @param newName the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public FolderItemDTO.Type getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the type to set
     */
    public void setType(FolderItemDTO.Type type) {
        this.type = type;
    }

    /**
     * Checks if is selected.
     *
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the selected.
     *
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
