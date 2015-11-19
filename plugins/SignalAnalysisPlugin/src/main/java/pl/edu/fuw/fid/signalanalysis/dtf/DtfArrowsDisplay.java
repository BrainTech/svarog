package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.math.linear.RealMatrix;

import org.signalml.app.util.GeometryUtils;
import org.signalml.app.view.montage.visualreference.VisualReferenceArrow;
import org.signalml.app.view.montage.visualreference.VisualReferenceBin;
import org.signalml.app.view.montage.visualreference.VisualReferenceChannel;
import org.signalml.app.view.montage.visualreference.VisualReferenceDisplay;
import org.signalml.app.view.montage.visualreference.VisualReferenceModel;
import org.signalml.app.view.montage.visualreference.VisualReferenceSourceChannel;

/**
 * @author ptr@mimuw.edu.pl
 */
public class DtfArrowsDisplay extends VisualReferenceDisplay {

	private static final Color INACTIVE_CHANNEL_COLOR = Color.LIGHT_GRAY;
	private static final Color ACTIVE_CHANNEL_COLOR = new Color(0, 198, 255);

	private final LinkedList<VisualReferenceArrow> arrows = new LinkedList<VisualReferenceArrow>();
	private RealMatrix transfers; // between output channels of montage

	public DtfArrowsDisplay(VisualReferenceModel model) {
		super(model);
	}

	public void setTransferData(RealMatrix transfers) {
		this.transfers = transfers;
		positionArrows();
		repaint();
	}

	public void paintChannel(String label, int perPrimarySize, Shape shape, Shape outlineShape, boolean source, boolean selected, Graphics2D g) {
		Color fill = source ? INACTIVE_CHANNEL_COLOR : ACTIVE_CHANNEL_COLOR;
		paintGivenChannel(label, perPrimarySize, shape, outlineShape, fill, Color.BLACK, false, g);
	}

	@Override
	protected void paintBinContents(VisualReferenceBin bin, Graphics2D g) {
		Iterator<VisualReferenceSourceChannel> it = bin.iterator();
		while (it.hasNext()) {
			VisualReferenceSourceChannel channel = it.next();
			int channelIdx = channel.getChannel();
			int perPrimarySize = model.channelsPerPrimarySize(channelIdx);

			if (perPrimarySize == 0) {
				paintChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), true, false, g);
			} else {
				VisualReferenceChannel montageChannel = model.getChannelPerPrimary(channelIdx, 0);
				paintChannel(montageChannel.getLabel(), perPrimarySize, channel.getShape(), channel.getOutlineShape(), false, false, g);
			}
		}
	}

	@Override
	protected void paintComponent(Graphics gOrig) {
		super.paintComponent(gOrig);
		// let's paint some arrows on top
		Graphics2D g = Get2DGraphics(gOrig);
		for (VisualReferenceArrow arrow : arrows) {
			g.setColor(arrow.getColor());
			Shape arrowShape = arrow.getShape();
			g.fill(arrowShape);
		}
	}

	@Override
	protected Dimension calculateRequiredSize() {
		Dimension retval = super.calculateRequiredSize();
		positionArrows();
		return retval;
	}

	@Override
	public Dimension getMinimumSize() {
		return null;
	}

	private void positionArrow(VisualReferenceArrow arrow) {
		Point point, fromPoint, toPoint;
		Rectangle bounds;

		int source = arrow.getSourceChannel();
		int target = arrow.getTargetChannel();

		VisualReferenceSourceChannel arrowSource = model.getSourceChannel(source);
		VisualReferenceChannel toChannel = model.getChannel(target);
		VisualReferenceSourceChannel arrowTarget = model.getSourceChannel(toChannel.getPrimaryChannel());

		point = arrowSource.getLocation();
		bounds = arrowSource.getShape().getBounds();
		fromPoint = new Point(point.x + bounds.width / 2, point.y + bounds.height / 2);

		point = arrowTarget.getLocation();
		bounds = arrowTarget.getShape().getBounds();
		toPoint = new Point(point.x + bounds.width / 2, point.y + bounds.height / 2);

		GeometryUtils.translatePointToCircleBorder(fromPoint, toPoint, VisualReferenceSourceChannel.CIRCLE_DIAMETER/2);
		GeometryUtils.translatePointToCircleBorder(toPoint, fromPoint, VisualReferenceSourceChannel.CIRCLE_DIAMETER/2);

		arrow.setFromPoint(fromPoint);
		arrow.setToPoint(toPoint);
		arrow.setPositioned(true);
	}

	private void positionArrows() {
		arrows.clear();
		if (transfers != null) {
			double max = 0.0;
			for (int src=0; src<transfers.getRowDimension(); ++src) {
				double[] srcCoeffs = transfers.getRow(src);
				for (int dst=0; dst<srcCoeffs.length; ++dst) if (dst != src) {
					max = Math.max(max, srcCoeffs[dst]);
				}
			}
			for (int src=0; src<transfers.getRowDimension(); ++src) {
				double[] srcCoeffs = transfers.getRow(src);
				for (int dst=0; dst<srcCoeffs.length; ++dst) if (dst != src) {
					if (srcCoeffs[dst] > 0) {
						double value = srcCoeffs[dst] / max;
						float[] rgb = Color.getHSBColor((2+(float)value)/3, 1, 1).getRGBColorComponents(new float[3]);
						Color color = new Color(rgb[0], rgb[1], rgb[2], (float) value);
						VisualReferenceArrow arrow = new VisualReferenceArrow(model.getMontage().getMontagePrimaryChannelAt(src), dst);
						arrow.setColor(color);
						positionArrow(arrow);
						arrows.add(arrow);
					}
				}
			}
		}
	}

}
