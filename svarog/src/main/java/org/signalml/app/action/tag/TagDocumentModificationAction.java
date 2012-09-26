package org.signalml.app.action.tag;

import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TagDocumentFocusSelector;

/**
 * An action concerning one active tag document.
 * Supports enabling the action when at least one tag document
 * is open.
 *
 * @author Piotr Szachewicz
 */
public abstract class TagDocumentModificationAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	public TagDocumentModificationAction(TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(tagDocumentFocusSelector);
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTagDocument() != null);
	}

}
