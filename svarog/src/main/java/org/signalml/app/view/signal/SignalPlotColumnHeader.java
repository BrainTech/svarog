/* SignalPlotColumnHeader.java created 2007-10-15
 *
 */

package org.signalml.app.view.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.signalml.app.document.TagDocument;
import org.signalml.app.view.tag.TagRenderer;
import org.signalml.app.view.tag.comparison.TagDifferenceRenderer;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagDifference;
import org.signalml.domain.tag.TagDifferenceSet;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/** SignalPlotColumnHeader
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPlotColumnHeader extends JComponent {

	private static final int TOP_OFFSET = 3;

	private static final long serialVersionUID = 1L;

	private boolean calculated = false;
	private int componentHeight = 0;
	private int pageTagAreaHeight = 0;

	private String columnUnitLabel;
	private double pixelPerColumnUnit;

	private double pixelPerSecond;
	private int maxSampleCount;
	private double timeZoomFactor;
	private float pageSize;
	private double pixelPerPage;
	private float samplingFrequency;
	private boolean pageLinesVisible;
	int timeScaleY;

	private TagRenderer tagRenderer;
	private TagDifferenceRenderer tagDifferenceRenderer;

	private SignalPlot plot;

	private SignalPlotPopupProvider signalPlotPopupProvider;
	private Font font;
	private FontMetrics fontMetrics;

	private Rectangle tempBounds = new Rectangle();

	private boolean compact;
	private SetCompactAction setCompactAction;

	public SignalPlotColumnHeader(SignalPlot plot) {
		super();
		this.plot = plot;
	}

	public void reset() {
		calculated = false;
	}

	private void calculate(Graphics2D g) {

		if (calculated) {
			return;
		}

		if (compact) {
			font = g.getFont().deriveFont(Font.PLAIN, 9);
		} else {
			font = g.getFont();
		}
		fontMetrics = g.getFontMetrics(font);

		samplingFrequency = plot.getSamplingFrequency();
		pixelPerSecond = plot.getPixelPerSecond();
		pageSize = plot.getPageSize();
		pixelPerPage = plot.getPixelPerPage();
		timeZoomFactor = plot.getTimeZoomFactor();
		maxSampleCount = plot.getMaxSampleCount();
		pageLinesVisible = plot.isPageLinesVisible();

		if (pixelPerSecond > 6) {
			pixelPerColumnUnit = pixelPerSecond;
			columnUnitLabel = "1 s";
		} else if (pixelPerSecond > (6F / 60F)) {
			pixelPerColumnUnit = pixelPerSecond * 60F;
			columnUnitLabel = "1 min";
		} else {
			pixelPerColumnUnit = pixelPerSecond * 3600F;
			columnUnitLabel = "1 h";
		}

		if (compact) {
			pageTagAreaHeight = 2 * fontMetrics.getAscent() + 2;
		} else {
			if (plot.getView().isComparingTags()) {
				pageTagAreaHeight = 40;
			} else {
				pageTagAreaHeight = Math.min(100, Math.max(40, plot.getDocument().getTagDocuments().size() * 20));
			}
		}

		componentHeight = TOP_OFFSET + pageTagAreaHeight + 2 + 3 + SignalPlot.SCALE_TO_SIGNAL_GAP;

		if (!compact) {
			componentHeight += (2 + fontMetrics.getAscent());
		}

		if (compact) {
			timeScaleY = TOP_OFFSET;
		} else {
			timeScaleY = TOP_OFFSET + pageTagAreaHeight / 2;
		}

		calculated = true;

	}

	public Rectangle getPixelPageTagBounds(SignalSelection tag, int tagCnt, int tagNumber, boolean comparing, Rectangle useRect) {

		Rectangle rect;
		if (useRect == null) {
			rect = new Rectangle();
		} else {
			rect = useRect;
		}

		double position = tag.getPosition();
		rect.x = ((int)(position * pixelPerSecond));
		if (rect.x > 0 && pageLinesVisible && pixelPerPage > 4) {  // avoid obscuring by page lines if visible
			int linePosition = (int)((int)((position / pageSize)) * pixelPerPage);
			if (linePosition == rect.x) {
				rect.x++;
			}
		}
		int endX = (int)((position+tag.getLength()) * pixelPerSecond);
		rect.width = endX-rect.x;

		if (compact) {
			rect.y = TOP_OFFSET;
			rect.height = pageTagAreaHeight;
		}
		else if (comparing) {

			// 0 - top, 1 - bottom, 2 - comparison
			int tagHeight = (pageTagAreaHeight-SignalPlot.COMPARISON_STRIP_HEIGHT) / 2;
			if (tagNumber == 0) {
				rect.y = TOP_OFFSET;
				rect.height = tagHeight;
			} else if (tagNumber == 1) {
				rect.y = TOP_OFFSET + SignalPlot.COMPARISON_STRIP_HEIGHT + tagHeight;
				rect.height = tagHeight;
			} else {
				rect.y = TOP_OFFSET + tagHeight + SignalPlot.COMPARISON_STRIP_MARGIN;
				rect.height = SignalPlot.COMPARISON_STRIP_HEIGHT - (2 * SignalPlot.COMPARISON_STRIP_MARGIN);
			}

		} else {

			float pixerPerTag = ((float) pageTagAreaHeight) / tagCnt;

			rect.y = TOP_OFFSET + (int)(((float) tagNumber) * pixerPerTag);
			if ((tagCnt % 2 == 0) && (tagNumber == (tagCnt/2))) {  // avoid obscuring axis
				rect.y++;
			}
			int endY = TOP_OFFSET + (int)(((float)(tagNumber+1)) * pixerPerTag);
			rect.height = endY - rect.y;

		}

		return rect;

	}

	protected void paintPageTags(Graphics2D g) {

		List<TagDocument> tagDocuments = plot.getDocument().getTagDocuments();
		int tagCnt = tagDocuments.size();
		if (tagCnt == 0) {
			return;
		}

		if (tagRenderer == null) {
			tagRenderer = new TagRenderer();
		}

		Rectangle clip = g.getClipBounds();

		StyledTagSet tagSet;
		SortedSet<Tag> tagsToDraw;
		SortedSet<Tag> activeTags = null;
		TagStyle style;
		SignalSelectionType type;
		Component tagRendererComponent;

		Rectangle tagBounds;

		int cnt = 0;
		float start = (float)(clip.x / pixelPerSecond);
		float end = (float)((clip.x+clip.width) / pixelPerSecond);

		boolean active;
		boolean showActivity = (tagDocuments.size() > 1);
		boolean comparing = plot.getView().isComparingTags();
		TagDocument[] comparedTags = null;

		if (comparing) {
			comparedTags = plot.getView().getComparedTags();
		}

		for (TagDocument tagDocument : tagDocuments) {
			active = (tagDocument == plot.getDocument().getActiveTag());
			if (compact && !active) {
				// in compact mode paint only the active tag
				continue;
			}
			if (comparing && tagDocument != comparedTags[0] && tagDocument != comparedTags[1]) {
				// in comparing mode paint only the compared tags
				continue;
			}
			tagSet = tagDocument.getTagSet();
			tagsToDraw = tagSet.getTagsBetween(start, end);
			if (active) {
				activeTags = tagsToDraw;
			}
			active = (!compact) && showActivity && active;
			for (Tag tag : tagsToDraw) {
				style = tag.getStyle();
				type = style.getType();
				if (type == SignalSelectionType.PAGE) {
					tagBounds = getPixelPageTagBounds(tag, tagCnt, cnt, comparing, tempBounds);
				} else {
					// channel and block tags drawn in the main plot
					continue;
				}

				tagRendererComponent = tagRenderer.getTagRendererComponent(tag.getStyle(), active, false);
				tagRendererComponent.setBounds(tagBounds);
				tagRendererComponent.paint(g.create(tagBounds.x, tagBounds.y, tagBounds.width, tagBounds.height));
			}
			cnt++;
		}

		// draw differences
		if (comparing && !compact) {
			if (tagDifferenceRenderer == null) {
				tagDifferenceRenderer = new TagDifferenceRenderer();
			}
			TagDifferenceSet differenceSet = plot.getView().getDifferenceSet();
			if (differenceSet != null) {
				SortedSet<TagDifference> differencesToDraw = differenceSet.getDifferencesBetween(start,end);
				for (TagDifference difference : differencesToDraw) {
					if (difference.getType() == SignalSelectionType.PAGE) {
						tagBounds = getPixelPageTagBounds(difference, 0, 2, true, tempBounds);
					} else {
						continue;
					}

					tagRendererComponent = tagDifferenceRenderer.getTagDifferenceRendererComponent(difference.getDifferenceType());
					tagRendererComponent.setBounds(tagBounds);
					tagRendererComponent.paint(g.create(tagBounds.x, tagBounds.y, tagBounds.width, tagBounds.height));

				}
			}
		}

		// draw page tag names
		if (activeTags != null) {

			g.setColor(Color.GRAY);
			g.setFont(font);

			for (Tag tag : activeTags) {
				style = tag.getStyle();
				type = style.getType();
				if (type == SignalSelectionType.PAGE) {


					int x = (int)(pixelPerSecond * tag.getPosition());
					String text = style.getDescriptionOrName();
					if (text.length() > 20) {
						text = text.substring(0, 18) + "...";
					}

					if (compact) {

						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g.drawString(text, x + 2, timeScaleY + (2 * fontMetrics.getAscent()));
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

					} else {

						g.drawString(text, x + 2, timeScaleY - (fontMetrics.getDescent() + 1));

					}

				} else {
					// do nothing
				}

			}
		}

	}

	private void paintSelectedTagSelectionBox(Graphics2D g, PositionedTag tagSelection) {

		SignalSelectionType type;
		Component tagRendererComponent;

		Rectangle tagBounds;

		int cnt = tagSelection.tagPositionIndex;
		TagDocument tagDocument = plot.getDocument().getTagDocuments().get(cnt);
		boolean active = (tagDocument == plot.getDocument().getActiveTag());
		if (compact && !active) {
			return;
		}
		type = tagSelection.tag.getType();
		if (type == SignalSelectionType.PAGE) {
			tagBounds = getPixelPageTagBounds(tagSelection.tag, plot.getDocument().getTagDocuments().size(), cnt, plot.getView().isComparingTags(), tempBounds);
		} else {
			tagBounds = null;
		}

		if (tagBounds != null) {
			tagRendererComponent = tagRenderer.getTagSelectionRendererComponent();
			tagRendererComponent.setBounds(tagBounds);
			tagRendererComponent.paint(g.create(tagBounds.x, tagBounds.y, tagBounds.width, tagBounds.height));
		}

	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D)gOrig;
		calculate(g);

		Point viewportPoint = plot.getViewport().getViewPosition();
		Dimension viewportSize = plot.getViewport().getExtentSize();
		Dimension size = getSize();

		Rectangle clip = g.getClipBounds();

		g.setColor(getBackground());
		g.fillRect(clip.x,clip.y,clip.width,clip.height);

		g.setColor(Color.WHITE);
		g.fillRect(clip.x, TOP_OFFSET, clip.width, pageTagAreaHeight);

		paintPageTags(g);

		int clipEndX = clip.x + clip.width - 1;

		size.height -= SignalPlot.SCALE_TO_SIGNAL_GAP;

		int i;
		int x;

		// this draws second ticks
		g.setColor(Color.GRAY);
		g.drawLine(viewportPoint.x, size.height-4, viewportPoint.x+viewportSize.width, size.height-4);
		int tickCnt = 1 + ((int)(((float)(viewportSize.width+1))  / pixelPerColumnUnit));
		for (i=0; i<tickCnt; i++) {
			x = viewportPoint.x + ((int)(i*pixelPerColumnUnit));
			g.drawLine(x, size.height-3, x , size.height-1);
		}

		if (pageLinesVisible && pixelPerPage > 4) {
			// this draws page boundaries
			int startPage = (int) Math.floor(clip.x / pixelPerPage);
			if (startPage == 0) {
				startPage++;
			}
			int endPage = (int) Math.ceil(clipEndX / pixelPerPage);

			g.setColor(Color.RED);
			for (i=startPage; i <= endPage; i++) {
				x = (int)(i * pixelPerPage);
				g.drawLine(x, TOP_OFFSET, x, TOP_OFFSET + pageTagAreaHeight - 1);
			}
		}

		// this draws time axis
		g.setColor(Color.GRAY);
		g.setFont(font);
		g.drawLine(clip.x, timeScaleY, clipEndX, timeScaleY);

		// XXX this must be offset to prevent parts of labels from disappearing
		// The offset of "5" is arbitrary
		// It could be recoded into a clener and well calculated offset
		int startUnit = (int) Math.max(0, Math.floor(clip.x / pixelPerColumnUnit) - 5);
		int endUnit = (int) Math.ceil(clipEndX / pixelPerColumnUnit) + 1;
		int second, minute, hour;
		Formatter formatter;
		String label;

		for (i=startUnit; i <= endUnit; i++) {
			x = (int)(i * pixelPerColumnUnit);
			if ((i % 10) != 0) {
				g.drawLine(x, (compact ? timeScaleY : timeScaleY-1), x, timeScaleY+1);
			} else {
				long firstSampleTimestamp = plot.getFirstSampleTimestamp();
				g.drawLine(x, (compact ? timeScaleY : timeScaleY-2), x, timeScaleY+2);
				second = (int) Math.round((i * pixelPerColumnUnit) / pixelPerSecond);
				if (firstSampleTimestamp > 0) {
					TimeZone timeZone = Calendar.getInstance().getTimeZone();
					// TimeZone API uses timestamp in milliseconds
					int offset = timeZone.getOffset(1000 * firstSampleTimestamp) / 1000;
					second = (int) ((firstSampleTimestamp + second + offset) % 86400);
				}
				hour = second / 3600;
				minute = (second % 3600) / 60;
				second = second % 60;
				formatter = new Formatter();
				if (firstSampleTimestamp > 0 || maxSampleCount / samplingFrequency > 3600) {
					formatter.format("%02d:", hour);
				}
				formatter.format("%02d:%02d", minute, second);
				label = formatter.toString();
				if (compact) {
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				}
				g.drawString(label, x + 3, timeScaleY + fontMetrics.getAscent() + 1);
				if (compact) {
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				}
			}
		}

		if (!compact) {
			g.setColor(Color.GRAY);
			g.drawString(columnUnitLabel, viewportPoint.x+3, size.height-6);
		}

		PositionedTag tagSelection = plot.getView().getTagSelection(plot);
		if (tagSelection != null) {
			paintSelectedTagSelectionBox(g, tagSelection);
		}

	}

	@Override
	public Dimension getPreferredSize() {
		calculate((Graphics2D) getGraphics());
		return new Dimension((int)(maxSampleCount*timeZoomFactor),componentHeight);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		if (plot.getView().isToolEngaged()) {
			return null;
		}
		if (signalPlotPopupProvider == null) {
			return null;
		}
		return signalPlotPopupProvider.getColumnHeaderPopupMenu();
	}

	@Override
	public String getToolTipText(MouseEvent event) {

		if (!plot.isTagToolTipsVisible()) {
			return null;
		}

		Point p = event.getPoint();

		PositionedTag tag = getSelectableTagAtPoint(p);
		if (tag == null) {
			return null;
		}
		String locationMessage = _R("T: {0} [P: {1}, B: {2}]",
									plot.toTimeSpace(p),
									plot.toPageSpace(p),
									plot.toBlockSpace(p));
		return plot.getTagToolTip(locationMessage, tag);
	}

	// note that this returns the first page tag encountered
	// this is based on assumption that page tags do not overlap
	public PositionedTag getSelectableTagAtPoint(Point point) {

		List<TagDocument> tagDocuments = plot.getDocument().getTagDocuments();

		int tagCnt = tagDocuments.size();
		if (tagCnt == 0) {
			return null;
		}
		if (point.y < TOP_OFFSET || point.y > (TOP_OFFSET+pageTagAreaHeight)) {
			return null;
		}

		SortedSet<Tag> tagSet = null;

		float time = plot.toTimeSpace(point);

		TagDocument pageTagDocument = null;
		Tag pageTag = null;

		boolean comparing = plot.getView().isComparingTags();
		TagDocument[] comparedTags = null;
		if (comparing) {
			comparedTags = plot.getView().getComparedTags();
		}
		int cnt = 0;
		Rectangle tagBounds;

		for (TagDocument tagDocument : tagDocuments) {
			if (compact && (tagDocument != plot.getDocument().getActiveTag())) {
				// in compact mode scan only the active tag
				continue;
			}
			if (comparing && tagDocument != comparedTags[0] && tagDocument != comparedTags[1]) {
				// in comparing mode scan only the compared tags
				continue;
			}

			tagSet = tagDocument.getTagSet().getTagsBetween(time, time);
			for (Tag tag : tagSet) {
				if (!tag.getStyle().isVisible())
					continue;
				if (tag.getStyle().getType() == SignalSelectionType.PAGE) {
					if (time >= tag.getPosition() && time < tag.getEndPosition()) {
						tagBounds = getPixelPageTagBounds(tag, tagCnt, cnt, comparing, tempBounds);
						if (tagBounds.contains(point)) {
							pageTag = tag;
							pageTagDocument = tagDocument;
							break;
						}
					}
				}
			}

			cnt++;
		}

		if (pageTag == null) {
			return null;
		} else {
			return new PositionedTag(pageTag, tagDocuments.indexOf(pageTagDocument));
		}

	}

	public SignalPlotPopupProvider getSignalViewPopupProvider() {
		return signalPlotPopupProvider;
	}

	public void setSignalViewPopupProvider(SignalPlotPopupProvider signalPlotPopupProvider) {
		this.signalPlotPopupProvider = signalPlotPopupProvider;
	}

	public SignalPlot getPlot() {
		return plot;
	}

	public boolean isCompact() {
		return compact;
	}

	public void setCompact(boolean compact) {
		if (this.compact != compact) {
			reset();
			revalidate();
			repaint();
			this.compact = compact;
			getSetCompactAction().putValue(AbstractAction.SELECTED_KEY, compact);
		}
	}

	public SetCompactAction getSetCompactAction() {
		if (setCompactAction == null) {
			setCompactAction = new SetCompactAction();
			setCompactAction.putValue(AbstractAction.SELECTED_KEY, isCompact());
		}
		return setCompactAction;
	}

	public class SetCompactAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SetCompactAction() {
			super(_("Compact mode"));
		}

		public void actionPerformed(ActionEvent ev) {

			Object source = ev.getSource();
			if (source instanceof ItemSelectable) {
				setSelected(((ItemSelectable) source).getSelectedObjects() != null);
			}

		}

		private void setSelected(boolean selected) {
			if (selected) {
				// remove any tag-selection if it a page tag and not in active document (otherwise it would disappear)
				PositionedTag pTag = plot.getView().getTagSelection(plot);
				if (pTag != null) {
					if (pTag.tag.getType().isPage()) {
						TagDocument activeDocument = plot.getDocument().getActiveTag();
						if (activeDocument == null || pTag.tagPositionIndex != plot.getDocument().getTagDocuments().indexOf(activeDocument)) {
							plot.getView().clearTagSelection();
						}
					}
				}
			}
			setCompact(selected);
			putValue(AbstractAction.SELECTED_KEY, selected);
		}

	}

}
