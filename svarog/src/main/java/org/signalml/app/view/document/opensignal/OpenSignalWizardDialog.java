package org.signalml.app.view.document.opensignal;

import static org.signalml.app.util.i18n.SvarogI18n._;
import java.awt.Window;

import javax.swing.JComponent;

import javax.swing.JPanel;

import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.view.components.dialogs.AbstractWizardDialog;
import org.signalml.app.view.montage.SignalMontagePanel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;

public class OpenSignalWizardDialog extends AbstractWizardDialog {

	private static final long serialVersionUID = -6697344610944631342L;
	
	private ViewerElementManager viewerElementManager;
	
	private OpenSignalWizardStepOnePanel stepOnePanel;
	private SignalMontagePanel stepTwoPanel;

	public OpenSignalWizardDialog(ViewerElementManager viewerElementManager, Window f, boolean isModal) {
		super(f, isModal);
		this.viewerElementManager = viewerElementManager;
		this.setTitle(_("Open signal"));
	}

	@Override
	public int getStepCount() {
		return 2;
	}

	@Override
	protected JComponent createInterfaceForStep(int step) {

		switch (step) {
		case 0:
			return getStepOnePanel();
		case 1:
			return getStepTwoPanel();
		default:
			return new JPanel();
		}

	}
	
	protected OpenSignalWizardStepOnePanel getStepOnePanel() {
		if (stepOnePanel == null) {
			stepOnePanel = new OpenSignalWizardStepOnePanel(viewerElementManager);
		}
		return stepOnePanel;
	}
	
	public SignalMontagePanel getStepTwoPanel() {
		if (stepTwoPanel == null) {
			stepTwoPanel = new SignalMontagePanel(viewerElementManager);
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
	}

}
