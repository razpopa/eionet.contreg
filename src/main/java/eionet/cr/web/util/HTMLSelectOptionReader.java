package eionet.cr.web.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.openrdf.query.BindingSet;

import eionet.cr.dao.readers.ResultSetMixedReader;
import eionet.cr.dao.readers.ResultSetReaderException;
import eionet.cr.util.URIUtil;

/**
 * A query result set reader for {@link HTMLSelectOption}.
 *
 * @author jaanus
 */
public class HTMLSelectOptionReader extends ResultSetMixedReader<HTMLSelectOption> {

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.util.sql.SQLResultSetReader#readRow(java.sql.ResultSet)
     */
    @Override
    public void readRow(ResultSet rs) throws SQLException, ResultSetReaderException {
        throw new UnsupportedOperationException();
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

        String value = getStringValue(bindingSet, "object");
        String label = getStringValue(bindingSet, "label");
        if (StringUtils.isBlank(label)) {
            label = URIUtil.extractURILabel(value, value);
        }

        String title = getStringValue(bindingSet, "title");
        if (StringUtils.isBlank(title)) {
            title = label;
        }

        HTMLSelectOption option = new HTMLSelectOption();
        option.setValue(value);
        option.setTitle(title);
        option.setLabel(label);

        resultList.add(option);
    }
}