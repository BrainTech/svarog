package org.signalml.app.worker.monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalDescriptor.SourceSignalType;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;

/**
 * SignalRecorderWorker
 */
public class SignalRecorderWorker {

	/**
	 * Logger.
	 */
	protected static final Logger logger = Logger.getLogger(SignalRecorderWorker.class);

	/**
	 * Array to store received samples in.
	 */
	private List<float[]> sampleList;

	/**
	 * The stream to save signal to.
	 */
	private OutputStream outputStream;

	/**
	 * Path to data file.
	 */
	private String dataFilePath;

	/**
	 * Path to metadata file.
	 */
	private String metadataFilePath;

	/**
	 * Object describing the signal.
	 */
	private ExperimentDescriptor monitorDescriptor;

	/**
	 * How many samples have been received.
	 */
	private int savedSampleCount;

	/**
	 * How often a backup should be saved.
	 */
	private float backupFrequencyInMiliseconds;

	/**
	 * The timestamp of the first recorded sample.
	 */
	private double firstSampleTimestamp;

	/**
	 * The timestamp of the first recorded video frame.
	 */
	private double firstVideoFrameTimestamp;

	/**
	 * Whether the worker is finished.
	 */
	private volatile boolean finished;

	/**
	 * Timestamp of last offer.
	 */
	private long lastOffer;

	/**
	 * Sum of time elapsed between offers;
	 */
	private long timeElapsed;

	/**
	 * Tags recorder related to current recording.
	 * This object calls the backup method, so it is synchronized.
	 */
	private TagRecorder tagRecorder;

	/**
	 * Descriptor for recorded samples.
	 */
	private RawSignalDescriptor rawSignalDescriptor = new RawSignalDescriptor();

	/**
	 * Default constructor.
	 * @param dataPath path to output file
	 * @param experimentDescriptor object describing the signal
	 * @throws FileNotFoundException when output stream cannot be initialized
	 */
	public SignalRecorderWorker(String dataPath, ExperimentDescriptor experimentDescriptor) throws FileNotFoundException {

		String metadataPath;
		if (dataPath.endsWith(".raw")) {
			metadataPath = dataPath.substring(0, dataPath.length() - 4) + ".xml";
		} else {
			metadataPath = dataPath + ".xml";
			dataPath = dataPath + ".raw";
		}

		this.dataFilePath = dataPath;
		this.metadataFilePath = metadataPath;

		this.sampleList = new ArrayList<float[]>();
		this.monitorDescriptor = experimentDescriptor;

		this.backupFrequencyInMiliseconds = experimentDescriptor.getBackupFrequency() * 1000;
		this.lastOffer = System.currentTimeMillis();
		this.timeElapsed = 0;

		this.finished = false;
		this.savedSampleCount = 0;
		this.firstSampleTimestamp = Double.NaN;
		this.firstVideoFrameTimestamp = Double.NaN;

		this.tagRecorder = null;

		rawSignalDescriptor.setSampleType(RawSignalSampleType.FLOAT);
		rawSignalDescriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);

