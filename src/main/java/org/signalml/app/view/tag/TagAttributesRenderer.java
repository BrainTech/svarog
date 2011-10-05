/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Piotr Szachewicz
 */
public class TagAttributesRenderer extends JComponent {

	private Tag tag;
	private static int paddingTop = 10;
	private static int paddingRight = 10;
	private static int paddingBottom = 10;
	private static int paddingLeft = 10;
	private static int marginTopForMarkers = 30;
	private static int spaceBetweenLines = 5;
	private static final BasicStroke ATTRIBUTES_BORDER_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[]{3, 3}, 3F);

	public Component getTagAttributesRendererComponent(Tag tag) {
		this.tag = tag;
		return this;

	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		if (tag == null) {
			return;
		}

		if (!doesTagHaveVisibleAttributes(tag)) {
			return;
		}

		Graphics2D g = (Graphics2D) gOrig;

		Point rectangleStartPoint;

		if (tag.isMarker()) {
			rectangleStartPoint = new Point(0, marginTopForMarkers);
		} else {
			rectangleStartPoint = new Point(0, 0);
		}
		Rectangle rect = new Rectangle(rectangleStartPoint, getAttributesRectangleDimensions(g, tag));

		//g.setColor(new Color(200, 200, 200));
		g.setColor(new Color(0, 0, 0));
		//g.setColor(Color.BLUE);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		g.fill(rect);

		g.setColor(Color.BLACK);
		g.setStroke(ATTRIBUTES_BORDER_STROKE);
		g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);

		int y = getCurrentFontHeight(g) + paddingTop;

		if (tag.isMarker()) {
			y += marginTopForMarkers;
		}

		//g.setColor(Color.BLACK);
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

	protected String getAttributeDisplayString(TagAttributeValue a) {
		String displayValue = a.getAttributeDefinition().getDisplayName();
		String value = a.getAttributeValue();

		if (value.length() > 15) {
			value = value.substring(0, 15);
		}

		return displayValue + ": " + value;
	}

	protected int getCurrentFontHeight(Graphics2D g) {
		FontMetrics metrics = new FontMetrics(g.getFont()) {
		};
		return metrics.getHeight();
	}

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
