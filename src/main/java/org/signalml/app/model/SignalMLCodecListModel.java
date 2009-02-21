/* SignalMLCodecListModel.java created 2007-09-17
 * 
 */

package org.signalml.app.model;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecManagerEvent;
import org.signalml.codec.SignalMLCodecManagerListener;

/** SignalMLCodecListModel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLCodecListModel extends AbstractListModel implements ComboBoxModel, SignalMLCodecManagerListener {

	private static final long serialVersionUID = 1L;

	private SignalMLCodecManager codecManager;
	
	private Object selectedObject;
		
	public SignalMLCodecListModel() {
		super();
	}

	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		if( this.codecManager != codecManager ) {
			if( this.codecManager != null ) {
				this.codecManager.removeSignalMLCodecManagerListener(this);
			}
			this.codecManager = codecManager;
			if( codecManager != null ) {
				codecManager.addSignalMLCodecManagerListener(this);
			}
		}
	}
	
	@Override
	public int getSize() {
		return codecManager.getCodecCount();
	}
	
	@Override
	public Object getElementAt(int index) {
		return codecManager.getCodecAt(index);
	}

	@Override
	public Object getSelectedItem() {
		return selectedObject;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedObject = anItem;
	}

	@Override
	public void codecAdded(SignalMLCodecManagerEvent ev) {
		int index = ev.getIndex();
		fireIntervalAdded(this, index, index);		
	}

	@Override
	public void codecRemoved(SignalMLCodecManagerEvent ev) {
		int index = ev.getIndex();
		fireIntervalRemoved(this, index, index);		
	}

	@Override
	public void codecsChanged(SignalMLCodecManagerEvent ev) {
		int cnt = codecManager.getCodecCount();
		fireContentsChanged(this, 0, cnt-1);
	}
	
}
