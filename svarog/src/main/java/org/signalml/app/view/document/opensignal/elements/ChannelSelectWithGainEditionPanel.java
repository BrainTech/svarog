package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/**
 * A {@link ChannelSelectPanel} with additional button for editing
 * the gain and offset for channels.
 *
 * @author Piotr Szachewicz
 */
public class ChannelSelectWithGainEditionPanel extends ChannelSelectPanel {

	protected AbstractOpenSignalDescriptor openSignalDescriptor;
	private JButton editGainAndOffsetButton;
	private EditGainAndOffsetDialog editGainAndOffsetDialog;

	@Override
	protected JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new BorderLayout());

		JPanel selectAndClearButtonsPanel = super.createButtonsPanel();

		buttonsPanel.add(selectAndClearButtonsPanel, BorderLayout.WEST);
		buttonsPanel.add(createEditGainAndOffsetPanel(), BorderLayout.EAST);

		return buttonsPanel;
	}

	protected JPanel createEditGainAndOffsetPanel() {
		JPanel panel = new JPanel();
		panel.add(getEditGainAndOffsetButton());
		return panel;
	}

	/**
	 * Returns the edit gain and offset button.
	 *
	 * @return the edit gain and offset button
	 */
	protected JButton getEditGainAndOffsetButton() {

		if (editGainAndOffsetButton == null) {
			editGainAndOffsetButton = new JButton(new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					fillSignalParametersGainAndOffset(openSignalDescriptor);
					getEditGainAndOffsetDialog().showDialog(openSignalDescriptor, true);
				}
			});

			editGainAndOffsetButton.setText(_("Gain and offset…"));
			editGainAndOffsetButton.setEnabled(false);
		}
		return editGainAndOffsetButton;
	}

	/**
	 * Returns the edit gain and offset dialog
	 *
	 * @return the edit gain and offset dialog
	 */
	protected EditGainAndOffsetDialog getEditGainAndOffsetDialog() {

		if (editGainAndOffsetDialog == null) {
			editGainAndOffsetDialog = new EditGainAndOffsetDialog(null, true);
		}
		return editGainAndOffsetDialog;
	}

	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		this.openSignalDescriptor = openSignalDescriptor;

		super.fillPanelFromModel(openSignalDescriptor);
		setEnabledAsNeeded(openSignalDescriptor);
	}

	@Override
	public void fillModelFromPanel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		super.fillModelFromPanel(openSignalDescriptor);
		fillSignalParametersGainAndOffset(openSignalDescriptor);
	}

	protected void setEnabledAsNeeded(AbstractOpenSignalDescriptor openSignalDescriptor) {

		if (openSignalDescriptor == null) {
			getEditGainAndOffsetButton().setEnabled(false);
			return;
		}

		if (openSignalDescriptor instanceof RawSignalDescriptor) {
			getEditGainAndOffsetButton().setEnabled(true);
		}
		else {
			if (openSignalDescriptor instanceof SignalMLDescriptor)
				getEditGainAndOffsetButton().setEnabled(false);
			else
				getEditGainAndOffsetButton().setEnabled(true);
		}

	}

	protected void fillSignalParametersGainAndOffset(AbstractOpenSignalDescriptor openSignalDescriptor) {
		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;
			List<AmplifierChannel> channels = experimentDescriptor
											  .getAmplifier().getSelectedChannels();

			float[] gain = new float[channels.size()];
			float[] offset = new float[channels.size()];

			int i = 0;
			for (AmplifierChannel channel : channels) {
				gain[i] = channel.getCalibrationGain();
				offset[i] = channel.getCalibrationOffset();
				i++;
			}
			experimentDescriptor.getSignalParameters().setCalibrationGain(gain);
			experimentDescriptor.getSignalParameters().setCalibrationOffset(offset);
		}
	}

}
