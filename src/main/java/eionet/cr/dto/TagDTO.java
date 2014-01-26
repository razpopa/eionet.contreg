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
 * Agency.  Portions created by Tieto Estonia are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */
package eionet.cr.dto;

import java.io.Serializable;
import java.util.Comparator;

// TODO: Auto-generated Javadoc
/**
 * A DTO representing tags for the CR's tag cloud.
 *
 * @author Jaanus
 */
public class TagDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The tag. */
    private String tag;

    /** The count. */
    private int count;

    /** The scale. */
    private int scale;

    /**
     * Instantiates a new tag dto.
     *
     * @param tag the tag
     * @param count the count
     * @param maxTagCount the max tag count
     */
    public TagDTO(String tag, int count, int maxTagCount) {

        this.tag = tag;
        this.count = count;

        if (maxTagCount <= 0) {
            scale = 0;
        } else {
            setScale((count * 5) / maxTagCount);
        }
    }

    /**
     * Gets the tag.
     *
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the scale.
     *
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     *
     * @param scale the new scale
     */
    public void setScale(int scale) {
        if (scale < 0) {
            scale = 0;
        }
        if (scale > 4) {
            scale = 4;
        }

        this.scale = scale;
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
        TagDTO other = (TagDTO) obj;
        if (count != other.count) {
            return false;
        }
        if (scale != other.scale) {
            return false;
        }
        if (tag == null) {
            if (other.tag != null) {
                return false;
            }
        } else if (!tag.equals(other.tag)) {
            return false;
        }
        return true;
    }

    /**
     * Compares two tags by name in ascending order.
     */
    public static class NameComparatorAsc implements Comparator<TagDTO> {

        /*
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(TagDTO tag1, TagDTO tag2) {
            return tag1.getTag().compareToIgnoreCase(tag2.getTag());
        }
    }

    /**
     * Compares two tags by count in ascending order.
     */
    public static class CountComparatorDesc implements Comparator<TagDTO> {

        /*
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(TagDTO tag1, TagDTO tag2) {
            int countComparison = tag2.getCount() - tag1.getCount();

            // if the score is the same sort by name
            if (countComparison == 0) {
                return (new NameComparatorAsc()).compare(tag1, tag2);
            } else {
                return countComparison;
            }
        }
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
        result = prime * result + count;
        result = prime * result + scale;
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        return result;
    }
}
