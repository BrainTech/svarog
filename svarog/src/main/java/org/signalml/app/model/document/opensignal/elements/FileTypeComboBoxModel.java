package org.signalml.app.model.document.opensignal.elements;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.signalml.app.SvarogApplication;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecManagerEvent;
import org.signalml.codec.SignalMLCodecManagerListener;

/**
 * A {@link ComboBoxModel} for a combobox for selecting how a file should be opened:
 *<ul>
 * <li>AUTODETECT - autodetect the file type and open it automatically</li>
 * <li>ASCII - will try to load a file as a text (ASCII) signal file</li>
 * <li>RAW - will try to load a file as a RAW signal file
 * <li>EDF/EASYS/etc. - will use a {@link SignalMLCodec} to open the file.
 *</ul>
 *
 * @author Piotr Szachewicz
 */
public class FileTypeComboBoxModel extends AbstractListModel implements ComboBoxModel, SignalMLCodecManagerListener {

	private SignalMLCodecManager codecManager;
	private Object selectedItem;

	public FileTypeComboBoxModel() {
		this.codecManager = SvarogApplication.getSharedInstance().getSignalMLCodecManager();
		this.codecManager.addSignalMLCodecManagerListener(this);
	}

	@Override
	public Object getElementAt(int index) {
		switch (index) {
		case 0:
			return FileOpenSignalMethod.AUTODETECT;
		case 1:
			return FileOpenSignalMethod.RAW;
		case 2:
			return FileOpenSignalMethod.CSV;
		default:
			return codecManager.getCodecAt(index - 3);
		}
	}

	@Override
	public int getSize() {
		return codecManager.getCodecCount() + 3;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedItem = anItem;
	}

	@Override
	public void codecAdded(SignalMLCodecManagerEvent ev) {
		fireContentsChanged(this, 2, getSize());
	}

	@Override
	public void codecRemoved(SignalMLCodecManagerEvent ev) {
		fireContentsChanged(this, 2, getSize());
	}

	@Override
	public void codecsChanged(SignalMLCodecManagerEvent ev) {
		fireContentsChanged(this, 2, getSize());
	}

}
