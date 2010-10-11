/* StagerAdvancedConfigPanel.java created 2008-02-14
 *
 */
package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.element.CompactButton;
import org.signalml.method.stager.StagerParameters;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerAdvancedConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerAdvancedConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private AbstractDialog owner;

	private JPanel parametersPanel;

	private MinMaxSpinnerPanel deltaAmplitudePanel;
	private MinMaxSpinnerPanel deltaFrequencyPanel;
	private MinMaxSpinnerPanel deltaScalePanel;

	private MinMaxSpinnerPanel thetaAmplitudePanel;
	private MinMaxSpinnerPanel thetaFrequencyPanel;
	private MinMaxSpinnerPanel thetaScalePanel;

	private MinMaxSpinnerPanel alphaAmplitudePanel;
	private MinMaxSpinnerPanel alphaFrequencyPanel;
	private MinMaxSpinnerPanel alphaScalePanel;

	private MinMaxSpinnerPanel spindleAmplitudePanel;
	private MinMaxSpinnerPanel spindleFrequencyPanel;
	private MinMaxSpinnerPanel spindleScalePanel;

	private MinMaxSpinnerPanel kComplexAmplitudePanel;
	private MinMaxSpinnerPanel kComplexFrequencyPanel;
	private MinMaxSpinnerPanel kComplexScalePanel;
	private MinMaxSpinnerPanel kComplexPhasePanel;

	public StagerAdvancedConfigPanel(MessageSourceAccessor messageSource, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.owner = owner;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("stagerMethod.dialog.advancedParametersTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		add(getParametersPanel(), BorderLayout.NORTH);
		add(Box.createVerticalGlue(), BorderLayout.CENTER);

	}

	public JPanel getParametersPanel() {
		if (parametersPanel == null) {

			parametersPanel = new JPanel(new GridLayout(6,5,6,10));

			TitleLabel deltaLabel = new TitleLabel(messageSource.getMessage("stagerMethod.dialog.delta"));
			TitleLabel thetaLabel = new TitleLabel(messageSource.getMessage("stagerMethod.dialog.theta"));
			TitleLabel alphaLabel = new TitleLabel(messageSource.getMessage("stagerMethod.dialog.alpha"));
			TitleLabel spindleLabel = new TitleLabel(messageSource.getMessage("stagerMethod.dialog.spindle"));
			TitleLabel kComplexLabel = new TitleLabel(messageSource.getMessage("stagerMethod.dialog.kComplex"));

			TitleLabel amplitudeLabel = new TitleLabel("<html><body><div align=\"center\">" + messageSource.getMessage("stagerMethod.dialog.amplitude") + "</div></body></html>");
			TitleLabel frequencyLabel = new TitleLabel("<html><body><div align=\"center\">" + messageSource.getMessage("stagerMethod.dialog.frequency") + "</div></body></html>");
			TitleLabel scaleLabel = new TitleLabel("<html><body><div align=\"center\">" + messageSource.getMessage("stagerMethod.dialog.scale") + "</div></body></html>");
			TitleLabel phaseLabel = new TitleLabel("<html><body><div align=\"center\">" + messageSource.getMessage("stagerMethod.dialog.phase") + "</div></body></html>");

			TitleLabel deltaPhaseLabel = new TitleLabel("-");
			TitleLabel thetaPhaseLabel = new TitleLabel("-");
			TitleLabel alphaPhaseLabel = new TitleLabel("-");
			TitleLabel spindlePhaseLabel = new TitleLabel("-");

			CompactButton parametersHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_PARAMETERS);

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

	public MinMaxSpinnerPanel getDeltaAmplitudePanel() {
		if (deltaAmplitudePanel == null) {
			deltaAmplitudePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE);
		}
		return deltaAmplitudePanel;
	}

	public MinMaxSpinnerPanel getDeltaFrequencyPanel() {
		if (deltaFrequencyPanel == null) {
			deltaFrequencyPanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MAX_FREQUENCY, StagerParameters.INCR_FREQUENCY);
		}
		return deltaFrequencyPanel;
	}

	public MinMaxSpinnerPanel getDeltaScalePanel() {
		if (deltaScalePanel == null) {
			deltaScalePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MAX_SCALE, StagerParameters.INCR_SCALE);
		}
		return deltaScalePanel;
	}

	public MinMaxSpinnerPanel getThetaAmplitudePanel() {
		if (thetaAmplitudePanel == null) {
			thetaAmplitudePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE);
		}
		return thetaAmplitudePanel;
	}

	public MinMaxSpinnerPanel getThetaFrequencyPanel() {
		if (thetaFrequencyPanel == null) {
			thetaFrequencyPanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MAX_FREQUENCY, StagerParameters.INCR_FREQUENCY);
		}
		return thetaFrequencyPanel;
	}

	public MinMaxSpinnerPanel getThetaScalePanel() {
		if (thetaScalePanel == null) {
			thetaScalePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MAX_SCALE, StagerParameters.INCR_SCALE);
		}
		return thetaScalePanel;
	}

	public MinMaxSpinnerPanel getAlphaAmplitudePanel() {
		if (alphaAmplitudePanel == null) {
			alphaAmplitudePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE);
		}
		return alphaAmplitudePanel;
	}

	public MinMaxSpinnerPanel getAlphaFrequencyPanel() {
		if (alphaFrequencyPanel == null) {
			alphaFrequencyPanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MAX_FREQUENCY, StagerParameters.INCR_FREQUENCY);
		}
		return alphaFrequencyPanel;
	}

	public MinMaxSpinnerPanel getAlphaScalePanel() {
		if (alphaScalePanel == null) {
			alphaScalePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MAX_SCALE, StagerParameters.INCR_SCALE);
		}
		return alphaScalePanel;
	}

	public MinMaxSpinnerPanel getSpindleAmplitudePanel() {
		if (spindleAmplitudePanel == null) {
			spindleAmplitudePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE);
		}
		return spindleAmplitudePanel;
	}

	public MinMaxSpinnerPanel getSpindleFrequencyPanel() {
		if (spindleFrequencyPanel == null) {
			spindleFrequencyPanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MAX_FREQUENCY, StagerParameters.INCR_FREQUENCY);
		}
		return spindleFrequencyPanel;
	}

	public MinMaxSpinnerPanel getSpindleScalePanel() {
		if (spindleScalePanel == null) {
			spindleScalePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MAX_SCALE, StagerParameters.INCR_SCALE);
		}
		return spindleScalePanel;
	}

	public MinMaxSpinnerPanel getKComplexAmplitudePanel() {
		if (kComplexAmplitudePanel == null) {
			kComplexAmplitudePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE);
		}
		return kComplexAmplitudePanel;
	}

	public MinMaxSpinnerPanel getKComplexFrequencyPanel() {
		if (kComplexFrequencyPanel == null) {
			kComplexFrequencyPanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MIN_FREQUENCY, StagerParameters.MAX_FREQUENCY, StagerParameters.INCR_FREQUENCY);
		}
		return kComplexFrequencyPanel;
	}

	public MinMaxSpinnerPanel getKComplexScalePanel() {
		if (kComplexScalePanel == null) {
			kComplexScalePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MIN_SCALE, StagerParameters.MAX_SCALE, StagerParameters.INCR_SCALE);
		}
		return kComplexScalePanel;
	}

	public MinMaxSpinnerPanel getKComplexPhasePanel() {
		if (kComplexPhasePanel == null) {
			kComplexPhasePanel = new MinMaxSpinnerPanel(messageSource, StagerParameters.MIN_PHASE, StagerParameters.MIN_PHASE, StagerParameters.MIN_PHASE, StagerParameters.MAX_PHASE, StagerParameters.INCR_PHASE);
		}
		return kComplexPhasePanel;
	}

	public void fillPanelFromParameters(StagerParameters parameters) {

		getDeltaAmplitudePanel().setRange(parameters.getDeltaAmplitude());
		getDeltaFrequencyPanel().setRange(parameters.getDeltaFrequency());
		getDeltaScalePanel().setRange(parameters.getDeltaScale());

		getThetaAmplitudePanel().setRange(parameters.getThetaAmplitude());
		getThetaFrequencyPanel().setRange(parameters.getThetaFrequency());
		getThetaScalePanel().setRange(parameters.getThetaScale());

		getAlphaAmplitudePanel().setRange(parameters.getAlphaAmplitude());
		getAlphaFrequencyPanel().setRange(parameters.getAlphaFrequency());
		getAlphaScalePanel().setRange(parameters.getAlphaScale());

		getSpindleAmplitudePanel().setRange(parameters.getSpindleAmplitude());
		getSpindleFrequencyPanel().setRange(parameters.getSpindleFrequency());
		getSpindleScalePanel().setRange(parameters.getSpindleScale());

		getKComplexAmplitudePanel().setRange(parameters.getKComplexAmplitude());
		getKComplexFrequencyPanel().setRange(parameters.getKComplexFrequency());
		getKComplexScalePanel().setRange(parameters.getKComplexScale());
		getKComplexPhasePanel().setRange(parameters.getKComplexPhase());

	}

	public void fillParametersFromPanel(StagerParameters parameters) {

		getDeltaAmplitudePanel().getRange(parameters.getDeltaAmplitude());
		getDeltaFrequencyPanel().getRange(parameters.getDeltaFrequency());
		getDeltaScalePanel().getRange(parameters.getDeltaScale());

		getThetaAmplitudePanel().getRange(parameters.getThetaAmplitude());
		getThetaFrequencyPanel().getRange(parameters.getThetaFrequency());
		getThetaScalePanel().getRange(parameters.getThetaScale());

		getAlphaAmplitudePanel().getRange(parameters.getAlphaAmplitude());
		getAlphaFrequencyPanel().getRange(parameters.getAlphaFrequency());
		getAlphaScalePanel().getRange(parameters.getAlphaScale());

		getSpindleAmplitudePanel().getRange(parameters.getSpindleAmplitude());
		getSpindleFrequencyPanel().getRange(parameters.getSpindleFrequency());
		getSpindleScalePanel().getRange(parameters.getSpindleScale());

		getKComplexAmplitudePanel().getRange(parameters.getKComplexAmplitude());
		getKComplexFrequencyPanel().getRange(parameters.getKComplexFrequency());
		getKComplexScalePanel().getRange(parameters.getKComplexScale());
		getKComplexPhasePanel().getRange(parameters.getKComplexPhase());

		parameters.getDeltaAmplitude().normalize();
		parameters.getDeltaFrequency().normalize();
		parameters.getDeltaScale().normalize();

		parameters.getThetaAmplitude().normalize();
		parameters.getThetaFrequency().normalize();
		parameters.getThetaScale().normalize();

		parameters.getAlphaAmplitude().normalize();
		parameters.getAlphaFrequency().normalize();
		parameters.getAlphaScale().normalize();

		parameters.getSpindleAmplitude().normalize();
		parameters.getSpindleFrequency().normalize();
		parameters.getSpindleScale().normalize();

		parameters.getKComplexAmplitude().normalize();
		parameters.getKComplexFrequency().normalize();
		parameters.getKComplexScale().normalize();

	}

	public void validatePanel(Errors errors) {

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
