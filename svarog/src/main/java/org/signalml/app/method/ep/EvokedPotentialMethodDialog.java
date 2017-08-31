/* EvokedPotentialMethodDialog.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ep.view.ArtifactRejectionPanel;
import org.signalml.app.method.ep.view.EvokedPotentialSettingsPanel;
import org.signalml.app.method.ep.view.signalspace.ERPSignalSpacePanel;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.AbstractPresetDialog;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.method.ep.EvokedPotentialParameters;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.view.FileChooser;

/** EvokedPotentialMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialMethodDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;
	public static final String ICON_PATH = "org/signalml/app/icon/runmethod.png";

	private JTabbedPane tabbedPane;

	private ERPSignalSpacePanel signalSpacePanel;
	private EvokedPotentialSettingsPanel evokedPotentialSettingsPanel;
	private ArtifactRejectionPanel artifactRejectionPanel;

	public EvokedPotentialMethodDialog(PresetManager presetManager, Window w) {
		super(presetManager, w, true);
	}

	@Override
	protected void initialize() {
		setTitle(_("Evoked potential averaging configuration"));
		setIconImage(IconUtils.loadClassPathImage(ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		tabbedPane = new JTabbedPane();
		tabbedPane.add("Signal selection", getSignalSpacePanel());
		tabbedPane.add("ERP settings", getEvokedPotentialSettingsPanel());
		tabbedPane.add("Artifact rejection", getArtifactRejectionPanel());

		return tabbedPane;
	}

	public ERPSignalSpacePanel getSignalSpacePanel() {
		if (signalSpacePanel == null) {
			signalSpacePanel = new ERPSignalSpacePanel();
		}
		return signalSpacePanel;
	}

	public EvokedPotentialSettingsPanel getEvokedPotentialSettingsPanel() {
		if (evokedPotentialSettingsPanel == null)
			evokedPotentialSettingsPanel = new EvokedPotentialSettingsPanel();
		return evokedPotentialSettingsPanel;
	}

	public ArtifactRejectionPanel getArtifactRejectionPanel() {
		if (artifactRejectionPanel == null)
			artifactRejectionPanel = new ArtifactRejectionPanel();
		return artifactRejectionPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		EvokedPotentialApplicationData data = (EvokedPotentialApplicationData) model;
		SignalDocument signalDocument = data.getSignalDocument();
		SignalView signalView = (SignalView) signalDocument.getDocumentView();

		SignalSpaceConstraints constraints = signalView.createSignalSpaceConstraints();

		TagDocument tagDocument = data.getTagDocument();
		if (tagDocument != null) {
			tagDocument.updateSignalSpaceConstraints(constraints);
		}

		getSignalSpacePanel().setConstraints(constraints);

		EvokedPotentialParameters parameters;

		Preset preset = getPresetManager().getDefaultPreset();
		if (preset == null) {
			parameters = data.getParameters();
		} else {
			parameters = (EvokedPotentialParameters) preset;
		}

		getEvokedPotentialSettingsPanel().setSignalDocument(signalDocument);

		getEvokedPotentialSettingsPanel().setTagDocument(tagDocument);
		getArtifactRejectionPanel().setTagDocument(tagDocument);

		data.setParameters(parameters);
		fillDialogFromParameters(parameters);
		
		// test for active selections and use them if possible
		SignalPlot masterPlot = signalView.getMasterPlot();
		SignalSelection signalSelection = signalView.getSignalSelection(masterPlot);
		if (signalSelection != null)
		{
			SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
			SignalSpace space = signalExportDescriptor.getSignalSpace();
			space.configureFromSelections(signalSelection, null);
			getSignalSpacePanel().fillPanelFromModel(space);
		}
	}

	protected void fillDialogFromParameters(EvokedPotentialParameters parameters) {

		SignalSpace space = parameters.getSignalSpace();

		getSignalSpacePanel().fillPanelFromModel(space);
		getEvokedPotentialSettingsPanel().fillPanelFromModel(parameters);
		getArtifactRejectionPanel().fillPanelFromModel(parameters);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		EvokedPotentialApplicationData data = (EvokedPotentialApplicationData) model;

		fillParametersFromDialog(data.getParameters());

		data.calculate();

	}

	protected void fillParametersFromDialog(EvokedPotentialParameters parameters) {

		SignalSpace space = parameters.getSignalSpace();

		getSignalSpacePanel().fillModelFromPanel(space);
		getEvokedPotentialSettingsPanel().fillModelFromPanel(parameters);
		getArtifactRejectionPanel().fillModelFromPanel(parameters);

	}

	@Override
	public Preset getPreset() throws SignalMLException {

		EvokedPotentialParameters parameters = new EvokedPotentialParameters();

		fillParametersFromDialog(parameters);

		return parameters;

	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {

		EvokedPotentialParameters parameters = (EvokedPotentialParameters) preset;

		fillDialogFromParameters(parameters);

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {

		getSignalSpacePanel().validatePanel(errors);
		getEvokedPotentialSettingsPanel().validatePanel(errors);

		List<TagStyleGroup> averagedStyles = getEvokedPotentialSettingsPanel().getAveragedTagSelectionPanel().getSelectedTagStyles();
		List<TagStyleGroup> artifactStyles = getArtifactRejectionPanel().getArtifactTagsSelectionPanel().getSelectedTagStyles();

		for (TagStyleGroup avStyle: averagedStyles) {
			if (artifactStyles.contains(avStyle))
				errors.addError(_("The list of artifact tags cannot contain averaged tags!"));
			return;
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
		return EvokedPotentialApplicationData.class.isAssignableFrom(clazz);
	}

	@Override
	public void setFileChooser(FileChooser fileChooser) {
		super.setFileChooser(fileChooser);
		getEvokedPotentialSettingsPanel().setFileChooser(fileChooser);
	}

}
