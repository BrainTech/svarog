package org.signalml.app.view.monitor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Table for channel definitions.
 *
 * @author Tomasz Sawicki
 */
public class ChannelDefinitionsTable extends JTable {

        /**
         * Message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * Wheter this table is used in edit g&o dialog (true), or in edit
         * amp def dialog (false).
         */
        private boolean dialog;

        /**
         * Default constructor.
         */
        public ChannelDefinitionsTable(MessageSourceAccessor messageSource, boolean dialog) {

                super();
                setModel(new ChannelDefinitionTableModel(dialog, new ArrayList<ChannelDefinition>(), messageSource));
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                this.messageSource = messageSource;
                this.dialog = dialog;
        }

        /**
         * Adds a definition to the table.
         *
         * @param definition definition to be added
         */
        public void add(ChannelDefinition definition) {

                List<ChannelDefinition> definitions = getDefinitionTableModel().getDefinitions();
                definitions.add(definition);
                setModel(new ChannelDefinitionTableModel(dialog, definitions, messageSource));

        }

        /**
         * Removes selected definition from the table.
         */
        public void removeSelected() {

                int selectedRow = getSelectedRow();
                if (selectedRow < 0) {
                        return;
                }
                List<ChannelDefinition> definitions = getDefinitionTableModel().getDefinitions();
                definitions.remove(selectedRow);
                setModel(new ChannelDefinitionTableModel(dialog, definitions, messageSource));
        }

        /**
         * Sets data.
         *
         * @param definitions definitions list
         */
        public void setData(List<ChannelDefinition> definitions) {

                setModel(new ChannelDefinitionTableModel(dialog, definitions, messageSource));
        }

        /**
         * Gets data.
         *
         * @return data
         */
        public List<ChannelDefinition> getData() {

                return getDefinitionTableModel().getDefinitions();
        }

        /**
         * Gets the definition table model.
         *
         * @return the definition table model
         */
        private ChannelDefinitionTableModel getDefinitionTableModel() {

                return (ChannelDefinitionTableModel) getModel();
        }

        /**
         * Gets the channel number
         *
         * @return list of channel numbers
         */
        public List<Integer> getChannelNumbers() {

                List<Integer> numbers = new ArrayList<Integer>();

                for (ChannelDefinition definition : getData()) {

                        numbers.add(definition.getNumber());
                }

                return numbers;
        }

        /**
         * Gets the gain values
         *
         * @return list of gain values
         */
        public List<Float> getGainValues() {

                List<Float> gain = new ArrayList<Float>();
                for (ChannelDefinition definition : getData()) {

                        gain.add(definition.getGain());
                }
                return gain;
        }

        /**
         * Gets the offset values
         *
         * @return list of channel numbers
         */
        public List<Float> getOffsetValues() {

                List<Float> offset = new ArrayList<Float>();
                for (ChannelDefinition definition : getData()) {

                        offset.add(definition.getOffset());
                }
                return offset;
        }

        /**
         * Gets the the default names.
         *
         * @return list of default names
         */
        public List<String> getDefaultNames() {

                List<String> names = new ArrayList<String>();
                for (ChannelDefinition definition : getData()) {

                        names.add(definition.getDefaultName());
                }
                return names;
        }

        /**
         * Wheter all gain and offset are presented, or only one.
         * All are default.
         *
         * @param all all
         */
        public void setAllEditable(boolean allEditable) {

                ChannelDefinitionTableModel newModel = new ChannelDefinitionTableModel(dialog, getData(), messageSource);
                newModel.setAllGainAndOffset(allEditable);
                setModel(newModel);
        }
}

/**
 * Table model for channel definition table
 * 
 * @author Tomasz Sawicki
 */
class ChannelDefinitionTableModel extends AbstractTableModel {

        private final int CHANNEL_NUMBER = 0;
        private final int CHANNEL_GAIN = 1;
        private final int CHANNEL_OFFSET = 2;
        private final int CHANNEL_NAME = 3;
        /**
         * Wheter this table is used in edit g&o dialog (true), or in edit
         * amp def dialog (false).
         */
        private boolean dialog;
        /**
         * Definitions.
         */
        private List<ChannelDefinition> definitions;
        /**
         * The message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * Wheter all gain and offset are presented, or only one.
         */
        private boolean allGainAndOffset;

