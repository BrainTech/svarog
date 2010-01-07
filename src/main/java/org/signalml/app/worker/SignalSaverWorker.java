package org.signalml.app.worker;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalExportDescriptor;

/** 
 * SignalSaverWorker
 */
public class SignalSaverWorker extends SwingWorker< Integer, Integer> {

    protected static final Logger logger = Logger.getLogger( SignalSaverWorker.class);

    private LinkedBlockingQueue< int[]> sampleQueue;
    private OutputStream outputStream;
    private SignalExportDescriptor signalDescriptor;
    private Integer pollingInterval;

    private volatile boolean pendingAbort;
    private volatile int savedSampleCount;

	public SignalSaverWorker( LinkedBlockingQueue< int[]> sampleQueue, 
	                          OutputStream outputStream, 
	                          SignalExportDescriptor signalDescriptor,
	                          Integer pollingInterval) {
	    this.sampleQueue = sampleQueue;
	    this.outputStream = outputStream;
	    this.signalDescriptor = signalDescriptor;
	    this.pollingInterval = pollingInterval;
	    pendingAbort = false;
	    savedSampleCount = 0;
	}

	@Override
	protected Integer doInBackground() throws Exception {

	    while (!pendingAbort) {
	        int[] chunk  = null;
	        try {
	            chunk = sampleQueue.poll( pollingInterval, TimeUnit.MILLISECONDS);
	        }
	        catch ( InterruptedException e) {
                return new Integer( getSavedSampleCount());
            }
	        if (chunk != null) {
	            saveChunk( chunk);
	        }
            if( pendingAbort() ) {
                return new Integer( getSavedSampleCount());
            }
	    }

		return null;
	}

	private void saveChunk( int[] chunk) throws IOException {

        int sampleByteSize = signalDescriptor.getSampleType().getByteWidth();

        byte[] byteBuffer = new byte[ chunk.length * sampleByteSize];

        ByteBuffer bBuffer = ByteBuffer.wrap(byteBuffer).order( signalDescriptor.getByteOrder().getByteOrder());
        
        IntBuffer iBuffer = bBuffer.asIntBuffer();
        // enforce byte order
        iBuffer.clear();
        iBuffer.put( chunk, 0, chunk.length);
        outputStream.write( byteBuffer, 0, chunk.length);
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
