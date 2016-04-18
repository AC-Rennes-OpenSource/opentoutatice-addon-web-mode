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
 *   mberhaut1
 *    
 */
package fr.toutatice.ecm.platform.web.fragments;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.annotations.Scope;

import fr.toutatice.ecm.platform.core.constants.ExtendedSeamPrecedence;
import fr.toutatice.ecm.platform.web.forms.bean.SpacesConfigFormBean;

/**
 * SimpleForm is used for configuration icon selector to hold an UIComponent
 * the clientId of this component is used by the js scripts
 * 
 */
@Name("simpleForm")
@Scope(ScopeType.SESSION)
@Install(precedence = ExtendedSeamPrecedence.INHERIT_TOUTATICE)
@Roles({@Role(name = "genericStyleSimpleForm", scope = ScopeType.SESSION), 
        @Role(name = "listStyleSimpleForm", scope = ScopeType.SESSION),
        @Role(name = "templateSimpleForm", scope = ScopeType.SESSION)})
public class SimpleFormBean extends SpacesConfigFormBean {
    
}
