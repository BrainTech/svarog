/* TimeDomainFilterParametersPanel.java created 2011-02-17
 *
 */
package org.signalml.app.view.montage.filters;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.components.DoubleSpinner;
import org.signalml.app.view.components.ResolvableComboBox;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.util.Util;

import org.springframework.validation.Errors;

/**
 * Panel consisting of a controls capable of editing the time domain filter parameters
 * (filter type, filter approximation function, passband and stopband frequencies,
 * attenuation etc.)
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainFilterParametersPanel extends JPanel {

	/**
	 * A value of step size for passband and stop band edge frequency
	 * spinners.
	 */
	private final double FREQUENCY_SPINNER_STEP_SIZE = 0.1;
	/**
	 * The value of step size for passband ripple and stopband attenuation
	 * spinners.
	 */
	private final double DECIBELS_SPINNER_STEP_SIZE = 0.1;
	/**
	 * The maximum value which can be set using the decibels spinners
	 * (passband ripple and stopband attenuation spinners).
	 */
	private final double DECIBELS_SPINNER_MAXIMUM_VALUE = 100.0;
	/**
	 * The minimum value which can be set using the decibels spinners.
	 */
	private final double DECIBELS_SPINNER_MINIMUM_VALUE = 0.0;
	/**
	 * The currently edited filter.
	 */
	private TimeDomainSampleFilter currentFilter;
	/**
	 * The sampling frequency of the signal.
	 */
	private double samplingFrequency;
	/**
	 * A {@link JTextField} which can be used to edit the filter's description.
	 */
	private JTextField descriptionTextField;
	/**
	 * A {@link ResolvableComboBox} to select filter's {@link FilterType} from.
	 */
	private ResolvableComboBox filterTypeComboBox;
	/**
	 * A {@link ResolvableComboBox} which can be used  to select filter's
	 * {@link ApproximationFunctionType} from.
	 */
	private ResolvableComboBox filterFamilyComboBox;
	/**
	 * The spinner controlling the value of the first passband edge
	 * frequency of the filter.
	 */
	private DoubleSpinner passbandEdgeFrequency1Spinner;
	/**
	 * A {@link JSpinner} controlling the value of the second passband edge
	 * frequency of the filter.
	 */
	private DoubleSpinner passbandEdgeFrequency2Spinner;
	/**
	 * A {@link JSpinner} controlling the value of the first stopband edge
	 * frequency of the filter.
	 */
	private DoubleSpinner stopbandEdgeFrequency1Spinner;
	/**
	 * A spinner controlling the value of the second stopband edge
	 * frequency of the filter.
	 */
	private DoubleSpinner stopbandEdgeFrequency2Spinner;
	/**
	 * A {@link JSpinner} controlling the value of filter's maximum ripple
	 * in the passband.
	 */
	private DoubleSpinner passbandRippleSpinner;
	/**
	 * A {@link JSpinner} controlling the value of filter's minimum
	 * attenuation in the stopband.
	 */
	private DoubleSpinner stopbandAttenuationSpinner;

	/**
	 * Constructor.
	 */
	public TimeDomainFilterParametersPanel() {
		createInterface();
	}

	/**
	 * Creates all components and puts them in appropriate places on this panel.
	 */
	protected void createInterface() {
		JPanel filterParametersPanel = new JPanel(null);

		filterParametersPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

		GroupLayout layout = new GroupLayout(filterParametersPanel);
		filterParametersPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel descriptionLabel = new JLabel(_("Filter description"));
		JLabel filterTypeLabel = new JLabel(_("Filter type"));
		JLabel filterFamilyLabel = new JLabel(_("Filter family"));
		JLabel passbandEdgeFrequency1Label = new JLabel(_("Passband edge frequency 1 [Hz]"));
		JLabel passbandEdgeFrequency2Label = new JLabel(_("Passband edge frequency 2 [Hz]"));
		JLabel stopbandEdgeFrequency1Label = new JLabel(_("Stopband edge frequency 1 [Hz]"));
		JLabel stopbandEdgeFrequency2Label = new JLabel(_("Stopband edge frequency 2 [Hz]"));
		JLabel passbandRippleLabel = new JLabel(_("Passband ripple [dB]"));
		JLabel stopbandAttenuationLabel = new JLabel(_("Stopband attenuation [dB]"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup().addComponent(descriptionLabel).addComponent(filterTypeLabel).addComponent(passbandEdgeFrequency1Label).addComponent(stopbandEdgeFrequency1Label).addComponent(passbandRippleLabel));

		hGroup.addGroup(
			layout.createParallelGroup().addComponent(getDescriptionTextField()).addComponent(getFilterTypeComboBox()).addComponent(getPassbandEdgeFrequency1Spinner()).addComponent(getStopbandEdgeFrequency1Spinner()).addComponent(getPassbandRippleSpinner()));

		hGroup.addGroup(
			layout.createParallelGroup().addComponent(filterFamilyLabel).addComponent(passbandEdgeFrequency2Label).addComponent(stopbandEdgeFrequency2Label).addComponent(stopbandAttenuationLabel));

		hGroup.addGroup(
			layout.createParallelGroup().addComponent(getFilterFamilyComboBox()).addComponent(getPassbandEdgeFrequency2Spinner()).addComponent(getStopbandEdgeFrequency2Spinner()).addComponent(getStopbandAttenuationSpinner()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE).addComponent(descriptionLabel).addComponent(getDescriptionTextField()));

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE).addComponent(filterTypeLabel).addComponent(getFilterTypeComboBox()).addComponent(filterFamilyLabel).addComponent(getFilterFamilyComboBox()));

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE).addComponent(passbandEdgeFrequency1Label).addComponent(getPassbandEdgeFrequency1Spinner()).addComponent(passbandEdgeFrequency2Label).addComponent(getPassbandEdgeFrequency2Spinner()));
		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE).addComponent(stopbandEdgeFrequency1Label).addComponent(getStopbandEdgeFrequency1Spinner()).addComponent(stopbandEdgeFrequency2Label).addComponent(getStopbandEdgeFrequency2Spinner()));
		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE).addComponent(passbandRippleLabel).addComponent(getPassbandRippleSpinner()).addComponent(stopbandAttenuationLabel).addComponent(getStopbandAttenuationSpinner()));

		layout.setVerticalGroup(vGroup);

		this.setLayout(new GridLayout(1, 1));
		this.add(filterParametersPanel);
	}

	/**
	 * Returns the {@link JTextField} which is shown in this panel and
	 * can be used to edit the filter's description.
	 * @return the {@link JTextField} to edit the filter's description
	 */
	public JTextField getDescriptionTextField() {

		if (descriptionTextField == null) {
			descriptionTextField = new JTextField();
			descriptionTextField.setPreferredSize(new Dimension(200, 25));
		}
		return descriptionTextField;

	}

	/**
	 * Returns the {@link ResolvableComboBox} which can be used to set
	 * the type of the filter (i.e. whether the filter is low-pass, high-pass,
	 * band-pass or band-stop).
	 * @return a {@link ResolvableComboBox} used to set the type of this
	 * filter
	 */
	public ResolvableComboBox getFilterTypeComboBox() {

		if (filterTypeComboBox == null) {

			filterTypeComboBox = new ResolvableComboBox();
			filterTypeComboBox.setPreferredSize(new Dimension(200, 25));

			FilterType[] filterTypes = FilterType.values();

			DefaultComboBoxModel model = new DefaultComboBoxModel(filterTypes);
			filterTypeComboBox.setModel(model);

			filterTypeComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (((FilterType) filterTypeComboBox.getSelectedItem()).isHighpass() || ((FilterType) filterTypeComboBox.getSelectedItem()).isLowpass()) {
						getPassbandEdgeFrequency2Spinner().setEnabled(false);
						getStopbandEdgeFrequency2Spinner().setEnabled(false);
					} else {
						getPassbandEdgeFrequency2Spinner().setEnabled(true);
						getStopbandEdgeFrequency2Spinner().setEnabled(true);
					}
				}
			});

		}

		return filterTypeComboBox;

	}

	/**
	 * Returns the {@link ResolvableComboBox} which can be used to set the
	 * filter family ({@link ApproximationFunctionType}).
	 * @return the {@link ResolvableComboBox} which can be used to set the
	 * filter family
	 */
	public ResolvableComboBox getFilterFamilyComboBox() {

		if (filterFamilyComboBox == null) {
			filterFamilyComboBox = new ResolvableComboBox();
			filterFamilyComboBox.setPreferredSize(new Dimension(200, 25));

			ApproximationFunctionType[] filterFamilyTypes = ApproximationFunctionType.values();

			DefaultComboBoxModel model = new DefaultComboBoxModel(filterFamilyTypes);
			filterFamilyComboBox.setModel(model);

		}
		return filterFamilyComboBox;

	}

	/**
	 * Returns the {@link JSpinner} allowing to set the first passband
	 * edge frequency of this filter.
	 * @return the {@link JSpinner} allowing to set the first passband
	 * edge frequency of this filter
	 */
	public DoubleSpinner getPassbandEdgeFrequency1Spinner() {

		if (passbandEdgeFrequency1Spinner == null) {

			passbandEdgeFrequency1Spinner = new DoubleSpinner(createFrequencySpinnerNumberModel());
			passbandEdgeFrequency1Spinner.setPreferredSize(new Dimension(80, 25));

			passbandEdgeFrequency1Spinner.setEditor(new JSpinner.NumberEditor(passbandEdgeFrequency1Spinner, "0.00"));
			passbandEdgeFrequency1Spinner.setFont(passbandEdgeFrequency1Spinner.getFont().deriveFont(Font.PLAIN));

		}

		return passbandEdgeFrequency1Spinner;

	}

	/**
	 * Returns the {@link JSpinner} allowing to set the second passband
	 * edge frequency of this filter.
	 * @return the {@link JSpinner} allowing to set the second passband
	 * edge frequency of this filter
	 */
	public DoubleSpinner getPassbandEdgeFrequency2Spinner() {

		if (passbandEdgeFrequency2Spinner == null) {

			passbandEdgeFrequency2Spinner = new DoubleSpinner(createFrequencySpinnerNumberModel());
			passbandEdgeFrequency2Spinner.setPreferredSize(new Dimension(80, 25));

			passbandEdgeFrequency2Spinner.setEditor(new JSpinner.NumberEditor(passbandEdgeFrequency2Spinner, "0.00"));
			passbandEdgeFrequency2Spinner.setFont(passbandEdgeFrequency2Spinner.getFont().deriveFont(Font.PLAIN));

		}

		return passbandEdgeFrequency2Spinner;

	}

	/**
	 * Returns the {@link JSpinner} allowing to set the first stopband
	 * edge frequency of this filter.
	 * @return the {@link JSpinner} allowing to set the first stopband
	 * edge frequency of this filter
	 */
	public DoubleSpinner getStopbandEdgeFrequency1Spinner() {

		if (stopbandEdgeFrequency1Spinner == null) {

			stopbandEdgeFrequency1Spinner = new DoubleSpinner(createFrequencySpinnerNumberModel());
			stopbandEdgeFrequency1Spinner.setPreferredSize(new Dimension(80, 25));

			stopbandEdgeFrequency1Spinner.setEditor(new JSpinner.NumberEditor(stopbandEdgeFrequency1Spinner, "0.00"));
			stopbandEdgeFrequency1Spinner.setFont(stopbandEdgeFrequency1Spinner.getFont().deriveFont(Font.PLAIN));

		}

		return stopbandEdgeFrequency1Spinner;

	}

	/**
	 * Returns the {@link JSpinner} allowing to set the second stopband
	 * edge frequency of this filter.
	 * @return the {@link JSpinner} allowing to set the second stopband
	 * edge frequency of this filter
	 */
	public DoubleSpinner getStopbandEdgeFrequency2Spinner() {

		if (stopbandEdgeFrequency2Spinner == null) {

			stopbandEdgeFrequency2Spinner = new DoubleSpinner(createFrequencySpinnerNumberModel());
			stopbandEdgeFrequency2Spinner.setPreferredSize(new Dimension(80, 25));

			stopbandEdgeFrequency2Spinner.setEditor(new JSpinner.NumberEditor(stopbandEdgeFrequency2Spinner, "0.00"));
			stopbandEdgeFrequency2Spinner.setFont(stopbandEdgeFrequency2Spinner.getFont().deriveFont(Font.PLAIN));

		}

		return stopbandEdgeFrequency2Spinner;

	}

	/**
	 * Returns the {@link JSpinner} used in this window to control the
	 * maximum passband ripple for this filter.
	 * @return the {@link JSpinner} for this window, which can be used
	 * to control the passband ripple of this filter
	 */
	public DoubleSpinner getPassbandRippleSpinner() {

		if (passbandRippleSpinner == null) {

			passbandRippleSpinner = new DoubleSpinner(new SpinnerNumberModel(3.0, DECIBELS_SPINNER_MINIMUM_VALUE, DECIBELS_SPINNER_MAXIMUM_VALUE, DECIBELS_SPINNER_STEP_SIZE));
			passbandRippleSpinner.setPreferredSize(new Dimension(80, 25));

			passbandRippleSpinner.setEditor(new JSpinner.NumberEditor(passbandRippleSpinner, "0.00"));
			passbandRippleSpinner.setFont(passbandRippleSpinner.getFont().deriveFont(Font.PLAIN));

		}

		return passbandRippleSpinner;

	}

	/**
	 * Returns the {@link JSpinner} used in this window to control the
	 * maximum passband ripple for this filter.
	 * @return the {@link JSpinner} for this window, which can be used
	 * to control the passband ripple of this filter
	 */
	public DoubleSpinner getStopbandAttenuationSpinner() {

		if (stopbandAttenuationSpinner == null) {

			stopbandAttenuationSpinner = new DoubleSpinner(new SpinnerNumberModel(30.0, DECIBELS_SPINNER_MINIMUM_VALUE, DECIBELS_SPINNER_MAXIMUM_VALUE, DECIBELS_SPINNER_STEP_SIZE));
			stopbandAttenuationSpinner.setPreferredSize(new Dimension(80, 25));

			stopbandAttenuationSpinner.setEditor(new JSpinner.NumberEditor(stopbandAttenuationSpinner, "0.00"));
			stopbandAttenuationSpinner.setFont(stopbandAttenuationSpinner.getFont().deriveFont(Font.PLAIN));

		}

		return stopbandAttenuationSpinner;

	}

	/**
	 * Returns a {@link SpinnerNumberModel} which should be used for
	 * passband and stopband edge frequency spinners.
	 * @return a {@link SpinnerNumberModel} to be used with frequency
	 * spinners
	 */
	protected SpinnerNumberModel createFrequencySpinnerNumberModel() {
		return new SpinnerNumberModel(0.0, 0.0, getCurrentSamplingFrequency() / 2, FREQUENCY_SPINNER_STEP_SIZE);
	}

	/**
	 * Sets all of the controls to the values describing the given filter.
	 * @param model the filter to be 'shown' on the controls
	 */
	public void fillPanelFromModel(TimeDomainSampleFilter model) {

		currentFilter = new TimeDomainSampleFilter(model);
		currentFilter.setSamplingFrequency(getCurrentSamplingFrequency());

		getDescriptionTextField().setText(currentFilter.getDescription());
		getFilterTypeComboBox().setSelectedItem(currentFilter.getFilterType());
		getFilterFamilyComboBox().setSelectedItem(currentFilter.getApproximationFunctionType());
		getPassbandEdgeFrequency1Spinner().setValue((double) currentFilter.getPassbandEdgeFrequencies()[0]);
		getPassbandEdgeFrequency2Spinner().setValue((double) currentFilter.getPassbandEdgeFrequencies()[1]);
		getStopbandEdgeFrequency1Spinner().setValue((double) currentFilter.getStopbandEdgeFrequencies()[0]);
		getStopbandEdgeFrequency2Spinner().setValue((double) currentFilter.getStopbandEdgeFrequencies()[1]);
		getPassbandRippleSpinner().setValue((double) currentFilter.getPassbandRipple());
		getStopbandAttenuationSpinner().setValue((double) currentFilter.getStopbandAttenuation());

	}

	/**
	 * Sets all of the vales in the given filter to match the values
	 * set by the controls.
	 * @param model the model to be filled
	 */
	public void fillModelFromPanel(TimeDomainSampleFilter model) {

		currentFilter.setDescription(getDescriptionTextField().getText());
		currentFilter.setFilterType((FilterType) getFilterTypeComboBox().getSelectedItem());
		currentFilter.setApproximationFunctionType((ApproximationFunctionType) getFilterFamilyComboBox().getSelectedItem());
		currentFilter.setPassbandEdgeFrequencies(
			new double[]{
				getPassbandEdgeFrequency1Spinner().getValue(),
				getPassbandEdgeFrequency2Spinner().getValue()
			});
		currentFilter.setStopbandEdgeFrequencies(
			new double[]{
				getStopbandEdgeFrequency1Spinner().getValue(),
				getStopbandEdgeFrequency2Spinner().getValue()
			});
		currentFilter.setPassbandRipple(getPassbandRippleSpinner().getValue());
		currentFilter.setStopbandAttenuation(getStopbandAttenuationSpinner().getValue());

		currentFilter.setSamplingFrequency(getCurrentSamplingFrequency());

		model.copyFrom(currentFilter);

	}

	/**
	 * Sets the sampling frequency which should be used by this panel
	 * (it determines the maximum frequency which can be set
	 * when using the frequency spinners).
	 * @param samplingFrequency the current value of the sampling frequency
	 */
	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
		updateFrequencySpinnersMaximumValues();
	}

	/**
	 * Returns the maximum frequency which can be set using
	 * the frequency spinners.
	 * @return maximum frequency which can be set in this panel
	 */
	private double getMaximumFrequencyValue() {
		return samplingFrequency / 2;
	}

	/**
	 * Updates the frequency spinners so that the maximum frequency which
	 * can be set using them is appropriate (half of the sampling frequency).
	 */
	private void updateFrequencySpinnersMaximumValues() {
		double maximumFrequencyValue = getMaximumFrequencyValue();
		getPassbandEdgeFrequency1Spinner().setMaximumValue(maximumFrequencyValue);
		getPassbandEdgeFrequency2Spinner().setMaximumValue(maximumFrequencyValue);
		getStopbandEdgeFrequency1Spinner().setMaximumValue(maximumFrequencyValue);
		getStopbandEdgeFrequency2Spinner().setMaximumValue(maximumFrequencyValue);
	}

	/**
	 * Returns the current sampling frequency.
	 * @return the current sampling frequency
	 */
	private double getCurrentSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Validates if data entered in this panel is correct.
	 * @param model the model for this dialog
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Object model, ValidationErrors errors) {

		String description = getDescriptionTextField().getText();
		if (description == null || description.isEmpty()) {
			errors.addError(_("A filter must have a description"));
		} else if (Util.hasSpecialChars(description)) {
			errors.addError(_("Filter description must not contain control characters"));
		}

	}
}
