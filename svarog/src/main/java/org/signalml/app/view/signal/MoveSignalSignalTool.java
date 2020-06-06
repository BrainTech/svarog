/* MoveSignalSignalTool.java created 2007-09-26
 *
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.AbstractSignalTool;

/** MoveSignalSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MoveSignalSignalTool extends AbstractSignalTool {

	private Point dragStart = null;
	private Point dragScrollStart = null;

	private SignalPlot plot;

	public MoveSignalSignalTool(SignalView signalView) {
		super(signalView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getHandCursor();
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


			dragStart = e.getLocationOnScreen();
			dragScrollStart = plot.getViewport().getViewPosition();
			setEngaged(true);
			e.consume();

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			dragStart = null;
			dragScrollStart = null;
			setEngaged(false);
			plot = null;
			e.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (plot != null) {
			if (SwingUtilities.isLeftMouseButton(e)) {

				Point position = e.getLocationOnScreen();

				JViewport viewport = plot.getViewport();
				Dimension d = viewport.getViewSize();

				Point p = new Point(dragScrollStart.x, dragScrollStart.y);
				p.x -= (position.x - dragStart.x);
				p.y -= (position.y - dragStart.y);

				p.x = Math.max(0, Math.min(d.width-viewport.getWidth(), p.x));
				p.y = Math.max(0, Math.min(d.height-viewport.getHeight(), p.y));

				viewport.setViewPosition(p);

				dragStart = e.getLocationOnScreen();
				dragScrollStart = p;

				e.consume();

			}
		}

	}

}
