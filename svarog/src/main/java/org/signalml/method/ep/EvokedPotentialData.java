/* EvokedPotentialData.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import java.io.Serializable;
import java.util.List;

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
	private List<MultichannelSegmentedSampleSource> sampleSources;
	private List<MultichannelSegmentedSampleSource> baselineSampleSources;

	public EvokedPotentialData() {
		parameters = new EvokedPotentialParameters();
	}

	public EvokedPotentialData(EvokedPotentialParameters parameters) {
		this.parameters = parameters;
	}

	public void setSampleSource(List<MultichannelSegmentedSampleSource> sampleSources) {
		this.sampleSources = sampleSources;
	}

	public List<MultichannelSegmentedSampleSource> getSampleSources() {
		return sampleSources;
	}

	public List<MultichannelSegmentedSampleSource> getBaselineSampleSources() {
		return baselineSampleSources;
	}

	public void setBaselineSampleSources(List<MultichannelSegmentedSampleSource> baselineSampleSources) {
		this.baselineSampleSources = baselineSampleSources;
	}

	public EvokedPotentialParameters getParameters() {
		return parameters;
	}

	public void validate(Errors errors) {

		if (sampleSources == null) {
			errors.reject("error.evokedPotential.noSampleSource");
		}

		errors.pushNestedPath("parameters");
		parameters.validate(errors);
		errors.popNestedPath();

	}

}
