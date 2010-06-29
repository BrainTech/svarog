/* SignalMontageDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.config.preset.FFTSampleFilterPresetManager;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.MontageDescriptor;
import org.signalml.app.model.SeriousWarningDescriptor;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.SignalType;
import org.signalml.exception.SignalMLException;
import org.signalml.util.SvarogConstants;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/** SignalMontageDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMontageDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private EditFFTSampleFilterDialog editFFTSampleFilterDialog;

	private MontageChannelsPanel channelsPanel;
	private MontageGeneratorPanel generatorPanel;
	private MatrixReferenceEditorPanel matrixReferenceEditorPanel;
	private VisualReferenceEditorPanel visualReferenceEditorPanel;
	private MontageFiltersPanel filtersPanel;
	private MontageMiscellaneousPanel miscellaneousPanel;

	private JTabbedPane tabbedPane;

	private SignalDocument signalDocument;
	private Montage currentMontage;

	private URL contextHelpURL = null;

	private FFTSampleFilterPresetManager fftFilterPresetManager;

	public SignalMontageDialog(MessageSourceAccessor messageSource, MontagePresetManager montagePresetManager, Window f, boolean isModal) {
		super(messageSource, montagePresetManager, f, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("signalMontage.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/montage.png"));
		setPreferredSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		super.initialize();
		setMinimumSize(new Dimension(800,600));
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		generatorPanel = new MontageGeneratorPanel(messageSource);
		generatorPanel.setErrorsDialog(getErrorsDialog());
		generatorPanel.setSeriousWarningDialog(getSeriousWarningDialog());

		channelsPanel = new MontageChannelsPanel(messageSource);
		channelsPanel.setSeriousWarningDialog(getSeriousWarningDialog());

		matrixReferenceEditorPanel = new MatrixReferenceEditorPanel(messageSource);

		visualReferenceEditorPanel = new VisualReferenceEditorPanel(messageSource);

		filtersPanel = new MontageFiltersPanel(messageSource);
		filtersPanel.setSeriousWarningDialog(getSeriousWarningDialog());
		filtersPanel.setEditFFTSampleFilterDialog(getEditFFTSampleFilterDialog());

		miscellaneousPanel = new MontageMiscellaneousPanel(messageSource);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(messageSource.getMessage("signalMontage.channelsTabTitle"), channelsPanel);
		tabbedPane.addTab(messageSource.getMessage("signalMontage.visualTabTitle"), visualReferenceEditorPanel);
		tabbedPane.addTab(messageSource.getMessage("signalMontage.matrixTabTitle"), matrixReferenceEditorPanel);
		tabbedPane.addTab(messageSource.getMessage("signalMontage.filtersTabTitle"), filtersPanel);
		tabbedPane.addTab(messageSource.getMessage("signalMontage.miscellaneousTabTitle"), miscellaneousPanel);

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				if (index < 0) {
					return;
				}
				switch (index) {

				case 1 :
					visualReferenceEditorPanel.getEditor().requestFocusInWindow();
					break;
				case 2 :
					matrixReferenceEditorPanel.getReferenceTable().requestFocusInWindow();
					break;
				case 3 :
					miscellaneousPanel.getEditDescriptionPanel().getTextPane().requestFocusInWindow();
					break;
				case 0 :
				default :
					// no special focus

				}
			}

		});


		interfacePanel.add(generatorPanel, BorderLayout.NORTH);
		interfacePanel.add(tabbedPane, BorderLayout.CENTER);

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		if (model instanceof Montage) {

			// for presets
			currentMontage = new Montage((Montage) model);

		} else {

			MontageDescriptor descriptor = (MontageDescriptor) model;
			Montage montage = descriptor.getMontage();
			SignalDocument signalDocument = descriptor.getSignalDocument();
			boolean signalBound = (signalDocument != null);
			if (montage == null) {
				if (signalBound) {
					currentMontage = new Montage(new SourceMontage(signalDocument));
				} else {
					currentMontage = new Montage(new SourceMontage(SignalType.EEG_10_20));
				}
			} else {
				currentMontage = new Montage(montage);
			}
			this.signalDocument = signalDocument;

			if (signalBound) {
				getOkButton().setVisible(true);
				getRootPane().setDefaultButton(getOkButton());
			} else {
				getOkButton().setVisible(false);
				getRootPane().setDefaultButton(getCancelButton());
			}

			channelsPanel.setSignalBound(signalBound);
			filtersPanel.setSignalBound(signalBound);
			if (signalBound) {
				filtersPanel.setCurrentSamplingFrequency(signalDocument.getSamplingFrequency());
			} else {
				filtersPanel.setCurrentSamplingFrequency(-1);
			}

		}

		if (signalDocument != null) {
			if (!currentMontage.isCompatible(signalDocument)) {

				String warning =  messageSource.getMessage("montageDialog.onIncompatible");
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 3);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

				currentMontage.adapt(signalDocument);

			}
		}

		setMontageToPanels(currentMontage);
		setChanged(false);

	}

	private void setMontageToPanels(Montage montage) {

		generatorPanel.setMontage(montage);
		channelsPanel.setMontage(montage);
		visualReferenceEditorPanel.setMontage(montage);
		matrixReferenceEditorPanel.setMontage(montage);
		filtersPanel.setMontage(montage);
		miscellaneousPanel.setMontage(montage);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MontageDescriptor descriptor = (MontageDescriptor) model;

		// montage was edited immediately for the most part

		descriptor.setMontage(currentMontage);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		// validate montage table
		if (currentMontage.getMontageChannelCount() == 0) {
			errors.reject("error.noChannelInMontage");
		}

		String description = miscellaneousPanel.getEditDescriptionPanel().getTextPane().getText();
		if (description != null && !description.isEmpty()) {
			if (!Util.validateString(description)) {
				errors.rejectValue("montage.description", "error.descriptionBadChars");
			}
		}

	}

	@Override
	protected void onDialogClose() {
		super.onDialogClose();
		setMontageToPanels(null);
	}

	@Override
	public Preset getPreset() throws SignalMLException {

		Montage preset = new Montage(currentMontage);

		Errors errors = new BindException(preset, "data");
		validateDialog(preset, errors);

		if (errors.hasErrors()) {
			showValidationErrors(errors);
			return null;
		}

		return preset;

	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {

		fillDialogFromModel(preset);

	}

	@Override
	protected boolean isTrackingChanges() {
		return true;
	}

	@Override
	protected boolean showLoadDefaultButton() {
		return true;
	}

	@Override
	protected boolean showSaveDefaultButton() {
		return true;
	}

	@Override
	protected boolean showRemoveDefaultButton() {
		return true;
	}

	@Override
	public boolean isChanged() {
		if (currentMontage != null) {
			return currentMontage.isChanged();
		} else {
			return super.isChanged();
		}
	}

	@Override
	protected void setChanged(boolean changed) {
		if (currentMontage != null) {
			currentMontage.setChanged(changed);
		} else {
			super.setChanged(changed);
		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MontageDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource("org/signalml/help/contents.html")).getURL();
				contextHelpURL = new URL(contextHelpURL.toExternalForm() + "#montage");
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	public FFTSampleFilterPresetManager getFftFilterPresetManager() {
		return fftFilterPresetManager;
	}

	public void setFftFilterPresetManager(FFTSampleFilterPresetManager fftFilterPresetManager) {
		this.fftFilterPresetManager = fftFilterPresetManager;
	}

	protected EditFFTSampleFilterDialog getEditFFTSampleFilterDialog() {
		if (editFFTSampleFilterDialog == null) {
			editFFTSampleFilterDialog = new EditFFTSampleFilterDialog(messageSource, fftFilterPresetManager, this, true);
			editFFTSampleFilterDialog.setApplicationConfig(getApplicationConfig());
			editFFTSampleFilterDialog.setFileChooser(getFileChooser());
		}
		return editFFTSampleFilterDialog;
	}

}
