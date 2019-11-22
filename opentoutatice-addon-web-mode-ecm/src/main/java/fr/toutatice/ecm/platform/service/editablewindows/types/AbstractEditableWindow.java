package fr.toutatice.ecm.platform.service.editablewindows.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Abstract behavior for editable windows
 * @author Loïc Billon
 */
public abstract class AbstractEditableWindow implements EditableWindow {

	
    private static final Log log = LogFactory.getLog(AbstractEditableWindow.class);

	/**
	 * Duplicate an editable window
	 * - Get specific schemas and create a new entry with values of the old entries
	 * - Generate a specific uri
	 * - reorder below editable windows
	 */
	public void duplicate(DocumentModel doc, String fromUri, String newUri) {
		
		for(String schemaToCopy : getSpecifcSchemas()) {
			
			log.warn("Copy "+schemaToCopy+" from "+fromUri+" to "+newUri);
			
			Map<String, Object> properties = doc.getProperties(schemaToCopy);
			
			Collection<Object> values = properties.values();
			Object liste = values.iterator().next();
			
			if (liste instanceof List) {
				List<Map<String, String>> listeEw = (List<Map<String, String>>) liste;
				
				for(Map<String, String> window : listeEw) {
					
					if (fromUri.equals(window.get("refURI"))) {				
						
						Map<String, String> newEntry = new HashMap<String, String>();
                        newEntry.putAll(window);
                        newEntry.put("refURI", newUri);
                        
                        listeEw.add(newEntry);
						break;
					}
				}
				
				doc.setProperties(schemaToCopy, properties);
			}
			
		}
	}
	
	public abstract List<String> getSpecifcSchemas();

}
