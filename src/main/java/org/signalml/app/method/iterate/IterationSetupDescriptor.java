/* IterationSetupDescriptor.java created 2007-12-05
 * 
 */

package org.signalml.app.method.iterate;

import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.MethodIteratorData;

/** IterationSetupDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class IterationSetupDescriptor {

	private IterableMethod method;
	private MethodIteratorData data;
	
	public IterationSetupDescriptor() {
	}
	
	public IterationSetupDescriptor(IterableMethod method, MethodIteratorData data) {
		this.method = method;
		this.data = data;
	}

	public IterableMethod getMethod() {
		return method;
	}

	public void setMethod(IterableMethod method) {
		this.method = method;
	}

	public MethodIteratorData getData() {
		return data;
	}

	public void setData(MethodIteratorData data) {
		this.data = data;
	}
		
}
