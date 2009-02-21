/* MRUDRegistryEvent.java created 2007-09-21
 * 
 */

package org.signalml.app.document;

import java.util.EventObject;

/** MRUDRegistryEvent
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MRUDRegistryEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private MRUDEntry entry;
	private int index;
	private int inTypeIndex;
	
	public MRUDRegistryEvent(MRUDRegistry source, MRUDEntry entry, int index, int inTypeIndex) {
		super(source);
		this.entry = entry;
		this.index = index;
		this.inTypeIndex = inTypeIndex;
	}
	
	public DocumentManager getDocumentManager() {
		return (DocumentManager) source;
	}
		
	public MRUDEntry getEntry() {
		return entry;
	}

	public int getIndex() {
		return index;
	}

	public int getInTypeIndex() {
		return inTypeIndex;
	}	
	
}
