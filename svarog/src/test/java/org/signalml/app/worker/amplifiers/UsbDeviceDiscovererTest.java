package org.signalml.app.worker.amplifiers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Tests {@link BluetoothDeviceDiscoverer}.
 *
 * @author Tomasz Sawicki
 */
public class UsbDeviceDiscovererTest implements PropertyChangeListener {

        private USBDeviceDiscoverer discoverer;
        private final Object lock = new Object();

        @Test
        public void test() {

                MessageSource messageSource = new MessageSource() {

                        @Override
                        public String getMessage(String string, Object[] os, String string1, Locale locale) {

                                return "";
                        }

                        @Override
                        public String getMessage(String string, Object[] os, Locale locale) throws NoSuchMessageException {

                                if (string.equals("amplifierSelection.usbSearchCompleted"))
                                        return "USB search completed.";
                                else if (string.equals("amplifierSelection.usbInitializeError"))
                                        return "Error while initializing USB search: ";
                                else if (string.equals("amplifierSelection.usbStartError"))
                                        return "Error while starting USB search";
                                else
                                        return "";
                        }

                        @Override
                        public String getMessage(MessageSourceResolvable msr, Locale locale) throws NoSuchMessageException {

                                return "";
                        }
                };

                discoverer = new USBDeviceDiscoverer();
                discoverer.addPropertyChangeListener(this);

                synchronized (lock) {

                        System.out.println("Searching...");
                        discoverer.execute();

                        try {
                                lock.wait();
                        } catch (InterruptedException ex) {
                        }
                }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (AbstractDeviceDiscoverer.DEVICE_FOUND.equals(evt.getPropertyName())) {

                        DeviceInfo info = (DeviceInfo) evt.getNewValue();
                        System.out.println("Device found: " + info.getName() + " " + info.getAddress() + " (" + info.getDeviceType() + ")");

                } else if (AbstractDeviceDiscoverer.END_OF_SEARCH.equals(evt.getPropertyName())) {

                        synchronized (lock) {

                                System.out.println((String) evt.getNewValue());
                                lock.notify();
                        }
                }
        }
}
