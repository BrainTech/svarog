package org.signalml.app.view.monitor;

import org.signalml.app.worker.amplifiers.AmplifierInstance;

/**
 * Interface that has one method called when an amplifier is chosen from a list.
 *
 * @author Tomasz Sawicki
 */
public interface AmplifierSelectionListener {

        /**
         * Called when amplifier is chosen.
         *
         * @param instance chosen amplifier's instance
         */
        public void amplifierChosen(AmplifierInstance instance);
}
