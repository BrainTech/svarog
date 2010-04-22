package org.signalml.app.document;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.DocumentView;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.MonitorWorker;
import org.signalml.app.worker.SignalRecorderWorker;
import org.signalml.domain.signal.RoundBufferSampleSource;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.exception.SignalMLException;

/**
 * @author mario
 *
 */
public class MonitorSignalDocument extends AbstractSignal {

    protected static final Logger logger = Logger.getLogger(MonitorSignalDocument.class);

    private JmxClient jmxClient;
    private OpenMonitorDescriptor monitorOptions;
    private OutputStream recorderOutput;
    private MonitorWorker monitorWorker;
    private SignalRecorderWorker recorderWorker;
    private String name;

    public MonitorSignalDocument( OpenMonitorDescriptor monitorOptions) {
        super( monitorOptions.getType());
        this.monitorOptions = monitorOptions;
        double freq = monitorOptions.getSamplingFrequency();
        double ps = monitorOptions.getPageSize();
        int sampleCount = (int) Math.ceil( ps * freq);
        sampleSource = new RoundBufferSampleSource( monitorOptions.getSelectedChannelList().length, sampleCount);
        ((RoundBufferSampleSource) sampleSource).setLabels( monitorOptions.getSelectedChannelList());
        ((RoundBufferSampleSource) sampleSource).setDocumentView( getDocumentView());
    }


    public void setName( String name) {
        this.name = name;
    }

    public float getMinValue() {
        return monitorOptions.getMinimumValue();
    }

    public float getMaxValue() {
        return monitorOptions.getMaximumValue();
    }

    public double[] getGain() {
        double[] result = new double[monitorOptions.getChannelCount()];
        float[] fg = monitorOptions.getCalibrationGain();
        for (int i=0; i<monitorOptions.getChannelCount(); i++)
            result[i] = fg[i];
        return result;
    }

    public double[] getOffset() {
        double[] result = new double[monitorOptions.getChannelCount()];
        float[] fg = monitorOptions.getCalibrationOffset();
        for (int i=0; i<monitorOptions.getChannelCount(); i++)
            result[i] = fg[i];
        return result;
    }

    @Override
    public void setDocumentView(DocumentView documentView) {
        super.setDocumentView(documentView);
        if (documentView != null){
            for (Iterator<SignalPlot> i=((SignalView) documentView).getPlots().iterator(); i.hasNext(); ) {
                SignalPlot signalPlot = i.next();
                SignalProcessingChain signalChain = SignalProcessingChain.createRawChain( sampleSource, getType());
                signalPlot.setSignalChain( signalChain);
            }
        }
        if (sampleSource != null && sampleSource instanceof RoundBufferSampleSource) {
            ((RoundBufferSampleSource) sampleSource).setDocumentView( documentView);
        }
    }

    public JmxClient getJmxClient() {
        return jmxClient;
    }

    public void setJmxClient(JmxClient jmxClient) {
        this.jmxClient = jmxClient;
    }

    public OutputStream getRecorderOutput() {
        return recorderOutput;
    }

    public void setRecorderOutput(OutputStream recorderOutput) {
        this.recorderOutput = recorderOutput;
    }

    @Override
    public void openDocument() throws SignalMLException, IOException {

        if (jmxClient == null) {
            throw new IOException(); //TODO
        }

        LinkedBlockingQueue< double[]> sampleQueue = null;
        if (recorderOutput != null) {
            sampleQueue = new LinkedBlockingQueue< double[]>();
            recorderWorker = new SignalRecorderWorker( sampleQueue, recorderOutput, monitorOptions, 50L); // TODO sparametryzować pollingInterval
            recorderWorker.execute();
        }

        monitorWorker = new MonitorWorker( jmxClient, monitorOptions, (RoundBufferSampleSource) sampleSource);
        if (sampleQueue != null)
            monitorWorker.setSampleQueue( sampleQueue);
        monitorWorker.execute();

    }

    @Override
    public void closeDocument() throws SignalMLException {
        if (monitorWorker != null && !monitorWorker.isCancelled()) {
            monitorWorker.cancel( false);
            monitorWorker = null;
        }
        if (recorderWorker != null && !recorderWorker.isCancelled()) {
            recorderWorker.cancel( false);
            recorderWorker = null;
        }
        super.closeDocument();
    }

    @Override
    public int getBlockCount() {
        return 1;
    }

    @Override
    public float getBlockSize() {
        return getPageSize();
    }

    @Override
    public int getChannelCount() {
        return sampleSource.getChannelCount();
    }

    @Override
    public SignalChecksum[] getChecksums(String[] types,
            SignalChecksumProgressMonitor monitor) throws SignalMLException {
        return null;
    }

    @Override
    public String getFormatName() {
        return null;
    }

    @Override
    public float getMaxSignalLength() {
        return sampleSource.getSampleCount( 0);
    }

    @Override
    public float getMinSignalLength() {
        return sampleSource.getSampleCount( 0);
    }

    @Override
    public int getPageCount() {
        return 1;
    }

    @Override
    public float getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(float pageSize) {
        if( this.pageSize != pageSize ) {
            float last = this.pageSize;
            this.pageSize = pageSize;
            this.blockSize = pageSize;
            pcSupport.firePropertyChange(PAGE_SIZE_PROPERTY, last, pageSize);
        }
    }

    @Override
    public int getBlocksPerPage() {
        return 1;
    }

    @Override
    public void setBlocksPerPage(int blocksPerPage) {
        if (blocksPerPage != 1)
            throw new IllegalArgumentException();
        this.blocksPerPage = 1;
    }

    @Override
    public void addDependentDocument(Document document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeDependentDocument(Document document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Document> getDependentDocuments() {
        return new ArrayList<Document>();
    }

    @Override
    public boolean hasDependentDocuments() {
        return false;
    }

//    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addTagDocument(TagDocument document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TagDocument getActiveTag() {
        // dla monitora tagi nie są obsługiwane - na razie
        return null;
    }

}
