/* BoundedSignalSelection.java created 2007-10-04
 *
 */

package org.signalml.domain.signal;

import org.signalml.plugin.export.signal.SignalSelection;

/**
 * This class represents a selected part of a signal that is limited by the
 * given parameters (maximum page, block and point in time).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BoundedSignalSelection {

	private static final long serialVersionUID = 1L;

	/**
	 * the number of the last page that can be used in the selection
	 */
	private int maxPage;

	/**
	 * the number of the last block that can be used in the selection
	 */
	private int maxBlock;

	/**
	 * the last point in time that can be used in the selection
	 */
	private float maxTime;

	/**
	 * the length (in seconds) of a page of the signal
	 */
	private float pageSize;

	/**
	 * the number of blocks per page of the signal
	 */
	private int blocksPerPage;

	/**
	 * the number of samples per second
	 */
	private float samplingFrequency;

	/**
	 * an array of names of signal channels
	 */
	private String[] channels;

	/**
	 * the selection associated with this bounded part of a signal
	 */
	private SignalSelection selection;

	/**
	 * Constructor. Creates an empty bounded signal selection
	 */
	public BoundedSignalSelection() {
		super();
	}

	/**
	 * Constructor. Creates a bounded signal selection with a given
	 * {@link SignalSelection selection}
	 * @param selection the actual selection that is added to the created
	 * object
	 */
	public BoundedSignalSelection(SignalSelection selection) {
		super();
		this.selection = selection;
	}

	/**
	 * Returns the number of the last page that can be used in the selection.
	 * @return the number of the last page that can be used in the selection
	 */
	public int getMaxPage() {
		return maxPage;
	}

	/**
	 * Sets the number of the last page that can be used in the selection.
	 * @param maxPage the number of the last page that can be used in
	 * the selection.
	 */
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	/**
	 * Returns the number of the last block that can be used in the
	 * selection.
	 * @return the number of the last block that can be used in the
	 * selection
	 */
	public int getMaxBlock() {
		return maxBlock;
	}

	/**
	 * Sets the number of the last block that can be used in the
	 * selection.
	 * @param maxBlock the number of the last block that can be used in the
	 * selection
	 */
	public void setMaxBlock(int maxBlock) {
		this.maxBlock = maxBlock;
	}

	/**
	 * Returns the last point in time that can be used in the selection.
	 * @return the last point in time that can be used in the selection
	 */
	public float getMaxTime() {
		return maxTime;
	}

	/**
	 * Sets the last point in time that can be used in the selection.
	 * @param maxTime the last point in time that can be used in the
	 * selection
	 */
	public void setMaxTime(float maxTime) {
		this.maxTime = maxTime;
	}

	/**
	 * Returns the number of blocks per page of the signal.
	 * @return the number of blocks per page of the signal
	 */
	public int getBlocksPerPage() {
		return blocksPerPage;
	}

	/**
	 * Sets the number of blocks per page of the signal.
	 * @param blocksPerPage the number of blocks per page of the signal
	 */
	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	/**
	 * Returns the labels of signal channels.
	 * @return an array of strings - the names of signal channels
	 */
	public String[] getChannels() {
		return channels;
	}

	/**
	 * Sets the labels of signal channels.
	 * @param channels an array of strings - the names of signal channels
	 */
	public void setChannels(String[] channels) {
		this.channels = channels;
	}

	/**
	 * Returns the length (in seconds) of a page of the signal.
	 * @return the length (in seconds) of a page of the signal
	 */
	public float getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the length (in seconds) of a page of the signal.
	 * @param pageSize the length (in seconds) of a page of the signal
	 */
	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
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
	 * Returns the actual {@link SignalSelection selection} associated with
	 * this bounded selection.
	 * @return the actual selection associated with this bounded selection
	 */
	public SignalSelection getSelection() {
		return selection;
	}

	/**
	 * Sets the actual {@link SignalSelection selection} associated with
	 * this bounded selection.
	 * @param selection the actual selection associated with this bounded
	 * selection
	 */
	public void setSelection(SignalSelection selection) {
		this.selection = selection;
	}

}
