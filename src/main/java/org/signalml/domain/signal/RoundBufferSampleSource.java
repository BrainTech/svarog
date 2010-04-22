package org.signalml.domain.signal;

import java.util.Iterator;
import java.util.List;

import org.signalml.app.view.DocumentView;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.exception.SignalMLException;

public class RoundBufferSampleSource extends DoubleArraySampleSource implements OriginalMultichannelSampleSource {

    protected int nextInsertPos;
    protected boolean full;
    protected DocumentView documentView;
    protected Object[] labels;

    public RoundBufferSampleSource( int channelCount, int sampleCount) {
        super( null, channelCount, sampleCount);
        this.samples = new double[channelCount][sampleCount];
        nextInsertPos = 0;
        full = false;
    }

    public DocumentView getDocumentView() {
        return documentView;
    }

    public void setDocumentView(DocumentView documentView) {
        this.documentView = documentView;
    }

    synchronized int getNextInsertPos() {
        return nextInsertPos;
    }

    synchronized void setNextInsertPos(int nextInsertPos) {
        this.nextInsertPos = nextInsertPos;
    }

    synchronized boolean isFull() {
        return full;
    }

    synchronized void setFull(boolean full) {
        this.full = full;
    }

    synchronized double[][] getSamples() {
        return this.samples;
    }

    synchronized void setSamples( double[][] samples) {
        this.samples = samples;
    }

    protected synchronized void incrNextInsertPos() {
        nextInsertPos++;
        if (nextInsertPos == sampleCount) {
            full = true;
            nextInsertPos = 0;
        }
    }

    public synchronized void addSamples( double[] newSamples) {
        for (int i=0; i<channelCount; i++) {
            samples[i][nextInsertPos] = newSamples[i];
        }
        incrNextInsertPos();
    }

    public synchronized void addSamples( List<double[]> newSamples) {
        for (Iterator< double[]> i=newSamples.iterator(); i.hasNext(); )
            addSamples( i.next());
        for (Iterator<SignalPlot> i=((SignalView) documentView).getPlots().iterator(); i.hasNext(); )
            i.next().repaint();
    }

//    public synchronized void addSamples( List<Float> newSamples) {
//        for (int i=0; i<channelCount; i++) {
//            samples[i][nextInsertPos] = newSamples.get(i);
//        }
//        incrNextInsertPos();
//    }

    // TODO przy zwykłych źródłach sygnału sampleCount jest znany z góry, a tu nie;
    // tutaj sampleCount oznacza maksymalną ilość próbek w stanie gdy cały bufor już jest wypełniony
    // offset powinien być w ramach przedziału od zera do sampleCount-1 - count
    // jeśli bufor jest pusty to wszystkie próbki od zera do sampleCount - 1 są równe zero
    // jeśli bufor jest częściowo wypełniony, to próbki od zera do sampleCount - 1 - n są równe zero
    // a pozostałe nie, gdzie n jest liczbą próbek zaczytanych do bufora; jesli cały bufor jest wypełiony
    // to offset trzeba przesunąć odpowiednio względem bieżącego punktu wstawiania
    @Override
    public synchronized void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
        // TODO dorobić implementację bez tablicy tymczasowej
        double[] tmp = new double[sampleCount];
        if (full) {
            for( int i=0; i<sampleCount; i++ ) {
                tmp[i] = samples[channel][(nextInsertPos+i)%sampleCount];
            }
        }
        else {
            if (nextInsertPos == 0) {
                for (int i=0; i<sampleCount; i++) {
                    tmp[i] = 0.0;
                }
            }
            else {
                int n = sampleCount - nextInsertPos;
                for (int i=0; i<n; i++)
                    tmp[i] = 0.0;
                for (int i=n; i<sampleCount; i++) {
                    tmp[i] = samples[channel][i - n];
                }
            }
        }
        for( int i=0; i<count; i++ ) {
            target[arrayOffset+i] = tmp[signalOffset+i];
        }
        
//        if (full) {
//            // TODO dorobić przeliczanie indeksu
//            for( int i=0; i<count; i++ ) {
//                target[arrayOffset+i] = samples[channel][signalOffset+i];
//            }
//        }
//        else {
//            if (nextInsertPos == 0) {
//                for (int i=0; i<count; i++) {
//                    target[i] = 0.0;
//                }
//            }
//            else {
//                for (int i=0; i<nextInsertPos; i++)
//                    target[i] = 0.0;
//                for (int i=0; i<count; i++) {
//                    target[arrayOffset+i] = samples[channel][signalOffset+i];
//                }
//            }
//        }
    }

    @Override
    public OriginalMultichannelSampleSource duplicate()
            throws SignalMLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCalibration(float calibration) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setChannelCount(int channelCount) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSamplingFrequency(float samplingFrequency) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getLabel(int channel) {
        if (labels != null)
            return labels[channel].toString();
        else
            return super.getLabel(channel);
    }

    public Object[] getLabels() {
        return labels;
    }

    public void setLabels( Object[] labels) {
        this.labels = labels;
    }

}
