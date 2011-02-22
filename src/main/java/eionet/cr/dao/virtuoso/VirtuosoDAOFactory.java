package eionet.cr.dao.virtuoso;

import java.util.HashMap;
import java.util.Map;

import eionet.cr.dao.DAO;
import eionet.cr.dao.DAOFactory;
import eionet.cr.dao.ExporterDAO;
import eionet.cr.dao.HelperDAO;
import eionet.cr.dao.SearchDAO;
import eionet.cr.dao.TagsDAO;

/**
 * 
 * @author jaanus
 *
 */
public class VirtuosoDAOFactory extends DAOFactory{

	/** */
	private static VirtuosoDAOFactory instance;	
	private Map<Class<? extends DAO>, Class<? extends VirtuosoBaseDAO>> registeredDaos;

	/**
	 * 
	 */
	private VirtuosoDAOFactory() {
		init();
	}

	/**
	 * 
	 */
	private void init() {
		
		registeredDaos = new HashMap<Class<? extends DAO>, Class<? extends VirtuosoBaseDAO>>();
		registeredDaos.put(ExporterDAO.class, VirtuosoExporterDAO.class);
		registeredDaos.put(HelperDAO.class, VirtuosoHelperDAO.class);
		registeredDaos.put(SearchDAO.class, VirtuosoSearchDAO.class);
		registeredDaos.put(TagsDAO.class, VirtuosoTagsDAO.class);
	}

	/**
	 * 
	 * @return
	 */
	public static VirtuosoDAOFactory get() {
		if(instance == null) {
			instance = new VirtuosoDAOFactory();
		}
		return instance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see eionet.cr.dao.DAOFactory#getDao(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T extends DAO> T getDao(Class<T> implementedInterface) {
		
		// due to synchronization problems we have to create DAOs for each method invocation.
		try {
			Class implClass = registeredDaos.get(implementedInterface);
			if (implClass==null){
				return null;
			}
			else{
				return (T) implClass.newInstance();
			}
		}
		catch (Exception fatal) {
			throw new RuntimeException(fatal);
		}
	}
}