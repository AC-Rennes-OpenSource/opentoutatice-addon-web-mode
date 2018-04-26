/**
 * 
 */
package fr.toutatice.ecm.platform.web.fragments.initialization;

import java.io.IOException;
import java.net.URL;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.blob.URLBlob;
import org.nuxeo.ecm.core.repository.RepositoryInitializationHandler;
import org.nuxeo.ecm.platform.filemanager.api.FileManager;
import org.nuxeo.runtime.api.Framework;


/**
 * @author david
 */
// TODO: implements reload too
public class DefaultConfigurationInitializationHandler extends RepositoryInitializationHandler {

    public static final String DEFAULT_FRAGMENTS_ZIP_PATH = "/configuration/default-objects-configuration.zip";
    public static final boolean OVERWRITE_FRAGMENTS = true;

    @Override
    public void doInitializeRepository(CoreSession session) {
        URL configLocation = this.getClass().getResource(DEFAULT_FRAGMENTS_ZIP_PATH);
        importConfiguration(configLocation, OVERWRITE_FRAGMENTS, session);
    }

    public void importConfiguration(URL configLocation, boolean overwrite, CoreSession session) {

        FileManager fileManager = Framework.getService(FileManager.class);

        Blob blob = new URLBlob(configLocation);
        String fullFileName = configLocation.getFile();

        DocumentModel doc;
        try {
            doc = fileManager.createDocumentFromBlob(session, blob, session.getRootDocument().getPathAsString(), OVERWRITE_FRAGMENTS, fullFileName);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }

        if (doc == null) {
            throw new NuxeoException("Can not import document " + fullFileName);
        }

        session.save();

    }

}
