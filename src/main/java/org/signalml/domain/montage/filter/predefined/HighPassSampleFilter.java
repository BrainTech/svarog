/* HighPassSampleFilter.java created 2008-02-01 modified 2010-08-24
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
		defaultEffectDescription = "8-.. Hz";

                /*
                 a,b generated in python:
                 b,a = signal.iirdesign(wp = 0.125, ws=0.0625 , gstop= 30, gpass=3, ftype='butter')
                  wp=8Hz (if fs=128Hz, then Nyquist freq=64Hz) 0.125*64
                  ws=4Hz (--''--)
                 */

                aCoefficients=new double[]{
                1.000000000000000000000000000000000000000000000000000000000000,
                -3.746443646315793962742191069992259144783020019531250000000000,
                5.736156322400199236710705008590593934059143066406250000000000,
                -4.466584890027154308711487828986719250679016113281250000000000,
                1.763916846779651770305008540162816643714904785156250000000000,
                -0.282066117729388665669176816663821227848529815673828125000000
                };

                bCoefficients=new double[]{
                0.531098994476631003358590987772913649678230285644531250000000,
                -2.655494972383154017592232776223681867122650146484375000000000,
                5.310989944766308923362885252572596073150634765625000000000000,
                -5.310989944766308923362885252572596073150634765625000000000000,
                2.655494972383154017592232776223681867122650146484375000000000,
                -0.531098994476631003358590987772913649678230285644531250000000
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
