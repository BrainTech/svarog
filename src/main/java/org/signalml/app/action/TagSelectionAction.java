/* PreciseSelectionAction.java created 2007-10-05
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalPlotFocusSelector;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.tag.TagStyleSelector;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;

/** PreciseSelectionAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagSelectionAction extends AbstractFocusableSignalMLAction<SignalPlotFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(TagSelectionAction.class);

	public TagSelectionAction(MessageSourceAccessor messageSource, SignalPlotFocusSelector signalPlotFocusSelector) {
		super(messageSource, signalPlotFocusSelector);
		setText("action.tagSelection");
		setToolTip("action.tagSelectionToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		SignalPlot plot = getActionFocusSelector().getActiveSignalPlot();
		if (plot == null) {
			logger.warn("Target plot doesn't exist");
			return;
		}

		TagDocument tagDocument = plot.getDocument().getActiveTag();

		if (tagDocument == null) {
			logger.warn("Target tag document doesn't exist");
			return;
		}
		SignalView view = plot.getView();

		SignalSelection selection = view.getSignalSelection(plot);
		if (selection == null) {
			return;
		}

		Object source = e.getSource();
		if (!(source instanceof TagStyleSelector)) {
			logger.warn("Event source is not a tag style selector");
			return;
		}
		TagStyle style = ((TagStyleSelector) source).getTagStyle();

		// if style is null then tags will be erased
		if (style == null) {
			plot.eraseTagsFromSelection(tagDocument, selection);
		} else {

			if (selection.getType() == SignalSelectionType.CHANNEL && style.isMarker()) {

				double centerPoint = selection.getCenterPosition();

				float samplingFrequency = plot.getSamplingFrequency();

				int sampleAtPoint = (int)(centerPoint * samplingFrequency);

				float startPosition = sampleAtPoint / samplingFrequency;

				selection = plot.getChannelSelection(startPosition, startPosition + 1/samplingFrequency, selection.getChannel());

			}

			plot.tagSelection(tagDocument, style, selection, false);

		}

	}

}
