/* BandPassSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage.filter.predefined;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a band-pass sample filter,
 * which passes frequencies 10-30 Hz.
 * Allows to create and duplicate a filter.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("bandpassfilter")
public class BandPassSampleFilter extends TimeDomainSampleFilter {

	private static final long serialVersionUID = 1L;

        /**
         * Constructor. Creates band-pass filter with default coefficients.
         * Passes frequencies 10-30 Hz.
         */
	public BandPassSampleFilter() {

		messageCodes = new String[] { "sampleFilter.td.bandPass" };
		effectCodes = new String[] { "sampleFilter.td.bandPassEffect" };
		defaultEffectDescription = "10-30 Hz";

		aCoefficients = new double[] {
		        4.769534E-7,
		        0.0,
		        -1.4308603E-6,
		        0.0,
		        1.4308603E-6,
		        0.0,
		        -4.769534E-7
		};

		bCoefficients = new double[] {
		        1.0,
		        -5.9680324,
		        14.841216,
		        -19.684528,
		        14.686607,
		        -5.844336,
		        0.96907
		};

		margin = aCoefficients.length * 4;

	}

        /**
         * Duplicates the current object.
         * @return copy of a current object
         */
	@Override
	public SampleFilterDefinition duplicate() {
		BandPassSampleFilter duplicate = new BandPassSampleFilter();
		duplicate.passCount = passCount;
		return duplicate;
	}

}
