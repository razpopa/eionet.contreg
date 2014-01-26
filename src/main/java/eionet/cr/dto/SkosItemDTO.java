package eionet.cr.dto;

import org.apache.commons.lang.StringUtils;

/**
 * A generic DTO for representing resources that can have a skos:notation, skos:prefLabel, etc.
 * This could be an object whose rdf:type is skos:Concept or skos:ConceptScheme, for exmaple.
 * 
 * @author jaanus
 */
public class SkosItemDTO {

    /** The item's URI. */
    private String uri;

    /** The item's skos:notation. */
    private String skosNotation;

    /** The item's skos:prefLabel. */
    private String skosPrefLabel;

    /**
     * Simple constructor for the given SKOS item URI.
     * 
     * @param uri The URI.
     */
    public SkosItemDTO(String uri) {

        if (StringUtils.isBlank(uri)) {
            throw new IllegalArgumentException("The URI must not be blank!");
        }
        this.uri = uri;
    }

    /**
     * @return the skosNotation
     */
    public String getSkosNotation() {
        return skosNotation;
    }

    /**
     * @param skosNotation the skosNotation to set
     */
    public void setSkosNotation(String skosNotation) {
        this.skosNotation = skosNotation;
    }

    /**
     * @return the skosPrefLabel
     */
    public String getSkosPrefLabel() {
        return skosPrefLabel;
    }

    /**
     * @param skosPrefLabel the skosPrefLabel to set
     */
    public void setSkosPrefLabel(String skosPrefLabel) {
        this.skosPrefLabel = skosPrefLabel;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return skosNotation;
    }
}
