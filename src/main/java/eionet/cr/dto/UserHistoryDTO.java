package eionet.cr.dto;

// TODO: Auto-generated Javadoc
/**
 * A DTO for representing rows of a user history list-
 *
 * @author Jaanus
 */
public class UserHistoryDTO {

    /** The url. */
    String url;

    /** The last operation time. */
    String lastOperationTime;

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the last operation.
     *
     * @return the last operation
     */
    public String getLastOperation() {
        return lastOperationTime;
    }

    /**
     * Sets the last operation.
     *
     * @param lastOperation the new last operation
     */
    public void setLastOperation(String lastOperation) {
        this.lastOperationTime = lastOperation;
    }

}
