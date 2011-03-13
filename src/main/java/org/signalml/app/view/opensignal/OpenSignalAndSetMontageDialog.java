/* OpenSignalAndSetMontageDialog.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.Window;
import javax.swing.JComponent;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

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

		return interfacePanel;
	}

	protected SignalSourcePanel getSignalSourcePanel() {
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
		//OpenSignalDescriptor descriptor = (OpenSignalDescriptor) model;
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		if (model instanceof Montage) {
			super.fillModelFromDialog(model);
		}
//		super.fillModelFromDialog(model);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenSignalDescriptor.class.isAssignableFrom(clazz);
	}

        public Montage getChannelTabSourceMontage() {
                return channelsPanel.getMontage();
        }
}
