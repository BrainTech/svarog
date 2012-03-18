package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;
import javax.swing.JComponent;

import org.signalml.app.action.document.RegisterCodecAction;
import org.signalml.app.model.signal.SignalMLCodecListModel;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.plugin.export.SignalMLException;

public class ManageSignalMLCodecsDialog extends AbstractDialog {

	private ViewerElementManager viewerElementManager;
	private SignalMLOptionsPanel signalMLOptionsPanel;

	public ManageSignalMLCodecsDialog(ViewerElementManager viewerElementManager) {
		super(viewerElementManager.getDialogParent(), true);
		this.viewerElementManager = viewerElementManager;
	}
	
	@Override
	protected JComponent createInterface() {
		signalMLOptionsPanel = new SignalMLOptionsPanel();

		SignalMLCodecManager codecManager = viewerElementManager.getCodecManager();
		SignalMLCodecListModel codecListModel = new SignalMLCodecListModel();
        codecListModel.setCodecManager(codecManager);

        RegisterCodecAction registerCodecAction = new RegisterCodecAction();
        registerCodecAction.setCodecManager(codecManager);
        registerCodecAction.setRegisterCodecDialog(viewerElementManager.getRegisterCodecDialog());
        registerCodecAction.setPleaseWaitDialog(viewerElementManager.getPleaseWaitDialog());
        registerCodecAction.initializeAll();

     /*   registerCodecAction.setSelector(new SignalMLCodecSelector() {

                @Override
                public SignalMLCodec getSelectedCodec() {
                        return (SignalMLCodec) signalMLOptionsPanel.getSignalMLDriverComboBox().getSelectedItem();
                }

                @Override
                public void setSelectedCodec(SignalMLCodec codec) {
                        signalMLOptionsPanel.getSignalMLDriverComboBox().setSelectedItem(codec);
                }
        });*/

        signalMLOptionsPanel.getSignalMLDriverComboBox().setModel(codecListModel);
        signalMLOptionsPanel.getRegisterCodecButton().setAction(registerCodecAction);
        return signalMLOptionsPanel;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return true;
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
	}

}
