/* VisualReferencePositionedBin.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage.visualreference;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import org.signalml.domain.montage.SignalConfigurer;
import org.signalml.math.geometry.Polar3dPoint;

/** VisualReferencePositionedBin
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferencePositionedBin extends VisualReferenceBin {

	// this requires max height

	private Insets backdropMargin;
	private int hOffset;
	private int vOffset;
	private int avHeight;

	public VisualReferencePositionedBin() {
	}

	public Insets getBackdropMargin() {
		if (!valid) {
			validate();
		}
		return backdropMargin;
	}

	@Override
	protected void validate() {

		// calculate available height
		avHeight = maxHeight - (HEADER_HEIGHT + margin.top + margin.bottom);

		// calculate Size
		size = new Dimension(avHeight + margin.left + margin.right, maxHeight);

		int cnt = channels.size();
		if (cnt == 0) {
			valid = true;
			return;
		}

		// calculate backdrop offsets
		Dimension cellSize = getCellSize();
		hOffset = cellSize.width / 2;
		vOffset = cellSize.height / 2;

		backdropMargin = new Insets(vOffset, hOffset, vOffset, hOffset);

		valid = true;

	}

	@Override
	protected void reposition() {

		int cnt = channels.size();
		if (cnt == 0) {
			positioned = true;
			return;
		}

		if (!valid) {
			validate();
		}

		Point location = getLocation();

		if (location != null) {

			int avHeight = this.avHeight - (2*vOffset);
			int avWidth = this.avHeight - (2*hOffset);

			double absoluteX;
			double absoluteY;

			Dimension d;

			// position channels
			for (VisualReferenceSourceChannel channel : channels) {

				Polar3dPoint polar3dPoint = channel.getSourceChannel().getEegElectrode().getPolarPosition();
				d = channel.getShape().getBounds().getSize();

				double centerX = location.x + margin.left + hOffset + avWidth / 2;
				double centerY = location.y + HEADER_HEIGHT + margin.top + vOffset + avHeight / 2;
				Point2D center = new Point2D.Double(centerX, centerY);

				int headSizeForConverter = avWidth/2 - SignalConfigurer.EAR_OR_NOSE_LENGTH - d.width/2;
				Point2D point2d = polar3dPoint.convertTo2DPoint(center, headSizeForConverter);

				absoluteX = point2d.getX() - d.width / 2;
				absoluteY = point2d.getY() - d.height / 2;
				channel.setLocation(new Point((int) Math.round(absoluteX), (int) Math.round(absoluteY)));
			}

			positioned = true;

		}

	}

}
