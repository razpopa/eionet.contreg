package eionet.cr.harvest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mortbay.jetty.Server;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import eionet.cr.common.Predicates;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HarvestSourceDAO;
import eionet.cr.dto.HarvestSourceDTO;
import eionet.cr.test.helpers.CRDatabaseTestCase;
import eionet.cr.test.helpers.JettyUtil;
import eionet.cr.test.helpers.RdfLoader;
import eionet.cr.util.sesame.SesameUtil;

/**
 * Unit tests for checking the various cases of detecting conversion schemas from harvested XML files and storing it into the
 * file's metadata under {@link Predicates#CR_SCHEMA} get properly handled.
 *
 * @author Jaanus
 */
public class ConversionSchemaParsingTest extends CRDatabaseTestCase {

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.test.helpers.CRDatabaseTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new RdfLoader().clearAllTriples();
    }

    /*
     * (non-Javadoc)
     *
     * @see eionet.cr.test.helpers.CRDatabaseTestCase#getXMLDataSetFiles()
     */
    @Override
    protected List<String> getXMLDataSetFiles() {
        return Arrays.asList("emptydb.xml");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testMultipleSchemas() throws Exception {

        Server server = null;
        try {
            server = JettyUtil.startResourceServerMock(8999, "/testResources", "multiple-schemas.xml");
            String url = "http://localhost:8999/testResources/multiple-schemas.xml";

            HarvestSourceDTO source = new HarvestSourceDTO();
            source.setUrl(url);
            source.setIntervalMinutes(5);
            DAOFactory.get().getDao(HarvestSourceDAO.class).addSource(source);

            PullHarvest harvest = new PullHarvest(url);
            harvest.execute();

            HashSet<String> expectedValues =
                    new HashSet<String>(Arrays.asList(
                            "http://dd.eionet.europa.eu/schemas/id2011850eu/AirQualityReporting_0.3.6b.xsd",
                            "http://schemas.opengis.net/sweCommon/2.0/swe.xsd"));

            List<Value> actualValues = getXmlSchemaAttrValues(url);
            assertNotNull("Expected values list to not be null", actualValues);
            assertEquals("Expect 2 values", 2, actualValues.size());
            for (Value value : actualValues) {
                assertTrue("Expected URI value", value instanceof URI);
                assertTrue("Unexpected avlue", expectedValues.contains(value.stringValue()));
            }
        } finally {
            JettyUtil.close(server);
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void testRootElem() throws Exception {

        Server server = null;
        try {
            server = JettyUtil.startResourceServerMock(8999, "/testResources", "inline-rdf.xml");
            String url = "http://localhost:8999/testResources/inline-rdf.xml";

            HarvestSourceDTO source = new HarvestSourceDTO();
            source.setUrl(url);
            source.setIntervalMinutes(5);
            DAOFactory.get().getDao(HarvestSourceDAO.class).addSource(source);

            PullHarvest harvest = new PullHarvest(url);
            harvest.execute();

            List<Value> actualValues = getXmlSchemaAttrValues(url);
            assertTrue("Expected no values", actualValues == null || actualValues.isEmpty());
        } finally {
            JettyUtil.close(server);
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void testPublicAndSystemDtd() throws Exception {

        Server server = null;
        try {
            server = JettyUtil.startResourceServerMock(8999, "/testResources", "xhtml-with-public-and-system-dtd.xhtml");
            String url = "http://localhost:8999/testResources/xhtml-with-public-and-system-dtd.xhtml";

            HarvestSourceDTO source = new HarvestSourceDTO();
            source.setUrl(url);
            source.setIntervalMinutes(5);
            DAOFactory.get().getDao(HarvestSourceDAO.class).addSource(source);

            PullHarvest harvest = new PullHarvest(url);
            harvest.execute();

            String systemDtd = "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd";
            List<Value> actualValues = getXmlSchemaAttrValues(url);

            assertNotNull("Expected values list to not be null", actualValues);
            assertEquals("Expect 1 value", 1, actualValues.size());

            Value actualValue = actualValues.get(0);
            assertTrue("Expected URI value", actualValue instanceof URI);
            assertEquals("Unexpected string value", systemDtd, actualValue.stringValue());
        } finally {
            JettyUtil.close(server);
        }
    }

    /**
     * @param subjectUri
     * @return
     * @throws RepositoryException
     */
    private List<Value> getXmlSchemaAttrValues(String subjectUri) throws RepositoryException {

        ArrayList<Value> resultList = new ArrayList<Value>();
        RepositoryConnection repoConn = null;
        try {
            repoConn = SesameUtil.getRepositoryConnection();
            ValueFactory vf = repoConn.getValueFactory();
            URI subject = vf.createURI(subjectUri);
            URI predicate = vf.createURI(Predicates.CR_SCHEMA);
            RepositoryResult<Statement> statements = repoConn.getStatements(subject, predicate, null, false);
            if (statements != null) {
                while (statements.hasNext()) {
                    Statement stmt = statements.next();
                    resultList.add(stmt.getObject());
                }
            }
        } finally {
            SesameUtil.close(repoConn);
        }

        return resultList;
    }
}
