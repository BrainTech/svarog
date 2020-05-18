package org.signalml.app.view.tag;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.signalml.plugin.export.signal.TagStyle;

/**
 * TagIconProducer
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagIconProducer {

	private static final Font labelFont = new Font("Dialog", Font.PLAIN, 10);

	private Map<TagStyle,Icon> icons = new HashMap<TagStyle,Icon>();

	private Polygon markerShape;

	public Icon getIcon(TagStyle style) {
		Icon icon = icons.get(style);
		if (icon == null) {
			icon = createIcon(style);
			icons.put(style, icon);
		}
		return icon;
	}


	public void reset(TagStyle style) {
		icons.remove(style);
	}

	public void resetAll() {
		icons.clear();
	}

	protected Color getContrastingColor(Color backgroundColor) {

		// color conversion based on http://en.wikipedia.org/wiki/Grayscale

		float[] rgb = backgroundColor.getColorComponents(null);
		double grayScale = 0.3*rgb[0] + 0.59*rgb[1] + 0.11*rgb[2];
		if (grayScale > 0.4) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}

	}

	protected void paintNormal(TagStyle tagStyle, Graphics2D g) {

		Color fillColor = tagStyle.getFillColor();
		g.setColor(fillColor);
		g.fillRect(0,0,16,16);

		g.setColor(tagStyle.getOutlineColor());
		float width = Math.min(1F, tagStyle.getOutlineWidth());
		g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, tagStyle.getOutlineDash(), 0F));
		g.drawRect(0, 0, 15, 15);

		String letter = getLetterForTagStyle(tagStyle);

		g.setFont(labelFont);

		g.setColor(getContrastingColor(fillColor));

		Rectangle2D labelBounds = labelFont.getStringBounds(letter, g.getFontRenderContext());
		g.drawString(
			letter,
			(float)((16-labelBounds.getWidth())/2),
			(float)(((16-labelBounds.getHeight())/2) - labelBounds.getY())
		);

	}

	private String getLetterForTagStyle(TagStyle tagStyle) {
		KeyStroke keyStroke = tagStyle.getKeyStroke();
		if (keyStroke != null) {
			// we cannot use keyStroke.getKeyChar because
			// these are "key press" events instead of "key type" as they should
			int keyCode = keyStroke.getKeyCode();
			String label = KeyEvent.getKeyText(keyCode);
			if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
				if ((keyStroke.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) == 0) {
					label = label.toLowerCase();
				}
			}
			return label;
		}
		String tagName = tagStyle.getName();
		if (tagName != null && !tagName.isEmpty()) {
			return tagName.substring(0, 1);
		}
		return "?";
	}

	protected Shape getMarkerShape() {

		if (markerShape == null) {
			markerShape = new Polygon();

			markerShape.addPoint(0, 0);
			markerShape.addPoint(15, 0);
			markerShape.addPoint(15-3, 15);
			markerShape.addPoint(3, 15);

		}
		return markerShape;

	}

	protected void paintMarker(TagStyle tagStyle, Graphics2D g) {

		Shape shape = getMarkerShape();

		Color fillColor = tagStyle.getFillColor();
		g.setColor(fillColor);
		g.fill(shape);

		g.setColor(tagStyle.getOutlineColor());
		float width = Math.min(1F, tagStyle.getOutlineWidth());
		g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, tagStyle.getOutlineDash(), 0F));

		g.draw(shape);
		String letter = getLetterForTagStyle(tagStyle);

		g.setFont(labelFont);

		g.setColor(getContrastingColor(fillColor));

		Rectangle2D labelBounds = labelFont.getStringBounds(letter, g.getFontRenderContext());
		g.drawString(
			letter,
			(float)((16-labelBounds.getWidth())/2),
			(float)(((16-labelBounds.getHeight())/2) - labelBounds.getY())
		);

	}

	public Icon createIcon(TagStyle tagStyle) {

		BufferedImage bi = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();

		if (tagStyle.isMarker()) {
			paintMarker(tagStyle, g);
		} else {
			paintNormal(tagStyle, g);
		}

		return new ImageIcon(bi);

	}

}
