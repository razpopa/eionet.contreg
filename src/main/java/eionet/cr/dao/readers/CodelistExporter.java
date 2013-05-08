package eionet.cr.dao.readers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    /**
     * Represents current codelist item row to be written into spreadsheet.
     * Keys are spreadsheet columns (0-based index), values are strings to be written into those columns.
     * To be initialized at first need.
     */
    private HashMap<Integer, String> rowMap;

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
            rowMap = new HashMap<Integer, String>();
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
            rowMap.put(columnIndex, value.stringValue());
        } else if (!(value instanceof BNode)) {

            String strValue = value.stringValue();
            if (strValue.startsWith(CODELIST_URI_PREFIX)) {

                strValue = StringUtils.substringAfterLast(strValue.replace('/', '#'), "#");
                rowMap.put(columnIndex, strValue);
            } else {
                rowMap.put(columnIndex, value.stringValue());
            }
        }
    }

    /**
     * Saves the row map to the row indicated by the number of exported items.
     */
    private void saveRowMap() {

        if (rowMap == null || rowMap.isEmpty()) {
            return;
        }

        int rowIndex = itemsExported + 1;
        LOGGER.trace("Saving row #" + rowIndex);

        Row row = worksheet.getRow(rowIndex);
        if (row == null) {
            row = worksheet.createRow(rowIndex);
        }

        int maxLines = 1;
        for (Entry<Integer, String> entry : rowMap.entrySet()) {

            int cellIndex = entry.getKey();
            String cellValue = entry.getValue();
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
    private static final Map<String, String> createSpecialBindingsMap() {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("memberOf", DAD_PROPERTY_NAMESPACE + "member-of");
        map.put("order", DAD_PROPERTY_NAMESPACE + "order");
        return map;
    }
}