package org.signalml.app.worker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingWorker;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.ProgressState;
import org.signalml.app.worker.amplifiers.AmplifierInstance;
import org.signalml.app.worker.processes.ProcessManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Class responsible for starting all OpenBCI modules and
 * sending configuration data to the hashtable.
 *
 * @author Tomasz Sawicki
 */
public class OpenBCIManager extends SwingWorker<ProgressState, ProgressState> implements PropertyChangeListener {

        private final int CONNECTION_TIMEOUT = 100;
        private final int CONNECTION_TRYOUT = 3;

        /**
         * The message source.
         */
        private MessageSourceAccessor messageSource;

        /**
         * The element manager.
         */
        private ViewerElementManager elementManager;

        /**
         * Amplifier to connect to.
         */
        private AmplifierInstance amplifierInstance;

        /**
         * The process manager.
         */
        private ProcessManager processManager;

        /**
         * Configuration data to be sent to OpenBCI.
         */
        private HashMap<String, String> configurationData;

        /**
         * A lock used to syncronize execution of this worker with events
         * called by sub-workers.
         */
        private final Object lock = new Object();

        /**
         * Default constructor sets values of fields.
         *
         * @param messageSource {@link #messageSource}
         * @param elementManager {@link #elementManager}
         */
        public OpenBCIManager(MessageSourceAccessor messageSource, ViewerElementManager elementManager) {

                this.messageSource = messageSource;
                this.elementManager = elementManager;

                processManager = new ProcessManager();
                processManager.addPropertyChangeListener(this);
        }

        /**
         * Kills all processes created by this object.
         * Should be called when main program is exiting.
         */
        public void killAllProcesses() {
                
                processManager.killAll();
        }

        /**
         * Performs all the execution.
         *
         * @return execution result
         * @throws Exception when interrupted etc
         */
        @Override
        protected ProgressState doInBackground() throws Exception {

                return null;
        }

        /**
         * Publishes progress status to window performing operation.
         *
         * @param chunks chunks
         */
        @Override
        protected void process(List<ProgressState> chunks) {

                for (ProgressState progressState : chunks) {
                        firePropertyChange("progressState", null, progressState);
                }
        }

        /**
         * Processes events from sub-workers.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if ("processEnded".equals(evt.getPropertyName())) {

                } else if("sendingDone".equals(evt.getPropertyName())) {

                } else if ("jmxConnection".equals(evt.getPropertyName())) {
                        
                }
        }
}