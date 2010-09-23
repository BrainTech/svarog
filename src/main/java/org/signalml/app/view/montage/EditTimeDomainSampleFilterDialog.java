/* EditTimeDomainSampleFilterDialog.java created 2010-09-23
 *
 */

package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType;
import org.signalml.domain.montage.filter.iirdesigner.BadFilterParametersException;
import org.signalml.domain.montage.filter.iirdesigner.FilterCoefficients;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;
import org.signalml.domain.montage.filter.iirdesigner.IIRDesigner;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** EditTimeDomainSampleFilterDialog
 *
 *
 * @author Piotr Szachewicz
 */
public class EditTimeDomainSampleFilterDialog extends EditSampleFilterDialog {


	private TimeDomainSampleFilter currentFilter;

	private JPanel filterParametersPanel;

	private ResolvableComboBox filterTypeComboBox;
	private ResolvableComboBox filterFamilyComboBox;
	private JSpinner passbandEdgeFrequency1Spinner;
	private JSpinner passbandEdgeFrequency2Spinner;
	private JSpinner stopbandEdgeFrequency1Spinner;
	private JSpinner stopbandEdgeFrequency2Spinner;
	private JSpinner passbandRippleSpinner;
	private JSpinner stopbandAttenuationSpinner;


	public EditTimeDomainSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	public EditTimeDomainSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("editTimeDomainSampleFilter.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/editfilter.png"));
		setResizable(false);

		super.initialize();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {

				if (((FilterType) filterTypeComboBox.getSelectedItem()).isHighpass() || ((FilterType) filterTypeComboBox.getSelectedItem()).isLowpass()) {
					getPassbandEdgeFrequency2Spinner().setEnabled(false);
					getStopbandEdgeFrequency2Spinner().setEnabled(false);
				}
				//getCoefficientChartPanel().setSelectionHighlightStart(((Number) getFromFrequencySpinner().getValue()).doubleValue());
				//getCoefficientChartPanel().setSelectionHighlightEnd(((Number) getToFrequencySpinner().getValue()).doubleValue());
			}

		});
	}

	@Override
	public JComponent createInterface() {

		JPanel descriptionPanel = getDescriptionPanel();
		JPanel graphPanel = getGraphPanel();

		JPanel editFilterParametersPanel = new JPanel(new BorderLayout(3, 3));

		editFilterParametersPanel.setBorder(new TitledBorder(messageSource.getMessage("editTimeDomainSampleFilter.filterParametersTitle")));

//		JPanel addNewRangeButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));
//		addNewRangeButtonPanel.add(getAddNewRangeButton());

		editFilterParametersPanel.add(getFilterParametersPanel(), BorderLayout.CENTER);
//		editFilterParametersPanel.add(addNewRangeButtonPanel, BorderLayout.SOUTH);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(descriptionPanel, BorderLayout.NORTH);
		interfacePanel.add(graphPanel, BorderLayout.CENTER);
		interfacePanel.add(editFilterParametersPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	@Override
	public JSpinner getGraphScaleSpinner() {

		if (graphScaleSpinner == null) {

			graphScaleSpinner = super.getGraphScaleSpinner();
			/*graphScaleSpinner.addChangeListener(new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					graphFrequencyMax = ((Number) graphScaleSpinner.getValue()).doubleValue();
					updateGraph();

					if(getUnlimitedCheckBox().isSelected()) {
						getFrequencyResponseChartPanel().setSelectionHighlightEnd(graphFrequencyMax);
					}

				}

			});*/
		}
		return graphScaleSpinner;

	}

	@Override
	public NumberAxis getGainAxis() {
		if (gainAxis == null) {
			gainAxis = new NumberAxis();
			gainAxis.setAutoRange(false);
			gainAxis.setTickUnit(new NumberTickUnit(1));
		}
		return gainAxis;
	}

	public ResolvableComboBox getFilterTypeComboBox() {

		if (filterTypeComboBox == null) {

			filterTypeComboBox = new ResolvableComboBox(messageSource);
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
					}
					else {
						getPassbandEdgeFrequency2Spinner().setEnabled(true);
						getStopbandEdgeFrequency2Spinner().setEnabled(true);
					}

					getPassbandEdgeFrequency1Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getStopbandEdgeFrequency1Spinner()));
					getStopbandEdgeFrequency1Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getPassbandEdgeFrequency1Spinner()));
					getPassbandEdgeFrequency2Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getStopbandEdgeFrequency2Spinner()));
					getStopbandEdgeFrequency2Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getPassbandEdgeFrequency2Spinner()));

				}

			});

		}

		return filterTypeComboBox;

	}

	public ResolvableComboBox getFilterFamilyComboBox() {
		if (filterFamilyComboBox == null) {
			filterFamilyComboBox = new ResolvableComboBox(messageSource);
			filterFamilyComboBox.setPreferredSize(new Dimension(200, 25));

			ApproximationFunctionType[] filterFamilyTypes = ApproximationFunctionType.values();

			DefaultComboBoxModel model = new DefaultComboBoxModel(filterFamilyTypes);
			filterFamilyComboBox.setModel(model);

		}
		return filterFamilyComboBox;
	}

	public JPanel getFilterParametersPanel() {
		if (filterParametersPanel == null) {

			filterParametersPanel = new JPanel(null);

			filterParametersPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

			GroupLayout layout = new GroupLayout(filterParametersPanel);
			filterParametersPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel filterTypeLabel = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.filterType"));
			JLabel filterFamilyLabel = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.filterFamily"));
			JLabel passbandEdgeFrequency1Label = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.passbandEdgeFrequency1"));
			JLabel passbandEdgeFrequency2Label = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.passbandEdgeFrequency2"));
			JLabel stopbandEdgeFrequency1Label = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.stopbandEdgeFrequency1"));
			JLabel stopbandEdgeFrequency2Label = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.stopbandEdgeFrequency2"));
			JLabel passbandRippleLabel = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.passbandRipple"));
			JLabel stopbandAttenuationLabel = new JLabel(messageSource.getMessage("editTimeDomainSampleFilter.stopbandAttenuation"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(filterTypeLabel)
			        .addComponent(passbandEdgeFrequency1Label)
			        .addComponent(stopbandEdgeFrequency1Label)
			        .addComponent(passbandRippleLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(getFilterTypeComboBox())
			        .addComponent(getPassbandEdgeFrequency1Spinner())
			        .addComponent(getStopbandEdgeFrequency1Spinner())
			        .addComponent(getPassbandRippleSpinner())
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(filterFamilyLabel)
			        .addComponent(passbandEdgeFrequency2Label)
			        .addComponent(stopbandEdgeFrequency2Label)
			        .addComponent(stopbandAttenuationLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(getFilterFamilyComboBox())
			        .addComponent(getPassbandEdgeFrequency2Spinner())
			        .addComponent(getStopbandEdgeFrequency2Spinner())
			        .addComponent(getStopbandAttenuationSpinner())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.BASELINE)
			        .addComponent(filterTypeLabel)
			        .addComponent(getFilterTypeComboBox())
			        .addComponent(filterFamilyLabel)
			        .addComponent(getFilterFamilyComboBox())
			);

			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.BASELINE)
			        .addComponent(passbandEdgeFrequency1Label)
			        .addComponent(getPassbandEdgeFrequency1Spinner())
			        .addComponent(passbandEdgeFrequency2Label)
			        .addComponent(getPassbandEdgeFrequency2Spinner())
			);
			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.BASELINE)
			        .addComponent(stopbandEdgeFrequency1Label)
			        .addComponent(getStopbandEdgeFrequency1Spinner())
			        .addComponent(stopbandEdgeFrequency2Label)
			        .addComponent(getStopbandEdgeFrequency2Spinner())
			);
			vGroup.addGroup(
			        layout.createParallelGroup(Alignment.BASELINE)
			        .addComponent(passbandRippleLabel)
			        .addComponent(getPassbandRippleSpinner())
			        .addComponent(stopbandAttenuationLabel)
			        .addComponent(getStopbandAttenuationSpinner())
			);

			layout.setVerticalGroup(vGroup);

		}
		return filterParametersPanel;
	}

	public JSpinner getPassbandEdgeFrequency1Spinner() {

		if (passbandEdgeFrequency1Spinner == null) {

			passbandEdgeFrequency1Spinner = new JSpinner(new SpinnerNumberModel(0.25, 0.0, getCurrentSamplingFrequency() / 2, 0.25));
			passbandEdgeFrequency1Spinner.setPreferredSize(new Dimension(80, 25));

			passbandEdgeFrequency1Spinner.setEditor(new JSpinner.NumberEditor(passbandEdgeFrequency1Spinner, "0.00"));
			passbandEdgeFrequency1Spinner.setFont(passbandEdgeFrequency1Spinner.getFont().deriveFont(Font.PLAIN));


			passbandEdgeFrequency1Spinner.addChangeListener(new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					FilterType filterType = (FilterType)getFilterTypeComboBox().getSelectedItem();
					double value = ((Number) passbandEdgeFrequency1Spinner.getValue()).doubleValue();
					double otherValue;


					if (filterType.isLowpass() || filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getStopbandEdgeFrequency1Spinner().setValue(value + 0.25);
						}

					}
					else if (filterType.isHighpass() || filterType.isBandpass()) {

						otherValue = ((Number) getStopbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getStopbandEdgeFrequency1Spinner().setValue(value - 0.25);
						}

					}

					if (filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getPassbandEdgeFrequency2Spinner().setValue(value + 0.25);
						}

					}

					if (value < 0)
						passbandEdgeFrequency1Spinner.setValue(0.0);

				}

			});


		}

		return passbandEdgeFrequency1Spinner;

	}

	public JSpinner getPassbandEdgeFrequency2Spinner() {

		if (passbandEdgeFrequency2Spinner == null) {

			passbandEdgeFrequency2Spinner = new JSpinner(new SpinnerNumberModel(0.25, 0.0, getCurrentSamplingFrequency() / 2, 0.25));
			passbandEdgeFrequency2Spinner.setPreferredSize(new Dimension(80,25));

			passbandEdgeFrequency2Spinner.setEditor(new JSpinner.NumberEditor(passbandEdgeFrequency2Spinner, "0.00"));
			passbandEdgeFrequency2Spinner.setFont(passbandEdgeFrequency2Spinner.getFont().deriveFont(Font.PLAIN));


			passbandEdgeFrequency2Spinner.addChangeListener(new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					FilterType filterType = (FilterType)getFilterTypeComboBox().getSelectedItem();
					double value = ((Number) passbandEdgeFrequency2Spinner.getValue()).doubleValue();
					double otherValue;


					if (filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getStopbandEdgeFrequency2Spinner().setValue(value - 0.25);
						}

					}
					else if (filterType.isBandpass()) {

						otherValue = ((Number) getStopbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getStopbandEdgeFrequency2Spinner().setValue(value + 0.25);
						}

					}

					if (filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getPassbandEdgeFrequency1Spinner().setValue(value - 0.25);
						}

					}

					if (value > getCurrentSamplingFrequency() / 2)
						passbandEdgeFrequency2Spinner.setValue(getCurrentSamplingFrequency() /2);

				}

			});

		}

		return passbandEdgeFrequency2Spinner;

	}

	public JSpinner getStopbandEdgeFrequency1Spinner() {

		if (stopbandEdgeFrequency1Spinner == null) {

			stopbandEdgeFrequency1Spinner = new JSpinner(new SpinnerNumberModel(0.25, 0.0, getCurrentSamplingFrequency() / 2, 0.25));
			stopbandEdgeFrequency1Spinner.setPreferredSize(new Dimension(80,25));

			stopbandEdgeFrequency1Spinner.setEditor(new JSpinner.NumberEditor(stopbandEdgeFrequency1Spinner, "0.00"));
			stopbandEdgeFrequency1Spinner.setFont(stopbandEdgeFrequency1Spinner.getFont().deriveFont(Font.PLAIN));

			stopbandEdgeFrequency1Spinner.addChangeListener(new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					FilterType filterType = (FilterType)getFilterTypeComboBox().getSelectedItem();
					double value = ((Number) stopbandEdgeFrequency1Spinner.getValue()).doubleValue();
					double otherValue;


					if (filterType.isLowpass() || filterType.isBandstop()) {

						otherValue = ((Number) getPassbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getPassbandEdgeFrequency1Spinner().setValue(value - 0.25);
						}

					}
					else if (filterType.isHighpass() || filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getPassbandEdgeFrequency1Spinner().setValue(value + 0.25);
						}

					}

					if (filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getStopbandEdgeFrequency2Spinner().setValue(value + 0.25);
						}

					}

					if (value < 0)
						stopbandEdgeFrequency1Spinner.setValue(0.0);

				}

			});


		}

		return stopbandEdgeFrequency1Spinner;

	}

	public JSpinner getStopbandEdgeFrequency2Spinner() {
		if (stopbandEdgeFrequency2Spinner == null) {

			stopbandEdgeFrequency2Spinner = new JSpinner(new SpinnerNumberModel(0.25, 0.0, getCurrentSamplingFrequency() / 2, 0.25));
			stopbandEdgeFrequency2Spinner.setPreferredSize(new Dimension(80, 25));

			stopbandEdgeFrequency2Spinner.setEditor(new JSpinner.NumberEditor(stopbandEdgeFrequency2Spinner, "0.00"));
			stopbandEdgeFrequency2Spinner.setFont(stopbandEdgeFrequency2Spinner.getFont().deriveFont(Font.PLAIN));

			stopbandEdgeFrequency2Spinner.addChangeListener(new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					FilterType filterType = (FilterType)getFilterTypeComboBox().getSelectedItem();
					double value = ((Number) stopbandEdgeFrequency2Spinner.getValue()).doubleValue();
					double otherValue;


					if (filterType.isBandstop()) {

						otherValue = ((Number) getPassbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getPassbandEdgeFrequency2Spinner().setValue(value + 0.25);
						}

					}
					else if (filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getPassbandEdgeFrequency2Spinner().setValue(value - 0.25);
						}

					}

					if (filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getStopbandEdgeFrequency1Spinner().setValue(value - 0.25);
						}

					}

					if (value > getCurrentSamplingFrequency() / 2)
						stopbandEdgeFrequency2Spinner.setValue(getCurrentSamplingFrequency() /2);

				}

			});

		}

		return stopbandEdgeFrequency2Spinner;

	}

	public JSpinner getPassbandRippleSpinner() {

		if (passbandRippleSpinner == null) {

			passbandRippleSpinner = new JSpinner(new SpinnerNumberModel(3.0, 0.25, 10.0, 0.25));
			passbandRippleSpinner.setPreferredSize(new Dimension(80, 25));

			passbandRippleSpinner.setEditor(new JSpinner.NumberEditor(passbandRippleSpinner, "0.00"));
			passbandRippleSpinner.setFont(passbandRippleSpinner.getFont().deriveFont(Font.PLAIN));

		}

		return passbandRippleSpinner;

	}

	public JSpinner getStopbandAttenuationSpinner() {

		if (stopbandAttenuationSpinner == null) {

			stopbandAttenuationSpinner = new JSpinner(new SpinnerNumberModel(30.0, 10.25, 100.0, 0.25));
			stopbandAttenuationSpinner.setPreferredSize(new Dimension(80, 25));

			stopbandAttenuationSpinner.setEditor(new JSpinner.NumberEditor(stopbandAttenuationSpinner, "0.00"));
			stopbandAttenuationSpinner.setFont(stopbandAttenuationSpinner.getFont().deriveFont(Font.PLAIN));

		}

		return stopbandAttenuationSpinner;

	}

	@Override
	protected void updateGraph() {

		/*if(currentFilter == null) {
			return;
		}

		int frequencyCnt = (int) Math.ceil(getGraphFrequencyMax() / 0.25) + 1;
		double[] frequencies = new double[frequencyCnt];
		double[] coefficients = new double[frequencyCnt];
		int i;
		double frequency = 0;

		for(i=0; i<frequencyCnt; i++) {
			frequencies[i] = frequency;
			frequency += 0.25;
		}

		Iterator<Range> it = currentFilter.getRangeIterator();
		Range range;
		double limit;
		float lowFrequency;
		float highFrequency;
		double coefficient;
		double maxCoefficient = 0;

		while(it.hasNext()) {

			range = it.next();

			lowFrequency = range.getLowFrequency();
			if(lowFrequency > getGraphFrequencyMax()) {
				break;
			}

			highFrequency = range.getHighFrequency();
			coefficient = range.getCoefficient();

			if(highFrequency <= lowFrequency) {
				limit = getGraphFrequencyMax();
			} else {
				limit = Math.min(highFrequency, getGraphFrequencyMax());
			}

			int index;
			for(frequency=lowFrequency; frequency<=limit; frequency += 0.25) {

				index = (int) (frequency / 0.25);
				coefficients[index] = coefficient;

			}

			if(coefficient > maxCoefficient) {
				maxCoefficient = coefficient;
			}

		}

		maxCoefficient *= 1.1;
		if(maxCoefficient < 1) {
			maxCoefficient  = 1;
		}

		double unit = Math.max(4, Math.round(getGraphFrequencyMax() / (16*4)) * 4);
		NumberAxis axis = getFrequencyAxis();
		axis.setRange(0, getGraphFrequencyMax());
		axis.setTickUnit(new NumberTickUnit(unit));

		getGainAxis().setRange(0, maxCoefficient);

		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("data", new double[][] { frequencies, coefficients });
		getFrequencyResponsePlot().setDataset(dataset);*/

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		currentFilter = new TimeDomainSampleFilter((TimeDomainSampleFilter) model);

		getFilterTypeComboBox().setSelectedItem(currentFilter.getFilterType());
		getFilterFamilyComboBox().setSelectedItem(currentFilter.getApproximationFunctionType());
		getPassbandEdgeFrequency1Spinner().setValue((double) currentFilter.getPassbandEdgeFrequencies()[0]);
		getPassbandEdgeFrequency2Spinner().setValue((double) currentFilter.getPassbandEdgeFrequencies()[1]);
		getStopbandEdgeFrequency1Spinner().setValue((double) currentFilter.getStopbandEdgeFrequencies()[0]);
		getStopbandEdgeFrequency2Spinner().setValue((double) currentFilter.getStopbandEdgeFrequencies()[1]);
		getPassbandRippleSpinner().setValue((double) currentFilter.getPassbandRipple());
		getStopbandAttenuationSpinner().setValue((double) currentFilter.getStopbandAttenuation());

		getDescriptionTextField().setText(currentFilter.getDescription());

		updateGraph();

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		currentFilter.setDescription(getDescriptionTextField().getText());
		currentFilter.setFilterType((FilterType) getFilterTypeComboBox().getSelectedItem());
		currentFilter.setApproximationFunctionType((ApproximationFunctionType) getFilterFamilyComboBox().getSelectedItem());
		currentFilter.setPassbandEdgeFrequencies(
		        new double[] {
		                ((Number)getPassbandEdgeFrequency1Spinner().getValue()).doubleValue(),
		                ((Number)getPassbandEdgeFrequency2Spinner().getValue()).doubleValue()
		        }
		);
		currentFilter.setStopbandEdgeFrequencies(
		        new double[] {
		                ((Number)getStopbandEdgeFrequency1Spinner().getValue()).doubleValue(),
		                ((Number)getStopbandEdgeFrequency2Spinner().getValue()).doubleValue()
		        }
		);
		currentFilter.setPassbandRipple(((Number) getPassbandRippleSpinner().getValue()).doubleValue());
		currentFilter.setStopbandAttenuation(((Number) getStopbandAttenuationSpinner().getValue()).doubleValue());

		currentFilter.setSamplingFrequency(getCurrentSamplingFrequency());

		((TimeDomainSampleFilter)model).copyFrom(currentFilter);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		fillModelFromDialog(currentFilter);
		try {
			FilterCoefficients coeffs = IIRDesigner.designDigitalFilter(currentFilter);
			int filterOrder = coeffs.getFilterOrder();
			if(filterOrder > 7)
				errors.reject("error.editTimeDomainSampleFilter.badFilterParametersFilterOrderTooBig");
		} catch (BadFilterParametersException ex) {
			errors.reject("error.editTimeDomainSampleFilter.badFilterParameters");
		} catch (Exception e) {
			errors.reject("error.editTimeDomainSampleFilter.badFilterParameters");
		}


	}

	@Override
	public Preset getPreset() throws SignalMLException {
		fillModelFromDialog(currentFilter);
		return currentFilter.duplicate();
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		fillDialogFromModel(preset);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return TimeDomainSampleFilter.class.isAssignableFrom(clazz);
	}


	@Override
	public FrequencyResponseChartPanel getFrequencyResponseChartPanel() {
		if (frequencyResponseChartPanel == null) {

			frequencyResponseChartPanel = new TimeDomainFilterFrequencyResponseChartPanel(getFrequencyResponseChart());
			frequencyResponseChartPanel.setBackground(Color.WHITE);
			frequencyResponseChartPanel.setPreferredSize(new Dimension(500, 150));

		}
		return frequencyResponseChartPanel;
	}

	protected class TimeDomainFilterFrequencyResponseChartPanel extends FrequencyResponseChartPanel {

		public TimeDomainFilterFrequencyResponseChartPanel(JFreeChart chart) {
			super(chart);
		}

		@Override
		public void mouseReleased(MouseEvent ev) {
			startFrequency = null;
			hideSelectionHighlight = false;
			clearDragHighlight();
			repaint();

			/*JSpinner spinner = getCoefficientSpinner();
			// XXX ugly hack - this class is utterly hopeless...
			JTextField editor = ((JTextField) ((JSpinner.NumberEditor) spinner.getEditor()).getComponent(0));
			editor.selectAll();
			editor.requestFocusInWindow();*/
		}

		@Override
		public void mouseClicked(MouseEvent ev) {

			double frequency = getFrequency(ev.getPoint());
			if (frequency >= getGraphFrequencyMax()) {
				return;
			}

			//getFromFrequencySpinner().setValue(frequency);
			//getToFrequencySpinner().setValue(frequency+0.25);

		}

		@Override
		public void mouseDragged(MouseEvent ev) {

			if (startFrequency == null) {
				return;
			}

			double startFrequency = this.startFrequency;
			double endFrequency = getFrequency(ev.getPoint());

			if (startFrequency == endFrequency) {
				clearDragHighlight();
				return;
			}

			if (startFrequency > endFrequency) {
				double temp = startFrequency;
				startFrequency = endFrequency;
				endFrequency = temp;
			}

			/*getFromFrequencySpinner().setValue(startFrequency);
			if(endFrequency >= getGraphFrequencyMax()) {
				getToFrequencySpinner().setValue(getGraphFrequencyMax());
				getUnlimitedCheckBox().setSelected(true);
				setDragHighlight(startFrequency, getGraphFrequencyMax());
			} else {
				getUnlimitedCheckBox().setSelected(false);
				getToFrequencySpinner().setValue(endFrequency);
				setDragHighlight(startFrequency, endFrequency);
			}
			*/


		}

	}

}
