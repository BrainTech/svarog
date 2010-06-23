/* TagBlockSignalTool.java created 2007-10-13
 * 
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.TagStyle;

/** TagBlockSignalTool
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagBlockSignalTool extends SignalTool implements TaggingSignalTool {

	protected static final Logger logger = Logger.getLogger(TagBlockSignalTool.class);
	
	private Integer startBlock;
	private SignalPlot plot;
		
	public TagBlockSignalTool(SignalView signalView) {
		super(signalView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	
	@Override
	public SignalSelectionType getTagType() {
		return SignalSelectionType.BLOCK;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {

		if( SwingUtilities.isLeftMouseButton(e) ) {
		
			Object source = e.getSource();
			if( !(source instanceof SignalPlot) ) {
				plot = null;
				return;
			}
			plot = (SignalPlot) source;
			
			startBlock = plot.toBlockSpace(e.getPoint());
			engaged = true;
			e.consume();
			
		}
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if( SwingUtilities.isLeftMouseButton(e) ) {
			tagTo( e.getPoint() );
			startBlock = null;
			engaged = false;
			plot = null;
			e.consume();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if( SwingUtilities.isLeftMouseButton(e) ) {		
			Point point = e.getPoint();
			selectTo(point);
			Rectangle r = new Rectangle(point.x, point.y, 1, 1);
			((SignalPlot)e.getSource()).scrollRectToVisible(r);
		}
	}

	private void selectTo(Point point) {
		if( startBlock != null ) {
			Integer endBlock = plot.toBlockSpace(point);
			if( endBlock != null ) {
				signalView.setSignalSelection(plot,plot.getBlockSelection(startBlock, endBlock));
			}
		}		
	}

	private void tagTo(Point point) {
		
		if( startBlock != null ) {
		
			Integer endBlock = plot.toBlockSpace(point);
			if( endBlock != null ) {
				
				TagStyle style = signalView.getCurrentTagStyle(SignalSelectionType.BLOCK);
				TagDocument tagDocument = signalView.getDocument().getActiveTag();
				if( tagDocument != null ) {

					if( style == null ) {
						plot.eraseTagsFromSelection(tagDocument, plot.getBlockSelection(startBlock, endBlock));
					} else {
						plot.tagBlockSelection(tagDocument, style, plot.getBlockSelection(startBlock, endBlock), true);
					}
					
				}
							
			}
	
			signalView.clearSignalSelection();
			
		}
		
	}
	
}
