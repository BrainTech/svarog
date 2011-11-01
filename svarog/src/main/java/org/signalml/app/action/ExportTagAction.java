/* ExportTagAction.java created 2007-11-18
 *
 */
package org.signalml.app.action;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.domain.tag.LegacyTagExporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;

/** ExportTagAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportTagAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExportTagAction.class);

	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	public  ExportTagAction( TagDocumentFocusSelector tagDocumentFocusSelector) {
		super( tagDocumentFocusSelector);
		setText("action.exportTag");
		setToolTip("action.exportTagToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Import tag");

		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		if (tagDocument == null) {
			logger.warn("Target document doesn't exist");
			return;
		}
		SignalDocument signalDocument = tagDocument.getParent();

		File file;
		boolean hasFile = false;
		do {

			file = fileChooser.chooseExportTag(optionPaneParent);
			if (file == null) {
				return;
			}

			hasFile = true;

			if (file.exists()) {
				int res = OptionPane.showFileAlreadyExists(optionPaneParent);
				if (res != OptionPane.OK_OPTION) {
					hasFile = false;
				}
			}

		} while (!hasFile);
		doExport(tagDocument.getTagSet(),file,signalDocument);
	}
	
	/**
	 * Perform export to a given file. This method should be overridden in subclasses
	 * @param tagSet Tags to export
	 * @param file target file
	 * @param signalDocument
	 * @author Maciej Pawlisz
	 */
	protected void doExport(StyledTagSet tagSet,File file, SignalDocument signalDocument)
	{
		LegacyTagExporter exporter = new LegacyTagExporter();
		try {
			exporter.exportLegacyTags(tagSet, file, signalDocument.getChannelCount(), signalDocument.getSamplingFrequency());
		} catch (SignalMLException ex) {
			logger.error("Failed to import tags", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTagDocument() != null);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

}
