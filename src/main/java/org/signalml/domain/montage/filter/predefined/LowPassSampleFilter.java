/* LowPassSampleFilter.java created 2008-02-01
 * 
 */

package org.signalml.domain.montage.filter.predefined;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** LowPassSampleFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("lowpassfilter")
public class LowPassSampleFilter extends TimeDomainSampleFilter {

	private static final long serialVersionUID = 1L;
	
	public LowPassSampleFilter() {

		messageCodes = new String[] { "sampleFilter.td.lowPass" };
		effectCodes = new String[] { "sampleFilter.td.lowPassEffect" };
		defaultEffectDescription = "0-20 Hz";
			
		aCoefficients = new double[] {
				2.2769885E-13,
				1.3661932E-12,
				3.4154827E-12,
				4.553977E-12,
				3.4154827E-12,
				1.3661932E-12,
				2.2769885E-13
		};
		
		bCoefficients = new double[] {
				1.0,
				-5.939309,
				14.698385,
				-19.400414,
				14.40402,
				-5.703797,
				0.94111335
		};
		
		margin = aCoefficients.length * 4;
			
	}
	
	@Override
	public SampleFilterDefinition duplicate() {
		LowPassSampleFilter duplicate = new LowPassSampleFilter();
		duplicate.passCount = passCount;
		return duplicate;
	}		
	
}
