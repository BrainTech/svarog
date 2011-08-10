package org.signalml.app.worker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingWorker;
import multiplexer.jmx.client.JmxClient;
import org.signalml.app.action.ConnectMultiplexerActionNoMetadata;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.ProgressDialog;
import org.signalml.app.view.element.ProgressState;
import org.signalml.app.worker.processes.OpenBCIModuleData;
import org.signalml.app.worker.processes.ProcessExitData;
import org.signalml.app.worker.processes.ProcessManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Class responsible for starting all OpenBCI modules, sending configuration
 * data to the hashtable and connecting to OpenBCI.
 *
 * @author Tomasz Sawicki
 */
public class OpenBCIManager extends SwingWorker<ProgressState, ProgressState> implements PropertyChangeListener {

        public static final String MX_ID = "mx";
        public static final String HASHTABLE_ID = "hashtable";
        public final int MAX_PROGRESS = 7;
        /**
         * The message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * The element manager.
         */
        private ViewerElementManager elementManager;
        /**
         * Connection descriptor.
         */
        private AmplifierConnectionDescriptor descriptor;
        /**
         * The process manager.
         */
        private ProcessManager processManager;
        /**
         * Connect action.
         */
        private ConnectMultiplexerActionNoMetadata connectAction;
        /**
         * Configuration worker.
         */
        private BCIConfigurationWorker configurationWorker;
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
         * @param descriptor {@link #descriptor}
         */
        public OpenBCIManager(MessageSourceAccessor messageSource, ViewerElementManager elementManager, AmplifierConnectionDescriptor descriptor) {

                this.messageSource = messageSource;
                this.elementManager = elementManager;
                this.descriptor = descriptor;

                processManager = ProcessManager.getInstance();
                processManager.addPropertyChangeListener(this);

                connectAction = new ConnectMultiplexerActionNoMetadata(elementManager);
                connectAction.addPropertyChangeListener(this);
        }

        /**
         * Extracts data to send from {@link #descriptor}.
         *
         * @return data to send
         */
        private HashMap<String, String> getConfigurationDataToSend() {

                float[] descrGain = descriptor.getOpenMonitorDescriptor().getCalibrationGain();
                float[] descrOffset = descriptor.getOpenMonitorDescriptor().getCalibrationOffset();

                Float[] gain = new Float[descriptor.getOpenMonitorDescriptor().getChannelCount()];
                Float[] offset = new Float[descriptor.getOpenMonitorDescriptor().getChannelCount()];

                for (int i = 0; i < descriptor.getOpenMonitorDescriptor().getChannelCount(); i++) {

                        gain[i] = new Float(descrGain[i]);
                        offset[i] = new Float(descrOffset[i]);
                }


                ConfigurationDataFormatter dataFormatter = new ConfigurationDataFormatter(
                        descriptor.getOpenMonitorDescriptor().getChannelCount(),
                        gain, offset,
                        new Integer((int) descriptor.getOpenMonitorDescriptor().getSamplingFrequency().floatValue()),
                        descriptor.getAmplifierInstance().getDefinition().getChannelNumbers().toArray(new Integer[0]),
                        descriptor.getOpenMonitorDescriptor().getChannelLabels(),
                        descriptor.getAmplifierInstance().getDefinition().getAmplifierNull());

                return dataFormatter.formatData();
        }

        /**
         * Gets openBCI modules data.
         *
         * @return openBCI modules data
         * @throws Exception when some data is missing
         */
        private HashMap<String, OpenBCIModuleData> getModulesData() throws Exception {

                HashMap<String, OpenBCIModuleData> modulesData = elementManager.getOpenBCIModulePresetManager().getAllModulesData();
                String driverModuleName = descriptor.getAmplifierInstance().getDefinition().getModuleName();
                String address = descriptor.getAmplifierInstance().getAddress();

                if (!modulesData.containsKey(MX_ID)) {
                        throw new Exception(MX_ID);
                } else if (!modulesData.containsKey(HASHTABLE_ID)) {
                        throw new Exception(HASHTABLE_ID);               
                } else if (!modulesData.containsKey(driverModuleName)) {
                        throw new Exception(driverModuleName);
                }

                modulesData.get(driverModuleName).replaceAddress(address);
                return modulesData;
        }

        /**
         * Cancels execution, kills all created processes and disconnects from
         * the multiplexer.
         */
        public void cancel() {

                connectAction.cancel();
                if (configurationWorker != null) {
                        configurationWorker.cancel(true);
                }
                processManager.killAll();
                try {
                        JmxClient jmxClient = elementManager.getJmxClient();
                        if (jmxClient != null) {
                                jmxClient.shutdown();
                        }
                        elementManager.setJmxClient(null);
                } catch (InterruptedException e) {
                }
                elementManager.setJmxClient(null);
                cancel(true);
        }

        /**
         * Performs all the execution.
         *
         * @return execution result
         * @throws Exception when interrupted etc
         */
        @Override
        protected ProgressState doInBackground() throws Exception {

                // Kill all running modules first
                processManager.killAll();

                // Get modules data
                HashMap<String, OpenBCIModuleData> modulesData;
                try {
                        modulesData = getModulesData();
                } catch (Exception ex) {
                        return new ProgressState(messageSource.getMessage("opensignal.amplifier.missingModulesData") + ": " + ex.getMessage(), -1, MAX_PROGRESS);
                }

                // Start multiplexer
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.startingMultiplexer"), 0, MAX_PROGRESS));
                processManager.runProcess(MX_ID, modulesData.get(MX_ID).getPath(), modulesData.get(MX_ID).getParameters());
                Thread.sleep(modulesData.get(MX_ID).getDelay());

                // Start hashtable
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.startingHashtable"), 1, MAX_PROGRESS));
                processManager.runProcess(HASHTABLE_ID, modulesData.get(HASHTABLE_ID).getPath(), modulesData.get(HASHTABLE_ID).getParameters());
                Thread.sleep(modulesData.get(HASHTABLE_ID).getDelay());

                // Connect to openBCI
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.connecting"), 3, MAX_PROGRESS));
                synchronized (lock) {
                        connectAction.setMultiplexerAddressAndPort(modulesData.get(MX_ID).getParameters().get(1));
                        connectAction.actionPerformed(null);
                        lock.wait();
                }

                // Send configuration data
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.sendingConfigData"), 5, MAX_PROGRESS));
                synchronized (lock) {
                        configurationWorker = new BCIConfigurationWorker(messageSource, elementManager.getJmxClient(), getConfigurationDataToSend());
                        configurationWorker.addPropertyChangeListener(this);
                        configurationWorker.execute();
                        lock.wait();
                }

                // Start driver
                String driverModuleName = descriptor.getAmplifierInstance().getDefinition().getModuleName();
                publish(new ProgressState(messageSource.getMessage("opensignal.amplifier.startingAmplifier"), 6, MAX_PROGRESS));
                processManager.runProcess(driverModuleName, modulesData.get(driverModuleName).getPath(), modulesData.get(driverModuleName).getParameters());
                Thread.sleep(modulesData.get(driverModuleName).getDelay());

                return new ProgressState(messageSource.getMessage("success"), MAX_PROGRESS, MAX_PROGRESS);
        }

        /**
         * Publishes progress status to window performing operation.
         *
         * @param chunks chunks
         */
        @Override
        protected void process(List<ProgressState> chunks) {

                for (ProgressState progressState : chunks) {
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, progressState);
                }
        }

