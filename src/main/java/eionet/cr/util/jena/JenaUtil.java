package eionet.cr.util.jena;

import org.apache.commons.lang.StringUtils;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import eionet.cr.util.sesame.SesameUtil;

public class JenaUtil {

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
     * @return
     * @throws OpenRDFException
     */
    public static int saveModel(Model model, String graphUri) throws OpenRDFException {

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
                        subjectUri != null ? vf.createURI(subjectUri) : vf.createBNode(jenaSubject.getId().toString());

                Property jenaPredicate = statement.getPredicate();
                String predicateUri = jenaPredicate.getURI();
                // Skip anonymous predicates as they should be theoretically impossible, and in case practically useless.
                if (predicateUri == null) {
                    continue;
                }
                URI sesamePredicate = vf.createURI(predicateUri);

                RDFNode jenaObject = statement.getObject();
                Value sesameObject = null;

                if (jenaObject.isLiteral()) {
                    ;
                    Literal jenaLiteral = jenaObject.asLiteral();
                    String language = jenaLiteral.getLanguage();
                    if (StringUtils.isNotBlank(language)) {
                        sesameObject = vf.createLiteral(jenaLiteral.getLexicalForm(), language);
                    } else {
                        String datatypeUri = jenaLiteral.getDatatypeURI();
                        if (StringUtils.isNotBlank(datatypeUri)) {
                            sesameObject = vf.createLiteral(jenaLiteral.getLexicalForm(), vf.createURI(datatypeUri));
                        } else {
                            sesameObject = vf.createLiteral(jenaLiteral.getLexicalForm());
                        }
                    }
                } else {
                    com.hp.hpl.jena.rdf.model.Resource jenaResource = jenaObject.asResource();
                    String uri = jenaResource.getURI();
                    if (uri != null) {
                        sesameObject = vf.createURI(uri);
                    } else {
                        sesameObject = vf.createBNode(jenaResource.getId().toString());
                    }
                }

                repoConn.add(sesameSubject, sesamePredicate, sesameObject, graphURI);
                addedStmtCounter++;
            }

            if (addedStmtCounter > 0) {
                repoConn.commit();
            }

            return addedStmtCounter;
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
