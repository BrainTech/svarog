package org.signalml.app.worker.amplifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * Device search result contains result list and information about errors.
 *
 * @author Tomasz Sawicki
 */
public class DeviceSearchResult {

        /**
         * List of amplifier instances.
         */
        private List<AmplifierInstance> results;

        /**
         * True if there were no problems with bluetooth search.
         */
        private boolean bluetoothOK;

        /**
         * If there was a problem with bluetooth search this field
         * will contain the error message.
         */
        private String bluetoothErrorMsg;

        /**
         * True if there were no problems with usb search.
         */
        private boolean usbOK;

        /**
         * If there was a problem with usb search this field
         * will contain the error message.
         */
        private String usbErrorMsg;

        /**
         * Default constructor.
         */
        public DeviceSearchResult() {

                results = new ArrayList<AmplifierInstance>();

                bluetoothOK = true;
                usbOK = true;
                bluetoothErrorMsg = null;
                usbErrorMsg = null;
        }

        /**
         * Adds a result.
         *
         * @param instance a result
         */
        public void addResult(AmplifierInstance instance) {
                results.add(instance);
        }

        public String getBluetoothErrorMsg() {
                return bluetoothErrorMsg;
        }

        public boolean isBluetoothOK() {
                return bluetoothOK;
        }

        public List<AmplifierInstance> getResults() {
                return results;
        }

        public String getUsbErrorMsg() {
                return usbErrorMsg;
        }

        public boolean isUsbOK() {
                return usbOK;
        }

        public void setBluetoothErrorMsg(String bluetoothErrorMsg) {
                this.bluetoothErrorMsg = bluetoothErrorMsg;
        }

        public void setBluetoothOK(boolean bluetoothOK) {
                this.bluetoothOK = bluetoothOK;
        }

        public void setUsbErrorMsg(String usbErrorMsg) {
                this.usbErrorMsg = usbErrorMsg;
        }

        public void setUsbOK(boolean usbOK) {
                this.usbOK = usbOK;
        }
}