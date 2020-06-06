/* PlotScrollingCoordinator.java created 2007-11-08
 *
 */

package org.signalml.app.view.signal;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** PlotScrollingCoordinator
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PlotScrollingCoordinator implements ChangeListener {

	private SignalPlot masterPlot;

	private LinkedList<SignalPlot> plots;
	private HashMap<JViewport,SignalPlot> plotsByViewports;

	private boolean lock = false;

	public PlotScrollingCoordinator(SignalPlot masterPlot) {
		this.masterPlot = masterPlot;
		masterPlot.getViewport().addChangeListener(this);
		plots = new LinkedList<SignalPlot>();
		plotsByViewports = new HashMap<JViewport, SignalPlot>();
	}

	public void addPlot(SignalPlot plot) {
		JViewport viewport = plot.getViewport();
		viewport.addChangeListener(this);
		plotsByViewports.put(viewport, plot);
		plots.add(plot);
	}

	public void removePlot(SignalPlot plot) {
		JViewport viewport = plot.getViewport();
		viewport.removeChangeListener(this);
		plotsByViewports.remove(viewport);
		plots.remove(plot);
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		if (lock) {
			return;
		}

		try {

			lock = true;

			JViewport viewport = (JViewport) e.getSource();
			SignalPlot plot;
			boolean masterChanged = false;

			if (viewport != masterPlot.getViewport()) {
				plot = plotsByViewports.get(viewport);
				if (plot == null) {
					throw new NullPointerException("Plot not in the map");
				}
				masterChanged = synchronizeMaster(plot);
			} else {
				plot = masterPlot;
				masterChanged = true;
			}

			if (masterChanged) {
				synchronizeToMaster(viewport);
			}

			plot.updateSignalPlotSynchronizationLabel();

		} finally {
			lock = false;
		}

	}

	private void synchronizeToMaster(JViewport originator) {

		Point masterPosition = masterPlot.getViewport().getViewPosition();
		Point slavePosition;
		Dimension slaveSize;
		Dimension slaveViewportSize;

		JViewport viewport;
		for (SignalPlot plot : plots) {
			viewport = plot.getViewport();
			if (viewport == originator) {
				continue;
			}
			if (plot.getMasterPlot() == masterPlot) {

				if (plot.isHorizontalLock() || plot.isVerticalLock()) {

					slavePosition = viewport.getViewPosition();
					slaveViewportSize = viewport.getExtentSize();
					slaveSize = plot.getSize();
					if (plot.isHorizontalLock()) {
						slavePosition.x = masterPosition.x + plot.getHorizontalPixelLead();
						slavePosition.x = Math.max(0, Math.min(slaveSize.width-slaveViewportSize.width, slavePosition.x));
					}
					if (plot.isVerticalLock()) {
						slavePosition.y = masterPosition.y + plot.getVerticalPixelLead();
						slavePosition.y = Math.max(0, Math.min(slaveSize.height-slaveViewportSize.height, slavePosition.y));
					}

					viewport.setViewPosition(slavePosition);

				}

				plot.updateSignalPlotSynchronizationLabel();

			}

		}

	}

	private boolean synchronizeMaster(SignalPlot plot) {

		boolean changed = false;

		if (plot.isHorizontalLock() || plot.isVerticalLock()) {

			Point slavePosition = plot.getViewport().getViewPosition();
			JViewport viewport = masterPlot.getViewport();

			Point masterPosition = viewport.getViewPosition();;
			Dimension masterSize = masterPlot.getSize();
			Dimension masterViewportSize = viewport.getExtentSize();

			int oldPosition;

			if (plot.isHorizontalLock()) {
				oldPosition = masterPosition.x;
				masterPosition.x = slavePosition.x - plot.getHorizontalPixelLead();
				masterPosition.x = Math.max(0, Math.min(masterSize.width-masterViewportSize.width, masterPosition.x));
				if (masterPosition.x != oldPosition) {
					changed = true;
				}
			}
			if (plot.isVerticalLock()) {
				oldPosition = masterPosition.y - plot.getVerticalPixelLead();
				masterPosition.y = slavePosition.y;
				masterPosition.y = Math.max(0, Math.min(masterSize.height-masterViewportSize.height, masterPosition.y));
				if (masterPosition.y != oldPosition) {
					changed = true;
				}
			}

			if (changed) {
				viewport.setViewPosition(masterPosition);
			}

		}

		return changed;

	}

}
