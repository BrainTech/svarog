package org.signalml.app.view.opensignal;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.view.monitor.ChannelDefinition;
import org.signalml.app.view.monitor.ChannelDefinitionPanel;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * A dialog that allows to edit gain and offset.
 *
 * @author Tomasz Sawicki
 */
public class EditGainAndOffsetDialog extends AbstractDialog {

        /**
         * The main panel.
         */
        private EditDefinitionPanel editDefinitionPanel;

        /**
         * Default constructor sets window's title.
         */
        public EditGainAndOffsetDialog(MessageSourceAccessor messageSource, Window w, boolean m) {

                super(messageSource, w, m);
                setTitle(messageSource.getMessage("opensignal.parameters.editGainAndOffset"));
        }

        /**
         * Creates the interface (the {@link #editDefinitionPanel}
         *
         * @return the interface panel
         */
        @Override
        protected JComponent createInterface() {

                return getEditDefinitionPanel();
        }

        /**
         * Returns the edit definition panel.
         *
         * @return the edit definiiton panel
         */
        private EditDefinitionPanel getEditDefinitionPanel() {

                if (editDefinitionPanel == null) {
                        editDefinitionPanel = new EditDefinitionPanel(messageSource);
                }
                return editDefinitionPanel;
        }

        /**
         * Only supported model is a {@link SignalParametersDescriptor}.
         *
         * @param clazz checked model
         * @return true if SignalParametersDescriptor is assignable from clazz
         */
        @Override
        public boolean supportsModelClass(Class<?> clazz) {

                boolean isAmpConnection = AmplifierConnectionDescriptor.class.isAssignableFrom(clazz);
                // TODO: add other classes
                
                return isAmpConnection;
        }

        /**
         * Fills the dialog from a {@link SignalParametersDescriptor} object.
         * Only calls {@link EditDefinitionPanel#fillPanelFromModel()}
         *
         * @param model the descriptor
         */
        @Override
        public void fillDialogFromModel(Object model) throws SignalMLException {

                getEditDefinitionPanel().fillPanelFromModel(model);
        }

        /**
         * Fills a {@link SignalParametersDescriptor} from the dialog.
         * Only calls {@link EditDefinitionPanel#fillModelFromPanel()}
         *
         * @param model the descriptor
         * @throws SignalMLException when input data is invalid
         */
        @Override
        public void fillModelFromDialog(Object model) throws SignalMLException {

                getEditDefinitionPanel().fillModelFromPanel(model);
        }

        /**
         * Validates the dialog by trying to fill the model from it.
         *
         * @param model current model
         * @param errors errors
         * @throws SignalMLException thrown when data is invalid
         */
        @Override
        public void validateDialog(Object model, Errors errors) throws SignalMLException {

                try {
                        fillModelFromDialog(model);
                } catch (SignalMLException ex) {
                        if (ex.getMessage().equals(messageSource.getMessage("error.invalidData"))) {
                                errors.reject("error.invalidData");
                        }
                }
        }

        /**
         * Panel that allows the definition of gain and offset, it's derived
         * from {@link ChannelDefinitionPanel}.
         */
        private class EditDefinitionPanel extends ChannelDefinitionPanel implements ListSelectionListener {

                /**
                 * Default constructor.
                 *
                 * @param messageSource the message source
                 */
                public EditDefinitionPanel(MessageSourceAccessor messageSource) {

                        super(messageSource);
                        initialize();
                }

                /**
                 * Initializes the panel.
                 */
                private void initialize() {

                        getRemoveButton().setVisible(false);
                        getAddButton().setText(messageSource.getMessage("opensignal.parameters.editGainAndOffsetDialog.edit"));
                        getChannelTextField().setEditable(false);
                        getDefinitionsList().addListSelectionListener(this);
                }

                /**
                 * When a list item is selected fill the fields.
                 *
                 * @param e list selection event
                 */
                @Override
                public void valueChanged(ListSelectionEvent e) {

                        int selectedIndex = getDefinitionsList().getSelectedIndex();
                        if (selectedIndex < 0) return;
                        ChannelDefinition definition = (ChannelDefinition) getDefinitionsList().getModel().getElementAt(selectedIndex);
                        getChannelTextField().setText(String.valueOf(definition.getNumber()));
                        getGainTextField().setText(String.valueOf(definition.getGain()));
                        getOffsetTextField().setText(String.valueOf(definition.getOffset()));
                }

                /**
                 * When edit button is clicked edit the list.
                 *
                 * @param e action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {

                        if (getAddButton().equals(e.getSource())) {

                                ChannelDefinition definition = validateFields();
                                if (definition == null) return;

                                List<ChannelDefinition> definitions = getChannelDefinitions();

                                for (int i = 0; i < definitions.size(); i++) {

                                        if (definitions.get(i).getNumber() == definition.getNumber()) {

                                                definitions.get(i).setGain(definition.getGain());
                                                definitions.get(i).setOffset(definition.getOffset());
                                                break;
                                        }
                                }

                                getDefinitionsList().setListData(definitions.toArray());
                                clearTextFields();
                        }
                }

                /**
                 * Wheter all gain and offset values can be edited.
                 *
                 * @param editable if true, all values can be edited. If false,
                 * only one value can be edited and will be applied to all channels.
                 */
                private void setAllGainAndOffsetEditable(boolean editable) {

                        if (editable) {
                                getAddButton().setVisible(true);
                                getDefinitionsList().setEnabled(true);
                                getChannelTextField().setText("");
                        } else {
                                getAddButton().setVisible(false);
                                getDefinitionsList().setListData(new String[] { "" });
                                getDefinitionsList().setEnabled(false);
                                getChannelTextField().setText(messageSource.getMessage("opensignal.parameters.editGainAndOffsetDialog.all"));
                        }
                }

                /**
                 * Fills this panel from a model
                 *
                 * @param model the model
                 * @throws SignalMLException when model is not uspported
                 */
                public void fillPanelFromModel(Object model) throws SignalMLException {
                                
                }

                /**
                 * Fills a model object from this panel.
                 * Sets channel numbers, gain and offset.
                 *
                 * @param model the model
                 * @throws SignalMLException when input data is invalid or model is not supported
                 */
                public void fillModelFromPanel(Object model) throws SignalMLException {

                }
        }
}