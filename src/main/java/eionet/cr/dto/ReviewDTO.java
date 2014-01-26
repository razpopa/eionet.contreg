package eionet.cr.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

// TODO: Auto-generated Javadoc
/**
 * A DTO for representing reviews of a folder item.
 *
 * @author <a href="mailto:jaak.kapten@tieto.com">Jaak Kapten</a>
 */

public class ReviewDTO implements Serializable {

    /** The review subject uri. */
    private String reviewSubjectUri;

    /** The title. */
    private String title;

    /** The object url. */
    private String objectUrl;

    /** The review content. */
    private String reviewContent;

    /** The review id. */
    private int reviewID;

    /** The attachments. */
    private List<String> attachments;

    /** The review content type. */
    private String reviewContentType;

    /**
     * Gets the review subject uri.
     *
     * @return the review subject uri
     */
    public String getReviewSubjectUri() {
        return reviewSubjectUri;
    }

    /**
     * Sets the review subject uri.
     *
     * @param reviewSubjectUri the new review subject uri
     */
    public void setReviewSubjectUri(String reviewSubjectUri) {

        int id = -1;
        int i = reviewSubjectUri.lastIndexOf('/');
        if (i != -1) {
            try {
                id = Integer.parseInt(reviewSubjectUri.substring(i + 1));
            } catch (IndexOutOfBoundsException e) {
                // All errors resulting from malformed URi will be thrown below.
            } catch (NumberFormatException e) {
                // All errors resulting from malformed URi will be thrown below.
            }
        }

        if (id == -1) {
            throw new IllegalArgumentException("Malformed review URI: " + reviewSubjectUri);
        } else {
            this.reviewSubjectUri = reviewSubjectUri;
            this.reviewID = id;
        }
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the object url.
     *
     * @return the object url
     */
    public String getObjectUrl() {
        return objectUrl;
    }

    /**
     * Gets the object url html.
     *
     * @return the object url html
     */
    public String getObjectUrlHTML() {
        return StringEscapeUtils.escapeHtml(objectUrl);
    }

    /**
     * Sets the object url.
     *
     * @param objectUrl the new object url
     */
    public void setObjectUrl(String objectUrl) {
        this.objectUrl = objectUrl;
    }

    /**
     * Gets the review content.
     *
     * @return the review content
     */
    public String getReviewContent() {
        return reviewContent;
    }

    /**
     * Sets the review content.
     *
     * @param reviewContent the new review content
     */
    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    /**
     * Gets the review subject html formatted.
     *
     * @return the review subject html formatted
     */
    public String getReviewSubjectHtmlFormatted() {
        return StringEscapeUtils.escapeHtml(reviewSubjectUri);
    }

    /**
     * Gets the review id.
     *
     * @return the review id
     */
    public int getReviewID() {
        return reviewID;
    }

    /**
     * Gets the attachments.
     *
     * @return the attachments
     */
    public List<String> getAttachments() {
        return attachments;
    }

    /**
     * Sets the attachments.
     *
     * @param attachments the new attachments
     */
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    /**
     * Gets the review content type.
     *
     * @return the review content type
     */
    public String getReviewContentType() {
        return reviewContentType;
    }

    /**
     * Sets the review content type.
     *
     * @param reviewContentType the new review content type
     */
    public void setReviewContentType(String reviewContentType) {
        this.reviewContentType = reviewContentType;
    }

}
