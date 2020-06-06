package org.signalml.app.action.document.monitor;
import java.awt.Container;
import org.apache.log4j.Logger;
import org.signalml.app.document.MonitorSignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;

/**
 *
 * @author Marian Dovgialo
 */
public class StartMonitorRecordingWorker extends SwingWorkerWithBusyDialog<Void, Void> {
        MonitorSignalDocument monitorSignalDocument;
        protected static final Logger logger = Logger.getLogger(StartMonitorRecordingWorker.class);

    
   
    	public StartMonitorRecordingWorker(Container parentContainer, MonitorSignalDocument monitorSignalDocument) {
		super(parentContainer);
                this.monitorSignalDocument = monitorSignalDocument;
		getBusyDialog().setText(_("Starting saving."));
		getBusyDialog().setCancellable(false);
	}
        
    	@Override
	protected Void doInBackground() throws Exception {

		showBusyDialog();
                try {
                monitorSignalDocument.startMonitorRecording();
                }
                catch (OpenbciCommunicationException ex) {
				ex.showErrorDialog(_("Failed to start recording"));
				logger.error("Failed to start recording", ex);
		
            }
            return null;
        }
}
