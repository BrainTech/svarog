/* OpenSignalAndSetMontageDialog.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.signalml.app.config.preset.PredefinedTimeDomainFiltersPresetManager;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageDialog extends SignalMontageDialog {

	private SignalSourcePanel signalSourcePanel;

	public OpenSignalAndSetMontageDialog(MessageSourceAccessor messageSource, MontagePresetManager montagePresetManager,
		PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager, Window f, boolean isModal) {
		super(messageSource, montagePresetManager, predefinedTimeDomainSampleFilterPresetManager, f, isModal);
	}

	@Override
	public JComponent createInterface() {
		JComponent interfacePanel = super.createInterface();

		tabbedPane.insertTab("Signal source", null, getSignalSourcePanel(), "", 0);

		return interfacePanel;
	}

	protected JPanel getSignalSourcePanel() {
		if (signalSourcePanel == null)
			signalSourcePanel = new SignalSourcePanel(messageSource);
		return signalSourcePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		//OpenSignalDescriptor descriptor = (OpenSignalDescriptor) model;
		//super.fillDialogFromModel(descriptor.getMontage());
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
//		super.fillModelFromDialog(model);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenSignalDescriptor.class.isAssignableFrom(clazz);
	}

}
