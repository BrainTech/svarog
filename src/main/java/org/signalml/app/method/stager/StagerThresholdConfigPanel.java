/* StagerThresholdConfigPanel.java created 2008-02-14
 *
 */
package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.element.AutoSpinnerWithSliderPanel;
import org.signalml.app.view.element.CompactButton;
import org.signalml.method.stager.StagerParameters;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerThresholdConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerThresholdConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
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

	public StagerThresholdConfigPanel(MessageSourceAccessor messageSource, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("stagerMethod.dialog.thresholdsTitle")),
		        new EmptyBorder(3,3,3,3)
		);
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

			Dimension fillerSize = new Dimension(1,1);
			Component filler1 = Box.createRigidArea(fillerSize);
			//Component filler3 = Box.createRigidArea(fillerSize);
			Component filler4 = Box.createRigidArea(fillerSize);
			Component filler5 = Box.createRigidArea(fillerSize);
			Component filler6 = Box.createRigidArea(fillerSize);

			JLabel emgToneThresholdLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.emgToneThreshold"));
			JLabel mtEegThresholdLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.mtEegThreshold"));
			JLabel mtEmgThresholdLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.mtEmgThreshold"));
			JLabel mtToneEmgThresholdLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.mtToneEmgThreshold"));
			JLabel remEogDeflectionThresholdLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.remEogDeflectionThreshold"));
			JLabel semEogDeflectionThresholdLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.semEogDeflectionThreshold"));

			Component glue1 = Box.createHorizontalGlue();
			Component glue2 = Box.createHorizontalGlue();
			Component glue3 = Box.createHorizontalGlue();
			Component glue4 = Box.createHorizontalGlue();
			Component glue5 = Box.createHorizontalGlue();
			Component glue6 = Box.createHorizontalGlue();

			CompactButton emgToneThresholdHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_EMG_TONE_THRESHOLD);
			CompactButton mtEegThresholdHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_MT_EEG_THRESHOLD);
			CompactButton mtEmgThresholdHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_MT_EMG_THRESHOLD);
			CompactButton mtToneEmgThresholdHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_MT_TONE_EMG_THRESHOLD);
			CompactButton remEogDeflectionThresholdHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_REM_EOG_DEFLECTION_THRESHOLD);
			CompactButton semEogDeflectionThresholdHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_SEM_EOG_DEFLECTION_THRESHOLD);

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(filler1)
			        .addComponent(getMtEegThresholdEnabledCheckBox())
			        .addComponent(getMtArtifactsThresholdEnabledCheckBox())
			        .addComponent(filler4)
			        .addComponent(filler5)
			        .addComponent(filler6)
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(emgToneThresholdLabel)
			        .addComponent(mtEegThresholdLabel)
			        .addComponent(mtEmgThresholdLabel)
			        .addComponent(mtToneEmgThresholdLabel)
			        .addComponent(remEogDeflectionThresholdLabel)
			        .addComponent(semEogDeflectionThresholdLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(glue1)
			        .addComponent(glue2)
			        .addComponent(glue3)
			        .addComponent(glue4)
			        .addComponent(glue5)
			        .addComponent(glue6)
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.TRAILING)
			        .addComponent(getEmgToneThresholdPanel())
			        .addComponent(getMtEegThresholdPanel())
			        .addComponent(getMtEmgThresholdPanel())
			        .addComponent(getMtToneEmgThresholdPanel())
			        .addComponent(getRemEogDeflectionThresholdPanel())
			        .addComponent(getSemEogDeflectionThresholdPanel())
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(emgToneThresholdHelpButton)
			        .addComponent(mtEegThresholdHelpButton)
			        .addComponent(mtEmgThresholdHelpButton)
			        .addComponent(mtToneEmgThresholdHelpButton)
			        .addComponent(remEogDeflectionThresholdHelpButton)
			        .addComponent(semEogDeflectionThresholdHelpButton)
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.CENTER)
			        .addComponent(filler1)
			        .addComponent(emgToneThresholdLabel)
			        .addComponent(glue1)
			        .addComponent(getEmgToneThresholdPanel())
			        .addComponent(emgToneThresholdHelpButton)
			);

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.CENTER)
			        .addComponent(getMtEegThresholdEnabledCheckBox())
			        .addComponent(mtEegThresholdLabel)
			        .addComponent(glue2)
			        .addComponent(getMtEegThresholdPanel())
			        .addComponent(mtEegThresholdHelpButton)
			);

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.CENTER)
			        .addComponent(getMtArtifactsThresholdEnabledCheckBox())
			        .addComponent(mtEmgThresholdLabel)
			        .addComponent(glue3)
			        .addComponent(getMtEmgThresholdPanel())
			        .addComponent(mtEmgThresholdHelpButton)
			);

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.CENTER)
			        .addComponent(filler4)
			        .addComponent(mtToneEmgThresholdLabel)
			        .addComponent(glue4)
			        .addComponent(getMtToneEmgThresholdPanel())
			        .addComponent(mtToneEmgThresholdHelpButton)
			);

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.CENTER)
			        .addComponent(filler5)
			        .addComponent(remEogDeflectionThresholdLabel)
			        .addComponent(glue5)
			        .addComponent(getRemEogDeflectionThresholdPanel())
			        .addComponent(remEogDeflectionThresholdHelpButton)
			);

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.CENTER)
			        .addComponent(filler6)
			        .addComponent(semEogDeflectionThresholdLabel)
			        .addComponent(glue6)
			        .addComponent(getSemEogDeflectionThresholdPanel())
			        .addComponent(semEogDeflectionThresholdHelpButton)
			);

			layout.setVerticalGroup(vGroup);

		}
		return thresholdsPanel;
	}

	public AutoSpinnerWithSliderPanel getEmgToneThresholdPanel() {
		if (emgToneThresholdPanel == null) {
			emgToneThresholdPanel = new AutoSpinnerWithSliderPanel(messageSource, StagerParameters.MIN_EMG_TONE_THRESHOLD, StagerParameters.MIN_EMG_TONE_THRESHOLD, StagerParameters.MAX_EMG_TONE_THRESHOLD, 1, false);
		}
		return emgToneThresholdPanel;
	}

	public SpinnerWithSliderPanel getMtEegThresholdPanel() {
		if (mtEegThresholdPanel == null) {
			mtEegThresholdPanel = new SpinnerWithSliderPanel(StagerParameters.MIN_MT_EEG_THRESHOLD, StagerParameters.MIN_MT_EEG_THRESHOLD, StagerParameters.MAX_MT_EEG_THRESHOLD, 1);
		}
		return mtEegThresholdPanel;
	}

	public JCheckBox getMtEegThresholdEnabledCheckBox() {
		if (mtEegThresholdEnabledCheckBox == null) {
			mtEegThresholdEnabledCheckBox = new JCheckBox();

			mtEegThresholdEnabledCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					getMtEegThresholdPanel().setEnabled(mtEegThresholdEnabledCheckBox.isSelected());

				}

			});

		}
		return mtEegThresholdEnabledCheckBox;
	}

	public JCheckBox getMtArtifactsThresholdEnabledCheckBox() {
		if (mtArtifactsThresholdEnabledCheckBox == null) {
			mtArtifactsThresholdEnabledCheckBox = new JCheckBox();

			mtArtifactsThresholdEnabledCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					getMtEmgThresholdPanel().setEnabled(mtArtifactsThresholdEnabledCheckBox.isSelected());
					getMtToneEmgThresholdPanel().setEnabled(mtArtifactsThresholdEnabledCheckBox.isSelected());

				}

			});

		}
		return mtArtifactsThresholdEnabledCheckBox;
	}


	public SpinnerWithSliderPanel getMtEmgThresholdPanel() {
		if (mtEmgThresholdPanel == null) {
			mtEmgThresholdPanel = new SpinnerWithSliderPanel(StagerParameters.MIN_MT_EMG_THRESHOLD, StagerParameters.MIN_MT_EMG_THRESHOLD, StagerParameters.MAX_MT_EMG_THRESHOLD, 1);
		}
		return mtEmgThresholdPanel;
	}

	public SpinnerWithSliderPanel getMtToneEmgThresholdPanel() {
		if (mtToneEmgThresholdPanel == null) {
			mtToneEmgThresholdPanel = new SpinnerWithSliderPanel(StagerParameters.MIN_MT_TONE_EMG_THRESHOLD, StagerParameters.MIN_MT_TONE_EMG_THRESHOLD, StagerParameters.MAX_MT_TONE_EMG_THRESHOLD, 1);
		}
		return mtToneEmgThresholdPanel;
	}

	public SpinnerWithSliderPanel getRemEogDeflectionThresholdPanel() {
		if (remEogDeflectionThresholdPanel == null) {
			remEogDeflectionThresholdPanel = new SpinnerWithSliderPanel(StagerParameters.MIN_REM_EOG_DEFLECTION_THRESHOLD, StagerParameters.MIN_REM_EOG_DEFLECTION_THRESHOLD, StagerParameters.MAX_REM_EOG_DEFLECTION_THRESHOLD, 1);
		}
		return remEogDeflectionThresholdPanel;
	}

	public SpinnerWithSliderPanel getSemEogDeflectionThresholdPanel() {
		if (semEogDeflectionThresholdPanel == null) {
			semEogDeflectionThresholdPanel = new SpinnerWithSliderPanel(StagerParameters.MIN_SEM_EOG_DEFLECTION_THRESHOLD, StagerParameters.MIN_SEM_EOG_DEFLECTION_THRESHOLD, StagerParameters.MAX_SEM_EOG_DEFLECTION_THRESHOLD, 1);
		}
		return semEogDeflectionThresholdPanel;
	}

	public void fillPanelFromParameters(StagerParameters parameters) {

		getEmgToneThresholdPanel().setValue(parameters.getEmgToneThreshold());
		getMtEegThresholdPanel().setValue(parameters.getMtEegThreshold());
		getMtEegThresholdEnabledCheckBox().setSelected(parameters.isMtEegThresholdEnabled());
		getMtEmgThresholdPanel().setValue(parameters.getMtEmgThreshold());
		getMtToneEmgThresholdPanel().setValue(parameters.getMtToneEmgThreshold());
		getMtArtifactsThresholdEnabledCheckBox().setSelected(parameters.isMtArtifactsThresholdEnabled());
		getRemEogDeflectionThresholdPanel().setValue(parameters.getRemEogDeflectionThreshold());
		getSemEogDeflectionThresholdPanel().setValue(parameters.getSemEogDeflectionThreshold());

	}

	public void fillParametersFromPanel(StagerParameters parameters) {

		parameters.setEmgToneThreshold(getEmgToneThresholdPanel().getValue());
		parameters.setMtEegThreshold(getMtEegThresholdPanel().getValue());
		parameters.setMtEegThresholdEnabled(getMtEegThresholdEnabledCheckBox().isSelected());
		parameters.setMtEmgThreshold(getMtEmgThresholdPanel().getValue());
		parameters.setMtToneEmgThreshold(getMtToneEmgThresholdPanel().getValue());
		parameters.setMtArtifactsThresholdEnabled(getMtArtifactsThresholdEnabledCheckBox().isSelected());
		parameters.setRemEogDeflectionThreshold(getRemEogDeflectionThresholdPanel().getValue());
		parameters.setSemEogDeflectionThreshold(getSemEogDeflectionThresholdPanel().getValue());

	}

	public void validatePanel(Errors errors) {

		// nothing to do

	}

}
