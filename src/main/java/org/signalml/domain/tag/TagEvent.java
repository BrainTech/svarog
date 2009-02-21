/* TagEvent.java created 2007-10-01
 * 
 */

package org.signalml.domain.tag;

import java.util.EventObject;

/** TagEvent
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private Tag tag;
	private float affectedRegionStart;
	private float affectedRegionEnd;
	
	public TagEvent(Object source, Tag tag, float affectedRegionStart, float affectedRegionEnd) {
		super(source);
		this.tag = tag;
		this.affectedRegionStart = affectedRegionStart;
		this.affectedRegionEnd = affectedRegionEnd;
	}

	public Tag getTag() {
		return tag;
	}

	public float getAffectedRegionStart() {
		return affectedRegionStart;
	}

	public float getAffectedRegionEnd() {
		return affectedRegionEnd;
	}
		
}
