/* BoundedSignalSelection.java created 2007-10-04
 *
 */

package org.signalml.domain.signal;

/** BoundedSignalSelection
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BoundedSignalSelection {

	private static final long serialVersionUID = 1L;

	private int maxPage;
	private int maxBlock;
	private float maxTime;

	private float pageSize;
	private int blocksPerPage;

	private float samplingFrequency;

	private String[] channels;

	private SignalSelection selection;

	public BoundedSignalSelection() {
		super();
	}

	public BoundedSignalSelection(SignalSelection selection) {
		super();
		this.selection = selection;
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

	public float getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(float maxTime) {
		this.maxTime = maxTime;
	}

	public int getBlocksPerPage() {
		return blocksPerPage;
	}

	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	public String[] getChannels() {
		return channels;
	}

	public void setChannels(String[] channels) {
		this.channels = channels;
	}

	public float getPageSize() {
		return pageSize;
	}

	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}

	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public SignalSelection getSelection() {
		return selection;
	}

	public void setSelection(SignalSelection selection) {
		this.selection = selection;
	}

}
