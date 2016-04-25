/**
 * 
 */
package fr.toutatice.ecm.platform.web.mode.service;

import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;
import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;
import fr.toutatice.ecm.platform.web.mode.constants.WebModeConstants;


/**
 * @author david
 *
 */
public class SegmentService extends DefaultComponent {
    
    /** Siblings query. */
    public static final String SIBLINGS_QUERY = "select * from Document where ecm:parentId = '%s' and ecm:uuid <> '%s' and ottcweb:segment = '%s' "
            + "and ecm:isVersion = 0 and ecm:isProxy = 0 and ecm:currentLifeCycleState <> 'deleted'";
    
    /**
     * @return true if given document:
     *         * is in a PortalSite with webUrls enabled
     *         * is in a Domain containing at least one PortalSite with webUrls
     *         enabled.
     */
    public boolean supportsWebUrls(CoreSession session, DocumentModel currentDocument) {
        boolean supports = false;

        DocumentModel domain = ToutaticeDocumentHelper.getDomain(session, currentDocument, true);
        if(domain != null){

            String query = "select * from PortalSite where ecm:parentId = '%s' and ottcwebc:enableWebUrl = 1";
            DocumentModelList portalSitesWithWebUrls = session.query(String.format(query, domain.getId()));
    
            // There exists at least one PortalSite with webUrls enabled
            if (CollectionUtils.isNotEmpty(portalSitesWithWebUrls) && portalSitesWithWebUrls.size() >= 1) {
                // Case of currentDocument is child of PortalSite with webUrls enabled
                boolean isChild = false;
                Iterator<DocumentModel> iterator = portalSitesWithWebUrls.iterator();
    
                while (iterator.hasNext() && !isChild) {
                    DocumentModel portalSite = iterator.next();
                    if (StringUtils.contains(currentDocument.getPathAsString(), portalSite.getPathAsString())) {
                        isChild = true;
                        supports = true;
                    }
                }
    
                // Case of current document out of PortalSite with webUrls enabled
                if (!isChild && currentDocument.hasFacet(WebModeConstants.HAS_WEB_URL_FACET)) {
                    supports = true;
                }
            }
        
        }

        return supports;
    }

    /**
     * 
     * @param document
     * @return a unique value of segment property for given document.
     * @throws DocumentException
     */
    // FIXME: try to use sequencer (cf AbstractUIDGenerator)
    public String createSegment(CoreSession session, DocumentModel document) {
        String initialSegment = (String) document.getPropertyValue(WebModeConstants.SEGMENT_PROPERTY);

        if (StringUtils.isBlank(initialSegment)) {
            initialSegment = generateSegment(document);
        }
        
        String segment = initialSegment;
        int increment = 1;
        while (!isUniqueSegment(session, document, segment)) {
            segment = getNextSegment(initialSegment, increment);
            increment++;
        }

        return segment;
    }

  
    /**
     * 
     * @param session
     * @param document
     * @param segment
     * @return true if segment of given document is not unique inside its siblings.
     */
    public boolean isUniqueSegment(CoreSession session, DocumentModel document, String segment) {
        // Unicity is necessary only between direct siblings 
        DocumentModel parent = session.getParentDocument(document.getRef());
        if(parent != null){
            DocumentModelList siblings =session.query(String.format(SIBLINGS_QUERY, parent.getId(), document.getId(), segment));
            return CollectionUtils.isEmpty(siblings);
        }
        return false;
    }


    /**
     * 
     * @param document
     * @return segment value based on formatted document title.
     * @throws DocumentException
     */
    protected String generateSegment(DocumentModel document) {
        String title = document.getTitle();
        return IdUtils.generateId(title, "-", false, 30);
    }

    /**
     * 
     * @param segment
     * @param increment
     * @return segment incremented.
     */
    // FIXME: try to use sequencer (cf AbstractUIDGenerator)
    private String getNextSegment(String segment, int increment) {
        return segment.concat(String.valueOf(increment));
    }

}
