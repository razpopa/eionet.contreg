package eionet.cr.web.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;

import eionet.cr.dao.readers.ResultSetMixedReader;
import eionet.cr.dao.readers.ResultSetReaderException;

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

        int i = 0;
        HTMLSelectOption option = new HTMLSelectOption();
        for (Binding binding : bindingSet) {
            i++;
            String bindingValue = binding.getValue() == null ? null : binding.getValue().stringValue();
            if (i == 1) {
                option.setValue(bindingValue);
            }
            else if (i == 2) {
                option.setLabel(bindingValue);
            }
            else if (i == 3) {
                option.setTitle(bindingValue);
            }
        }

        if (bindingSet.size() <= 2) {
            option.setTitle(option.getLabel());
        }

        resultList.add(option);
    }
}