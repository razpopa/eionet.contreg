package eionet.cr.dao.readers;

import org.apache.commons.lang.StringUtils;
import org.openrdf.query.BindingSet;

import eionet.cr.dto.SkosItemDTO;
import eionet.cr.util.sesame.SPARQLResultSetBaseReader;

/**
 * A SPARQL result set reader for objects of type {@link SkosItemDTO}.
 *
 * @author jaanus
 */
public class SkosItemsReader extends SPARQLResultSetBaseReader<SkosItemDTO> {

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

        String uri = getStringValue(bindingSet, "uri");
        if (StringUtils.isNotBlank(uri)) {

            SkosItemDTO item = new SkosItemDTO(uri);
            item.setSkosNotation(getStringValue(bindingSet, "skosNotation"));
            item.setSkosPrefLabel(getStringValue(bindingSet, "skosPrefLabel"));
            resultList.add(item);
        }
    }
}
