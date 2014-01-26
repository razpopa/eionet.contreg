package eionet.cr.common;

/**
 *
 * @author jaanus
 */
public class ConfigurationException extends CRRuntimeException {

    /**
     */
    public ConfigurationException() {
        super();
    }

    /**
     * @param s
     */
    public ConfigurationException(String s) {
        super(s);
    }

    /**
     * @param s
     * @param throwable
     */
    public ConfigurationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * @param throwable
     */
    public ConfigurationException(Throwable throwable) {
        super(throwable);
    }
}
