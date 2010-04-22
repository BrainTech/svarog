package org.signalml.app.worker;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.model.OpenMonitorDescriptor;

/** 
 * SignalRecorderWorker
 */
public class SignalRecorderWorker extends SwingWorker< Integer, Integer> {

    protected static final Logger logger = Logger.getLogger( SignalRecorderWorker.class);

    private LinkedBlockingQueue< double[]> sampleQueue;
    private OutputStream outputStream;
    private OpenMonitorDescriptor monitorDescriptor;
    private long pollingInterval;

    private volatile boolean pendingAbort;
    private volatile int savedSampleCount;

	public SignalRecorderWorker( LinkedBlockingQueue< double[]> sampleQueue, 
	                             OutputStream outputStream,
	                             OpenMonitorDescriptor monitorDescriptor,
	                             long pollingInterval) {
	    this.sampleQueue = sampleQueue;
	    this.outputStream = outputStream;
	    this.monitorDescriptor = monitorDescriptor;
	    this.pollingInterval = pollingInterval;
	    pendingAbort = false;
	    savedSampleCount = 0;
	}

	@Override
	protected Integer doInBackground() throws Exception {

	    while (!pendingAbort) {
	        double[] chunk  = null;
	        try {
	            chunk = sampleQueue.poll( pollingInterval, TimeUnit.MILLISECONDS);
	        }
	        catch ( InterruptedException e) {
                return new Integer( getSavedSampleCount());
            }
	        if (chunk != null) {
                logger.debug( "saving chunk...");
	            saveChunk( chunk);
	        }
            if( pendingAbort() ) {
                return new Integer( getSavedSampleCount());
            }
	    }

		return null;
	}

	private void saveChunk( double[] chunk) throws IOException {

        byte[] byteBuffer = new byte[ chunk.length * monitorDescriptor.getSampleType().getByteWidth()];

        ByteBuffer bBuffer = ByteBuffer.wrap(byteBuffer).order( monitorDescriptor.getByteOrder().getByteOrder());
        
        DoubleBuffer buf = bBuffer.asDoubleBuffer();
        // enforce byte order
        buf.clear();
        buf.put( chunk, 0, chunk.length);
        outputStream.write( byteBuffer, 0, byteBuffer.length);
	}

    @Override
    protected void done() {
        firePropertyChange( "savedSamplesCount", null, getSavedSampleCount());
    }

    @Override
    protected void process(List<Integer> chunks) {
        firePropertyChange( "savedSamplesIndex", null, chunks.get( chunks.size() - 1));
    }

    public int getSavedSampleCount() {
        return savedSampleCount;
    }

    public void abort() {
        pendingAbort = true;
    }

    public boolean pendingAbort() {
        return pendingAbort;
    }

}
