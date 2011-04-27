package org.signalml.app.worker.amplifiers;

import java.io.File;
import org.springframework.context.support.MessageSourceAccessor;

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
         * @param messageSource {@link #messageSource}
         */
        public USBDeviceDiscoverer(MessageSourceAccessor messageSource) {
                
                super(messageSource);
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

                return messageSource.getMessage("amplifierSelection.usbSearchCompleted");
        }
}