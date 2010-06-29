/* AtomFilterChainElement.java created 2008-03-04
 *
 */

package org.signalml.domain.book.filter;

import java.io.Serializable;

import org.signalml.exception.SignalMLException;
import org.springframework.context.MessageSourceResolvable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** AtomFilterChainElement
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("bookfilter")
public abstract class AbstractAtomFilter implements AtomFilter, Serializable, MessageSourceResolvable {

	private static final long serialVersionUID = 1L;

	private String name;

	private boolean enabled;
	private boolean blocking;

	protected AbstractAtomFilter() {
	}

	public AbstractAtomFilter(String name) {
		this.name = name;
		blocking = false;
		enabled = true;
	}

	public AbstractAtomFilter(String name, boolean enabled, boolean blocking) {
		this.name = name;
		this.enabled = enabled;
		this.blocking = blocking;
	}

	public AbstractAtomFilter(AbstractAtomFilter filter) {
		name = filter.name;
		blocking = filter.blocking;
		enabled = filter.enabled;
	}

	public abstract AbstractAtomFilter duplicate();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	public void initialize() throws SignalMLException {
		// do nothing, subclasses may override
	}

}
