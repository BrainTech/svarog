/* OpenSignalAndSetMontageDialog.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.Window;
import javax.swing.JComponent;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * The dialog for opening signal and setting montage for the signal in the same
 * dialog.
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageDialog extends SignalMontageDialog {

	/**
	 * ViewerElementManager used by this dialog.
	 */
	private ViewerElementManager viewerElementManager;

	/**
	 * The panel for selecting the source of the signal.
	 */
	private SignalSourcePanel signalSourcePanel;

	/**
	 * A manager used to coordinate the signal selection tab with
	 * the montage tabs.
	 */
	private OpenSignalAndSetMontageDialogManager dialogManager;

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized messages
	 * @param viewerElementManager ViewerElementManager to be used
	 * @param f the parent window
	 * @param isModal whether this dialog should be modal
	 */
	public OpenSignalAndSetMontageDialog(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager,
			Window f, boolean isModal) {

		super(messageSource, viewerElementManager, f, isModal);

		this.viewerElementManager = viewerElementManager;
		dialogManager = new OpenSignalAndSetMontageDialogManager(this, messageSource);
	}

	@Override
	public JComponent createInterface() {
		JComponent interfacePanel = super.createInterface();

		String tabTitle = messageSource.getMessage("opensignal.signalSourceTabTitle");
		tabbedPane.insertTab(tabTitle, null, getSignalSourcePanel(), "", 0);
		tabbedPane.setSelectedIndex(0);

		return interfacePanel;
	}

	/**
	 * Returns the panel for selecting a signal source.
	 * @return the panel for selecting the source of the signal
	 */
	public SignalSourcePanel getSignalSourcePanel() {
		if (signalSourcePanel == null)
			signalSourcePanel = new SignalSourcePanel(messageSource, viewerElementManager);
		return signalSourcePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		if (model instanceof Montage) {
			super.fillDialogFromModel(model);
		}
		else if (model instanceof OpenDocumentDescriptor) {
			OpenDocumentDescriptor openDocumentDescriptor = (OpenDocumentDescriptor) model;
			signalSourcePanel.fillPanelFromModel(openDocumentDescriptor);
		}
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		if (model instanceof Montage) {
			super.fillModelFromDialog(model);
		}
		else if (model instanceof OpenDocumentDescriptor) {
			OpenDocumentDescriptor openDocumentDescriptor = (OpenDocumentDescriptor) model;
			signalSourcePanel.fillModelFromPanel(openDocumentDescriptor);

			openDocumentDescriptor.getOpenSignalDescriptor().setMontage(getCurrentMontage());
		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenDocumentDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * Sets the montage-related tabs in enabled/disbled state.
	 * @param enable true if the montage tabs should be enabled, false otherwise
	 */
	public void setMontageTabsEnabled(boolean enable) {
		int tabCount = tabbedPane.getTabCount();

		for (int i = 1; i < tabCount - 1; i++)
			tabbedPane.setEnabledAt(i, enable);
	}

	/**
	 * Sets the OK button enabled state.
	 * @param enabled true if the OK button should be enabled, false otherwise
	 */
	public void setOKButtoneEnabled(boolean enabled) {
		getOkButton().setEnabled(enabled);
	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		signalSourcePanel.validatePanel(model, errors);
		super.validateDialog(model, errors);
	}

        /**
         * On cancel, if current model is an OpenSignalDescriptor and
         * {@link AmplifierConnectionDescriptor#isBciStarted()} is set to true,
         * set it to false and disconnect from openbci (isBciStarted set to true
         * means that someone clicked start, connected succesfully and then
         * clicked cancel).
         *
         * @return super.onCancel();neousPanel.getEditDescriptionPanel().getTextPane().getText();
		if (description != null && !description.isEmpty()) {
			if (Util.hasSpecialChars(description)) {
				errors.rejectValue("montage.description", "error.descriptionBadChars");
			}
		
         */
        @Override
        protected boolean onCancel() {

                if (currentModel instanceof OpenSignalDescriptor) {

                        OpenSignalDescriptor descriptor = (OpenSignalDescriptor) currentModel;
                        if (descriptor.getAmplifierConnectionDescriptor().isBciStarted()) {
                                descriptor.getAmplifierConnectionDescriptor().setBciStarted(false);
                                viewerElementManager.getStopBCIAction().actionPerformed(null);
                        }
                }
                return super.onCancel();
        }

}
