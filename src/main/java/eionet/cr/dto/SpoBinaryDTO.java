/*
 * The contents of this file are subject to the Mozilla Public
 *
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
 * Agency. Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency. All Rights Reserved.
 *
 * Contributor(s):
 * Jaanus Heinlaid, Tieto Eesti*/
package eionet.cr.dto;

// TODO: Auto-generated Javadoc
/**
 * A DTO for representing binary objects of triples.
 *
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public class SpoBinaryDTO {

    /** The subject hash. */
    private long subjectHash;

    /** The content type. */
    private String contentType;

    /** The language. */
    private String language;

    /** The must embed. */
    private boolean mustEmbed;

    /**
     * Instantiates a new spo binary dto.
     *
     * @param subjectHash the subject hash
     */
    public SpoBinaryDTO(long subjectHash) {

        this.subjectHash = subjectHash;
    }

    /**
     * Gets the content type.
     *
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type.
     *
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Checks if is must embed.
     *
     * @return the mustEmbed
     */
    public boolean isMustEmbed() {
        return mustEmbed;
    }

    /**
     * Sets the must embed.
     *
     * @param mustEmbed the mustEmbed to set
     */
    public void setMustEmbed(boolean mustEmbed) {
        this.mustEmbed = mustEmbed;
    }

    /**
     * Gets the subject hash.
     *
     * @return the subjectHash
     */
    public long getSubjectHash() {
        return subjectHash;
    }
}
