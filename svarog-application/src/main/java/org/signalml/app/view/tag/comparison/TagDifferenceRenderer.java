/* TagDifferenceRenderer.java created 2007-11-14
 * 
 */

package org.signalml.app.view.tag.comparison;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.signalml.domain.tag.TagDifferenceType;

/** TagDifferenceRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDifferenceRenderer extends JComponent {

	private static final long serialVersionUID = 1L;

	private TagDifferenceType type;
			
	public Component getTagDifferenceRendererComponent( TagDifferenceType type ) {
		
		this.type = type;
		
		return this;
		
	}
		
	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D) gOrig;
		
		Dimension size = getSize();
		Rectangle rect = new Rectangle(new Point(0,0), size);
				
		if( type == null ) {
			g.setColor(getBackground());
			g.fill(rect);
			return;
		}
		if( type == TagDifferenceType.SAME ) {
			return;
		}
				
		g.setColor(type.getColor());
		g.fill(rect);
							
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}

	public TagDifferenceType getType() {
		return type;
	}

	public void setType(TagDifferenceType type) {
		this.type = type;
	}
		
}
