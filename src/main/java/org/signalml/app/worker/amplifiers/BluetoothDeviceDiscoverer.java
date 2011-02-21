package org.signalml.app.worker.amplifiers;

import java.io.IOException;

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

                DeviceInfo info = new DeviceInfo(address, name, DeviceInfo.BLUETOOTH);

                deviceFound(info);
        }

        /**
         * Calls {@link #endOfSearch()}.
         *
         * @param i not used
         */
        @Override
        public void inquiryCompleted(int i) {

                endOfSearch();
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
         * Begins the search.
         *
         * @throws Exception when there is a problem with local
         * bluetooth device, i.e. it is off.
         */
        @Override
        public void startSearch() throws Exception {

                LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
        }
}