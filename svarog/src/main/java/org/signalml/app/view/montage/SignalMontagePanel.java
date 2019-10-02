package org.signalml.app.view.montage;

import com.alee.laf.tabbedpane.WebTabbedPane;
import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ManagerOfPresetManagers;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.config.preset.managers.EegSystemsPresetManager;
import org.signalml.app.config.preset.managers.FFTSampleFilterPresetManager;
import org.signalml.app.config.preset.managers.PredefinedTimeDomainFiltersPresetManager;
import org.signalml.app.config.preset.managers.TimeDomainSampleFilterPresetManager;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.view.common.components.presets.AbstractPanelWithPresets;
import org.signalml.app.view.common.components.presets.ComplexPresetControlsPanel;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.common.dialogs.errors.ValidationErrorsDialog;
import org.signalml.app.view.montage.filters.EditFFTSampleFilterDialog;
import org.signalml.app.view.montage.filters.EditTimeDomainSampleFilterDialog;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.system.ChannelFunction;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.IChannelFunction;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

public class SignalMontagePanel extends AbstractPanelWithPresets {

	/**
	 * This dialog is used when edititing
	 * {@link FFTSampleFilter FFTSampleFilter's} parameters.
	 */
	private EditFFTSampleFilterDialog editFFTSampleFilterDialog;

	/**
	 * This dialog is used when editing {@link TimeDomainSampleFilter}
	 * parameters.
	 */
	private EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog;

	/**
	 * A panel for editing the signal's montage.
	 */
	protected MontageEditionPanel montageEditionPanel;
	protected MontageFiltersPanel filtersPanel;
	protected MontageMiscellaneousPanel miscellaneousPanel;

	protected JTabbedPane tabbedPane;

	private SignalDocument signalDocument;
	private Montage currentMontage;

	private FFTSampleFilterPresetManager fftFilterPresetManager;

	/**
	 * A {@link PresetManager} managing the user-defined
	 * {@link TimeDomainSampleFilter} presets.
	 */
	private TimeDomainSampleFilterPresetManager timeDomainSampleFilterPresetManager;

	/**
	 * A {@link PresetManager} managing the predefined
	 * {@link TimeDomainSampleFilter TimeDomainSampleFilters}.
	 */
	private PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager;
	/**
	 * The preset manager which manages the available {@link EegSystem EEG systems}.
	 */
	private EegSystemsPresetManager eegSystemsPresetManager;

	private ValidationErrorsDialog errorsDialog;

	public SignalMontagePanel(ViewerFileChooser fileChooser) {

		super(SvarogApplication.getManagerOfPresetsManagers().getMontagePresetManager());

		ManagerOfPresetManagers managerOfPresetsManagers = SvarogApplication.getManagerOfPresetsManagers();
		this.predefinedTimeDomainSampleFilterPresetManager = managerOfPresetsManagers.getPredefinedTimeDomainSampleFilterPresetManager();
		this.eegSystemsPresetManager = managerOfPresetsManagers.getEegSystemsPresetManager();
		this.timeDomainSampleFilterPresetManager = managerOfPresetsManagers.getTimeDomainSampleFilterPresetManager();
		this.fftFilterPresetManager = managerOfPresetsManagers.getFftFilterPresetManager();

		setFileChooser(fileChooser);

		this.setLayout(new BorderLayout());
		this.add(createInterface(), BorderLayout.CENTER);
		this.add(getPresetControlsPanel(), BorderLayout.SOUTH);
	}

	protected JPanel createInterface() {
		JPanel interfacePanel = new JPanel();
		interfacePanel.setLayout(new BorderLayout());
		interfacePanel.setPreferredSize(new Dimension(800, 500));

		tabbedPane = new WebTabbedPane();

		filtersPanel = new MontageFiltersPanel(predefinedTimeDomainSampleFilterPresetManager);
		filtersPanel.setEditFFTSampleFilterDialog(getEditFFTSampleFilterDialog());
		filtersPanel.setTimeDomainSampleFilterDialog(getEditTimeDomainSampleFilterDialog());
		montageEditionPanel = new MontageEditionPanel();
		montageEditionPanel.setErrorsDialog(getErrorsDialog());

		miscellaneousPanel = new MontageMiscellaneousPanel();

		tabbedPane.addTab(_("Montage"), montageEditionPanel);
		tabbedPane.addTab(_("Filters"), filtersPanel);
		tabbedPane.addTab(_("Miscellaneous"), miscellaneousPanel);

		interfacePanel.add(tabbedPane);

		return interfacePanel;
	}

