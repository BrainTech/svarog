package org.signalml.app.worker.amplifiers;

import static org.signalml.app.SvarogI18n._;
import java.io.IOException;
import javax.bluetooth.BluetoothStateException;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 * Concrete {@link AbstractDeviceDiscoverer} discovering bluetooth devices
 * using javax.bluetooth package.
 *
 * @author Tomasz Sawicki
 */
public class BluetoothDeviceDiscoverer extends AbstractDeviceDiscoverer implements DiscoveryListener {

        /**
         * The bluetooth discovery agent.
         */
        private DiscoveryAgent discoveryAgent;
        /**
         * The lock used to synchronize the execution.
         */
        private final Object lock = new Object();

        /**
         * Default constructor.
         *
         */
        public BluetoothDeviceDiscoverer() {

                super();
        }

        /**
         * Does the background work.
         *
         * @return execution info message
         * @throws Exception when needed
         */
        @Override
        protected String doInBackground() throws Exception {
                
                LocalDevice localDevice;

                try {
                        localDevice = LocalDevice.getLocalDevice();
                } catch (BluetoothStateException ex) {
                        return _("Error while initializing bluetooth search: ") + ex.getMessage();
                }

                discoveryAgent = localDevice.getDiscoveryAgent();

                synchronized(lock) {

                        try {
                                discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
                        } catch (BluetoothStateException ex) {
                                return _("Error while starting bluetooth search: ") + ex.getMessage();
                        }

                        lock.wait();
                }

                return _("Bluetooth search completed.");
        }

        /**
         * Calls {@link #deviceFound()}.
         *
         * @param rd device information
         * @param dc not used
         */
        @Override
        public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {

                String address = rd.getBluetoothAddress();
                String name = null;

                try {
                        name = rd.getFriendlyName(true);
                } catch (IOException ex) {
                }

                DeviceInfo info = new DeviceInfo(formatAddress(address), name, DeviceInfo.BLUETOOTH);
                deviceFound(info);
        }

        /**
         * Calls {@link #endOfSearch()}.
         *
         * @param i not used
         */
        @Override
        public void inquiryCompleted(int i) {

                synchronized(lock) {
                        lock.notify();
                }
        }

        /**
         * Discovering services, not used
         *
         * @param i not used
         * @param srs not used
         */
        @Override
        public void servicesDiscovered(int i, ServiceRecord[] srs) {
        }

        /**
         * Service search completed, not used
         *
         * @param i not used
         * @param i1 not used
         */
        @Override
        public void serviceSearchCompleted(int i, int i1) {
        }

        /**
         * Formats address to xx:xx:xx:xx:xx:xx form
         *
         * @param input input address
         * @return formatted address
         */
        private String formatAddress(String input) {

                if (input.length() == 17) {
                        return input;
                }

                String retval = "";
                for (int i = 0; i < 12; i += 2) {
                        retval += input.charAt(i);
                        retval += input.charAt(i + 1);
                        if (i < 10)
                                retval += ":";
                }

                return retval;
        }

        /**
         * Cancels the search.
         */
        @Override
        protected void cancel() {

                if (discoveryAgent != null)
                        discoveryAgent.cancelInquiry(this);
                super.cancel();
        }


}
