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
 * A file DTO.
 *
 * @author altnyris
 */
public class FileDTO implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The title. */
    private String title;

    /** The uri. */
    private String uri;

    /** The triples cnt. */
    private int triplesCnt;

    /**
     * Instantiates a new file dto.
     */
    public FileDTO() {
    }

    /**
     * Instantiates a new file dto.
     *
     * @param uri the uri
     */
    public FileDTO(String uri) {
        this.uri = uri;
        title = URIUtil.extractURILabel(uri, SubjectDTO.NO_LABEL);
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileDTO other = (FileDTO) obj;
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the triples cnt.
     *
     * @return the triples cnt
     */
    public int getTriplesCnt() {
        return triplesCnt;
    }

    /**
     * Sets the triples cnt.
     *
     * @param triplesCnt the new triples cnt
     */
    public void setTriplesCnt(int triplesCnt) {
        this.triplesCnt = triplesCnt;
    }
}
