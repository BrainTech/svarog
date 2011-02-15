/* EditTimeDomainSampleFilterDialog.java created 2010-09-23
 *
 */
package org.signalml.app.view.montage.filters;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.app.view.montage.filters.charts.TimeDomainFilterResponseChartGroupPanel;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType;
import org.signalml.domain.montage.filter.iirdesigner.BadFilterParametersException;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;

/**
 * This class represents a dialog for {@link TimeDomainSampleFilter
 * TimeDomainSampleFilters} editing.
 *
 * @author Piotr Szachewicz
 */
public class EditTimeDomainSampleFilterDialog extends EditSampleFilterDialog {

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
	 * represents the currently edited filter
	 */
	private TimeDomainSampleFilter currentFilter;
	/**
	 * A panel containing controls allowing to change the filter's
	 * parameters.
	 */
	private JPanel filterParametersPanel;
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
	 * An action called after pressing the
	 * {@link EditTimeDomainSampleFilterDialog#drawFrequencyResponseButton}.
	 */
	private DrawFrequencyResponseAction drawFrequencyResponseAction;
	/**
	 * The button which can be used to draw the frequency response
	 * for the parametrs set in the
	 * {@link EditTimeDomainSampleFilterDialog#filterParametersPanel}.
	 */
	private JButton drawFrequencyResponseButton;
	protected TimeDomainFilterResponseChartGroupPanel graphsPanel;

	/**
	 * Constructor. Sets the message source, parent window, preset manager
	 * for time domain filters and if this dialog blocks top-level windows.
	 * @param messageSource message source to set
	 * @param presetManager a {@link PresetManager} to manage the presets
	 * configured in this window
	 * @param w the parent window or null if there is no parent
	 * @param isModal true if this dialog should block top-level windows,
	 * false otherwise
	 */
	public EditTimeDomainSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	/**
	 * Constructor. Sets the message source and a preset manager
	 * for this window.
	 * @param messageSource message source to set
	 * @param presetManager a {@link PresetManager} to manage the presets
	 * configured in this window
	 */
	public EditTimeDomainSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	@Override
	protected void initialize() {

		setTitle(messageSource.getMessage("editTimeDomainSampleFilter.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/editfilter.png"));
		setResizable(false);
		drawFrequencyResponseAction = new DrawFrequencyResponseAction();

		super.initialize();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {

				if (((FilterType) filterTypeComboBox.getSelectedItem()).isHighpass() || ((FilterType) filterTypeComboBox.getSelectedItem()).isLowpass()) {
					getPassbandEdgeFrequency2Spinner().setEnabled(false);
					getStopbandEdgeFrequency2Spinner().setEnabled(false);
				}

			}
		});

	}

	@Override
	public JComponent createInterface() {

		JPanel descriptionPanel = getDescriptionPanel();
		JPanel graphPanel = getChartGroupPanelWithABorder();

		JPanel editFilterParametersPanel = new JPanel(new BorderLayout(3, 3));

		editFilterParametersPanel.setBorder(new TitledBorder(messageSource.getMessage("editTimeDomainSampleFilter.filterParametersTitle")));

		JPanel drawFrequencyResponseButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));
		drawFrequencyResponseButtonPanel.add(getDrawFrequencyResponseButton());

		editFilterParametersPanel.add(getFilterParametersPanel(), BorderLayout.CENTER);
		editFilterParametersPanel.add(drawFrequencyResponseButtonPanel, BorderLayout.SOUTH);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(descriptionPanel, BorderLayout.NORTH);
		interfacePanel.add(graphPanel, BorderLayout.CENTER);
		interfacePanel.add(editFilterParametersPanel, BorderLayout.SOUTH);

		return interfacePanel;

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
					} else {
						getPassbandEdgeFrequency2Spinner().setEnabled(true);
						getStopbandEdgeFrequency2Spinner().setEnabled(true);
					}

					getPassbandEdgeFrequency1Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getStopbandEdgeFrequency1Spinner()));
					getStopbandEdgeFrequency1Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getStopbandEdgeFrequency1Spinner()));
					getPassbandEdgeFrequency2Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getStopbandEdgeFrequency2Spinner()));
					getStopbandEdgeFrequency2Spinner().getListeners(ChangeListener.class)[0].stateChanged(new ChangeEvent(getPassbandEdgeFrequency2Spinner()));

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
			filterFamilyComboBox = new ResolvableComboBox(messageSource);
			filterFamilyComboBox.setPreferredSize(new Dimension(200, 25));

			ApproximationFunctionType[] filterFamilyTypes = ApproximationFunctionType.values();

			DefaultComboBoxModel model = new DefaultComboBoxModel(filterFamilyTypes);
			filterFamilyComboBox.setModel(model);

		}
		return filterFamilyComboBox;

	}

	/**
	 * Returns the {@link JPanel} containing controls allowing to set the
	 * filter's parameters.
	 * @return the {@link JPanel} used in this dialog, containing spinners
	 * allowing to control the filter's parameters
	 */
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
				layout.createParallelGroup().addComponent(filterTypeLabel).addComponent(passbandEdgeFrequency1Label).addComponent(stopbandEdgeFrequency1Label).addComponent(passbandRippleLabel));

			hGroup.addGroup(
				layout.createParallelGroup().addComponent(getFilterTypeComboBox()).addComponent(getPassbandEdgeFrequency1Spinner()).addComponent(getStopbandEdgeFrequency1Spinner()).addComponent(getPassbandRippleSpinner()));

			hGroup.addGroup(
				layout.createParallelGroup().addComponent(filterFamilyLabel).addComponent(passbandEdgeFrequency2Label).addComponent(stopbandEdgeFrequency2Label).addComponent(stopbandAttenuationLabel));

			hGroup.addGroup(
				layout.createParallelGroup().addComponent(getFilterFamilyComboBox()).addComponent(getPassbandEdgeFrequency2Spinner()).addComponent(getStopbandEdgeFrequency2Spinner()).addComponent(getStopbandAttenuationSpinner()));

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE).addComponent(filterTypeLabel).addComponent(getFilterTypeComboBox()).addComponent(filterFamilyLabel).addComponent(getFilterFamilyComboBox()));

			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE).addComponent(passbandEdgeFrequency1Label).addComponent(getPassbandEdgeFrequency1Spinner()).addComponent(passbandEdgeFrequency2Label).addComponent(getPassbandEdgeFrequency2Spinner()));
			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE).addComponent(stopbandEdgeFrequency1Label).addComponent(getStopbandEdgeFrequency1Spinner()).addComponent(stopbandEdgeFrequency2Label).addComponent(getStopbandEdgeFrequency2Spinner()));
			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE).addComponent(passbandRippleLabel).addComponent(getPassbandRippleSpinner()).addComponent(stopbandAttenuationLabel).addComponent(getStopbandAttenuationSpinner()));

			layout.setVerticalGroup(vGroup);

		}

		return filterParametersPanel;

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

			passbandEdgeFrequency1Spinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					FilterType filterType = (FilterType) getFilterTypeComboBox().getSelectedItem();
					double value = passbandEdgeFrequency1Spinner.getValue();
					double otherValue;


					if (filterType.isLowpass() || filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getStopbandEdgeFrequency1Spinner().setValue(value + FREQUENCY_SPINNER_STEP_SIZE);
						}

					} else if (filterType.isHighpass() || filterType.isBandpass()) {

						otherValue = ((Number) getStopbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getStopbandEdgeFrequency1Spinner().setValue(value - FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getPassbandEdgeFrequency2Spinner().setValue(value + FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (value < 0) {
						passbandEdgeFrequency1Spinner.setValue(0.0);
					}

					updateHighlights();

				}
			});


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


			passbandEdgeFrequency2Spinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					FilterType filterType = (FilterType) getFilterTypeComboBox().getSelectedItem();
					double value = passbandEdgeFrequency2Spinner.getValue();
					double otherValue;


					if (filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getStopbandEdgeFrequency2Spinner().setValue(value - FREQUENCY_SPINNER_STEP_SIZE);
						}

					} else if (filterType.isBandpass()) {

						otherValue = ((Number) getStopbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getStopbandEdgeFrequency2Spinner().setValue(value + FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getPassbandEdgeFrequency1Spinner().setValue(value - FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (value > getCurrentSamplingFrequency() / 2) {
						passbandEdgeFrequency2Spinner.setValue(getCurrentSamplingFrequency() / 2);
					}

					updateHighlights();

				}
			});

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

			stopbandEdgeFrequency1Spinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					FilterType filterType = (FilterType) getFilterTypeComboBox().getSelectedItem();
					double value = stopbandEdgeFrequency1Spinner.getValue();
					double otherValue;


					if (filterType.isLowpass() || filterType.isBandstop()) {

						otherValue = ((Number) getPassbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getPassbandEdgeFrequency1Spinner().setValue(value - FREQUENCY_SPINNER_STEP_SIZE);
						}

					} else if (filterType.isHighpass() || filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getPassbandEdgeFrequency1Spinner().setValue(value + FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getStopbandEdgeFrequency2Spinner().setValue(value + FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (value < 0) {
						stopbandEdgeFrequency1Spinner.setValue(0.0);
					}

					updateHighlights();

				}
			});


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

			stopbandEdgeFrequency2Spinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					FilterType filterType = (FilterType) getFilterTypeComboBox().getSelectedItem();
					double value = stopbandEdgeFrequency2Spinner.getValue();
					double otherValue;


					if (filterType.isBandstop()) {

						otherValue = ((Number) getPassbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value >= otherValue) {
							getPassbandEdgeFrequency2Spinner().setValue(value + FREQUENCY_SPINNER_STEP_SIZE);
						}

					} else if (filterType.isBandpass()) {

						otherValue = ((Number) getPassbandEdgeFrequency2Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getPassbandEdgeFrequency2Spinner().setValue(value - FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (filterType.isBandstop()) {

						otherValue = ((Number) getStopbandEdgeFrequency1Spinner().getValue()).doubleValue();

						if (value <= otherValue) {
							getStopbandEdgeFrequency1Spinner().setValue(value - FREQUENCY_SPINNER_STEP_SIZE);
						}

					}

					if (value > getCurrentSamplingFrequency() / 2) {
						stopbandEdgeFrequency2Spinner.setValue(getCurrentSamplingFrequency() / 2);
					}

					updateHighlights();

				}
			});

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

			passbandRippleSpinner = new DoubleSpinner(new SpinnerNumberModel(3.0, DECIBELS_SPINNER_STEP_SIZE, 10.0, DECIBELS_SPINNER_STEP_SIZE));
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

			stopbandAttenuationSpinner = new DoubleSpinner(new SpinnerNumberModel(30.0, 10.0 + DECIBELS_SPINNER_STEP_SIZE, 100.0, DECIBELS_SPINNER_STEP_SIZE));
			stopbandAttenuationSpinner.setPreferredSize(new Dimension(80, 25));

			stopbandAttenuationSpinner.setEditor(new JSpinner.NumberEditor(stopbandAttenuationSpinner, "0.00"));
			stopbandAttenuationSpinner.setFont(stopbandAttenuationSpinner.getFont().deriveFont(Font.PLAIN));

		}

		return stopbandAttenuationSpinner;

	}

	/**
	 * Returns the button which can be used to draw the frequency response
	 * for the parametrs set in this dialog.
	 * @return the button used to redraw the frequency response of this
	 * filter
	 */
	public JButton getDrawFrequencyResponseButton() {

		if (drawFrequencyResponseButton == null) {
			drawFrequencyResponseButton = new JButton(drawFrequencyResponseAction);
		}
		return drawFrequencyResponseButton;

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

	@Override
	protected void updateGraph() {
		try {
			getChartGroupPanel().updateGraphs(currentFilter);
		} catch (BadFilterParametersException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		currentFilter = new TimeDomainSampleFilter((TimeDomainSampleFilter) model);
		currentFilter.setSamplingFrequency(getCurrentSamplingFrequency());

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
			new double[]{
				((Number) getPassbandEdgeFrequency1Spinner().getValue()).doubleValue(),
				((Number) getPassbandEdgeFrequency2Spinner().getValue()).doubleValue()
			});
		currentFilter.setStopbandEdgeFrequencies(
			new double[]{
				((Number) getStopbandEdgeFrequency1Spinner().getValue()).doubleValue(),
				((Number) getStopbandEdgeFrequency2Spinner().getValue()).doubleValue()
			});
		currentFilter.setPassbandRipple(((Number) getPassbandRippleSpinner().getValue()).doubleValue());
		currentFilter.setStopbandAttenuation(((Number) getStopbandAttenuationSpinner().getValue()).doubleValue());

		currentFilter.setSamplingFrequency(getCurrentSamplingFrequency());

		((TimeDomainSampleFilter) model).copyFrom(currentFilter);

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
	protected URL getContextHelpURL() {
		URL contextHelpURL = null;
		try {
			contextHelpURL = (new ClassPathResource("org/signalml/help/editTimeDomainSampleFilterDialog.html")).getURL();
		} catch (IOException ex) {
			logger.error("Failed to get help URL", ex);
		}
		return contextHelpURL;
	}

	@Override
	public TimeDomainFilterResponseChartGroupPanel getChartGroupPanel() {
		if (graphsPanel == null) {
			graphsPanel = new TimeDomainFilterResponseChartGroupPanel(messageSource, currentFilter);
			graphsPanel.setSamplingFrequency(getCurrentSamplingFrequency());
		}
		return graphsPanel;
	}

	@Override
	protected void updateHighlights() {
		/* TODO: TimeDomain filter dialog doesn't support highlighting the
		 * selection yet.
		 */
	}

	/**
	 * An action envoked when the user presses the
	 * {@link EditTimeDomainSampleFilterDialog#drawFrequencyResponseButton}.
	 */
	protected class DrawFrequencyResponseAction extends AbstractAction {

		public DrawFrequencyResponseAction() {
			super(messageSource.getMessage("editTimeDomainSampleFilter.drawFilterFrequencyResponse"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {

			try {
				fillModelFromDialog(currentFilter);
			} catch (SignalMLException ex) {
				Logger.getLogger(EditTimeDomainSampleFilterDialog.class.getName()).log(Level.SEVERE, null, ex);
			}

			updateGraph();

		}
	}
}
