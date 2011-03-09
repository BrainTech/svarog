package org.signalml.app.view.monitor;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

/**
 * List responsible for showing channel definition objects.
 *
 * @author Tomasz Sawicki
 */
public class ChannelDefinitionList extends JList {

        /**
         * Default constructor creates the list.
         */
        public ChannelDefinitionList() {

                super();
                setCellRenderer(new ChannelDefinitionListRenderer());
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
}

/**
 * Renderer for a {@link ChannelDefinitionList}.
 * 
 * @author Tomasz Sawicki
 */
class ChannelDefinitionListRenderer extends JComponent implements ListCellRenderer {

        private DefaultListCellRenderer number;
        private DefaultListCellRenderer gain;
        private DefaultListCellRenderer offset;

        public ChannelDefinitionListRenderer() {
                
                number = new DefaultListCellRenderer();
                gain = new DefaultListCellRenderer();
                offset = new DefaultListCellRenderer();
                
                setLayout(new GridLayout(1, 3));
                add(number);
                add(gain);
                add(offset);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                ChannelDefinition definition = (ChannelDefinition) value;
                String noVal = "No: " + definition.getNumber();
                String gainVal = "Gain: " + definition.getGain();
                String offsetVal = "Offset: " + definition.getOffset();

                number.getListCellRendererComponent(list, noVal, index, isSelected, cellHasFocus);
                gain.getListCellRendererComponent(list, gainVal, index, isSelected, cellHasFocus);
                offset.getListCellRendererComponent(list, offsetVal, index, isSelected, cellHasFocus);

                return this;
        }
}