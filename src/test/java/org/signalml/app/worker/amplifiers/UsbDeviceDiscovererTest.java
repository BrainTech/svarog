package org.signalml.app.worker.amplifiers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link USBDeviceDiscoverer}.
 *
 * @author Tomasz Sawicki
 */
public class UsbDeviceDiscovererTest implements PropertyChangeListener {

        private USBDeviceDiscoverer discoverer;

        @BeforeClass
        public static void setUp() {

                (new UsbDeviceDiscovererTest()).test();
        }

        @Test
        public void test() {

                discoverer = new USBDeviceDiscoverer();
                discoverer.addPropertyChangeListener(this);
                discoverer.startSearch();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (AbstractDeviceDiscoverer.DEVICE_FOUND.equals(evt.getPropertyName())) {

                        DeviceInfo info = (DeviceInfo) evt.getNewValue();
                        System.out.println("Device found: " + info.getName() + " " + info.getAddress() + " (" + info.getDeviceType() + ")");
                }
                else if (AbstractDeviceDiscoverer.END_OF_SEARCH.equals(evt.getPropertyName())) {

                        System.out.println("End of search!");
                }
        }
}