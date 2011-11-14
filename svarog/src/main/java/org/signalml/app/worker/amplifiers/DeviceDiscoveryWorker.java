package org.signalml.app.worker.amplifiers;

import static org.signalml.app.SvarogApplication._;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * Searches for all types of devices. Property listeners can be attached.
 * Two property changes will be fired: {@link #DISCOVERY_STATE} when a device
 * is found or some message is passed (the new value is a {@link DiscoveryState}
 * object, and {@link #END_OF_SEARCH} when the search is over (the new value is
 * a String containing result info).
 *
 * @author Tomasz Sawicki
 */
public class DeviceDiscoveryWorker extends SwingWorker<String, DiscoveryState> implements PropertyChangeListener {

        public static final String DISCOVERY_STATE = "discoveryState";
        public static final String END_OF_SEARCH = "endOfSearch";
        /**
         * Bluetooth device discoverer.
         */
        private AbstractDeviceDiscoverer bluetoothDeviceDiscoverer;
        /**
         * USB device discoverer
         */
        private AbstractDeviceDiscoverer usbDeviceDiscoverer;
        /**
         * Set to true if search for bluetooth devices is over.
         */
        private boolean bluetoothSearchOver;
        /**
         * Set to true if search for usb devices is over.
         */
        private boolean usbSearchOver;
        /**
         * Lock used to synchronize execution.
         */
        private final Object lock = new Object();

        /**
         * Default constructor.
         *
         * @param definitions {@link #definitions}
         */
        public  DeviceDiscoveryWorker() {
        }

        /**
         * Performs the background work.
         *
         * @return a discovery state
         * @throws Exception when needed
         */
        @Override
        protected String doInBackground() throws Exception {

                publish(new DiscoveryState(_("Searching for devices...")));

                usbDeviceDiscoverer = new USBDeviceDiscoverer();
                usbDeviceDiscoverer.addPropertyChangeListener(this);
                usbSearchOver = false;

                bluetoothDeviceDiscoverer = new BluetoothDeviceDiscoverer();
                bluetoothDeviceDiscoverer.addPropertyChangeListener(this);
                bluetoothSearchOver = false;

                synchronized (lock) {

                        usbDeviceDiscoverer.execute();
                        bluetoothDeviceDiscoverer.execute();
                        lock.wait();

                }                
                return _("Search completed.");
        }

        /**
         * Cancels the search.
         */
        public void cancelSearch() {

                usbDeviceDiscoverer.cancel();
                bluetoothDeviceDiscoverer.cancel();
                this.cancel(true);
        }

        /**
         * Fires {@link #DISCOVERY_STATE} property change when needed.
         *
         * @param chunks list of chunks
         */
        @Override
        protected void process(List<DiscoveryState> chunks) {

                for (DiscoveryState state : chunks) {
                        firePropertyChange(DISCOVERY_STATE, null, state);
                }
        }

        /**
         * When search is done fire the last property change.
         */
        @Override
        protected void done() {
                
                try {
                        String result = get();
                        firePropertyChange(END_OF_SEARCH, null, result);
                } catch (Exception ex) {
                }
        }

        /**
         * If a device is found publish the result.
         * If search is over, notify the lock.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (AbstractDeviceDiscoverer.DEVICE_FOUND.equals(evt.getPropertyName())) {

                        DeviceInfo info = (DeviceInfo) evt.getNewValue();
                        publish(new DiscoveryState(info));

                } else if (AbstractDeviceDiscoverer.END_OF_SEARCH.equals(evt.getPropertyName())) {

                        synchronized (lock) {

                                AbstractDeviceDiscoverer discoverer = (AbstractDeviceDiscoverer) evt.getSource();
                                publish(new DiscoveryState((String) evt.getNewValue()));

                                if (discoverer.equals(bluetoothDeviceDiscoverer)) {
                                        bluetoothSearchOver = true;
                                } else if (discoverer.equals(usbDeviceDiscoverer)) {
                                        usbSearchOver = true;
                                }

                                if (usbSearchOver && bluetoothSearchOver) {
                                        
                                        lock.notify();
                                }
                        }
                }
        }
}
