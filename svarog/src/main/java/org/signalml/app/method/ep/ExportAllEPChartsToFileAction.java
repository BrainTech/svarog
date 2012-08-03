package org.signalml.app.method.ep;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class ExportAllEPChartsToFileAction extends AbstractSignalMLAction {

	private ViewerFileChooser fileChooser;
	private EvokedPotentialResult result;
	private EvokedPotentialGraphPanel evokedPotentialGraphPanel;

	ExportAllEPChartsToFileAction(ViewerFileChooser fileChooser, EvokedPotentialGraphPanel evokedPotentialGraphPanel) {
		super();
		this.fileChooser = fileChooser;
		this.evokedPotentialGraphPanel = evokedPotentialGraphPanel;

		setText(_("Save charts to PNG file"));
		setIconPath("org/signalml/app/icon/picture_save.png");
		setToolTip(_("Save charts to PNG file"));
	}

	public void setResult(EvokedPotentialResult result) {
		this.result = result;
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(result != null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File file = fileChooser.chooseChartSaveAsPngFile(null);

		if (file == null)
			return;

		try {
			evokedPotentialGraphPanel.savePanelToFile(file);
		} catch (IOException e1) {
			Dialogs.showExceptionDialog(e1);
			e1.printStackTrace();
		}
	}

}