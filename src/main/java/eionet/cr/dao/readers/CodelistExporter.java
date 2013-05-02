package eionet.cr.dao.readers;

import java.io.File;
import java.util.Map;

import org.openrdf.query.BindingSet;

import eionet.cr.util.sesame.SPARQLResultSetBaseReader;

/**
 * An extension of {@link SPARQLResultSetBaseReader} that exports Scoreboard codelist items into a given spreadsheet template file.
 * The mapping of codelist item properties to corresponding spreadsheet columns is also a pre-requisite.
 *
 * @author Jaanus Heinlaid
 *
 */
@SuppressWarnings("rawtypes")
public class CodelistExporter extends SPARQLResultSetBaseReader {

    /** The spreadsheet template reference. */
    private File spreadsheetTemplate;

    /** The properties to spreadsheet columns mapping */
    private Map<String, Integer> propsToSpreadsheetCols;

    /** Number of codelist items exported. */
    private int itemsExported;

    /**
     * Construct new instance with the given spreadsheet template reference and the properties to spreadsheet columns mapping.
     *
     * @param spreadsheetTemplate The spreadsheet template reference.
     * @param propsToSpreadsheetCols The properties to spreadsheet columns mapping.
     */
    public CodelistExporter(File spreadsheetTemplate, Map<String, Integer> propsToSpreadsheetCols) {

        this.spreadsheetTemplate = spreadsheetTemplate;
        this.propsToSpreadsheetCols = propsToSpreadsheetCols;
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.util.sesame.SPARQLResultSetReader#readRow(org.openrdf.query.BindingSet)
     */
    @Override
    public void readRow(BindingSet bindingSet) throws ResultSetReaderException {
    }

    /**
     * To be called after last codelist item has been exported. The purpose of the method is to properly save the spreadsheet
     * template file and close all resources.
     */
    private void saveAndClose() {
        ;
    }

    /*
     * (non-Javadoc)
     * @see eionet.cr.util.sesame.SPARQLResultSetBaseReader#endResultSet()
     */
    @Override
    public void endResultSet() {
        ;
    }

    /**
     * Returns the number of codelist items exported by this exporter at the time this method is called.
     *
     * @return As described.
     */
    public int getItemsExported() {
        return itemsExported;
    }

}
