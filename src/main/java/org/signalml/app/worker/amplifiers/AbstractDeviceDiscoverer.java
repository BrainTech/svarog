package org.signalml.app.worker.amplifiers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a device discoverer.
 * Property change listeners can be attached. Two property changes will be
 * fired: "deviceFound" - the new value represents latest device found, and
 * "endOfSearch" - when the search is over (both values are null).
 *
 * @author Tomasz Sawicki
 */
public abstract class AbstractDeviceDiscoverer {

        public static final String DEVICE_FOUND = "deviceFound";
        public static final String END_OF_SEARCH = "endOfSearch";

        /**
         * List of listeners that will be notified when a device is found
         * and when the search is over.
         */
        protected List<PropertyChangeListener> listeners;

        /**
         * Default constructor.
         */
        public AbstractDeviceDiscoverer() {

                listeners = new ArrayList<PropertyChangeListener>();
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
         * Begins the search.
         *
         * @throws Exception when needed
         */
        public abstract void startSearch() throws Exception;

        /**
         * Fires property change.
         *
         * @param propertyName property name
         * @param oldValue the old value
         * @param newValue the new value
         */
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {

                PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

                for (PropertyChangeListener listener : listeners) {

                        listener.propertyChange(event);
                }
        }

        /**
         * Should be called when a device is found.
         *
         * @param info the device info
         */
        protected void deviceFound(DeviceInfo info) {

                firePropertyChange(DEVICE_FOUND, null, info);
        }

        /**
         * Should be called when the search if over.
         */
        protected void endOfSearch() {

                firePropertyChange(END_OF_SEARCH, null, null);
        }
}