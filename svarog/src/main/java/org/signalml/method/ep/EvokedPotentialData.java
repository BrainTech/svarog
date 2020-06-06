/* EvokedPotentialData.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import java.io.Serializable;
import java.util.List;
import org.signalml.domain.signal.space.MarkerSegmentedSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.method.BaseMethodData;
import org.springframework.validation.Errors;

/** EvokedPotentialData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialData extends BaseMethodData implements Serializable {

	private static final long serialVersionUID = 1L;

	private EvokedPotentialParameters parameters;
	private List<MarkerSegmentedSampleSource> sampleSources;
	private List<MarkerSegmentedSampleSource> baselineSampleSources;
	private StyledTagSet styledTagSet;

	public EvokedPotentialData() {
		parameters = new EvokedPotentialParameters();
	}

	public EvokedPotentialData(EvokedPotentialParameters parameters) {
		this.parameters = parameters;
	}

	public void setSampleSource(List<MarkerSegmentedSampleSource> sampleSources) {
		this.sampleSources = sampleSources;
	}

	public List<MarkerSegmentedSampleSource> getSampleSources() {
		return sampleSources;
	}

	public List<MarkerSegmentedSampleSource> getBaselineSampleSources() {
		return baselineSampleSources;
	}

	public void setBaselineSampleSources(List<MarkerSegmentedSampleSource> baselineSampleSources) {
		this.baselineSampleSources = baselineSampleSources;
	}

	public EvokedPotentialParameters getParameters() {
		return parameters;
	}

	public void setParameters(EvokedPotentialParameters parameters) {
		this.parameters = parameters;
	}

	public void validate(Errors errors) {

		if (sampleSources == null) {
			errors.reject("error.evokedPotential.noSampleSource");
		}

		errors.pushNestedPath("parameters");
		parameters.validate(errors);
		errors.popNestedPath();

	}

	public void setStyledTagSet(StyledTagSet styledTagSet) {
		this.styledTagSet = styledTagSet;
	}

	public StyledTagSet getStyledTagSet() {
		return styledTagSet;
	}

}
