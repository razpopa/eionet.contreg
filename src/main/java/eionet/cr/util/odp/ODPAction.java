package eionet.cr.util.odp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 *
 * Type definition ...
 *
 * @author Jaanus
 */
public enum ODPAction {

    // @formatter:off

    ADD_DRAFT("Add/replace with status Draft"),
    ADD_PUBLISHED("Add/replace with status Published"),
    SET_DRAFT("Set status to Draft"),
    SET_PUBLISHED("Set status to Published"),
    REMOVE("Remove");

    // @formatter:on

    /** */
    private static final char[] WORD_SEPARATOR = {'_'};

    /** */
    private String label;

    /** */
    private String nameCamelCase;

    /**
     *
     * Class constructor.
     *
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

    /**
     * Returns the enum's name in camel case, with lower-case first letter.
     *
     * @return The result.
     */
    public String getNameCamelCase() {

        if (nameCamelCase == null) {
            String fullyCapitalized = WordUtils.capitalizeFully(name(), WORD_SEPARATOR);
            String separatorsRemoved = StringUtils.remove(fullyCapitalized, WORD_SEPARATOR[0]);
            nameCamelCase = StringUtils.uncapitalize(separatorsRemoved);
        }
        return nameCamelCase;
    }
}
