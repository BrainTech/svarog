/* TimeDomainSampleFilterEngine.java created 2010-08-24
 *
 */

package org.signalml.domain.signal;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/**
 * This class represents a Time Domain (IIR or FIR) filter of samples.
 * Allows to return the filtered samples based on the given source.
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainSampleFilterEngine extends SampleFilterEngine {

        private int cacheSize=0;
        private int newSamples=0;
        private double[] filteredCache=null;

        /**
         * a Coefficients of the Time Domain filter (feedback filter coefficients).
         */
        protected double aCoefficients[];
        /**
         * b Coefficients of the Time Domain filter (feedforward filter coefficients).
         */
	protected double bCoefficients[];

        /**
         * Constructor. Creates an engine of a filter for provided
         * {@link SampleSource source} of samples.
         * @param source the source of samples
         * @param definition the {@link TimeDomainSampleFilter definition} of the
         * filter
         */
	public TimeDomainSampleFilterEngine( SampleSource source, TimeDomainSampleFilter definition ) {
		super( source );

                this.definition=definition;
                aCoefficients=definition.getACoefficients();
                bCoefficients=definition.getBCoefficients();
	}

        public TimeDomainSampleFilterEngine(SampleSource source, TimeDomainSampleFilter definition, int cacheSize){
            this(source,definition);
            this.cacheSize=cacheSize;
        }

        public synchronized void updateCache(int numberOfNewSamples){
                        newSamples+=numberOfNewSamples;

                        double[] filtered = null;
                        double[] cache = null;

                        int i,j;

                        int newCount=source.getSampleCount();
                        cache = new double[newCount];
                        filtered= new double[newCount];

                        source.getSamples(cache, 0, newCount, 0);

                        System.out.println(this+": new samples="+newSamples);

                        for(i=0; i< newCount; i++){
                            if(filteredCache!=null && i<cacheSize-newSamples){
                                    filtered[i]=filteredCache[newSamples+i];
                            }
                            else{
                                for(j=i-bCoefficients.length+1; j<=i; j++){
                                    if (j<0) j=0;

                                    else{
                                        filtered[i]+=cache[j]*bCoefficients[i-j];
                                        if (j<i)
                                            filtered[i]-=filtered[j]*aCoefficients[i-j];
                                    }
                                }
                                filtered[i]/=aCoefficients[0];
                            }
                        }

                        if(filteredCache==null)
                            filteredCache=new double[cacheSize];

                        for(i=0; i<cacheSize; i++){
                            filteredCache[i]=filtered[i];
                            newSamples=0;
                        }

        }


        @Override
        public TimeDomainSampleFilter getFilterDefinition(){
            return (TimeDomainSampleFilter)definition.duplicate();
        }

        /**
         * Returns the given number of the filtered samples starting from
         * the given position in time.
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized( this ) {
                    
                        for(int i=0; i<count; i++ )
                                target[arrayOffset+i]=filteredCache[signalOffset+i];

		}
	}

}
