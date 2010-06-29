/* SelectAtomBookTool.java created 2008-02-23
 *
 */

package org.signalml.app.view.book;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.signalml.domain.book.SegmentReconstructionProvider;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.exception.SanityCheckException;

/** SelectAtomBookTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SelectAtomBookTool extends BookTool {

	public SelectAtomBookTool(BookView bookView) {
		super(bookView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return Cursor.getDefaultCursor();
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {

			BookPlot plot = bookView.getPlot();
			StandardBookAtom nearestAtom = plot.getNearestAtom(e.getPoint(), 20);
			if (nearestAtom != null) {

				SegmentReconstructionProvider provider = plot.getReconstructionProvider();
				int index = plot.getSegment().indexOfAtom(nearestAtom);
				if (provider.isAtomInSelectiveReconstruction(index)) {
					provider.removeAtomFromSelectiveReconstruction(index);
				} else {
					provider.addAtomToSelectiveReconstruction(index);
				}
				Point atomLocation = plot.getAtomLocation(nearestAtom);
				if (atomLocation == null) {
					throw new SanityCheckException("Nearest atom not in map");
				}
				plot.repaint(atomLocation.x-4, atomLocation.y-4, 8, 8);
				plot.repaint(plot.getReconstructionRectangle());

			}

		}

	}

}
