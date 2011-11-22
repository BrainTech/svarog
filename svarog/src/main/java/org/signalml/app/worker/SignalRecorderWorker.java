package org.signalml.app.worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptor.SourceSignalType;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;

/** 
 * SignalRecorderWorker
 */
public class SignalRecorderWorker {

        /**
         * Logger.
         */
	protected static final Logger logger = Logger.getLogger( SignalRecorderWorker.class);

        /**
         * Array to store received samples in.
         */
	private List<double[]> sampleList;

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
	private OpenMonitorDescriptor monitorDescriptor;

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
         * Default constructor.
         * @param dataPath path to output file
         * @param monitorDescriptor object describing the signal        
         * @throws FileNotFoundException when output stream cannot be initialized
         */
	public SignalRecorderWorker(String dataPath, OpenMonitorDescriptor monitorDescriptor) throws FileNotFoundException {

                String metadataPath;
                if (dataPath.endsWith(".raw")) {
			metadataPath = dataPath.substring(0, dataPath.length() - 4) + ".xml";
                } else {
			metadataPath = dataPath + ".xml";
			dataPath = dataPath + ".raw";
		}

                this.dataFilePath = dataPath;
                this.metadataFilePath = metadataPath;
               
		this.sampleList = new ArrayList<double[]>();		
		this.monitorDescriptor = monitorDescriptor;

                this.backupFrequencyInMiliseconds = monitorDescriptor.getBackupFrequency() * 1000;
                this.lastOffer = System.currentTimeMillis();
                this.timeElapsed = 0;
                
                this.finished = false;
		this.savedSampleCount = 0;
                this.firstSampleTimestamp = Double.NaN;

                this.tagRecorder = null;

		logger.setLevel((Level) Level.INFO);
	}

	/**
	 * Adds samples to the list, and if it is time for a backup - saves them.
	 * @param samples samples to be recorded
	 */
	public void offerChunk(double[] samples) {

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

                synchronized(this) {
                        
                        doSave(false);
                        finished = true;
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
                
                int sampleSize = monitorDescriptor.getSampleType().getByteWidth();
                int chunkSize = sampleList.get(0).length * sampleSize;

                byte[] toSave = new byte[chunkSize * chunkCount];

                int position = 0;
                for (int i = 0; i < chunkCount; i++) {

                        double[] chunk = sampleList.get(i);
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
	private byte[] processChunk(double[] chunk) {

		byte[] byteBuffer = new byte[chunk.length * monitorDescriptor.getSampleType().getByteWidth()];

		ByteBuffer bBuffer = ByteBuffer.wrap(byteBuffer).order(monitorDescriptor.getByteOrder().getByteOrder());		
		DoubleBuffer buf = bBuffer.asDoubleBuffer();

		buf.clear();
		buf.put(chunk, 0, chunk.length);

		return byteBuffer;
	}

        /**
         * Saves metadata.
         * @param isBackup whether this save is a backup or a normal save
         * @throws IOException when file cannot be used
         */
        private void saveMetadata(boolean isBackup) throws IOException {

                File metadataFile = new File(metadataFilePath);
                if (metadataFile.exists())
                        metadataFile.delete();
                metadataFile.createNewFile();

		RawSignalDescriptor rsd = new RawSignalDescriptor();
		rsd.setExportFileName(dataFilePath);
		rsd.setBlocksPerPage(1);
		rsd.setByteOrder(monitorDescriptor.getByteOrder());
		rsd.setCalibrationGain(monitorDescriptor.getSelectedChannelsCalibrationGain());
		rsd.setCalibrationOffset(monitorDescriptor.getSelectedChannelsCalibrationOffset());
		rsd.setChannelCount(monitorDescriptor.getSelectedChannelsCount());
		rsd.setChannelLabels(monitorDescriptor.getSelectedChannelsLabels());
		rsd.setPageSize(monitorDescriptor.getPageSize());
		rsd.setSampleCount(savedSampleCount);
		rsd.setSampleType(monitorDescriptor.getSampleType());
		rsd.setSamplingFrequency(monitorDescriptor.getSamplingFrequency());
		rsd.setSourceSignalType(SourceSignalType.RAW);
		rsd.setFirstSampleTimestamp(firstSampleTimestamp);
                rsd.setIsBackup(isBackup);
		if (monitorDescriptor.getEegSystem() != null)
			rsd.setEegSystemName(monitorDescriptor.getEegSystem().getName());

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
		if (Double.isNaN(firstSampleTimestamp)) {
			return false;
                }
		return true;
	}

        /**
         * Sets {@link #tagRecorder}.
         * @param tagRecorder {@link #tagRecorder}
         */
        public void setTagRecorder(TagRecorder tagRecorder) {
                this.tagRecorder = tagRecorder;
        }


}
