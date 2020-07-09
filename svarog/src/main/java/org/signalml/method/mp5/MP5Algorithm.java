/* MP2Algorithm.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import java.io.Serializable;
import org.springframework.context.MessageSourceResolvable;

/** MP2Algorithm
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum MP5Algorithm implements Serializable, MessageSourceResolvable {

	SMP("SMP"),
	MMP1("MMP1"),
	MMP2("MMP2"),
	MMP3("MMP3");

	private String name;

	private MP5Algorithm(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "mp5Method.algorithm." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}
