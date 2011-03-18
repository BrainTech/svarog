/* OpenBCISignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MultiplexerConnectionPanel;
import org.springframework.context.support.MessageSourceAccessor;
import org.signalml.app.view.element.MonitorRecordingPanel;
import org.signalml.app.model.OpenMonitorDescriptor;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenBCISignalSourcePanel extends AbstractMonitorSourcePanel {

	private OpenMonitorDescriptor currentModel;

	private MultiplexerConnectionPanel multiplexerConnectionPanel;
	private MonitorRecordingPanel monitorRecordingPanel = null;
	private SignalParametersPanelForOpenMonitor signalParametersPanel;

	public OpenBCISignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource, viewerElementManager);
	}

	@Override
	protected JPanel createLeftColumnPanel() {
		JPanel leftColumnPanel = new JPanel();
		leftColumnPanel.setLayout(new BorderLayout());
		leftColumnPanel.add(getMultiplexerConnectionPanel(), BorderLayout.NORTH);
		return leftColumnPanel;
	}

	@Override
	protected JPanel createRightColumnPanel() {
		JPanel rightColumnPanel = new JPanel(new BorderLayout());
		rightColumnPanel.add(getSignalParametersPanel(), BorderLayout.CENTER);
		rightColumnPanel.add(getMonitorRecordingPanel(), BorderLayout.SOUTH);
		return rightColumnPanel;
	}

        public void fillPanelFromModel(OpenMonitorDescriptor descriptor) {

		currentModel = descriptor;

		getMultiplexerConnectionPanel().fillPanelFromModel(currentModel);
		signalParametersPanel.fillPanelFromModel(currentModel);
		getMonitorRecordingPanel().setEnabledAll(true);
        }

	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		signalParametersPanel.fillModelFromPanel(descriptor);
		getMultiplexerConnectionPanel().fillModelFromPanel(descriptor);

		descriptor.setJmxClient(viewerElementManager.getJmxClient());
		descriptor.setTagClient(viewerElementManager.getTagClient());
	}

	protected MultiplexerConnectionPanel getMultiplexerConnectionPanel() {
		if (multiplexerConnectionPanel == null) {
			multiplexerConnectionPanel = new MultiplexerConnectionPanel(viewerElementManager);
			multiplexerConnectionPanel.getConnectAction().addPropertyChangeListener(this);
			multiplexerConnectionPanel.getDisconnectAction().addPropertyChangeListener(this);
		}
		return multiplexerConnectionPanel;
	}

	public SignalParametersPanelForOpenMonitor getSignalParametersPanel() {
		if (signalParametersPanel == null) {
			signalParametersPanel = new SignalParametersPanelForOpenMonitor(messageSource, viewerElementManager.getApplicationConfig());
			signalParametersPanel.addPropertyChangeListener(this);
		}
		return signalParametersPanel;
	}

	protected MonitorRecordingPanel getMonitorRecordingPanel() {
		if (monitorRecordingPanel == null) {
			monitorRecordingPanel = new MonitorRecordingPanel(messageSource);
		}
		return monitorRecordingPanel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if ("metadataRetrieved".equals(propertyName)) {
			System.out.println("Metadata retrieved");
			/* model was changed by the connectAction in the
			multiplexerConnectionPanel */
			fillPanelFromModel(currentModel);
			setConnected(true);
		}
		else if ("disconnected".equals(propertyName)) {
			setConnected(false);
			/*try {
				OpenMonitorDescriptor m = ((OpenMonitorDescriptor) getCurrentModel());
				m.setSamplingFrequency(null);
				m.setChannelCount(null);
				m.setChannelLabels(null);


				fillDialogFromModel(getCurrentModel());
			} catch (SignalMLException ex) {
				Logger.getLogger(OpenMonitorDialog.class.getName()).log(Level.SEVERE, null, ex);
			}*/
		}
		else
			super.propertyChange(evt);
	}

	@Override
	public int getChannelCount() {
		return signalParametersPanel.getChannelCount();
	}

	@Override
	public float getSamplingFrequency() {
		return signalParametersPanel.getSamplingFrequency();
	}

}
