/* NewStagerAdvancedConfigPanel.java created 2008-02-14
 *
 */
package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.Box;
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
import org.signalml.plugin.newstager.data.NewStagerFASPThreshold;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerParametersPreset;
import org.signalml.plugin.newstager.helper.NewStagerAutoParametersHelper;
import org.signalml.plugin.newstager.ui.components.NewStagerMinMaxSpinnerPanel;

/**
 * NewStagerAdvancedConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerAdvancedConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AbstractDialog owner;

	private JPanel parametersPanel;

	private NewStagerMinMaxSpinnerPanel deltaAmplitudePanel;
	private NewStagerMinMaxSpinnerPanel deltaFrequencyPanel;
	private NewStagerMinMaxSpinnerPanel deltaScalePanel;

	private NewStagerMinMaxSpinnerPanel thetaAmplitudePanel;
	private NewStagerMinMaxSpinnerPanel thetaFrequencyPanel;
	private NewStagerMinMaxSpinnerPanel thetaScalePanel;

	private NewStagerMinMaxSpinnerPanel alphaAmplitudePanel;
	private NewStagerMinMaxSpinnerPanel alphaFrequencyPanel;
	private NewStagerMinMaxSpinnerPanel alphaScalePanel;

	private NewStagerMinMaxSpinnerPanel spindleAmplitudePanel;
	private NewStagerMinMaxSpinnerPanel spindleFrequencyPanel;
	private NewStagerMinMaxSpinnerPanel spindleScalePanel;

	private NewStagerMinMaxSpinnerPanel kComplexAmplitudePanel;
	private NewStagerMinMaxSpinnerPanel kComplexFrequencyPanel;
	private NewStagerMinMaxSpinnerPanel kComplexScalePanel;
	private NewStagerMinMaxSpinnerPanel kComplexPhasePanel;

	public NewStagerAdvancedConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(new TitledBorder(
				_("Parameters")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		add(getParametersPanel(), BorderLayout.NORTH);
		add(Box.createVerticalGlue(), BorderLayout.CENTER);

	}

	public JPanel getParametersPanel() {
		if (parametersPanel == null) {

			parametersPanel = new JPanel(new GridLayout(6, 5, 6, 10));

			TitleLabel deltaLabel = new TitleLabel(_("Delta waves"));
			TitleLabel thetaLabel = new TitleLabel(_("Theta waves"));
			TitleLabel alphaLabel = new TitleLabel(_("Alpha waves"));
			TitleLabel spindleLabel = new TitleLabel(_("Sleep spindles"));
			TitleLabel kComplexLabel = new TitleLabel(_("K-Complex"));

			TitleLabel amplitudeLabel = new TitleLabel(
					"<html><body><div align=\"center\">" + _("Amplitude [uV]")
							+ "<br />" + _("min/max") + "</div></body></html>");
			TitleLabel frequencyLabel = new TitleLabel(
					"<html><body><div align=\"center\">" + _("Frequency [Hz]")
							+ "<br />" + _("min/max") + "</div></body></html>");
			TitleLabel scaleLabel = new TitleLabel(
					"<html><body><div align=\"center\">" + _("Scale [s]")
							+ "<br />" + _("min/max") + "</div></body></html>");
			TitleLabel phaseLabel = new TitleLabel(
					"<html><body><div align=\"center\">" + _("Phase [rad]")
							+ "<br />" + _("min/max") + "</div></body></html>");

			TitleLabel deltaPhaseLabel = new TitleLabel("-");
			TitleLabel thetaPhaseLabel = new TitleLabel("-");
			TitleLabel alphaPhaseLabel = new TitleLabel("-");
			TitleLabel spindlePhaseLabel = new TitleLabel("-");

			CompactButton parametersHelpButton = SwingUtils
					.createFieldHelpButton(owner,
							NewStagerMethodDialog.HELP_PARAMETERS);

			parametersPanel.add(parametersHelpButton);
			parametersPanel.add(amplitudeLabel);
			parametersPanel.add(frequencyLabel);
			parametersPanel.add(scaleLabel);
			parametersPanel.add(phaseLabel);

			parametersPanel.add(deltaLabel);
			parametersPanel.add(getDeltaAmplitudePanel());
			parametersPanel.add(getDeltaFrequencyPanel());
			parametersPanel.add(getDeltaScalePanel());
			parametersPanel.add(deltaPhaseLabel);

			parametersPanel.add(thetaLabel);
			parametersPanel.add(getThetaAmplitudePanel());
			parametersPanel.add(getThetaFrequencyPanel());
			parametersPanel.add(getThetaScalePanel());
			parametersPanel.add(thetaPhaseLabel);

			parametersPanel.add(alphaLabel);
			parametersPanel.add(getAlphaAmplitudePanel());
			parametersPanel.add(getAlphaFrequencyPanel());
			parametersPanel.add(getAlphaScalePanel());
			parametersPanel.add(alphaPhaseLabel);

			parametersPanel.add(spindleLabel);
			parametersPanel.add(getSpindleAmplitudePanel());
			parametersPanel.add(getSpindleFrequencyPanel());
			parametersPanel.add(getSpindleScalePanel());
			parametersPanel.add(spindlePhaseLabel);

			parametersPanel.add(kComplexLabel);
			parametersPanel.add(getKComplexAmplitudePanel());
			parametersPanel.add(getKComplexFrequencyPanel());
			parametersPanel.add(getKComplexScalePanel());
			parametersPanel.add(getKComplexPhasePanel());

		}
		return parametersPanel;
	}

	public NewStagerMinMaxSpinnerPanel getDeltaAmplitudePanel() {
		if (deltaAmplitudePanel == null) {
			deltaAmplitudePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.INCR_AMPLITUDE, 0, 0);
		}
		return deltaAmplitudePanel;
	}

	public NewStagerMinMaxSpinnerPanel getDeltaFrequencyPanel() {
		if (deltaFrequencyPanel == null) {
			deltaFrequencyPanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.INCR_FREQUENCY, 0, 0);
		}
		return deltaFrequencyPanel;
	}

	public NewStagerMinMaxSpinnerPanel getDeltaScalePanel() {
		if (deltaScalePanel == null) {
			deltaScalePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.INCR_SCALE, 0, 0);
		}
		return deltaScalePanel;
	}

	public NewStagerMinMaxSpinnerPanel getThetaAmplitudePanel() {
		if (thetaAmplitudePanel == null) {
			thetaAmplitudePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.INCR_AMPLITUDE, 0, 0);
		}
		return thetaAmplitudePanel;
	}

	public NewStagerMinMaxSpinnerPanel getThetaFrequencyPanel() {
		if (thetaFrequencyPanel == null) {
			thetaFrequencyPanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.INCR_FREQUENCY, 0, 0);
		}
		return thetaFrequencyPanel;
	}

	public NewStagerMinMaxSpinnerPanel getThetaScalePanel() {
		if (thetaScalePanel == null) {
			thetaScalePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.INCR_SCALE, 0, 0);
		}
		return thetaScalePanel;
	}

	public NewStagerMinMaxSpinnerPanel getAlphaAmplitudePanel() {
		if (alphaAmplitudePanel == null) {
			alphaAmplitudePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.INCR_AMPLITUDE, 0, 0);
		}
		return alphaAmplitudePanel;
	}

	public NewStagerMinMaxSpinnerPanel getAlphaFrequencyPanel() {
		if (alphaFrequencyPanel == null) {
			alphaFrequencyPanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.INCR_FREQUENCY, 0, 0);
		}
		return alphaFrequencyPanel;
	}

	public NewStagerMinMaxSpinnerPanel getAlphaScalePanel() {
		if (alphaScalePanel == null) {
			alphaScalePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.INCR_SCALE, 0, 0);
		}
		return alphaScalePanel;
	}

	public NewStagerMinMaxSpinnerPanel getSpindleAmplitudePanel() {
		if (spindleAmplitudePanel == null) {
			spindleAmplitudePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.INCR_AMPLITUDE, 0, 0);
		}
		return spindleAmplitudePanel;
	}

	public NewStagerMinMaxSpinnerPanel getSpindleFrequencyPanel() {
		if (spindleFrequencyPanel == null) {
			spindleFrequencyPanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.INCR_FREQUENCY, 0, 0);
		}
		return spindleFrequencyPanel;
	}

	public NewStagerMinMaxSpinnerPanel getSpindleScalePanel() {
		if (spindleScalePanel == null) {
			spindleScalePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.INCR_SCALE, 0, 0);
		}
		return spindleScalePanel;
	}

	public NewStagerMinMaxSpinnerPanel getKComplexAmplitudePanel() {
		if (kComplexAmplitudePanel == null) {
			kComplexAmplitudePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.MIN_AMPLITUDE,
					NewStagerConstants.MAX_AMPLITUDE,
					NewStagerConstants.INCR_AMPLITUDE, 0, 0);
		}
		return kComplexAmplitudePanel;
	}

	public NewStagerMinMaxSpinnerPanel getKComplexFrequencyPanel() {
		if (kComplexFrequencyPanel == null) {
			kComplexFrequencyPanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.MIN_FREQUENCY,
					NewStagerConstants.MAX_FREQUENCY,
					NewStagerConstants.INCR_FREQUENCY, 0, 0);
		}
		return kComplexFrequencyPanel;
	}

	public NewStagerMinMaxSpinnerPanel getKComplexScalePanel() {
		if (kComplexScalePanel == null) {
			kComplexScalePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.MIN_SCALE, NewStagerConstants.MAX_SCALE,
					NewStagerConstants.INCR_SCALE, 0, 0);
		}
		return kComplexScalePanel;
	}

	public NewStagerMinMaxSpinnerPanel getKComplexPhasePanel() {
		if (kComplexPhasePanel == null) {
			kComplexPhasePanel = new NewStagerMinMaxSpinnerPanel(
					NewStagerConstants.MIN_PHASE, NewStagerConstants.MAX_PHASE,
					NewStagerConstants.MIN_PHASE, NewStagerConstants.MAX_PHASE,
					NewStagerConstants.INCR_PHASE, 0, 0);
		}
		return kComplexPhasePanel;
	}

	public void fillPanelFromParameters(
			NewStagerParametersPreset parametersPreset) {
		NewStagerParameters parameters = parametersPreset.parameters;

		NewStagerParameterThresholds thresholds = parameters.thresholds;
		NewStagerFASPThreshold alpha = thresholds.alphaThreshold;
		NewStagerFASPThreshold delta = thresholds.deltaThreshold;
		NewStagerFASPThreshold theta = thresholds.thetaThreshold;
		NewStagerFASPThreshold spindle = thresholds.spindleThreshold;
		NewStagerFASPThreshold kComplex = thresholds.kCThreshold;

		getDeltaAmplitudePanel()
				.setRange(
						delta.amplitude,
						parametersPreset.isAutoDeltaAmplitude ? NewStagerAutoParametersHelper
								.GetAutoDeltaAmplitude() : null, null);
		getDeltaFrequencyPanel().setRange(delta.frequency);
		getDeltaScalePanel().setRange(delta.scale);

		getThetaAmplitudePanel().setRange(theta.amplitude);
		getThetaFrequencyPanel().setRange(theta.frequency);
		getThetaScalePanel().setRange(theta.scale);

		getAlphaAmplitudePanel()
				.setRange(
						alpha.amplitude,
						parametersPreset.isAutoAlphaAmplitude ? NewStagerAutoParametersHelper
								.GetAutoAlphaAmplitude() : null, null);
		getAlphaFrequencyPanel().setRange(alpha.frequency);
		getAlphaScalePanel().setRange(alpha.scale);

		getSpindleAmplitudePanel()
				.setRange(
						spindle.amplitude,
						parametersPreset.isAutoSpindleAmplitude ? NewStagerAutoParametersHelper
								.GetAutoSpindleAmplitude() : null, null);
		getSpindleFrequencyPanel().setRange(spindle.frequency);
		getSpindleScalePanel().setRange(spindle.scale);

		getKComplexAmplitudePanel().setRange(kComplex.amplitude);
		getKComplexFrequencyPanel().setRange(kComplex.frequency);
		getKComplexScalePanel().setRange(kComplex.scale);
		getKComplexPhasePanel().setRange(kComplex.phase);
	}

	public void fillParametersFromPanel(
			NewStagerParametersPreset parametersPreset) {
		NewStagerParameters parameters = parametersPreset.parameters;

		NewStagerParameterThresholds thresholds = parameters.thresholds;
		NewStagerFASPThreshold alpha = thresholds.alphaThreshold;
		NewStagerFASPThreshold delta = thresholds.deltaThreshold;
		NewStagerFASPThreshold theta = thresholds.thetaThreshold;
		NewStagerFASPThreshold spindle = thresholds.spindleThreshold;
		NewStagerFASPThreshold kComplex = thresholds.kCThreshold;

		NewStagerMinMaxSpinnerPanel amplitudePanel;

		amplitudePanel = getDeltaAmplitudePanel();
		amplitudePanel.getRange(delta.amplitude);
		parametersPreset.isAutoDeltaAmplitude = amplitudePanel.isMinAuto();
		if (parametersPreset.isAutoDeltaAmplitude) {
			delta.amplitude.setMin(NewStagerAutoParametersHelper.GetAutoDeltaAmplitude());
		}
		getDeltaFrequencyPanel().getRange(delta.frequency);
		getDeltaScalePanel().getRange(delta.scale);

		getThetaAmplitudePanel().getRange(theta.amplitude);
		getThetaFrequencyPanel().getRange(theta.frequency);
		getThetaScalePanel().getRange(theta.scale);

		amplitudePanel = getAlphaAmplitudePanel();
		amplitudePanel.getRange(alpha.amplitude);
		parametersPreset.isAutoAlphaAmplitude = amplitudePanel.isMinAuto();
		if (parametersPreset.isAutoAlphaAmplitude) {
			alpha.amplitude.setMin(NewStagerAutoParametersHelper.GetAutoAlphaAmplitude());
		}
		getAlphaFrequencyPanel().getRange(alpha.frequency);
		getAlphaScalePanel().getRange(alpha.scale);

		amplitudePanel = getSpindleAmplitudePanel();
		amplitudePanel.getRange(spindle.amplitude);
		parametersPreset.isAutoSpindleAmplitude = amplitudePanel.isMinAuto();
		if (parametersPreset.isAutoSpindleAmplitude) {
			spindle.amplitude.setMin(NewStagerAutoParametersHelper.GetAutoSpindleAmplitude());
		}
		getSpindleFrequencyPanel().getRange(spindle.frequency);
		getSpindleScalePanel().getRange(spindle.scale);

		getKComplexAmplitudePanel().getRange(kComplex.amplitude);
		getKComplexFrequencyPanel().getRange(kComplex.frequency);
		getKComplexScalePanel().getRange(kComplex.scale);
		getKComplexPhasePanel().getRange(kComplex.phase);

		normalize(alpha);
		normalize(theta);
		normalize(delta);
		normalize(spindle);
		normalize(kComplex);
	}

	private void normalize(NewStagerFASPThreshold threshold) {
		threshold.amplitude.normalize();
		threshold.frequency.normalize();
		threshold.scale.normalize();
	}

	public void validatePanel(ValidationErrors errors) {
		// nothing to do
	}

	private class TitleLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		private TitleLabel(String text) {
			super(text);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

	}

}
