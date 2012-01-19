/* ExportTagAction.java created 2007-11-18
 *
 */
package org.signalml.app.action.tag;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.components.dialogs.OptionPane;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * This is an abstract class representing an action for exporting tags to some other format.
 * All actions for exporting tags should extend this class and override its
 * {@link ExportTagAction#doExport(org.signalml.domain.tag.StyledTagSet, java.io.File, org.signalml.app.document.SignalDocument)}
 * method. Also the constructor should be overriden and the name of the specific
 * action should be set with the {@link AbstractSignalMLAction#setText(java.lang.String)}.
 * For reference and example please see {@link ExportEEGLabTagAction}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractExportTagAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(AbstractExportTagAction.class);
	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	public AbstractExportTagAction(TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(tagDocumentFocusSelector);
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
		doExport(tagDocument.getTagSet(), file, signalDocument);
	}

	/**
	 * Perform export to a given file. This method should be overridden in subclasses
	 * @param tagSet Tags to export
	 * @param file target file
	 * @param signalDocument
	 * @author Maciej Pawlisz
	 */
	protected abstract void doExport(StyledTagSet tagSet, File file, SignalDocument signalDocument);

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
