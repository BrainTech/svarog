/* VisualReferenceListener.java created 2007-11-30
 * 
 */

package org.signalml.app.view.montage;

import java.util.EventListener;

/** VisualReferenceListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface VisualReferenceListener extends EventListener {

	void sourceChannelsChanged( VisualReferenceEvent ev );
	
	void montageChannelsChanged( VisualReferenceEvent ev );
	
	void referenceChanged( VisualReferenceEvent ev );	

	void montageStructureChanged( VisualReferenceEvent ev );	
	
}
