package org.signalml.app.action.document.monitor;

import java.beans.PropertyChangeEvent;
import javax.swing.JTextField;

import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.WorkerResult;
import org.signalml.app.worker.monitor.MultiplexerConnectWorker;

/**
 * A {@link ConnectMultiplexerAction} without receiving the metadata.
 *
 * @author Tomasz Sawicki
 */
public class ConnectMultiplexerActionNoMetadata extends ConnectMultiplexerAction {

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 10;

        /**
         * Default constructor. Sets address and port to the default one.
         *
         * @param elementManager {@link #elementManager}
         */
        public ConnectMultiplexerActionNoMetadata(ViewerElementManager elementManager) {

                super(elementManager);
                metadataWorker = null;
                setTimeoutMilis(TIMEOUT_MILIS);
                setTryoutCount(TRYOUT_COUNT);
        }

        /**
         * Sets the multiplexer address and port
         * 
         * @param addressAndPort multiplexer address and port in a form:
         * xxx.xxx.xxx.xxx:yyyy
         */
        public void setMultiplexerAddressAndPort(String addressAndPort) {

                String[] splitted = addressAndPort.split(":");
                String address = splitted[0];
                int port = Integer.parseInt(splitted[1]);
                
                setMultiplexerAddressField(new JTextField(address));
                setMultiplexerPortField(new JTextField(String.valueOf(port)));
        }

        /**
         * The same as in {@link ConnectMultiplexerAction}, but without
         * receiving the metadata
         * 
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
                
                firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                if (MultiplexerConnectWorker.JMX_CONNECTION.equals(evt.getPropertyName())) {
                        WorkerResult res = (WorkerResult) evt.getNewValue();
                        if (res.success) {
                                executeTest();
                        }
                }
        }
}
