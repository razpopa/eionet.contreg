package eionet.cr.dao.readers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

import eionet.cr.dao.DAOException;
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
    private static final Logger LOGGER = Logger.getLogger(CodelistExporter.class);

    /** */
    private static final String CODELIST_URI_PREFIX = "http://semantic.digital-agenda-data.eu/codelist/";

    /** */
    private static final String DAD_PROPERTY_NAMESPACE = "http://semantic.digital-agenda-data.eu/def/property/";

    /** */
    private static final Map<String, String> SPECIAL_BINDINGS_MAP = createSpecialBindingsMap();

    /** The spreadsheet template reference. */
    private File template;

    /** The properties to spreadsheet columns mapping */
    private Map<String, Integer> mappings;

    /** The target spreadsheet file where the exported workbook will be saved to. */
    private File target;

    /** Number of codelist items exported. */
    private int itemsExported;

    /** The total number of worksheet rows added by this exporter at any moment of execution. */
    private int worksheetRowsAdded;

    /**
     * Represents current codelist item to be written into spreadsheet.
     * Keys are spreadsheet columns (0-based index), values are sets of strings to be written into those columns.
     * So the idea is that a column may have multiple values. For every such value the worksheet row will simply be duplicated
     * by repeating the values of the other columns.
     * This fields is to be initialized at first need.
     */
    private HashMap<Integer, Set<String>> rowMap;

    /** The current subject URI as the SPARQL result set is traversed. To be initialized at first need. */
    private String currentSubjectUri;

    /** The workbook object that represents the given spreadsheet template file. */
    private Workbook workbook;

    /** The current worksheet in the workbook. */
    private Sheet worksheet;

    private int resultSetRowCounter;

    /**
     * Construct new instance with the given spreadsheet template reference and the properties to spreadsheet columns mapping.
     *
     * @param template The spreadsheet template reference.
     * @param mappings The properties to spreadsheet columns mapping.
     * @param target The target spreadsheet file where the exported workbook will be saved to.
     * @throws DAOException
     */
    public CodelistExporter(File template, Map<String, Integer> mappings, File target) throws DAOException {

        if (template == null || !template.exists() || !template.isFile()) {
            throw new IllegalArgumentException("The given spreadsheet template must not be null and the file must exist!");
        }

        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("The given properties to spreadsheet columns mapping must not be null or empty!");
        }

        if (target == null || !target.getParentFile().exists()) {
            throw new IllegalArgumentException("The given spreadsheet target file must not be null and its path must exist!");
        }

        try {
            workbook = WorkbookFactory.create(template);
        } catch (InvalidFormatException e) {
            throw new DAOException("Failed to recognize workbook at " + template, e);
        } catch (IOException e) {
            throw new DAOException("IOException when trying to create workbook object from " + template, e);
        }

        worksheet = workbook.getSheetAt(0);
        if (worksheet == null) {
            worksheet = workbook.createSheet();
        }
        if (worksheet == null) {
            throw new DAOException("Failed to get or create the workbook's first sheet: simply got null as the result");
        }

        this.template = template;
        this.mappings = mappings;
        this.target = target;
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
                itemsExported++;
            }
            currentSubjectUri = subjectUri;
            rowMap = new HashMap<Integer, Set<String>>();
        }

        String predicateUri = getStringValue(bindingSet, "p");
        Integer columnIndex = mappings.get(predicateUri);
        if (columnIndex == null || columnIndex.intValue() < 0) {
            return;
        }

        Value value = bindingSet.getValue("o");
        if (value != null) {
            putIntoRowMap(columnIndex, value);
        }

        for (Entry<String, String> entry : SPECIAL_BINDINGS_MAP.entrySet()) {

            String bindingName = entry.getKey();
            String bindingPredicate = entry.getValue();

            value = bindingSet.getValue(bindingName);
            if (value != null) {
                columnIndex = mappings.get(bindingPredicate);
                if (columnIndex != null && columnIndex.intValue() >= 0) {
                    putIntoRowMap(columnIndex, value);
                }
            }
        }
    }

    /**
     * Puts the given column-index-to-value pair into the {@link #rowMap}. The latter is initialized if null.
     *
     * @param columnIndex
     * @param value
     */
    private void putIntoRowMap(Integer columnIndex, Value value) {

        if (value instanceof Literal) {
            putIntoRowMap(rowMap, columnIndex, value.stringValue());
        } else if (!(value instanceof BNode)) {

            String strValue = value.stringValue();
            if (strValue.startsWith(CODELIST_URI_PREFIX)) {

                strValue = StringUtils.substringAfterLast(strValue.replace('/', '#'), "#");
                putIntoRowMap(rowMap, columnIndex, strValue);
            } else {
                putIntoRowMap(rowMap, columnIndex, value.stringValue());
            }
        }
    }

    /**
     * Puts the given column-index-to-string-value pair into the given row-map.{@link #rowMap}.
     *
     * @param map The given row-map.
     * @param columnIndex Column index, i.e. the key of the map entry.
     * @param value Column value, i.e. the value of the map entry.
     */
    private static void putIntoRowMap(HashMap<Integer, Set<String>> map, Integer columnIndex, String value) {

        Set<String> set = map.get(columnIndex);
        if (set == null) {
            set = new HashSet<String>();
            map.put(columnIndex, set);
        }
        set.add(value);
    }

    /**
     * Saves the contents of the current row-map into the target worksheet.
     */
    private void saveRowMap() {

        if (rowMap == null || rowMap.isEmpty()) {
            return;
        }

        LOGGER.trace("Saving item #" + itemsExported);

        // The row-map represents the current codelist item to be written into the target rowsheet.
        // Each key-value pair in the row-map represents worksheet column index and corresponding values.
        // Yes, the column can have multiple values. We handle this by repeating worksheet row for every such row.
        // Imagine map like this: {1=["james"], 2="bond" 3=["tall", "handsome"]}
        // In the worksheet the outcome must be 2 "distinct rows", like this (columns ordered by column index starting from 1):
        // "james", "bond", "tall"
        // "james", "bond", "handsome".

        // Convert the map to the set of distinct rows, following above-described principle.
        HashSet<ArrayList<String>> distinctRows = mapToDistinctRows(rowMap);

        // Loop over distinct rows, save each row into worksheet.
        for (Iterator<ArrayList<String>> iter = distinctRows.iterator(); iter.hasNext();) {
            ArrayList<String> distinctRow = iter.next();
            if (distinctRow != null && !distinctRow.isEmpty()) {
                saveDistinctRow(distinctRow);
                worksheetRowsAdded++;
            }
        }
    }

    /**
     * Saves a "distinct row" into the target worksheet.
     * See inside {@link #saveRowMap()} for more comments on what's a "distinct row".
     *
     * @param distinctRow The "distinct row" to save.
     */
    private void saveDistinctRow(ArrayList<String> distinctRow) {

        if (distinctRow == null || distinctRow.isEmpty()) {
            return;
        }

        int rowIndex = worksheetRowsAdded + 1;
        Row row = worksheet.getRow(rowIndex);
        if (row == null) {
            row = worksheet.createRow(rowIndex);
        }

        LOGGER.trace("Populating worksheet row at position " + worksheetRowsAdded);

        int maxLines = 1;
        for (int i = 0; i < distinctRow.size(); i++) {

            int cellIndex = i;
            String cellValue = distinctRow.get(i);

            Cell cell = row.getCell(cellIndex);
            if (cell == null) {
                cell = row.createCell(cellIndex);
            }
            cell.setCellValue(cellValue);
            CellStyle cellStyle = cell.getCellStyle();
            if (cellStyle == null) {
                cellStyle = workbook.createCellStyle();
                cell.setCellStyle(cellStyle);
            }
            cellStyle.setWrapText(true);

            String[] split = StringUtils.split(cellValue.replace("\r\n", "\n"), "\n");
            maxLines = Math.max(maxLines, split.length);
        }

        if (maxLines > 1) {
            maxLines = Math.min(maxLines, 30);
            float defaultRowHeightInPoints = worksheet.getDefaultRowHeightInPoints();
            row.setHeightInPoints(maxLines * defaultRowHeightInPoints);
        }
    }

    /**
     * To be called after last codelist item has been exported. The purpose of the method is to properly save the spreadsheet
     * template file and close all resources.
     *
     * @throws DAOException
     */
    public void saveAndClose() throws DAOException {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
            worksheet.showInPane((short) 1, (short) 0);
            workbook.write(fos);
        } catch (IOException e) {
            throw new DAOException("Error when saving workbook to " + target, e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
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
            itemsExported++;
        }
    }

    /**
     * Returns the number of codelist items exported by this exporter at the time this method is called.
     *
     * @return As described.
     */
    public int getItemsExported() {
        return itemsExported;
    }

    /**
     *
     * @return
     */
    private static Map<String, String> createSpecialBindingsMap() {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("memberOf", DAD_PROPERTY_NAMESPACE + "member-of");
        map.put("order", DAD_PROPERTY_NAMESPACE + "order");
        return map;
    }

    /**
     *
     * @param map
     * @return
     */
    private static HashSet<ArrayList<String>> mapToDistinctRows(HashMap<Integer, Set<String>> map) {

        int maxIndex = Collections.max(map.keySet()).intValue();
        ArrayList<String> defaultRow = new ArrayList<String>();
        for (int i = 0; i <= maxIndex; i++) {
            defaultRow.add(StringUtils.EMPTY);
        }

        for (Entry<Integer, Set<String>> entry : map.entrySet()) {
            int index = entry.getKey();
            Set<String> values = entry.getValue();
            defaultRow.set(index, values.iterator().next());
        }

        HashSet<ArrayList<String>> distinctRows = new HashSet<ArrayList<String>>();
        distinctRows.add(defaultRow);

        Set<Entry<Integer, Set<String>>> entrySet = map.entrySet();
        for (Entry<Integer, Set<String>> entry : entrySet) {

            int index = entry.getKey();
            Set<String> values = entry.getValue();
            if (values.size() > 1) {

                Iterator<String> iter = values.iterator();
                for (iter.next(); iter.hasNext();) {
                    ArrayList<String> row = (ArrayList<String>) defaultRow.clone();
                    row.set(index, iter.next());
                    distinctRows.add(row);
                }
            }
        }
        return distinctRows;
    }
}
