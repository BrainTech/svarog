package org.signalml.app.view.opensignal;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.monitor.ChannelDefinition;
import org.signalml.app.view.monitor.ChannelDefinitionsTable;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/**
 * A dialog that allows to edit gain and offset.
 *
 * @author Tomasz Sawicki
 */
public class EditGainAndOffsetDialog extends AbstractDialog  {

        /**
         * The definition table.
         */
        private ChannelDefinitionsTable definitionsTable;

        /**
         * Default constructor sets window's title.
         */
        public EditGainAndOffsetDialog(Window w, boolean m) {

                super(w, m);
                setTitle(_("Edit gain and offset"));
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
         *
         * @param model the descriptor
         */
        @Override
        public void fillDialogFromModel(Object model) {

                if (model instanceof AmplifierConnectionDescriptor) {
                        fillDialogForAmplifierConnection((AmplifierConnectionDescriptor) model);
                } else if (model instanceof OpenMonitorDescriptor) {
                        fillDialogForOpenBCIConnection((OpenMonitorDescriptor) model);
                } else if (model instanceof RawSignalDescriptor) {
                        fillDialogForFileOpening((RawSignalDescriptor) model);
                }
        }

        /**
         * Fills a {@link SignalParametersDescriptor} from the dialog.
         *
         * @param model the descriptor
         */
        @Override
        public void fillModelFromDialog(Object model) {

                if (model instanceof AmplifierConnectionDescriptor) {
                        fillModelForAmplifierConnection((AmplifierConnectionDescriptor) model);
                } else if (model instanceof OpenMonitorDescriptor) {
                        fillModelForOpenBCIConnection((OpenMonitorDescriptor) model);
                } else if (model instanceof RawSignalDescriptor) {
                        fillModelForFileOpening((RawSignalDescriptor) model);
                }
        }

        /**
         * Fills the dialog for amplifier connection.
         *
         * @param descriptor the descriptor
         */
        private void fillDialogForAmplifierConnection(AmplifierConnectionDescriptor descriptor) {

                setAllGainAndOffsetEditable(true);
                List<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();
                for (int i = 0; i < descriptor.getOpenMonitorDescriptor().getChannelCount(); i++) {

                        definitions.add(new ChannelDefinition(
                                descriptor.getAmplifierInstance().getDefinition().getChannelNumbers().get(i),
                                descriptor.getOpenMonitorDescriptor().getCalibrationGain()[i],
                                descriptor.getOpenMonitorDescriptor().getCalibrationOffset()[i],
                                descriptor.getAmplifierInstance().getDefinition().getDefaultNames().get(i)));
                }

                getDefinitionsTable().setData(definitions);
        }

        /**
         * Fills the dialog for BCI connection.
         *
         * @param descriptor the descriptor
         */
        private void fillDialogForOpenBCIConnection(OpenMonitorDescriptor descriptor) {

                setAllGainAndOffsetEditable(true);
                List<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();
                for (int i = 0; i < descriptor.getChannelCount(); i++) {

                        definitions.add(new ChannelDefinition(
                                i,
                                descriptor.getCalibrationGain()[i],
                                descriptor.getCalibrationOffset()[i],
                                descriptor.getChannelLabels()[i]));
                }

                getDefinitionsTable().setData(definitions);
        }

        /**
         * Fills the dialog for file opening.
         *
         * @param descriptor the descriptor
         */
        private void fillDialogForFileOpening(RawSignalDescriptor descriptor) {

                if (descriptor.getChannelLabels() != null) {
                        setAllGainAndOffsetEditable(true);
                        List<ChannelDefinition> definitions = new ArrayList<ChannelDefinition>();
                        for (int i = 0; i < descriptor.getChannelCount(); i++) {

                                definitions.add(new ChannelDefinition(
                                        i+1,
                                        descriptor.getCalibrationGain()[i],
                                        descriptor.getCalibrationOffset()[i],
                                        descriptor.getChannelLabels()[i]));
                        }
                        getDefinitionsTable().setData(definitions);
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

                Float[] gainFromList = getDefinitionsTable().getGainValues().toArray(new Float[0]);
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

                Float[] offsetFromList = getDefinitionsTable().getOffsetValues().toArray(new Float[0]);
                float[] offset = new float[offsetFromList.length];
                for (int i = 0; i < offsetFromList.length; i++) {
                        offset[i] = offsetFromList[i];
                }
                return offset;
        }

        /**
         * Creates the interface (the {@link #editDefinitionPanel}
         *
         * @return the interface panel
         */
        @Override
        protected JComponent createInterface() {

                JPanel mainPanel = new JPanel(new BorderLayout());
                CompoundBorder border = new CompoundBorder(
                        new TitledBorder(""),
                        new EmptyBorder(3, 3, 3, 3));
                mainPanel.setBorder(border);
                mainPanel.setLayout(new BorderLayout(10, 10));
                mainPanel.add(new JScrollPane(getDefinitionsTable()), BorderLayout.CENTER);

                return mainPanel;
        }

        /**
         * Returns the edit definition panel.
         *
         * @return the edit definiiton panel
         */
        private ChannelDefinitionsTable getDefinitionsTable() {

                if (definitionsTable == null) {
                        definitionsTable = new ChannelDefinitionsTable(true);
                }
                return definitionsTable;
        }

        /**
         * Wheter all gain and offset values can be edited.
         *
         * @param editable if true, all values can be edited. If false,
         * only one value can be edited and will be applied to all channels.
         */
        private void setAllGainAndOffsetEditable(boolean editable) {
                
                if (!editable) {
                        List<ChannelDefinition> list = new ArrayList<ChannelDefinition>();
                        list.add(new ChannelDefinition(0, 0.0f, 0.0f, "0"));
                        getDefinitionsTable().setData(list);
                }
                getDefinitionsTable().setAllEditable(editable);
        }

        /**
         * Gets the gain. Used only if only one gain and offset is editable.
         *
         * @return the gain
         */
        private float getGain() {

                return getDefinitionsTable().getData().get(0).getGain();
        }

        /**
         * Gets the offset. Used only if only one gain and offset is editable.
         *
         * @return the offset
         */
        private float getOffset() {

                return getDefinitionsTable().getData().get(0).getOffset();
        }

        /**
         * Sets the gain. Used only if only one gain and offset is editable.
         *
         * @param gain the gain
         */
        private void setGain(float gain) {

                ChannelDefinition definition = getDefinitionsTable().getData().get(0);
                definition.setGain(gain);
                List<ChannelDefinition> list = new ArrayList<ChannelDefinition>();
                list.add(definition);
                getDefinitionsTable().setData(list);
                getDefinitionsTable().setAllEditable(false);
        }

        /**
         * Sets the offset. Used only if only one gain and offset is editable.
         *
         * @param offset the offset
         */
        private void setOffset(float offset) {

                ChannelDefinition definition = getDefinitionsTable().getData().get(0);
                definition.setOffset(offset);
                List<ChannelDefinition> list = new ArrayList<ChannelDefinition>();
                list.add(definition);
                getDefinitionsTable().setData(list);
                getDefinitionsTable().setAllEditable(false);
        }
}
