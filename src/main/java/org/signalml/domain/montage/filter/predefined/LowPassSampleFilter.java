/* LowPassSampleFilter.java created 2008-02-01 modified 2010-08-24
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
		defaultEffectDescription = "0-2 Hz";

                /*a,b generated in python;
                        wp=2Hz, ws=4Hz
                b,a = signal.iirdesign(wp = 0.0625, ws=0.125 , gstop= 30, gpass=3, ftype=butter)
                 */

                aCoefficients=new double[]{
                1.000000000000000000000000000000000000000000000000000000000000,
                -4.357143726284558482575448579154908657073974609375000000000000,
                7.630384157448907522791614610468968749046325683593750000000000,
                -6.710226111950030158936897350940853357315063476562500000000000,
                2.962152618150947436248543453984893858432769775390625000000000,
                -0.524938519332786790450029457133496180176734924316406250000000
                };

                bCoefficients=new double[]{
                0.000007138063515025385789001567682365845257663750089704990387,
                0.000035690317575126913698414787834423123058513738214969635010,
                0.000071380635150253840949356731737651671210187487304210662842,
                0.000071380635150253840949356731737651671210187487304210662842,
                0.000035690317575126913698414787834423123058513738214969635010,
                0.000007138063515025385789001567682365845257663750089704990387
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
