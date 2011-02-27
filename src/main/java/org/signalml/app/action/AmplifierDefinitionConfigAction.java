package org.signalml.app.action;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

import org.signalml.app.view.monitor.AmplifierDefinitionConfigDialog;
import org.signalml.app.worker.amplifiers.AmplifierDefinition;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Opens {@link AmplifierDefinitionConfigDialog}.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierDefinitionConfigAction extends AbstractSignalMLAction {

        /**
	 * Logger to save history of execution at.
	 */
        protected static final Logger logger = Logger.getLogger(AmplifierDefinitionConfigAction.class);

        /**
	 * The dialog which is shown after evoking this action.
	 */
        private AmplifierDefinitionConfigDialog configDialog;

        /**
         * Constructor sets text and tooltip.
         *
         * @param messageSource the message source
         */
        public AmplifierDefinitionConfigAction(MessageSourceAccessor messageSource) {
                
                super(messageSource);
                setText("action.amplifierDefinitionConfig");
                setToolTip("action.amplifierDefinitionConfigToolTip");
        }

        /**
         * Opens the {@link #configDialog}.
         */
        @Override
        public void actionPerformed(ActionEvent e) {

                logger.debug("Amplifier config");
                configDialog.showDialog(new AmplifierDefinition());
        }

        /**
         * Gets the {@link #configDialog}.
         *
         * @return the {@link #eeglabExportDialog}
         */
        public AmplifierDefinitionConfigDialog getConfigDialog() {

                return configDialog;
        }

        /**
         * Sets the {@link #configDialog}
         *
         * @param configDialog  an {@link AmplifierDefinitionConfigDialog} object
         */
        public void setConfigDialog(AmplifierDefinitionConfigDialog configDialog) {

                if(configDialog == null) {
			throw new NullPointerException();
		}
                this.configDialog = configDialog;
        }
}
