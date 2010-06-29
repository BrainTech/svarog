/* EvokedPotentialMethodDialog.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.method.artifact.ArtifactMethodDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.app.view.element.SignalSpacePanel;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.tag.Tag;
import org.signalml.exception.SignalMLException;
import org.signalml.method.ep.EvokedPotentialParameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** EvokedPotentialMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialMethodDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private SignalSpacePanel signalSpacePanel;

	public EvokedPotentialMethodDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w) {
		super(messageSource, presetManager, w, true);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("evokedPotentialMethod.dialog.title"));
		setIconImage(IconUtils.loadClassPathImage(ArtifactMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		return getSignalSpacePanel();

	}

	public SignalSpacePanel getSignalSpacePanel() {
		if (signalSpacePanel == null) {
			signalSpacePanel = new SignalSpacePanel(messageSource);
		}
		return signalSpacePanel;
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

		constraints.setRequireCompletePages(true);

		getSignalSpacePanel().setConstraints(constraints);

		// test for active selections and use them if possible

		SignalPlot masterPlot = signalView.getMasterPlot();

		EvokedPotentialParameters parameters;

		Preset preset = getPresetManager().getDefaultPreset();
		if (preset == null) {
			parameters = data.getParameters();
		} else {
			parameters = (EvokedPotentialParameters) preset;
		}

		SignalSpace space = parameters.getSignalSpace();

		SignalSelection signalSelection = signalView.getSignalSelection(masterPlot);
		Tag tag = null;
		if (tagDocument != null) {
			PositionedTag tagSelection = signalView.getTagSelection(masterPlot);
			if (tagSelection != null) {
				if (tagSelection.getTagPositionIndex() == signalDocument.getTagDocuments().indexOf(tagDocument)) {
					tag = tagSelection.getTag();
				}
			}
		}

		space.configureFromSelections(signalSelection, tag);

		fillDialogFromParameters(parameters);

	}

	protected void fillDialogFromParameters(EvokedPotentialParameters parameters) {


		SignalSpace space = parameters.getSignalSpace();

		getSignalSpacePanel().fillPanelFromModel(space);

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
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		getSignalSpacePanel().validatePanel(errors);

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

}
