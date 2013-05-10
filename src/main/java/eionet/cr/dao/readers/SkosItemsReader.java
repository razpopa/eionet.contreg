package eionet.cr.dao.readers;

import org.apache.commons.lang.StringUtils;
import org.openrdf.query.BindingSet;

import eionet.cr.dto.SkosItemDTO;
import eionet.cr.util.URIUtil;
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

            // Get and set skos:notation. If it's blank, try detecting from URI.
            String skosNotation = getStringValue(bindingSet, "skosNotation");
            if (StringUtils.isBlank(skosNotation)) {
                skosNotation = URIUtil.extractURILabel(uri, uri);
            }
            item.setSkosNotation(skosNotation);

            // Get and set skos:prefLabel. If it's blank, fall back to skos:notation.
            String prefLabel = getStringValue(bindingSet, "skosPrefLabel");
            if (StringUtils.isBlank(prefLabel)) {
                prefLabel = skosNotation;
            }
            item.setSkosPrefLabel(prefLabel);

            // Add the item to the result list.
            resultList.add(item);
        }
    }
}
