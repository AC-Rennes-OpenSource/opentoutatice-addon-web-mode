/**
 * 
 */
package fr.toutatice.ecm.platform.service.editablewindows.types;

import com.phloc.commons.collections.pair.IPair;


/**
 * @author david
 *
 */
public interface SpecificEditableWindow extends EditableWindow {

    /**
     * @return left: key of code2 value,
     *         right: xpath of parent property containing code2 property.
     * 
     */
    public IPair<String, String> getXPathInfosOfCode2();

}
