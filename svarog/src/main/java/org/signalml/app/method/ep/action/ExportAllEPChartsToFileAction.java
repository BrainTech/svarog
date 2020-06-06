package org.signalml.app.method.ep.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import org.signalml.app.method.ep.EvokedPotentialGraphPanel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;

/**
 * An action for saving the averaged evoked potentials charts to a PNG file.
 *
 * @author Piotr Szachewicz
 */
public class ExportAllEPChartsToFileAction extends AbstractSaveAction {

	private EvokedPotentialGraphPanel evokedPotentialGraphPanel;

	public ExportAllEPChartsToFileAction(ViewerFileChooser fileChooser, EvokedPotentialGraphPanel evokedPotentialGraphPanel) {
		super(fileChooser);
		this.evokedPotentialGraphPanel = evokedPotentialGraphPanel;

		setText(_("Save charts to PNG file"));
		setIconPath("org/signalml/app/icon/picture_save.png");
		setToolTip(_("Save charts to PNG file"));
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
			logger.error("", e1);
		}
	}

}