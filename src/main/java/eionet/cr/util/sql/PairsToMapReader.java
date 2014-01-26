package eionet.cr.util.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openrdf.query.BindingSet;

import eionet.cr.dao.readers.ResultSetReaderException;

/**
 * @author jaanus
 * @param <L>
 * @param <R>
 */
public class PairsToMapReader<L, R> extends PairReader<L, R> {

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.util.sql.PairReader#readRow(java.sql.ResultSet)
     */
    @Override
    public void readRow(ResultSet rs) throws SQLException, ResultSetReaderException {

        super.readRow(rs);
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.util.sql.PairReader#readRow(org.openrdf.query.BindingSet)
     */
    @Override
    public void readRow(BindingSet bindingSet) {
    }
}
