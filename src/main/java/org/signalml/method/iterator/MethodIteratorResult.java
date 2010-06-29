/* MethodIteratorResult.java created 2007-12-05
 *
 */

package org.signalml.method.iterator;

import java.io.Serializable;
import java.util.ArrayList;

/** MethodIteratorResult
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MethodIteratorResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private ParameterIterationSettings[] parameters;

	private ArrayList<Object[]> parameterValues;
	private ArrayList<Object> results;

	public MethodIteratorResult(ParameterIterationSettings[] parameters) {
		this.parameters = parameters;
		results = new ArrayList<Object>();
		parameterValues = new ArrayList<Object[]>();
	}

	public void add(Object result, Object[] parameterArr) {
		if (parameterArr.length < parameters.length) {
			throw new IndexOutOfBoundsException("Parameter values too short");
		}
		results.add(result);
		parameterValues.add(parameterArr);
	}

	public void clear() {
		results.clear();
		parameterValues.clear();
	}

	public ParameterIterationSettings[] getParameters() {
		return parameters;
	}

	public Object getResultAt(int index) {
		return results.get(index);
	}

	public Object[] getParameterValuesAt(int index) {
		return parameterValues.get(index);
	}

	public boolean isEmpty() {
		return results.isEmpty();
	}

	public int size() {
		return results.size();
	}

}
