/* Selection.java created 2011-02-14
 *
 */
package org.signalml.app.view.montage.charts.elements;

/**
 *
 * @author Piotr Szachewicz
 */
class Selection {

	private int startPosition;
	private int endPosition;
	private boolean visible;
	private boolean dragging;

	public Selection() {
		visible = false;
	}

	public Selection(int point1, int point2) {

		startPosition = point1;
		endPosition = point2;

		visible = true;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getLowerPosition() {
		if (startPosition <= endPosition) {
			return startPosition;
		} else {
			return endPosition;
		}
	}

	public int getHigherPosition() {
		if (endPosition >= startPosition) {
			return endPosition;
		} else {
			return startPosition;
		}

	}

	public void startDragging(int startPosition) {
		this.startPosition = startPosition;
		this.endPosition = startPosition;
		dragging = true;
		visible = true;
	}

	public boolean isDragging() {
		return dragging;
	}

	public void dragTo(int position) {
		if (!dragging) {
			return;
		}

		endPosition = position;
	}

	public void stopDragging(int stopPosition) {
		if (!dragging) {
			return;
		}
		endPosition = stopPosition;
		dragging = false;
	}
}
