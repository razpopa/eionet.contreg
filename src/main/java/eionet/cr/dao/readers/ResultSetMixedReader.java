package eionet.cr.dao.readers;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

import eionet.cr.util.sesame.SPARQLResultSetReader;
import eionet.cr.util.sql.SQLResultSetReader;

/**
 * 
 * @author jaanus
 * 
 */
public abstract class ResultSetMixedReader<T> implements SQLResultSetReader<T>, SPARQLResultSetReader<T> {

    /** */
    protected List<T> resultList = new ArrayList<T>();

    /** */
    protected List<String> bindingNames;

    /** */
    protected ResultSetMetaData resultSetMetaData;

    /** */
    protected String blankNodeUriPrefix;

    /*
     * (non-Javadoc)
     * 
     * @see eionet.cr.util.sql.SQLResultSetReader#startResultSet(java.sql.ResultSetMetaData)
     */
    @Override
    public void startResultSet(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    /**
     * 
     * @param bindingNames
     */
    public void startResultSet(List<String> bindingNames) {
        this.bindingNames = bindingNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.cr.dao.readers.ResultSetReader#endResultSet()
     */
    @Override
    public void endResultSet() {

        // default implementation, which does nothing, implementors can override
    }

    /*
     * (non-Javadoc)
     * 
     * @see eionet.cr.dao.readers.ResultSetReader#getResultList()
     */
    @Override
    public List<T> getResultList() {
        return resultList;
    }

    /**
     * @param blnakNodeUriPrefix the blnakNodeUriPrefix to set
     */
    public void setBlankNodeUriPrefix(String blankNodeUriPrefix) {
        this.blankNodeUriPrefix = blankNodeUriPrefix;
    }

    /**
     * @param bindingSet
     * @param bindingName
     * @return
     */
    protected String getStringValue(BindingSet bindingSet, String bindingName) {

        if (bindingSet == null || StringUtils.isBlank(bindingName)) {
            return null;
        } else {
            Value value = bindingSet.getValue(bindingName);
            return value == null ? null : value.stringValue();
        }
    }
}