	@Override
	protected ComplexPresetControlsPanel getPresetControlsPanel() {
		if (presetControlsPanel == null)
			presetControlsPanel = new ComplexPresetControlsPanel(this, presetManager, true);
		return presetControlsPanel;
	}

	public FFTSampleFilterPresetManager getFftFilterPresetManager() {
		return fftFilterPresetManager;
	}

	public void setFftFilterPresetManager(FFTSampleFilterPresetManager fftFilterPresetManager) {
		this.fftFilterPresetManager = fftFilterPresetManager;
	}

	/**
	 * Returns the {@link TimeDomainSampleFilterPresetManager} used by this
	 * SignalMontageDialog.
	 * @return the {@link TimeDomainSampleFilterPresetManager} used
	 */
	public TimeDomainSampleFilterPresetManager getTimeDomainSampleFilterPresetManager() {
		return timeDomainSampleFilterPresetManager;
	}

	/**
	 * Sets a {@link TimeDomainSampleFilterPresetManager} to be used by this
	 * SignalMontageDialog.
	 * @param timeDomainSampleFilterPresetManager
	 * the {@link TimeDomainSampleFilterPresetManager} to be used
	 */
	public void setTimeDomainSampleFilterPresetManager(TimeDomainSampleFilterPresetManager timeDomainSampleFilterPresetManager) {
		this.timeDomainSampleFilterPresetManager = timeDomainSampleFilterPresetManager;
	}

	protected EditFFTSampleFilterDialog getEditFFTSampleFilterDialog() {
		if (editFFTSampleFilterDialog == null) {
			editFFTSampleFilterDialog = new EditFFTSampleFilterDialog(this.getParentWindow(), true);
			editFFTSampleFilterDialog.setFileChooser(getFileChooser());
		}
		return editFFTSampleFilterDialog;
	}

	/**
	 * Returns the {@link EditTimeDomainSampleFilterDialog} used by
	 * this SignalMontageDialog.
	 * @return the {@link EditTimeDomainSampleFilterDialog} used
	 */
	protected EditTimeDomainSampleFilterDialog getEditTimeDomainSampleFilterDialog() {
		if (editTimeDomainSampleFilterDialog == null) {
			editTimeDomainSampleFilterDialog = new EditTimeDomainSampleFilterDialog(this.getParentWindow(), true);
			editTimeDomainSampleFilterDialog.setFileChooser(getFileChooser());
		}
		return editTimeDomainSampleFilterDialog;
	}

	/**
	 * Sets the sampling frequency used by the filters in this montage.
	 * @param samplingFrequency sampling frequency to be used
	 */
	public void setSamplingFrequency(float samplingFrequency) {
		filtersPanel.setCurrentSamplingFrequency(samplingFrequency);
	}

	/**
	 * Returns the current sampling frequency used by the filters in this montage.
	 * @return the sampling frequency
	 */
	public float getSamplingFrequency() {
		return filtersPanel.getCurrentSamplingFrequency();
	}

	/**
	 * Returns the current montage.
	 * @return the current montage
	 */
	public Montage getCurrentMontage() {
		return currentMontage;
	}

	/**
	 * Returns the {@link EegSystemsPresetManager} used by this dialog.
	 * @return the {@link EegSystemsPresetManager} used by this dialog
	 */
	public EegSystemsPresetManager getEegSystemsPresetManager() {
		return eegSystemsPresetManager;
	}

	/**
	 * Returns the validation errors dialog.
	 * If it doesn't exist it is created.
	 * @return the errors dialog
	 */
	protected synchronized ValidationErrorsDialog getErrorsDialog() {
		if (errorsDialog == null) {
			errorsDialog = new ValidationErrorsDialog(this.getParentWindow(), true);
		}
		return errorsDialog;
	}

