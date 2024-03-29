package org.signalml.app.action.document;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.document.opensignal.OpenSignalWizardDialog;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.monitor.MonitorWorker;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;

public class OpenSignalWizardAction extends AbstractSignalMLAction implements PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(OpenSignalWizardAction.class);

	private DocumentFlowIntegrator documentFlowIntegrator;
	private ViewerElementManager viewerElementManager;
	private OpenDocumentDescriptor openDocumentDescriptor;

	private SignalDocument signalDocument;
	private final String selectedSourceTab;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager to be used by this action.
	 * @param selectedSourceTab name of the tab to be made active by default (if null, first tab will be active)
	 */
	public OpenSignalWizardAction(ViewerElementManager viewerElementManager, String selectedSourceTab) {
		super();
		this.documentFlowIntegrator = viewerElementManager.getDocumentFlowIntegrator();
		this.viewerElementManager = viewerElementManager;
		this.selectedSourceTab = selectedSourceTab;
		setText(_(selectedSourceTab != null ? selectedSourceTab : "Open"));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open signal and set montage for it"));
		setMnemonic(KeyEvent.VK_O);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		openDocumentDescriptor = new OpenDocumentDescriptor();

		OpenSignalWizardDialog openSignalWizardDialog = new OpenSignalWizardDialog(
			viewerElementManager, selectedSourceTab
		);
		boolean ok = openSignalWizardDialog.showDialog(openDocumentDescriptor, true);
		if (!ok) {
			return;
		}
		openDocumentDescriptor.getOpenSignalDescriptor();
		signalDocument = (SignalDocument) documentFlowIntegrator.maybeOpenDocument(openDocumentDescriptor);

		if (signalDocument instanceof MonitorSignalDocument) {
			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;
			MonitorWorker monitorWorker = monitorSignalDocument.getMonitorWorker();
			monitorWorker.addPropertyChangeListener(this);
		} else if (openDocumentDescriptor.getOpenSignalDescriptor().isTryToOpenTagDocument()) {
			//for non-monitors
			tryToOpenTagDocument();
		}

	}

	/**
	 * Tries to open a tag document with the name equal to the signal document
	 * file name but with tag document extension.
	 */
	protected void tryToOpenTagDocument() {
		File signalFile = openDocumentDescriptor.getFile();
		File tagFile = null;

		boolean tagFileExists = false;
		for (String ext: ManagedDocumentType.TAG.getAllFileExtensions()) {
			tagFile = Util.changeOrAddFileExtension(signalFile, ext);
			if (tagFile.exists()) {
				tagFileExists = true;
				break;
			}
		}

		if (!tagFileExists)
			return;

		OpenDocumentDescriptor tagDocumentDescriptor = new OpenDocumentDescriptor();
		tagDocumentDescriptor.setType(ManagedDocumentType.TAG);
		
		//try loading legacy tag first
		try {
			LegacyTagImporter importer = new LegacyTagImporter();
			//if Tags are not legacy tags and are not importable then importer will throw a SignalMLException
			StyledTagSet tagSet = importer.importLegacyTags(tagFile, signalDocument.getSamplingFrequency());
			TagDocument tagDocument = new TagDocument(tagSet);
			tagDocument.setBackingFile(tagFile);
			//override opening document descriptor with existing tag document one which we just created
			tagDocumentDescriptor.getTagOptions().setExistingDocument(tagDocument);
		} catch (SignalMLException ex) {
			logger.info("Failed to import tags, not a legacy tag, loading as xml tag");
			tagDocumentDescriptor.setFile(tagFile);
		}

		tagDocumentDescriptor.getTagOptions().setParent(signalDocument);
		documentFlowIntegrator.maybeOpenDocument(tagDocumentDescriptor);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (signalDocument != null && MonitorWorker.OPENING_MONITOR_CANCELLED.equals(evt.getPropertyName())) {
			try {
				documentFlowIntegrator.closeDocument(signalDocument, true, true);
			} catch (IOException e) {
				logger.error("", e);
			} catch (SignalMLException e) {
				logger.error("", e);
			}
		}
	}

}
