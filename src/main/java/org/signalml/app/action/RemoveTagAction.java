/* RemoveTagAction.java created 2007-10-15
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.domain.tag.StyledTagSet;
import org.springframework.context.support.MessageSourceAccessor;

/** RemoveTagAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RemoveTagAction extends AbstractFocusableSignalMLAction<TagFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RemoveTagAction.class);

	public RemoveTagAction(MessageSourceAccessor messageSource, TagFocusSelector tagFocusSelector) {
		super(messageSource, tagFocusSelector);
		setText("action.removeTag");
		setIconPath("org/signalml/app/icon/removetag.png");
		setToolTip("action.removeTagToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		TagFocusSelector tagFocusSelector = getActionFocusSelector();

		SignalDocument signalDocument = tagFocusSelector.getActiveSignalDocument();
		if (signalDocument == null) {
			return;
		}

		PositionedTag positionedTag = tagFocusSelector.getActiveTag();

		if (positionedTag == null) {
			logger.warn("Target tag doesn't exist");
			return;
		}

		TagDocument tagDocument = signalDocument.getTagDocuments().get(positionedTag.getTagPositionIndex());

		StyledTagSet tagSet = tagDocument.getTagSet();
		tagSet.removeTag(positionedTag.getTag());
		tagDocument.invalidate();

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTag() != null);
	}

}
