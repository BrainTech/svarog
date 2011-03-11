package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.worker.amplifiers.AmplifierInstance;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel enabling configuring, starting and connecting to OpenBCI.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierSignalSourcePanel extends AbstractSignalSourcePanel implements AmplifierSelectionListener {

        /**
         * Signal parameters panel.
         */
        private SignalParametersPanel signalParametersPanel = null;
        /**
         * Amplifier selection panel.
         */
        private AmplifierSelectionPanel amplifierSelectionPanel = null;

        /**
         * Default constructor.
         *
         * @param messageSource message source
         * @param viewerElementManager viewer element manager
         */
        public AmplifierSignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {

                super(messageSource, viewerElementManager);
        }

        /**
         * Creates left panel.
         *
         * @return the left panel
         */
        @Override
        protected JPanel createLeftColumnPanel() {

                JPanel leftColumnPanel = new JPanel(new BorderLayout());
                leftColumnPanel.add(getAmplifierSelectionPanel(), BorderLayout.CENTER);
                return leftColumnPanel;
        }

        /**
         * Creates right panel.
         *
         * @return the right panel
         */
        @Override
        protected JPanel createRightColumnPanel() {

                JPanel rightColumnPanel = new JPanel(new BorderLayout());
                rightColumnPanel.add(getSignalParametersPanel(), BorderLayout.NORTH);
                return rightColumnPanel;
        }

        /**
         * Creates the model.
         */
        @Override
        protected void createModel() {
                
                currentModel = new Object();
        }

        /**
         * Gets the signal parameters panel.
         *
         * @return the signal parameters panel
         */
        private SignalParametersPanel getSignalParametersPanel() {

                if (signalParametersPanel == null) {
                        signalParametersPanel = new SignalParametersPanel(messageSource, currentModel);
                }
                return signalParametersPanel;
        }

        /**
         * Gets the amplifier selection panel.
         *
         * @return the amplifier selection panel
         */
        private AmplifierSelectionPanel getAmplifierSelectionPanel() {

                if (amplifierSelectionPanel == null) {
                        amplifierSelectionPanel = new AmplifierSelectionPanel(messageSource, viewerElementManager, this, null, null);
                }
                return amplifierSelectionPanel;
        }

        @Override
        public void amplifierChosen(AmplifierInstance instance) {

        }
}
