package org.signalml.app.worker.amplifiers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link BluetoothDeviceDiscoverer}.
 *
 * @author Tomasz Sawicki
 */
public class BluetoothDeviceDiscovererTest implements PropertyChangeListener {

        private BluetoothDeviceDiscoverer discoverer;
        private final Object event = new Object();

        @Test
        public void test() {

                discoverer = new BluetoothDeviceDiscoverer();
                discoverer.addPropertyChangeListener(this);

                try {
                        System.out.println("Searching...");
                        discoverer.startSearch();
                } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        return;
                }
                
                synchronized(event) {
                        try {
                                event.wait();
                        } catch (InterruptedException ex) {
                        }
                }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if ("deviceFound".equals(evt.getPropertyName())) {

                        DeviceInfo info = (DeviceInfo) evt.getNewValue();
                        System.out.println("Device found: " + info.getName() + " " + info.getAddress() + " (" + info.getDeviceType() + ")");
                }
                else if ("endOfSearch".equals(evt.getPropertyName())) {

                        System.out.println("End of search!");
                        
                        synchronized(event) {
                                event.notify();
                        }
                }
        }
}