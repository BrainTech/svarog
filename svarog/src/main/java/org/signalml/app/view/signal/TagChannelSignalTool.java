/* TagChannelSignalTool.java created 2007-10-13
 *
 */

package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.InvalidClassException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.AbstractSignalTool;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;

/** TagChannelSignalTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagChannelSignalTool extends AbstractSignalTool implements TaggingSignalTool {

	protected static final Logger logger = Logger.getLogger(TagChannelSignalTool.class);

	public Float startPosition;

	/**
	 * The channel on which the current selection was started.
	 */
	private Integer channel = null;

	private SignalPlot plot;
	private ExportedTagStyle style;

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

			Point point = e.getPoint();
			startPosition = plot.toTimeSpace(point);
			channel = plot.toChannelSpace(point);

			setEngaged(true);
			e.consume();

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {

			style = getSignalView().getCurrentTagStyle(SignalSelectionType.CHANNEL);

			if (startPosition != null) {
				try {
					tagTo(e.getPoint());
				} catch (InvalidClassException ex) {
					logger.error(ex, ex);
				}
			}
			startPosition = null;
			setEngaged(false);
			plot = null;
			style = null;
			e.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (startPosition != null && SwingUtilities.isLeftMouseButton(e)) {
			Point point = e.getPoint();
			try {
				selectTo(point);
			} catch (InvalidClassException ex) {
				logger.error(ex, ex);
			}
			Rectangle r = new Rectangle(point.x, point.y, 1, 1);
			((SignalPlot)e.getSource()).scrollRectToVisible(r);
		}
	}

	private void selectTo(Point point) throws InvalidClassException {
		if (startPosition != null) {
			Float endPosition = plot.toTimeSpace(point);
			if (endPosition != null) {
				if (startPosition.equals(endPosition)) {
					getSignalView().clearSignalSelection();
				} else {

					int currentChannel = plot.toChannelSpace(point);

					if (Math.abs((currentChannel - channel)) > 0)
						currentChannel = SignalSelection.CHANNEL_NULL;
					else
						currentChannel = channel;

					getSignalView().setSignalSelection(plot,plot.getChannelSelection(startPosition, endPosition, currentChannel));

				}
			}
		}
	}

	private void tagTo(Point point) throws InvalidClassException {
		if (startPosition != null) {
			Float endPosition = plot.toTimeSpace(point);
			if (endPosition != null) {

				if (!startPosition.equals(endPosition)) {
					Integer currentChannel = plot.toChannelSpace(point);
					if (currentChannel != null) {

						ExportedTagDocument tagDocument = getSignalView().getDocument().getActiveTag();
						if (tagDocument != null) {

							if (Math.abs((currentChannel - channel)) > 0)
								currentChannel = SignalSelection.CHANNEL_NULL;
							else
								currentChannel = channel;

							if (style == null) {
								plot.eraseTagsFromSelection(tagDocument, plot.getChannelSelection(startPosition, endPosition, currentChannel));
							} else {
								plot.tagChannelSelection(tagDocument, style, plot.getChannelSelection(startPosition, endPosition, currentChannel), true);
							}

						}

					}
				}

			}

			getSignalView().clearSignalSelection();
		}
	}

}
