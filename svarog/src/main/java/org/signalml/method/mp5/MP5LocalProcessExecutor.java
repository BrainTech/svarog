/* MP5LocalProcessExecutor.java created 2008-02-08
 *
 */

package org.signalml.method.mp5;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.util.ResolvableString;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MP5LocalProcessExecutor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mp5localexecutor")
public class MP5LocalProcessExecutor implements MP5Executor {

	protected static final Logger logger = Logger.getLogger(MP5LocalProcessExecutor.class);

	private static final String[] CODES = new String[] { "mp5Method.executor.localProcess" };

	private String uid;

	private String name;
	private String mp5ExecutablePath;

	private transient MP5ConfigCreator configCreator = new MP5ConfigCreator();
	private transient RawSignalWriter rawSignalWriter = new RawSignalWriter();
	private transient MP5LocalProcessController processController = new MP5LocalProcessController();

	public MP5LocalProcessExecutor() {
		uid = UUID.randomUUID().toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getUID() {
		return uid;
	}

	public String getMp5ExecutablePath() {
		return mp5ExecutablePath;
	}

	public void setMp5ExecutablePath(String mp5ExecutablePath) {
		this.mp5ExecutablePath = mp5ExecutablePath;
	}

	@Override
	public boolean execute(MP5Data data, int segment, File resultFile, MethodExecutionTracker tracker) throws ComputationException {

		File workingDirectory = data.getWorkingDirectory();

		MP5Parameters parameters = data.getParameters();
		MultichannelSegmentedSampleSource sampleSource = data.getSampleSource();

		File configFile = new File(workingDirectory, "mp5.cfg");
		File signalFile = new File(workingDirectory, "signal.bin");

		MP5RuntimeParameters runtimeParameters = new MP5RuntimeParameters();

		runtimeParameters.setChannelCount(sampleSource.getChannelCount());
		runtimeParameters.setSegementSize(sampleSource.getSegmentLength());
		runtimeParameters.setChosenChannels(null);
		runtimeParameters.setDataFormat(MP5SignalFormatType.FLOAT);
		runtimeParameters.setFooterSize(0);
		runtimeParameters.setHeaderSize(0);
		runtimeParameters.setOutputDirectory(null);
		runtimeParameters.setPointsPerMicrovolt(1F);
		runtimeParameters.setSamplingFrequency(sampleSource.getSamplingFrequency());
		runtimeParameters.setSignalFile(signalFile);
		runtimeParameters.setWritingMode(MP5WritingModeType.CREATE);
		runtimeParameters.setResultFileExtension(null);

		SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
		signalExportDescriptor.setSampleType(RawSignalSampleType.FLOAT);
		signalExportDescriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
		signalExportDescriptor.setNormalize(false);

		Formatter configFormatter = configCreator.createConfigFormatter();

		String rawConfig = parameters.getRawConfigText();
		if (rawConfig == null) {
			configCreator.writeRuntimeInvariantConfig(parameters, configFormatter);
		} else {
			configCreator.writeRawConfig(rawConfig, configFormatter);
		}

		configCreator.writeRuntimeConfig(runtimeParameters, configFormatter);

		// write config
		try {
			configCreator.writeMp5Config(configFormatter, configFile);
		} catch (IOException ex) {
			logger.error("Failed to create config file", ex);
			throw new ComputationException(ex);
		}

		tracker.setMessage(new ResolvableString("mp5Method.message.writingSignalFile"));

		// write data file
		try {
			rawSignalWriter.writeSignal(signalFile, sampleSource, signalExportDescriptor, segment, null);
		} catch (IOException ex) {
			logger.error("Failed to create data file", ex);
			throw new ComputationException(ex);
		}

		// delete results in the way
		File generatedBookFile;
		if (parameters.getAlgorithm() == MP5Algorithm.SMP) {
			generatedBookFile = new File(workingDirectory, "signal_smp.b");
		} else {
			generatedBookFile = new File(workingDirectory, "signal_mmp.b");
		}

		if (generatedBookFile.exists()) {
			generatedBookFile.delete();
		}

		tracker.setMessage(new ResolvableString("mp5Method.message.startingExecutable"));

		boolean executionOk = processController.executeProcess(workingDirectory, mp5ExecutablePath, configFile, tracker);
		if (!executionOk) {
			return false;
		}

		if (!generatedBookFile.exists()) {
			logger.error("MP5 process didn't produce expected result file [" + generatedBookFile.getAbsolutePath() + "]");
			throw new ComputationException("error.mp5.exitedWithNoResult");
		}

		boolean renameOk = generatedBookFile.renameTo(resultFile);
		if (!renameOk) {
			logger.error("Failed to rename [" + generatedBookFile.getAbsolutePath() + "] to [" + resultFile.getAbsolutePath() + "]");
			throw new ComputationException("error.mp5.resultRenameFailed");
		}

		return true;

	}

	@Override
	public Object[] getArguments() {
		return new Object[] { name };
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return "MP5LocalProcessExecutor [" + name + "]";
	}

	@Override
	public String toString() {
		return getDefaultMessage();
	}

}
