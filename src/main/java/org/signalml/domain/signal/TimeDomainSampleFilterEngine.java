/* TimeDomainSampleFilterEngine.java created 2010-08-24
 *
 */

package org.signalml.domain.signal;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/**
 * This class represents a Time Domain (IIR or FIR) filter of samples.
 * Allows to return the filtered samples based on the given source.
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainSampleFilterEngine extends SampleFilterEngine {
    protected static final Logger logger = Logger.getLogger(TimeDomainSampleFilterEngine.class);

        protected RoundBufferSampleSource filtered=null;

        /**
         * a Coefficients of the Time Domain filter (feedback filter coefficients).
         */
        protected double aCoefficients[];
        /**
         * b Coefficients of the Time Domain filter (feedforward filter coefficients).
         */
	protected double bCoefficients[];

        protected int filterOrder;

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
                filterOrder=definition.getFilterOrder();
                filtered=null;
	}

        protected double[] getUnfilteredSamplesCache(int newSamples){
            int unfilteredSamplesNeeded=newSamples+filterOrder;
            int zeroPaddingSize=0;
            double[] unfilteredSamplesCache=new double[unfilteredSamplesNeeded];

            System.out.println("UnfilteredSamplesNeeded="+unfilteredSamplesNeeded);
            if(unfilteredSamplesNeeded>source.getSampleCount())
                zeroPaddingSize=unfilteredSamplesNeeded-source.getSampleCount();
            for(int i=0;i<zeroPaddingSize;i++)
                unfilteredSamplesCache[i]=0.0;

            System.out.println("ZeroPaddingSize="+zeroPaddingSize);
            System.out.println("start="+(source.getSampleCount()-unfilteredSamplesNeeded+zeroPaddingSize));
            System.out.println("count="+(unfilteredSamplesNeeded-zeroPaddingSize));
            source.getSamples(unfilteredSamplesCache,
                    source.getSampleCount()-unfilteredSamplesNeeded+zeroPaddingSize,
                    unfilteredSamplesNeeded-zeroPaddingSize, zeroPaddingSize);
            return unfilteredSamplesCache;
        }

        protected double[] getFilteredSamplesCache(int newSamples){
            int filteredCacheSize=newSamples+filterOrder;
            int zeroPaddingSize=0;
            double[] filteredSamplesCache=new double[filteredCacheSize];

            if(filtered==null){
                filtered=new RoundBufferSampleSource(source.getSampleCount());
                for(int i=0;i<source.getSampleCount();i++)
                    filtered.addSamples(new double[] {0.0});
            }

            if(filteredCacheSize>filtered.getSampleCount())
                zeroPaddingSize=filteredCacheSize-filtered.getSampleCount();
            for(int i=0;i<zeroPaddingSize;i++)
                filteredSamplesCache[i]=0.0;

            filtered.getSamples(filteredSamplesCache, filtered.getSampleCount()-filterOrder, filterOrder, zeroPaddingSize);
            return filteredSamplesCache;
        }

        protected double[] calculateNewFilteredSamples(double[] unfilteredSamplesCache, double[] filteredSamplesCache, int newSamples){
            for(int i=filterOrder;i<filteredSamplesCache.length;i++){
                for(int j=i-filterOrder;j<=i;j++){
                    filteredSamplesCache[i]+=unfilteredSamplesCache[j]*bCoefficients[i-j];
                    if(j<i)
                        filteredSamplesCache[i]-=filteredSamplesCache[j]*aCoefficients[i-j];
                }
                filteredSamplesCache[i]/=aCoefficients[0];

            }

            double[] newFilteredSamples=new double[newSamples];
            for(int i=0;i<newSamples;i++)
                newFilteredSamples[i]=filteredSamplesCache[filterOrder+i];
            return newFilteredSamples;
        }

        public synchronized void updateCache(int newSamples){
            System.out.println("TimeSampleFilterEngine: Updating cache: "+newSamples+" new samples");

            String s="";
            double[] unfilteredSamplesCache=getUnfilteredSamplesCache(newSamples);

            int i;
            s+="UnfilteredSmplesCache ["+unfilteredSamplesCache.length+"]:";
            for(i=0;(i<unfilteredSamplesCache.length)&&(i<20);i++)
              s+=unfilteredSamplesCache[i]+", ";
            s+=" [...] ";
            i=unfilteredSamplesCache.length-20;
            if(i<0) i=0;
            for(;i<unfilteredSamplesCache.length;i++)
              s+=unfilteredSamplesCache[i]+", ";
            logger.debug(s);

            double[] filteredSamplesCache=getFilteredSamplesCache(newSamples);
            s="FilteredSamplesCache ["+filteredSamplesCache.length+"[:";
            for(i=0;(i<filteredSamplesCache.length)&&(i<20);i++)
                s+=filteredSamplesCache[i]+", ";
            s+=" [...] ";
            i=filteredSamplesCache.length-20;
            if(i<0) i=0;
            for(;(i<filteredSamplesCache.length);i++)
                s+=filteredSamplesCache[i]+", ";
            logger.debug(s);

            double[] newFilteredSamples=calculateNewFilteredSamples(unfilteredSamplesCache, filteredSamplesCache, newSamples);
            s="newFilteredSamplesCache ["+newFilteredSamples.length+": ";
            for(i=0;(i<newFilteredSamples.length)&&(i<20);i++)
                s+=newFilteredSamples[i]+", ";
            s+=" [...] ";
            i=filteredSamplesCache.length-20;
            if(i<0) i=0;
            for(;(i<filteredSamplesCache.length);i++)
                s+=filteredSamplesCache[i]+", ";
            logger.debug(s);
            
            filtered.addSamples(newFilteredSamples);
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
	public synchronized void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		synchronized( this ) {
                    if(filtered==null){
                        //logger.debug("TEST21: Wywołuję updateCache z getSamples");
                        updateCache(source.getSampleCount());
                    }
                    filtered.getSamples(target, signalOffset, count, arrayOffset);
		}
	}

}
