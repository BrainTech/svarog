/* VisualReferenceDisplay.java created 2010-10-24
 *
 */

package org.signalml.app.view.montage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/** VisualReferenceDisplay
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class VisualReferenceDisplay extends JComponent implements VisualReferenceListener, PropertyChangeListener, Scrollable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_CHANNEL_LABEL_LENGTH = 7;
	public static final int BIN_SPACING = 10;

	protected VisualReferenceModel model;

	protected Font binLabelFont;
	protected Font channelLabelFont;

	protected FontMetrics binLabelFontMetrics;
	protected FontMetrics channelLabelFontMetrics;

	protected Stroke defaultStroke;
	protected Stroke activeChannelStroke;

	protected JViewport viewport;

	protected Dimension requiredSize = null;


	public VisualReferenceDisplay(VisualReferenceModel model) {

		super();

		setAutoscrolls(true);

		if (model == null) {
			throw new NullPointerException("No model");
		}
		this.model = model;

		setFocusable(true);

		model.addPropertyChangeListener(this);
		model.addVisualReferenceListener(this);
	}

	public JViewport getViewport() {
		return viewport;
	}

	public void setViewport(JViewport viewport) {
		this.viewport = viewport;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	protected abstract void paintBinContents(VisualReferenceBin bin, Graphics2D g);

        protected void paintGivenChannel(String label, int perPrimarySize, Shape shape, Shape outlineShape, Color fill, Color outline, boolean boldBorder, Graphics2D g) {

		g.setColor(fill);
		g.fill(shape);
		
		g.setColor(outline);
                if (boldBorder) g.setStroke(activeChannelStroke);
		
		g.draw(outlineShape);
		g.setStroke(defaultStroke);

		g.setColor(Color.BLACK);
		g.setFont(channelLabelFont);
                
		String abbrLabel;
		if (label.length() > MAX_CHANNEL_LABEL_LENGTH) {
			abbrLabel = label.substring(0,MAX_CHANNEL_LABEL_LENGTH-2) + "...";
		} else {
			abbrLabel = label;
		}
		Rectangle2D rect = channelLabelFontMetrics.getStringBounds(abbrLabel, g);

		int textHeight = (int)(rect.getHeight());
		int textWidth  = (int)(rect.getWidth());

		Rectangle r = shape.getBounds();

		g.drawString(abbrLabel, r.x + (r.width  - textWidth)  / 2, r.y + (r.height - textHeight) / 2  + channelLabelFontMetrics.getAscent());

		if (perPrimarySize > 1) {

			abbrLabel = "(" + Integer.toString(perPrimarySize) + ")";
			rect = channelLabelFontMetrics.getStringBounds(abbrLabel, g);
			textWidth  = (int)(rect.getWidth());

			g.drawString(abbrLabel, r.x + (r.width  - textWidth)  / 2, r.y + (r.height - textHeight) / 2  + channelLabelFontMetrics.getAscent() + textHeight);

		}

	}

        protected void paintBin(VisualReferenceBin bin, Graphics2D g) {

		Dimension size = bin.getSize();
		Point location = bin.getLocation();
		String name = bin.getName();

		g.setColor(Color.DARK_GRAY);
		g.drawRect(location.x, location.y, size.width-1, size.height-1);
		g.fillRect(location.x, location.y, size.width, VisualReferenceBin.HEADER_HEIGHT);

		if (name != null && !name.isEmpty()) {

			g.setColor(Color.WHITE);

			g.setFont(binLabelFont);
			Rectangle2D rect = binLabelFontMetrics.getStringBounds(name, g);

			int textHeight = (int)(rect.getHeight());
			int textWidth  = (int)(rect.getWidth());

			g.drawString(name, location.x + (size.width  - textWidth)  / 2, location.y + (VisualReferenceBin.HEADER_HEIGHT - textHeight) / 2  + binLabelFontMetrics.getAscent());

		}

		if (bin instanceof VisualReferencePositionedBin) {
			VisualReferencePositionedBin positionedBin = (VisualReferencePositionedBin) bin;
			Insets backdropMargin = positionedBin.getBackdropMargin();
			if (backdropMargin == null) {
				backdropMargin = new Insets(0,0,0,0);
			}
			Insets margin = positionedBin.getMargin();
			int width = size.width - (margin.left + margin.right + backdropMargin.left + backdropMargin.right);
			int height = size.height - (VisualReferenceBin.HEADER_HEIGHT + margin.top + margin.bottom + backdropMargin.top + backdropMargin.bottom);
			Image backdrop = model.getMontage().getSignalTypeConfigurer().getMatrixBackdrop(width, height);
			if (backdrop != null) {
				g.drawImage(backdrop, location.x + margin.left + backdropMargin.left, location.y + VisualReferenceBin.HEADER_HEIGHT + margin.top + backdropMargin.top, null);
			}
		}

	}

        protected Graphics2D Get2DGraphics(Graphics gOrig) {

		Graphics2D g = (Graphics2D) gOrig;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (defaultStroke == null) {
			defaultStroke = g.getStroke();
			activeChannelStroke = new BasicStroke(3);
		}

		if (binLabelFont == null || channelLabelFont == null) {
			binLabelFont = g.getFont();
			channelLabelFont = new Font(Font.DIALOG, Font.PLAIN, 9);
			binLabelFontMetrics = g.getFontMetrics(binLabelFont);
			channelLabelFontMetrics = g.getFontMetrics(channelLabelFont);
		}

        	return g;

        }

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = Get2DGraphics(gOrig);

		Rectangle clip = g.getClipBounds();

		g.setColor(getBackground());
		g.fill(clip);

		VisualReferenceBin othersBin = model.getOthersBin();
		VisualReferencePositionedBin positionedBin = model.getPositionedBin();
		VisualReferenceBin referencesBin = model.getReferencesBin();
		VisualReferenceBin primariesBin = model.getPrimariesBin();

		paintBin(positionedBin, g);
		if (!othersBin.isEmpty()) {
			paintBin(othersBin, g);
		}
		if (!referencesBin.isEmpty()) {
			paintBin(referencesBin, g);
		}
		if (!primariesBin.isEmpty()) {
			paintBin(primariesBin, g);
		}

		paintBinContents(positionedBin, g);
		if (!othersBin.isEmpty()) {
			paintBinContents(othersBin, g);
		}
		if (!referencesBin.isEmpty()) {
			paintBinContents(referencesBin, g);
		}
		if (!primariesBin.isEmpty()) {
			paintBinContents(primariesBin, g);
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

	protected Dimension calculateRequiredSize() {

		Dimension viewportSize = viewport.getExtentSize();
		int avHeight = viewportSize.height - (2 * BIN_SPACING);

		VisualReferenceBin othersBin = model.getOthersBin();
		VisualReferencePositionedBin positionedBin = model.getPositionedBin();
		VisualReferenceBin referencesBin = model.getReferencesBin();
		VisualReferenceBin primariesBin = model.getPrimariesBin();

		int minimumHeight = 0;
		int minBinHeight;
		if (!othersBin.isEmpty()) {
			minBinHeight = othersBin.getMinHeight();
			if (minBinHeight > minimumHeight) {
				minimumHeight = minBinHeight;
			}
		}

		minimumHeight += (2 * BIN_SPACING);
		if (avHeight < minimumHeight) {
			avHeight = minimumHeight;
		}

		Dimension size;

		positionedBin.setMaxHeight(avHeight);

		size = positionedBin.getSize();

		int width = 2*BIN_SPACING + size.width;

		if (!othersBin.isEmpty()) {

			othersBin.setMaxHeight(avHeight);

			size = othersBin.getSize();
			width += (BIN_SPACING + size.width);

		}

		if (!referencesBin.isEmpty()) {

			referencesBin.setMaxHeight(avHeight);
			size = referencesBin.getSize();

			width += (BIN_SPACING + size.width);

		}

		if (!primariesBin.isEmpty()) {

			primariesBin.setMaxHeight(avHeight);
			size = primariesBin.getSize();

			width += (BIN_SPACING + size.width);

		}

		int centeringOffset = 0;
		if (viewportSize.width > width) {
			centeringOffset = (viewportSize.width - width) / 2;
		}

		// now position the bins and re-validate them
		int topX = centeringOffset + BIN_SPACING;

		if (!othersBin.isEmpty()) {

			othersBin.setLocation(new Point(topX, BIN_SPACING));
			if (!othersBin.isPositioned()) {
				othersBin.reposition();
			}
			size = othersBin.getSize();
			topX += (size.width + BIN_SPACING);

		}

		if (!primariesBin.isEmpty()) {

			primariesBin.setLocation(new Point(topX, BIN_SPACING));
			if (!primariesBin.isPositioned()) {
				primariesBin.reposition();
			}
			size = primariesBin.getSize();
			topX += (size.width + BIN_SPACING);


		}

		if (!referencesBin.isEmpty()) {

			referencesBin.setLocation(new Point(topX, BIN_SPACING));
			if (!referencesBin.isPositioned()) {
				referencesBin.reposition();
			}
			size = referencesBin.getSize();
			topX += (size.width + BIN_SPACING);

		}

		positionedBin.setLocation(new Point(topX, BIN_SPACING));
		if (!positionedBin.isPositioned()) {
			positionedBin.reposition();
		}

		return new Dimension(width, viewportSize.height);

	}
               
	public VisualReferenceModel getModel() {
		return model;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 100;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return (getPreferredSize().width < viewport.getExtentSize().width);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		String name = evt.getPropertyName();
		if (source == model) {
			if (VisualReferenceModel.ACTIVE_ARROW_PROPERTY.equals(name)) {
				repaint();
			}
			else if (VisualReferenceModel.ACTIVE_CHANNEL_PROPERTY.equals(name)) {
				repaint();
			}
		}
	}

	@Override
	public void montageChannelsChanged(VisualReferenceEvent ev) {
				
		revalidate();
		repaint();
	}

	@Override
	public void montageStructureChanged(VisualReferenceEvent ev) {

		revalidate();
		repaint();
	}

	@Override
	public void referenceChanged(VisualReferenceEvent ev) {

	}

	@Override
	public void sourceChannelsChanged(VisualReferenceEvent ev) {

        	revalidate();
		repaint();
	}

}
