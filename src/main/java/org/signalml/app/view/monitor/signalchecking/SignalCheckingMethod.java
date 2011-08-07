package org.signalml.app.view.monitor.signalchecking;

/**
 * Signal checking method. Currently, three methods are implemented:
 * - AMPNULL checks whether signal is equal to ampNull.
 * - DC checks whether average signal value is greater than maximum.
 * - FFT checks whether FFT of some frequencies is greater than FFT of others.
 *
 * @author Tomasz Sawicki
 */
public enum SignalCheckingMethod {

        AMPNULL,
        DC,
        FFT;

        /**
         * Whether is amp null
         * @return true if is amp null
         */
        public boolean isAmpNull() {
                return (this == AMPNULL);
        }

        /**
         * Whether is DC
         * @return true if is DC
         */
        public boolean isDC() {
                return (this == DC);
        }

        /**
         * Whether is FFT
         * @return true if is FFT
         */
        public boolean isFFT() {
                return (this == FFT);
        }
}
