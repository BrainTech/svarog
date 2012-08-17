package org.signalml.app.method.ep.action;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.signalml.app.method.ep.model.minmax.ChannelStatistics;
import org.signalml.app.method.ep.model.minmax.MinMaxTableModel;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.util.Util;

/**
 * An action for saving the evoked potentials statistics (min/max values/times)
 * to a file.
 *
 * @author Piotr Szachewicz
 */
public class SaveStatisticsAction extends AbstractSaveAction {

	private MinMaxTableModel tableModel;
	private static final String SEPARATOR = ";";

	public SaveStatisticsAction(ViewerFileChooser fileChooser, MinMaxTableModel minMaxTableModel) {
		super(fileChooser);
		this.tableModel = minMaxTableModel;
		setText(_("Save statistics to CSV"));
		setIconPath("org/signalml/app/icon/filesave.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File file = fileChooser.chooseSaveAsCSVFile(null);
		file = Util.changeOrAddFileExtension(file, "csv");

		if (file == null)
			return;

		try {
			writeData(file);
		} catch (IOException e1) {
			Dialogs.showError(_("An error occured while saving the file."));
		}

	}

	protected void writeData(File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write(_("channel_name") + SEPARATOR);
		writer.write(_("min_time") + SEPARATOR);
		writer.write(_("min_value") + SEPARATOR);
		writer.write(_("max_time") + SEPARATOR);
		writer.write(_("max_value") + "\n");

		List<ChannelStatistics> statistics = tableModel.getStatistics();
		for (ChannelStatistics channelStatistics: statistics) {
			writer.write(channelStatistics.getChannelName() + SEPARATOR);
			writer.write(channelStatistics.getMinTime() + SEPARATOR);
			writer.write(channelStatistics.getMinValue() + SEPARATOR);
			writer.write(channelStatistics.getMaxTime() + SEPARATOR);
			writer.write(channelStatistics.getMaxValue() + "\n");
		}

		writer.close();
	}


}
