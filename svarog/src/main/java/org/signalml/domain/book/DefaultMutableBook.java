/* DefaultMutableBook.java created 2008-02-24
 *
 */

package org.signalml.domain.book;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import org.signalml.util.Util;

/** DefaultMutableBook
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultMutableBook extends AbstractMutableBook implements MutableBook {

	private int channelCount;

	private String bookComment;

	private float energyPercent;
	private int maxIterationCount;
	private int dictionarySize;
	private char dictionaryType;
	private float samplingFrequency;
	private float calibration;
	private int signalChannelCount;
	private String textInfo;
	private String webSiteInfo;
	private String date;

	private String[] channelLabels;

	private ArrayList<MutableBookSegment[]> segments;

	public DefaultMutableBook(int channelCount, float samplingFrequency) {
		super();

		this.samplingFrequency = samplingFrequency;

		segments = new ArrayList<MutableBookSegment[]>();
		this.channelCount = channelCount;
		this.channelLabels = new String[channelCount];
		for (int i=0; i<channelCount; i++) {
			channelLabels[i] = "L" + (i+1);
		}
	}

	@Override
	public MutableBookSegment[] addNewSegment(float segmentTime, int segmentLength) {

		int segmentIndex = segments.size();

		MutableBookSegment[] newSegments = new MutableBookSegment[channelCount];
		for (int i=0; i<channelCount; i++) {
			newSegments[i] = new DefaultMutableBookSegment(samplingFrequency, i, segmentIndex, segmentTime, segmentLength);
		}
		segments.add(newSegments);

		return newSegments;

	}

	@Override
	public int addSegment(StandardBookSegment[] segments) throws IllegalArgumentException {

		if (segments.length != channelCount) {
			throw new IllegalArgumentException("Bad array length [" + segments.length + "] expected [" + channelCount + "]");
		}

		int segmentIndex = this.segments.size();

		MutableBookSegment[] newSegments = new MutableBookSegment[channelCount];
		for (int i=0; i<channelCount; i++) {
			newSegments[i] = new DefaultMutableBookSegment(segments[i]);
		}

		this.segments.add(newSegments);

		return segmentIndex;

	}

	@Override
	public void clear() {
		segments.clear();
		fireBookStructureChanged();
	}

	@Override
	public StandardBookSegment[] removeSegmentAt(int segmentIndex) {
		MutableBookSegment[] removed = segments.remove(segmentIndex);
		fireSegmentRemoved(segmentIndex);
		return removed;
	}

	@Override
	public void setBookComment(String comment) {
		if (!Util.equalsWithNulls(this.bookComment, comment)) {
			String oldComment = this.bookComment;
			this.bookComment = comment;
			firePropertyChange(BOOK_COMMENT_PROPERTY, oldComment, comment);
		}
	}

	@Override
	public void setCalibration(float calibration) {
		if (this.calibration != calibration) {
			float oldCalibration = this.calibration;
			this.calibration = calibration;
			firePropertyChange(CALIBRATION_PROPERTY, oldCalibration, calibration);
		}

	}

	@Override
	public void setChannelLabel(int channelIndex, String label) {
		if (label == null) {
			throw new NullPointerException("Null label not allowed");
		}
		if (!channelLabels[channelIndex].equals(label)) {
			String oldLabel = channelLabels[channelIndex];
			channelLabels[channelIndex] = label;
			fireIndexedPropertyChange(CHANNEL_LABEL_PROPERTY, channelIndex, oldLabel, label);
		}
	}

	@Override
	public void setDate(String date) {
		if (!Util.equalsWithNulls(this.date, date)) {
			String oldDate = this.date;
			this.date = date;
			firePropertyChange(DATE_PROPERTY, oldDate, date);
		}
	}

	@Override
	public void setDictionarySize(int dictionarySize) {
		if (this.dictionarySize != dictionarySize) {
			int  oldDictionarySize = this.dictionarySize;
			this.dictionarySize = dictionarySize;
			firePropertyChange(DICTIONARY_SIZE_PROPERTY, oldDictionarySize, dictionarySize);
		}
	}

	@Override
	public void setDictionaryType(char dictionaryType) {
		if (this.dictionaryType != dictionaryType) {
			char oldDictionaryType = this.dictionaryType;
			this.dictionaryType = dictionaryType;
			firePropertyChange(DICTIONARY_TYPE_PROPERTY, oldDictionaryType, dictionaryType);
		}
	}

	@Override
	public void setEnergyPercent(float energyPercent) {
		if (this.energyPercent != energyPercent) {
			float oldEnergyPercent = this.energyPercent;
			this.energyPercent = energyPercent;
			firePropertyChange(ENERGY_PERCENT_PROPERTY, oldEnergyPercent, energyPercent);
		}
	}

	@Override
	public void setMaxIterationCount(int maxIterationCount) {
		if (this.maxIterationCount != maxIterationCount) {
			int oldMaxIterationCount = this.maxIterationCount;
			this.maxIterationCount = maxIterationCount;
			firePropertyChange(MAX_ITERATION_COUNT_PROPERTY, oldMaxIterationCount, maxIterationCount);
		}
	}

	@Override
	public void setSamplingFrequency(float samplingFrequency) {
		if (this.samplingFrequency != samplingFrequency) {
			float oldSamplingFrequency = this.samplingFrequency;
			this.samplingFrequency = samplingFrequency;
			firePropertyChange(SAMPLING_FREQUENCY_PROPERTY, oldSamplingFrequency, samplingFrequency);
		}
	}

	private void setSegmentAtInternal(int segmentIndex, int channelIndex, StandardBookSegment segment) {
		MutableBookSegment current = segments.get(segmentIndex)[channelIndex];
		current.setDecompositionEnergy(segment.getDecompositionEnergy());
		current.setSignalEnergy(segment.getSignalEnergy());
		current.setSignalSamples(segment.getSignalSamples());
		current.clear();
		int atomCount = segment.getAtomCount();
		for (int i=0; i<atomCount; i++) {
			current.addAtom(segment.getAtomAt(i));
		}
	}

	@Override
	public void setSegmentAt(int segmentIndex, int channelIndex, StandardBookSegment segment) {
		setSegmentAtInternal(segmentIndex, channelIndex, segment);
		fireSegmentChanged(segmentIndex);
		fireSegmentAtomsChanged(channelIndex, segmentIndex);
	}

	@Override
	public void setSegmentAt(int segmentIndex, StandardBookSegment[] segments) {
		int count = Math.min(channelCount, segments.length);
		int i;
		for (i=0; i<count; i++) {
			setSegmentAtInternal(segmentIndex, i, segments[i]);
		}
		fireSegmentChanged(segmentIndex);
		for (i=0; i<count; i++) {
			fireSegmentAtomsChanged(i, segmentIndex);
		}
	}

	@Override
	public void setSignalChannelCount(int signalChannelCount) {
		if (this.signalChannelCount != signalChannelCount) {
			int oldSignalChannelCount = this.signalChannelCount;
			this.signalChannelCount = signalChannelCount;
			firePropertyChange(SIGNAL_CHANNEL_COUNT_PROPERTY, oldSignalChannelCount, signalChannelCount);
		}
	}

	@Override
	public void setTextInfo(String textInfo) {
		if (!Util.equalsWithNulls(this.textInfo, textInfo)) {
			String oldTextInfo = this.textInfo;
			this.textInfo = textInfo;
			firePropertyChange(TEXT_INFO_PROPERTY, oldTextInfo, textInfo);
		}
	}

	@Override
	public void setWebSiteInfo(String webSiteInfo) {
		if (!Util.equalsWithNulls(this.webSiteInfo, webSiteInfo)) {
			String oldWebSiteInfo = this.webSiteInfo;
			this.webSiteInfo = webSiteInfo;
			firePropertyChange(WEB_SITE_INFO_PROPERTY, oldWebSiteInfo, webSiteInfo);
		}
	}

	@Override
	public void close() {
		// nothing to do
	}

	@Override
	public String getBookComment() {
		return bookComment;
	}

	@Override
	public float getCalibration() {
		return calibration;
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public String getChannelLabel(int channelIndex) {
		return channelLabels[channelIndex];
	}

	@Override
	public String getDate() {
		return date;
	}

	@Override
	public int getDictionarySize() {
		return dictionarySize;
	}

	@Override
	public char getDictionaryType() {
		return dictionaryType;
	}

	@Override
	public float getEnergyPercent() {
		return energyPercent;
	}

	@Override
	public int getMaxIterationCount() {
		return maxIterationCount;
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("No properties");
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		Vector<String> names = new Vector<String>();
		return names.elements();
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	@Override
	public StandardBookSegment getSegmentAt(int segmentIndex, int channelIndex) {
		return segments.get(segmentIndex)[channelIndex];
	}

	@Override
	public StandardBookSegment[] getSegmentAt(int segmentIndex) {
		return segments.get(segmentIndex);
	}

	@Override
	public int getSegmentCount() {
		return segments.size();
	}

	@Override
	public int getSignalChannelCount() {
		return signalChannelCount;
	}

	@Override
	public String getTextInfo() {
		return textInfo;
	}

	@Override
	public String getVersion() {
		return "MPv5";
	}

	@Override
	public String getWebSiteInfo() {
		return webSiteInfo;
	}

}
