package org.signalml.plugin.bookreporter.method;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.plugin.bookreporter.BookReporterPlugin;
import org.signalml.plugin.bookreporter.chart.BookReporterChartData;
import org.signalml.plugin.bookreporter.chart.preset.BookReporterChartPreset;
import org.signalml.plugin.bookreporter.data.BookReporterData;
import org.signalml.plugin.bookreporter.data.BookReporterResult;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;
import org.signalml.plugin.bookreporter.exception.BookReporterBookReaderException;
import org.signalml.plugin.bookreporter.io.BookReporterBookReader;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.method.BaseMethodData;
import static org.signalml.plugin.i18n.PluginI18n._;
import org.signalml.plugin.method.PluginAbstractMethod;
import org.signalml.plugin.tool.PluginResourceRepository;

/**
 * @author piotr@develancer.pl
 * (based on Oskar Kapala's NewStagerMethod)
 */
public class BookReporterMethod extends PluginAbstractMethod implements
	TrackableMethod, IterableMethod {

	protected static final Logger logger = Logger.getLogger(BookReporterMethod.class);

	private static final String UID = "c8b4fdc5-2749-4867-96f2-e40b8ed83d25";
	private static final int[] VERSION = new int[] { 0, 1 };

	@Override
	public Object doComputation(Object data, MethodExecutionTracker tracker)
	throws ComputationException {

		BookReporterData bookReporterData = (BookReporterData) data;
		BookReporterBookReader reader = null;
		try {
			reader = new BookReporterBookReader(bookReporterData.getParameters().bookFilePath);
		} catch (BookReporterBookReaderException ex) {
			throw new RuntimeException(ex.getMessage());
		}

		double signalLength = reader.getTimeLength();
		BookReporterChartPreset[] chartPresets = bookReporterData.getParameters().chartPresets;
		BookReporterChartData[] chartData = new BookReporterChartData[chartPresets.length];
		for (int i=0; i<chartPresets.length; i++) {
			chartData[i] = chartPresets[i].createEmptyData(signalLength);
		}

		tracker.setTickerLimit(0, reader.getAllSegmentsCount());
		Collection<BookReporterAtom> atomSample = null;
		while ((atomSample = reader.getAtomsFromNextSegment()) != null) {
			if (tracker.isRequestingAbort()) {
				return null;
			}
			for (BookReporterChartData chart : chartData) {
				chart.process(atomSample);
			}
			tracker.setTicker(0, reader.getProcessedSegmentsCount());
		}

		BookReporterResult result = new BookReporterResult();
		for (BookReporterChartData chart : chartData) {
			result.addChart(chart.getChart());
		}
		return result;
	}

	@Override
	public Object digestIterationResult(int iteration, Object result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IterableParameter[] getIterableParameters(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		try {
			return ((PluginConfigForMethod) PluginResourceRepository
					.GetResource("config", BookReporterPlugin.class))
				   .getMethodConfig().getMethodName();
		} catch (PluginException e) {
			return "";
		}
	}

	@Override
	public BaseMethodData createData() {
		return null;
	}

	@Override
	public boolean supportsDataClass(Class<?> clazz) {
		return BookReporterData.class.isAssignableFrom(clazz);
	}

	@Override
	public Class<?> getResultClass() {
		return BookReporterResult.class;
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public int[] getVersion() {
		return VERSION;
	}

	@Override
	public int getTickerCount() {
		return 1;
	}

	@Override
	public String getTickerLabel(int ticker) {
		if (ticker == 0) {
			return _("Book reporting");
		} else {
			throw new IndexOutOfBoundsException("No ticker [" + ticker + "]");
		}
	}

}
