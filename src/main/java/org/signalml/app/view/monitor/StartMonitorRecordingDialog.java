/* StartMonitorRecordingDialog.java created 2010-11-03
 *
 */

package org.signalml.app.view.monitor;

import java.awt.Window;
import javax.swing.JComponent;

import org.signalml.app.model.MonitorRecordingDescriptor;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class StartMonitorRecordingDialog extends AbstractDialog {

	protected ChooseFilesForMonitorRecordingPanel chooseFilesForMonitorRecordingPanel;

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param messageSource message source to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public StartMonitorRecordingDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("startMonitorRecording.title"));
		super.initialize();
	}

	@Override
	protected JComponent createInterface() {
		return getChooseFilesForMonitorRecordingPanel();
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MonitorRecordingDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		getChooseFilesForMonitorRecordingPanel().fillDialogFromModel(model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		getChooseFilesForMonitorRecordingPanel().fillModelFromDialog(model);
	}

	public ChooseFilesForMonitorRecordingPanel getChooseFilesForMonitorRecordingPanel() {
		if (chooseFilesForMonitorRecordingPanel == null)
			chooseFilesForMonitorRecordingPanel = new ChooseFilesForMonitorRecordingPanel(messageSource);
		return chooseFilesForMonitorRecordingPanel;
	}

}
