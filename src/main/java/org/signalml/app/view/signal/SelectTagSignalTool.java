/* SelectTagSignalTool.java created 2007-09-26
 *
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/** SelectTagSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SelectTagSignalTool extends SignalTool {

	public SelectTagSignalTool(SignalView signalView) {
		super(signalView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return Cursor.getDefaultCursor();
	}

	@Override
	public boolean supportsColumnHeader() {
		return true;
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
			Object source = e.getSource();
			if (source instanceof SignalPlot) {
				((SignalPlot) source).selectTagAtPoint(e.getPoint());
			} else if (source instanceof SignalPlotColumnHeader) {
				SignalPlotColumnHeader columnHeader = (SignalPlotColumnHeader) source;
				PositionedTag tag = columnHeader.getSelectableTagAtPoint(e.getPoint());
				columnHeader.getPlot().getView().setTagSelection(columnHeader.getPlot(),tag);
			}
		}
	}

}
