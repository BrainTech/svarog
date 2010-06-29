/* SleepStatistic.java created 2008-02-21
 *
 */

package org.signalml.app.method.stager;

import java.beans.IntrospectionException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.PropertyProvider;
import org.signalml.domain.tag.SleepTagName;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStatistic;
import org.signalml.domain.tag.TagStyle;
import org.signalml.method.stager.StagerResult;
import org.signalml.util.Util;

/** SleepStatistic
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SleepStatistic extends TagStatistic implements PropertyProvider {

	protected static final Logger logger = Logger.getLogger(SleepStatistic.class);

	protected int segmentCount;
	protected float segmentLength;

	protected int[] styleSegments;

	protected float firstSleepTime;
	protected float lastSleepTime;
	protected float firstSlowWaveTime;
	protected float firstREMTime;
	protected float wakeInsidePropperSleepTime;

	protected float deltaThr;
	protected float alphaThr;
	protected float spindleThr;
	protected float emgTone;

	protected int slowSegments;

	public SleepStatistic(StagerResult stagerResult, TagDocument tagDocument, int segmentCount, float segmentLength) {
		super();


		deltaThr = (float) stagerResult.getDeltaThr();
		alphaThr = (float) stagerResult.getAlphaThr();
		spindleThr = (float) stagerResult.getSpindleThr();
		emgTone = (float) stagerResult.getEmgTone();

		this.segmentCount = segmentCount;
		this.segmentLength = segmentLength;

		StyledTagSet tagSet = tagDocument.getTagSet();
		LinkedHashSet<TagStyle> pageStyleList = tagSet.getPageStylesNoMarkers();
		TagStyle[] styles = new TagStyle[pageStyleList.size()];
		pageStyleList.toArray(styles);

		init(styles, segmentLength * segmentCount);

		styleSegments = new int[styles.length+1];

		firstSleepTime = -1;
		firstREMTime = -1;
		firstSlowWaveTime = -1;
		lastSleepTime = -1;

		SortedSet<Tag> tags = tagSet.getTags();
		TagStyle style;
		String name;
		int index;
		Integer idx;
		float position;
		float endPosition;
		float length;
		float wakeInsidePropperSleepTimeCandidate = 0;
		for (Tag tag : tags) {

			style = tag.getStyle();

			if (style.isMarker() || !style.getType().isPage()) {
				continue;
			}
			name = style.getName();
			idx = styleIndices.get(style);
			if (idx == null) {
				logger.warn("Tag with unknown style [" + name + "]");
				continue;
			}
			length = tag.getLength();
			if (length != segmentLength) {
				logger.warn("Tag with bad length [" + length + "]");
				continue;
			}

			index = idx.intValue();

			addStyleTime(index, segmentLength);
			styleSegments[index+1]++;

			position = tag.getPosition();
			endPosition = tag.getEndPosition();
			if (SleepTagName.isPropperSleep(name)) {
				if (firstSleepTime < 0) {
					firstSleepTime = position;
				}
			}

			if (SleepTagName.isAnySleep(name)) {
				lastSleepTime = endPosition;
				wakeInsidePropperSleepTime += wakeInsidePropperSleepTimeCandidate;
				wakeInsidePropperSleepTimeCandidate = 0;
			}
			else if (SleepTagName.isWake(name)) {
				if (firstSleepTime >= 0) {
					wakeInsidePropperSleepTimeCandidate += length;
				}
			}

			if (firstREMTime < 0) {
				if (SleepTagName.isREM(name)) {
					firstREMTime = position;
				}
			}

			if (SleepTagName.isSlowWave(name)) {
				if (firstSlowWaveTime < 0) {
					firstSlowWaveTime = position;
				}
				slowSegments++;
			}

		}

	}

	public int getSegmentCount() {
		return segmentCount;
	}

	public float getSegmentLength() {
		return segmentLength;
	}

	public float getFirstSleepTime() {
		return firstSleepTime;
	}

	public float getLastSleepTime() {
		return lastSleepTime;
	}

	public float getFirstSlowWaveTime() {
		return firstSlowWaveTime;
	}

	public float getFirstREMTime() {
		return firstREMTime;
	}

	public float getSleepPeriodTime() {
		if (firstSleepTime >= 0 && lastSleepTime >= 0) {
			return (lastSleepTime - firstSleepTime);
		} else {
			return 0;
		}
	}

	public String getSleepPeriodTimePretty() {
		return Util.getPrettyTimeString(getSleepPeriodTime());
	}

	public float getTotalSleepTime() {
		if (firstSleepTime >= 0 && lastSleepTime >= 0) {
			return (lastSleepTime - firstSleepTime) - wakeInsidePropperSleepTime;
		} else {
			return 0;
		}
	}

	public String getTotalSleepTimePretty() {
		return Util.getPrettyTimeString(getTotalSleepTime());
	}

	public float getSleepEfficiencyIndex() {
		return (getTotalSleepTime() / getTotalLength()) * 100;
	}

	public Double getSleepEfficiencyIndexPretty() {
		return (((double) Math.round(getSleepEfficiencyIndex() * 100)) / 100);
	}

	public float getSleepOnsetLatency() {
		return firstSleepTime;
	}

	public String getSleepOnsetLatencyPretty() {
		if (firstSleepTime < 0) {
			return "-";
		} else {
			return Util.getPrettyTimeString(firstSleepTime);
		}
	}

	public float getSleepOnsetToSWS() {
		if (firstSlowWaveTime < 0) {
			return -1;
		} else {
			return (firstSlowWaveTime - firstSleepTime);
		}
	}

	public String getSleepOnsetToSWSPretty() {
		float value = getSleepOnsetToSWS();
		if (value < 0) {
			return "-";
		} else {
			return Util.getPrettyTimeString(value);
		}
	}

	public float getSleepOnsetToREM() {
		if (firstREMTime < 0) {
			return -1;
		} else {
			return (firstREMTime - firstSleepTime);
		}
	}

	public String getSleepOnsetToREMPretty() {
		float value = getSleepOnsetToREM();
		if (value < 0) {
			return "-";
		} else {
			return Util.getPrettyTimeString(value);
		}
	}

	public float getWakeInsidePropperSleepTime() {
		return wakeInsidePropperSleepTime;
	}

	public String getWakeInsidePropperSleepTimePretty() {
		return Util.getPrettyTimeString(wakeInsidePropperSleepTime);
	}

	public float getDeltaThr() {
		return deltaThr;
	}

	public void setDeltaThr(float deltaThr) {
		this.deltaThr = deltaThr;
	}

	public float getAlphaThr() {
		return alphaThr;
	}

	public void setAlphaThr(float alphaThr) {
		this.alphaThr = alphaThr;
	}

	public float getSpindleThr() {
		return spindleThr;
	}

	public void setSpindleThr(float spindleThr) {
		this.spindleThr = spindleThr;
	}

	public float getEmgTone() {
		return emgTone;
	}

	public void setEmgTone(float emgTone) {
		this.emgTone = emgTone;
	}

	public int getSlowSegments() {
		return slowSegments;
	}

	public int getStyleSegmentsAt(int index) {
		return styleSegments[index+1];
	}

	public String getDeltaThrPretty() {
		return new String(""+deltaThr);
	}

	public String getAlphaThrPretty() {
		return new String(""+alphaThr);
	}

	public String getSpindleThrPretty() {
		return new String(""+spindleThr);
	}

	public String getEmgTonePretty() {
		return new String(""+emgTone);
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.eegRecordingTime", "totalLengthPretty", SleepStatistic.class, "getTotalLengthPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.sleepPeriodTime", "sleepPeriodTimePretty", SleepStatistic.class, "getSleepPeriodTimePretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.totalSleepTime", "totalSleepTimePretty", SleepStatistic.class, "getTotalSleepTimePretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.sleepEfficiencyIndex", "sleepEfficiencyIndexPretty", SleepStatistic.class, "getSleepEfficiencyIndexPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.sleepOnsetLatency", "sleepOnsetLatencyPretty", SleepStatistic.class, "getSleepOnsetLatencyPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.sleepOnsetToSWS", "sleepOnsetToSWSPretty", SleepStatistic.class, "getSleepOnsetToSWSPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.sleepOnsetToREM", "sleepOnsetToREMPretty", SleepStatistic.class, "getSleepOnsetToREMPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.wakePeriodsAfterSleepOnset", "wakeInsidePropperSleepTimePretty", SleepStatistic.class, "getWakeInsidePropperSleepTimePretty", null));

		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.deltaThr", "deltaThrPretty", SleepStatistic.class, "deltaThrPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.alphaThr", "alphaThrPretty", SleepStatistic.class, "alphaThrPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.spindleThr", "spindleThrPretty", SleepStatistic.class, "spindleThrPretty", null));
		list.add(new LabelledPropertyDescriptor("property.sleepStatistic.emgTone", "emgTonePretty", SleepStatistic.class, "emgTonePretty", null));

		return list;

	}

}
