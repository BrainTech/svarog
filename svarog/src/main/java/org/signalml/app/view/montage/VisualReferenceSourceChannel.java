/* VisualReferenceSourceChannel.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.signalml.domain.montage.IChannelFunction;
import org.signalml.util.Util;

/** VisualReferenceSourceChannel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceSourceChannel {

	public static final int CIRCLE_DIAMETER = 40;

	private int channel;
	private String label;
	private IChannelFunction function;

	private Point location;

	private Shape cachedShape;
	private Shape cachedOutlineShape;

	public VisualReferenceSourceChannel(int channel) {
		this.channel = channel;
	}

	public int getChannel() {
		return channel;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public IChannelFunction getFunction() {
		return function;
	}

	public void setFunction(IChannelFunction function) {
		this.function = function;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		if (!Util.equalsWithNulls(this.location, location)) {
			this.location = location;
			cachedShape = null;
			cachedOutlineShape = null;
		}
	}

	public Shape getShape() {
		if (cachedShape == null) {
			if (location != null) {
				cachedShape = new Ellipse2D.Float(location.x,location.y,CIRCLE_DIAMETER,CIRCLE_DIAMETER);
			} else {
				cachedShape = new Ellipse2D.Float(0,0,CIRCLE_DIAMETER,CIRCLE_DIAMETER);
			}
		}
		return cachedShape;
	}

	public Shape getOutlineShape() {
		if (cachedOutlineShape == null) {
			if (location != null) {
				cachedOutlineShape = new Ellipse2D.Float(location.x,location.y,CIRCLE_DIAMETER-1,CIRCLE_DIAMETER-1);
			} else {
				cachedOutlineShape = new Ellipse2D.Float(0,0,CIRCLE_DIAMETER-1,CIRCLE_DIAMETER-1);
			}
		}
		return cachedOutlineShape;
	}

}
