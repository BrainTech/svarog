package org.signalml.app.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import multiplexer.jmx.client.JmxClient;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.opensignal.SignalSource;
import org.signalml.app.worker.processes.ProcessManager;
import org.signalml.plugin.export.signal.Document;

/**
 * Action responsible for stopping a running OpenBCI.
 *
 * @author Tomasz Sawicki
 */
public class StopBCIAction extends AbstractFocusableSignalMLAction<DocumentFocusSelector> {

        /**
         * The viewer element manager.
         */
        private ViewerElementManager elementManager;

        /**
         * Default constructor.
         *
         * @param documentFocusSelector documentFocusSelector
         * @param elementManager elementManager
         */
        public StopBCIAction( DocumentFocusSelector documentFocusSelector, ViewerElementManager elementManager) {

                super( documentFocusSelector);
                this.elementManager = elementManager;

                // set text and tooltip if this will be used in gui
        }

        /**
         * On action performed stop recording, disconnect from openbci, then kill all processes.
         * Also, set signal source of the amplifier document (if it was opened) to monitor
         * so other amplifier documents can be opened.
         * 
         * @param e ActionEvent
         */
        @Override
        public void actionPerformed(ActionEvent e) {

                // stop recording
                DocumentManager manager = elementManager.getDocumentManager();
                for (int i = 0; i < manager.getDocumentCount(); i++) {
                        Document document = manager.getDocumentAt(i);
                        if (document instanceof MonitorSignalDocument) {
                                try {
                                        ((MonitorSignalDocument) document).stopMonitorRecording();
                                } catch (IOException ex) {
                                }
                                break;
                        }
                }

                // disconnect from openbci
                try {
                        JmxClient jmxClient = elementManager.getJmxClient();
                        if (jmxClient != null) {
                                jmxClient.shutdown();
                        }
                        elementManager.setJmxClient(null);
                } catch (InterruptedException ex) {
                }

                // kill all processes
                ProcessManager.getInstance().killAll();

                // change document type
                for (int i = 0; i < manager.getDocumentCount(); i++) {
                        Document document = manager.getDocumentAt(i);
                        if (document instanceof MonitorSignalDocument) {
                                OpenMonitorDescriptor descriptor = ((MonitorSignalDocument) document).getOpenMonitorDescriptor();
                                if (descriptor.getSignalSource().isAmplifier()) {
                                        descriptor.setSignalSource(SignalSource.OPENBCI);
                                }
                        }
                }
        }

        /**
         * Action is enabled if active document was opened when starting openbci
         * and connecting to the amplifier.
         */
        @Override
        public void setEnabledAsNeeded() {

                Document document = getActionFocusSelector().getActiveDocument();
                setEnabled(document instanceof MonitorSignalDocument
                        && ((MonitorSignalDocument) document).getOpenMonitorDescriptor().getSignalSource().isAmplifier());

                // this makes sense only if this action will be used in gui
        }
}
