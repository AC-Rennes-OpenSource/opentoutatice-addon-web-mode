/**
 * 
 */
package fr.toutatice.ecm.platform.web.mode.listener;

import org.apache.commons.lang.BooleanUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventBundleImpl;
import org.nuxeo.ecm.core.event.impl.ShallowEvent;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;
import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;
import fr.toutatice.ecm.platform.web.mode.constants.WebModeConstants;
import fr.toutatice.ecm.platform.web.mode.service.SegmentService;


/**
 * @author david
 *
 */
public class SetSegmentListener implements EventListener {

    @Override
    public void handleEvent(Event event) throws ClientException {
        SegmentService segmentService = (SegmentService) Framework.getService(SegmentService.class);
        
        DocumentEventContext evtCtx = (DocumentEventContext) event.getContext();
        DocumentModel sourceDocument = evtCtx.getSourceDocument();
        CoreSession session = evtCtx.getCoreSession();
        
        if(DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName()) 
                && ToutaticeNuxeoStudioConst.CST_DOC_TYPE_PORTALSITE.equals(sourceDocument.getType())){
            
            Property webUrlsEnabledProp = sourceDocument.getProperty(WebModeConstants.ARE_WEB_URLS_ENABLED_PROP);
            if(webUrlsEnabledProp.isDirty() && BooleanUtils.isNotFalse((Boolean) webUrlsEnabledProp.getValue())){
                setSegment(session, segmentService, sourceDocument);
                
                // beforeDocumentModification is an inline event, so it can not be listened
                // by an asynchronous post commit listener; so we redirect manually to BulkSetSegmentsListener.
                ShallowEvent shallowEvent = ShallowEvent.create(event);
                EventBundleImpl b = new EventBundleImpl();
                b.push(shallowEvent);
                Framework.getLocalService(EventService.class).fireEventBundle(b);
            }
            
        } else if(!DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName())) {
        
            if(segmentService.supportsWebUrls(session, sourceDocument)){
                // ToutaticeDocumentMove manages local proxy move
                if(!sourceDocument.isProxy() && !DocumentEventTypes.DOCUMENT_MOVED.equals(event.getName())){
                    setSegment(session, segmentService, sourceDocument);
                }
            }
        
        }
 
    }

    /**
     * Sets segment on given sourceDocument.
     * 
     * @param sourceDocument
     * @param segmentService
     * @param session
     */
    protected void setSegment(CoreSession session, SegmentService segmentService, DocumentModel sourceDocument) {
        String segment = segmentService.createSegment(session, sourceDocument);
        sourceDocument.setPropertyValue(WebModeConstants.SEGMENT_PROPERTY, segment);
        // To prevent fire of new beforeDocumentModification event
        ToutaticeDocumentHelper.saveDocumentSilently(session, sourceDocument, false);
    }

}
