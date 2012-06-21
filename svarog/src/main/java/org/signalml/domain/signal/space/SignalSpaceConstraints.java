/* SignalSpaceConstraints.java created 2008-01-25
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * This class represents different parameters of
 * {@link MultichannelSampleSource sampled signal}
 * (length, size of pages, blocks, frequency of sampling, labels of channels
 * and so on).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSpaceConstraints {

	/**
	 * the number of samples in the signal
	 */
	private int signalLength;
	/**
	 * the length (in seconds) of the signals
	 */
	private float timeSignalLength;

	/**
	 * the index of the last complete (whole) page in the signal
	 */
	private int maxWholePage;

	/**
	 * the length of a page (in seconds)
	 */
	private float pageSize;
	/**
	 * the number of blocks per page
	 */
	private int blocksPerPage;

	/**
	 * the length of a block (in seconds)
	 */
	private float blockSize;

	/**
	 * the index of the last page in the signal
	 */
	private int maxPage;
	/**
	 * the index of the last block in the signal
	 */
	private int maxBlock;

	/**
	 * the number of samples per second
	 */
	private float samplingFrequency;

	/**
	 * an array of labels of the {@link SourceChannel source channels}
	 */
	private String[] sourceChannels;
	/**
	 * an array of labels of the {@link MontageChannel montage channels}
	 */
	private String[] channels;

	/**
	 * the possible {@link TagStyle styles} of the marker
	 */
	private TagStyle[] markerStyles;

	/**
	 * the {@link TagIconProducer producer} of tag icons
	 */
	private TagIconProducer tagIconProducer;

	/**
	 * true if only complete (whole) pages can be used, false otherwise
	 */
	private boolean requireCompletePages;

	/**
	 * Returns the number of samples in the signal.
	 * @return the number of samples in the signal
	 */
	public int getSignalLength() {
		return signalLength;
	}

	/**
	 * Sets the number of samples in the signal.
	 * @param signalLength the number of samples in the signal
	 */
	public void setSignalLength(int signalLength) {
		this.signalLength = signalLength;
	}

	/**
	 * Returns the length (in seconds) of the signals.
	 * @return the length (in seconds) of the signals
	 */
	public float getTimeSignalLength() {
		return timeSignalLength;
	}

	/**
	 * Sets the length (in seconds) of the signals.
	 * @param timeSignalLength the length (in seconds) of the signals
	 */
	public void setTimeSignalLength(float timeSignalLength) {
		this.timeSignalLength = timeSignalLength;
	}

	/**
	 * Returns the index of the last complete (whole) page in the signal.
	 * @return the index of the last complete (whole) page in the signal
	 */
	public int getMaxWholePage() {
		return maxWholePage;
	}

	/**
	 * Sets the index of the last complete (whole) page in the signal
	 * @param maxWholePage the index of the last complete (whole) page
	 * in the signal
	 */
	public void setMaxWholePage(int maxWholePage) {
		this.maxWholePage = maxWholePage;
	}

	/**
	 * Returns the index of the last page in the signal.
	 * @return the index of the last page in the signal
	 */
	public int getMaxPage() {
		return maxPage;
	}

	/**
	 * Sets the index of the last page in the signal.
	 * @param maxPage the index of the last page in the signal
	 */
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	/**
	 * Returns the index of the last block in the signal
	 * @return the index of the last block in the signal
	 */
	public int getMaxBlock() {
		return maxBlock;
	}

	/**
	 * Sets the index of the last block in the signal
	 * @param maxBlock the index of the last block in the signal
	 */
	public void setMaxBlock(int maxBlock) {
		this.maxBlock = maxBlock;
	}

	/**
	 * Returns the length of a page (in seconds).
	 * @return the length of a page (in seconds)
	 */
	public float getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the length of a page.
	 * @param pageSize the length of a page (in seconds)
	 */
	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns the number of blocks per page.
	 * @return the number of blocks per page
	 */
	public int getBlocksPerPage() {
		return blocksPerPage;
	}

	/**
	 * Sets the number of blocks per page.
	 * @param blocksPerPage the number of blocks per page
	 */
	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	/**
	 * Returns the length of a block (in seconds).
	 * @return the length of a block (in seconds)
	 */
	public float getBlockSize() {
		return blockSize;
	}

	/**
	 * Sets the length of a block.
	 * @param blockSize the length of a block (in seconds)
	 */
	public void setBlockSize(float blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * Returns the number of samples per second.
	 * @return the number of samples per second
	 */
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Sets the number of samples per second.
	 * @param samplingFrequency the number of samples per second
	 */
	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	/**
	 * Returns an array of labels of the
	 * {@link SourceChannel source channels}.
	 * @return an array of labels of the source channels
	 */
	public String[] getSourceChannels() {
		return sourceChannels;
	}

	/**
	 * Sets an array of labels of the {@link SourceChannel source channels}.
	 * @param sourceChannels an array of labels of the source channels
	 */
	public void setSourceChannels(String[] sourceChannels) {
		this.sourceChannels = sourceChannels;
	}

	/**
	 * Returns an array of labels of the
	 * {@link MontageChannel montage channels}.
	 * @return an array of labels of the montage channels
	 */
	public String[] getChannels() {
		return channels;
	}

	/**
	 * Sets an array of labels of the
	 * {@link MontageChannel montage channels}.
	 * @param channels an array of labels of the montage channels
	 */
	public void setChannels(String[] channels) {
		this.channels = channels;
	}

	/**
	 * Returns the possible {@link TagStyle styles} of the marker.
	 * @return the possible styles of the marker
	 */
	public TagStyle[] getMarkerStyles() {
		return markerStyles;
	}

	/**
	 * Sets the possible {@link TagStyle styles} of the marker.
	 * @param markerStyles the possible styles of the marker
	 */
	public void setMarkerStyles(TagStyle[] markerStyles) {
		this.markerStyles = markerStyles;
	}

	/**
	 * Returns the {@link TagIconProducer producer} of tag icons.
	 * @return the producer of tag icons
	 */
	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	/**
	 * Sets the {@link TagIconProducer producer} of tag icons.
	 * @param tagIconProducer the producer of tag icons
	 */
	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
	}

	/**
	 * Returns if only complete (whole) pages can be used.
	 * @return true if only complete (whole) pages can be used,
	 * false otherwise
	 */
	public boolean isRequireCompletePages() {
		return requireCompletePages;
	}

	/**
	 * Sets if only complete (whole) pages can be used.
	 * @param requireCompletePages true if only complete (whole) pages can
	 * be used, false otherwise
	 */
	public void setRequireCompletePages(boolean requireCompletePages) {
		this.requireCompletePages = requireCompletePages;
	}

}
