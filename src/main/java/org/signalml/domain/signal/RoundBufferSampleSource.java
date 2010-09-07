/* RoundBufferSampleSource.java created 2010-09-07
 *
 */

package org.signalml.domain.signal;

/**
 * Round buffer which can be used for samples storage/caching.
 * 
 * @author Piotr Szachewicz
 */
public class RoundBufferSampleSource {
        /**
         * an index at which next sample will be stored in samples[] array
         */
	protected int nextInsertPos=0;

        /**
         * a boolean indicating, if the buffer is full
         */
 	protected boolean full;

        /**
         * an array containing samples.
         */
        protected double samples[];
        /**
         * an integer indicating how much samples can be stored in the buffer
         */
        protected int sampleCount;

        /**
         * Constructor. Creates a new {@link RoundBufferSampleSource} which can
         * store up to a given number of samples.
         * @param sampleCount number of samples which the buffer must be able
         * to store
         */
        RoundBufferSampleSource(int sampleCount){
            this.sampleCount=sampleCount;
            samples=new double[sampleCount];
        }

        /**
         * Return the next position at which a new sample will be stored in the
         * samples[] array.
         * @return an index in the samples[] array at which next sample will be
         * stored
         */
      	private int getNextInsertPos() {
		return nextInsertPos;
	}

        /**
         * Changes the value of the position at which the next new sample will be
         * stored.
         * @param the new value of nextInsertPos
         */
	synchronized void setNextInsertPos(int nextInsertPos) {
		this.nextInsertPos = nextInsertPos;
	}

        /**
         * Returns whether the buffer is full or not.
         * @return true if the {@link RoundBufferSampleSource} is full, false
         * otherwise
         */
	synchronized boolean isFull() {
		return full;
	}

        /**
         * Sets the next position in the array at which a new sample
         * will be stored.
         */
        protected synchronized void incrNextInsertPos() {
                nextInsertPos++;
                if (nextInsertPos == sampleCount){
                        nextInsertPos = 0;
                        full=true;
                }
        }

        /**
         * Adds a number of new samples to the buffer.
         * @param newSamples new samples to be added to the buffer
         */
        public void addSamples(double[] newSamples){
                for(int i=0;i<newSamples.length;i++){
                    samples[nextInsertPos]=newSamples[i];
                    incrNextInsertPos();
                }
        }

        /**
         * Returns an array containing samples in this buffer.
         * @return an array containing samples in this buffer
         */
        double[] getSamples(){
                return this.samples;
        }

        /**
         * Returns the given number of samples starting from a given position in time.
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
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

        public int getSampleCount() {
                return sampleCount;
        }

}
