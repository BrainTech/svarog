/* TagStyleEvent.java created 2007-10-01
 * 
 */

package org.signalml.domain.tag;

import java.util.EventObject;

/** TagStyleEvent
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private TagStyle tagStyle;
	private int inTypeIndex;
	
	public TagStyleEvent(Object source, TagStyle tagStyle, int inTypeIndex) {
		super(source);
		this.tagStyle = tagStyle;
		this.inTypeIndex = inTypeIndex;
	}

	public TagStyle getTagStyle() {
		return tagStyle;
	}

	public int getInTypeIndex() {
		return inTypeIndex;
	}	
	
}
