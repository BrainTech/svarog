package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel enabling configuring, starting and connecting to OpenBCI.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierSignalSourcePanel extends AbstractSignalSourcePanel {

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
         * Gets the signal parameters panel.
         *
         * @return the signal parameters panel
         */
        private SignalParametersPanel getSignalParametersPanel() {

                if (signalParametersPanel == null) {
                        signalParametersPanel = new SignalParametersPanel(messageSource);
			signalParametersPanel.addPropertyChangeListener(this);
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
                        amplifierSelectionPanel = new AmplifierSelectionPanel(
                                messageSource,
                                viewerElementManager,
                                this,
                                null,
                                null);
                }
                return amplifierSelectionPanel;
        }

        /**
         * Fills this panel from a model.
         *
         * @param model the model
         * @throws SignalMLException when model is not supported or an amplifier
         * cannot be found (check {@link SignalMLException#getMessage()}
         */
        @Override
        public void fillPanelFromModel(Object model) throws SignalMLException {

                fillPanelFromModel(model, false);
        }

        /**
         * Fills a model from this panel.
         *
         * @param model the model
         * @throws SignalMLException when model is not supported or input data
         * is not valid (check {@link SignalMLException#getMessage()}
         */
        @Override
        public void fillModelFromPanel(Object model) throws SignalMLException {

                // TODO
        }

        /**
         * Fills this panel from a model with an option to omit the {@link #amplifierSelectionPanel}.
         *
         * @param model the model
         * @param omitAmplifierSelectionPanel wheter to omit the {@link #amplifierSelectionPanel}
         * @throws SignalMLException when model is not supported or an amplifier
         * cannot be found (check {@link SignalMLException#getMessage()}
         */
        public void fillPanelFromModel(Object model, boolean omitAmplifierSelectionPanel) throws SignalMLException {

                getSignalParametersPanel().fillPanelFromModel(model);
                if (!omitAmplifierSelectionPanel) getAmplifierSelectionPanel().fillPanelFromModel((AmplifierConnectionDescriptor) model);
        }

	@Override
	public int getNumberOfChannels() {
		return 3;
	}
}