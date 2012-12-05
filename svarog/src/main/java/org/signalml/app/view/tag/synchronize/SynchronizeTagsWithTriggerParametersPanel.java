package org.signalml.app.view.tag.synchronize;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import org.signalml.app.model.tag.SlopeType;
import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import org.signalml.app.view.common.components.panels.ComponentWithLabel;
import org.signalml.app.view.common.components.panels.LabeledComponentsPanel;
import org.signalml.app.view.common.components.spinners.DoubleSpinner;

/**
 * A panel for selecting parameters for synchronizing tags with trigger.
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerParametersPanel extends LabeledComponentsPanel {

	private DoubleSpinner thresholdSpinner;
	private JComboBox channelComboBox;
	private JComboBox slopeSelectionComboBox;

	public SynchronizeTagsWithTriggerParametersPanel() {
		super(_("Parameters"));
	}

	@Override
	protected List<ComponentWithLabel> createComponents() {
		List<ComponentWithLabel> components = new ArrayList<ComponentWithLabel>();

		ComponentWithLabel componentWithLabel = new ComponentWithLabel(new JLabel(_("Signal threshold")), getThresholdSpinner());
		components.add(componentWithLabel);

		componentWithLabel = new ComponentWithLabel(new JLabel(_("Trigger channel")), getChannelComboBox());
		components.add(componentWithLabel);

		componentWithLabel = new ComponentWithLabel(new JLabel(_("Slope")), getSlopeSelectionComboBox());
		components.add(componentWithLabel);

		return components;
	}

	public DoubleSpinner getThresholdSpinner() {
		if (thresholdSpinner == null) {
			thresholdSpinner = new DoubleSpinner(new SpinnerNumberModel(1.0, -100.0, 10000.0, 0.01));
		}
		return thresholdSpinner;
	}

	public JComboBox getChannelComboBox() {
		if (channelComboBox == null) {
			channelComboBox = new JComboBox();
		}
		return channelComboBox;
	}

	public JComboBox getSlopeSelectionComboBox() {
		if (slopeSelectionComboBox == null) {
			slopeSelectionComboBox = new JComboBox(SlopeType.values());
		}
		return slopeSelectionComboBox;
	}

	@Override
	protected int getNumberOfColumns() {
		return 1;
	}

	public void fillPanelFromModel(SynchronizeTagsWithTriggerParameters model) {
		List<String> channelLabels = model.getChannelLabels();
		getChannelComboBox().setModel(new DefaultComboBoxModel(channelLabels.toArray(new String[0])));

		Pattern pattern = Pattern.compile("trig");
		for (int i = 0 ; i < channelLabels.size(); i++) {
			Matcher matcher = pattern.matcher(channelLabels.get(i));
			if (matcher.find()) {
				getChannelComboBox().setSelectedIndex(i);
				break;
			}
		}
	}

	public void fillModelFromDialog(SynchronizeTagsWithTriggerParameters model) {
		model.setThresholdValue(getThresholdSpinner().getValue());
		model.setTriggerChannel(getChannelComboBox().getSelectedIndex());
		model.setSlopeType((SlopeType) getSlopeSelectionComboBox().getSelectedItem());
	}

}
