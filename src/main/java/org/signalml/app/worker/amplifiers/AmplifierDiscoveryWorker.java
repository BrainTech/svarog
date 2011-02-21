package org.signalml.app.worker.amplifiers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Searches for all types of amplifiers. Property listeners can be attached,
 * one property change will be fired: "endOfSearch", the new value will be
 * a {@link DeviceSearchResult} object.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDiscoveryWorker implements PropertyChangeListener {

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
         * Search result.
         */
        private DeviceSearchResult result;

        /**
         * List of listeners.
         */
        private List<PropertyChangeListener> listeners;

        /**
         * Default constructor.
         *
         * @param definitions definitions list
         */
        public AmplifierDiscoveryWorker(List<AmplifierDefinition> definitions) {

                this.definitions = definitions;

                result = new DeviceSearchResult();

                listeners = new ArrayList<PropertyChangeListener>();

                bluetoothSearchOver = false;
                usbSearchOver = false;
        }

        /**
         * Begins the search for amplifiers.
         */
        public void startSearch() {

                usbDeviceDiscoverer = new USBDeviceDiscoverer();
                usbDeviceDiscoverer.addPropertyChangeListener(this);

                try {
                        usbDeviceDiscoverer.startSearch();
                }
                catch (Exception ex) {
                        usbSearchOver = true;
                        result.setUsbOK(false);
                        result.setUsbErrorMsg(ex.getMessage());
                }

                bluetoothDeviceDiscoverer = new BluetoothDeviceDiscoverer();
                bluetoothDeviceDiscoverer.addPropertyChangeListener(this);

                try {
                        bluetoothDeviceDiscoverer.startSearch();
                }
                catch (Exception ex) {
                        bluetoothSearchOver = true;
                        result.setBluetoothOK(false);
                        result.setBluetoothErrorMsg(ex.getMessage());
                }

                if (usbSearchOver && bluetoothSearchOver) {
                        endOfSearch();
                }
        }

        public void propertyChange(PropertyChangeEvent evt) {

                if ("deviceFound".equals(evt.getPropertyName())) {

                        DeviceInfo info = (DeviceInfo) evt.getNewValue();
                        compareAll(info);
                }
                else if ("endOfSearch".equals(evt.getPropertyName())) {

                        AbstractDeviceDiscoverer discoverer = (AbstractDeviceDiscoverer) evt.getSource();

                        if (discoverer.equals(bluetoothDeviceDiscoverer)) {
                                bluetoothSearchOver = true;
                        }
                        else if (discoverer.equals(usbDeviceDiscoverer)) {
                                usbSearchOver = true;
                        }

                        if (usbSearchOver && bluetoothSearchOver) {
                                endOfSearch();
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

        /**
         * Compares a {@link DeviceInfo} object with all amplifier definitons,
         * and adds matches to the result list.
         *
         * @param info device info object
         */
        private void compareAll(DeviceInfo info) {

                for (AmplifierDefinition definition : definitions) {

                        if (compare(info, definition)) {

                                result.addResult(new AmplifierInstance(definition, info.getAddress()));
                        }
                }
        }

        /**
         * Adds a property change listener.
         *
         * @param listener the listener
         */
        public void addPropertyChangeListener(PropertyChangeListener listener) {

                listeners.add(listener);
        }

        /**
         * Removes a property change listener.
         *
         * @param listener the listener
         */
        public void removePropertyChangeListener(PropertyChangeListener listener) {

                listeners.remove(listener);
        }

        /**
         * Fires property change.
         *
         * @param propertyName property name
         * @param oldValue the old value
         * @param newValue the new value
         */
        private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {

                PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

                for (PropertyChangeListener listener : listeners) {

                        listener.propertyChange(event);
                }
        }

        /**
         * Called when the search is over, fires "endOfSeach" property change.
         */
        private void endOfSearch() {

                firePropertyChange("endOfSearch", null, result);
        }
}