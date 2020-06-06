/* EvokedPotentialParameters.java created 2008-01-12
 *
 */

package org.signalml.method.ep;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.FilterType;
import org.springframework.validation.Errors;

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
	private float averagingStartTime = 0.0F;
	private float averagingTimeLength = 1.0F;

	private boolean baselineCorrectionEnabled = true;
	private float baselineStartTime = -0.2F;
	private float baselineTimeLength = 0.2F;

	private boolean filteringEnabled = true;
	private List<TagStyleGroup> artifactTagStyles = new ArrayList<TagStyleGroup>();
	private TimeDomainSampleFilter timeDomainSampleFilter;

	private SignalSpace wholeSignalSpace;

	public EvokedPotentialParameters() {
		wholeSignalSpace = new SignalSpace();

		timeDomainSampleFilter = new TimeDomainSampleFilter(FilterType.BANDPASS, ApproximationFunctionType.BUTTERWORTH, new double[] { 1.0, 20.0 }, new double[] { 0.1, 50 }, 3.0, 20.0);
		timeDomainSampleFilter.setDescription(_("After ERP averaging filter"));
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

	public float getAveragingStartTime() {
		return averagingStartTime;
	}

	public void setAveragingStartTime(float averagingStartTime) {
		this.averagingStartTime = averagingStartTime;
	}

	public float getAveragingTimeLength() {
		return averagingTimeLength;
	}

	public void setAveragingTimeLength(float averagingTimeLength) {
		this.averagingTimeLength = averagingTimeLength;
	}

	public float getBaselineTimeStart() {
		return baselineStartTime;
	}

	public void setBaselineTimeStart(float baselineTimeStart) {
		this.baselineStartTime = baselineTimeStart;
	}

	public float getBaselineTimeLength() {
		return baselineTimeLength;
	}

	public void setBaselineTimeLength(float baselineTimeLength) {
		this.baselineTimeLength = baselineTimeLength;
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

	public TimeDomainSampleFilter getTimeDomainSampleFilter() {
		return timeDomainSampleFilter;
	}

	public void setTimeDomainSampleFilter(TimeDomainSampleFilter timeDomainSampleFilter) {
		this.timeDomainSampleFilter = timeDomainSampleFilter;
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
