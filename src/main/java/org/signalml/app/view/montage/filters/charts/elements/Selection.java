/* Selection.java created 2011-02-14
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

/**
 * This class represents a selection made on a chart.
 * @author Piotr Szachewicz
 */
class Selection {

	/**
	 * The beginning position of this selection (in pixels).
	 */
	private int startPosition;

	/**
	 * The ending position of this selection.
	 */
	private int endPosition;

	/**
	 * True if the selection should be visible.
	 */
	private boolean visible;

	/**
	 * True if the user is currently dragging his mouse and making
	 * a selection.
	 */
	private boolean dragging;

	public Selection() {
		visible = false;
	}

	/**
	 * Creates a new selection from point1 to point2.
	 * @param point1 beginning position of the selection
	 * @param point2 end position of the selection
	 */
	public Selection(int point1, int point2) {

		startPosition = point1;
		endPosition = point2;

		visible = true;
	}

	/**
	 * Returns whether this selection should be visible.
	 * @return true if this selection should be visible, false otherwise.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets whether this selection should be visible.
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns the smaller position of the two positions defining the selection.
	 * @return the beggining position of the selection
	 */
	public int getLowerPosition() {
		if (startPosition <= endPosition) {
			return startPosition;
		} else {
			return endPosition;
		}
	}

	/**
	 * Returns the bigger position of the two positions defining the selection.
	 * @return the end position of the selection
	 */
	public int getHigherPosition() {
		if (endPosition >= startPosition) {
			return endPosition;
		} else {
			return startPosition;
		}

	}

	/**
	 * Sets the selection to be in the drag-mode.
	 * @param startPosition the position at which the user pressed his
	 * mouse in order to start making the selection
	 */
	public void startDragging(int startPosition) {
		this.startPosition = startPosition;
		this.endPosition = startPosition;
		dragging = true;
		visible = true;
	}

	/**
	 * Returns whether the selection is in the drag-mode.
	 * @return true if the selecion is currently being made, false otherwise
	 */
	public boolean isDragging() {
		return dragging;
	}

	/**
	 * This method can be used in the drag-mode to change the ending position
	 * of the selection.
	 * @param position the position to which the user moved his mouse
	 * after starting the drag-mode
	 */
	public void dragTo(int position) {
		if (!dragging) {
			return;
		}

		endPosition = position;
	}

	/**
	 * Stops the dragging of the selection in the position where the user
	 * last hold his mouse.
	 * @param stopPosition the end position of the selection
	 */
	public void stopDragging(int stopPosition) {
		if (!dragging) {
			return;
		}
		endPosition = stopPosition;
		dragging = false;
	}
}
