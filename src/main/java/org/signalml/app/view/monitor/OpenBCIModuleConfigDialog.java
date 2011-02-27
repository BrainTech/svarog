package org.signalml.app.view.monitor;

import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.view.dialog.AbstractPresetDialog;
import org.signalml.app.view.element.FileSelectPanel;
import org.signalml.app.worker.processes.OpenBCIModule;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Allows to configure openbci modules' paths.
 *
 * @author Tomasz Sawicki
 */
public class OpenBCIModuleConfigDialog extends AbstractPresetDialog {

        /**
         * File select panel
         */
        private FileSelectPanel fileSelectPanel = null;

        /**
         * Default constructor.
         *
         * @param messageSource the message source
         * @param presetManager the preset manager
         * @param w parent window
         * @param isModal if this window is modal
         */
        public OpenBCIModuleConfigDialog(MessageSourceAccessor messageSource, PresetManager presetManger, Window w, boolean isModal) {

                super(messageSource, presetManger, w, isModal);
        }

        /**
         * Sets window's title then calls {@link AbstractDialog#initialize()}.
         */
        @Override
        public void initialize() {

                setTitle(messageSource.getMessage("openBCIModuleConfig.title"));
                super.initialize();
        }

        /**
         * Creates the interface.
         *
         * @return the interface
         */
        @Override
        protected JComponent createInterface() {

                JPanel mainPanel = new JPanel();
                CompoundBorder panelBorder = new CompoundBorder(new TitledBorder(""), new EmptyBorder(3, 3, 3, 3));
                mainPanel.setBorder(panelBorder);

                mainPanel.add(getFileSelectPanel());

                return mainPanel;
        }

        @Override
        public void fillDialogFromModel(Object model) throws SignalMLException {

                OpenBCIModule module = (OpenBCIModule) model;
                getFileSelectPanel().setFileName(module.getPath());
        }

        @Override
        public void fillModelFromDialog(Object model) throws SignalMLException {

                OpenBCIModule module = (OpenBCIModule) model;
                module.setPath(getFileSelectPanel().getFileName());
        }

        @Override
        public Preset getPreset() throws SignalMLException {

                OpenBCIModule module = new OpenBCIModule();
                fillModelFromDialog(module);
                return module;
        }

        @Override
        public void setPreset(Preset preset) throws SignalMLException {

                fillDialogFromModel(preset);
        }

        @Override
        public boolean supportsModelClass(Class<?> clazz) {

                return OpenBCIModule.class.isAssignableFrom(clazz);
        }

        @Override
        public boolean isCancellable() {

                return false;
        }

        /**
         * Gets the file select panel.
         *
         * @return the file select panel
         */
        private FileSelectPanel getFileSelectPanel() {

                if (fileSelectPanel == null) {
                        fileSelectPanel = new FileSelectPanel(messageSource, messageSource.getMessage("openBCIModuleConfig.path"));
                        fileSelectPanel.returnRelativePath(true);
                }
                return fileSelectPanel;
        }
        
}
