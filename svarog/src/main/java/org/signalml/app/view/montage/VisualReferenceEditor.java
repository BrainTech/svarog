/* VisualReferenceEditor.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.signalml.app.util.GeometryUtils;

/** VisualReferenceEditor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceEditor extends VisualReferenceDisplay {

	private static final long serialVersionUID = 1L;

	private static final BasicStroke WHITE_SELECTION_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[] {3,3}, 0F);
	private static final BasicStroke BLACK_SELECTION_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[] {3,3}, 3F);

	private static final Color[] ARROW_COLORS = {
		Color.RED,
		Color.GREEN.darker(),
		Color.BLUE,
		Color.CYAN,
		Color.MAGENTA,
		Color.YELLOW,
		Color.ORANGE,
		Color.BLACK
	};

        private static final Color INACTIVE_CHANNEL_COLOR = Color.LIGHT_GRAY;
	private static final Color ACTIVE_CHANNEL_COLOR = new Color(0, 198, 255);

	private LinkedList<VisualReferenceArrow> tempArrowsToDraw = new LinkedList<VisualReferenceArrow>();
	private VisualReferenceArrow prospectiveArrow;

	public VisualReferenceEditor(VisualReferenceModel model) {

                super(model);

		VisualReferenceEditorMouseHandler mouseHandler = new VisualReferenceEditorMouseHandler(this);

		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

	}

	public VisualReferenceArrow getProspectiveArrow() {
		return prospectiveArrow;
	}

	public void setProspectiveArrow(VisualReferenceArrow prospectiveArrow) {
		if (this.prospectiveArrow != prospectiveArrow) {
			this.prospectiveArrow = prospectiveArrow;
			if (prospectiveArrow != null) {
				positionArrow(prospectiveArrow, true);
			}
			repaint();
		}
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	public void paintChannel(String label, int perPrimarySize, Shape shape, Shape outlineShape, boolean source, boolean selected, Graphics2D g) {

        	Color fill;
                Color outline;
                boolean boldBorder;

		if (source) fill = INACTIVE_CHANNEL_COLOR;
		else fill = ACTIVE_CHANNEL_COLOR;
		
		if (selected) {
			outline = Color.RED;
			boldBorder = true;
		} else {
			outline = Color.BLACK;
                        boldBorder = false;
		}

		paintGivenChannel(label, perPrimarySize, shape, outlineShape, fill, outline, boldBorder, g);

	}
        
        @Override
	protected void paintBinContents(VisualReferenceBin bin, Graphics2D g) {

		Iterator<VisualReferenceSourceChannel> it = bin.iterator();
		VisualReferenceSourceChannel channel;
		int channelIdx;
		Iterator<VisualReferenceChannel> montageIt;
		VisualReferenceChannel selChannel = model.getActiveChannel();
		VisualReferenceChannel montageChannel;
		boolean selectedPainted;
		int perPrimarySize;

		while (it.hasNext()) {

			channel = it.next();
			channelIdx = channel.getChannel();
			perPrimarySize = model.channelsPerPrimarySize(channelIdx);

			if (perPrimarySize == 0) {

				paintChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), true, false, g);

			} else {

				selectedPainted = false;

				if (selChannel != null) {
					montageIt = model.channelsPerPrimaryIterator(channelIdx);
					while (montageIt.hasNext()) {
						montageChannel = montageIt.next();
						if (montageChannel == selChannel) {
							paintChannel(montageChannel.getLabel(), perPrimarySize, channel.getShape(), channel.getOutlineShape(), false, true, g);
							selectedPainted = true;
							break;
						}
					}

					if (!selectedPainted) {
						montageChannel = model.getChannelPerPrimary(channelIdx, 0);
						paintChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), (selChannel != null), false, g);
					}

				} else {

					if (!selectedPainted) {
						montageChannel = model.getChannelPerPrimary(channelIdx, 0);
						paintChannel(montageChannel.getLabel(), perPrimarySize, channel.getShape(), channel.getOutlineShape(), (selChannel != null), false, g);
					}

				}


			}

		}

	}

	protected void paintArrow(VisualReferenceArrow arrow, Graphics2D g, boolean active, boolean selected) {

		if (active) {
			g.setColor(arrow.getColor());
		} else {
			g.setColor(Color.LIGHT_GRAY);
		}

		Shape arrowShape = arrow.getShape();
		g.fill(arrowShape);

		if (selected) {

			g.setColor(Color.WHITE);
			g.setStroke(WHITE_SELECTION_STROKE);
			g.draw(arrowShape);
			g.setColor(Color.BLACK);
			g.setStroke(BLACK_SELECTION_STROKE);
			g.draw(arrowShape);

		}

	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		super.paintComponent(gOrig);

        	Graphics2D g = Get2DGraphics(gOrig);

		VisualReferenceChannel selChannel = model.getActiveChannel();
		int selChannelIndex = -1;
		if (selChannel != null) {
			selChannelIndex = model.indexOfChannel(selChannel);
		}

		VisualReferenceArrow selArrow = model.getActiveArrow();
		VisualReferenceArrow arrow;

                tempArrowsToDraw.clear();

		int colorIndex;
		VisualReferenceChannel toChannel;

		Iterator<VisualReferenceArrow> arrowIt = model.arrowsIterator();
		while (arrowIt.hasNext()) {
			arrow = arrowIt.next();
			if (selChannel == null) {
				toChannel = model.getChannel(arrow.getTargetChannel());
				colorIndex = model.indexOfChannelPerPrimary(toChannel.getPrimaryChannel(), toChannel);
				colorIndex = colorIndex % ARROW_COLORS.length;
				paintArrow(arrow, g, true, (arrow == selArrow));
			} else {
				if (arrow.getTargetChannel() != selChannelIndex) {
					paintArrow(arrow, g, false, false);
				} else {
					tempArrowsToDraw.add(arrow);
				}
			}
		}

		if (selChannel != null) {
			arrowIt = tempArrowsToDraw.iterator();
			while (arrowIt.hasNext()) {
				arrow = arrowIt.next();
				paintArrow(arrow, g, true, (arrow == selArrow));
			}
		}

		if (prospectiveArrow != null) {
			paintArrow(prospectiveArrow, g, true, true);
		}

	}

	@Override
	public boolean isDoubleBuffered() {
		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		requiredSize = null;
	}

	@Override
	public Dimension getPreferredSize() {
		if (requiredSize == null) {
			requiredSize = calculateRequiredSize();
		}
		return requiredSize;
	}

	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}

	public VisualReferenceSourceChannel findChannelInBinAt(VisualReferenceBin bin, Point point) {

		if (bin.isEmpty()) {
			return null;
		}

		Iterator<VisualReferenceSourceChannel> it = bin.iterator();
		VisualReferenceSourceChannel channel;
		while (it.hasNext()) {
			channel = it.next();
			if (channel.getShape().contains(point)) {
				return channel;
			}
		}

		return null;

	}

	public VisualReferenceSourceChannel findChannelAt(Point point) {

		VisualReferencePositionedBin positionedBin = model.getPositionedBin();
		VisualReferenceBin primariesBin = model.getPrimariesBin();
		VisualReferenceBin othersBin = model.getOthersBin();
		VisualReferenceBin referencesBin = model.getReferencesBin();

		if (positionedBin.getBounds().contains(point)) {
			return findChannelInBinAt(positionedBin, point);
		}
		else if (primariesBin.getBounds().contains(point)) {
			return findChannelInBinAt(primariesBin, point);
		}
		else if (othersBin.getBounds().contains(point)) {
			return findChannelInBinAt(othersBin, point);
		}
		else if (referencesBin.getBounds().contains(point)) {
			return findChannelInBinAt(referencesBin, point);
		}

		return null;

	}

	public ArrayList<VisualReferenceArrow> findArrowsAtPoint(Point point, ArrayList<VisualReferenceArrow> listToFill) {

		ArrayList<VisualReferenceArrow> list;
		if (listToFill != null) {
			list = listToFill;
			list.clear();
		} else {
			list = new ArrayList<VisualReferenceArrow>();
		}

		Iterator<VisualReferenceArrow> arrowIt = model.arrowsIterator();
		VisualReferenceArrow arrow;
		Shape arrowShape;

		while (arrowIt.hasNext()) {

			arrow = arrowIt.next();
			arrowShape = arrow.getShape();

			if (arrowShape.getBounds().contains(point)) {
				if (arrowShape.contains(point)) {
					list.add(arrow);
				}
			}

		}

		return list;

	}

        @Override
	protected Dimension calculateRequiredSize() {

		Dimension retval = super.calculateRequiredSize();

                positionArrows(true);

		return retval;

	}

	private void positionArrows(boolean all) {

		VisualReferenceArrow arrow;
		Iterator<VisualReferenceArrow> it = model.arrowsIterator();

		while (it.hasNext()) {
			arrow = it.next();
			if (all || !arrow.isPositioned()) {
				positionArrow(arrow, false);
			}
		}

	}

	private void positionArrow(VisualReferenceArrow arrow, boolean targetIsPrimary) {

		int source, target;
		VisualReferenceSourceChannel arrowSource;
		VisualReferenceSourceChannel arrowTarget;
		VisualReferenceChannel toChannel;
		Point point;
		Point fromPoint;
		Point toPoint;
		Rectangle bounds;

		source = arrow.getSourceChannel();
		target = arrow.getTargetChannel();

		arrowSource = model.getSourceChannel(source);
		int arrowOrder;
		int colorIndex;
		if (targetIsPrimary) {
			arrowTarget = model.getSourceChannel(target);
			arrow.setColor(Color.WHITE);
			arrowOrder = 0;
		} else {
			toChannel = model.getChannel(target);
			arrowTarget = model.getSourceChannel(toChannel.getPrimaryChannel());
			arrowOrder = model.getArrowOrder(target, source);
			colorIndex = model.indexOfChannelPerPrimary(toChannel.getPrimaryChannel(), toChannel);
			arrow.setColor(ARROW_COLORS[ colorIndex % ARROW_COLORS.length ]);
		}

		point = arrowSource.getLocation();
		bounds = arrowSource.getShape().getBounds();
		fromPoint = new Point(point.x + bounds.width / 2, point.y + bounds.height / 2);

		point = arrowTarget.getLocation();
		bounds = arrowTarget.getShape().getBounds();
		toPoint = new Point(point.x + bounds.width / 2, point.y + bounds.height / 2);

		GeometryUtils.translatePointToCircleBorder(fromPoint, toPoint, VisualReferenceSourceChannel.CIRCLE_DIAMETER/2);
		GeometryUtils.translatePointToCircleBorder(toPoint, fromPoint, VisualReferenceSourceChannel.CIRCLE_DIAMETER/2);
		if (arrowOrder > 0) {

			// this rotates the arrows slightly so that they superimpose less

			arrowOrder = Math.min(10, arrowOrder);   // limit to 10 to prevent reahing anything close 90 deg
			double angle = Math.toRadians(10*((arrowOrder+1)/2) * ((arrowOrder % 2) == 0 ? -1 : 1));
			Point centerPoint = (Point) arrowTarget.getLocation().clone();
			centerPoint.translate(VisualReferenceSourceChannel.CIRCLE_DIAMETER / 2, VisualReferenceSourceChannel.CIRCLE_DIAMETER / 2);
			GeometryUtils.rotatePoint(toPoint, centerPoint, angle);

		}

		arrow.setFromPoint(fromPoint);
		arrow.setToPoint(toPoint);

		arrow.setPositioned(true);

	}

	@Override
	public void montageChannelsChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		// TODO maybe optimize this
		revalidate();
		repaint();
	}

	@Override
	public void montageStructureChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		revalidate();
		repaint();
	}

	@Override
	public void referenceChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		// TODO maybe optimize this
		positionArrows(true);
		repaint();
	}

	@Override
	public void sourceChannelsChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		revalidate();
		repaint();
	}

}
