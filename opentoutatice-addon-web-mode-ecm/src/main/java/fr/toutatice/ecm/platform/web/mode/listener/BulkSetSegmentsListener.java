/**
 * 
 */
package fr.toutatice.ecm.platform.web.mode.listener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;
import fr.toutatice.ecm.platform.core.helper.ToutaticeDocumentHelper;
import fr.toutatice.ecm.platform.core.helper.ToutaticeSilentProcessRunnerHelper;
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

    /** Leafs query which can have segments. */
    public static final String LEAFS_WITH_SEGMENTS = "select * from Document where ttc:domainID = '%s' and ecm:mixinType = '%s' "
            + "and ecm:isVersion = 0 and ecm:isProxy = 0 and ecm:currentLifeCycleState <> 'deleted'";

    @Override
    public void handleEvent(EventBundle events) throws ClientException {

        for (Event event : events) {

            if (DocumentEventTypes.BEFORE_DOC_UPDATE.equals(event.getName())) {

                EventContext eventContext = event.getContext();
                if (eventContext instanceof DocumentEventContext) {

                    DocumentModel sourceDocument = ((DocumentEventContext) eventContext).getSourceDocument();
                    CoreSession session = sourceDocument.getCoreSession();

                    // For security, we check some tests even if it must be always true
                    if (ToutaticeNuxeoStudioConst.CST_DOC_TYPE_PORTALSITE.equals(sourceDocument.getType())) {

                        Boolean webUrlsEnabled = (Boolean) sourceDocument.getPropertyValue(WebModeConstants.ARE_WEB_URLS_ENABLED_PROP);
                        if (BooleanUtils.isTrue(webUrlsEnabled)) {
                            SegmentService segmentService = Framework.getService(SegmentService.class);

                            DocumentModelList authorizedDocs = new DocumentModelListImpl();
                            authorizedDocs.add(sourceDocument);

                            String domainId = (String) sourceDocument.getPropertyValue(ToutaticeNuxeoStudioConst.CST_DOC_XPATH_TOUTATICE_DOMAIN_ID);
                            String leafQuery = String.format(LEAFS_WITH_SEGMENTS, domainId, WebModeConstants.HAS_WEB_URL_FACET);

                            DocumentModelList leafsDocuments = session.query(leafQuery);
                            if (CollectionUtils.isNotEmpty(leafsDocuments)) {
                                authorizedDocs.addAll(leafsDocuments);
                            }

                            for (DocumentModel doc : authorizedDocs) {
                                if (segmentService.supportsWebUrls(session, doc)) {
                                    setSegments(session, segmentService, doc);
                                }
                            }

                            session.save();

                        }
                    }
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
        if (StringUtils.isBlank(segment)) {
            generateSegment(session, segmentService, document);
        }
        if (session.hasChildren(document.getRef())) {
            DocumentModelList children = session.query(String.format(LIVES_CHILDREN, document.getId()));
            for (DocumentModel child : children) {
                setSegments(session, segmentService, child);
            }
        }

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
        ToutaticeDocumentHelper.saveDocumentSilently(session, document, true);

        // Check local proxy
        DocumentModel localProxy = ToutaticeDocumentHelper.getProxy(session, document, null);
        if (localProxy != null) {
            DocumentModel version = session.getSourceDocument(localProxy.getRef());
            version.putContextData(CoreSession.ALLOW_VERSION_WRITE, Boolean.TRUE);
            version.setPropertyValue(WebModeConstants.SEGMENT_PROPERTY, segment);
            ToutaticeDocumentHelper.saveDocumentSilently(session, version, ToutaticeSilentProcessRunnerHelper.DEFAULT_FILTERED_SERVICES_LIST, true);
        }
    }

}