        /**
         * Default constructor.
         *
         * @param dialog {@link #dialog}
         * @param definitions {@link #definitions}
         */
        public ChannelDefinitionTableModel(boolean dialog, List<ChannelDefinition> definitions, MessageSourceAccessor messageSource) {

                this.dialog = dialog;
                this.definitions = definitions;
                this.messageSource = messageSource;
                this.allGainAndOffset = true;
        }

        /**
         * The row count.
         *
         * @return the row count
         */
        @Override
        public int getRowCount() {

                return definitions.size();
        }

        /**
         * 3 or 4 columns, depending on {@link #dialog}.
         *
         * @return the column count
         */
        @Override
        public int getColumnCount() {

                return (dialog) ? 3 : 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

                if (columnIndex == CHANNEL_NUMBER) {
                        if (allGainAndOffset) {
                                return definitions.get(rowIndex).getNumber();
                        } else {
                                return messageSource.getMessage("opensignal.parameters.editGainAndOffsetDialog.all");
                        }
                } else if (columnIndex == CHANNEL_GAIN) {
                        return definitions.get(rowIndex).getGain();
                } else if (columnIndex == CHANNEL_OFFSET) {
                        return definitions.get(rowIndex).getOffset();
                } else if (columnIndex == CHANNEL_NAME) {
                        return definitions.get(rowIndex).getDefaultName();
                }
                return null;
        }

        /**
         * Column names.
         *
         * @param column column number
         * @return column name
         */
        @Override
        public String getColumnName(int column) {

                if (column == CHANNEL_NUMBER) {
                        return messageSource.getMessage("amplifierDefinitionConfig.channelno");
                } else if (column == CHANNEL_GAIN) {
                        return messageSource.getMessage("amplifierDefinitionConfig.gain");
                } else if (column == CHANNEL_OFFSET) {
                        return messageSource.getMessage("amplifierDefinitionConfig.offset");
                } else if (column == CHANNEL_NAME) {
                        return messageSource.getMessage("amplifierDefinitionConfig.defaultName");
                }
                return null;
        }

        /**
         * Gets the definitions list.
         *
         * @return definitions list
         */
        public List<ChannelDefinition> getDefinitions() {

                return definitions;
        }

        /**
         * Depending on {@link #editable}.
         */
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {

                if (!dialog) {
                        return true;
                } else if (dialog && (columnIndex == CHANNEL_GAIN || columnIndex == CHANNEL_OFFSET)) {
                        return true;
                } else {
                        return false;
                }
        }

        /**
         * Sets the value.
         *
         * @param aValue new value
         * @param rowIndex row no
         * @param columnIndex col no
         */
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

                if (columnIndex == CHANNEL_NUMBER) {

                        int newValue;

                        try {
                                newValue = Integer.parseInt((String) aValue);
                        } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, messageSource.getMessage("error.amplifierDefinitionConfig.integer"));
                                return;
                        }

                        definitions.get(rowIndex).setNumber(newValue);

                } else if (columnIndex == CHANNEL_GAIN || columnIndex == CHANNEL_OFFSET) {

                        float newValue;

                        try {
                                newValue = Float.parseFloat((String) aValue);
                        } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, messageSource.getMessage("error.amplifierDefinitionConfig.rational"));
                                return;
                        }

                        if (columnIndex == CHANNEL_GAIN) {
                                definitions.get(rowIndex).setGain(newValue);
                        } else if (columnIndex == CHANNEL_OFFSET) {
                                definitions.get(rowIndex).setOffset(newValue);
                        }

                } else if (columnIndex == CHANNEL_NAME) {

                        definitions.get(rowIndex).setDefaultName((String) aValue);
                }
        }

        /**
         * Sets {@link #allGainAndOffset}.
         *
         * @param allGainAndOffset {@link #allGainAndOffset}
         */
        public void setAllGainAndOffset(boolean allGainAndOffset) {

                this.allGainAndOffset = allGainAndOffset;
        }
}
