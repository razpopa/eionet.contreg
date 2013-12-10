package eionet.cr.util.odp;

/**
 *
 * Type definition ...
 *
 * @author Jaanus
 */
public enum ODPAction {

    ADD_DRAFT("Add/replace with status Draft"), ADD_PUBLISHED("Add/replace with status Published"), SET_DRAFT(
            "Set status to Draft"), SET_PUBLISHED("Set status to Published"), REMOVE("Remove");

    /** */
    String label;

    /**
     *
     * Class constructor.
     * @param label
     */
    ODPAction(String label) {
        this.label = label;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }
}
