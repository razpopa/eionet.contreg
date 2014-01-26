package eionet.cr.web.util;

/**
 * Helper bean for representing options of an HTML <select>.
 * 
 * @author jaanus
 */
public class HTMLSelectOption {

    /** */
    private String value;
    private String label;
    private String title;

    /**
     * Simple constructor.
     */
    public HTMLSelectOption() {
        // Simple constructor.
    }

    /**
     * @param value
     * @param label
     */
    public HTMLSelectOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * @param value
     * @param label
     * @param title
     */
    public HTMLSelectOption(String value, String label, String title) {
        this.value = value;
        this.label = label;
        this.title = title;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
