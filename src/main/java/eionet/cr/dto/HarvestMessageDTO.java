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

import eionet.cr.harvest.util.HarvestMessageType;

// TODO: Auto-generated Javadoc
/**
 * A DTO representing a harvest message.
 *
 * @author heinljab
 *
 */
public class HarvestMessageDTO implements java.io.Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5769350408372163478L;

    /** The harvest id. */
    Integer harvestId = null;

    /** The type. */
    String type = null;

    /** The message. */
    String message = null;

    /** The stack trace. */
    String stackTrace = null;

    /** The harvest message id. */
    Integer harvestMessageId = null;

    /**
     * Instantiates a new harvest message dto.
     */
    public HarvestMessageDTO() {
    }

    /**
     * Gets the harvest id.
     *
     * @return the harvestId
     */
    public Integer getHarvestId() {
        return harvestId;
    }

    /**
     * Sets the harvest id.
     *
     * @param harvestId the harvestId to set
     */
    public void setHarvestId(Integer harvestId) {
        this.harvestId = harvestId;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the stack trace.
     *
     * @return the stackTrace
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the stack trace.
     *
     * @param stackTrace the stackTrace to set
     */
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    /**
     * Gets the harvest message id.
     *
     * @return the harvest message id
     */
    public Integer getHarvestMessageId() {
        return harvestMessageId;
    }

    /**
     * Sets the harvest message id.
     *
     * @param harvestMessageId the new harvest message id
     */
    public void setHarvestMessageId(Integer harvestMessageId) {
        this.harvestMessageId = harvestMessageId;
    }

    /**
     * Creates the DTO with give parameters.
     *
     * @param message the message
     * @param messageType the message type
     * @param stackTrace the stack trace
     * @return the harvest message dto
     */
    public static HarvestMessageDTO create(String message, HarvestMessageType messageType, String stackTrace) {

        HarvestMessageDTO dto = new HarvestMessageDTO();
        dto.setMessage(message);
        dto.setType(messageType == null ? null : messageType.toString());
        dto.setStackTrace(stackTrace);
        return dto;
    }
}