		logger.setLevel((Level) Level.INFO);
	}

	/**
	 * Adds samples to the list, and if it is time for a backup - saves them.
	 * @param samples samples to be recorded
	 */
	public void offerChunk(float[] samples) {

		synchronized (this) {

			if (!finished) {

				sampleList.add(samples);

				long currentTimestamp = System.currentTimeMillis();
				timeElapsed += currentTimestamp - lastOffer;
				lastOffer = currentTimestamp;

				if (timeElapsed > backupFrequencyInMiliseconds) {

					timeElapsed = 0;
					doSave(true);

					// it's time for a backup - let the TagRecorder know
					if (tagRecorder != null)
						tagRecorder.doBackup();
				}
			}
		}
	}

	/**
	 * Saves all remaining samples.
	 */
	public void save() {

		synchronized (this) {

			doSave(false);
			finished = true;
		}
	}
	
	/**
	 * Stops receiving signal chunks and flushes signal
	 * Does not save metadata.
	 */
	public void stopSaving() {
		
		synchronized (this) {
			try {
				flushSamples();
				finished = true;
			} catch (IOException ex) {
				logger.error("Failed to write samples", ex);
			}
		}
	}

	/**
	 * Saves all data to the disk.
	 * @param isBackup whether the save is a backup
	 */
	private void doSave(boolean isBackup) {

		try {
			flushSamples();
			saveMetadata(isBackup);
		} catch (IOException ex) { }
	}

	/**
	 * Saves all samples from the list to the output.
	 */
	private void flushSamples() throws IOException {

		if (outputStream == null)
			outputStream = new FileOutputStream(dataFilePath);

		int chunkCount = sampleList.size();
		if (chunkCount == 0)
			return;

		int sampleSize = rawSignalDescriptor.getSampleType().getByteWidth();
		int chunkSize = sampleList.get(0).length * sampleSize;

		byte[] toSave = new byte[chunkSize * chunkCount];

		int position = 0;
		for (int i = 0; i < chunkCount; i++) {

			float[] chunk = sampleList.get(i);
			byte[] processedChunk = processChunk(chunk);

			for (int j = 0; j < processedChunk.length; j++) {
				toSave[position++] = processedChunk[j];
			}
		}

		outputStream.write(toSave, 0, toSave.length);
		outputStream.flush();

		savedSampleCount += chunkCount;
		sampleList.clear();
	}

	/**
	 * Processes a chunk to be saved.
	 * @param chunk chunk to be saved
	 * @return processed chunk
	 */
	private byte[] processChunk(float[] chunk) {

		byte[] byteBuffer = new byte[chunk.length * rawSignalDescriptor.getSampleType().getByteWidth()];

		ByteBuffer bBuffer = ByteBuffer.wrap(byteBuffer).order(rawSignalDescriptor.getByteOrder().getByteOrder());
		FloatBuffer buf = bBuffer.asFloatBuffer();

		buf.clear();
		buf.put(chunk, 0, chunk.length);

		return byteBuffer;
	}

	/**
	 * Saves metadata.
	 * @param isBackup whether this save is a backup or a normal save
	 * @throws IOException when file cannot be used
	 */
	public void saveMetadata(boolean isBackup) throws IOException {

		File metadataFile = new File(metadataFilePath);
		if (metadataFile.exists())
			metadataFile.delete();
		metadataFile.createNewFile();

		RawSignalDescriptor rsd = new RawSignalDescriptor();
		rsd.setExportFileName(dataFilePath);
		rsd.setBlocksPerPage(1);
		SignalParameters signalParameters = monitorDescriptor.getSignalParameters();
		rsd.setByteOrder(rawSignalDescriptor.getByteOrder());
		rsd.setCalibrationGain(signalParameters.getCalibrationGain());
		rsd.setCalibrationOffset(signalParameters.getCalibrationOffset());
		rsd.setChannelCount(signalParameters.getChannelCount());
		rsd.setChannelLabels(monitorDescriptor.getAmplifier().getSelectedChannelsLabels());
		rsd.setPageSize(signalParameters.getPageSize());
		rsd.setSampleCount(savedSampleCount);
		rsd.setSampleType(rawSignalDescriptor.getSampleType());
		rsd.setSamplingFrequency(signalParameters.getSamplingFrequency());
		rsd.setSourceSignalType(SourceSignalType.RAW);
		rsd.setFirstSampleTimestamp(firstSampleTimestamp);
		rsd.setIsBackup(isBackup);
		if (monitorDescriptor.getEegSystem() != null)
			rsd.setEegSystemName(monitorDescriptor.getEegSystem().getEegSystemName());

		MonitorRecordingDescriptor descriptor = monitorDescriptor.getMonitorRecordingDescriptor();
		if (descriptor.isVideoRecordingEnabled()) {
			rsd.setVideoFileName(descriptor.getVideoRecordingFilePathWithExtension());
			if (isFirstSampleTimestampSet() && isFirstVideoFrameTimestampSet()) {
				float videoFileOffset = (float) (firstVideoFrameTimestamp - firstSampleTimestamp);
				rsd.setVideoFileOffset(videoFileOffset);
			}
		}

		RawSignalDescriptorWriter descrWriter = new RawSignalDescriptorWriter();
		descrWriter.writeDocument(rsd, metadataFile);
	}

	/**
	 * Sets the timestamp of the first sample for this signal.
	 * @param value new value of the timestamp
	 */
	public void setFirstSampleTimestamp(double value) {
		this.firstSampleTimestamp = value;
	}

	/**
	 * Returns if the firstSampleTimestamp was set using {@link SignalRecorderWorker#setFirstSampleTimestamp(double)}.
	 * @return true if the firstSampleTimestamp was set, false otherwise.
	 */
	public boolean isFirstSampleTimestampSet() {
		return !Double.isNaN(firstSampleTimestamp);
	}

	/**
	 * Sets the timestamp of the first video frame recorder with this signal.
	 * @param value new value of the timestamp
	 */
	public void setFirstVideoFrameTimestamp(double value) {
		this.firstVideoFrameTimestamp = value;
	}

	/**
	 * Returns if the firstSampleTimestamp was set using {@link SignalRecorderWorker#setFirstSampleTimestamp(double)}.
	 * @return true if the firstSampleTimestamp was set, false otherwise.
	 */
	public boolean isFirstVideoFrameTimestampSet() {
		return !Double.isNaN(firstVideoFrameTimestamp);
	}

	/**
	 * Sets {@link #tagRecorder}.
	 * @param tagRecorder {@link #tagRecorder}
	 */
	public void setTagRecorder(TagRecorder tagRecorder) {
		this.tagRecorder = tagRecorder;
	}

}
