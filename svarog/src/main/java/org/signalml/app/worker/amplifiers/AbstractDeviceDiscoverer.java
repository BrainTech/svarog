package org.signalml.app.worker.amplifiers;

import java.util.List;
import javax.swing.SwingWorker;

/**
 * Abstract class representing a device discoverer.
 * Property change listeners can be attached. Two property changes will be
 * fired: "deviceFound" - the new value represents latest device found, and
 * "endOfSearch" - when the search is over (new value is a String containing
 * search info - an error message, or a success message).
 *
 * @author Tomasz Sawicki
 */
public abstract class AbstractDeviceDiscoverer extends SwingWorker<String, DeviceInfo> {

        public static final String DEVICE_FOUND = "deviceFound";
        public static final String END_OF_SEARCH = "endOfSearch";
        
        /**
         * Default constructor.
         *
         */
        public AbstractDeviceDiscoverer() {
        }

        /**
         * Should be called when a device is found.
         * 
         * @param info info about the found device
         */
        protected void deviceFound(DeviceInfo info) {

                publish(info);
        }

        /**
         * When devices are being found.
         *
         * @param chunks devices
         */
        @Override
        protected final void process(List<DeviceInfo> chunks) {

                for (DeviceInfo info : chunks) {
                        firePropertyChange(DEVICE_FOUND, null, info);
                }
        }

        /**
         * When the search is over.
         */
        @Override
        protected final void done() {

                try {
                        String result = get();
                        firePropertyChange(END_OF_SEARCH, null, result);
                } catch (Exception ex) {
                }
        }

        /**
         * Cancels the search.
         */
        protected void cancel() {

                cancel(true);
        }
}
