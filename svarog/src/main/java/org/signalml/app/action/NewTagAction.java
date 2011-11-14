/* NewTagAction.java created 2007-10-14
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.NewTagDescriptor;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.NewTagDescriptor.NewTagTypeMode;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.NewTagDialog;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;

/** NewTagAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewTagAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(NewTagAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;
	private NewTagDialog newTagDialog;

	public NewTagAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("New Tag"));
		setIconPath("org/signalml/app/icon/filenew.png");
		setToolTip(_("Create a new tag for this signal"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("New tag");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setType(ManagedDocumentType.TAG);
		ofd.setMakeActive(true);

		NewTagDescriptor descriptor = new NewTagDescriptor();
		descriptor.setPageSize(signalDocument.getPageSize());
		descriptor.setBlocksPerPage(signalDocument.getBlocksPerPage());
		boolean ok = newTagDialog.showDialog(descriptor, 0.5, 0.2);
		if (!ok) {
			return;
		}

		TagDocument tagDocument = null;

		try {
			NewTagTypeMode mode = descriptor.getMode();
			if (mode == NewTagTypeMode.EMPTY) {
				tagDocument = new TagDocument(descriptor.getPageSize(), descriptor.getBlocksPerPage());
			}
			else if (mode == NewTagTypeMode.DEFAULT_SLEEP) {
				tagDocument = TagDocument.getNewSleepDefaultDocument(descriptor.getPageSize(), descriptor.getBlocksPerPage());
			}
			else if (mode == NewTagTypeMode.PRESET) {
				tagDocument = new TagDocument(descriptor.getTagStylesPreset());
			}
			else if (mode == NewTagTypeMode.FROM_FILE) {
				tagDocument = TagDocument.getStylesFromFileDocument(descriptor.getFile(), descriptor.getPageSize(), descriptor.getBlocksPerPage());
			} else {
				throw new SanityCheckException("Unknown mode [" + mode + "]");
			}
		} catch (SignalMLException ex) {
			logger.error("Failed to create document", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to create document - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}

		ofd.getTagOptions().setParent(signalDocument);
		ofd.getTagOptions().setExistingDocument(tagDocument);

		documentFlowIntegrator.maybeOpenDocument(ofd);
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	public NewTagDialog getNewTagDialog() {
		return newTagDialog;
	}

	public void setNewTagDialog(NewTagDialog newTagDialog) {
		this.newTagDialog = newTagDialog;
	}

}