	public void fillPanelFromModel(Object model) throws SignalMLException {

		if (model instanceof Montage) {
			this.currentMontage = new Montage((Montage) model);
			setMontageToPanels(currentMontage);
		} else {
			final MontageDescriptor descriptor = (MontageDescriptor) model;
			final Montage montage = descriptor.getMontage();
			final SignalDocument signalDocument = descriptor.getSignalDocument();
			final boolean signalBound = (signalDocument != null);
			if (montage == null)
				this.currentMontage = new Montage(signalBound ?
												  new SourceMontage(signalDocument) : new SourceMontage());
			else
				this.currentMontage = new Montage(montage);
			this.signalDocument = signalDocument;

			//getOkButton().setVisible(signalBound);
			//getRootPane().setDefaultButton(signalBound ? getOkButton() : getCancelButton());

			montageEditionPanel.setSignalBound(signalBound);
			filtersPanel.setSignalBound(signalBound);

			filtersPanel.setCurrentSamplingFrequency(signalBound ?
					signalDocument.getSamplingFrequency() : 128.0F);
		}

		if (signalDocument != null && !this.currentMontage.isCompatible(signalDocument))
			this.currentMontage.adapt(signalDocument);

		setMontageToPanels(this.currentMontage);
	}

	public void setMontageToPanels(Montage montage) {
		if (montage != null && montage.getEegSystemName() != null) {
			EegSystem system = (EegSystem) eegSystemsPresetManager.getPresetByName(montage.getEegSystemFullName());
			montage.setEegSystem(system);
		}

		montageEditionPanel.setMontageToPanels(montage);
		filtersPanel.setMontage(montage);
		miscellaneousPanel.setMontage(montage);
	}

	public void fillModelFromPanel(Object model) throws SignalMLException {
		if (model instanceof MontageDescriptor) {
			MontageDescriptor descriptor = (MontageDescriptor) model;

			// montage was edited immediately for the most part
			descriptor.setMontage(currentMontage);
		}
		else if (model instanceof OpenDocumentDescriptor) {
			OpenDocumentDescriptor openDocumentDescriptor = (OpenDocumentDescriptor) model;
			openDocumentDescriptor.getOpenSignalDescriptor().setMontage(getCurrentMontage());
		}
	}

	public void validate(Object model, ValidationErrors errors) throws SignalMLException {
		// validate montage table
		if (currentMontage.getMontageChannelCount() == 0) {
			errors.addError(_("The montage is empty. Please add some target channels"));
		}
		String description = miscellaneousPanel.getEditDescriptionPanel().getTextPane().getText();
		if (description != null && !description.isEmpty()) {
			if (Util.hasSpecialChars(description)) {
				errors.addError(_("Description must not contain control characters"));
			}
		}
	}

	@Override
	public Preset getPreset() throws SignalMLException {
		Montage preset = new Montage(getCurrentMontage());

		ValidationErrors errors = new ValidationErrors();
		validate(preset, errors);

		if (errors.hasErrors()) {
			getErrorsDialog().showDialog(errors, true);

			return null;
		}

		return preset;
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		fillPanelFromModel(preset);
	}

	@Override
	public boolean isPresetCompatible(Preset preset) {
		Montage montagePreset = (Montage) preset;
		int presetChannelsCount = getNormalChannelsCount(montagePreset);
		int thisChannelsCount = getNormalChannelsCount(getCurrentMontage());

		if (presetChannelsCount != thisChannelsCount) {
			Dialogs.showError(_("Preset is incompatible with this montage—bad channels count in the preset montage!"));
			logger.error("Preset incompatible: current montage 'normal' channel count = " +
						 + thisChannelsCount + " preset channel count = " + presetChannelsCount);
			return false;
		}
		return true;
	}

	private int getNormalChannelsCount(Montage montage) {
		int normalChannelsCount = 0;

		for (int i = 0; i < montage.getSourceChannelCount(); i++) {
			IChannelFunction channelFunction = montage.getSourceChannelFunctionAt(i);
			if (channelFunction != ChannelFunction.ZERO && channelFunction != ChannelFunction.ONE)
				normalChannelsCount++;
		}
		return normalChannelsCount;
	}

}
