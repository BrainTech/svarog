/* OpenTagAction.java created 2007-10-07
 *
 */
package org.signalml.app.action.tag;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;

/** OpenTagAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenTagAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OpenTagAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;
	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	public OpenTagAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Open Tag..."));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open a tag file for this signal"));
		setMnemonic(KeyEvent.VK_O);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Open tag");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		File file = fileChooser.chooseOpenTag(optionPaneParent);
		if (file == null) {
			return;
		}

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setType(ManagedDocumentType.TAG);
		ofd.setMakeActive(true);

		boolean legTag = true;
		LegacyTagImporter importer = new LegacyTagImporter();
		StyledTagSet tagSet = null;
		try {
			tagSet = importer.importLegacyTags(file, signalDocument.getSamplingFrequency());
		} catch (SignalMLException ex) {
			legTag = false;
			logger.info("Failed to import tags, not a legacy tag");
		}

		TagDocument tagDocument = null;
		try {
			tagDocument = new TagDocument(tagSet);
		} catch (SignalMLException ex) {
			legTag = false;
			logger.info("Failed to create document, not a legacy tag");
		}

		if (legTag) {
			ofd.getTagOptions().setExistingDocument(tagDocument);
		} else {
			ofd.setFile(file);
		}

		ofd.getTagOptions().setParent(signalDocument);

		documentFlowIntegrator.maybeOpenDocument(ofd);
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(isSignalDocumentOfflineSignalDocument(getActionFocusSelector().getActiveSignalDocument()));
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
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
