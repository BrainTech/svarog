/* HypnogramPlot.java created 2007-11-06
 *
 */

package org.signalml.app.view.signal;

import static org.signalml.app.SvarogApplication._;
import static org.signalml.app.SvarogApplication._R;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.SleepTagName;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagEvent;
import org.signalml.domain.tag.TagListener;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.impl.PluginAccessClass;

/** HypnogramPlot
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class HypnogramPlot extends JComponent implements PropertyChangeListener, ChangeListener, TagListener {

	private static final long serialVersionUID = 1L;

	public enum HypnogramMode {
		SHOW_ALL,
		SHOW_ACTIVE
	}

	private static final int SINGLE_HYPNO_SIZE = 18;
	private static final Dimension minimumSize = new Dimension(300,SINGLE_HYPNO_SIZE+7);

	private SignalView view;
	private HashMap<StyledTagSet, HypnogramLine[]> hypnogramMap = new HashMap<StyledTagSet, HypnogramLine[]>();

	private float pixelFactor;
	private float pixelPerSecond;
	private float pixelPerPage;
	private float pageSize;

	private int focusStart;
	private int focusEnd;
	private int focusWidth;
	private int focusCenter;
		
	private int factorStep;
	
	private String noTagsMessage = null;

	private HypnogramMode mode = HypnogramMode.SHOW_ACTIVE;

	private JRadioButtonMenuItem activeRadio;
	private JRadioButtonMenuItem allRadio;
	private JPopupMenu popupMenu;

	private boolean focusCalculated = false;

	public HypnogramPlot(SignalView view) {
		this.view = view;
		noTagsMessage = _("(no tags to display in the hypnogram)");
		setBackground(view.getBackground());
		setAutoscrolls(false);

		HypnogramMouseListener hypnogramMouseListener = new HypnogramMouseListener();

		addMouseListener(hypnogramMouseListener);
		addMouseMotionListener(hypnogramMouseListener);

		setToolTipText("");

	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		reset();
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		reset();
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		reset();
	}

	public void revalidateAndReset() {
		resetAllLines();
		focusCalculated = false;
		revalidate();
		repaint();
	}

	public void reset() {
		resetAllLines();
		focusCalculated = false;
		repaint();
	}

	private void calculateFocus() {

		if (focusCalculated) {
			return;
		}

		SignalPlot plot = view.getMasterPlot();

		Dimension size = getSize();
		Dimension plotSize = plot.getPreferredSize();

		JViewport viewport = plot.getViewport();

		Point viewportPoint = viewport.getViewPosition();
		Dimension viewportSize = viewport.getExtentSize();

		pixelFactor = ((float) size.width) / plotSize.width;
		pixelPerSecond = ((float) size.width) / plot.getMaxTime();
		pageSize = plot.getPageSize();
		pixelPerPage = pixelPerSecond * pageSize;

		focusStart = (int) Math.round(viewportPoint.x * pixelFactor);
		focusWidth = (int) Math.round(viewportSize.width * pixelFactor);
		focusWidth = Math.min(size.width, focusWidth);
		focusEnd = focusStart + focusWidth - 1;
		focusCenter = focusStart + focusWidth/2;

		focusCenter = Math.max(0, Math.min(size.width-1, focusCenter));
		focusEnd = Math.min(size.width-1, focusEnd);

		factorStep = SINGLE_HYPNO_SIZE / 6;

		focusCalculated = true;

	}

	private void resetAllLines() {
		hypnogramMap.clear();
	}

	private void resetLines(StyledTagSet tagSet) {
		hypnogramMap.remove(tagSet);
	}

	protected void paintHypnogram(Graphics2D g, TagDocument tagDocument, int offset, Dimension size) {

		HypnogramLine[] hypnogramLines = null;
		if (tagDocument != null) {
			hypnogramLines = hypnogramMap.get(tagDocument.getTagSet());
			// draw hypnogram
			if (hypnogramLines == null) {
				hypnogramLines = getHypnogramLines(tagDocument);
				hypnogramMap.put(tagDocument.getTagSet(), hypnogramLines);
			}
		}

		if (tagDocument == null || hypnogramLines.length == 0) {

			FontMetrics fm   = g.getFontMetrics(g.getFont());
			Rectangle2D rect = fm.getStringBounds(noTagsMessage, g);

			int textHeight = (int)(rect.getHeight());
			int textWidth  = (int)(rect.getWidth());

			g.setColor(Color.BLACK);
			g.drawString(noTagsMessage, (size.width  - textWidth)  / 2, offset+(SINGLE_HYPNO_SIZE - textHeight) / 2  + fm.getAscent());

		} else {

			int i;
			for (i=0; i<hypnogramLines.length; i++) {

				if (i > 0) {
					if (hypnogramLines[i-1].level != hypnogramLines[i].level) {
						if ((hypnogramLines[i].start - hypnogramLines[i-1].end) <= 1) {
							g.setColor(Color.BLACK);
							g.drawLine(
							        hypnogramLines[i].start,
							        offset+hypnogramLines[i-1].level,
							        hypnogramLines[i].start,
							        offset+hypnogramLines[i].level
							);
						}
					}
				}

				g.setColor(hypnogramLines[i].color);
				g.drawLine(
				        hypnogramLines[i].start,
				        offset+hypnogramLines[i].level,
				        hypnogramLines[i].end,
				        offset+hypnogramLines[i].level
				);

			}

		}

	}

	@Override
	protected void paintComponent(Graphics g1) {

		calculateFocus();

		Graphics2D g = (Graphics2D) g1;

		// draw bg
		g.setColor(getBackground());
		Dimension size = getSize();
		g.fill(new Rectangle(new Point(0,0), size));

		// draw focus
		if (focusEnd - focusStart > 15) {
			// draw area focus

			g.setColor(Color.WHITE);
			g.fillRect(focusStart, 0, 1+(focusEnd-focusStart), size.height);
			g.setColor(Color.RED);

			g.drawLine(focusStart, 0, focusStart, 2);
			g.drawLine(focusStart+1, 0, focusStart+1, 1);
			g.drawLine(focusStart+2, 0, focusStart+2, 0);

			g.drawLine(focusEnd, 0, focusEnd, 2);
			g.drawLine(focusEnd-1, 0, focusEnd-1, 1);
			g.drawLine(focusEnd-2, 0, focusEnd-2, 0);

			g.drawLine(focusStart, size.height-1, focusStart, size.height-3);
			g.drawLine(focusStart+1, size.height-1, focusStart+1, size.height-2);
			g.drawLine(focusStart+2, size.height-1, focusStart+2, size.height-1);

			g.drawLine(focusEnd, size.height-1, focusEnd, size.height-3);
			g.drawLine(focusEnd-1, size.height-1, focusEnd-1, size.height-2);
			g.drawLine(focusEnd-2, size.height-1, focusEnd-2, size.height-1);

		}

		// draw point focus

		g.setColor(Color.RED);

		g.drawLine(focusCenter-2, 0, focusCenter-2, 0);
		g.drawLine(focusCenter-1, 0, focusCenter-1, 1);
		g.drawLine(focusCenter, 0, focusCenter, 2);
		g.drawLine(focusCenter+1, 0, focusCenter+1, 1);
		g.drawLine(focusCenter+2, 0, focusCenter+2, 0);

		g.drawLine(focusCenter-2, size.height-1, focusCenter-2, size.height-1);
		g.drawLine(focusCenter-1, size.height-1, focusCenter-1, size.height-2);
		g.drawLine(focusCenter, size.height-1, focusCenter, size.height-3);
		g.drawLine(focusCenter+1, size.height-1, focusCenter+1, size.height-2);
		g.drawLine(focusCenter+2, size.height-1, focusCenter+2, size.height-1);

		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(focusCenter, 3, focusCenter, size.height-4);

		int offset = 3;

		List<TagDocument> tags = view.getDocument().getTagDocuments();

		if (tags.size() == 0 || mode == HypnogramMode.SHOW_ACTIVE) {

			paintHypnogram(g, view.getDocument().getActiveTag(), offset, size);

		} else {

			int tagCnt = tags.size();
			int i;
			for (i=0; i<tagCnt; i++) {
				if (i > 0) {
					offset += SINGLE_HYPNO_SIZE + 2;
					g.setColor(Color.LIGHT_GRAY);
					g.drawLine(0,offset,size.width-1,offset);
					offset += 2;
				}
				paintHypnogram(g, tags.get(i), offset, size);
			}

		}

	}

	@Override
	public JPopupMenu getComponentPopupMenu() {

		if (popupMenu == null) {

			popupMenu = new JPopupMenu();
			ButtonGroup group = new ButtonGroup();
			activeRadio = new JRadioButtonMenuItem(_("For active tag only"));
			group.add(activeRadio);
			popupMenu.add(activeRadio);
			allRadio = new JRadioButtonMenuItem(_("For all tags"));
			group.add(allRadio);
			popupMenu.add(allRadio);
			
			PluginAccessClass.getGUIImpl().addToHypnogramPlotPopupMenu(popupMenu);

			ActionListener actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (allRadio.isSelected()) {
						setMode(HypnogramMode.SHOW_ALL);
					} else {
						setMode(HypnogramMode.SHOW_ACTIVE);
					}
				}
			};

			activeRadio.addActionListener(actionListener);
			allRadio.addActionListener(actionListener);

		}
		if (mode == HypnogramMode.SHOW_ACTIVE) {
			activeRadio.setSelected(true);
		} else {
			allRadio.setSelected(true);
		}

		return popupMenu;

	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Point point = event.getPoint();
		TagDocument tagDocument = getTagDocumentAtPoint(point);
		if (tagDocument != null) {

			String message;
			if (tagDocument.getBackingFile() == null) {
				message = _R("Hypnogram for new tag {0}", new Object[] { tagDocument.getName() });
			} else {
				message = _R("Hypnogram for {0}", new Object[] { tagDocument.getName() });
			}

			float time = ((float) point.x) / pixelPerSecond ;
			SortedSet<Tag> tags = tagDocument.getTagSet().getTagsBetween(time, time);
			Tag pageTag = null;
			for (Tag tag : tags) {
				if (tag.getType().isPage()) {
					if (time >= tag.getPosition() && time < tag.getEndPosition()) {
						pageTag = tag;
						break;
					}
				}
			}

			if (pageTag != null) {
				message = pageTag.getStyle().getDescriptionOrName() + " ; " + message;
			}

			return message;

		} else {
			return _("Hypnogram");
		}
	}

	private TagDocument getTagDocumentAtPoint(Point point) {
		if (mode == HypnogramMode.SHOW_ACTIVE) {
			return view.getDocument().getActiveTag();
		} else {
			int index = point.y / (SINGLE_HYPNO_SIZE + 4);
			List<TagDocument> tags = view.getDocument().getTagDocuments();
			if (index >= 0 && index < tags.size()) {
				return tags.get(index);
			}
			return null;
		}
	}

	private HypnogramLine[] getHypnogramLines(TagDocument tagDocument) {

		LinkedList<HypnogramLine> lines = new LinkedList<HypnogramLine>();
		HypnogramLine lastLine = null;
		HypnogramLine line = null;

		if (tagDocument != null) {

			SortedSet<Tag> tags = tagDocument.getTagSet().getTags();
			for (Tag tag : tags) {

				if (tag.getType().isPage()) {

					line = getHypnogramLine(tag);
					if (line != null) {

						if (lastLine == null || line.start != lastLine.start || line.end != lastLine.end) {

							lines.add(line);

						}

						lastLine = line;

					}
				}

			}


		}

		HypnogramLine[] lineArr = new HypnogramLine[lines.size()];
		lines.toArray(lineArr);
		return lineArr;

	}

	private HypnogramLine getHypnogramLine(Tag tag) {

		String type = tag.getStyle().getName().toLowerCase();
		int linePosition;
		Color color = null;

		linePosition = SleepTagName.getLevel(type) * factorStep;
		color = SleepTagName.getColor(type);
		if (color == null) {
			color = Color.BLACK;
		}

		int start = (int) Math.round(tag.getPosition()*pixelPerSecond);
		int end = (int) Math.round((tag.getPosition()+tag.getLength()) *pixelPerSecond);

		return new HypnogramLine(color,linePosition, start, end);

	}

	private void showPoint(int x) {

		boolean snapToPageMode = view.isSnapToPageMode();
		if (snapToPageMode) {
			int page = (int) Math.floor(((float) x) / pixelPerPage);
			view.showTime((float)(page * pageSize));
		} else {
			view.showTimeCentered(((float) x) / pixelPerSecond);
		}

	}

	public HypnogramMode getMode() {
		return mode;
	}

	public void setMode(HypnogramMode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			revalidate();
			repaint();
		}
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = new Dimension(minimumSize);
		int tagCnt = view.getDocument().getTagDocuments().size();
		if (tagCnt > 0 && mode == HypnogramMode.SHOW_ALL) {
			size.height = 7 + (tagCnt * SINGLE_HYPNO_SIZE) + (tagCnt > 0 ? (tagCnt-1) * 4 : 0);
		}
		return size;
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		reset();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		focusCalculated = false;
		repaint();
	}

	@Override
	public void tagAdded(TagEvent e) {
		resetLines((StyledTagSet) e.getSource());
		repaint();
	}

	@Override
	public void tagChanged(TagEvent e) {
		resetLines((StyledTagSet) e.getSource());
		repaint();
	}

	@Override
	public void tagRemoved(TagEvent e) {
		resetLines((StyledTagSet) e.getSource());
		repaint();
	}

	private class HypnogramLine {

		private Color color;
		private int level;
		private int start;
		private int end;

		private HypnogramLine(Color color, int level, int start, int end) {
			this.color = color;
			this.level = level;
			this.start = start;
			this.end = end;
		}

		public Color getColor() {
			return color;
		}

		public int getLevel() {
			return level;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

	}

	private class HypnogramMouseListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				showPoint(e.getX());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				showPoint(e.getX());
			}
		}

	}

}
