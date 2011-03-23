package org.signalml.app.worker.amplifiers;

import java.io.File;

/**
 * Concrete {@link AbstractDeviceDiscoverer} discovering USB devices
 *
 * @author Tomasz Sawicki
 */
public class USBDeviceDiscoverer extends AbstractDeviceDiscoverer {

        private static final String DIRECTORY = "/dev";

        /**
         * Does nothing.
         */
        @Override
        public void initializeSearch() {
        }

        /**
         * Begins the search.
         */
        @Override
        public void startSearch() {
                search();
        }

        /**
         * Does nothing.
         */
        @Override
        public void cancelSearch() {
        }

        /**
         * Searches for amplifiers in devices directory.
         */
        private void search() {

                File dev = new File(DIRECTORY);
                String[] files = dev.list();

                for (String file : files) {

                        String path = DIRECTORY + "/" + file;
                        DeviceInfo info = new DeviceInfo(path, file, DeviceInfo.USB);

                        deviceFound(info);
                }

                endOfSearch();
        }
}