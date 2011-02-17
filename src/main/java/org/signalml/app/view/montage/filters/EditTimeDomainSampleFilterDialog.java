/* EditTimeDomainSampleFilterDialog.java created 2010-09-23
 *
 */
package org.signalml.app.view.montage.filters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.montage.filters.charts.TimeDomainFilterResponseChartGroupPanel;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.TimeDomainSampleFilterValidator;
import org.signalml.domain.montage.filter.iirdesigner.BadFilterParametersException;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Errors;

/**
 * This class represents a dialog for {@link TimeDomainSampleFilter
 * TimeDomainSampleFilters} editing.
 *
 * @author Piotr Szachewicz
 */
public class EditTimeDomainSampleFilterDialog extends EditSampleFilterDialog {

	/**
	 * represents the currently edited filter
	 */
	private TimeDomainSampleFilter currentFilter;
	/**
	 * A panel containing controls allowing to change the filter's
	 * parameters.
	 */
	private TimeDomainFilterParametersPanel filterParametersPanel;

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

	/**
	 * A label used to show filer-not-valid messages.
	 */
	private JLabel filterNotValidLabel;

	/**
	 * A panel for drawing and controling the filter responses.
	 * It contains all the charts visualizing the filter and associated
	 * spinners to control the maximum value shown on the x-axis.
	 */
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

	}

	@Override
	public JComponent createInterface() {

		JPanel descriptionPanel = getDescriptionPanel();
		JPanel graphPanel = getChartGroupPanelWithABorder();

		JPanel editFilterParametersPanel = new JPanel(new BorderLayout(3, 3));
		editFilterParametersPanel.setBorder(new TitledBorder(messageSource.getMessage("editTimeDomainSampleFilter.filterParametersTitle")));

		JPanel filterNotValidPanel = createFilterNotValidPanel();

		JPanel drawFrequencyResponseButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));
		drawFrequencyResponseButtonPanel.add(getDrawFrequencyResponseButton());

		editFilterParametersPanel.add(getFilterParametersPanel(), BorderLayout.NORTH);
		editFilterParametersPanel.add(filterNotValidPanel, BorderLayout.CENTER);
		editFilterParametersPanel.add(drawFrequencyResponseButtonPanel, BorderLayout.SOUTH);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(graphPanel, BorderLayout.NORTH);
		interfacePanel.add(descriptionPanel, BorderLayout.CENTER);
		interfacePanel.add(editFilterParametersPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	/**
	 * Returns the {@link JPanel} containing controls allowing to set the
	 * filter's parameters.
	 * @return the {@link JPanel} used in this dialog, containing spinners
	 * allowing to control the filter's parameters
	 */
	public TimeDomainFilterParametersPanel getFilterParametersPanel() {

		if (filterParametersPanel == null) {
			filterParametersPanel = new TimeDomainFilterParametersPanel(messageSource);
		}

		return filterParametersPanel;

	}

	protected JPanel createFilterNotValidPanel() {
		JPanel filterNotValidPanel = new JPanel(new BorderLayout());
		GroupLayout layout = new GroupLayout(filterNotValidPanel);
		filterNotValidPanel.setLayout(layout);

		filterNotValidLabel = new JLabel();
		filterNotValidLabel.setForeground(Color.red);
		filterNotValidLabel.setHorizontalAlignment(JLabel.CENTER);
		filterNotValidLabel.setMinimumSize(new Dimension(1050, 13)); //making sure that the error messages will have enough space

		layout.setHorizontalGroup(
		   layout.createSequentialGroup()
		      .addComponent(filterNotValidLabel)
		);
		layout.setVerticalGroup(
		   layout.createSequentialGroup()
		      .addComponent(filterNotValidLabel)
		);

		filterNotValidPanel.add(filterNotValidLabel);

		return filterNotValidPanel;
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

	@Override
	protected void updateGraph() {

		if(!validateCurrentFilterAndShowErrorMessage())
				return;

		try {
			getChartGroupPanelWithABorder().updateGraphs(currentFilter);
			clearFilterNotValidMessage();
		} catch (BadFilterParametersException ex) {
			showFilterNotValidMessage(ex.getMessage());
		}
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		currentFilter = new TimeDomainSampleFilter((TimeDomainSampleFilter) model);
		currentFilter.setSamplingFrequency(getCurrentSamplingFrequency());

		getFilterParametersPanel().fillPanelFromModel(currentFilter);
		getDescriptionTextField().setText(currentFilter.getDescription());

		updateGraph();

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		currentFilter.setDescription(getDescriptionTextField().getText());
		getFilterParametersPanel().fillModelFromPanel(currentFilter);
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
	public TimeDomainFilterResponseChartGroupPanel getChartGroupPanelWithABorder() {
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

	@Override
	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		super.setCurrentSamplingFrequency(currentSamplingFrequency);
		getFilterParametersPanel().setSamplingFrequency(currentSamplingFrequency);
	}

	/**
	 * Validates if the current filter is correct and if not, displays an
	 * error message.
	 * @return true if the filter is correct, false otherwise
	 */
	protected boolean validateCurrentFilterAndShowErrorMessage() {
		TimeDomainSampleFilterValidator validator = new TimeDomainSampleFilterValidator(messageSource, currentFilter);
		boolean isValid;
		isValid = validator.isValid();

		if (!isValid)
			showFilterNotValidMessage(validator.getErrorMessage());
		else
			clearFilterNotValidMessage();

		return isValid;
	}

	/**
	 * Shows a filter-not-valid message.
	 * @param message text of the message
	 */
	protected void showFilterNotValidMessage(String message) {
		filterNotValidLabel.setText(message);
	}

	/**
	 * Clears the filter-not-valid message so that no message is shown.
	 */
	protected void clearFilterNotValidMessage() {
		showFilterNotValidMessage(" ");
	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		fillModelFromDialog(currentFilter);
		if (!validateCurrentFilterAndShowErrorMessage())
			errors.reject("timeDomainFilter.badFilterParameters");
	}

}
