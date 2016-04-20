/**
 * 
 */
package fr.toutatice.ecm.platform.web.mode.bean;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.BooleanUtils;
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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventNames;

import fr.toutatice.ecm.platform.core.constants.ExtendedSeamPrecedence;
import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;
import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;
import fr.toutatice.ecm.platform.web.document.ToutaticeDocumentActionsBean;
import fr.toutatice.ecm.platform.web.mode.constants.WebModeConstants;

/**
 * @author david
 *
 */
@Name("webModeDocumentActionsBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = ExtendedSeamPrecedence.INHERIT_TOUTATICE)
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
    
    /**
     * @return true if given document:
     *         * is in a PortalSite with webUrls enabled
     *         * is in a Domain containing at least one PortalSite with webUrls
     *           enabled.
     */
    public boolean supportsWebUrls(DocumentModel currentDocument) {
        boolean supports = false;
        
        // Case of document in Publish Space with webUrls enabled
        DocumentModelList publishSpaceList = ToutaticeDocumentHelper.getParentPublishSpaceList(documentManager, currentDocument, true, true);
        
        Boolean webUrlsEnabled = Boolean.FALSE;
        DocumentModel publishSpaceWithWebUrls = null;
        if (CollectionUtils.isNotEmpty(publishSpaceList)) {
            Iterator<DocumentModel> iterator = publishSpaceList.iterator();
            while (iterator.hasNext() && !webUrlsEnabled) {
                DocumentModel publishSpace = iterator.next();
                webUrlsEnabled = (Boolean) publishSpace.getPropertyValue(WebModeConstants.ARE_WEB_URLS_ENABLED_PROP);
                if (BooleanUtils.isTrue(webUrlsEnabled)){
                    publishSpaceWithWebUrls = publishSpace;
                }
            }
        }
        
        // Case of document in a Domain containing at least one PortalSite with webUrls enabled
        if(BooleanUtils.isTrue(webUrlsEnabled)){
            DocumentModel domainOfDoc = ToutaticeDocumentHelper.getDomain(documentManager, currentDocument, true);
            DocumentModel domainOfPublishSpace = ToutaticeDocumentHelper.getDomain(documentManager, publishSpaceWithWebUrls, true);
            
            boolean sameDomain = StringUtils.equals(domainOfPublishSpace.getId(), domainOfDoc.getId());
            if (sameDomain && currentDocument.hasFacet(WebModeConstants.HAS_WEB_URL_FACET)) {
                supports = true;
            }
        } else {
            supports = false;
        }
        
        return supports;
    }
	
}
