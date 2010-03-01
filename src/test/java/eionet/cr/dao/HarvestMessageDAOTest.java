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
 * The Original Code is Content Registry 2.0.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Jaanus Heinlaid, Tieto Eesti
 */
package eionet.cr.dao;

import java.util.List;

import junit.framework.TestCase;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;

import eionet.cr.config.GeneralConfig;
import eionet.cr.dto.HarvestMessageDTO;
import eionet.cr.dto.HarvestSourceDTO;
import eionet.cr.test.helpers.CRDatabaseTestCase;
import eionet.cr.test.helpers.DbHelper;
import eionet.cr.test.helpers.dbunit.DbUnitDatabaseConnection;
import eionet.cr.util.Hashes;
import eionet.cr.util.sql.ConnectionUtil;

/**
 * JUnit test tests HarvestMessageDAO functionality.
 * 
 * @author altnyris
 *
 */
public class HarvestMessageDAOTest extends CRDatabaseTestCase {

	/*
	 * (non-Javadoc)
	 * @see org.dbunit.DatabaseTestCase#getDataSet()
	 */
	protected IDataSet getDataSet() throws Exception {
		return getXmlDataSet("sources-harvests-messages.xml");
	}
	
	@Test
	public void testInsertAndFindHarvestMessage() throws Exception {
		
		HarvestMessageDTO harvestMessage = new HarvestMessageDTO();
		harvestMessage.setHarvestId(55);
		harvestMessage.setMessage("test");
		harvestMessage.setStackTrace("teststack");
		harvestMessage.setType("01");
		
		HarvestMessageDAO dao = DAOFactory.get().getDao(HarvestMessageDAO.class);
		Integer messageID = dao.insertHarvestMessage(harvestMessage);
		assertNotNull(messageID);
		
		harvestMessage = dao.findHarvestMessageByMessageID(messageID.intValue());
		assertEquals(messageID, harvestMessage.getHarvestMessageId());
	}
	
	@Test
	public void testFindHarvestMessagesByHarvestID() throws Exception {
		
		List<HarvestMessageDTO> messages = DAOFactory.get().getDao(HarvestMessageDAO.class).findHarvestMessagesByHarvestID(121);
		assertEquals(2, messages.size());
	}
	
	@Test
	public void testDeleteMessage() throws Exception {

		DAOFactory.get().getDao(HarvestMessageDAO.class).deleteMessage(5);
		HarvestMessageDTO message = DAOFactory.get().getDao(HarvestMessageDAO.class).findHarvestMessageByMessageID(5);
		assertNull(message);
	}
	
	@Test
	public void testInsertSource() throws Exception {
		
		HarvestSourceDTO dto = new HarvestSourceDTO();
		dto.setUrl("http://1.ee");
		dto.setEmails("emails");
		dto.setIntervalMinutes(1);
		dto.setTrackedFile(true);
		
		Integer id = DAOFactory.get().getDao(HarvestSourceDAO.class).addSource(dto, "user");
		assertNotNull(id);
		
		DAOFactory.get().getDao(HarvestSourceDAO.class).addSourceIgnoreDuplicate(dto, "user");
	}
}
