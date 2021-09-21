package org.signalml.app.worker.monitor.recording;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.OpenTagDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.Helper;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.CheckSavingSignalStatus;
import org.signalml.app.worker.monitor.messages.SavingSignalError;
import org.signalml.app.worker.monitor.messages.SavingSignalStatus;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.util.Util;

/**
 * Thread for periodic checking of saving status, for given session ID.
 *
 * @author piotr.rozanski@braintech.pl
 */
class RecordingStateChecker extends Thread {

	private static final Logger logger = Logger.getLogger(RecordingStateChecker.class);

	private final String savingSessionID;
	private final RecordingStateReference state;

	public RecordingStateChecker(String savingSessionID, RecordingStateReference state) {
		super(savingSessionID);
		this.savingSessionID = savingSessionID;
		this.state = state;
		setDaemon(true);
	}

	@Override
	public void run() {
		final CheckSavingSignalStatus checkMessage = new CheckSavingSignalStatus(savingSessionID);
		RecordingState lastState = null;
		while (RecordingState.FINISHED != lastState) {
			try {
				sleep(500);
			} catch (InterruptedException ex) {
				// does not matter
			}
			try {
				BaseMessage response = Helper.sendRequestAndParseResponse(
					checkMessage,
					Helper.getOpenBCIIpAddress(), Helper.getOpenbciPort(),
					null // any type
				);
				if (response instanceof SavingSignalStatus) {
					SavingSignalStatus status = (SavingSignalStatus) response;
					lastState = RecordingState.valueOf(status.status.toUpperCase());
				} else if (response instanceof SavingSignalError) {
					SavingSignalError error = (SavingSignalError) response;
					logger.error(_("recording finished with error: ") + error.details);
                                        String error_text = "";
                                        for (Object err_text: error.details.values())
                                                {
                                                   try{
                                                       error_text += (String)err_text;
                                                   }
                                                   catch(ClassCastException e)
                                                   {
                                                       error_text += err_text.toString();
                                                   }
                                                   error_text += "\n";
                                                }
                                        error_text += _("Signal preceeding this error message is not lost.") + "\n";
                                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                                        error_text += _("Error received at ") + timeStamp;

                                        Dialogs.showError(_("Signal saving error"), error_text);
					lastState = RecordingState.FINISHED;
				} else {
					logger.warn("received unexpected response while checking recording status");
					continue;
				}

				state.set(lastState);
				if (lastState == RecordingState.FINISHED) {
					maybeOpenDocument();
					break;
				}
			} catch (OpenbciCommunicationException ex) {
				// communication error, will try again
			}
		}
	}
	
	private void maybeOpenDocument() {
		DocumentFlowIntegrator integrator = SvarogApplication.getSharedInstance().getViewerElementManager().getDocumentFlowIntegrator();
		MonitorSignalDocument activeSignalDocument = (MonitorSignalDocument) integrator.getActionFocusManager().getActiveDocument();
		MonitorRecordingDescriptor monitorRecordingDescriptor = activeSignalDocument.getExperimentDescriptor().getMonitorRecordingDescriptor();
		String rawSignalFile = monitorRecordingDescriptor.getSignalRecordingFilePath();
		String targetSignalPath = monitorRecordingDescriptor.getSignalRecordingFilePath();
		OpenDocumentDescriptor documentDescriptor = getOpenDocumentDescriptor(rawSignalFile);
		
		if (documentDescriptor != null) {
			SignalDocument signalDocument = (SignalDocument) integrator.maybeOpenDocument(documentDescriptor);
			OpenDocumentDescriptor tagDocumentDescriptor = getTagDocumentDescriptor(rawSignalFile);
			if (tagDocumentDescriptor != null) {
				OpenTagDescriptor openTagDescriptor = new OpenTagDescriptor();
				openTagDescriptor.setParent(signalDocument);
				tagDocumentDescriptor.setTagOptions(openTagDescriptor);
				integrator.maybeOpenDocument(tagDocumentDescriptor);
			}
		}
	}
	
	private OpenDocumentDescriptor getOpenDocumentDescriptor(String rawSignalFilePath) {
		File signalFile = new File(rawSignalFilePath + ".obci.raw");
		RawSignalDescriptor openSignalDescriptor = getOpenSignalDescriptor(signalFile);
		File f = new File(rawSignalFilePath + ".mkv");
		if(f.exists() && !f.isDirectory()) { 
			openSignalDescriptor.setVideoFileName(FilenameUtils.getBaseName(rawSignalFilePath) + ".mkv");
		}
		if (openSignalDescriptor != null) {
			OpenDocumentDescriptor documentDescriptor = new OpenDocumentDescriptor();
			documentDescriptor.setFile(signalFile);
			documentDescriptor.setType(ManagedDocumentType.SIGNAL);
			documentDescriptor.setMakeActive(true);
			documentDescriptor.setOpenSignalDescriptor(openSignalDescriptor);
			return documentDescriptor;
		} else {
			return null;
		}
	}
	
	private RawSignalDescriptor getOpenSignalDescriptor(File signalFile) {
		RawSignalDescriptorReader reader = new RawSignalDescriptorReader();
		RawSignalDescriptor openSignalDescriptor = new RawSignalDescriptor();
		try {
			openSignalDescriptor = reader.readDocument(Util.changeOrAddFileExtension(signalFile, "xml"));
			openSignalDescriptor.setCorrectlyRead(true);
			DocumentFlowIntegrator integrator = SvarogApplication.getSharedInstance().getViewerElementManager().getDocumentFlowIntegrator();
			MonitorSignalDocument activeSignalDocument = (MonitorSignalDocument) integrator.getActionFocusManager().getActiveDocument();
			
			openSignalDescriptor.setMontage(activeSignalDocument.getMontage());
			openSignalDescriptor.setTryToOpenTagDocument(true);
			return openSignalDescriptor;
		} catch (Exception e) {
			Dialogs.showError(_("There was an error while reading the XML manifest."));
			return null;
		}
	}
	
	
	private OpenDocumentDescriptor getTagDocumentDescriptor(String rawSignalFile) {
		boolean tagFileExists = false;
		File tagFile = null;
		for (String ext : ManagedDocumentType.TAG.getAllFileExtensions()) {
			tagFile = new File(rawSignalFile + ".obci." +ext);
			if (tagFile.exists()) {
				tagFileExists = true;
				break;
			}
		}
		if (!tagFileExists) {
			return null;
		}

		OpenDocumentDescriptor tagDocumentDescriptor = new OpenDocumentDescriptor();
		tagDocumentDescriptor.setType(ManagedDocumentType.TAG);
		tagDocumentDescriptor.setFile(tagFile);
		return tagDocumentDescriptor;
	}
}
