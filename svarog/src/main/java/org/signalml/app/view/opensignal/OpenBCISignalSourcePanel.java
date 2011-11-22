/* OpenBCISignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import org.signalml.app.config.preset.StyledTagSetPresetManager;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MultiplexerConnectionPanel;
import org.springframework.context.support.MessageSourceAccessor;
import org.signalml.app.view.element.MonitorRecordingPanel;
import org.signalml.app.model.OpenMonitorDescriptor;

/**
 * The panel for choosing setting a connection to an openBCI system and
 * setting parameters using which the signal should be opened.
 *
 * @author Piotr Szachewicz
 */
public class OpenBCISignalSourcePanel extends AbstractMonitorSourcePanel {

	/**
	 * The current OpenMonitorDescriptor.
	 */
	private OpenMonitorDescriptor currentModel;

	/**
	 * A panel used to connect or disconnect to a multiplexer.
	 */
	private MultiplexerConnectionPanel multiplexerConnectionPanel;

	/**
	 * A panel used to specify monitor recording options.
	 */
	private MonitorRecordingPanel monitorRecordingPanel;

	/**
	 * A panel used to specify parameters for the monitor to be opened.
	 */
	private SignalParametersPanelForOpenBCI signalParametersPanel;

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages
	 * @param viewerElementManager ViewerElementManager to be used by this
	 * panel
	 */
	public OpenBCISignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource, viewerElementManager);
	}

	@Override
	protected JPanel createLeftColumnPanel() {
		JPanel leftColumnPanel = new JPanel();
		leftColumnPanel.setLayout(new BorderLayout());
		leftColumnPanel.add(getMultiplexerConnectionPanel(), BorderLayout.NORTH);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(getTagPresetSelectionPanel(), BorderLayout.NORTH);

		leftColumnPanel.add(southPanel);
		return leftColumnPanel;
	}

	@Override
	protected JPanel createRightColumnPanel() {
		JPanel rightColumnPanel = new JPanel(new BorderLayout());
		rightColumnPanel.add(getSignalParametersPanel(), BorderLayout.CENTER);

		JPanel lowerPanel = new JPanel(new BorderLayout());
		lowerPanel.add(getEegSystemSelectionPanel(), BorderLayout.NORTH);
		lowerPanel.add(getMonitorRecordingPanel(), BorderLayout.SOUTH);
		rightColumnPanel.add(lowerPanel, BorderLayout.SOUTH);
		return rightColumnPanel;
	}

	/**
	 * Fills the components in this panel using the data contained in the
	 * given descriptor.
	 * @param descriptor descriptor to be used to filled this panel
	 */
        public void fillPanelFromModel(OpenMonitorDescriptor descriptor) {

		currentModel = descriptor;

		getMultiplexerConnectionPanel().fillPanelFromModel(currentModel);
		signalParametersPanel.fillPanelFromModel(currentModel);
		getEegSystemSelectionPanel().setEegSystem(getEegSystemSelectionPanel().getSelectedEegSystem());

        }

	/**
	 * Fills the given descriptor using the data set in the components
	 * contained in this panel.
	 * @param openMonitorDescriptor the descriptor to be filled
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		signalParametersPanel.fillModelFromPanel(descriptor);
		getMultiplexerConnectionPanel().fillModelFromPanel(descriptor);

		descriptor.setJmxClient(viewerElementManager.getJmxClient());
		descriptor.setTagClient(viewerElementManager.getTagClient());
                descriptor.setSignalSource(SignalSource.OPENBCI);
		descriptor.setEegSystem(getEegSystemSelectionPanel().getSelectedEegSystem());

		getMonitorRecordingPanel().fillModelFromPanel(descriptor);
		getTagPresetSelectionPanel().fillModelFromPanel(descriptor);
	}

	/**
	 * Returns the panel for connecting/disconnecting to the multiplexer.
	 * @return the panel for connecting/disconnecting to the multiplexer
	 */
	protected MultiplexerConnectionPanel getMultiplexerConnectionPanel() {
		if (multiplexerConnectionPanel == null) {
			multiplexerConnectionPanel = new MultiplexerConnectionPanel(viewerElementManager);
			multiplexerConnectionPanel.getConnectAction().addPropertyChangeListener(this);
			multiplexerConnectionPanel.getDisconnectAction().addPropertyChangeListener(this);
		}
		return multiplexerConnectionPanel;
	}

	/**
	 * Returns the panel for setting the parameters for the monitor to be opened.
	 * @return the panel for setting the parameters for the monitor to be opened
	 */
	public SignalParametersPanelForOpenBCI getSignalParametersPanel() {
		if (signalParametersPanel == null) {
			signalParametersPanel = new SignalParametersPanelForOpenBCI(messageSource);
			signalParametersPanel.addPropertyChangeListener(this);
		}
		return signalParametersPanel;
	}

	/**
	 * Returns the panel used to set the monitor recording options.
	 * @return the panel used to set the monitor recording options
	 */
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
			/* model was changed by the connectAction in the
			multiplexerConnectionPanel */
			fillPanelFromModel(currentModel);
			setConnected(true);
		}
		else if ("disconnected".equals(propertyName)) {
			setConnected(false);
		}
		else
			forwardPropertyChange(evt);
	}

	@Override
	public int getChannelCount() {
		return signalParametersPanel.getChannelCount();
	}

	@Override
	public float getSamplingFrequency() {
		return signalParametersPanel.getSamplingFrequency();
	}

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		getSignalParametersPanel().getSamplingFrequencyComboBox().setSelectedItem(samplingFrequency);
	}

}
