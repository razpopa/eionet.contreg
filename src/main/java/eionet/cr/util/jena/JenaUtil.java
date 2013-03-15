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
import com.hp.hpl.jena.rdf.model.Resource;
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

                Resource jenaSubject = statement.getSubject();
                Property jenaPredicate = statement.getPredicate();
                RDFNode jenaObject = statement.getObject();

                URI sesameSubject = vf.createURI(jenaSubject.getURI());
                URI sesamePredicate = vf.createURI(jenaPredicate.getURI());
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
                    sesameObject = vf.createURI(jenaObject.asResource().getURI());
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
