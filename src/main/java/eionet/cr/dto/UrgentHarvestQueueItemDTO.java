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

import java.io.Serializable;

/**
 * A DTO class representing a row (i.e. an item) in the URGENT_HARVEST_QUEUE table.
 *
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public class UrgentHarvestQueueItemDTO implements Serializable {

    /** Generated serial version ID. */
    private static final long serialVersionUID = 4550170623025835930L;

    /** The item's ID. */
    private int itemId;

    /** The URL of the source to be urgently-harvest. */
    private String url;

    /** Time when this item was added */
    private java.util.Date timeAdded;

    /** String-reprsentation of the "pushed" content to be harvested. */
    private String pushedContent;

    /** The user who added the item. */
    private String userName;

    /**
     * @return the itemId
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the timeAdded
     */
    public java.util.Date getTimeAdded() {
        return timeAdded;
    }

    /**
     * @param timeAdded the timeAdded to set
     */
    public void setTimeAdded(java.util.Date timeAdded) {
        this.timeAdded = timeAdded;
    }

    /**
     * @return the pushedContent
     */
    public String getPushedContent() {
        return pushedContent;
    }

    /**
     * @param pushedContent the pushedContent to set
     */
    public void setPushedContent(String pushedContent) {
        this.pushedContent = pushedContent;
    }

    /**
     *
     * @return
     */
    public boolean isPushHarvest() {
        return pushedContent != null;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
