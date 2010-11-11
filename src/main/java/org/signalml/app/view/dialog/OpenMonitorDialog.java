/* OpenMonitorDialog.java created 2010-11-09
 *
 */
package org.signalml.app.view.dialog;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MonitorChannelSelectPanel;
import org.signalml.app.view.element.MonitorRecordingPanel;
import org.signalml.app.view.element.MonitorSignalParametersPanel;
import org.signalml.app.view.element.MultiplexerConnectionPanel;
import org.signalml.app.worker.WorkerResult;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenMonitorDialog extends AbstractDialog implements PropertyChangeListener {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 10;
	public static final Color SUCCESS_COLOR = Color.GREEN;
	public static final Color FAILURE_COLOR = Color.RED;
	private ApplicationConfiguration applicationConfig;
	private ViewerElementManager viewerElementManager = null;
	private MultiplexerConnectionPanel multiplexerConnectionPanel = null;
	private MonitorSignalParametersPanel monitorSignalParametersPanel = null;
	private MonitorChannelSelectPanel monitorChannelSelectPanel = null;
	private MonitorRecordingPanel monitorRecordingPanel = null;

	public OpenMonitorDialog(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource);
		initialize(viewerElementManager);
	}

	public OpenMonitorDialog(MessageSourceAccessor messageSource,
		ViewerElementManager viewerElementManager,
		Window f,
		boolean isModal) {
		super(messageSource, f, isModal);
		initialize(viewerElementManager);
	}

	private void initialize(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		this.setTitle(messageSource.getMessage("openMonitor.title"));
	}

	@Override
	protected JComponent createInterface() {
		JPanel interfacePanel = new JPanel(new GridLayout(2, 2, 10, 10));

		interfacePanel.add(getMultiplexerConnectionPanel());
		interfacePanel.add(getMonitorSignalParametersPanel());
		interfacePanel.add(getMonitorChannelSelectPanel());
		interfacePanel.add(getMonitorRecordingPanel());

		return interfacePanel;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenMonitorDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		OpenMonitorDescriptor openMonitorDescriptor = (OpenMonitorDescriptor) model;

		getMultiplexerConnectionPanel().fillPanelFromModel(openMonitorDescriptor);

		getMonitorSignalParametersPanel().fillPanelFromModel(openMonitorDescriptor);
		getMonitorChannelSelectPanel().fillPanelFromModel(openMonitorDescriptor);

		if (viewerElementManager.getJmxClient() == null) {
			getOkButton().setEnabled(false);
		} else {
			getOkButton().setEnabled(true);
		}

		/*String fileName = m.getFileName();
		if (fileName == null)
		fileName = applicationConfig.getSignalRecorderFileName();
		getFileSelectPanel().setFileName(fileName);*/
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		OpenMonitorDescriptor m = (OpenMonitorDescriptor) model;

		/*String fileName = getFileSelectPanel().getFileName();
		if (fileName.endsWith( ".raw") || fileName.endsWith( ".xml"))
		fileName = fileName.substring( 0, fileName.length() - 4);
		m.setFileName( fileName);*/

		getMultiplexerConnectionPanel().fillModelFromPanel(m);

		m.setJmxClient(viewerElementManager.getJmxClient());
		m.setTagClient(viewerElementManager.getTagClient());

		getMonitorSignalParametersPanel().fillModelFromPanel(m);

		getMonitorChannelSelectPanel().fillModelFromPanel(m);

		getMonitorRecordingPanel().fillModelFromPanel(m);
		/*

		try {
		m.setSelectedChannelList( getChannelSelectPanel().getChannelList().getSelectedValues());
		}
		catch (Exception e) {
		throw new SignalMLException( e);
		}*/
	}

	protected MultiplexerConnectionPanel getMultiplexerConnectionPanel() {
		if (multiplexerConnectionPanel == null) {
			multiplexerConnectionPanel = new MultiplexerConnectionPanel(viewerElementManager, applicationConfig);
			multiplexerConnectionPanel.getConnectAction().addPropertyChangeListener(this);
			multiplexerConnectionPanel.getDisconnectAction().addPropertyChangeListener(this);
		}
		return multiplexerConnectionPanel;
	}

	protected MonitorSignalParametersPanel getMonitorSignalParametersPanel() {
		if (monitorSignalParametersPanel == null) {
			monitorSignalParametersPanel = new MonitorSignalParametersPanel(messageSource, applicationConfig);
		}
		return monitorSignalParametersPanel;
	}

	protected MonitorChannelSelectPanel getMonitorChannelSelectPanel() {
		if (monitorChannelSelectPanel == null) {
			monitorChannelSelectPanel = new MonitorChannelSelectPanel(messageSource);
		}
		return monitorChannelSelectPanel;
	}

	protected MonitorRecordingPanel getMonitorRecordingPanel() {
		if (monitorRecordingPanel == null) {
			monitorRecordingPanel = new MonitorRecordingPanel(messageSource);
		}
		return monitorRecordingPanel;
	}

	public void cancelConnection() {
		getMultiplexerConnectionPanel().cancel();
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
		getMultiplexerConnectionPanel().setApplicationConfiguration(applicationConfig);
		getMonitorSignalParametersPanel().setApplicationConfiguration(applicationConfig);
	}

	/**
	 * Updates this dialog in reponse to changes in the connection status.
	 *
	 * @param evt
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if ("metadataRetrieved".equals(propertyName)) {
			System.out.println("Metadata retrieved");
			try {
				fillDialogFromModel(getCurrentModel());
			} catch (SignalMLException ex) {
				Logger.getLogger(OpenMonitorDialog.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		else if ("disconnected".equals(propertyName)) {
			try {
				OpenMonitorDescriptor m = ((OpenMonitorDescriptor) getCurrentModel());
				m.setSamplingFrequency(null);
				m.setChannelCount(null);
				m.setChannelLabels(null);


				fillDialogFromModel(getCurrentModel());
			} catch (SignalMLException ex) {
				Logger.getLogger(OpenMonitorDialog.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		fillModelFromDialog(getCurrentModel());

		OpenMonitorDescriptor openMonitorDescriptor = (OpenMonitorDescriptor) model;

		if (openMonitorDescriptor.getSelectedChannelList() == null || openMonitorDescriptor.getSelectedChannelList().length == 0) {
			errors.reject("error.openMonitor.noChannelsSelected");
		}

		getMonitorRecordingPanel().validatePanel(model, errors);

	}
}
