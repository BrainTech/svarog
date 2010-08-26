/* AbstractSignalTypeConfigurer.java created 2008-02-01 modified 2010-08-26
 * 
 */

package org.signalml.domain.signal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/** AbstractSignalTypeConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalTypeConfigurer implements SignalTypeConfigurer {

        private static final List<SampleFilterDefinition> predefinedFilters = getAllPredefinedFilters();

	private static List<SampleFilterDefinition> getAllPredefinedFilters() {
		ArrayList<SampleFilterDefinition> filters = new ArrayList<SampleFilterDefinition>();

                //b,a generated in Python import scipy.signal as signal
                //b,a=signal.iirdesign(wp=0.1875,ws=0.375,gstop=30, gpass=3,ftype='butter')
                //wp=0.1875 [PI*rad/sample]*Nyquist frequency=0.1875*fs/2=12 [Hz]
                //ws=0.375*fs/2=24 Hz
                filters.add( new TimeDomainSampleFilter("sampleFilter.td.lowPass", "0-12 Hz",
                    new double[]{
                    1.000000000000000000000000000000000000000000000000000000000000000000,
                    -2.917786697204184154230688363895751535892486572265625000000000000000,
                    3.681424675604834106934504234232008457183837890625000000000000000000,
                    -2.434258561933403619548244023462757468223571777343750000000000000000,
                    0.834778314581329006216492416569963097572326660156250000000000000000,
                    -0.117863783663858107120248064347833860665559768676757812500000000000},
                    new double[]{
                    0.001446685855772459044360500612924624874722212553024291992187500000,
                    0.007233429278862292186036420105210709152743220329284667968750000000,
                    0.014466858557724586106796316187228512717410922050476074218750000000,
                    0.014466858557724586106796316187228512717410922050476074218750000000,
                    0.007233429278862292186036420105210709152743220329284667968750000000,
                    0.001446685855772459044360500612924624874722212553024291992187500000}
                ) );

                //b,a = signal.iirdesign(wp = 0.125, ws=0.0625 , gstop= 30, gpass=3, ftype='butter')
                filters.add( new TimeDomainSampleFilter("sampleFilter.td.highPass", "8-.. Hz",
                    new double[]{
                    1.000000000000000000000000000000000000000000000000000000000000,
                    -3.746443646315793962742191069992259144783020019531250000000000,
                    5.736156322400199236710705008590593934059143066406250000000000,
                    -4.466584890027154308711487828986719250679016113281250000000000,
                    1.763916846779651770305008540162816643714904785156250000000000,
                    -0.282066117729388665669176816663821227848529815673828125000000},
                    new double[]{
                    0.531098994476631003358590987772913649678230285644531250000000,
                    -2.655494972383154017592232776223681867122650146484375000000000,
                    5.310989944766308923362885252572596073150634765625000000000000,
                    -5.310989944766308923362885252572596073150634765625000000000000,
                    2.655494972383154017592232776223681867122650146484375000000000,
                    -0.531098994476631003358590987772913649678230285644531250000000}
                ) );

				
		return Collections.unmodifiableList(filters);
	}
	
	@Override
	public int getPredefinedFilterCount() {
		return predefinedFilters.size();
	}

	@Override
	public Collection<SampleFilterDefinition> getPredefinedFilters() {
		return predefinedFilters;
	}
	
	@Override
	public SampleFilterDefinition getPredefinedFilterAt(int index) {
		return predefinedFilters.get(index);
	}
	
}
