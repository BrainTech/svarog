/* OpenBCISignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.document.opensignal.monitor;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.signalml.app.config.preset.StyledTagSetPresetManager;
import org.signalml.app.view.components.MonitorRecordingPanel;
import org.signalml.app.view.document.opensignal.SignalSource;

import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.OpenMonitorDescriptor;

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

	private ChooseExperimentPanel chooseExperimentPanel;
	private ChannelSelectPanel channelSelectPanel;
	
	/**
	 * A panel used to specify parameters for the monitor to be opened.
	 */
	private SignalParametersPanelForOpenBCI signalParametersPanel;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager to be used by this
	 * panel
	 */
	public OpenBCISignalSourcePanel(ViewerElementManager viewerElementManager) {
		super(viewerElementManager);
	}

	@Override
	protected JPanel createLeftColumnPanel() {
		JPanel leftColumnPanel = new JPanel();
		leftColumnPanel.setLayout(new BorderLayout());
		
		chooseExperimentPanel = new ChooseExperimentPanel();
		chooseExperimentPanel.addPropertyChangeListener(this);
		leftColumnPanel.add(chooseExperimentPanel, BorderLayout.NORTH);
		
		leftColumnPanel.add(getSignalParametersPanel(), BorderLayout.CENTER);
		
		return leftColumnPanel;
	}

	@Override
	protected JPanel createRightColumnPanel() {
		JPanel rightColumnPanel = new JPanel(new BorderLayout());
		channelSelectPanel = new ChannelSelectPanel();
		rightColumnPanel.add(channelSelectPanel, BorderLayout.CENTER);

		JPanel lowerPanel = new JPanel(new BorderLayout());
		lowerPanel.add(getTagPresetSelectionPanel(), BorderLayout.NORTH);
		lowerPanel.add(getEegSystemSelectionPanel(), BorderLayout.SOUTH);
		rightColumnPanel.add(lowerPanel, BorderLayout.SOUTH);
		return rightColumnPanel;
	}

	/**
	 * Fills the components in this panel using the data contained in the given
	 * descriptor.
	 * 
	 * @param descriptor
	 *            descriptor to be used to filled this panel
	 */
	public void fillPanelFromModel(OpenMonitorDescriptor descriptor) {

		currentModel = descriptor;
		//signalParametersPanel.fillPanelFromModel(currentModel);
		getEegSystemSelectionPanel().setEegSystem(
				getEegSystemSelectionPanel().getSelectedEegSystem());

	}

	/**
	 * Fills the given descriptor using the data set in the components
	 * contained in this panel.
	 * @param openMonitorDescriptor the descriptor to be filled
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		signalParametersPanel.fillModelFromPanel(descriptor);
		//getMultiplexerConnectionPanel().fillModelFromPanel(descriptor);

		descriptor.setJmxClient(viewerElementManager.getJmxClient());
		descriptor.setTagClient(viewerElementManager.getTagClient());
                descriptor.setSignalSource(SignalSource.OPENBCI);
		descriptor.setEegSystem(getEegSystemSelectionPanel().getSelectedEegSystem());

		getTagPresetSelectionPanel().fillModelFromPanel(descriptor);
	}

	/**
	 * Returns the panel for setting the parameters for the monitor to be opened.
	 * @return the panel for setting the parameters for the monitor to be opened
	 */
	public SignalParametersPanelForOpenBCI getSignalParametersPanel() {
		if (signalParametersPanel == null) {
			signalParametersPanel = new SignalParametersPanelForOpenBCI();
			signalParametersPanel.addPropertyChangeListener(this);
		}
		return signalParametersPanel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if (ChooseExperimentPanel.EXPERIMENT_SELECTED_PROPERTY.equals(propertyName)) {
			ExperimentDescriptor experiment = (ExperimentDescriptor) evt.getNewValue();
			channelSelectPanel.fillPanelFromModel(experiment);
			signalParametersPanel.fillPanelFromModel(experiment);
		}
		
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

	@Override
	public SignalSource getSignalSource() {
		return SignalSource.OPENBCI;
	}

}
