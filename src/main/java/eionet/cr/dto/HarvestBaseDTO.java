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
 * Base DTO for harvests.
 *
 * @author heinljab
 *
 */
public class HarvestBaseDTO {

    /** The has fatals. */
    private Boolean hasFatals;

    /** The has errors. */
    private Boolean hasErrors;

    /** The has warnings. */
    private Boolean hasWarnings;

    /**
     * Gets the checks for fatals.
     *
     * @return the checks for fatals
     */
    public Boolean getHasFatals() {
        return hasFatals;
    }

    /**
     * Sets the checks for fatals.
     *
     * @param hasFatals the new checks for fatals
     */
    public void setHasFatals(Boolean hasFatals) {
        this.hasFatals = hasFatals;
    }

    /**
     * Gets the checks for errors.
     *
     * @return the checks for errors
     */
    public Boolean getHasErrors() {
        return hasErrors;
    }

    /**
     * Sets the checks for errors.
     *
     * @param hasErrors the new checks for errors
     */
    public void setHasErrors(Boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    /**
     * Gets the checks for warnings.
     *
     * @return the checks for warnings
     */
    public Boolean getHasWarnings() {
        return hasWarnings;
    }

    /**
     * Sets the checks for warnings.
     *
     * @param hasWarnings the new checks for warnings
     */
    public void setHasWarnings(Boolean hasWarnings) {
        this.hasWarnings = hasWarnings;
    }

    /**
     * Adds the message type.
     *
     * @param dto the dto
     * @param messageType the message type
     */
    public static final void addMessageType(HarvestBaseDTO dto, String messageType) {

        if (dto != null && messageType != null) {
            if (messageType.equals(HarvestMessageType.FATAL.toString())) {
                dto.setHasFatals(Boolean.TRUE);
            } else if (messageType.equals(HarvestMessageType.ERROR.toString())) {
                dto.setHasErrors(Boolean.TRUE);
            } else if (messageType.equals(HarvestMessageType.WARNING.toString())) {
                dto.setHasWarnings(Boolean.TRUE);
            }
        }
    }
}
