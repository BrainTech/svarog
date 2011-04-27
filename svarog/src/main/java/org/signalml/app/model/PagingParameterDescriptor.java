/* PagingParameterDescriptor.java created 2007-11-16
 *
 */

package org.signalml.app.model;

/** PagingParameterDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PagingParameterDescriptor {

	public static final float DEFAULT_PAGE_SIZE = 20F;
	public static final int DEFAULT_BLOCKS_PER_PAGE = 5;

	private Float pageSize = DEFAULT_PAGE_SIZE;
	private Integer blocksPerPage = DEFAULT_BLOCKS_PER_PAGE;

	private boolean pageSizeEditable = true;
	private boolean blocksPerPageEditable = true;

	public Float getPageSize() {
		return pageSize;
	}

	public void setPageSize(Float pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getBlocksPerPage() {
		return blocksPerPage;
	}

	public void setBlocksPerPage(Integer blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	public boolean isPageSizeEditable() {
		return pageSizeEditable;
	}

	public void setPageSizeEditable(boolean pageSizeEnabled) {
		this.pageSizeEditable = pageSizeEnabled;
	}

	public boolean isBlocksPerPageEditable() {
		return blocksPerPageEditable;
	}

	public void setBlocksPerPageEditable(boolean blocksPerPageEnabled) {
		this.blocksPerPageEditable = blocksPerPageEnabled;
	}

}
