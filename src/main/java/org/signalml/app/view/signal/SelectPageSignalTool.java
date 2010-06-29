/* SelectPageSignalTool.java created 2007-10-04
 *
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.signalml.app.util.IconUtils;
import org.signalml.domain.signal.SignalSelectionType;

/** SelectPageSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SelectPageSignalTool extends SignalTool implements SelectionSignalTool {

	private Integer startPage;

	private SignalPlot plot;

	public SelectPageSignalTool(SignalView signalView) {
		super(signalView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	@Override
	public SignalSelectionType getSelectionType() {
		return SignalSelectionType.PAGE;
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

			startPage = plot.toPageSpace(e.getPoint());
			engaged = true;
			e.consume();

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			selectTo(e.getPoint());
			startPage = null;
			engaged = false;
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
		if (startPage != null) {
			Integer endPage = plot.toPageSpace(point);
			if (endPage != null) {
				signalView.setSignalSelection(plot,plot.getPageSelection(startPage, endPage));
			}
		}
	}

}
