/* AbstractSignalTypeConfigurer.java created 2008-02-01
 *
 */

package org.signalml.domain.signal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;

/** AbstractSignalTypeConfigurer
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalTypeConfigurer implements SignalTypeConfigurer {

	private static final List<SampleFilterDefinition> predefinedFilters = getAllPredefinedFilters();

	private static List<SampleFilterDefinition> getAllPredefinedFilters() {

		ArrayList<SampleFilterDefinition> filters = new ArrayList<SampleFilterDefinition>();


		

		//b,a generated in Python (import scipy.signal as signal)

		//b,a=signal.iirdesign(wp = 0.3125, ws=0.46875 , gstop= 40, gpass=3, ftype='cheby1')
		filters.add(new TimeDomainSampleFilter("sampleFilter.td.lowPass", "0-20 Hz",
		                                        new double[] {
		                                                1.0000000000000000000000000000000000000000000000000000000000000000,
		                                                -3.3464713712644158505327141028828918933868408203125000000000000000,
		                                                5.3144381825969482235905161360278725624084472656250000000000000000,
		                                                -4.7969354817306388838460406986996531486511230468750000000000000000,
		                                                2.4468123897669520339093196525936946272850036621093750000000000000,
		                                                -0.5700791722915911075020289899839553982019424438476562500000000000
		                                        },
		                                        new double[] {
		                                                0.0014926420961642345320435909528100637544412165880203247070312500,
		                                                0.0074632104808211689739305683133352431468665599822998046875000000,
		                                                0.0149264209616423431520315645570917695295065641403198242187500000,
		                                                0.0149264209616423431520315645570917695295065641403198242187500000,
		                                                0.0074632104808211689739305683133352431468665599822998046875000000,
		                                                0.0014926420961642345320435909528100637544412165880203247070312500
		                                        }
		                                       ,
						       FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
						       new double[] {20, 0}, new double[] {30, 8}, 5.0, 20.0));

		//b,a=signal.iirdesign(wp = 0.078125, ws=0.0520833333333 , gstop= 40, gpass=3, ftype='cheby1')
		filters.add(new TimeDomainSampleFilter("sampleFilter.td.highPass", "5-.. Hz",
		                                        new double[] {
		                                                1.0000000000000000000000000000000000000000000000000000000000000000,
		                                                -3.7810149027063286730765412357868626713752746582031250000000000000,
		                                                5.6847007899587422841136685747187584638595581054687500000000000000,
		                                                -4.1442428652711198466818132146727293729782104492187500000000000000,
		                                                1.3939736470638048881909298870596103370189666748046875000000000000,
		                                                -0.1460588396290199431071243907354073598980903625488281250000000000
		                                        },
		                                        new double[] {
		                                                0.5046872201446568340088560944423079490661621093750000000000000000,
		                                                -2.5234361007232832818658607720863074064254760742187500000000000000,
		                                                5.0468722014465674519101412442978471517562866210937500000000000000,
		                                                -5.0468722014465674519101412442978471517562866210937500000000000000,
		                                                2.5234361007232832818658607720863074064254760742187500000000000000,
		                                                -0.5046872201446568340088560944423079490661621093750000000000000000
		                                        }
		                                       ,
						       FilterType.HIGHPASS, ApproximationFunctionType.CHEBYSHEV1,
						       new double[] {6, 0}, new double[] {1, 8}, 3.0, 40.0)
						       );

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
