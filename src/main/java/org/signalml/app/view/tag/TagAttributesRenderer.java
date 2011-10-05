package org.signalml.app.view.tag;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JComponent;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.tagStyle.TagAttributeValue;

/**
 * Renderer capable of rendering tag attributes on a tag.
 *
 * @author Piotr Szachewicz
 */
public class TagAttributesRenderer extends JComponent {

	/**
	 * The forementioned tag.
	 */
	private Tag tag;

	/**
	 * The top padding of the filled rectangle in which tag attributes are
	 * displayed.
	 */
	private static int paddingTop = 10;
	/**
	 * The right padding of the filled rectangle in which tag attributes are
	 * displayed.
	 */
	private static int paddingRight = 10;
	/**
	 * The bottom padding of the filled rectangle in which tag attributes are
	 * displayed.
	 */
	private static int paddingBottom = 10;
	/**
	 * The left padding of the filled rectangle in which tag attributes are
	 * displayed.
	 */
	private static int paddingLeft = 10;
	/**
	 * The top margin of the filled rectangle set when the tag is a marker.
	 */
	private static int marginTopForMarkers = 30;
	/**
	 * Space between each line used when printing attributes.
	 */
	private static int spaceBetweenLines = 5;
	/**
	 * Stroke used to border the filled rectangle.
	 */
	private static final BasicStroke ATTRIBUTES_BORDER_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[]{3, 3}, 3F);

	/**
	 * Returns the attributes renderer for the given tag.
	 * @param tag a Tag for which renderer will be returned
	 * @return renderer for the given tag
	 */
	public Component getTagAttributesRendererComponent(Tag tag) {
		this.tag = tag;
		return this;
	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		if (tag == null || !doesTagHaveVisibleAttributes(tag))
			return;

		Graphics2D g = (Graphics2D) gOrig;

		drawRectangleAndBorder(g);
		printAttributes(g);

	}

	/**
	 * Draws the filled rectangle with a border in which the attributes will
	 * be printed.
	 * @param g Graphics2D object used for drawing
	 */
	protected void drawRectangleAndBorder(Graphics2D g) {
		Point rectangleStartPoint;

		if (tag.isMarker()) {
			rectangleStartPoint = new Point(0, marginTopForMarkers);
		} else {
			rectangleStartPoint = new Point(0, 0);
		}
		Rectangle rect = new Rectangle(rectangleStartPoint, getAttributesRectangleDimensions(g, tag));

		g.setColor(new Color(0, 0, 0));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		g.fill(rect);

		g.setColor(Color.BLACK);
		g.setStroke(ATTRIBUTES_BORDER_STROKE);
		g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
	}

	/**
	 * Draws the tag attributes in the filled rectangle.
	 * @param g a Graphics2D instance used for drawing
	 */
	protected void printAttributes(Graphics2D g) {
		int y = getCurrentFontHeight(g) + paddingTop;

		if (tag.isMarker()) {
			y += marginTopForMarkers;
		}

		g.setColor(Color.WHITE);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		for (TagAttributeValue a : tag.getAttributes().getAttributesList()) {
			if (!a.getAttributeDefinition().isVisible()) {
				continue;
			}

			String display = getAttributeDisplayString(a);
			g.drawString(display, paddingLeft, y);
			y += getCurrentFontHeight(g) + spaceBetweenLines;
		}
	}

	/**
	 * Returns, whether the given tag has any visible attributes.
	 * @param tag tag to be investigated
	 * @return true if the given tag has any attributes whose definitions
	 * state that they should be visible, false otherwise
	 */
	protected boolean doesTagHaveVisibleAttributes(Tag tag) {
		List<TagAttributeValue> attributesList = tag.getAttributes().getAttributesList();
		if (attributesList.size() == 0) {
			return false;
		}

		for (TagAttributeValue v : attributesList) {
			if (v.getAttributeDefinition().isVisible()) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Returns a string that should be displayed for the given {@link TagAttributeValue}.
	 * @param a {@link TagAttributeValue} for which the string will be
	 * returned
	 * @return the string that should be displayed
	 */
	protected String getAttributeDisplayString(TagAttributeValue a) {
		String displayValue = a.getAttributeDefinition().getDisplayName();
		String value = a.getAttributeValue();

		if (value.length() > 15) {
			value = value.substring(0, 15);
		}

		return displayValue + ": " + value;
	}

	/**
	 * Returns the height of the currently selected font.
	 * @param g Graphics2D object used for drawing
	 * @return the height of the currently selected font
	 */
	protected int getCurrentFontHeight(Graphics2D g) {
		FontMetrics metrics = new FontMetrics(g.getFont()) {
		};
		return metrics.getHeight();
	}

	/**
	 * Returns the dimensions of the filled rectangle that is shown under
	 * the attributes.
	 *
	 * @param g the Graphics2D object used for drawing
	 * @param tag the currently rendered tag
	 * @return the dimensions of the attributes rectangle
	 */
	protected Dimension getAttributesRectangleDimensions(Graphics2D g, Tag tag) {
		FontMetrics metrics = new FontMetrics(g.getFont()) {
		};

		int rectangleWidth = 0;
		int rectangleHeight = 0;

		int numberOfVisibleAttributes = 0;
		for (TagAttributeValue a : tag.getAttributes().getAttributesList()) {
			if (!a.getAttributeDefinition().isVisible()) {
				continue;
			}

			String displayString = getAttributeDisplayString(a);
			Rectangle2D bounds = metrics.getStringBounds(displayString, null);
			int widthInPixels = (int) bounds.getWidth();
			int heightInPixels = (int) bounds.getHeight();

			if (widthInPixels > rectangleWidth) {
				rectangleWidth = widthInPixels;
			}

			rectangleHeight += heightInPixels;
			numberOfVisibleAttributes++;
		}
		rectangleHeight += (numberOfVisibleAttributes - 1) * spaceBetweenLines;
		rectangleHeight += paddingTop;
		rectangleHeight += paddingBottom;

		rectangleWidth += paddingLeft;
		rectangleWidth += paddingRight;

		return new Dimension(rectangleWidth, rectangleHeight);
	}

}
