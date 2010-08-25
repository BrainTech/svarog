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
		defaultEffectDescription = "0-12 Hz";

                /*
                  a,b generated in python:

                  b,a=signal.iirdesign(wp=0.1875,ws=0.375,gstop=30, gpass=3,ftype='butter')
                  wp=0.1875 [PI*rad/sample]*Nyquist frequency=0.1875*fs/2=12 [Hz]
                  ws=0.375*fs/2=24 Hz

                 *
                 */

                aCoefficients=new double[]{
                    1.000000000000000000000000000000000000000000000000000000000000000000,
                    -2.917786697204184154230688363895751535892486572265625000000000000000,
                    3.681424675604834106934504234232008457183837890625000000000000000000,
                    -2.434258561933403619548244023462757468223571777343750000000000000000,
                    0.834778314581329006216492416569963097572326660156250000000000000000,
                    -0.117863783663858107120248064347833860665559768676757812500000000000

                };

                bCoefficients=new double[]{
                0.001446685855772459044360500612924624874722212553024291992187500000,
                0.007233429278862292186036420105210709152743220329284667968750000000,
                0.014466858557724586106796316187228512717410922050476074218750000000,
                0.014466858557724586106796316187228512717410922050476074218750000000,
                0.007233429278862292186036420105210709152743220329284667968750000000,
                0.001446685855772459044360500612924624874722212553024291992187500000
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
