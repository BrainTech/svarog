/* SignalColor.java created 2007-11-22
 * 
 */

package org.signalml.app.view.signal;

import java.awt.Color;

import org.springframework.context.MessageSourceResolvable;

/** SignalColor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SignalColor implements MessageSourceResolvable {
		
	BLACK( Color.BLACK ),
	RED( Color.RED ),
	GREEN( Color.GREEN.darker() ),
	BLUE( Color.BLUE )
	
	;

	private final static Object[] ARGUMENTS = new Object[0];
	
	private Color color;	
	
	private SignalColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalColor." + this.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return this.toString();
	}

}
