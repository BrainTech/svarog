package org.signalml.app.action.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetSocketAddress;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.WorkerResult;
import org.signalml.app.worker.monitor.MultiplexerConnectWorker;
import org.signalml.app.worker.monitor.MultiplexerConnectionTestWorker;
import org.signalml.app.worker.monitor.MultiplexerTagConnectWorker;

public class ConnectMultiplexerAction extends AbstractAction implements PropertyChangeListener {

        private static final long serialVersionUID = 1L;
        protected ViewerElementManager elementManager;
        private ExperimentDescriptor experimentDescriptor;
        private JTextField multiplexerAddressField;
        private JTextField multiplexerPortField;
        private Integer timeoutMilis;
        private Integer tryoutCount;
        private DisconnectMultiplexerAction disconnectAction;
        protected MultiplexerConnectWorker connectWorker;
        protected MultiplexerTagConnectWorker tagConnectWorker;
        protected MultiplexerConnectionTestWorker testWorker;
        private InetSocketAddress multiplexerSocket;
        protected static final Logger logger = Logger.getLogger(ConnectMultiplexerAction.class);

        public ConnectMultiplexerAction(ViewerElementManager elementManager) {
                this.elementManager = elementManager;
                this.putValue(NAME, _("Connect"));
        }

        public ExperimentDescriptor getExperimentDescriptor() {
                return experimentDescriptor;
        }

        public void setExperimentDescriptor(ExperimentDescriptor experimentDescriptor) {
                this.experimentDescriptor = experimentDescriptor;
        }

        public JTextField getMultiplexerAddressField() {
                return multiplexerAddressField;
        }

        public void setMultiplexerAddressField(JTextField multiplexerAddressField) {
                this.multiplexerAddressField = multiplexerAddressField;
        }

        public JTextField getMultiplexerPortField() {
                return multiplexerPortField;
        }

        public void setMultiplexerPortField(JTextField multiplexerPortField) {
                this.multiplexerPortField = multiplexerPortField;
        }

        public Integer getTimeoutMilis() {
                return timeoutMilis;
        }

        public void setTimeoutMilis(Integer timeoutMilis) {
                this.timeoutMilis = timeoutMilis;
        }

        public Integer getTryoutCount() {
                return tryoutCount;
        }

        public void setTryoutCount(Integer tryoutCount) {
                this.tryoutCount = tryoutCount;
        }

        public DisconnectMultiplexerAction getDisconnectAction() {
                return disconnectAction;
        }

        public void setDisconnectAction(DisconnectMultiplexerAction disconnectAction) {
                this.disconnectAction = disconnectAction;
        }

        @Override
        public synchronized void actionPerformed(ActionEvent ev) {

                logger.info("Connecting multiplexer...");

                createSocket();
                executeConnect(multiplexerSocket);
        }

        protected synchronized void createSocket() {

                String multiplexerAddress = null;
                Integer multiplexerPort = null;

                try {
                        multiplexerAddress = multiplexerAddressField.getText();
                        multiplexerPort = Integer.parseInt(multiplexerPortField.getText());
                        multiplexerSocket = new InetSocketAddress(multiplexerAddress, multiplexerPort.intValue());
                } catch (NumberFormatException e) {
                        logger.error("bad port number! " + e.getMessage());
                        WorkerResult result = new WorkerResult(Boolean.FALSE, _("Bad address!"));
                        firePropertyChange("connectionTestResult", null, result);
                        return;
                } catch (IllegalArgumentException e) {
                        logger.error("bad address! " + e.getMessage());
                        WorkerResult result = new WorkerResult(Boolean.FALSE, _("Bad port number!"));
                        firePropertyChange("connectionTestResult", null, result);
                        return;
                }
        }

        protected synchronized void executeConnect(InetSocketAddress multiplexerSocket) {
                connectWorker = new MultiplexerConnectWorker(
                        elementManager,
                        multiplexerSocket,
                        timeoutMilis,
                        tryoutCount);
                connectWorker.addPropertyChangeListener(this);
                connectWorker.execute();
        }

        protected synchronized void executeTest() {
                testWorker = new MultiplexerConnectionTestWorker(
                        elementManager.getJmxClient(),
                        timeoutMilis,
                        tryoutCount);
                testWorker.addPropertyChangeListener(this);
                testWorker.execute();
        }

        protected synchronized void executeTagConnect(InetSocketAddress multiplexerSocket) {
                tagConnectWorker = new MultiplexerTagConnectWorker(
                        elementManager,
                        multiplexerSocket,
                        timeoutMilis,
                        tryoutCount);
                tagConnectWorker.addPropertyChangeListener(this);
                tagConnectWorker.execute();
        }

        public synchronized void cancel() {
                if (testWorker != null) {
                        testWorker.cancel(false);
                }
                if (connectWorker != null) {
                        connectWorker.cancel(false);
                }
                JmxClient jmxClient = elementManager.getJmxClient();
                if (jmxClient != null) {
                        try {
                                jmxClient.shutdown();
                                elementManager.setJmxClient(null);
                        } catch (Exception e) {
                        }
                }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
                firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                if (MultiplexerConnectWorker.JMX_CONNECTION.equals(evt.getPropertyName())) {
                        WorkerResult res = (WorkerResult) evt.getNewValue();
                        if (res.success) {
                                executeTest();
                        }
                }
                if (MultiplexerConnectionTestWorker.CONNECTION_TEST_RESULT.equals(evt.getPropertyName())) {
                        WorkerResult res = (WorkerResult) evt.getNewValue();
                }
        }
}
