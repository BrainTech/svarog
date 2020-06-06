package org.signalml.app.method.ep;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.signalml.app.method.ep.view.minmax.MinMaxDialog;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * An action for showing tables containing the statistics concerning
 * the averaged evoked potentials, e.g. the min/max values for each channel.
 * @author Piotr Szachewicz
 */
public class ShowMinMaxAction extends AbstractSignalMLAction {

	private ViewerFileChooser fileChooser;
	private MinMaxDialog minMaxDialog;
	private EvokedPotentialResult result;

	public ShowMinMaxAction(ViewerFileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		setText(_("Show min/max"));
		putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/minmax_table.png"));
	}

	public MinMaxDialog getMinMaxDialog() {
		if (minMaxDialog == null)
			minMaxDialog = new MinMaxDialog(fileChooser);
		return minMaxDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getMinMaxDialog().showDialog(result);
	}

	public void setResult(EvokedPotentialResult result) {
		this.result = result;
	}

}
