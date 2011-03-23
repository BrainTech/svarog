package org.signalml.app.worker.amplifiers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Searches for all types of amplifiers. Property listeners can be attached,
 * one property change will be fired: {@link #DISCOVERY_STATE} when a device
 * is found, or some message is passed.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDiscoveryWorker extends SwingWorker<DiscoveryState, DiscoveryState> implements PropertyChangeListener {

        public static final String DISCOVERY_STATE = "discoveryState";
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
         * List of amplifier definitions.
         */
        private List<AmplifierDefinition> definitions;
        /**
         * Message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * Lock used to synchronize execution.
         */
        private final Object lock = new Object();

        /**
         * Default constructor.
         *
         * @param messageSource {@link #messageSource}
         * @param definitions {@link #definitions}
         */
        public AmplifierDiscoveryWorker(MessageSourceAccessor messageSource, List<AmplifierDefinition> definitions) {

                this.messageSource = messageSource;
                this.definitions = definitions;
        }

        /**
         * Performs the background work.
         *
         * @return a discovery state
         * @throws Exception when needed
         */
        @Override
        protected DiscoveryState doInBackground() throws Exception {

                publish(new DiscoveryState(messageSource.getMessage("amplifierSelection.search")));

                usbDeviceDiscoverer = new USBDeviceDiscoverer();
                usbDeviceDiscoverer.addPropertyChangeListener(this);
                usbSearchOver = false;

                bluetoothDeviceDiscoverer = new BluetoothDeviceDiscoverer();
                bluetoothDeviceDiscoverer.addPropertyChangeListener(this);
                bluetoothSearchOver = false;

                synchronized (lock) {

                        try {
                                usbDeviceDiscoverer.initializeSearch();
                        } catch (Exception ex) {
                                String errorMsg = messageSource.getMessage("amplifierSelection.usbInitializeError");
                                errorMsg += ex.getMessage();
                                publish(new DiscoveryState(errorMsg));
                                usbSearchOver = true;
                        }

                        if (!usbSearchOver) {
                                try {
                                        usbDeviceDiscoverer.startSearch();
                                } catch (Exception ex) {
                                        String errorMsg = messageSource.getMessage("amplifierSelection.usbStartError");
                                        errorMsg += ex.getMessage();
                                        publish(new DiscoveryState(errorMsg));
                                        usbSearchOver = true;
                                }
                        }

                        try {
                                bluetoothDeviceDiscoverer.initializeSearch();
                        } catch (Exception ex) {
                                String errorMsg = messageSource.getMessage("amplifierSelection.bluetoothInitializeError");
                                errorMsg += ex.getMessage();
                                publish(new DiscoveryState(errorMsg));
                                bluetoothSearchOver = true;
                        }

                        if (!bluetoothSearchOver) {
                                try {
                                        bluetoothDeviceDiscoverer.startSearch();
                                } catch (Exception ex) {
                                        String errorMsg = messageSource.getMessage("amplifierSelection.bluetoothStartError");
                                        errorMsg += ex.getMessage();
                                        publish(new DiscoveryState(errorMsg));
                                        bluetoothSearchOver = true;
                                }
                        }

                        if (!(bluetoothSearchOver && usbSearchOver)) {
                                lock.wait();
                        }
                }

                return new DiscoveryState(messageSource.getMessage("amplifierSelection.searchFinished"));
        }

        /**
         * Cancels the search.
         */
        public void cancelSearch() {

                try {
                        usbDeviceDiscoverer.cancelSearch();
                } catch (Exception ex) {
                }
                try {
                        bluetoothDeviceDiscoverer.cancelSearch();
                } catch (Exception ex) {
                }
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
                        DiscoveryState state = get();
                        firePropertyChange(DISCOVERY_STATE, null, state);
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
                        for (AmplifierDefinition definition : definitions) {
                                if (compare(info, definition)) {
                                        publish(new DiscoveryState(new AmplifierInstance(definition, info.getAddress())));
                                }
                        }

                } else if (AbstractDeviceDiscoverer.END_OF_SEARCH.equals(evt.getPropertyName())) {

                        synchronized (lock) {
                                AbstractDeviceDiscoverer discoverer = (AbstractDeviceDiscoverer) evt.getSource();

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

        /**
         * Compares a {@link DeviceInfo} object with an {@link AmplifierDefinition} object
         *
         * @param info device info object
         * @param definition amplifier definition object
         * @return true if they match, false if they don't
         */
        private boolean compare(DeviceInfo info, AmplifierDefinition definition) {

                Pattern pattern = Pattern.compile(definition.getMatch());
                Matcher matcher = pattern.matcher(info.getName());

                boolean regexMatch = matcher.find();
                boolean protocolMatch = info.getDeviceType().equals(definition.getProtocol());

                return (regexMatch && protocolMatch);
        }
}
