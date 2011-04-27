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
 * Tests {@link DeviceDiscoveryWorker}.
 *
 * @author Tomasz Sawicki
 */
public class DeviceDiscoveryWorkerTest implements PropertyChangeListener {

        private DeviceDiscoveryWorker worker;
        private final Object lock = new Object();

        @Test
        public void test() throws InterruptedException {

                worker = new DeviceDiscoveryWorker(getMessageSource());
                worker.addPropertyChangeListener(this);

                synchronized(lock) {
                        worker.execute();
                        lock.wait();
                }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (DeviceDiscoveryWorker.DISCOVERY_STATE.equals(evt.getPropertyName())) {

                        DiscoveryState state = (DiscoveryState) evt.getNewValue();
                        if (state.getInfo() != null) {
                                DeviceInfo info = state.getInfo();
                                System.out.println("Device found: " + info.getName() + " " + info.getAddress() + " (" + info.getDeviceType() + ")");
                        } else if (state.getMessage() != null) {
                                System.out.println(state.getMessage());
                        }
                } else if (DeviceDiscoveryWorker.END_OF_SEARCH.equals(evt.getPropertyName())) {

                        synchronized(lock) {
                                lock.notify();
                        }
                }
        }

        public static MessageSourceAccessor getMessageSource() {

                MessageSource messageSource = new MessageSource() {

                        @Override
                        public String getMessage(String string, Object[] os, String string1, Locale locale) {

                                return "";
                        }

                        @Override
                        public String getMessage(String string, Object[] os, Locale locale) throws NoSuchMessageException {

                                if (string.equals("amplifierSelection.bluetoothSearchCompleted"))
                                        return "Bluetooth search completed.";
                                else if (string.equals("amplifierSelection.bluetoothInitializeError"))
                                        return "Error while initializing bluetooth search: ";
                                else if (string.equals("amplifierSelection.bluetoothStartError"))
                                        return "Error while starting bluetooth search";
                                else if (string.equals("amplifierSelection.usbSearchCompleted"))
                                        return "USB search completed.";
                                else if (string.equals("amplifierSelection.usbInitializeError"))
                                        return "Error while initializing USB search: ";
                                else if (string.equals("amplifierSelection.usbStartError"))
                                        return "Error while starting USB search";
                                else if (string.equals("amplifierSelection.search"))
                                        return "Searching for devices...";
                                else if (string.equals("amplifierSelection.searchCompleted"))
                                        return "Search completed.";
                                else
                                        return "";
                        }

                        @Override
                        public String getMessage(MessageSourceResolvable msr, Locale locale) throws NoSuchMessageException {

                                return "";
                        }
                };

                return new MessageSourceAccessor(messageSource);
        }

}