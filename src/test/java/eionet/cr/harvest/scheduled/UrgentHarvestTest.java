/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.cr.harvest.scheduled;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.UrgentHarvestQueueDAO;
import eionet.cr.dto.UrgentHarvestQueueItemDTO;
import eionet.cr.test.helpers.CRDatabaseTestCase;

/**
 * Test urgent harvests.
 *
 * @author Enriko Käsper
 */
public class UrgentHarvestTest extends CRDatabaseTestCase {

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
     * Test that a simple urgent is initiated without errors and that it is successfully polled/removed from the queue.
     *
     * @throws Exception When any sort of error happens.
     */
    @Test
    public void testUrgentHarvestUnicodeUrls() throws Exception {

        String url = "http://www.google.com/öö";

        UrgentHarvestQueueDAO dao = DAOFactory.get().getDao(UrgentHarvestQueueDAO.class);
        dao.removeUrl(url);
        assertFalse("Didn't expect this URL in harvest queue: " + url, UrgentHarvestQueue.isInQueue(url));

        UrgentHarvestQueue.addPullHarvest(url, "enriko");
        assertTrue("Expected this URL in harvest queue: " + url, UrgentHarvestQueue.isInQueue(url));

        UrgentHarvestQueueItemDTO dto = UrgentHarvestQueue.poll();
        assertNotNull("Expected non-null poll result", dto);
        assertTrue("Expected poll-result to have this URL: " + url, url.equals(dto.getUrl()));

        Thread.sleep(1000);
        assertFalse("Didn't expect this URL in harvest queue any more: " + url, UrgentHarvestQueue.isInQueue(url));
    }
}
