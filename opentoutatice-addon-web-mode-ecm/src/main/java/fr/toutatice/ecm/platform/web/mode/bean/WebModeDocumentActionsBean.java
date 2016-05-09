/**
 * 
 */
package fr.toutatice.ecm.platform.web.mode.bean;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;
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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventNames;

import fr.toutatice.ecm.platform.core.constants.ExtendedSeamPrecedence;
import fr.toutatice.ecm.platform.core.constants.ToutaticeGlobalConst;
import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;
import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;
import fr.toutatice.ecm.platform.web.document.ToutaticeDocumentActionsBean;
import fr.toutatice.ecm.platform.web.mode.constants.WebModeConstants;
import fr.toutatice.ecm.platform.web.mode.service.SegmentService;

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
	
	@In(create = true)
	protected SegmentService segmentService;
	
	@RequestParameter("portalWebPath")
    private String portalWebPath;
	
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
        if (currentDocument.hasFacet(ToutaticeNuxeoStudioConst.CST_FACET_REMOTE_PROXY)){
            return false;
        }
        return this.segmentService.supportsWebUrls(documentManager, currentDocument);
    }
	
}
