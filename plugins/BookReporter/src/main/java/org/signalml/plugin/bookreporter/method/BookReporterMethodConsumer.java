package org.signalml.plugin.bookreporter.method;

import java.awt.Window;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.signalml.method.Method;
import org.signalml.plugin.bookreporter.data.BookReporterData;
import org.signalml.plugin.bookreporter.data.BookReporterResult;
import org.signalml.plugin.bookreporter.ui.BookReporterResultFrame;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.method.IPluginMethodResultConsumer;
import org.signalml.plugin.method.PluginMethodManager;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerMethodConsumer)
 */
public class BookReporterMethodConsumer implements IPluginMethodResultConsumer {

	protected static final Logger logger = Logger.getLogger(BookReporterMethodConsumer.class);

	private Window dialogParent;

	@Override
	public void initialize(PluginMethodManager manager) {
		this.dialogParent = manager.getSvarogAccess().getGUIAccess().getDialogParent();
	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		if (!(methodData instanceof BookReporterData)) {
			logger.error("Invalid book reporter data");
			return false;
		}
		BookReporterResult result = (BookReporterResult) methodResult;
		
		BookReporterResultFrame resultFrame = new BookReporterResultFrame();
		for (JFreeChart chart : result.getCharts()) {
			resultFrame.addChartToPanel(chart);
		}
		resultFrame.setVisible(true);
//		
//		JOptionPane.showMessageDialog(
//			dialogParent,
//			"Number of exported charts: " + result.getChartCount(),
//			"Book reporting finished",
//			JOptionPane.INFORMATION_MESSAGE
//		);
		return true;
	}
}
