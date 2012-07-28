/* ArtifactMethodDialog.java created 2007-10-28
 *
 */

package org.signalml.plugin.newartifact.ui;

import static org.signalml.plugin.newartifact.NewArtifactPlugin._;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.method.InputSignalPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.SourceMontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.AbstractPresetDialog;
import org.signalml.app.view.montage.SourceMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.system.ChannelFunction;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.method.helper.PluginPresetManagerFilter;
import org.signalml.plugin.newartifact.NewArtifactPlugin;
import org.signalml.plugin.newartifact.data.NewArtifactApplicationData;
import org.signalml.plugin.newartifact.data.NewArtifactExclusionDescriptor;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;
import org.signalml.plugin.newartifact.data.NewArtifactPowerGridFrequency;
import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.springframework.core.io.ClassPathResource;

/**
 * ArtifactMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o. (dialog design based on work by Hubert Klekowicz)
 */
public class NewArtifactMethodDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private URL contextHelpURL = null;

	private InputSignalPanel signalPanel;
	private NewArtifactTypesPanel typesPanel;
	private NewArtifactOptionsPanel optionsPanel;

	private SourceMontageDialog montageDialog;
	private NewArtifactExclusionDialog exclusionDialog;

	private NewArtifactType[] artifactTypes = NewArtifactType.values();

	private SourceMontage currentMontage;
	private int[][] currentExclusion;

	public NewArtifactMethodDialog(PresetManager presetManager, Window w) {
		super(new PluginPresetManagerFilter(presetManager,
				NewArtifactMethodDialog.GetPresetClasses()), w, true);
	}

	@Override
	protected void initialize() {
		setTitle(_("Artifact detection configuration"));
		setIconImage(IconUtils.loadClassPathImage(NewArtifactPlugin.iconPath));
		setResizable(false);
		super.initialize();
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource(
						"org/signalml/help/artifact.html")).getURL();
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		signalPanel = new InputSignalPanel();
		typesPanel = new NewArtifactTypesPanel();
		optionsPanel = new NewArtifactOptionsPanel();

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

		NewArtifactApplicationData data = (NewArtifactApplicationData) model;

		ExportedSignalDocument signalDocument = data.getSignalDocument();
		String path = "?";
		if (signalDocument instanceof FileBackedDocument) {
			path = ((FileBackedDocument) signalDocument).getBackingFile()
					.getAbsolutePath();
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

		int channelCount = signalDocument.getSourceChannelLabels().size(); // TODO
		// sourceChannelCount
		currentExclusion = new int[artifactTypes.length][channelCount];
		int[][] exclusion = data.getExcludedChannels();
		if (exclusion != null) {
			int i, e;
			for (i = 0; i < Math.min(artifactTypes.length, exclusion.length); i++) {
				for (e = 0; e < Math.min(channelCount, exclusion[i].length); e++) {
					currentExclusion[i][e] = exclusion[i][e];
				}
			}
		}

		configureAvailableTypes();
	}

	private void fillDialogFromParameters(NewArtifactParameters parameters) {

		typesPanel.fillPanelFromParameters(parameters);
		NewArtifactPowerGridFrequency powerGridFrequency = NewArtifactPowerGridFrequency
				.forFloat(parameters.getPowerGridFrequency());
		if (powerGridFrequency == null) {
			powerGridFrequency = NewArtifactPowerGridFrequency.EUROPE;
		}
		optionsPanel.getPowerComboBox().setSelectedItem(powerGridFrequency);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		NewArtifactApplicationData data = (NewArtifactApplicationData) model;
		if (!data.isExistingProject()) {
			data.setMontage(currentMontage);
		}
		data.setExcludedChannels(currentExclusion);

		fillParametersFromDialog(data.getParameters());

		data.calculate();

	}

	private void fillParametersFromDialog(NewArtifactParameters parameters) {

		typesPanel.fillParametersFromPanel(parameters);

		NewArtifactPowerGridFrequency powerGridFrequency = (NewArtifactPowerGridFrequency) optionsPanel
				.getPowerComboBox().getSelectedItem();
		parameters.setPowerGridFrequency(powerGridFrequency.getFrequency());

	}

	@Override
	public Preset getPreset() throws SignalMLException {
		NewArtifactParameters parameters = new NewArtifactParameters();
		fillParametersFromDialog(parameters);
		return parameters;
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		NewArtifactParameters parameters;
		try {
			 parameters = (NewArtifactParameters) preset;
		} catch (ClassCastException e) {
			return;
		}
		fillDialogFromParameters(parameters);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors)
			throws SignalMLException {

		// dialog doesn't need any additional validation

	}

	private void configureAvailableTypes() {

		SourceChannel f3 = currentMontage.getSourceChannelByLabel("F3");
		SourceChannel f4 = currentMontage.getSourceChannelByLabel("F4");
		SourceChannel c3 = currentMontage.getSourceChannelByLabel("C3");
		SourceChannel c4 = currentMontage.getSourceChannelByLabel("C4");
		SourceChannel fp1 = currentMontage.getSourceChannelByLabel("Fp1");
		SourceChannel fp2 = currentMontage.getSourceChannelByLabel("Fp2");
		SourceChannel f7 = currentMontage.getSourceChannelByLabel("F7");
		SourceChannel f8 = currentMontage.getSourceChannelByLabel("F8");
		SourceChannel t3 = currentMontage.getSourceChannelByLabel("T3");
		SourceChannel t4 = currentMontage.getSourceChannelByLabel("T4");
		SourceChannel eogl = currentMontage.getSourceChannelByLabel("EOGL");
		SourceChannel eogp = currentMontage.getSourceChannelByLabel("EOGP");
		int[] ecg = currentMontage
				.getSourceChannelsByFunction(ChannelFunction.ECG);

		boolean noECG = (ecg == null) || (ecg.length == 0);
		typesPanel.setLockOnType(NewArtifactType.ECG, noECG);

		if ((f3 == null) || (f4 == null) || (c3 == null) || (c4 == null)
				|| (fp1 == null) || (fp2 == null)) {
			typesPanel.setLockOnType(NewArtifactType.EYEBLINKS, true);
		} else {
			typesPanel.setLockOnType(NewArtifactType.EYEBLINKS, false);
		}

		if (((f7 == null) || (f8 == null)) && ((t3 == null) || (t4 == null))
				&& ((eogl == null) || (eogp == null))) {
			typesPanel.setLockOnType(NewArtifactType.EYE_MOVEMENT, true);
		} else {
			typesPanel.setLockOnType(NewArtifactType.EYE_MOVEMENT, false);
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
		return NewArtifactApplicationData.class.isAssignableFrom(clazz);
	}

	protected class EditMontageAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditMontageAction() {
			super(_("Edit montage"));
			putValue(
					AbstractAction.SMALL_ICON,
					IconUtils
							.loadClassPathIcon("org/signalml/app/icon/montage.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
					_("Edit channel labels and functions"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montageDialog == null) {
				montageDialog = new SourceMontageDialog(
						NewArtifactMethodDialog.this, true);
			}

			SourceMontageDescriptor descriptor = new SourceMontageDescriptor(
					currentMontage);

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
			super(_("Select excluded derivations"));
			putValue(
					AbstractAction.SMALL_ICON,
					IconUtils
							.loadClassPathIcon("org/signalml/app/icon/editexclusion.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
					_("Select excluded derivations"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (exclusionDialog == null) {
				exclusionDialog = new NewArtifactExclusionDialog(
						NewArtifactMethodDialog.this, true);
			}

			NewArtifactExclusionDescriptor descriptor = new NewArtifactExclusionDescriptor(
					currentMontage, currentExclusion);

			exclusionDialog.showDialog(descriptor, true);

		}

	}

	private static Collection<Class<? extends Preset>> GetPresetClasses() {
		Collection<Class<? extends Preset>> l = new ArrayList<Class<? extends Preset>>();
		l.add(NewArtifactParameters.class);
		return l;
	}

}