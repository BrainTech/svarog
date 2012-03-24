/* SignalMontageDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.AbstractPresetDialog;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.ChannelFunction;
import org.signalml.domain.montage.system.IChannelFunction;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.SvarogConstants;
import org.springframework.core.io.ClassPathResource;

/** SignalMontageDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMontageDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private ViewerElementManager viewerElementManager;
	private SignalMontagePanel signalMontagePanel;

	private URL contextHelpURL = null;

	public SignalMontageDialog(ViewerElementManager viewerElementManager,
				   Window f, boolean isModal) {

		super(viewerElementManager.getMontagePresetManager(), f, isModal);
		this.viewerElementManager = viewerElementManager;
	}

	@Override
	protected void initialize() {
		setTitle(_("Signal montage"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/montage.png"));
		setPreferredSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		super.initialize();
		setMinimumSize(new Dimension(800, 600));
	}

	@Override
	public JComponent createInterface() {
		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getSignalMontagePanel(), BorderLayout.CENTER);

		return interfacePanel;
	}
	
	public SignalMontagePanel getSignalMontagePanel() {
		if (signalMontagePanel == null)
			signalMontagePanel = new SignalMontagePanel(viewerElementManager);
		return signalMontagePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		getSignalMontagePanel().fillPanelFromModel(model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		getSignalMontagePanel().fillModelFromPanel(model);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		getSignalMontagePanel().validate(model, errors);
	}

	@Override
	protected void onDialogClose() {
		super.onDialogClose();
		getSignalMontagePanel().setMontageToPanels(null);
	}

	@Override
	public Preset getPreset() throws SignalMLException {
		Montage preset = new Montage(getSignalMontagePanel().getCurrentMontage());

		ValidationErrors errors = new ValidationErrors();
		validateDialog(preset, errors);

		if (errors.hasErrors()) {
			showValidationErrors(errors);
			return null;
		}

		return preset;
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {

		Montage montagePreset = (Montage) preset;
		int presetChannelsCount = getNormalChannelsCount(montagePreset);
		int thisChannelsCount = getNormalChannelsCount(getSignalMontagePanel().getCurrentMontage());

		if (presetChannelsCount != thisChannelsCount) {
			Dialogs.showError(_("Preset is incompatible with this montage - bad channels count in the preset montage!"));
			logger.error("Preset incompatible: current montage 'normal' channel count = " +
					+ thisChannelsCount + " preset channel count = " + presetChannelsCount);
			return;
		}

		fillDialogFromModel(preset);
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
		Montage currentMontage = getSignalMontagePanel().getCurrentMontage();
		if (currentMontage != null) {
			return currentMontage.isChanged();
		} else {
			return super.isChanged();
		}
	}

	@Override
	protected void setChanged(boolean changed) {
		Montage currentMontage = getSignalMontagePanel().getCurrentMontage();
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
				contextHelpURL = new ClassPathResource("org/signalml/help/contents.html").getURL();
				contextHelpURL = new URL(contextHelpURL.toExternalForm() + "#montage");
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

}
