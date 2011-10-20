/* VisualReferencePositionedBin.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

import org.signalml.domain.montage.IChannelFunction;

/** VisualReferencePositionedBin
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferencePositionedBin extends VisualReferenceBin {

	// this requires max height

	private Insets backdropMargin;
	private int matrixWidth;
	private int matrixHeight;
	private int hOffset;
	private int vOffset;
	private int avHeight;

	public VisualReferencePositionedBin(int matrixWidth, int matrixHeight) {
		this.matrixWidth = matrixWidth;
		this.matrixHeight = matrixHeight;
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

			// calculate grid size
			int avHeight = this.avHeight - (2*vOffset);
			int avWidth = this.avHeight - (2*hOffset);

			float hSize = ((float) avWidth) / (matrixWidth-1);
			float vSize = ((float) avHeight) / (matrixHeight-1);

			float x;
			float y;

			IChannelFunction function;
			int gridX, gridY;

			Dimension d;

			// position channels
			for (VisualReferenceSourceChannel channel : channels) {
				function = channel.getFunction();
				gridX = function.getMatrixCol();
				gridY = function.getMatrixRow();
				d = channel.getShape().getBounds().getSize();
				if (gridX < 0 || gridY < 0) {
					channel.setLocation(new Point(0,0));
					continue;
				}
				x = location.x + margin.left + hOffset + gridX*hSize - d.width / 2;
				y = location.y + HEADER_HEIGHT + margin.top + vOffset + gridY*vSize - d.height / 2;
				channel.setLocation(new Point((int) Math.round(x), (int) Math.round(y)));
			}

			positioned = true;

		}

	}

}
