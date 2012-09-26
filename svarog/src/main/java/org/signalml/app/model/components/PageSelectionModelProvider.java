/* PageSelectionModelProvider.java created 2007-10-04
 *
 */

package org.signalml.app.model.components;

import javax.swing.AbstractSpinnerModel;

/** PageSelectionModelProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PageSelectionModelProvider {

	private int maxPage;
	private int currentPage;
	private int currentLength;

	private StartPageSpinnerModel startPageSpinnerModel;
	private LengthSpinnerModel lengthSpinnerModel;

	public PageSelectionModelProvider(int maxPage, int currentPage, int currentLength) {
		this.maxPage = maxPage;
		this.currentPage = currentPage;
		this.currentLength = currentLength;

		startPageSpinnerModel = new StartPageSpinnerModel();
		lengthSpinnerModel = new LengthSpinnerModel();

	}

	public int getMaxPage() {
		return maxPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		startPageSpinnerModel.setValue(currentPage);
	}

	public int getCurrentLength() {
		return currentLength;
	}

	public void setCurrentLength(int currentLength) {
		lengthSpinnerModel.setValue(currentLength);
	}

	public StartPageSpinnerModel getStartPageSpinnerModel() {
		return startPageSpinnerModel;
	}

	public LengthSpinnerModel getLengthSpinnerModel() {
		return lengthSpinnerModel;
	}

	private class StartPageSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

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

	private class LengthSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

		@Override
		public Object getNextValue() {
			return (currentPage + currentLength - 1 < maxPage ? new Integer(currentLength+1) : null);
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
			if (length < 1 || length > (maxPage+1-currentPage)) {
				throw new IllegalArgumentException();
			}
			if (length != currentLength) {
				currentLength = length;
				fireStateChanged();
			}
		}

		public void update() {
			if (currentLength > (maxPage+1-currentPage)) {
				setValue(new Integer(maxPage+1-currentPage));
			}
			fireStateChanged();
		}

		@Override
		public Comparable<? extends Number> getMaximum() {
			return new Integer(maxPage+1-currentPage);
		}

		@Override
		public Comparable<? extends Number> getMinimum() {
			return new Integer(1);
		}

	}

}
