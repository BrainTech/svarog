/* SignalSpaceConstraints.java created 2008-01-25
 * 
 */

package org.signalml.domain.signal.space;

import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.domain.tag.TagStyle;

/** SignalSpaceConstraints
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSpaceConstraints {

	private int signalLength;
	private float timeSignalLength;
	
	private int maxWholePage;
	
	private float pageSize;
	private int blocksPerPage;

	private float blockSize;

	private int maxPage;
	private int maxBlock;	
	
	private float samplingFrequency;
	
	private String[] sourceChannels;
	private String[] channels;

	private TagStyle[] markerStyles;
	
	private TagIconProducer tagIconProducer;
	
	private boolean requireCompletePages;
	
	public int getSignalLength() {
		return signalLength;
	}

	public void setSignalLength(int signalLength) {
		this.signalLength = signalLength;
	}

	public float getTimeSignalLength() {
		return timeSignalLength;
	}

	public void setTimeSignalLength(float timeSignalLength) {
		this.timeSignalLength = timeSignalLength;
	}

	public int getMaxWholePage() {
		return maxWholePage;
	}

	public void setMaxWholePage(int maxWholePage) {
		this.maxWholePage = maxWholePage;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public int getMaxBlock() {
		return maxBlock;
	}

	public void setMaxBlock(int maxBlock) {
		this.maxBlock = maxBlock;
	}

	public float getPageSize() {
		return pageSize;
	}

	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}

	public int getBlocksPerPage() {
		return blocksPerPage;
	}

	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}
	
	public float getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(float blockSize) {
		this.blockSize = blockSize;
	}

	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}
	
	public String[] getSourceChannels() {
		return sourceChannels;
	}

	public void setSourceChannels(String[] sourceChannels) {
		this.sourceChannels = sourceChannels;
	}

	public String[] getChannels() {
		return channels;
	}

	public void setChannels(String[] channels) {
		this.channels = channels;
	}

	public TagStyle[] getMarkerStyles() {
		return markerStyles;
	}

	public void setMarkerStyles(TagStyle[] markerStyles) {
		this.markerStyles = markerStyles;
	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
	}

	public boolean isRequireCompletePages() {
		return requireCompletePages;
	}

	public void setRequireCompletePages(boolean requireCompletePages) {
		this.requireCompletePages = requireCompletePages;
	}	
			
}
