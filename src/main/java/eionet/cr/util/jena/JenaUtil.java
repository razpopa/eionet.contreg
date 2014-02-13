package eionet.cr.util.jena;

import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.openrdf.OpenRDFException;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFHandler;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eionet.cr.util.Pair;
import eionet.cr.util.sesame.SesameUtil;

/**
 * Utility class for operations with Jena API.
 *
 * @author Jaanus
 */
public class JenaUtil {

    /**
     * Disable utility class constructor.
     */
    private JenaUtil() {
        // Empty constructor.
    }

    /**
     *
     * @param model
     */
    public static void close(Model model) {
        if (model != null) {
            try {
                model.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     *
     * @param model
     * @param graphUri
     * @param clearGraph
     * @param stmtListener
     * @return
     * @throws OpenRDFException
     */
    public static Pair<Integer, Integer> saveModel(Model model, String graphUri, boolean clearGraph, RDFHandler stmtListener)
            throws OpenRDFException {

        HashSet<String> distinctNonAnonymousResources = new HashSet<String>();
        RepositoryConnection repoConn = null;
        try {
            repoConn = SesameUtil.getRepositoryConnection();
            repoConn.setAutoCommit(false);

            ValueFactory vf = repoConn.getValueFactory();
            URI graphURI = vf.createURI(graphUri);
            int addedStmtCounter = 0;

            StmtIterator statements = model.listStatements();
            while (statements.hasNext()) {

                Statement statement = statements.next();

                com.hp.hpl.jena.rdf.model.Resource jenaSubject = statement.getSubject();
                String subjectUri = jenaSubject.getURI();
                org.openrdf.model.Resource sesameSubject =
                        subjectUri != null ? vf.createURI(subjectUri.trim()) : vf.createBNode(jenaSubject.getId().toString());

                Property jenaPredicate = statement.getPredicate();
                String predicateUri = jenaPredicate.getURI();
                // Skip anonymous predicates as they should be theoretically impossible, and in case practically useless.
                if (predicateUri == null) {
                    continue;
                } else {
                    predicateUri = predicateUri.trim();
                }
                URI sesamePredicate = vf.createURI(predicateUri);

                RDFNode jenaObject = statement.getObject();
                Value sesameObject = null;

                if (jenaObject.isLiteral()) {

                    Literal jenaLiteral = jenaObject.asLiteral();
                    String language = jenaLiteral.getLanguage();
                    String lexicalForm = jenaLiteral.getLexicalForm();

                    if (StringUtils.isNotBlank(language)) {
                        sesameObject = vf.createLiteral(lexicalForm, language);
                    } else {
                        String datatypeUri = jenaLiteral.getDatatypeURI();
                        if (StringUtils.isNotBlank(datatypeUri)) {
                            sesameObject = vf.createLiteral(lexicalForm, vf.createURI(datatypeUri));
                        } else {
                            sesameObject = vf.createLiteral(lexicalForm);
                        }
                    }
                } else {
                    com.hp.hpl.jena.rdf.model.Resource jenaResource = jenaObject.asResource();
                    String uri = jenaResource.getURI();
                    if (uri != null) {
                        sesameObject = vf.createURI(uri.trim());
                    } else {
                        sesameObject = vf.createBNode(jenaResource.getId().toString());
                    }
                }

                // If first triple to be added, and the graph should be cleared, then do so here now.
                if (clearGraph && addedStmtCounter == 0) {
                    repoConn.clear(graphURI);
                }

                repoConn.add(sesameSubject, sesamePredicate, sesameObject, graphURI);
                if (stmtListener != null) {
                    stmtListener.handleStatement(new ContextStatementImpl(sesameSubject, sesamePredicate, sesameObject, graphURI));
                }

                addedStmtCounter++;
                if (!(sesameSubject instanceof BNode)) {
                    distinctNonAnonymousResources.add(sesameSubject.stringValue());
                }
            }

            if (addedStmtCounter > 0) {
                repoConn.commit();
            }

            return new Pair<Integer, Integer>(addedStmtCounter, distinctNonAnonymousResources.size());
        } catch (Error e) {
            SesameUtil.rollback(repoConn);
            throw e;
        } catch (RuntimeException e) {
            SesameUtil.rollback(repoConn);
            throw e;
        } catch (OpenRDFException e) {
            SesameUtil.rollback(repoConn);
            throw e;
        } finally {
            SesameUtil.close(repoConn);
        }
    }

}
