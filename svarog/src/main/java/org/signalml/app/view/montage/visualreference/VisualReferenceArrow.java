/* VisualReferenceArrow.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage.visualreference;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import org.signalml.util.Util;

/** VisualReferenceArrow
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceArrow {

	public static final int FULL_SHAFT_WIDTH = 3;
	public static final int FULL_SHAFT_WIDTH_AT_POINT = 5;
	public static final int FULL_POINT_WIDTH = 8;
	public static final int POINT_LENGTH = 12;

	public static final double HALF_SHAFT_WIDTH = FULL_SHAFT_WIDTH / 2.;
	public static final double HALF_SHAFT_WIDTH_AT_POINT = FULL_SHAFT_WIDTH_AT_POINT / 2.;
	public static final double HALF_POINT_WIDTH = FULL_POINT_WIDTH / 2.;

	private int sourceChannel;
	private int targetChannel;

	private Point fromPoint;
	private Point toPoint;

	private Color color;

	private Shape cachedShape;

	private boolean positioned = false;

	public VisualReferenceArrow(int sourceChannel, int targetChannel) {
		this.sourceChannel = sourceChannel;
		this.targetChannel = targetChannel;
	}

	public int getSourceChannel() {
		return sourceChannel;
	}

	public int getTargetChannel() {
		return targetChannel;
	}

	public Point getFromPoint() {
		return fromPoint;
	}

	public void setFromPoint(Point fromPoint) {
		if (!Util.equalsWithNulls(this.fromPoint, fromPoint)) {
			this.fromPoint = fromPoint;
			cachedShape = null;
		}
	}

	public Point getToPoint() {
		return toPoint;
	}

	public void setToPoint(Point toPoint) {
		if (!Util.equalsWithNulls(this.toPoint, toPoint)) {
			this.toPoint = toPoint;
			cachedShape = null;
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Shape getShape() {
		if (cachedShape == null) {
			if (fromPoint == null || toPoint == null) {
				return null;
			} else {
				cachedShape = createArrowShape();
			}
		}
		return cachedShape;
	}

	private Shape createArrowShape() {

		double distance = fromPoint.distance(toPoint);
		double fixedPointLength = Math.min(distance, POINT_LENGTH);
		double shaftLength = distance - fixedPointLength;

		Path2D.Double path = new Path2D.Double();
		path.moveTo(fromPoint.x, fromPoint.y);
		if (shaftLength > 0) {

			path.lineTo(fromPoint.x, fromPoint.y-HALF_SHAFT_WIDTH);
			path.lineTo(fromPoint.x+shaftLength, fromPoint.y-HALF_SHAFT_WIDTH_AT_POINT);
			path.lineTo(fromPoint.x+shaftLength, fromPoint.y-HALF_POINT_WIDTH);
			path.lineTo(fromPoint.x+distance, fromPoint.y);
			path.lineTo(fromPoint.x+shaftLength, fromPoint.y+HALF_POINT_WIDTH);
			path.lineTo(fromPoint.x+shaftLength, fromPoint.y+HALF_SHAFT_WIDTH_AT_POINT);
			path.lineTo(fromPoint.x, fromPoint.y+HALF_SHAFT_WIDTH);

		} else {

			// short triangular shaft-less arrow to be used for very shor distances

			path.lineTo(fromPoint.x, fromPoint.y-HALF_POINT_WIDTH);
			path.lineTo(fromPoint.x+distance, fromPoint.y);
			path.lineTo(fromPoint.x, fromPoint.y+HALF_POINT_WIDTH);

		}
		path.lineTo(fromPoint.x, fromPoint.y);

		// calculate angle and rotate the arrow
		double dx = toPoint.x - fromPoint.x;
		double dy = toPoint.y - fromPoint.y;

		double theta = Math.atan2(dy, dx);

		return AffineTransform.getRotateInstance(theta, fromPoint.x, fromPoint.y).createTransformedShape(path);

	}

	public boolean isPositioned() {
		return positioned;
	}

	public void setPositioned(boolean positioned) {
		this.positioned = positioned;
	}

}
