package eionet.cr.harvest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;

import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.HarvestSourceDAO;
import eionet.cr.dto.HarvestSourceDTO;
import eionet.cr.test.helpers.CRDatabaseTestCase;
import eionet.cr.test.helpers.JettyUtil;

/**
 * Test harvesting an unauthorized source (i.e. returns HTTP 401) after it has been successfully harvested beforehand.
 *
 * @author Jaanus
 */
public class UnauthorizedHarvestTest extends CRDatabaseTestCase {

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
     *
     */
    @Test
    public void test() throws Exception {

        Server server = null;
        try {
            // Set up the server that will serve our servlet at "/testServlet", listening on port 8999.

            server = new Server(8999);
            ServletHandler handler = new ServletHandler();
            server.setHandler(handler);
            handler.addServletWithMapping(RdfServlet.class, "/testServlet");
            server.start();

            // Call our test servlet.

            String url = "http://localhost:8999/testServlet";
            int intervalMinutes = 15;
            int noOfTriples = 2;

            HarvestSourceDTO source = new HarvestSourceDTO();
            source.setUrl(url);
            source.setIntervalMinutes(intervalMinutes);

            HarvestSourceDAO harvestSourceDao = DAOFactory.get().getDao(HarvestSourceDAO.class);
            harvestSourceDao.addSource(source);
            PullHarvest harvest = new PullHarvest(url);
            harvest.execute();

            // Validate first call to our test servlet.

            assertTrue("Expected this URL to be available: " + url, harvest.isSourceAvailable());
            assertEquals("Unexpected number of harvested triples", noOfTriples, harvest.getStoredTriplesCount());
            source = harvestSourceDao.getHarvestSourceByUrl(url);
            assertNotNull("Expected a stored harvest source for this URL: " + url, source);
            assertEquals("Unexpected source harvest interval", Integer.valueOf(intervalMinutes), source.getIntervalMinutes());
            assertEquals("Unexpected number of triples in source", Integer.valueOf(noOfTriples), source.getStatements());

            // Second call, now expecting HTTP 401.

            server.stop();
            server = new Server(8999);
            handler = new ServletHandler();
            server.setHandler(handler);
            // Mapping 401-sending servlet to same URL as above.
            handler.addServletWithMapping(UnauthorizedServlet.class, "/testServlet");
            server.start();

            harvest = new PullHarvest(url);
            harvest.execute();

            // Assert that the harvest was unauthorized and no triples were harvested.
            assertTrue("Expected this URL to be unavailable: " + url, !harvest.isSourceAvailable());
            assertEquals("Unexpected number of harvested triples", 0, harvest.getStoredTriplesCount());
            source = harvestSourceDao.getHarvestSourceByUrl(url);
            assertNotNull("Expected a stored harvest source for this URL: " + url, source);
            assertEquals("Unexpected source harvest interval", Integer.valueOf(intervalMinutes), source.getIntervalMinutes());
            assertEquals("Unexpected number of triples in source", Integer.valueOf(0), source.getStatements());
        } finally {
            JettyUtil.close(server);
        }
    }

    /**
     * A servlet that returns proper RDF.
     *
     * @author Jaanus
     */
    @SuppressWarnings("serial")
    public static class RdfServlet extends HttpServlet {

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            response.setContentType("application/rdf+xml");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                writer.println("<?xml version=\"1.0\"?>");
                writer.println("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:ee=\"http://ee.ee/\">");
                writer.println("   <rdf:Description rdf:about=\"http://test.ee/resource1\">");
                writer.println("      <ee:attr1>Jaanus</ee:attr1>");
                writer.println("      <ee:attr2>Heinlaid</ee:attr2>");
                writer.println("   </rdf:Description>");
                writer.println("</rdf:RDF>");
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }
    }

    /**
     * A servlet that returns HTTP 401.
     *
     * @author Jaanus
     */
    @SuppressWarnings("serial")
    public static class UnauthorizedServlet extends HttpServlet {

        /*
         * (non-Javadoc)
         *
         * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            response.setContentType("application/rdf+xml");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

}
