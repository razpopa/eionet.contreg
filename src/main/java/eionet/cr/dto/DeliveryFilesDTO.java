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

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class DeliveryFilesDTO.
 *
 * @author altnyris
 */
public class DeliveryFilesDTO extends HarvestBaseDTO implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The files. */
    private List<FileDTO> files;

    /** The uri. */
    private String uri;

    /** The title. */
    private String title;

    /**
     * Instantiates a new delivery files dto.
     */
    public DeliveryFilesDTO() {
    }

    /**
     * Instantiates a new delivery files dto.
     *
     * @param uri the uri
     */
    public DeliveryFilesDTO(String uri) {
        this.uri = uri;
        files = new ArrayList<FileDTO>();
    }

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
     * @param uri the new uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Gets the files.
     *
     * @return the files
     */
    public List<FileDTO> getFiles() {
        return files;
    }

    /**
     * Sets the files.
     *
     * @param files the new files
     */
    public void setFiles(List<FileDTO> files) {
        this.files = files;
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

}
