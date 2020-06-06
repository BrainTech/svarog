/* ZoomMouseWheelListener.java created 2007-11-08
 *
 */

package org.signalml.app.view.signal;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import javax.swing.BoundedRangeModel;
import javax.swing.JViewport;

/** ZoomMouseWheelListener
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ZoomMouseWheelListener implements MouseWheelListener {

	private SignalView signalView;

	private boolean timeEnabled = true;

	public ZoomMouseWheelListener(SignalView signalView) {
		this.signalView = signalView;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		int valueTime = 0;
		BoundedRangeModel timeModel = null;

		Object source = e.getSource();
		if (!(source instanceof SignalPlot)) {
			return;
		}

		// prevent from conflicting with tools
		if (signalView.isToolEngaged()) {
			return;
		}

		SignalPlot plot = (SignalPlot) source;

		int rotation = -e.getWheelRotation();

		if (timeEnabled) {

			timeModel = plot.getTimeScaleRangeModel();

			valueTime = timeModel.getValue();

			int maxTime = timeModel.getMaximum();
			int minTime = timeModel.getMinimum();
			float stepTime = (maxTime - minTime) / 20F;
			valueTime = valueTime + (int)(stepTime*rotation);
			valueTime = Math.max(minTime, Math.min(maxTime, valueTime));

		}

		BoundedRangeModel channelModel = plot.getChannelHeightRangeModel();
		int valueChannel = channelModel.getValue();

		int maxChannel = channelModel.getMaximum();
		int minChannel = channelModel.getMinimum();
		float stepChannel = (maxChannel - minChannel) / 20F;
		valueChannel = valueChannel + (int)(stepChannel*rotation);
		valueChannel = Math.max(minChannel, Math.min(maxChannel, valueChannel));

		JViewport viewport = plot.getViewport();

		Point viewportPoint = viewport.getViewPosition();
		Dimension viewportSize = viewport.getExtentSize();
		Point p = e.getPoint();
		Point2D.Float p2 = plot.toSignalSpace(p);

		// suppress centerpoint compensation in order to compensate around mouse pointer
		plot.setCompensationEnabled(false);

		try {
			if (timeEnabled) {
				timeModel.setValue(valueTime);
			}
			channelModel.setValue(valueChannel);

			// viewport needs to be validated after change, so that getSize returns a valid value
			viewport.validate();
			Dimension plotSize = plot.getSize();

			Point newP = plot.toPixelSpace(p2);
			newP.x = newP.x - (p.x - viewportPoint.x);
			newP.y = newP.y - (p.y - viewportPoint.y);

			newP.x = Math.max(0, Math.min(plotSize.width - viewportSize.width, newP.x));
			newP.y = Math.max(0, Math.min(plotSize.height - viewportSize.height, newP.y));

			viewport.setViewPosition(newP);
		} finally {
			plot.setCompensationEnabled(true);
		}

	}

	public boolean isTimeEnabled() {
		return timeEnabled;
	}

	public void setTimeEnabled(boolean b) {
		timeEnabled = b;
	}

}
