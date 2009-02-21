/* TagPaintMode.java created 2007-11-22
 * 
 */

package org.signalml.app.view.tag;

import org.springframework.context.MessageSourceResolvable;

/** TagPaintMode
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum TagPaintMode implements MessageSourceResolvable {
		
	OVERLAY,
	XOR,
	ALPHA_50,
	ALPHA_80
	
	;

	private final static Object[] ARGUMENTS = new Object[0];
	
	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "tagPaintMode." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

}
