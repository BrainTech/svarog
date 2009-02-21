/* ParameterIterationSettings.java created 2007-12-05
 * 
 */

package org.signalml.method.iterator;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;

/** ParameterIterationSettings
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ParameterIterationSettings implements Serializable, MessageSourceResolvable {
	
	private static final long serialVersionUID = 1L;

	private IterableParameter parameter;
	
	private boolean iterated;
	
	private Object startValue;
	private Object endValue;
	
	public ParameterIterationSettings(IterableParameter parameter) {
		if( parameter == null ) {
			throw new NullPointerException( "No parameter" );
		}
		this.parameter = parameter;
	}

	public boolean isIterated() {
		return iterated;
	}

	public void setIterated(boolean iterated) {
		this.iterated = iterated;
	}

	public Object getStartValue() {
		return startValue;
	}

	public void setStartValue(Object startValue) {
		this.startValue = startValue;
	}

	public Object getEndValue() {
		return endValue;
	}

	public void setEndValue(Object endValue) {
		this.endValue = endValue;
	}

	public IterableParameter getParameter() {
		return parameter;
	}
	
	public Object setupForIteration( int iteration, int totalIterations ) {
		if( iterated ) {
			return parameter.setIterationValue(startValue, endValue, iteration, totalIterations);
		} else {
			return parameter.getValue();
		}
	}

	@Override
	public Object[] getArguments() {
		return parameter.getArguments();
	}

	@Override
	public String[] getCodes() {
		return parameter.getCodes();
	}

	@Override
	public String getDefaultMessage() {
		return parameter.getDefaultMessage();
	}
		
}
