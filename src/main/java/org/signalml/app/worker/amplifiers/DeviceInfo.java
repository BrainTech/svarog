package org.signalml.app.worker.amplifiers;

/**
 * Class representing some device info.
 *
 * @author Tomasz Sawicki
 */
public class DeviceInfo {

        public static final String USB = AmplifierDefinition.USB;
        public static final String BLUETOOTH = AmplifierDefinition.BLUETOOTH;

        /**
         * Device address.
         */
        private String address;

        /**
         * Device name.
         */
        private String name;

        /**
         * Device type
         */
        private String deviceType;

        /**
         * Constructor sets the data.
         *
         * @param address device address
         * @param name device name
         * @param deviceType device type
         */
        public DeviceInfo(String address, String name, String deviceType) {

                this.address = address;
                this.name = name;
                this.deviceType = deviceType;
        }

        public String getAddress() {
                return address;
        }

        public String getName() {
                return name;
        }

        public String getDeviceType() {
                return deviceType;
        }
}