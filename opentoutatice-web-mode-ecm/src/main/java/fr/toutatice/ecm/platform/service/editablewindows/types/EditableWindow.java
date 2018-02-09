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
 *   lbillon
 *   dchevrier
 *   mberhaut1
 *    
 */
package fr.toutatice.ecm.platform.service.editablewindows.types;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.toutatice.ecm.platform.service.editablewindows.EwServiceException;


public interface EditableWindow {

    public String prepareCreation(DocumentModel doc, String uri, String region, String belowUri, String code2) throws EwServiceException;

}
