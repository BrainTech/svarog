/* ParameterRangeFilterDialog.java created 2008-02-27
 *
 */

package org.signalml.app.view.book.filter;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.UnlimitedSpinnerPanel;
import org.signalml.domain.book.filter.ParameterRangeAtomFilter;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.MinMaxRangeFloat;
import org.signalml.util.MinMaxRangeInteger;

import org.springframework.validation.Errors;

/** ParameterRangeFilterDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ParameterRangeFilterDialog extends AbstractFilterDialog {

	private static final long serialVersionUID = 1L;

	private UnlimitedSpinnerPanel minIterationPanel;
	private UnlimitedSpinnerPanel maxIterationPanel;
	private UnlimitedSpinnerPanel minModulusPanel;
	private UnlimitedSpinnerPanel maxModulusPanel;
	private UnlimitedSpinnerPanel minAmplitudePanel;
	private UnlimitedSpinnerPanel maxAmplitudePanel;
	private UnlimitedSpinnerPanel minPositionPanel;
	private UnlimitedSpinnerPanel maxPositionPanel;
	private UnlimitedSpinnerPanel minScalePanel;
	private UnlimitedSpinnerPanel maxScalePanel;
	private UnlimitedSpinnerPanel minFrequencyPanel;
	private UnlimitedSpinnerPanel maxFrequencyPanel;
	private UnlimitedSpinnerPanel minPhasePanel;
	private UnlimitedSpinnerPanel maxPhasePanel;

	public  ParameterRangeFilterDialog( Window w, boolean isModal) {
		super( w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Parameter range atom filter"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/filter.png"));
		super.initialize();
		setResizable(false);
	}

	@Override
	protected JPanel createControlPane() {
		JPanel controlPane = super.createControlPane();
		controlPane.add(Box.createHorizontalStrut(10), 1);
		controlPane.add(new JButton(new ClearFilterAction()), 1);
		return controlPane;
	}

	@Override
	public JComponent createInterface() {

		JPanel parameterPanel = new JPanel();

		parameterPanel.setBorder(new CompoundBorder(
		                                 new TitledBorder(_("Choose limits")),
		                                 new EmptyBorder(3,3,3,3)
		                         ));

		GroupLayout layout = new GroupLayout(parameterPanel);
		parameterPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel iterationLabel = new JLabel(_("Iteration"));
		JLabel modulusLabel = new JLabel(_("Modulus"));
		JLabel amplitudeLabel = new JLabel(_("Amplitude"));
		JLabel positionLabel = new JLabel(_("Position"));
		JLabel scaleLabel = new JLabel(_("Scale"));
		JLabel frequencyLabel = new JLabel(_("Frequency"));
		JLabel phaseLabel = new JLabel(_("Phase"));

		JLabel minIterationLabel = new JLabel(_("from"));
		JLabel minModulusLabel = new JLabel(_("from"));
		JLabel minAmplitudeLabel = new JLabel(_("from"));
		JLabel minPositionLabel = new JLabel(_("from"));
		JLabel minScaleLabel = new JLabel(_("from"));
		JLabel minFrequencyLabel = new JLabel(_("from"));
		JLabel minPhaseLabel = new JLabel(_("from"));

		JLabel maxIterationLabel = new JLabel(_("to"));
		JLabel maxModulusLabel = new JLabel(_("to"));
		JLabel maxAmplitudeLabel = new JLabel(_("to"));
		JLabel maxPositionLabel = new JLabel(_("to"));
		JLabel maxScaleLabel = new JLabel(_("to"));
		JLabel maxFrequencyLabel = new JLabel(_("to"));
		JLabel maxPhaseLabel = new JLabel(_("to"));

		Component iterationGlue = Box.createHorizontalGlue();
		Component modulusGlue = Box.createHorizontalGlue();
		Component amplitudeGlue = Box.createHorizontalGlue();
		Component positionGlue = Box.createHorizontalGlue();
		Component scaleGlue = Box.createHorizontalGlue();
		Component frequencyGlue = Box.createHorizontalGlue();
		Component phaseGlue = Box.createHorizontalGlue();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(iterationLabel)
		        .addComponent(modulusLabel)
		        .addComponent(amplitudeLabel)
		        .addComponent(positionLabel)
		        .addComponent(scaleLabel)
		        .addComponent(frequencyLabel)
		        .addComponent(phaseLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(minIterationLabel)
		        .addComponent(minModulusLabel)
		        .addComponent(minAmplitudeLabel)
		        .addComponent(minPositionLabel)
		        .addComponent(minScaleLabel)
		        .addComponent(minFrequencyLabel)
		        .addComponent(minPhaseLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getMinIterationPanel())
		        .addComponent(getMinModulusPanel())
		        .addComponent(getMinAmplitudePanel())
		        .addComponent(getMinPositionPanel())
		        .addComponent(getMinScalePanel())
		        .addComponent(getMinFrequencyPanel())
		        .addComponent(getMinPhasePanel())
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(iterationGlue)
		        .addComponent(modulusGlue)
		        .addComponent(amplitudeGlue)
		        .addComponent(positionGlue)
		        .addComponent(scaleGlue)
		        .addComponent(frequencyGlue)
		        .addComponent(phaseGlue)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(maxIterationLabel)
		        .addComponent(maxModulusLabel)
		        .addComponent(maxAmplitudeLabel)
		        .addComponent(maxPositionLabel)
		        .addComponent(maxScaleLabel)
		        .addComponent(maxFrequencyLabel)
		        .addComponent(maxPhaseLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getMaxIterationPanel())
		        .addComponent(getMaxModulusPanel())
		        .addComponent(getMaxAmplitudePanel())
		        .addComponent(getMaxPositionPanel())
		        .addComponent(getMaxScalePanel())
		        .addComponent(getMaxFrequencyPanel())
		        .addComponent(getMaxPhasePanel())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(iterationLabel)
				.addComponent(minIterationLabel)
				.addComponent(getMinIterationPanel())
				.addComponent(iterationGlue)
				.addComponent(maxIterationLabel)
				.addComponent(getMaxIterationPanel())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(modulusLabel)
				.addComponent(minModulusLabel)
				.addComponent(getMinModulusPanel())
				.addComponent(modulusGlue)
				.addComponent(maxModulusLabel)
				.addComponent(getMaxModulusPanel())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(amplitudeLabel)
				.addComponent(minAmplitudeLabel)
				.addComponent(getMinAmplitudePanel())
				.addComponent(amplitudeGlue)
				.addComponent(maxAmplitudeLabel)
				.addComponent(getMaxAmplitudePanel())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(positionLabel)
				.addComponent(minPositionLabel)
				.addComponent(getMinPositionPanel())
				.addComponent(positionGlue)
				.addComponent(maxPositionLabel)
				.addComponent(getMaxPositionPanel())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(scaleLabel)
				.addComponent(minScaleLabel)
				.addComponent(getMinScalePanel())
				.addComponent(scaleGlue)
				.addComponent(maxScaleLabel)
				.addComponent(getMaxScalePanel())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(frequencyLabel)
				.addComponent(minFrequencyLabel)
				.addComponent(getMinFrequencyPanel())
				.addComponent(frequencyGlue)
				.addComponent(maxFrequencyLabel)
				.addComponent(getMaxFrequencyPanel())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(phaseLabel)
				.addComponent(minPhaseLabel)
				.addComponent(getMinPhasePanel())
				.addComponent(phaseGlue)
				.addComponent(maxPhaseLabel)
				.addComponent(getMaxPhasePanel())
			);

		layout.setVerticalGroup(vGroup);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getNamePanel(), BorderLayout.NORTH);
		interfacePanel.add(parameterPanel, BorderLayout.CENTER);

		return interfacePanel;

	}

	public UnlimitedSpinnerPanel getMinIterationPanel() {
		if (minIterationPanel == null) {
			minIterationPanel = new UnlimitedSpinnerPanel(1,1,Integer.MAX_VALUE,1,false);
		}
		return minIterationPanel;
	}

	public UnlimitedSpinnerPanel getMaxIterationPanel() {
		if (maxIterationPanel == null) {
			maxIterationPanel = new UnlimitedSpinnerPanel(1,1,Integer.MAX_VALUE,1,false);
		}
		return maxIterationPanel;
	}

	public UnlimitedSpinnerPanel getMinModulusPanel() {
		if (minModulusPanel == null) {
			minModulusPanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return minModulusPanel;
	}

	public UnlimitedSpinnerPanel getMaxModulusPanel() {
		if (maxModulusPanel == null) {
			maxModulusPanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return maxModulusPanel;
	}

	public UnlimitedSpinnerPanel getMinAmplitudePanel() {
		if (minAmplitudePanel == null) {
			minAmplitudePanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return minAmplitudePanel;
	}

	public UnlimitedSpinnerPanel getMaxAmplitudePanel() {
		if (maxAmplitudePanel == null) {
			maxAmplitudePanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return maxAmplitudePanel;
	}

	public UnlimitedSpinnerPanel getMinPositionPanel() {
		if (minPositionPanel == null) {
			minPositionPanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return minPositionPanel;
	}

	public UnlimitedSpinnerPanel getMaxPositionPanel() {
		if (maxPositionPanel == null) {
			maxPositionPanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return maxPositionPanel;
	}

	public UnlimitedSpinnerPanel getMinScalePanel() {
		if (minScalePanel == null) {
			minScalePanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return minScalePanel;
	}

	public UnlimitedSpinnerPanel getMaxScalePanel() {
		if (maxScalePanel == null) {
			maxScalePanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return maxScalePanel;
	}

	public UnlimitedSpinnerPanel getMinFrequencyPanel() {
		if (minFrequencyPanel == null) {
			minFrequencyPanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return minFrequencyPanel;
	}

	public UnlimitedSpinnerPanel getMaxFrequencyPanel() {
		if (maxFrequencyPanel == null) {
			maxFrequencyPanel = new UnlimitedSpinnerPanel(0,0,Double.MAX_VALUE,0.1,false);
		}
		return maxFrequencyPanel;
	}

	public UnlimitedSpinnerPanel getMinPhasePanel() {
		if (minPhasePanel == null) {
			minPhasePanel = new UnlimitedSpinnerPanel(0,-Math.PI,Math.PI,0.1,false);
		}
		return minPhasePanel;
	}

	public UnlimitedSpinnerPanel getMaxPhasePanel() {
		if (maxPhasePanel == null) {
			maxPhasePanel = new UnlimitedSpinnerPanel(0,-Math.PI,Math.PI,0.1,false);
		}
		return maxPhasePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		ParameterRangeAtomFilter filter = (ParameterRangeAtomFilter) model;

		super.fillDialogFromFilter(filter);

		MinMaxRangeInteger iteration = filter.getIteration();
		getMinIterationPanel().setMinValue(iteration);
		getMaxIterationPanel().setMaxValue(iteration);

		MinMaxRangeFloat modulus = filter.getModulus();
		getMinModulusPanel().setMinValue(modulus);
		getMaxModulusPanel().setMaxValue(modulus);

		MinMaxRangeFloat amplitude = filter.getAmplitude();
		getMinAmplitudePanel().setMinValue(amplitude);
		getMaxAmplitudePanel().setMaxValue(amplitude);

		MinMaxRangeFloat position = filter.getPosition();
		getMinPositionPanel().setMinValue(position);
		getMaxPositionPanel().setMaxValue(position);

		MinMaxRangeFloat scale = filter.getScale();
		getMinScalePanel().setMinValue(scale);
		getMaxScalePanel().setMaxValue(scale);

		MinMaxRangeFloat frequency = filter.getFrequency();
		getMinFrequencyPanel().setMinValue(frequency);
		getMaxFrequencyPanel().setMaxValue(frequency);

		MinMaxRangeFloat phase = filter.getPhase();
		getMinPhasePanel().setMinValue(phase);
		getMaxPhasePanel().setMaxValue(phase);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		ParameterRangeAtomFilter filter = (ParameterRangeAtomFilter) model;

		super.fillFilterFromDialog(filter);

		MinMaxRangeInteger iteration = filter.getIteration();
		getMinIterationPanel().getMinValue(iteration);
		getMaxIterationPanel().getMaxValue(iteration);
		iteration.normalize();

		MinMaxRangeFloat modulus = filter.getModulus();
		getMinModulusPanel().getMinValue(modulus);
		getMaxModulusPanel().getMaxValue(modulus);
		modulus.normalize();

		MinMaxRangeFloat amplitude = filter.getAmplitude();
		getMinAmplitudePanel().getMinValue(amplitude);
		getMaxAmplitudePanel().getMaxValue(amplitude);
		amplitude.normalize();

		MinMaxRangeFloat position = filter.getPosition();
		getMinPositionPanel().getMinValue(position);
		getMaxPositionPanel().getMaxValue(position);
		position.normalize();

		MinMaxRangeFloat scale = filter.getScale();
		getMinScalePanel().getMinValue(scale);
		getMaxScalePanel().getMaxValue(scale);
		scale.normalize();

		MinMaxRangeFloat frequency = filter.getFrequency();
		getMinFrequencyPanel().getMinValue(frequency);
		getMaxFrequencyPanel().getMaxValue(frequency);
		frequency.normalize();

		MinMaxRangeFloat phase = filter.getPhase();
		getMinPhasePanel().getMinValue(phase);
		getMaxPhasePanel().getMaxValue(phase);
		phase.normalize();

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		// nothing to do

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ParameterRangeAtomFilter.class.isAssignableFrom(clazz);
	}

	protected class ClearFilterAction extends AbstractAction {

		public static final String HELLO_STRING = "Hello server";
		private static final long serialVersionUID = 1L;

		public ClearFilterAction() {
			super(_("Clear filter"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/clearfilter.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Clear filter"));
		}

		public void actionPerformed(ActionEvent ev) {

			getMinIterationPanel().setUnlimited(true);
			getMaxIterationPanel().setUnlimited(true);

			getMinModulusPanel().setUnlimited(true);
			getMaxModulusPanel().setUnlimited(true);

			getMinAmplitudePanel().setUnlimited(true);
			getMaxAmplitudePanel().setUnlimited(true);

			getMinPositionPanel().setUnlimited(true);
			getMaxPositionPanel().setUnlimited(true);

			getMinScalePanel().setUnlimited(true);
			getMaxScalePanel().setUnlimited(true);

			getMinFrequencyPanel().setUnlimited(true);
			getMaxFrequencyPanel().setUnlimited(true);

			getMinPhasePanel().setUnlimited(true);
			getMaxPhasePanel().setUnlimited(true);

		}

	}

}
