/**
 * 
 */
package org.osivia.opentoutatice.web.bean;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.ecm.platform.core.constants.ExtendedSeamPrecedence;
import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;
import fr.toutatice.ecm.platform.services.permalink.PermaLinkService;
import fr.toutatice.ecm.platform.web.document.ToutaticeDocumentActionsBean;

/**
 * @author david
 *
 */
@Name("webModeDocumentActionsBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = ExtendedSeamPrecedence.TOUTATICE + 100)
public class WebModeDocumentActionsBean implements Serializable {

	private static final long serialVersionUID = 5410768946883126601L;
	
	@In(create = true)
    protected CoreSession documentManager;
	
	@In(create = true)
	protected NavigationContext navigationContext;
	
	@In(create = true)
	protected ToutaticeDocumentActionsBean documentActions;
	
	@RequestParameter("portalWebPath")
    private String portalWebPath;
	
	/**
	 * Default behavior: all Folderish are shown in menu.
	 * @throws ClientException
	 */
	@Observer(value = {EventNames.NEW_DOCUMENT_CREATED})
    public void initShowInMenu() throws ClientException {
	    documentActions.initShowInMenu();
		
		DocumentModel newDocument = navigationContext.getChangeableDocument();
		boolean folderish = newDocument.hasFacet("Folderish");
		
		newDocument.setPropertyValue(ToutaticeNuxeoStudioConst.CST_DOC_XPATH_TOUTATICE_SIM, folderish);
	}
	
	/**
     * @return the portalWebPath
     */
    public String getPortalWebPath() {
        return portalWebPath;
    }
    
    /**
     * @param portalWebPath the portalWebPath to set
     */
    public void setPortalWebPath(String portalWebPath) {
        this.portalWebPath = portalWebPath;
    }
	
}
