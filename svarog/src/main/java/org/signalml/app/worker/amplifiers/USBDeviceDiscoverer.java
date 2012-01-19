package org.signalml.app.worker.amplifiers;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;

/**
 * Concrete {@link AbstractDeviceDiscoverer} discovering USB devices
 *
 * @author Tomasz Sawicki
 */
public class USBDeviceDiscoverer extends AbstractDeviceDiscoverer {

        private static final String DIRECTORY = "/dev";

        /**
         * Default constructor.
         *
         */
        public USBDeviceDiscoverer() {
                
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

                File dev = new File(DIRECTORY);
                String[] files = dev.list();

                for (String file : files) {

                        String path = DIRECTORY + "/" + file;
                        DeviceInfo info = new DeviceInfo(path, file, DeviceInfo.USB);

                        deviceFound(info);
                }

                return _("USB search completed.");
        }
}