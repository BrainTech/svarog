package org.signalml.app.action.signal;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.plugin.export.signal.SignalTool;

/**
 * Action to change currently selected signal tool.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ChangeToolAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ChangeToolAction.class);

	private final Class<? extends SignalTool> toolClass;

	public ChangeToolAction(String text, Class<? extends SignalTool> toolClass, SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(text);
		this.toolClass = toolClass;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Tool change");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		signalDocument.getSignalView().setCurrentSignalToolClass(toolClass);
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

}
