package eionet.cr.dao.virtuoso.helpers;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import eionet.cr.common.Predicates;
import eionet.cr.dao.helpers.AbstractSearchHelper;
import eionet.cr.util.Bindings;
import eionet.cr.util.SortingRequest;
import eionet.cr.util.pagination.PagingRequest;

/**
 *
 * @author jaanus
 *
 */
public class VirtuosoSearchBySourceHelper extends AbstractSearchHelper {

    /** SPARQL for getting subjects from the source. */
    private static final String SOURCE_SUBJECTS_SPARQL = "select distinct ?s from ?sourceUrl where {?s ?p ?o}";

    /** */
    private static final String SOURCE_SKIP_ANON_SUBJECTS_SPARQL =
            "select distinct ?s from ?sourceUrl where {?s ?p ?o filter(!isBlank(?s))}";

    /** SPARQL for getting count of subjects in the source. */
    private static final String SOURCE_SUBJECTS_COUNT_SPARQL = "select count(distinct ?s) from ?sourceUrl where {?s ?p ?o}";

    /** */
    private static final String SOURCE_SKIP_ANON_SUBJECTS_COUNT_SPARQL =
            "select count(distinct ?s) from ?sourceUrl where {?s ?p ?o filter(!isBlank(?s))}";

    /** Query bindings. */
    private Bindings bindings;

    /** */
    private boolean skipAnonymous = true;

    /**
     * @param sourceUrl
     * @param pagingRequest
     * @param sortingRequest
     */
    public VirtuosoSearchBySourceHelper(String sourceUrl, PagingRequest pagingRequest,
            SortingRequest sortingRequest) {
        this(sourceUrl, true, pagingRequest, sortingRequest);
    }
    /**
     *
     * @param sourceUrl
     * @param skipAnonymous
     * @param pagingRequest
     * @param sortingRequest
     */
    public VirtuosoSearchBySourceHelper(String sourceUrl, boolean skipAnonymous, PagingRequest pagingRequest,
            SortingRequest sortingRequest) {

        super(pagingRequest, sortingRequest);
        this.skipAnonymous = skipAnonymous;
        bindings = new Bindings();
        // this binding is used in all SPARQL's
        bindings.setURI("sourceUrl", sourceUrl);
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.dao.helpers.AbstractSearchHelper#getUnorderedQuery(java.util.List)
     */
    @Override
    public String getUnorderedQuery(List<Object> inParams) {
        return skipAnonymous ? SOURCE_SKIP_ANON_SUBJECTS_SPARQL : SOURCE_SUBJECTS_SPARQL;
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.dao.helpers.AbstractSearchHelper#getOrderedQuery(java.util.List)
     */
    @Override
    protected String getOrderedQuery(List<Object> inParams) {

        String sparql = "select distinct ?s from ?sourceUrl where {?s ?p ?o optional {?s ?sortPredicate ?ord} } ORDER BY ";
        if (skipAnonymous) {
            sparql = StringUtils.replace(sparql, "optional", "filter(!isBlank(?s)) optional");
        }
        bindings.setURI("sortPredicate", sortPredicate);

        if (sortOrder != null) {
            sparql += sortOrder;
        }

        if (sortPredicate != null && sortPredicate.equals(Predicates.RDFS_LABEL)) {
            // If Label is not null then use Label. Otherwise use subject where we replace all / with #
            // and then get the string after last #.
            sparql +=
                    "(bif:lcase(bif:either(bif:isnull(?ord), "
                            + "(bif:subseq (bif:replace (?s, '/', '#'), bif:strrchr (bif:replace (?s, '/', '#'), '#')+1)), ?ord)))";
        } else if (sortPredicate != null && sortPredicate.equals(Predicates.RDF_TYPE)) {
            // Replace all / with # and then get the string after last #
            sparql += "(bif:lcase(bif:subseq (bif:replace (?ord, '/', '#'), bif:strrchr (bif:replace (?ord, '/', '#'), '#')+1)))";
        } else {
            sparql += "(?ord)";
        }
        return sparql;
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.dao.helpers.AbstractSearchHelper#getCountQuery(java.util.List)
     */
    @Override
    public String getCountQuery(List<Object> inParams) {
        return skipAnonymous ? SOURCE_SKIP_ANON_SUBJECTS_COUNT_SPARQL : SOURCE_SUBJECTS_COUNT_SPARQL;
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.dao.helpers.AbstractSearchHelper#getQueryBindings()
     */
    @Override
    public Bindings getQueryBindings() {
        return bindings;
    }
}
