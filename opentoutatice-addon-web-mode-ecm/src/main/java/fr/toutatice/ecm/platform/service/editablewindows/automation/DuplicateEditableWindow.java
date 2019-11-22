/*
 * (C) Copyright 2014 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * 
 * Contributors:
 * lbillon
 */
package fr.toutatice.ecm.platform.service.editablewindows.automation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.ecm.platform.service.editablewindows.EditableWindowService;

/**
 * Duplicate an editable window
 * 
 * @author lbillon
 */
@Operation(
        id = DuplicateEditableWindow.ID,
        category = Constants.CAT_DOCUMENT,
        label = "Duplicate an editable window",
        description = "Duplicate an editable window")
public class DuplicateEditableWindow {

    public static final String ID = "Document.DuplicateEditableWindow";

    @Context
    protected CoreSession session;

    @Param(name = "fromUri")
    protected String fromUri;


    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {

        EditableWindowService service = Framework.getService(EditableWindowService.class);
        
        service.duplicate(doc, fromUri);
        
        session.saveDocument(doc);

        return doc;
    }

}
