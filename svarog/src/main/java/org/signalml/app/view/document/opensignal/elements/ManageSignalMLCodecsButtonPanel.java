package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

import org.signalml.app.view.components.AbstractSignalMLPanel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class ManageSignalMLCodecsButtonPanel extends AbstractSignalMLPanel {

	private ViewerElementManager viewerElementManager;
	private JButton manageCodecsButton;
	private ManageSignalMLCodecsDialog manageSignalMLCodecsDialog;
	
	public ManageSignalMLCodecsButtonPanel(ViewerElementManager viewerElementManager) {
		setTitledBorder(_("SignalML Codecs"));
		this.viewerElementManager = viewerElementManager;
		initialize();
	}
	
	@Override
	protected void initialize() {
		manageCodecsButton = new JButton(new AbstractSignalMLAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getManageSignalMLCodecsDialog().showDialog(viewerElementManager, true);
			}
		});
		manageCodecsButton.setText(_("Manage SignalML codecs"));
		this.add(manageCodecsButton);
	}
	
	protected ManageSignalMLCodecsDialog getManageSignalMLCodecsDialog() {
		if (manageSignalMLCodecsDialog == null)
			manageSignalMLCodecsDialog = new ManageSignalMLCodecsDialog(viewerElementManager);
		return manageSignalMLCodecsDialog;
	}

}
