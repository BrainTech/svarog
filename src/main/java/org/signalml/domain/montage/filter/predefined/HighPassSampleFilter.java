/* HighPassSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage.filter.predefined;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** HighPassSampleFilter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("highpassfilter")
public class HighPassSampleFilter extends TimeDomainSampleFilter {

	private static final long serialVersionUID = 1L;

	public HighPassSampleFilter() {

		messageCodes = new String[] { "sampleFilter.td.highPass" };
		effectCodes = new String[] { "sampleFilter.td.highPassEffect" };
		defaultEffectDescription = "20-4000 Hz";

		aCoefficients = new double[] {
		        0.97010994,
		        -5.8206596,
		        14.551649,
		        -19.402199,
		        14.551649,
		        -5.8206596,
		        0.97010994
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
		HighPassSampleFilter duplicate = new HighPassSampleFilter();
		duplicate.passCount = passCount;
		return duplicate;
	}

}
