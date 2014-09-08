package org.signalml.plugin.bookreporter.method;

import org.apache.log4j.Logger;
import org.jfree.chart.plot.XYPlot;
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

	@Override
	public void initialize(PluginMethodManager manager) {
		// nothing here
	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		if (!(methodData instanceof BookReporterData)) {
			logger.error("Invalid book reporter data");
			return false;
		}
		BookReporterResult result = (BookReporterResult) methodResult;
		
		BookReporterResultFrame resultFrame = new BookReporterResultFrame();
		for (XYPlot plot : result.getPlots()) {
			resultFrame.addPlotToPanel(plot);
		}
		resultFrame.setTimeAxis(result.getTimeAxis());
		resultFrame.setTags(result.getTags());
		resultFrame.setVisible(true);
		return true;
	}
}
