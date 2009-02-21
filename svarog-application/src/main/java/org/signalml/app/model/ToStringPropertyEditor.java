/* ToStringPropertyEditor.java created 2007-10-05
 * 
 */

package org.signalml.app.model;

import java.beans.PropertyEditorSupport;

/** ToStringPropertyEditor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ToStringPropertyEditor extends PropertyEditorSupport {
	
	public ToStringPropertyEditor(PropertyProvider subject) {
		super(subject);
	}

	@Override
	public String getAsText() {
		return getValue().toString();
	}
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		// do nothing
	}
	
}
