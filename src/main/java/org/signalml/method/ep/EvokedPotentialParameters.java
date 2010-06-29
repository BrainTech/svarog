/* EvokedPotentialParameters.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;
import org.signalml.domain.signal.space.SignalSpace;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** EvokedPotentialParameters
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("epparameters")
public class EvokedPotentialParameters implements Serializable, Preset {

	private static final long serialVersionUID = 1L;

	private String name;

	private SignalSpace signalSpace;

	public EvokedPotentialParameters() {
		signalSpace = new SignalSpace();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public SignalSpace getSignalSpace() {
		return signalSpace;
	}

	public void setSignalSpace(SignalSpace signalSpace) {
		this.signalSpace = signalSpace;
	}

	public void validate(Errors errors) {


	}

	@Override
	public String toString() {
		return name;
	}

}
