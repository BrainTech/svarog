/* NewTagAction.java created 2007-10-14
 *
 */
package org.signalml.app.action.tag;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.tag.NewTagDescriptor;
import org.signalml.app.model.tag.NewTagDescriptor.NewTagTypeMode;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.tag.NewTagDialog;
import org.signalml.domain.tag.StyledTagSet;
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
		setMnemonic(KeyEvent.VK_N);
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
			else if (mode == NewTagTypeMode.DEFAULT_PRESET)
			{
				tagDocument = TagDocument.getDefaultPreset(descriptor.getPageSize(), descriptor.getBlocksPerPage(), descriptor.getDefaultPresetIndex());
			}
			else if (mode == NewTagTypeMode.PRESET) {
				/* TagStyle preset manager might need to get refactored,
				* now it saves full StyledTagSets, instead of TagStyle objects
				* but that most likely would break backward compatability
				* with saved user tagsstyles
				* it's not a problem if user saves a tag preset with tags which 
				* were open in Svarog, but to prevent them from appearing
				* during creation of new tag, we do this sanitization.
				*/
				StyledTagSet unsanitized = descriptor.getTagStylesPreset();
				StyledTagSet sanitized = new StyledTagSet(unsanitized.getTagStyles().clone(), unsanitized.getPageSize(), unsanitized.getBlocksPerPage());
				tagDocument = new TagDocument(sanitized);
			}
			else if (mode == NewTagTypeMode.FROM_FILE) {
				tagDocument = TagDocument.getStylesFromFileDocument(descriptor.getFile(), descriptor.getPageSize(), descriptor.getBlocksPerPage());
			} else {
				throw new SanityCheckException("Unknown mode [" + mode + "]");
			}
		} catch (SignalMLException ex) {
			logger.error("Failed to create document", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to create document - i/o exception", ex);
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		}

		ofd.getTagOptions().setParent(signalDocument);
		ofd.getTagOptions().setExistingDocument(tagDocument);

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

	public NewTagDialog getNewTagDialog() {
		return newTagDialog;
	}

	public void setNewTagDialog(NewTagDialog newTagDialog) {
		this.newTagDialog = newTagDialog;
	}

}
