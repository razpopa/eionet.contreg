package eionet.cr.util.odp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Utility class for getting Open Data Portal country mappings for any given Scoreboard reference area.
 *
 * @author Jaanus
 */
public class ODPCountryMappings {

    /** Static logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(ODPCountryMappings.class);

    /** The name of the mappings file. */
    private static final String MAPPINGS_FILENAME = "odp-country-mappings.xml";

    /** The loaded mappings, a final constant. */
    private static final Properties MAPPINGS = createMappings();

    /**
     * Returns ODP country mapping for the given Scoreboard reference area.
     *
     * @param referenceArea The given reference area.
     * @return Corresponding ODP country mapping or null if no such exists.
     */
    public static synchronized String getMappingFor(String referenceArea) {
        return referenceArea == null ? null : MAPPINGS.getProperty(referenceArea);
    }

    /**
     * Creates and returns {@link Properties} created from {@link #MAPPINGS_FILENAME}.
     *
     * @return The mapping properties.
     * @throws IOException If error reading the file.
     */
    private static Properties createMappings() {

        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = ODPCountryMappings.class.getClassLoader().getResourceAsStream(ODPCountryMappings.MAPPINGS_FILENAME);
            properties.loadFromXML(inputStream);
        } catch (IOException e) {
            LOGGER.error("Failed reading ODP country mappings from " + ODPCountryMappings.MAPPINGS_FILENAME, e);
        }

        return properties;
    }
}