        /**
         * Processes events from sub-workers.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (ProcessManager.PROCESS_ENDED.equals(evt.getPropertyName())) {

                        String errorMsg;
                        ProcessExitData exitData = (ProcessExitData) evt.getNewValue();
                        if (exitData.getExitCode() != null) {
                                errorMsg = messageSource.getMessage("opensignal.amplifier.processEnded");
                                errorMsg += exitData.getId() + " " + exitData.getExitCode().toString();
                        } else {
                                errorMsg = messageSource.getMessage("opensignal.amplifier.processFailed");
                                errorMsg += exitData.getId();
                        }
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, new ProgressState(errorMsg, -1, MAX_PROGRESS));
                        cancel();

                } else if (BCIConfigurationWorker.SENDING_DONE.equals(evt.getPropertyName())) {

                        synchronized (lock) {
                                WorkerResult res = (WorkerResult) evt.getNewValue();
                                if (res.success) {
                                        lock.notify();
                                } else {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.sendingFailed"), -1, MAX_PROGRESS));
                                        cancel();
                                }
                        }

                } else if (MultiplexerConnectWorker.JMX_CONNECTION.equals(evt.getPropertyName())) {

                        synchronized (lock) {
                                WorkerResult res = (WorkerResult) evt.getNewValue();
                                if (res.success) {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.testingConnection"), 4, MAX_PROGRESS));
                                } else {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.connectionFailed"), -1, MAX_PROGRESS));
                                        cancel();
                                }
                        }

                } else if (MultiplexerConnectionTestWorker.CONNECTION_TEST_RESULT.equals(evt.getPropertyName())) {

                        synchronized (lock) {
                                WorkerResult res = (WorkerResult) evt.getNewValue();
                                if (res.success) {
                                        lock.notify();
                                } else {
                                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null,
                                                new ProgressState(messageSource.getMessage("opensignal.amplifier.testFailed"), -1, MAX_PROGRESS));
                                        cancel();
                                }
                        }
                }
        }

        /**
         * When the work is done.
         */
        @Override
        protected void done() {

                try {
                        ProgressState result = get();
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, result);
                } catch (Exception ex) {
                        ProgressState result = new ProgressState(messageSource.getMessage("failed"), -1, MAX_PROGRESS);
                        firePropertyChange(ProgressDialog.PROGRESS_STATE, null, result);
                        cancel();
                }
                processManager.removePropertyChangeListener(this);
        }
}
