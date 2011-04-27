/* MethodIteratorData.java created 2007-12-05
 *
 */

package org.signalml.method.iterator;

import java.io.Serializable;

/** MethodIteratorData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MethodIteratorData implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object subjectMethodData;

	private ParameterIterationSettings[] parameters;

	private int totalIterations;
	private int completedIterations;

	private MethodIteratorResult completedResults;

	public MethodIteratorData() {
		this.totalIterations = 2;
	}

	public Object getSubjectMethodData() {
		return subjectMethodData;
	}

	public void setSubjectMethodData(Object subjectMethodData) {
		this.subjectMethodData = subjectMethodData;
	}

	public ParameterIterationSettings[] getParameters() {
		return parameters;
	}

	public void setParameters(ParameterIterationSettings[] parameters) {
		this.parameters = parameters;
	}

	public int getTotalIterations() {
		return totalIterations;
	}

	public void setTotalIterations(int totalIterations) {
		this.totalIterations = totalIterations;
	}

	public int getCompletedIterations() {
		return completedIterations;
	}

	public void setCompletedIterations(int completedIterations) {
		this.completedIterations = completedIterations;
	}

	public MethodIteratorResult getCompletedResults() {
		return completedResults;
	}

	public void setCompletedResults(MethodIteratorResult completedResults) {
		this.completedResults = completedResults;
	}

	public Object[] setupForIteration(int iteration) {
		Object[] values = new Object[parameters.length];
		int i;
		for (i=0; i<parameters.length; i++) {
			values[i] = parameters[i].setupForIteration(iteration, totalIterations);
		}
		return values;
	}

}
