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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test urgent harvests.
 *
 * @author Enriko Käsper
 */
public class UrgentHarvestTest extends TestCase {

    /**
     * Test that a simple urgent is initiated without errors and that it is successfully polled/removed from the queue.
     *
     * @throws Exception When any sort of error happens.
     */
    @Test
    public void testUrgentHarvestUnicodeUrls() throws Exception {

        String url = "http://www.google.com/öö";
        UrgentHarvestQueue.addPullHarvest(url, "enriko");

        assertTrue(UrgentHarvestQueue.isInQueue(url));
        UrgentHarvestQueue.poll();
        Thread.sleep(1000);
        assertFalse(UrgentHarvestQueue.isInQueue(url));
    }
}
