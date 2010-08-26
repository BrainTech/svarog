/* TimeDomainSampleFilter.java created 2008-02-01 modified 2010-08-26
 * 
 */

package org.signalml.domain.montage.filter;

import org.signalml.util.ResolvableString;
import org.springframework.context.MessageSourceResolvable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** TimeDomainSampleFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("timeDomainSampleFilter")
public class TimeDomainSampleFilter extends SampleFilterDefinition {

	private static final long serialVersionUID = 1L;

	protected Object[] arguments;
	protected String[] messageCodes;

	protected double aCoefficients[];
	protected double bCoefficients[];

        public TimeDomainSampleFilter(){
        }

        public TimeDomainSampleFilter(String messageCode, String passBand, double[] aCoefs, double[] bCoefs) {
                this.messageCodes=new String[] {messageCode};
                this.arguments=new Object[]{new String(passBand)};

                this.aCoefficients=aCoefs.clone();
                this.bCoefficients=bCoefs.clone();

                this.setDescription("Time Domain Filter");

	}
	
	public double[] getACoefficients() {
		return aCoefficients;
	}

	public double[] getBCoefficients() {
		return bCoefficients;
	}

	@Override
	public MessageSourceResolvable getEffectDescription() {
            return new ResolvableString(messageCodes, arguments, getDefaultEffectDescription());
	}
	
	@Override
	public String getDefaultEffectDescription() {
            return new String("Time Domain Filter");
	}

	@Override
	public SampleFilterType getType() {
		return SampleFilterType.TIME_DOMAIN;
	}

	@Override
	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public String[] getCodes() {
		return messageCodes;
	}

	@Override
	public String getDefaultMessage() {
            return "Time domain filter "+getClass().getSimpleName();
	}

        @Override
        public SampleFilterDefinition duplicate() {
                TimeDomainSampleFilter duplicate=new TimeDomainSampleFilter();

                duplicate.aCoefficients=aCoefficients.clone();
                duplicate.bCoefficients=bCoefficients.clone();
                duplicate.messageCodes=messageCodes.clone();
                duplicate.arguments=arguments.clone();

		return duplicate;
	}
	
}
