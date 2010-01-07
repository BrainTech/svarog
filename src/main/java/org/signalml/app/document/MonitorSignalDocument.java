package org.signalml.app.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.signalml.app.view.DocumentView;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.MonitorWorker;
import org.signalml.domain.signal.RoundBufferSampleSource;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.SignalType;
import org.signalml.exception.SignalMLException;

/**
 * @author mario
 *
 */
public class MonitorSignalDocument extends AbstractSignal {

    protected static final Logger logger = Logger.getLogger(MonitorSignalDocument.class);

    private JmxClient jmxClient;
    private MonitorWorker monitorWorker;
    private String name;

    public MonitorSignalDocument( SignalType type) {
        super(type);
    }

    public void setSamplmeSource( RoundBufferSampleSource sampleSource) {
        this.sampleSource = sampleSource;
        sampleSource.setDocumentView( getDocumentView());
    }

    public void setName( String name) {
        this.name = name;
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

//    public String getMultiplexerAddress() {
//        return multiplexerAddress;
//    }
//
//    public void setMultiplexerAddress( String multiplexerAddress) {
//        this.multiplexerAddress = multiplexerAddress;
//    }
//
//    public int getMultiplexerPort() {
//        return multiplexerPort;
//    }
//
//    public void setMultiplexerPort( int multiplexerPort) {
//        this.multiplexerPort = new Integer( multiplexerPort);
//    }

    public JmxClient getJmxClient() {
        return jmxClient;
    }

    public void setJmxClient(JmxClient jmxClient) {
        this.jmxClient = jmxClient;
    }

    @Override
    public void openDocument() throws SignalMLException, IOException {

        System.out.println( "opening document!");

        if (jmxClient == null) {
            throw new IOException(); //TODO
        }

        monitorWorker = new MonitorWorker( jmxClient, (RoundBufferSampleSource) sampleSource);
        monitorWorker.execute();

    }

    @Override
    public void closeDocument() throws SignalMLException {
        if (monitorWorker != null && !monitorWorker.isCancelled()) {
            monitorWorker.cancel( false);
            monitorWorker = null;
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
        // dla monitora tagi nie są obsługiwane
        return null;
    }

}
