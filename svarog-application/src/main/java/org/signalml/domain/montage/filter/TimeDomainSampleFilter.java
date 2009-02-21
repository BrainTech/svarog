/* TimeDomainSampleFilter.java created 2008-02-01
 * 
 */

package org.signalml.domain.montage.filter;

import org.signalml.util.ResolvableString;
import org.springframework.context.MessageSourceResolvable;

/** TimeDomainSampleFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class TimeDomainSampleFilter extends SampleFilterDefinition {

	private final static Object[] ARGUMENTS = new Object[0];
	
	// these must be built by the constructor
	protected transient String[] messageCodes;
	protected transient String[] effectCodes;
	protected transient String defaultEffectDescription;
	
	protected transient double aCoefficients[];
	protected transient double bCoefficients[];
	protected transient int margin;
		
	protected int passCount;
	
	public double[] getACoefficients() {
		return aCoefficients;
	}

	public double[] getBCoefficients() {
		return bCoefficients;
	}

	public int getMargin() {
		return margin;
	}
	
	public int getPassCount() {
		return passCount;
	}

	public void setPassCount(int passCount) {
		this.passCount = passCount;
	}

	@Override
	public MessageSourceResolvable getEffectDescription() {
		return new ResolvableString( effectCodes, new Object[] { passCount }, defaultEffectDescription );
	}
	
	@Override
	public String getDefaultEffectDescription() {
		return defaultEffectDescription;
	}

	@Override
	public SampleFilterType getType() {
		return SampleFilterType.TIME_DOMAIN;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return messageCodes;
	}

	@Override
	public String getDefaultMessage() {
		return "Time domain filter " + getClass().getSimpleName();
	}
	
}
