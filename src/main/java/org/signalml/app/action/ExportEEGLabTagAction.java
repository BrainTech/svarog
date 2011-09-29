package org.signalml.app.action;

import java.awt.Window;
import java.io.File;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.domain.tag.EEGLabTagExporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
/**
 * Export to EEGLab Action
 * @author Maciej Pawlisz, Titanis
 *
 */
public class ExportEEGLabTagAction extends ExportTagAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExportEEGLabTagAction.class);
	public ExportEEGLabTagAction(MessageSourceAccessor messageSource, TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(messageSource, tagDocumentFocusSelector);
		setText("action.exportEEGLabTag");
		setToolTip("action.exportEEGLabTagToolTip");
	}
	@Override
	protected void doExport(StyledTagSet tagSet, File file, SignalDocument signalDocument) {
		EEGLabTagExporter exporter = new EEGLabTagExporter();
		try {
			exporter.exportEEGLabTags(tagSet, file);
		} catch (SignalMLException ex) {
			logger.error("Failed to export tags", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}
	}
}