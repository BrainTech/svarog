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
	private float averagingTimeBefore = 2;
	private float averagingTimeAfter = 3;

	private boolean baselineCorrectionEnabled = true;
	private float baselineTimeBefore = 2;
	private float baselineTimeAfter = -1;

	private boolean filteringEnabled = true;
	private float filterCutOffFrequency = 20;
	private List<TagStyleGroup> artifactTagStyles = new ArrayList<TagStyleGroup>();

	private SignalSpace wholeSignalSpace;

	public EvokedPotentialParameters() {
		wholeSignalSpace = new SignalSpace();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public SignalSpace getWholeSignalSpace() {
		return wholeSignalSpace;
	}

	public void setSignalSpace(SignalSpace signalSpace) {
		this.wholeSignalSpace = signalSpace;
	}

	public List<TagStyleGroup> getAveragedTagStyles() {
		return averagedTagStyles;
	}

	public void setAveragedTagStyles(List<TagStyleGroup> list) {
		this.averagedTagStyles = list;
	}

	public float getAveragingTimeBefore() {
		return averagingTimeBefore;
	}

	public void setAveragingTimeBefore(float averagingTimeBefore) {
		this.averagingTimeBefore = averagingTimeBefore;
	}

	public float getAveragingTimeAfter() {
		return averagingTimeAfter;
	}

	public void setAveragingTimeAfter(float averagingTimeAfter) {
		this.averagingTimeAfter = averagingTimeAfter;
	}

	public float getBaselineTimeBefore() {
		return baselineTimeBefore;
	}

	public void setBaselineTimeBefore(float baselineTimeBefore) {
		this.baselineTimeBefore = baselineTimeBefore;
	}

	public float getBaselineTimeAfter() {
		return baselineTimeAfter;
	}

	public void setBaselineTimeAfter(float baselineTimeAfter) {
		this.baselineTimeAfter = baselineTimeAfter;
	}

	public boolean isBaselineCorrectionEnabled() {
		return baselineCorrectionEnabled;
	}

	public void setBaselineCorrectionEnabled(boolean baselineCorrectionEnabled) {
		this.baselineCorrectionEnabled = baselineCorrectionEnabled;
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
