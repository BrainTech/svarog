package org.signalml.app.view.opensignal;

import org.signalml.app.model.AmplifierConnectionDescriptor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.monitor.ChannelDefinition;
import org.signalml.app.view.monitor.ChannelDefinitionPanel;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
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
         * Dialog can be filled from {@link OpenMonitorDescriptor}, {@link RawSignalDescriptor}
         * or {@link AmplifierConnectionDescriptor}.
         *
         * @param clazz checked model
         * @return true if SignalParametersDescriptor is assignable from clazz
         */
        @Override
        public boolean supportsModelClass(Class<?> clazz) {

                boolean isAmpConnection = AmplifierConnectionDescriptor.class.isAssignableFrom(clazz);
                boolean isBCIConnection = OpenMonitorDescriptor.class.isAssignableFrom(clazz);
                boolean isFile = RawSignalDescriptor.class.isAssignableFrom(clazz);

                return isAmpConnection || isBCIConnection || isFile;
        }

        /**
         * Fills the dialog from a {@link SignalParametersDescriptor} object.
         * Only calls {@link EditDefinitionPanel#fillPanelFromModel()}
         *
         * @param model the descriptor
         */
        @Override
        public void fillDialogFromModel(Object model) {

                getEditDefinitionPanel().fillPanelFromModel(model);
        }

        /**
         * Fills a {@link SignalParametersDescriptor} from the dialog.
         * Only calls {@link EditDefinitionPanel#fillModelFromPanel()}
         *
         * @param model the descriptor
         */
        @Override
        public void fillModelFromDialog(Object model) {

                getEditDefinitionPanel().fillModelFromPanel(model);
        }

        /**
         * Validates the dialog by trying to fill the model from it.
         *
         * @param model model
         * @param errors errors
         * @throws SignalMLException thrown when data is invalid
         */
        @Override
        public void validateDialog(Object model, Errors errors) throws SignalMLException {

                if (!getEditDefinitionPanel().isAllGainAndOffsetEditable()) {

                        try {
                                Float.parseFloat(getEditDefinitionPanel().getChannelGainVaule());
                                Float.parseFloat(getEditDefinitionPanel().getChannelOffsetValue());
                        } catch (NumberFormatException ex) {
                                errors.reject("error.invalidData");
                        }
                }
        }

        /**
         * Panel that allows the definition of gain and offset, it's derived
         * from {@link ChannelDefinigaintionPanel}.
         */
        private class EditDefinitionPanel extends ChannelDefinitionPanel implements ListSelectionListener {

                /**
                 * If all gain and offset are editable.gain
                 */
                private boolean allGainAndOffsetEditable;

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
                        setAllGainAndOffsetEditable(true);
                        getDefaultNameLabel().setVisible(false);
                        getDefaultNameTextField().setVisible(false);
                }

                /**
                 * When a list item is selected fill the fields.
                 *
                 * @param e list selection event
                 */
                @Override
                public void valueChanged(ListSelectionEvent e) {

                        int selectedIndex = getDefinitionsList().getSelectedIndex();
                        if (selectedIndex < 0) {
                                return;
                        }
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
                                if (definition == null) {
                                        return;
                                }

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
                                getDefinitionsList().setListData(new String[]{""});
                                getDefinitionsList().setEnabled(false);
                                getChannelTextField().setText(messageSource.getMessage("opensignal.parameters.editGainAndOffsetDialog.all"));
                        }
                        allGainAndOffsetEditable = editable;
                }

                /**
                 * Returns {@link #allGainAndOffsetEditable}.
                 *
                 * @return {@link #allGainAndOffsetEditable}
                 */
                public boolean isAllGainAndOffsetEditable() {

                        return allGainAndOffsetEditable;
                }

                /**
                 * Returns gain textfield value.
                 *
                 * @return gain textfield value
                 */
                public String getChannelGainVaule() {

                        return getGainTextField().getText();
                }

                /**
                 * Returns offset textfield value.
                 *
                 * @return offset textfield value.
                 */
                public String getChannelOffsetValue() {

                        return getOffsetTextField().getText();
                }

                /**
                 * Fills this panel from a model.
                 *
                 * @param model the model
                 */
                public void fillPanelFromModel(Object model) {

                        if (model instanceof AmplifierConnectionDescriptor) {
                                fillPanelForAmplifierConnection((AmplifierConnectionDescriptor) model);
                        } else if (model instanceof OpenMonitorDescriptor) {
                                fillPanelForOpenBCIConnection((OpenMonitorDescriptor) model);
                        } else if (model instanceof RawSignalDescriptor) {
                                fillPanelForFileOpening((RawSignalDescriptor) model);
                        }
                }

                /**
                 * Fills the panel for amplifier connection.
                 *
                 * @param descriptor the descriptor
                 */
                private void fillPanelForAmplifierConnection(AmplifierConnectionDescriptor descriptor) {

                        setAllGainAndOffsetEditable(true);
                        List<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();
                        for (int i = 0; i < descriptor.getOpenMonitorDescriptor().getChannelCount(); i++) {

                                definitions.add(new ChannelDefinition(
                                        descriptor.getAmplifierInstance().getDefinition().getChannelNumbers().get(i),
                                        descriptor.getOpenMonitorDescriptor().getCalibrationGain()[i],
                                        descriptor.getOpenMonitorDescriptor().getCalibrationOffset()[i],
                                        descriptor.getAmplifierInstance().getDefinition().getDefaultNames().get(i)));
                        }
                        getDefinitionsList().setListData(definitions.toArray());
                }

                /**
                 * Fills the panel for BCI connection.
                 *
                 * @param descriptor the descriptor
                 */
                private void fillPanelForOpenBCIConnection(OpenMonitorDescriptor descriptor) {

                        setAllGainAndOffsetEditable(true);
                        List<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();
                        for (int i = 0; i < descriptor.getChannelCount(); i++) {

                                definitions.add(new ChannelDefinition(
                                        i,
                                        descriptor.getCalibrationGain()[i],
                                        descriptor.getCalibrationOffset()[i],
                                        descriptor.getChannelLabels()[i]));
                        }
                        getDefinitionsList().setListData(definitions.toArray());
                }

                /**
                 * Fills the panel for file opening.
                 *
                 * @param descriptor the descriptor
                 */
                private void fillPanelForFileOpening(RawSignalDescriptor descriptor) {

                        if (descriptor.getChannelLabels() != null) {
                                setAllGainAndOffsetEditable(true);
                                List<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();
                                for (int i = 0; i < descriptor.getChannelCount(); i++) {

                                        definitions.add(new ChannelDefinition(
                                                i,
                                                descriptor.getCalibrationGain()[i],
                                                descriptor.getCalibrationOffset()[i],
                                                descriptor.getChannelLabels()[i]));
                                }
                                getDefinitionsList().setListData(definitions.toArray());
                        }
                }

                /**
                 * Fills a model object from this panel.
                 * Sets channel numbers, gain and offset.
                 *
                 * @param model the model
                 * @throws SignalMLException when input data is invalid
                 */
                public void fillModelFromPanel(Object model) {

                        if (model instanceof AmplifierConnectionDescriptor) {
                                fillModelForAmplifierConnection((AmplifierConnectionDescriptor) model);
                        } else if (model instanceof OpenMonitorDescriptor) {
                                fillModelForOpenBCIConnection((OpenMonitorDescriptor) model);
                        } else if (model instanceof RawSignalDescriptor) {
                                fillModelForFileOpening((RawSignalDescriptor) model);
                        }
                }

                /**
                 * Fills the model for amplifier connection.
                 *
                 * @param descriptor the descriptor
                 */
                private void fillModelForAmplifierConnection(AmplifierConnectionDescriptor descriptor) {

                        descriptor.getOpenMonitorDescriptor().setCalibrationGain(getGainArray());
                        descriptor.getOpenMonitorDescriptor().setCalibrationOffset(getOffsetArray());
                }

                /**
                 * Fills the model for BCI connection.
                 *
                 * @param descriptor the descriptor
                 */
                private void fillModelForOpenBCIConnection(OpenMonitorDescriptor descriptor) {

                        descriptor.setCalibrationGain(getGainArray());
                        descriptor.setCalibrationOffset(getOffsetArray());
                }

                /**
                 * Fills the model for file opening.
                 *
                 * @param descriptor the descriptor
                 */
                private void fillModelForFileOpening(RawSignalDescriptor descriptor) {

                        descriptor.setCalibrationGain(getGainArray());
                        descriptor.setCalibrationOffset(getOffsetArray());
                }

                /**
                 * Gets gain as a float array.
                 *
                 * @return gain as a float array
                 */
                private float[] getGainArray() {

                        Float[] gainFromList = getGainValues().toArray(new Float[0]);
                        float[] gain = new float[gainFromList.length];
                        for (int i = 0; i < gainFromList.length; i++) {
                                gain[i] = gainFromList[i];
                        }
                        return gain;
                }

                /**
                 * Gets offset as a float array.
                 *
                 * @return offset as a float array
                 */
                private float[] getOffsetArray() {

                        Float[] offsetFromList = getOffsetValues().toArray(new Float[0]);
                        float[] offset = new float[offsetFromList.length];
                        for (int i = 0; i < offsetFromList.length; i++) {
                                offset[i] = offsetFromList[i];
                        }
                        return offset;
                }
        }
}
