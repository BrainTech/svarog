package org.signalml.app.view.document.opensignal;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker.StateValue;
import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.preset.managers.PredefinedTimeDomainFiltersPresetManager;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractWizardDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.montage.SignalMontagePanel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.monitor.ConnectToExperimentWorker;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SignalConfigurer;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.SvarogConstants;

public class OpenSignalWizardDialog extends AbstractWizardDialog implements PropertyChangeListener {

	protected static final Logger log = Logger.getLogger(OpenSignalWizardDialog.class);

	private static final long serialVersionUID = -6697344610944631342L;

	private ExperimentDescriptor experimentDescriptor;

	private ViewerElementManager viewerElementManager;
	private ConnectToExperimentWorker worker;

	private OpenSignalWizardStepOnePanel stepOnePanel;
	private SignalMontagePanel stepTwoPanel;

	private final String selectedSourceTab;

	public OpenSignalWizardDialog(ViewerElementManager viewerElementManager, String selectedSourceTab) {
		super(viewerElementManager.getDialogParent(), true);
		this.viewerElementManager = viewerElementManager;
		this.selectedSourceTab = selectedSourceTab;
		this.setTitle(_("Open signal"));
	}

	@Override
	protected void initialize() {
		setPreferredSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		super.initialize();
		setMinimumSize(new Dimension(800, 600));
		//I would like to make it fullscreen, but JDialog doesnt have a maximise option
		//So to avoid overlapping different taskbars/panels I'll make it 85% of the main screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int targetHeight = (int) (screenSize.height * 0.85);
		int targetWidth = (int) (screenSize.width * 0.85);

		setSize(targetWidth, targetHeight);
	}

	@Override
	public int getStepCount() {
		return 2;
	}

	@Override
	protected boolean onStepChange(int toStep, int fromStep, Object model)
			throws SignalMLException {

		if (toStep == 1 && fromStep == 0) {
			AbstractOpenSignalDescriptor openSignalDescriptor = getStepOnePanel().getOpenSignalDescriptor();

			if (openSignalDescriptor == null) {
				Dialogs.showError(_("Please select a proper file or monitor signal source!"));
				return false;
			}
			if (openSignalDescriptor.getChannelLabels().length == 0) {
				Dialogs.showError(_("Please select some channels!"));
				return false;
			}
			if (openSignalDescriptor.getSignalParameters().getSamplingFrequency() <= 0.0f) {
				Dialogs.showError(_("Please specify the sampling frequency."));
				return false;
			}

			SignalParameters signalParameters = openSignalDescriptor.getSignalParameters();
			Montage createdMontage = SignalConfigurer.createMontage(signalParameters.getChannelCount());
			PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager = SvarogApplication.getManagerOfPresetsManagers().getPredefinedTimeDomainSampleFilterPresetManager();

			if (SvarogApplication.getApplicationConfiguration().isAutoAddHighpassFilter()) {
				float samplingFrequency = openSignalDescriptor.getSignalParameters().getSamplingFrequency();
				TimeDomainSampleFilter filter = predefinedTimeDomainSampleFilterPresetManager.getPredefinedFilter(samplingFrequency, FilterType.HIGHPASS, 1.0, 0.0);
				if (filter != null) {
					createdMontage.addSampleFilter(filter);
				}
			}

			EegSystem selectedEegSystem = getStepOnePanel().getOtherSettingsPanel().getSelectedEegSystem();
			createdMontage.setEegSystem(selectedEegSystem);

			String[] channelLabels = openSignalDescriptor.getChannelLabels();
			for (int i = 0; i < channelLabels.length; i++) {
				try {
					createdMontage.setSourceChannelLabelAt(i, channelLabels[i]);
				} catch (MontageException e) {
					Dialogs.showError(e.getMessage());
					return false;
				}
			}
			createdMontage.getMontageGenerator().createMontage(createdMontage);

			getSignalMontagePanel().fillPanelFromModel(createdMontage);
			getSignalMontagePanel().setSamplingFrequency(signalParameters.getSamplingFrequency());

			getSignalMontagePanel().resetPreset();
		}

		return super.onStepChange(toStep, fromStep, model);
	}

	@Override
	protected JComponent createInterfaceForStep(int step) {

		switch (step) {
			case 0:
				return getStepOnePanel();
			case 1:
				return getSignalMontagePanel();
			default:
				return new JPanel();
		}

	}

	protected OpenSignalWizardStepOnePanel getStepOnePanel() {
		if (stepOnePanel == null) {
			stepOnePanel = new OpenSignalWizardStepOnePanel(viewerElementManager, selectedSourceTab);
		}
		return stepOnePanel;
	}

	protected SignalMontagePanel getSignalMontagePanel() {
		if (stepTwoPanel == null) {
			stepTwoPanel = new SignalMontagePanel(viewerElementManager.getFileChooser());
		}
		return stepTwoPanel;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenDocumentDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		OpenDocumentDescriptor openDocumentDescriptor = (OpenDocumentDescriptor) model;

		AbstractOpenSignalDescriptor openSignalDescriptor = getStepOnePanel().getOpenSignalDescriptor();

		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			openDocumentDescriptor.setType(ManagedDocumentType.MONITOR);
		} else {
			openDocumentDescriptor.setType(ManagedDocumentType.SIGNAL);
			File file = getStepOnePanel().getSignalSourceTabbedPane().getFileChooserPanel().getSelectedFile();
			openDocumentDescriptor.setFile(file);
		}
		openSignalDescriptor.setMontage(getSignalMontagePanel().getCurrentMontage());

		openDocumentDescriptor.setOpenSignalDescriptor(openSignalDescriptor);

	}

	@Override
	public boolean isFinishAllowedOnStep(int step) {
		return step == 1;
	}

	@Override
	protected void onDialogCloseWithOK() {
		super.onDialogCloseWithOK();

		this.getStepOnePanel().onDialogCloseWithOK();
	}

	@Override
	protected void onOkPressed() {
		if (getStepOnePanel().getOpenSignalDescriptor() instanceof ExperimentDescriptor) {
			experimentDescriptor = (ExperimentDescriptor) getStepOnePanel().getOpenSignalDescriptor();
			worker = new ConnectToExperimentWorker(this, experimentDescriptor);
			worker.addPropertyChangeListener(this);
			worker.execute();
		} else {
			super.onOkPressed();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (((StateValue) evt.getNewValue()) == StateValue.DONE) {
			try {
				worker.get();

				if (experimentDescriptor.getPeer() != null) {
					super.onOkPressed();
				}
			} catch (CancellationException ex) {
				logger.debug(_("Connecting to experiment cancelled."));
			} catch (InterruptedException e) {
				logger.error("", e);
			} catch (ExecutionException e) {
				logger.error("", e);
			}
		}
	}

	@Override
	protected void onDialogClose() {
		super.onDialogClose();

		getStepOnePanel().getSignalSourceTabbedPane().getChooseExperimentPanel().clearExperiments();
	}

}
