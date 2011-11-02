package org.signalml.app.action;

import java.awt.event.ActionEvent;
import org.signalml.app.view.monitor.OpenBCIModuleConfigDialog;
import org.signalml.app.worker.processes.OpenBCIModule;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.apache.log4j.Logger;

/**
 * Open an {@link OpenBCIModulesConfigDialog}.
 *
 * @author Tomasz Sawicki
 */
public class OpenBCIModuleConfigAction extends AbstractSignalMLAction {

        /**
	 * Logger to save history of execution at.
	 */
        protected static final Logger logger = Logger.getLogger(OpenBCIModuleConfigAction.class);

        /**
	 * The dialog which is shown after evoking this action.
	 */
        private OpenBCIModuleConfigDialog configDialog;

        /**
         * Constructor sets text and tooltip.
         *
         */
        public  OpenBCIModuleConfigAction() {

                super();
                setText("action.openBCIModulesConfig");
                setToolTip("action.openBCIModulesConfigToolTip");
        }

        /**
         * Opens the {@link #configDialog}.
         */
        @Override
        public void actionPerformed(ActionEvent e) {

                logger.debug("Amplifier config");
                configDialog.showDialog(new OpenBCIModule(), true);
        }

        /**
         * Gets the {@link #configDialog}.
         *
         * @return the {@link #eeglabExportDialog}
         */
        public OpenBCIModuleConfigDialog getConfigDialog() {

                return configDialog;
        }

        /**
         * Sets the {@link #configDialog}
         *
         * @param configDialog  an {@link AmplifierDefinitionConfigDialog} object
         */
        public void setConfigDialog(OpenBCIModuleConfigDialog configDialog) {

                if(configDialog == null) {
			throw new NullPointerException();
		}
                this.configDialog = configDialog;
        }
}
