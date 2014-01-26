package eionet.cr.dto;

import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * The download file DTO.
 *
 * @author Jaanus
 */
public class DownloadFileDTO {

    /** The content type. */
    private String contentType;

    /** The input stream. */
    private InputStream inputStream;

    /** The file found. */
    private boolean fileFound;

    /**
     * Gets the content type.
     *
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type.
     *
     * @param contentType the new content type
     */
    public void setContentType(String contentType) {
        fileFound = true;
        this.contentType = contentType;
    }

    /**
     * Gets the input stream.
     *
     * @return the input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Sets the input stream.
     *
     * @param inputStream the new input stream
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Checks if is file found.
     *
     * @return true, if is file found
     */
    public boolean isFileFound() {
        return fileFound;
    }

    /**
     * Sets the file found.
     *
     * @param fileFound the new file found
     */
    public void setFileFound(boolean fileFound) {
        this.fileFound = fileFound;
    }

}
