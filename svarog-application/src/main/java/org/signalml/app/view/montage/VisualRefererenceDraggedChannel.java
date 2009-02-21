/* VisualRefererenceDraggedChannel.java created 2007-12-02
 * 
 */

package org.signalml.app.view.montage;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/** VisualRefererenceDraggedChannel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualRefererenceDraggedChannel extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final Dimension PREFERRED_SIZE = new Dimension( VisualReferenceSourceChannel.CIRCLE_DIAMETER, VisualReferenceSourceChannel.CIRCLE_DIAMETER );
	
	private VisualReferenceEditor editor;
	
	private VisualReferenceSourceChannel channel;

	public VisualRefererenceDraggedChannel(VisualReferenceEditor editor) {
		super();
		this.editor = editor;
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return PREFERRED_SIZE;
	}

	@Override
	public Dimension getMaximumSize() {
		return PREFERRED_SIZE;
	}
	
	@Override
	protected void paintComponent(Graphics gOrig) {
		
		if( channel == null ) {
			return;
		}
		
		Graphics2D g = (Graphics2D) gOrig;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		editor.paintChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), true, false, g);
			
	}

	public VisualReferenceSourceChannel getChannel() {
		return channel;
	}

	public void setChannel(VisualReferenceSourceChannel channel) {
		if( channel != null ) {
			this.channel = new VisualReferenceSourceChannel( channel.getChannel() );
			this.channel.setFunction( channel.getFunction() );
			this.channel.setLabel( channel.getLabel() );
			// position remains null, which is good
		} else {
			channel = null;
		}
	}

	public VisualReferenceEditor getEditor() {
		return editor;
	}	
	
}
