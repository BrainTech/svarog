/* EvokedPotentialData.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import java.io.Serializable;

import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.springframework.validation.Errors;

/** EvokedPotentialData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialData implements Serializable {

	private static final long serialVersionUID = 1L;

	private EvokedPotentialParameters parameters;

	private MultichannelSegmentedSampleSource sampleSource;

	public EvokedPotentialData() {
		parameters = new EvokedPotentialParameters();
	}

	public EvokedPotentialData(EvokedPotentialParameters parameters) {
		this.parameters = parameters;
	}

	public MultichannelSegmentedSampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(MultichannelSegmentedSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public EvokedPotentialParameters getParameters() {
		return parameters;
	}

	public void validate(Errors errors) {

		if (sampleSource == null) {
			errors.reject("error.evokedPotential.noSampleSource");
		}

		errors.pushNestedPath("parameters");
		parameters.validate(errors);
		errors.popNestedPath();

	}

}
