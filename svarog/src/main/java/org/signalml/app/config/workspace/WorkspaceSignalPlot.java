/* WorkspaceSignalPlot.java created 2007-12-15
 *
 */

package org.signalml.app.config.workspace;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JViewport;

import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.domain.montage.Montage;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** WorkspaceSignalPlot
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("workspacesignalplot")
public class WorkspaceSignalPlot {

	private boolean antialiased;
	private boolean clamped;
	private boolean offscreenChannelsDrawn;
	private boolean tagToolTipsVisible;

	private boolean pageLinesVisible;
	private boolean blockLinesVisible;
	private boolean channelLinesVisible;

	private TagPaintMode tagPaintMode;
	private SignalColor signalColor;
	private boolean signalXOR;

	private float timePosition;
	private float valuePosition;

	private boolean horizontalLock;
	private boolean verticalLock;

	private float horizontalTimeLead;
	private float verticalValueLead;

	private Montage localMontage;

	private boolean compactColumnHeader;

	protected WorkspaceSignalPlot() {
	}

	public WorkspaceSignalPlot(SignalPlot plot) {

		antialiased = plot.isAntialiased();
		clamped = plot.isClamped();
		offscreenChannelsDrawn = plot.isOffscreenChannelsDrawn();
		tagToolTipsVisible = plot.isTagToolTipsVisible();

		pageLinesVisible = plot.isPageLinesVisible();
		blockLinesVisible = plot.isBlockLinesVisible();
		channelLinesVisible = plot.isChannelLinesVisible();

		tagPaintMode = plot.getTagPaintMode();
		signalColor = plot.getSignalColor();
		signalXOR = plot.isSignalXOR();

		Point p = plot.getViewport().getViewPosition();
		Point2D.Float p2 = plot.toSignalSpace(p);
		timePosition = (float) p2.getX();
		valuePosition = (float) p2.getY();

		if (plot.getMasterPlot() != null) {
			horizontalLock = plot.isHorizontalLock();
			verticalLock = plot.isVerticalLock();

			horizontalTimeLead = plot.getHorizontalTimeLead();
			verticalValueLead = plot.getVerticalValueLead();

			localMontage = plot.getLocalMontage();
		} else {
			horizontalLock = false;
			verticalLock = false;
			horizontalTimeLead = 0;
			verticalValueLead = 0;

			localMontage = null;
		}

		compactColumnHeader = plot.getSignalPlotColumnHeader().isCompact();

	}

	public void configurePlot(SignalPlot plot) {

		SignalPlot masterPlot = plot.getMasterPlot();

		if (masterPlot == null) {

			plot.setAntialiased(antialiased);
			plot.setClamped(clamped);
			plot.setOffscreenChannelsDrawn(offscreenChannelsDrawn);
			plot.setTagToolTipsVisible(tagToolTipsVisible);

			plot.setPageLinesVisible(pageLinesVisible);
			plot.setBlockLinesVisible(blockLinesVisible);
			plot.setChannelLinesVisible(channelLinesVisible);

			plot.setTagPaintMode(tagPaintMode);
			plot.setSignalColor(signalColor);
			plot.setSignalXOR(signalXOR);

		}

		Point2D.Float p2 = new Point2D.Float(timePosition, valuePosition);
		Point p = plot.toPixelSpace(p2);

		JViewport viewport = plot.getViewport();
		viewport.validate();
		Dimension viewportSize = viewport.getExtentSize();
		Dimension plotSize = plot.getSize();

		plot.getSignalPlotColumnHeader().setCompact(compactColumnHeader);

		if (masterPlot != null) {
			plot.setLocalMontage(localMontage);
		}

		p.x = Math.max(0, Math.min(plotSize.width - viewportSize.width, p.x));
		p.y = Math.max(0, Math.min(plotSize.height - viewportSize.height, p.y));

		viewport.setViewPosition(p);

		if (masterPlot != null) {
			plot.setHorizontalTimeLead(horizontalTimeLead);
			plot.setVerticalValueLead(verticalValueLead);

			plot.setHorizontalLock(horizontalLock);
			plot.setVerticalLock(verticalLock);
		}

	}

}
