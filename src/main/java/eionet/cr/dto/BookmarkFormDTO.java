package eionet.cr.dto;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

// TODO: Auto-generated Javadoc
/**
 * The DTO for BookmarkForm.
 */
public class BookmarkFormDTO {

    /** The id. */
    private Long id;

    /** The source. */
    private String source;

    /** The title. */
    private String title;

    /** The description. */
    private String description;

    /** The tags. */
    private String tags;

    /** The methodology. */
    private String methodology;

    /**
     * Instantiates a new bookmark form dto.
     */
    public BookmarkFormDTO() {
    }

    /**
     * Instantiates a new bookmark form dto.
     *
     * @param id the id
     * @param source the source
     * @param title the title
     * @param description the description
     * @param tags the tags
     * @param methodology the methodology
     */
    public BookmarkFormDTO(Long id, String source, String title, String description, String tags, String methodology) {
        this.id = id;
        this.source = source;
        this.description = description;
        this.tags = tags;
        this.title = title;
        this.methodology = methodology;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
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
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the tags.
     *
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * Sets the tags.
     *
     * @param tags the tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * Gets the methodology.
     *
     * @return the methodology
     */
    public String getMethodology() {
        return methodology;
    }

    /**
     * Sets the methodology.
     *
     * @param methodology the methodology to set
     */
    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }
}
