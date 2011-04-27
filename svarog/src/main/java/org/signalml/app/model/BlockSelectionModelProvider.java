/* BlockSelectionModelProvider.java created 2007-10-04
 *
 */

package org.signalml.app.model;

import javax.swing.AbstractSpinnerModel;

/** BlockSelectionModelProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BlockSelectionModelProvider {

	protected int maxPage;
	protected int maxBlock;
	protected int blocksPerPage;
	protected int currentPage;
	protected int currentBlock;
	protected int currentLength;

	private StartPageSpinnerModel startPageSpinnerModel;
	protected StartBlockSpinnerModel startBlockSpinnerModel;
	protected LengthSpinnerModel lengthSpinnerModel;

	public BlockSelectionModelProvider(int maxPage, int maxBlock, int blocksPerPage, int currentPage, int currentBlock, int currentLength) {
		this.maxPage = maxPage;
		this.maxBlock = maxBlock;
		this.blocksPerPage = blocksPerPage;
		this.currentPage = currentPage;
		this.currentBlock = currentBlock;
		this.currentLength = currentLength;

		startPageSpinnerModel = new StartPageSpinnerModel();
		startBlockSpinnerModel = new StartBlockSpinnerModel();
		lengthSpinnerModel = new LengthSpinnerModel();
	}

	public int getMaxPage() {
		return maxPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getCurrentLength() {
		return currentLength;
	}

	public StartPageSpinnerModel getStartPageSpinnerModel() {
		return startPageSpinnerModel;
	}

	public StartBlockSpinnerModel getStartBlockSpinnerModel() {
		return startBlockSpinnerModel;
	}

	public LengthSpinnerModel getLengthSpinnerModel() {
		return lengthSpinnerModel;
	}

	protected class StartPageSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

		@Override
		public Object getNextValue() {
			return (currentPage < maxPage ? new Integer(currentPage+1) : null);
		}

		@Override
		public Object getPreviousValue() {
			return (currentPage > 1 ? new Integer(currentPage-1) : null);
		}

		@Override
		public Object getValue() {
			return new Integer(currentPage);
		}

		@Override
		public void setValue(Object value) throws IllegalArgumentException {
			int page = ((Integer) value).intValue();
			if (page < 1 || page > maxPage) {
				throw new IllegalArgumentException();
			}
			if (page != currentPage) {
				currentPage = page;
				fireStateChanged();
				startBlockSpinnerModel.update();
				lengthSpinnerModel.update();
			}
		}

		@Override
		public Comparable<? extends Number> getMaximum() {
			return new Integer(maxPage);
		}

		@Override
		public Comparable<? extends Number> getMinimum() {
			return new Integer(1);
		}

	}

	protected class StartBlockSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

		private int getMaxBlock() {
			if (currentPage == maxPage) {
				return (maxBlock - ((currentPage-1) * blocksPerPage));
			} else {
				return blocksPerPage;
			}
		}

		@Override
		public Object getNextValue() {
			int max = getMaxBlock();
			return (currentBlock < max ? new Integer(currentBlock+1) : null);
		}

		@Override
		public Object getPreviousValue() {
			return (currentBlock > 1 ? new Integer(currentBlock-1) : null);
		}

		@Override
		public Object getValue() {
			return new Integer(currentBlock);
		}

		@Override
		public void setValue(Object value) throws IllegalArgumentException {
			int max = getMaxBlock();
			int block = ((Integer) value).intValue();
			if (block < 1 || block > max) {
				throw new IllegalArgumentException();
			}
			if (block != currentBlock) {
				currentBlock = block;
				fireStateChanged();
				lengthSpinnerModel.update();
			}
		}

		public void update() {
			int max = getMaxBlock();
			if (currentBlock > max) {
				setValue(max);
			}
			fireStateChanged();
		}

		@Override
		public Comparable<? extends Number> getMaximum() {
			return new Integer(getMaxBlock());
		}

		@Override
		public Comparable<? extends Number> getMinimum() {
			return new Integer(1);
		}

	}

	protected class LengthSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

		private int getMaxLength() {
			return (maxBlock - (((currentPage-1)*blocksPerPage) + (currentBlock-1)));
		}

		@Override
		public Object getNextValue() {
			int currentEnd = ((currentPage-1)*blocksPerPage) + (currentBlock-1) + currentLength - 1;
			return (currentEnd < maxBlock ? new Integer(currentLength+1) : null);
		}

		@Override
		public Object getPreviousValue() {
			return (currentLength > 1 ? new Integer(currentLength-1) : null);
		}

		@Override
		public Object getValue() {
			return new Integer(currentLength);
		}

		@Override
		public void setValue(Object value) {
			int length = ((Integer) value).intValue();
			int maxLength = getMaxLength();
			if (length < 1 || length > maxLength) {
				throw new IllegalArgumentException();
			}
			if (length != currentLength) {
				currentLength = length;
				fireStateChanged();
			}
		}

		public void update() {
			int maxLength = getMaxLength();
			if (currentLength > maxLength) {
				setValue(new Integer(maxLength));
			}
			fireStateChanged();
		}

		@Override
		public Comparable<? extends Number> getMaximum() {
			return new Integer(getMaxLength());
		}

		@Override
		public Comparable<? extends Number> getMinimum() {
			return new Integer(1);
		}

	}

}
