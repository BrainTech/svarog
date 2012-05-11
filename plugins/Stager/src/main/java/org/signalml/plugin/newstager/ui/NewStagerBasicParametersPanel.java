/* NewStagerBasicParametersPanel.java created 2008-02-14
 *
 */
package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.components.CompactButton;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerRules;
import org.signalml.plugin.newstager.ui.components.AutoSpinnerPanel;

/**
 * NewStagerBasicParametersPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerBasicParametersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AbstractDialog owner;

	private JComboBox rulesComboBox;

	private AutoSpinnerPanel deltaMinAmplitudePanel;
	private AutoSpinnerPanel alphaMinAmplitudePanel;
	private AutoSpinnerPanel spindleMinAmplitudePanel;

	private JCheckBox primaryHypnogramCheckBox;

	public NewStagerBasicParametersPanel(AbstractDialog owner,
										 NewStagerAdvancedConfigObservable advancedConfigObservable) {
		super();
		this.owner = owner;
		initialize();

		final NewStagerAdvancedConfigObservable observable = advancedConfigObservable;
		advancedConfigObservable.addObserver(new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				boolean flag = observable.getEnabled();

				getDeltaMinAmplitudePanel().setEnabled(flag);
				getAlphaMinAmplitudePanel().setEnabled(flag);
				getSpindleMinAmplitudePanel().setEnabled(flag);
			}
		});
	}

	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(new TitledBorder(
					_("Key parameters")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel rulesLabel = new JLabel(_("Scoring criteria"));
		JLabel deltaMinAmplitudeLabel = new JLabel(
			_("Amplitude threshold for delta waves [uV]"));
		JLabel alphaMinAmplitudeLabel = new JLabel(
			_("Amplitude threshold for alpha waves [uV]"));
		JLabel spindleMinAmplitudeLabel = new JLabel(
			_("Amplitude threshold for sleep spindles [uV]"));
		JLabel primaryHypnogramLabel = new JLabel(
			_("Show primary hypnogram and markers of waveforms in result"));
		primaryHypnogramLabel.setMinimumSize(new Dimension(25, 35));
		primaryHypnogramLabel.setVerticalAlignment(JLabel.CENTER);

		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();
		Component glue3 = Box.createHorizontalGlue();
		Component glue4 = Box.createHorizontalGlue();
		Component glue5 = Box.createHorizontalGlue();

		CompactButton rulesHelpButton = SwingUtils.createFieldHelpButton(owner,
										NewStagerMethodDialog.HELP_RULES);
		CompactButton deltaMinAmplitudeHelpButton = SwingUtils
				.createFieldHelpButton(owner,
									   NewStagerMethodDialog.HELP_DELTA_MIN_AMPLITUDE);
		CompactButton alphaMinAmplitudeHelpButton = SwingUtils
				.createFieldHelpButton(owner,
									   NewStagerMethodDialog.HELP_ALPHA_MIN_AMPLITUDE);
		CompactButton spindleMinAmplitudeHelpButton = SwingUtils
				.createFieldHelpButton(owner,
									   NewStagerMethodDialog.HELP_SPINDLE_MIN_AMPLITUDE);
		CompactButton primaryHypnogramHelpButton = SwingUtils
				.createFieldHelpButton(owner,
									   NewStagerMethodDialog.HELP_PRIMARY_HYPNOGRAM);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(rulesLabel)
						.addComponent(deltaMinAmplitudeLabel)
						.addComponent(alphaMinAmplitudeLabel)
						.addComponent(spindleMinAmplitudeLabel)
						.addComponent(primaryHypnogramLabel));

		hGroup.addGroup(layout.createParallelGroup().addComponent(glue1)
						.addComponent(glue2).addComponent(glue3).addComponent(glue4)
						.addComponent(glue5));

		hGroup.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addComponent(getRulesComboBox())
						.addComponent(getDeltaMinAmplitudePanel())
						.addComponent(getAlphaMinAmplitudePanel())
						.addComponent(getSpindleMinAmplitudePanel())
						.addComponent(getPrimaryHypnogramCheckBox()));

		hGroup.addGroup(layout.createParallelGroup()
						.addComponent(rulesHelpButton)
						.addComponent(deltaMinAmplitudeHelpButton)
						.addComponent(alphaMinAmplitudeHelpButton)
						.addComponent(spindleMinAmplitudeHelpButton)
						.addComponent(primaryHypnogramHelpButton));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(rulesLabel).addComponent(glue1)
						.addComponent(getRulesComboBox()).addComponent(rulesHelpButton));

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(deltaMinAmplitudeLabel).addComponent(glue2)
						.addComponent(getDeltaMinAmplitudePanel())
						.addComponent(deltaMinAmplitudeHelpButton));

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(alphaMinAmplitudeLabel).addComponent(glue3)
						.addComponent(getAlphaMinAmplitudePanel())
						.addComponent(alphaMinAmplitudeHelpButton));

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(spindleMinAmplitudeLabel).addComponent(glue4)
						.addComponent(getSpindleMinAmplitudePanel())
						.addComponent(spindleMinAmplitudeHelpButton));

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(primaryHypnogramLabel).addComponent(glue5)
						.addComponent(getPrimaryHypnogramCheckBox())
						.addComponent(primaryHypnogramHelpButton));

		layout.setVerticalGroup(vGroup);

	}

	public JComboBox getRulesComboBox() {
		if (rulesComboBox == null) {
			rulesComboBox = new JComboBox();

			rulesComboBox.setRenderer(new DefaultListCellRenderer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
					try {
						NewStagerRules rulesValue = (NewStagerRules) value;
						switch (rulesValue) {
						case RK:
							value = _("Rechtshaffen and Kales (R&K 1967) rules");
							break;
						case AASM:
							value = _("AASM (2007) rules");
							break;
						default:
							;
						}
					} catch (ClassCastException e) {
						// do nothing
					}

					return super.getListCellRendererComponent(list, value,
							index, isSelected, cellHasFocus);
				}

			});

			rulesComboBox.setModel(new DefaultComboBoxModel(
									   NewStagerRules.values()));
		}
		return rulesComboBox;
	}

	public AutoSpinnerPanel getDeltaMinAmplitudePanel() {
		if (deltaMinAmplitudePanel == null) {
			deltaMinAmplitudePanel = new AutoSpinnerPanel(
				NewStagerConstants.MIN_AMPLITUDE,
				NewStagerConstants.MIN_AMPLITUDE,
				NewStagerConstants.MAX_AMPLITUDE,
				NewStagerConstants.INCR_AMPLITUDE, false);
		}
		return deltaMinAmplitudePanel;
	}

	public AutoSpinnerPanel getAlphaMinAmplitudePanel() {
		if (alphaMinAmplitudePanel == null) {
			alphaMinAmplitudePanel = new AutoSpinnerPanel(
				NewStagerConstants.MIN_AMPLITUDE,
				NewStagerConstants.MIN_AMPLITUDE,
				NewStagerConstants.MAX_AMPLITUDE,
				NewStagerConstants.INCR_AMPLITUDE, false);
		}
		return alphaMinAmplitudePanel;
	}

	public AutoSpinnerPanel getSpindleMinAmplitudePanel() {
		if (spindleMinAmplitudePanel == null) {
			spindleMinAmplitudePanel = new AutoSpinnerPanel(
				NewStagerConstants.MIN_AMPLITUDE,
				NewStagerConstants.MIN_AMPLITUDE,
				NewStagerConstants.MAX_AMPLITUDE,
				NewStagerConstants.INCR_AMPLITUDE, false);
		}
		return spindleMinAmplitudePanel;
	}

	public JCheckBox getPrimaryHypnogramCheckBox() {
		if (primaryHypnogramCheckBox == null) {
			primaryHypnogramCheckBox = new JCheckBox();
			primaryHypnogramCheckBox.setPreferredSize(new Dimension(25, 25));
		}
		return primaryHypnogramCheckBox;
	}

	public void fillPanelFromParameters(NewStagerParameters parameters) {
		getRulesComboBox().setSelectedItem(parameters.rules);

		NewStagerParameterThresholds thresholds = parameters.thresholds;

		getDeltaMinAmplitudePanel().setValueWithAuto(
			thresholds.deltaThreshold.amplitude.getMinWithUnlimited());
		getAlphaMinAmplitudePanel().setValueWithAuto(
			thresholds.alphaThreshold.amplitude.getMinWithUnlimited());
		getSpindleMinAmplitudePanel().setValueWithAuto(
			thresholds.spindleThreshold.amplitude.getMinWithUnlimited());

		getPrimaryHypnogramCheckBox().setSelected(
			parameters.primaryHypnogramFlag);
	}

	public void fillParametersFromPanel(NewStagerParameters parameters) {
		parameters.rules = (NewStagerRules) getRulesComboBox()
						   .getSelectedItem();

		NewStagerParameterThresholds thresholds = parameters.thresholds;

		thresholds.deltaThreshold.amplitude
		.setMinWithUnlimited(getDeltaMinAmplitudePanel()
							 .getValueWithAuto());
		thresholds.alphaThreshold.amplitude
		.setMinWithUnlimited(getAlphaMinAmplitudePanel()
							 .getValueWithAuto());
		thresholds.spindleThreshold.amplitude
		.setMinWithUnlimited(getSpindleMinAmplitudePanel()
							 .getValueWithAuto());

		parameters.primaryHypnogramFlag = getPrimaryHypnogramCheckBox()
										  .isSelected();
	}

	public void validatePanel(ValidationErrors errors) {
		// nothing to do
	}

}
