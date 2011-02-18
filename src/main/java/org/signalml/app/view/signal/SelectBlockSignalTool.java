/* SelectBlockSignalTool.java created 2007-10-04
 *
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.SignalSelectionType;

/** SelectBlockSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SelectBlockSignalTool extends AbstractSignalTool implements SelectionSignalTool {

	private Integer startBlock;
	private SignalPlot plot;

	public SelectBlockSignalTool(SignalView signalView) {
		super(signalView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	@Override
	public SignalSelectionType getSelectionType() {
		return SignalSelectionType.BLOCK;
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {

			Object source = e.getSource();
			if (!(source instanceof SignalPlot)) {
				plot = null;
				return;
			}
			plot = (SignalPlot) source;

			startBlock = plot.toBlockSpace(e.getPoint());
			setEngaged(true);
			e.consume();

		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			selectTo(e.getPoint());
			startBlock = null;
			setEngaged(false);
			plot = null;
			e.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			Point point = e.getPoint();
			selectTo(point);
			Rectangle r = new Rectangle(point.x, point.y, 1, 1);
			((SignalPlot)e.getSource()).scrollRectToVisible(r);
		}
	}

	private void selectTo(Point point) {
		if (startBlock != null) {
			Integer endBlock = plot.toBlockSpace(point);
			if (endBlock != null) {
				getSignalView().setSignalSelection(plot,plot.getBlockSelection(startBlock, endBlock));
			}
		}
	}

}
