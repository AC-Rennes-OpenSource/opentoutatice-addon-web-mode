/**
 * 
 */
package fr.toutatice.ecm.platform.web.fragments.initialization;

import org.nuxeo.ecm.core.repository.RepositoryInitializationHandler;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author david
 */
public class ConfigurationInitializationService extends DefaultComponent {

    private RepositoryInitializationHandler initializationHandler;

    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
        this.initializationHandler = new DefaultConfigurationInitializationHandler();
        this.initializationHandler.install();
    }

    @Override
    public void deactivate(ComponentContext context) {
        super.deactivate(context);
        if (this.initializationHandler != null) {
            this.initializationHandler.uninstall();
        }
    }

}
