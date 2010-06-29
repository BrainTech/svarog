/* SignalChecksum.java created 2007-09-28
 *
 */

package org.signalml.domain.signal;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/** SignalChecksum
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("signature")
public class SignalChecksum implements Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamAsAttribute
	private String method;

	@XStreamAsAttribute
	private int offset;

	@XStreamAsAttribute
	private int length;

	@XStreamAsAttribute
	private String value;

	public SignalChecksum(String method, int offset, int length, String value) {
		this.method = method;
		this.offset = offset;
		this.length = length;
		this.value = value;
	}

	public SignalChecksum() {
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
