package eionet.cr.dao.readers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
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

    /** */
    private static final String CODELIST_URI_PREFIX = "http://semantic.digital-agenda-data.eu/codelist/";

    /** The spreadsheet template reference. */
    private File spreadsheetTemplate;

    /** The properties to spreadsheet columns mapping */
    private Map<String, Integer> propsToSpreadsheetCols;

    /** Number of codelist items exported. */
    private int itemsExported;

    /**
     * Represents current codelist item row to be written into spreadsheet.
     * Keys are spreadsheet columns (0-based index), values are strings to be written into those columns.
     * To be initialized at first need.
     */
    private HashMap<Integer, String> rowMap;

    /** The current subject URI as the SPARQL result set is traversed. To be initialized at first need. */
    private String currentSubjectUri;

    /**
     * Construct new instance with the given spreadsheet template reference and the properties to spreadsheet columns mapping.
     *
     * @param spreadsheetTemplate The spreadsheet template reference.
     * @param propsToSpreadsheetCols The properties to spreadsheet columns mapping.
     */
    public CodelistExporter(File spreadsheetTemplate, Map<String, Integer> propsToSpreadsheetCols) {

        if (spreadsheetTemplate == null || !spreadsheetTemplate.exists() || !spreadsheetTemplate.isFile()) {
            throw new IllegalArgumentException("The given spreadsheet template must not be null and the file must exist!");
        }

        if (propsToSpreadsheetCols == null || propsToSpreadsheetCols.isEmpty()) {
            throw new IllegalArgumentException("The given properties to spreadsheet columns mapping must not be null or empty!");
        }

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

        if (bindingSet == null || bindingSet.size() == 0) {
            return;
        }

        String subjectUri = getStringValue(bindingSet, "s");
        if (subjectUri == null) {
            return;
        }

        if (!subjectUri.equals(currentSubjectUri)) {
            if (rowMap != null && !rowMap.isEmpty()) {
                saveRowMap();
            }
            currentSubjectUri = subjectUri;
        }

        String predicateUri = getStringValue(bindingSet, "p");
        Integer columnIndex = propsToSpreadsheetCols.get(predicateUri);
        if (columnIndex == null || columnIndex.intValue() < 0) {
            return;
        }

        Value value = bindingSet.getValue("o");
        if (value != null) {
            putIntoRowMap(columnIndex, value);
        }
    }

    /**
     * Puts the given column-index-to-value pair into the {@link #rowMap}. The latter is initialized if null.
     *
     * @param columnIndex
     * @param value
     */
    private void putIntoRowMap(Integer columnIndex, Value value) {

        if (rowMap == null) {
            rowMap = new HashMap<Integer, String>();
        }

        if (value instanceof Literal) {
            rowMap.put(columnIndex, value.stringValue());
        }
        else if ((value instanceof BNode) == false) {

            String strValue = value.stringValue();
            if (strValue.startsWith(CODELIST_URI_PREFIX)) {

                strValue = StringUtils.substringAfterLast(strValue.replace('/', '#'), "#");
                rowMap.put(columnIndex, strValue);
            }
        }
    }

    /**
     * Saves the row map to the row indicated by the number of exported items.
     */
    private void saveRowMap() {
        // TODO implement this method
    }

    /**
     * To be called after last codelist item has been exported. The purpose of the method is to properly save the spreadsheet
     * template file and close all resources.
     */
    private void saveAndClose() {
        // TODO implement this method
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.util.sesame.SPARQLResultSetBaseReader#endResultSet()
     */
    @Override
    public void endResultSet() {

        if (rowMap != null && !rowMap.isEmpty()) {
            saveRowMap();
        }
        saveAndClose();
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
