/* NewStagerThresholdConfigPanel.java created 2008-02-14
 *
 */
package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.ui.components.AutoSpinnerWithSliderPanel;
import org.signalml.plugin.newstager.ui.components.SpinnerWithSliderPanel;

/**
 * NewStagerThresholdConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerThresholdConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AbstractDialog owner;

	private JPanel thresholdsPanel;

	private AutoSpinnerWithSliderPanel emgToneThresholdPanel;
	private SpinnerWithSliderPanel mtEegThresholdPanel;
	private JCheckBox mtArtifactsThresholdEnabledCheckBox;
	private JCheckBox mtEegThresholdEnabledCheckBox;
	private SpinnerWithSliderPanel mtEmgThresholdPanel;
	private SpinnerWithSliderPanel mtToneEmgThresholdPanel;
	private SpinnerWithSliderPanel remEogDeflectionThresholdPanel;
	private SpinnerWithSliderPanel semEogDeflectionThresholdPanel;

	public NewStagerThresholdConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(new TitledBorder(
					_("Thresholds")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		add(getThresholdsPanel(), BorderLayout.NORTH);
		add(Box.createVerticalGlue(), BorderLayout.CENTER);

	}

	public JPanel getThresholdsPanel() {
		if (thresholdsPanel == null) {

			thresholdsPanel = new JPanel();

			GroupLayout layout = new GroupLayout(thresholdsPanel);
			thresholdsPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			Dimension fillerSize = new Dimension(1, 1);
			Component filler1 = Box.createRigidArea(fillerSize);
			// Component filler3 = Box.createRigidArea(fillerSize);
			Component filler4 = Box.createRigidArea(fillerSize);
			Component filler5 = Box.createRigidArea(fillerSize);
			Component filler6 = Box.createRigidArea(fillerSize);

			JLabel emgToneThresholdLabel = new JLabel(_("EMG tone"));
			JLabel mtEegThresholdLabel = new JLabel(_("MT artifacts in EEG"));
			JLabel mtEmgThresholdLabel = new JLabel(_("MT artifacts in EMG"));
			JLabel mtToneEmgThresholdLabel = new JLabel(
				_("MT artifacts in tone EMG"));
			JLabel remEogDeflectionThresholdLabel = new JLabel(
				_("EOG deflection for rapid eye movement [%]"));
			JLabel semEogDeflectionThresholdLabel = new JLabel(
				_("EOG deflection for slow eye movement [%]"));

			Component glue1 = Box.createHorizontalGlue();
			Component glue2 = Box.createHorizontalGlue();
			Component glue3 = Box.createHorizontalGlue();
			Component glue4 = Box.createHorizontalGlue();
			Component glue5 = Box.createHorizontalGlue();
			Component glue6 = Box.createHorizontalGlue();

			CompactButton emgToneThresholdHelpButton = SwingUtils
					.createFieldHelpButton(owner,
										   NewStagerMethodDialog.HELP_EMG_TONE_THRESHOLD);
			CompactButton mtEegThresholdHelpButton = SwingUtils
					.createFieldHelpButton(owner,
										   NewStagerMethodDialog.HELP_MT_EEG_THRESHOLD);
			CompactButton mtEmgThresholdHelpButton = SwingUtils
					.createFieldHelpButton(owner,
										   NewStagerMethodDialog.HELP_MT_EMG_THRESHOLD);
			CompactButton mtToneEmgThresholdHelpButton = SwingUtils
					.createFieldHelpButton(owner,
										   NewStagerMethodDialog.HELP_MT_TONE_EMG_THRESHOLD);
			CompactButton remEogDeflectionThresholdHelpButton = SwingUtils
					.createFieldHelpButton(
						owner,
						NewStagerMethodDialog.HELP_REM_EOG_DEFLECTION_THRESHOLD);
			CompactButton semEogDeflectionThresholdHelpButton = SwingUtils
					.createFieldHelpButton(
						owner,
						NewStagerMethodDialog.HELP_SEM_EOG_DEFLECTION_THRESHOLD);

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(layout.createParallelGroup().addComponent(filler1)
							.addComponent(getMtEegThresholdEnabledCheckBox())
							.addComponent(getMtArtifactsThresholdEnabledCheckBox())
							.addComponent(filler4).addComponent(filler5)
							.addComponent(filler6));

			hGroup.addGroup(layout.createParallelGroup()
							.addComponent(emgToneThresholdLabel)
							.addComponent(mtEegThresholdLabel)
							.addComponent(mtEmgThresholdLabel)
							.addComponent(mtToneEmgThresholdLabel)
							.addComponent(remEogDeflectionThresholdLabel)
							.addComponent(semEogDeflectionThresholdLabel));

			hGroup.addGroup(layout.createParallelGroup().addComponent(glue1)
							.addComponent(glue2).addComponent(glue3)
							.addComponent(glue4).addComponent(glue5)
							.addComponent(glue6));

			hGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING)
							.addComponent(getEmgToneThresholdPanel())
							.addComponent(getMtEegThresholdPanel())
							.addComponent(getMtEmgThresholdPanel())
							.addComponent(getMtToneEmgThresholdPanel())
							.addComponent(getRemEogDeflectionThresholdPanel())
							.addComponent(getSemEogDeflectionThresholdPanel()));

			hGroup.addGroup(layout.createParallelGroup()
							.addComponent(emgToneThresholdHelpButton)
							.addComponent(mtEegThresholdHelpButton)
							.addComponent(mtEmgThresholdHelpButton)
							.addComponent(mtToneEmgThresholdHelpButton)
							.addComponent(remEogDeflectionThresholdHelpButton)
							.addComponent(semEogDeflectionThresholdHelpButton));

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
							.addComponent(filler1).addComponent(emgToneThresholdLabel)
							.addComponent(glue1)
							.addComponent(getEmgToneThresholdPanel())
							.addComponent(emgToneThresholdHelpButton));

			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
							.addComponent(getMtEegThresholdEnabledCheckBox())
							.addComponent(mtEegThresholdLabel).addComponent(glue2)
							.addComponent(getMtEegThresholdPanel())
							.addComponent(mtEegThresholdHelpButton));

			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
							.addComponent(getMtArtifactsThresholdEnabledCheckBox())
							.addComponent(mtEmgThresholdLabel).addComponent(glue3)
							.addComponent(getMtEmgThresholdPanel())
							.addComponent(mtEmgThresholdHelpButton));

			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
							.addComponent(filler4)
							.addComponent(mtToneEmgThresholdLabel).addComponent(glue4)
							.addComponent(getMtToneEmgThresholdPanel())
							.addComponent(mtToneEmgThresholdHelpButton));

			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
							.addComponent(filler5)
							.addComponent(remEogDeflectionThresholdLabel)
							.addComponent(glue5)
							.addComponent(getRemEogDeflectionThresholdPanel())
							.addComponent(remEogDeflectionThresholdHelpButton));

			vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
							.addComponent(filler6)
							.addComponent(semEogDeflectionThresholdLabel)
							.addComponent(glue6)
							.addComponent(getSemEogDeflectionThresholdPanel())
							.addComponent(semEogDeflectionThresholdHelpButton));

			layout.setVerticalGroup(vGroup);

		}
		return thresholdsPanel;
	}

	public AutoSpinnerWithSliderPanel getEmgToneThresholdPanel() {
		if (emgToneThresholdPanel == null) {
			emgToneThresholdPanel = new AutoSpinnerWithSliderPanel(
				NewStagerConstants.MIN_EMG_TONE_THRESHOLD,
				NewStagerConstants.MIN_EMG_TONE_THRESHOLD,
				NewStagerConstants.MAX_EMG_TONE_THRESHOLD, 1, false);
		}
		return emgToneThresholdPanel;
	}

	public SpinnerWithSliderPanel getMtEegThresholdPanel() {
		if (mtEegThresholdPanel == null) {
			mtEegThresholdPanel = new SpinnerWithSliderPanel(
				NewStagerConstants.MIN_MT_EEG_THRESHOLD,
				NewStagerConstants.MIN_MT_EEG_THRESHOLD,
				NewStagerConstants.MAX_MT_EEG_THRESHOLD, 1);
		}
		return mtEegThresholdPanel;
	}

	public JCheckBox getMtEegThresholdEnabledCheckBox() {
		if (mtEegThresholdEnabledCheckBox == null) {
			mtEegThresholdEnabledCheckBox = new JCheckBox();

			mtEegThresholdEnabledCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					getMtEegThresholdPanel().setEnabled(
						mtEegThresholdEnabledCheckBox.isSelected());

				}

			});

		}
		return mtEegThresholdEnabledCheckBox;
	}

	public JCheckBox getMtArtifactsThresholdEnabledCheckBox() {
		if (mtArtifactsThresholdEnabledCheckBox == null) {
			mtArtifactsThresholdEnabledCheckBox = new JCheckBox();

			mtArtifactsThresholdEnabledCheckBox
			.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					getMtEmgThresholdPanel().setEnabled(
						mtArtifactsThresholdEnabledCheckBox
						.isSelected());
					getMtToneEmgThresholdPanel().setEnabled(
						mtArtifactsThresholdEnabledCheckBox
						.isSelected());

				}

			});

		}
		return mtArtifactsThresholdEnabledCheckBox;
	}

	public SpinnerWithSliderPanel getMtEmgThresholdPanel() {
		if (mtEmgThresholdPanel == null) {
			mtEmgThresholdPanel = new SpinnerWithSliderPanel(
				NewStagerConstants.MIN_MT_EMG_THRESHOLD,
				NewStagerConstants.MIN_MT_EMG_THRESHOLD,
				NewStagerConstants.MAX_MT_EMG_THRESHOLD, 1);
		}
		return mtEmgThresholdPanel;
	}

	public SpinnerWithSliderPanel getMtToneEmgThresholdPanel() {
		if (mtToneEmgThresholdPanel == null) {
			mtToneEmgThresholdPanel = new SpinnerWithSliderPanel(
				NewStagerConstants.MIN_MT_TONE_EMG_THRESHOLD,
				NewStagerConstants.MIN_MT_TONE_EMG_THRESHOLD,
				NewStagerConstants.MAX_MT_TONE_EMG_THRESHOLD, 1);
		}
		return mtToneEmgThresholdPanel;
	}

	public SpinnerWithSliderPanel getRemEogDeflectionThresholdPanel() {
		if (remEogDeflectionThresholdPanel == null) {
			remEogDeflectionThresholdPanel = new SpinnerWithSliderPanel(
				NewStagerConstants.MIN_REM_EOG_DEFLECTION_THRESHOLD,
				NewStagerConstants.MIN_REM_EOG_DEFLECTION_THRESHOLD,
				NewStagerConstants.MAX_REM_EOG_DEFLECTION_THRESHOLD, 1);
		}
		return remEogDeflectionThresholdPanel;
	}

	public SpinnerWithSliderPanel getSemEogDeflectionThresholdPanel() {
		if (semEogDeflectionThresholdPanel == null) {
			semEogDeflectionThresholdPanel = new SpinnerWithSliderPanel(
				NewStagerConstants.MIN_SEM_EOG_DEFLECTION_THRESHOLD,
				NewStagerConstants.MIN_SEM_EOG_DEFLECTION_THRESHOLD,
				NewStagerConstants.MAX_SEM_EOG_DEFLECTION_THRESHOLD, 1);
		}
		return semEogDeflectionThresholdPanel;
	}

	public void fillPanelFromParameters(NewStagerParameters parameters) {

		NewStagerParameterThresholds thresholds = parameters.thresholds;

		getEmgToneThresholdPanel().setValue(thresholds.toneEMG);
		getMtEegThresholdPanel().setValue(thresholds.montageEEGThreshold);
		getMtEmgThresholdPanel().setValue(thresholds.montageEMGThreshold);
		getMtToneEmgThresholdPanel().setValue(
			thresholds.montageToneEMGThreshold);

		getRemEogDeflectionThresholdPanel().setValue(
			thresholds.remEogDeflectionThreshold);
		getSemEogDeflectionThresholdPanel().setValue(
			thresholds.semEogDeflectionThreshold);

		getMtEegThresholdEnabledCheckBox().setSelected(
			parameters.analyseEEGChannelsFlag);
		getMtArtifactsThresholdEnabledCheckBox().setSelected(
			parameters.analyseEMGChannelFlag);
	}

	public void fillParametersFromPanel(NewStagerParameters parameters) {

		NewStagerParameterThresholds thresholds = parameters.thresholds;

		thresholds.toneEMG = getEmgToneThresholdPanel().getValue();
		thresholds.montageEEGThreshold = getMtEegThresholdPanel().getValue();
		thresholds.montageEMGThreshold = getMtEmgThresholdPanel().getValue();
		thresholds.montageToneEMGThreshold = getMtToneEmgThresholdPanel()
											 .getValue();

		thresholds.remEogDeflectionThreshold = getRemEogDeflectionThresholdPanel()
											   .getValue();
		thresholds.semEogDeflectionThreshold = getSemEogDeflectionThresholdPanel()
											   .getValue();

		parameters.analyseEEGChannelsFlag = getMtEegThresholdEnabledCheckBox()
											.isSelected();
		parameters.analyseEMGChannelFlag = getMtArtifactsThresholdEnabledCheckBox()
										   .isSelected();
	}

	public void validatePanel(ValidationErrors errors) {
		// nothing to do
	}

}
