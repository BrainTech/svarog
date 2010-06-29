/* TagChannelSignalTool.java created 2007-10-13
 *
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.TagStyle;

/** TagChannelSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagChannelSignalTool extends SignalTool implements TaggingSignalTool {

	protected static final Logger logger = Logger.getLogger(TagChannelSignalTool.class);

	public Float startPosition;

	private SignalPlot plot;
	private TagStyle style;

	public TagChannelSignalTool(SignalView signalView) {
		super(signalView);
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	@Override
	public SignalSelectionType getTagType() {
		return SignalSelectionType.CHANNEL;
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {

			Object source = e.getSource();
			if (!(source instanceof SignalPlot)) {
				plot = null;
				return;
			}
			plot = (SignalPlot) source;
			style = signalView.getCurrentTagStyle(SignalSelectionType.CHANNEL);

			if (style != null && style.isMarker()) {
				markAt(e.getPoint());
			} else {
				startPosition = plot.toTimeSpace(e.getPoint());
			}

			engaged = true;
			e.consume();

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (startPosition != null) {
				tagTo(e.getPoint());
			}
			startPosition = null;
			engaged = false;
			plot = null;
			style = null;
			e.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (startPosition != null && SwingUtilities.isLeftMouseButton(e)) {
			Point point = e.getPoint();
			selectTo(point);
			Rectangle r = new Rectangle(point.x, point.y, 1, 1);
			((SignalPlot)e.getSource()).scrollRectToVisible(r);
		}
	}

	private void selectTo(Point point) {
		if (startPosition != null) {
			Float endPosition = plot.toTimeSpace(point);
			if (endPosition != null) {
				if (startPosition.equals(endPosition)) {
					signalView.clearSignalSelection();
				} else {
					Integer channel = plot.toChannelSpace(point);
					if (channel != null) {
						signalView.setSignalSelection(plot, plot.getChannelSelection(startPosition, endPosition, channel));
					}
				}
			}
		}
	}

	private void tagTo(Point point) {

		if (startPosition != null) {

			Float endPosition = plot.toTimeSpace(point);
			if (endPosition != null) {

				if (!startPosition.equals(endPosition)) {
					Integer channel = plot.toChannelSpace(point);
					if (channel != null) {

						TagDocument tagDocument = signalView.getDocument().getActiveTag();
						if (tagDocument != null) {

							if (style == null) {
								plot.eraseTagsFromSelection(tagDocument, plot.getChannelSelection(startPosition, endPosition, channel));
							} else {
								plot.tagChannelSelection(tagDocument, style, plot.getChannelSelection(startPosition, endPosition, channel), true);
							}

						}

					}
				}

			}

			signalView.clearSignalSelection();

		}

	}

	private void markAt(Point point) {

		Integer channel = plot.toChannelSpace(point);
		if (channel != null) {

			TagDocument tagDocument = signalView.getDocument().getActiveTag();
			if (tagDocument != null) {

				int sampleAtPoint = plot.toSampleSpace(point);

				float samplingFrequency = plot.getSamplingFrequency();
				float startPosition = sampleAtPoint / samplingFrequency;

				plot.tagChannelSelection(tagDocument, style, plot.getChannelSelection(startPosition, startPosition + 1/samplingFrequency, channel), true);

			}

		}

		signalView.clearSignalSelection();

	}

}
