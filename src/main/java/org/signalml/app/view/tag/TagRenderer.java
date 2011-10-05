/* TagRenderer.java created 2007-10-10
 *
 */
package org.signalml.app.view.tag;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.JComponent;

import org.signalml.plugin.export.signal.TagStyle;

/** TagRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagRenderer extends JComponent {

	private static final long serialVersionUID = 1L;
	// 2 * tg(30 deg)
	private static final double DOUBLE_TG30 = 2 * Math.tan(Math.toRadians(30));
	private TagStyle tagStyle;
	private boolean selected;
	private boolean selectionOnly;
	private boolean active;
	private static final BasicStroke WHITE_SELECTION_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[]{3, 3}, 0F);
	private static final BasicStroke BLACK_SELECTION_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[]{3, 3}, 3F);

	public Component getTagRendererComponent(TagStyle tagStyle, boolean isActive, boolean isSelected) {

		this.selectionOnly = false;
		this.tagStyle = tagStyle;
		this.active = isActive;
		this.selected = isSelected;

		return this;

	}

	public Component getTagSelectionRendererComponent() {

		this.selectionOnly = true;
		this.selected = true;

		return this;

	}

	protected void drawNormal(Graphics2D g) {

		Rectangle rect = new Rectangle(new Point(0, 0), getSize());

		if (!selectionOnly) {
			g.setColor(tagStyle.getFillColor());
			g.fill(rect);
			g.setComposite(AlphaComposite.SrcOver);

			g.setColor(tagStyle.getOutlineColor());
			float width = tagStyle.getOutlineWidth();
			int offset = (int) width;
			g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, tagStyle.getOutlineDash(), 0F));

			g.drawRect(rect.x + (offset / 2), rect.y + (offset / 2), rect.width - offset, rect.height - offset);
		}

		if (selected) {
			g.setColor(Color.WHITE);
			g.setStroke(WHITE_SELECTION_STROKE);
			g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
			g.setColor(Color.BLACK);
			g.setStroke(BLACK_SELECTION_STROKE);
			g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
		}

		if (!selectionOnly && active) {
			g.setColor(Color.RED);
			g.fillOval(2, 2, 4, 4);
		}
	}

	protected void drawMarker(Graphics2D g) {

		Rectangle rect = new Rectangle(new Point(0, 0), getSize());

		int rWidth = Math.min(50, rect.width);
		rWidth = Math.min(rWidth, rect.height / 3);
		rWidth = Math.max(rWidth, 5);

		int offset = (rWidth < rect.width ? (rect.width - rWidth) / 2 : 0);

		int rHeight = (int) Math.round(((double) rWidth) / DOUBLE_TG30);

		int rWidthDiv2 = rWidth / 2;

		Polygon triangle = new Polygon();

		triangle.addPoint(offset, 0);
		triangle.addPoint(offset + rWidth - 1, 0);
		triangle.addPoint(offset + rWidthDiv2, rHeight - 1);

		if (!selectionOnly) {
			g.setColor(tagStyle.getFillColor());
			g.fill(triangle);
			g.setComposite(AlphaComposite.SrcOver);

			g.setColor(tagStyle.getOutlineColor());
			float width = tagStyle.getOutlineWidth();

			Stroke oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, tagStyle.getOutlineDash(), 0F));
			g.draw(triangle);
			g.setStroke(oldStroke);
			g.drawLine(offset + rWidthDiv2, rHeight, offset + rWidthDiv2, rect.height - 1);

		}

		if (selected) {
			g.setColor(Color.WHITE);
			g.setStroke(WHITE_SELECTION_STROKE);
			g.draw(triangle);

			g.setColor(Color.BLACK);
			g.setStroke(BLACK_SELECTION_STROKE);
			g.draw(triangle);
		}

		if (!selectionOnly && active) {
			g.setColor(Color.RED);
			g.fillOval(offset + rWidthDiv2 - 2, 2, 4, 4);
		}

	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D) gOrig;
		Composite origComp = g.getComposite();

		if (!selectionOnly && tagStyle == null) {
			Rectangle rect = new Rectangle(new Point(0, 0), getSize());
			g.setComposite(AlphaComposite.SrcOver);
			g.setColor(getBackground());
			g.fill(rect);
		} else {

			if (tagStyle.isMarker()) {
				drawMarker(g);
			} else {
				drawNormal(g);
			}

		}

		g.setComposite(origComp);

	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	public TagStyle getTagStyle() {
		return tagStyle;
	}

	public void setTagStyle(TagStyle tagStyle) {
		if (this.tagStyle != tagStyle) {
			this.tagStyle = tagStyle;
			repaint();
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
