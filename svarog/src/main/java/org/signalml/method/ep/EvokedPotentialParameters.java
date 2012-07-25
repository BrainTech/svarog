/* EvokedPotentialParameters.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
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

	private List<TagStyleGroup> averagedTagStyles = new ArrayList<TagStyleGroup>();
	private float averagingSelectionStart;
	private float averagingSelectionEnd;

	private float baselineSelectionStart;
	private float baselineSelectionEnd;

	private boolean filteringEnabled;
	private float filterCutOffFrequency;
	private List<TagStyleGroup> artifactTagStyles = new ArrayList<TagStyleGroup>();

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

	public List<TagStyleGroup> getAveragedTagStyles() {
		return averagedTagStyles;
	}

	public void setAveragedTagStyles(List<TagStyleGroup> list) {
		this.averagedTagStyles = list;
	}

	public float getAveragingSelectionStart() {
		return averagingSelectionStart;
	}

	public void setAveragingSelectionStart(float averagingSelectionStart) {
		this.averagingSelectionStart = averagingSelectionStart;
	}

	public float getAveragingSelectionEnd() {
		return averagingSelectionEnd;
	}

	public void setAveragingSelectionEnd(float averagingSelectionEnd) {
		this.averagingSelectionEnd = averagingSelectionEnd;
	}

	public float getBaselineSelectionStart() {
		return baselineSelectionStart;
	}

	public void setBaselineSelectionStart(float baselineSelectionStart) {
		this.baselineSelectionStart = baselineSelectionStart;
	}

	public float getBaselineSelectionEnd() {
		return baselineSelectionEnd;
	}

	public void setBaselineSelectionEnd(float baselineSelectionEnd) {
		this.baselineSelectionEnd = baselineSelectionEnd;
	}

	public boolean isFilteringEnabled() {
		return filteringEnabled;
	}

	public void setFilteringEnabled(boolean filteringEnabled) {
		this.filteringEnabled = filteringEnabled;
	}

	public float getFilterCutOffFrequency() {
		return filterCutOffFrequency;
	}

	public void setFilterCutOffFrequency(float filterCutOffFrequency) {
		this.filterCutOffFrequency = filterCutOffFrequency;
	}

	public List<TagStyleGroup> getArtifactTagStyles() {
		return artifactTagStyles;
	}

	public void setArtifactTagStyles(List<TagStyleGroup> artifactTagStyles) {
		this.artifactTagStyles = artifactTagStyles;
	}

	public void validate(Errors errors) {
	}

	@Override
	public String toString() {
		return name;
	}

}
