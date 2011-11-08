/* ActivateTagAction.java created 2007-10-16
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.TagFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SignalView;

/** ActivateTagAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActivateTagAction extends AbstractFocusableSignalMLAction<TagFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ActivateTagAction.class);

	private ActionFocusManager actionFocusManager;

	public  ActivateTagAction( ActionFocusManager actionFocusManager, TagFocusSelector tagFocusSelector) {
		super( tagFocusSelector);
		this.actionFocusManager = actionFocusManager;
		setText(_("Show tag"));
		setIconPath("org/signalml/app/icon/activate.png");
		setToolTip(_("Show tag in viewer"));
	}

	public  ActivateTagAction( ActionFocusManager actionFocusManager) {
		this( actionFocusManager, actionFocusManager);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Activate focused tag");

		TagFocusSelector tagFocusSelector = getActionFocusSelector();

		SignalDocument signalDocument = tagFocusSelector.getActiveSignalDocument();
		if (signalDocument == null) {
			return;
		}

		PositionedTag positionedTag = tagFocusSelector.getActiveTag();
		if (positionedTag == null) {
			return;
		}

		SignalView signalView = (SignalView) signalDocument.getDocumentView();

		signalView.setTagSelection(signalView.getMasterPlot(),positionedTag);
		actionFocusManager.setActiveDocument(signalDocument);
		signalView.showTag(positionedTag.getTag());

	}

	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTag() != null);
	}

}
