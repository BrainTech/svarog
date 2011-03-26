package org.signalml.app.view.opensignal;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.view.ViewerElementManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel containing amp config and open bci modules config buttons.
 *
 * @author Piotr Szachewicz
 */
public class ConfigureAmplifiersPanel extends JPanel {

        /**
         * The message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * The viewer element manager.
         */
        private ViewerElementManager viewerElementManager;
        /**
         * Configure modules button.
         */
        private JButton configureModulesButton = null;
        /**
         * Configure definitions button.
         */
        private JButton configureDefinitionsButton = null;

        /**
         * Default constructor.
         *
         * @param messageSource {@link #messageSource}
         * @param viewerElementManager {@link #viewerElementManager}
         */
        public ConfigureAmplifiersPanel(MessageSourceAccessor messageSource,
                ViewerElementManager viewerElementManager) {
                this.messageSource = messageSource;
                this.viewerElementManager = viewerElementManager;
                initialize();
        }

        /**
         * Creates the interface.
         */
        private void initialize() {
                CompoundBorder configBorder = new CompoundBorder(
                        new TitledBorder(messageSource.getMessage("amplifierSelection.config")),
                        new EmptyBorder(3, 3, 3, 3));

                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setBorder(configBorder);
                add(getConfigureDefinitionsButton());
                add(Box.createRigidArea(new Dimension(0, 5)));
                add(getConfigureModulesButton());

        }

        /**
         * Gets the configure definitions button.
         *
         * @return the configure definitions button
         */
        private JButton getConfigureDefinitionsButton() {

                if (configureDefinitionsButton == null) {
                        configureDefinitionsButton = new JButton(viewerElementManager.getAmplifierDefinitionConfigAction());
                        configureDefinitionsButton.setText(messageSource.getMessage("action.amplifierDefinitionConfig"));
                        configureDefinitionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
                return configureDefinitionsButton;
        }

        /**
         * Gets the configure modules button.
         *
         * @return the configure modules button
         */
        private JButton getConfigureModulesButton() {

                if (configureModulesButton == null) {
                        configureModulesButton = new JButton(viewerElementManager.getOpenBCIModuleConfigAction());
                        configureModulesButton.setText(messageSource.getMessage("action.openBCIModulesConfig"));
                        configureModulesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                }
                return configureModulesButton;
        }
}
