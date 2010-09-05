/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.signalml.domain.signal;

/**
 *
 * @author Piotr Szachewicz
 */
public class RoundBufferSampleSource implements SampleSource {
	protected int nextInsertPos=0;
 	protected boolean full;

        protected double samples[];
        protected int sampleCount;

        RoundBufferSampleSource(int sampleCount){
            this.sampleCount=sampleCount;
            samples=new double[sampleCount];
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

        protected synchronized void incrNextInsertPos() {
            nextInsertPos++;
            if (nextInsertPos == sampleCount){
                    nextInsertPos = 0;
                    full=true;
            }
        }

        public void addSamples(double[] newSamples){
            for(int i=0;i<newSamples.length;i++){
                samples[nextInsertPos]=newSamples[i];
                incrNextInsertPos();
            }
        }

        double[] getSamples(){
            return this.samples;
        }

        @Override
        public void getSamples(double[] target, int signalOffset, int count, int arrayOffset){
            	double[] tmp = new double[sampleCount];
		if (full) {
			for( int i=0; i<sampleCount; i++ ) {
				tmp[i] = samples[(nextInsertPos+i)%sampleCount];
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
					tmp[i] = samples[i - n];
				}
			}
		}
		for( int i=0; i<count; i++ ) {
			target[arrayOffset+i] = tmp[signalOffset+i]; //tu array index = -4
		}

        }

        @Override
        public boolean isCalibrationCapable() {
            return false;
        }

        @Override
        public boolean isSamplingFrequencyCapable() {
            return false;
        }

        @Override
        public boolean isChannelCountCapable() {
            return true;
        }

        @Override
        public float getSamplingFrequency() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getSampleCount() {
            return sampleCount;
        }

        @Override
        public float getCalibration() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getLabel() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

}
