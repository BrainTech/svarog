package org.signalml.app.action.tag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.File;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.domain.tag.EEGLabTagExporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;

/**
 * Export to EEGLab Action
 * @author Maciej Pawlisz, Titanis
 *
 */
public class ExportEEGLabTagAction extends AbstractExportTagAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExportEEGLabTagAction.class);
	public ExportEEGLabTagAction(TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(tagDocumentFocusSelector);
		setText(_("Export to EEGLab..."));
		setToolTip(_("Export tags to EEGLab events ASCII file format"));
		setMnemonic(KeyEvent.VK_E);
	}
	@Override
	protected void doExport(StyledTagSet tagSet, File file, SignalDocument signalDocument) {
		EEGLabTagExporter exporter = new EEGLabTagExporter();
		try {
			exporter.exportEEGLabTags(tagSet, file);
		} catch (SignalMLException ex) {
			logger.error("Failed to export tags", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		}
	}
}