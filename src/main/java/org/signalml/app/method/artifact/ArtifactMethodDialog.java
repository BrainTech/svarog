/* ArtifactMethodDialog.java created 2007-10-28
 *
 */

package org.signalml.app.method.artifact;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.InputSignalPanel;
import org.signalml.app.model.SourceMontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.app.view.montage.SourceMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.exception.SignalMLException;
import org.signalml.method.artifact.ArtifactParameters;
import org.signalml.method.artifact.ArtifactPowerGridFrequency;
import org.signalml.method.artifact.ArtifactType;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Errors;

/** ArtifactMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 		(dialog design based on work by Hubert Klekowicz)
 */
public class ArtifactMethodDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private URL contextHelpURL = null;

	private InputSignalPanel signalPanel;
	private ArtifactTypesPanel typesPanel;
	private ArtifactOptionsPanel optionsPanel;

	private SourceMontageDialog montageDialog;
	private ArtifactExclusionDialog exclusionDialog;

	private ArtifactType[] artifactTypes = ArtifactType.values();

	private SourceMontage currentMontage;
	private int[][] currentExclusion;

	public ArtifactMethodDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w) {
		super(messageSource, presetManager, w, true);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("artifactMethod.dialog.title"));
		setIconImage(IconUtils.loadClassPathImage(ArtifactMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource("org/signalml/help/artifact.html")).getURL();
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		signalPanel = new InputSignalPanel(messageSource);
		typesPanel = new ArtifactTypesPanel(messageSource);
		optionsPanel = new ArtifactOptionsPanel(messageSource);

		optionsPanel.getExclusionButton().setAction(new EditExclusionAction());
		optionsPanel.getExclusionButton().setEnabled(false);

		signalPanel.getMontageButton().setAction(new EditMontageAction());

		interfacePanel.add(signalPanel, BorderLayout.NORTH);
		interfacePanel.add(typesPanel, BorderLayout.CENTER);
		interfacePanel.add(optionsPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		ArtifactApplicationData data = (ArtifactApplicationData) model;

		SignalDocument signalDocument = data.getSignalDocument();
		String path = "?";
		if (signalDocument instanceof FileBackedDocument) {
			path = ((FileBackedDocument) signalDocument).getBackingFile().getAbsolutePath();
		}
		signalPanel.getSignalTextField().setText(path);

		if (data.isExistingProject()) {
			fillDialogFromParameters(data.getParameters());
		} else {
			Preset preset = getPresetManager().getDefaultPreset();
			if (preset != null) {
				try {
					setPreset(preset);
				} catch (SignalMLException ex) {
					logger.error("Failed to set default preset", ex);
				}
			} else {
				fillDialogFromParameters(data.getParameters());
			}
		}

		if (data.isProcessedProject()) {
			currentMontage = data.getMontage();
			signalPanel.getMontageButton().setEnabled(false);
		} else {
			currentMontage = new Montage(data.getMontage());
			signalPanel.getMontageButton().setEnabled(true);
		}

		int channelCount = currentMontage.getSourceChannelCount();
		currentExclusion = new int[artifactTypes.length][channelCount];
		int[][] exclusion = data.getExcludedChannels();
		if (exclusion != null) {
			int i, e;
			for (i=0; i<artifactTypes.length; i++) {
				for (e=0; e<channelCount; e++) {
					currentExclusion[i][e] = exclusion[i][e];
				}
			}
		}

		configureAvailableTypes();

	}

	private void fillDialogFromParameters(ArtifactParameters parameters) {

		typesPanel.fillPanelFromParameters(parameters);
		ArtifactPowerGridFrequency powerGridFrequency = ArtifactPowerGridFrequency.forFloat(parameters.getPowerGridFrequency());
		if (powerGridFrequency == null) {
			powerGridFrequency = ArtifactPowerGridFrequency.EUROPE;
		}
		optionsPanel.getPowerComboBox().setSelectedItem(powerGridFrequency);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		ArtifactApplicationData data = (ArtifactApplicationData) model;
		if (!data.isExistingProject()) {
			data.setMontage(currentMontage);
		}
		data.setExcludedChannels(currentExclusion);

		fillParametersFromDialog(data.getParameters());

		data.calculate();

	}

	private void fillParametersFromDialog(ArtifactParameters parameters) {

		typesPanel.fillParametersFromPanel(parameters);

		ArtifactPowerGridFrequency powerGridFrequency = (ArtifactPowerGridFrequency) optionsPanel.getPowerComboBox().getSelectedItem();
		parameters.setPowerGridFrequency(powerGridFrequency.getFrequency());

	}

	@Override
	public Preset getPreset() throws SignalMLException {

		ArtifactParameters parameters = new ArtifactParameters();

		fillParametersFromDialog(parameters);

		return parameters;

	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {

		ArtifactParameters parameters = (ArtifactParameters) preset;

		fillDialogFromParameters(parameters);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		// dialog doesn't need any additional validation

	}

	private void configureAvailableTypes() {

		int f3 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.F3);
		int f4 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.F4);
		int c3 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.C3);
		int c4 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.C4);
		int fp1 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.FP1);
		int fp2 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.FP2);
		int f7 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.F7);
		int f8 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.F8);
		int t3 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.T3);
		int t4 = currentMontage.getFirstSourceChannelWithFunction(EegChannel.T4);
		int eogl = currentMontage.getFirstSourceChannelWithFunction(EegChannel.EOGL);
		int eogp = currentMontage.getFirstSourceChannelWithFunction(EegChannel.EOGP);
		int ecg = currentMontage.getFirstSourceChannelWithFunction(EegChannel.ECG);

		typesPanel.setLockOnType(ArtifactType.ECG, (ecg < 0));

		if ((f3<0) || (f4<0) || (c3<0) || (c4<0) || (fp1<0) || (fp2<0)) {
			typesPanel.setLockOnType(ArtifactType.EYEBLINKS, true);
		} else {
			typesPanel.setLockOnType(ArtifactType.EYEBLINKS, false);
		}

		if (((f7<0) || (f8<0)) && ((t3<0) || (t4<0)) && ((eogl<0) || (eogp<0))) {
			typesPanel.setLockOnType(ArtifactType.EYE_MOVEMENT, true);
		} else {
			typesPanel.setLockOnType(ArtifactType.EYE_MOVEMENT, false);
		}

	}

	@Override
	protected void onDialogClose() {
		try {
			Preset preset = getPreset();
			getPresetManager().setDefaultPreset(preset);
		} catch (SignalMLException ex) {
			logger.debug("Failed to get preset", ex);
		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ArtifactApplicationData.class.isAssignableFrom(clazz);
	}

	protected class EditMontageAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditMontageAction() {
			super(messageSource.getMessage("artifactMethod.dialog.editMontage"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/montage.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("artifactMethod.dialog.editMontageToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montageDialog == null) {
				montageDialog = new SourceMontageDialog(messageSource, ArtifactMethodDialog.this, true);
			}

			SourceMontageDescriptor descriptor = new SourceMontageDescriptor(currentMontage);

			boolean ok = montageDialog.showDialog(descriptor, true);
			if (!ok) {
				return;
			}

			currentMontage = descriptor.getMontage();
			configureAvailableTypes();

		}

	}

	protected class EditExclusionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditExclusionAction() {
			super(messageSource.getMessage("artifactMethod.dialog.editExclusion"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/editexclusion.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("artifactMethod.dialog.editExclusionToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (exclusionDialog == null) {
				exclusionDialog = new ArtifactExclusionDialog(messageSource, ArtifactMethodDialog.this, true);
			}

			ArtifactExclusionDescriptor descriptor = new ArtifactExclusionDescriptor(currentMontage, currentExclusion);

			exclusionDialog.showDialog(descriptor, true);

		}

	}

}
