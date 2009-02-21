/* VisualReferenceBin.java created 2007-11-30
 * 
 */

package org.signalml.app.view.montage;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.signalml.util.Util;

/** VisualReferenceBin
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class VisualReferenceBin {

	public static final int HEADER_HEIGHT = 20;
	
	protected String name;
	protected Point location;
	protected Dimension size;
	protected Insets margin;
	protected int hGap = 20;
	protected int vGap = 20;
	
	protected int maxHeight = 0;
		
	protected LinkedHashSet<VisualReferenceSourceChannel> channels = new LinkedHashSet<VisualReferenceSourceChannel>();
	protected boolean valid = false;
	protected boolean positioned = false;

	public VisualReferenceBin() {
		location = new Point(0,0);
		margin = new Insets(4,4,4,4);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		if( !Util.equalsWithNulls( this.location, location ) ) {
			this.location = location;
			positioned = false;
		}
	}
		
	public int getHGap() {
		return hGap;
	}

	public void setHGap(int gap) {
		if( hGap != gap ) {
			valid = false;
			positioned = false;
			hGap = gap;
		}
	}

	public int getVGap() {
		return vGap;
	}

	public void setVGap(int gap) {
		if( vGap != gap ) {
			valid = false;
			positioned = false;
			vGap = gap;
		}
	}

	public Dimension getSize() {
		if( !valid ) {
			validate();
		}
		return size;
	}
	
	public Dimension getCellSize() {
		int w = 0;
		int h = 0;
		Rectangle r;
		
		for( VisualReferenceSourceChannel channel : channels ) {
			r = channel.getShape().getBounds();
			if( w < r.width ) {
				w = r.width;
			}
			if( h < r.height ) {
				h = r.height;
			}			
		}
		
		return new Dimension(w,h);
	}

	public Rectangle getBounds() {
		if( !valid ) {
			validate();
		}
		return new Rectangle( location, size );
	}
		
	public Insets getMargin() {
		return margin;
	}

	public void setMargin(Insets margin) {
		if( !this.margin.equals(margin) ) {
			this.margin = margin;
			positioned = false;
			valid = false;
		}
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		if( this.maxHeight != maxHeight ) {
			this.maxHeight = maxHeight;
			positioned = false;
			valid = false;
		}
	}
	
	public int getMinHeight() {
		return HEADER_HEIGHT + margin.top + margin.bottom + VisualReferenceSourceChannel.CIRCLE_DIAMETER;
	}

	public boolean add(VisualReferenceSourceChannel channel) {
		valid = false;
		positioned = false;
		return channels.add(channel);
	}

	public boolean remove(VisualReferenceSourceChannel channel) {
		valid = false;
		positioned = false;
		return channels.remove(channel);
	}
	
	public void clear() {
		channels.clear();
		positioned = false;
		valid = false;
	}

	public boolean contains(Object o) {
		return channels.contains(o);
	}

	public boolean isEmpty() {
		return channels.isEmpty();
	}

	public Iterator<VisualReferenceSourceChannel> iterator() {
		return channels.iterator();
	}

	public int size() {
		return channels.size();
	}

	public boolean isValid() {
		return valid;
	}
		
	public boolean isPositioned() {
		return positioned;
	}

	protected abstract void validate();
	
	protected abstract void reposition();	
		
}
