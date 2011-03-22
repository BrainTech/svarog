/* OpenSignalAndSetMontageDialog.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.Window;
import javax.swing.JComponent;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageDialog extends SignalMontageDialog {

	private ViewerElementManager viewerElementManager;
	private SignalSourcePanel signalSourcePanel;
	private OpenSignalAndSetMontageDialogManager dialogManager;

	public OpenSignalAndSetMontageDialog(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager,
			Window f, boolean isModal) {

		super(messageSource, viewerElementManager.getMontagePresetManager(), viewerElementManager.getPredefinedTimeDomainFiltersPresetManager(), f, isModal);
		this.viewerElementManager = viewerElementManager;

		dialogManager = new OpenSignalAndSetMontageDialogManager(this, getSignalSourcePanel());
	}

	@Override
	public JComponent createInterface() {
		JComponent interfacePanel = super.createInterface();

		tabbedPane.insertTab("Signal source", null, getSignalSourcePanel(), "", 0);
		tabbedPane.setSelectedIndex(0);

		return interfacePanel;
	}

	private SignalSourcePanel getSignalSourcePanel() {
		if (signalSourcePanel == null)
			signalSourcePanel = new SignalSourcePanel(messageSource, viewerElementManager);
		return signalSourcePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		if (model instanceof Montage) {
			super.fillDialogFromModel(model);
		}
		else if (model instanceof OpenSignalDescriptor) {
			OpenSignalDescriptor openSignalDescriptor = (OpenSignalDescriptor) model;
			signalSourcePanel.fillPanelFromModel(openSignalDescriptor);
		}
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		if (model instanceof Montage) {
			super.fillModelFromDialog(model);
		}
		else if (model instanceof OpenSignalDescriptor) {
			OpenSignalDescriptor openSignalDescriptor = (OpenSignalDescriptor) model;
			signalSourcePanel.fillModelFromPanel(openSignalDescriptor);

			openSignalDescriptor.setMontage(getCurrentMontage());

		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenSignalDescriptor.class.isAssignableFrom(clazz);
	}

        public Montage getChannelTabSourceMontage() {
                return channelsPanel.getMontage();
        }

	public void setMontageTabsEnabled(boolean enable) {
		int tabCount = tabbedPane.getTabCount();

		for (int i = 1; i < tabCount - 1; i++)
			tabbedPane.setEnabledAt(i, enable);
	}

	public void setOKButtoneEnabled(boolean enabled) {
		getOkButton().setEnabled(enabled);
	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		signalSourcePanel.validatePanel(model, errors);
		super.validateDialog(model, errors);
	}

}
