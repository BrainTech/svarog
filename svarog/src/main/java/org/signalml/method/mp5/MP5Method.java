/* MP5Method.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.util.XMLUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.SignalProcessingChainDescriptor;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.filter.export.MultichannelSampleFilterForExport;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.space.MarkerSegmentedSampleSourceDescriptor;
import org.signalml.domain.signal.space.SegmentedSampleSourceDescriptor;
import org.signalml.domain.signal.space.SelectionSegmentedSampleSourceDescriptor;
import org.signalml.method.AbstractMethod;
import org.signalml.method.CleanupMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.SerializableMethod;
import org.signalml.method.TrackableMethod;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.util.Util;
import org.springframework.validation.Errors;

/** MP5Method
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5Method extends AbstractMethod implements TrackableMethod, SerializableMethod, CleanupMethod, SignalWriterMonitor {

	protected static final Logger logger = Logger.getLogger(MP5Method.class);

	private static final String UID = "93a02d05-92ce-4634-b51a-5b5c558be506";
	private static final String NAME = "MP";
	private static final int[] VERSION = new int[] {1,0};

	private File tempDirectory = null;
	private MP5ExecutorLocator executorLocator;
	private MP5ExecutorConfigurer executorConfigurer;

	private XStream streamer;

	private MethodExecutionTracker tracker;

	public MP5Method() throws SignalMLException {
		super();
	}

	public File getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(File tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public MP5ExecutorLocator getExecutorLocator() {
		return executorLocator;
	}

	public void setExecutorLocator(MP5ExecutorLocator executorLocator) {
		this.executorLocator = executorLocator;
	}

	public MP5ExecutorConfigurer getExecutorConfigurer() {
		return executorConfigurer;
	}

	public void setExecutorConfigurer(MP5ExecutorConfigurer executorConfigurer) {
		this.executorConfigurer = executorConfigurer;
	}

	@Override
	public Object doComputation(Object dataObj, final MethodExecutionTracker tracker) throws ComputationException {

		this.tracker = tracker;
		logger.debug("Beginning computation of MP");

		MP5Data data = (MP5Data) dataObj;

		tracker.setMessage(_("Creating files"));
		createWorkingDirectory(data);
		File workingDirectory = data.getWorkingDirectory();

		MultichannelSegmentedSampleSource sampleSource = data.getSampleSource();

		int totalSegmentCount = sampleSource.getSegmentCount();

		synchronized (tracker) {
			tracker.setTickerLimits(new int[] { totalSegmentCount, 1, 1, 1, 1 });
			tracker.setTickers(new int[] { 0, 0, 0, 0, 0 });
		}

		MP5Executor executor = executorLocator.findExecutor(data.getExecutorUID());
		if (executor == null) {
			throw new ComputationException("Executor not found [" + executor + "]");
		}
		if (executorConfigurer != null) {
			executorConfigurer.configure(executor);
		}

		logger.debug("Using MP executor [" + executor + "]");

		prepareFiltering(sampleSource);

		if (testAbort(tracker)) {
			return null;
		}

		tracker.setMessage(_R("Processing data"));

		File collectedBookFile = new File(workingDirectory, "collected.db");
		if (collectedBookFile.exists()) {
			collectedBookFile.delete();
		}

		boolean ok = executor.execute(data, collectedBookFile, tracker);
		if (!ok) {
			testAbort(tracker);
			return null;
		}

		tracker.setMessage(_("Collecting results"));

		MP5Result result = new MP5Result();
		result.setBookFilePath(collectedBookFile.getAbsolutePath());

		// for result serialization
		data.setBookFilePath(collectedBookFile.getAbsolutePath());

		tracker.setMessage(_("Finished"));

		return result;

	}

	private void prepareFiltering(MultichannelSampleSource sampleSource) {
		MultichannelSampleProcessor channelSubsetSampleSource = ((MultichannelSampleProcessor)sampleSource);
		SignalProcessingChain signalProcessingChain = ((SignalProcessingChain)channelSubsetSampleSource.getSource());
		if (signalProcessingChain.getOutput() instanceof MultichannelSampleFilterForExport) {
			MultichannelSampleFilterForExport multichannelSampleFilterForExport = (MultichannelSampleFilterForExport) signalProcessingChain.getOutput();
			int maximumSampleCount = signalProcessingChain.getSampleCount(0);
			tracker.setTickerLimit(4, maximumSampleCount);
			multichannelSampleFilterForExport.setSignalWriterMonitor(this);
		}
	}

	private boolean testAbort(MethodExecutionTracker tracker) {

		if (tracker.isRequestingAbort()) {
			logger.debug("Terminating execution");
			return true;
		}

		return false;

	}

	private void createWorkingDirectory(MP5Data data) throws ComputationException {

		File workingDirectory = data.getWorkingDirectory();
		if (workingDirectory == null) {
			workingDirectory = new File(tempDirectory, Util.getRandomHexString(8));
			data.setWorkingDirectory(workingDirectory);
		}

		if (!workingDirectory.exists()) {
			boolean ok = workingDirectory.mkdir();
			if (!ok) {
				logger.error("Failed to create working directory");
				throw new ComputationException(new IOException(_("Failed to create working dir")));
			}
		} else {
			if (!workingDirectory.canWrite()) {
				logger.error("Working directory not writeable");
				throw new ComputationException(new IOException(_("Working directory not writeable")));
			}
		}

	}

	@Override
	public void validate(Object dataObj, Errors errors) {
		super.validate(dataObj, errors);
		if (!errors.hasErrors()) {
			MP5Data data = (MP5Data) dataObj;
			data.validate(errors);
			if (!errors.hasErrors()) {
				MP5Executor executor = executorLocator.findExecutor(data.getExecutorUID());
				if (executor == null) {
					errors.rejectValue("executorUID", "error.mp5.executorNotFound", new Object[] { data.getExecutorUID() }, _("Executor not found"));
				}
			}
		}
	}

	@Override
	public int getTickerCount() {
		return 5;
	}

	@Override
	public String getTickerLabel(int ticker) {
		switch (ticker) {
		case 0 :
			return _("Segment progress");
		case 1 :
			return _("Channel progress");
		case 2 :
			return _("Atom (non-linear progress)");
		case 3 :
			return _("Current atom matching progress");
		case 4:
			return _("Data filtering");
		default :
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int[] getVersion() {
		return VERSION;
	}

	@Override
	public BaseMethodData createData() {
		return new MP5Data();
	}

	@Override
	public Class<?> getResultClass() {
		return MP5Result.class;
	}

	@Override
	public boolean supportsDataClass(Class<?> clazz) {
		return MP5Data.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean isDataSuspended(Object data) {
		return false;
	}

	@Override
	public void readFromPersistence(Object dataObj, File file) throws IOException, SignalMLException {

		MP5Data data = (MP5Data) dataObj;

		logger.debug("Reading from file [" + file.getAbsolutePath() +"]");
		XMLUtils.objectFromFile(data, file, getStreamer());

		SignalProcessingChainDescriptor chainDescriptor = data.getChainDescriptor();
		if (chainDescriptor == null) {
			throw new NullPointerException("No chain descriptor");
		}

		SegmentedSampleSourceDescriptor sourceDescriptor = data.getSourceDescriptor();
		if (sourceDescriptor == null) {
			throw new NullPointerException("No source descriptor");
		}

		SignalProcessingChain chain = new SignalProcessingChain(chainDescriptor);
		data.setSampleSource(sourceDescriptor.createSegmentedSource(chain));

	}

	@Override
	public File writeToPersistence(Object dataObj) throws IOException, SignalMLException {

		MP5Data data = (MP5Data) dataObj;

		File workingDirectory = data.getWorkingDirectory();
		if (workingDirectory == null) {
			throw new SignalMLException("No working directory");
		}

		File file = new File(workingDirectory, "serialized.xml");

		logger.debug("Writing to file [" + file.getAbsolutePath() +"]");
		XMLUtils.objectToFile(data, file, getStreamer());

		return file;

	}

	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createMP5Streamer();
		}
		return streamer;
	}

	private XStream createMP5Streamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForMontage(streamer);
		Annotations.configureAliases(
			streamer,
			SignalProcessingChainDescriptor.class,
			SelectionSegmentedSampleSourceDescriptor.class,
			MarkerSegmentedSampleSourceDescriptor.class,
			MP5Data.class,
			MP5Parameters.class
		);
		streamer.setMode(XStream.ID_REFERENCES);

		return streamer;
	}

	@Override
	public void cleanUp(Object dataObj) {

		MP5Data data = (MP5Data) dataObj;

		File workingDirectory = data.getWorkingDirectory();
		if (workingDirectory != null && workingDirectory.exists()) {
			logger.info("Cleaning up directory [" + workingDirectory.getAbsolutePath() + "]");
			File[] files = workingDirectory.listFiles();
			boolean deleteOk;
			for (File f : files) {
				logger.info("Deleting file [" + f.getAbsolutePath() + "]");
				deleteOk = f.delete();
				if (!deleteOk) {
					logger.error("Failed to delete");
				}
			}
			deleteOk = workingDirectory.delete();
			if (!deleteOk) {
				logger.error("Failed to delete directory");
			}
		}

	}

	@Override
	public void setProcessedSampleCount(int sampleCount) {
		if (tracker != null)
			tracker.setTicker(4, sampleCount);
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isRequestingAbort() {
		return false;
	}

}
