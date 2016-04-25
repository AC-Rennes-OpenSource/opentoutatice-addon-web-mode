/**
 * 
 */
package fr.toutatice.ecm.platform.web.mode.listener;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;
import fr.toutatice.ecm.platform.web.mode.constants.WebModeConstants;
import fr.toutatice.ecm.platform.web.mode.service.SegmentService;


/**
 * @author david
 *
 */
public class BulkSetSegmentsListener implements PostCommitEventListener {
    
    /** Lives children query. */
    public static final String LIVES_CHILDREN = "select * from Document where ecm:parentId = '%s' "
            + "and ecm:isVersion = 0 and ecm:isProxy = 0 and ecm:currentLifeCycleState <> 'deleted'";

    @Override
    public void handleEvent(EventBundle events) throws ClientException {

        for (Event event : events) {

            if (DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName())) {

                DocumentEventContext evtCtx = (DocumentEventContext) event.getContext();
                DocumentModel sourceDocument = evtCtx.getSourceDocument();
                CoreSession session = sourceDocument.getCoreSession();
                
                SegmentService segmentService = Framework.getService(SegmentService.class);
                if (sourceDocument.isFolder() && segmentService.supportsWebUrls(session, sourceDocument)) {
                    setSegments(session, segmentService, sourceDocument);
                }

            }
        }
    }
    
    /**
     * Set segments recursivly.
     * 
     * @param session
     * @param segmentService
     * @param document
     */
    private void setSegments(CoreSession session, SegmentService segmentService, DocumentModel document) {
        String segment = (String) document.getPropertyValue(WebModeConstants.SEGMENT_PROPERTY);
        if(StringUtils.isBlank(segment)){
            generateSegment(session, segmentService, document);
        }
        if (session.hasChildren(document.getRef())) {
            DocumentModelList children = session.query(String.format(LIVES_CHILDREN, document.getId()));
            for (DocumentModel child : children) {
                setSegments(session, segmentService, child);
            }
        }
        
        session.save();
    }

    /**
     * Generate a segment value for given document.
     * 
     * @param session
     * @param segmentService
     * @param document
     * @param child
     */
    private void generateSegment(CoreSession session, SegmentService segmentService, DocumentModel document) {
        String segment = segmentService.createSegment(session, document);
        document.setPropertyValue(WebModeConstants.SEGMENT_PROPERTY, segment);
        ToutaticeDocumentHelper.saveDocumentSilently(session, document, false);
    }

}
