/**
 *
 */
package fr.toutatice.ecm.platform.web.mode.bean;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;

import fr.toutatice.ecm.platform.core.constants.ExtendedSeamPrecedence;


/**
 * @author david
 *
 */
@Name("webModeValidator")
@Scope(ScopeType.SESSION)
@Install(precedence = ExtendedSeamPrecedence.TOUTATICE)
public class WebModeValidatorBean implements Serializable {

    private static final long serialVersionUID = 5623376301895535136L;

    private static final Pattern patternSegment = Pattern.compile("([a-zA-Z_0-9\\-]+)");

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = true)
    protected NavigationContext navigationContext;


    public void validateSegment(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if(StringUtils.isNotBlank((String) value)){
            Matcher m = patternSegment.matcher((String) value);
            if (!m.matches()) {
                String msg = ComponentUtils.translate(context, "webmode.validator.malformed.segment");
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
                throw new ValidatorException(message);
            }
            if (!validateUnicity((String) value)) {
                String msg = ComponentUtils.translate(context, "webmode.validator.existing.segment");
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
                throw new ValidatorException(message);
            }
        }
    }


    private boolean validateUnicity(String value) {

        final DocumentModel doc = navigationContext.getCurrentDocument();

        final DocumentModelList siblings = documentManager.getChildren(doc.getParentRef());

        if (siblings != null) {
            for (DocumentModel documentModel : siblings) {
                if (!StringUtils.startsWith(documentModel.getPathAsString(), doc.getPathAsString())) {
                    final String segmentUrl = (String) documentModel.getPropertyValue("ottcweb:segment");
                    if (StringUtils.equals(value, segmentUrl)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
