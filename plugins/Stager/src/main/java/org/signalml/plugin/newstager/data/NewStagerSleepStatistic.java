/* NewStagerSleepStatistic.java created 2008-02-21
 *
 */

package org.signalml.plugin.newstager.data;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.signalml.domain.tag.SleepTagName;
import org.signalml.domain.tag.TagStatistic;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;

import static org.signalml.util.FormatUtils.getPrettyTimeString;

/**
 * NewStagerSleepStatistic
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerSleepStatistic extends TagStatistic implements
	PropertyProvider {

	protected static final Logger logger = Logger
										   .getLogger(NewStagerSleepStatistic.class);

	protected int segmentCount;
	protected float segmentLength;

	protected int[] styleSegments;

	protected double firstSleepTime;
	protected double lastSleepTime;
	protected double firstSlowWaveTime;
	protected double firstREMTime;
	protected double wakeInsidePropperSleepTime;

	protected float deltaThr;
	protected float alphaThr;
	protected float spindleThr;
	protected float emgTone;

	protected int slowSegments;

	public NewStagerSleepStatistic(NewStagerResult stagerResult,
								   ExportedTagDocument tagDocument, int segmentCount, float segmentLength) {
		super();

		deltaThr = (float) stagerResult.getDeltaThr();
		alphaThr = (float) stagerResult.getAlphaThr();
		spindleThr = (float) stagerResult.getSpindleThr();
		emgTone = (float) stagerResult.getEmgTone();

		this.segmentCount = segmentCount;
		this.segmentLength = segmentLength;

		Set<ExportedTagStyle> pageStyleList = tagDocument.getTagStyles();
		ExportedTagStyle[] styles = new ExportedTagStyle[pageStyleList.size()];
		pageStyleList.toArray(styles);

		init(styles, segmentLength * segmentCount);

		styleSegments = new int[styles.length + 1];

		firstSleepTime = -1;
		firstREMTime = -1;
		firstSlowWaveTime = -1;
		lastSleepTime = -1;

		ExportedTagStyle style;
		String name;
		int index;
		Integer idx;
		double position;
		double endPosition;
		double length;
		float wakeInsidePropperSleepTimeCandidate = 0;
		for (ExportedTag tag : tagDocument.getSetOfTags()) {

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
			styleSegments[index + 1]++;

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
			} else if (SleepTagName.isWake(name)) {
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

	public double getSegmentLength() {
		return segmentLength;
	}

	public double getFirstSleepTime() {
		return firstSleepTime;
	}

	public double getLastSleepTime() {
		return lastSleepTime;
	}

	public double getFirstSlowWaveTime() {
		return firstSlowWaveTime;
	}

	public double getFirstREMTime() {
		return firstREMTime;
	}

	public double getSleepPeriodTime() {
		if (firstSleepTime >= 0 && lastSleepTime >= 0) {
			return (lastSleepTime - firstSleepTime);
		} else {
			return 0;
		}
	}

	public String getSleepPeriodTimePretty() {
		return getPrettyTimeString(getSleepPeriodTime());
	}

	public double getTotalSleepTime() {
		if (firstSleepTime >= 0 && lastSleepTime >= 0) {
			return (lastSleepTime - firstSleepTime)
				   - wakeInsidePropperSleepTime;
		} else {
			return 0;
		}
	}

	public String getTotalSleepTimePretty() {
		return getPrettyTimeString(getTotalSleepTime());
	}

	public double getSleepEfficiencyIndex() {
		return (getTotalSleepTime() / getTotalLength()) * 100;
	}

	public Double getSleepEfficiencyIndexPretty() {
		return (((double) Math.round(getSleepEfficiencyIndex() * 100)) / 100);
	}

	public double getSleepOnsetLatency() {
		return firstSleepTime;
	}

	public String getSleepOnsetLatencyPretty() {
		if (firstSleepTime < 0) {
			return "-";
		} else {
			return getPrettyTimeString(firstSleepTime);
		}
	}

	public double getSleepOnsetToSWS() {
		if (firstSlowWaveTime < 0) {
			return -1;
		} else {
			return (firstSlowWaveTime - firstSleepTime);
		}
	}

	public String getSleepOnsetToSWSPretty() {
		double value = getSleepOnsetToSWS();
		if (value < 0) {
			return "-";
		} else {
			return getPrettyTimeString(value);
		}
	}

	public double getSleepOnsetToREM() {
		if (firstREMTime < 0) {
			return -1;
		} else {
			return (firstREMTime - firstSleepTime);
		}
	}

	public String getSleepOnsetToREMPretty() {
		double value = getSleepOnsetToREM();
		if (value < 0) {
			return "-";
		} else {
			return getPrettyTimeString(value);
		}
	}

	public double getWakeInsidePropperSleepTime() {
		return wakeInsidePropperSleepTime;
	}

	public String getWakeInsidePropperSleepTimePretty() {
		return getPrettyTimeString(wakeInsidePropperSleepTime);
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
		return styleSegments[index + 1];
	}

	public String getDeltaThrPretty() {
		return new String("" + deltaThr);
	}

	public String getAlphaThrPretty() {
		return new String("" + alphaThr);
	}

	public String getSpindleThrPretty() {
		return new String("" + spindleThr);
	}

	public String getEmgTonePretty() {
		return new String("" + emgTone);
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList()
	throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.eegRecordingTime",
					 "totalLengthPretty", NewStagerSleepStatistic.class,
					 "getTotalLengthPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.sleepPeriodTime",
					 "sleepPeriodTimePretty", NewStagerSleepStatistic.class,
					 "getSleepPeriodTimePretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.totalSleepTime",
					 "totalSleepTimePretty", NewStagerSleepStatistic.class,
					 "getTotalSleepTimePretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.sleepEfficiencyIndex",
					 "sleepEfficiencyIndexPretty", NewStagerSleepStatistic.class,
					 "getSleepEfficiencyIndexPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.sleepOnsetLatency",
					 "sleepOnsetLatencyPretty", NewStagerSleepStatistic.class,
					 "getSleepOnsetLatencyPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.sleepOnsetToSWS",
					 "sleepOnsetToSWSPretty", NewStagerSleepStatistic.class,
					 "getSleepOnsetToSWSPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.sleepOnsetToREM",
					 "sleepOnsetToREMPretty", NewStagerSleepStatistic.class,
					 "getSleepOnsetToREMPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.wakePeriodsAfterSleepOnset",
					 "wakeInsidePropperSleepTimePretty",
					 NewStagerSleepStatistic.class,
					 "getWakeInsidePropperSleepTimePretty", null));

		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.deltaThr", "deltaThrPretty",
					 NewStagerSleepStatistic.class, "deltaThrPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.alphaThr", "alphaThrPretty",
					 NewStagerSleepStatistic.class, "alphaThrPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.spindleThr", "spindleThrPretty",
					 NewStagerSleepStatistic.class, "spindleThrPretty", null));
		list.add(new LabelledPropertyDescriptor(
					 "property.sleepStatistic.emgTone", "emgTonePretty",
					 NewStagerSleepStatistic.class, "emgTonePretty", null));

		return list;

	}

}
