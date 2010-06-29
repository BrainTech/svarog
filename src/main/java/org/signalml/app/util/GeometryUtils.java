/* GeometryUtils.java created 2007-12-01
 *
 */

package org.signalml.app.util;

import java.awt.Point;
import java.awt.geom.AffineTransform;

/** GeometryUtils
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class GeometryUtils {

	public static void translatePointToCircleBorder(Point point, Point refPoint, int radius) {

		if (refPoint.x == point.x) {
			if (refPoint.y < point.y) {
				point.y -= radius;
			} else {
				point.y += radius;
			}
		} else {
			int dx = point.x - refPoint.x;
			int dy = point.y - refPoint.y;
			int sgnx = (dx < 0 ? -1 : (dx > 0 ? 1 : 0));
			int sgny = (dy < 0 ? -1 : (dy > 0 ? 1 : 0));
			float tg = ((float) dy)/dx;
			int mx = (int) Math.round(((float) radius) / Math.sqrt(1+tg*tg));
			int my = (int) Math.round(((float) radius) / Math.sqrt(1+(1/(tg*tg))));
			point.x -= mx*sgnx;
			point.y -= my*sgny;
		}

	}

	public static void rotatePoint(Point point, Point centerPoint, double angle) {

		AffineTransform t = AffineTransform.getRotateInstance(angle, centerPoint.x, centerPoint.y);
		t.transform(point, point);

	}

}
