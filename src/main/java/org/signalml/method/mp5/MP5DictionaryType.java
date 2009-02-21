/* MP5DictionaryType.java created 2007-10-03
 * 
 */

package org.signalml.method.mp5;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/** MP5DictionaryType
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum MP5DictionaryType implements Serializable, MessageSourceResolvable {

	OCTAVE_FIXED( "OCTAVE_FIXED" ),
	OCTAVE_STOCH( "OCTAVE_STOCH" );
	
	private String name;

	private MP5DictionaryType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
		
	@Override
	public String toString() {
		return name;
	}	

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "mp5Method.dictionaryType." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}
	
	
}
