/* TimeDomainSampleFilterEngine.java created 2010-08-24
 *
 */

package org.signalml.domain.signal;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/**
 * This class represents a Time Domain (IIR or FIR) filter of samples.
 * Allows to return the filtered samples based on the given source.
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainSampleFilterEngine extends SampleFilterEngine {

        /**
         * used to store part of unfiltered signal
         */
	private double[] cache = null;
        /*
         * stores filtered samples
         */
	private double[] filtered = null;

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
			int i,j;

                        int addLeft=1800;
                        if(signalOffset-addLeft<0)
                            addLeft=signalOffset;
                        int newOffset=signalOffset-addLeft;
                        int newCount=count+addLeft;

                        cache = new double[newCount];
                        filtered= new double[newCount];

                        source.getSamples(cache, newOffset, newCount, 0);

                        for(i=0; i< newCount; i++){
                            for(j=i-bCoefficients.length+1; j<=i; j++){
                                if (j<0) j=0;
                                filtered[i]+=cache[j]*bCoefficients[i-j];
                                if (j<i)
                                    filtered[i]-=filtered[j]*aCoefficients[i-j];
                            }
                            filtered[i]/=aCoefficients[0];
                        }

                        for( i=0; i<count; i++ )
                                target[arrayOffset+i]=filtered[addLeft+i];
		}
	}

}
