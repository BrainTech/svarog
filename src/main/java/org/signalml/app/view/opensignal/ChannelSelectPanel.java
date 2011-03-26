package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.view.opensignal.elements.ChannelSelectTable;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class represents a panel for selecting channels which will be monitored.
 *
 * @author Piotr Szachewicz
 */
public class ChannelSelectPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        /**
         * Logger to save history of execution at.
         */
        protected static final Logger logger = Logger.getLogger(ChannelSelectPanel.class);
        /**
         * The {@link MessageSourceAccessor source} of messages (labels) for elements.
         */
        private MessageSourceAccessor messageSource;
        /**
         * A list on which selections can be made.
         */
        private ChannelSelectTable channelSelectTable;
        /**
         * Button for selecting all channels on the list.
         */
        private JButton selectAllButton;
        /**
         * Button for deselecting all channels on the list.
         */
        private JButton clearSelectionButton;

        /**
         * This is the default constructor.
         * @param messageSource {@link #messageSource}
         */
        public ChannelSelectPanel(MessageSourceAccessor messageSource) {
                super();
                this.messageSource = messageSource;
                initialize();
        }

        /**
         * This method initializes this panel.
         */
        private void initialize() {
                setLayout(new BorderLayout());
                add(new JScrollPane(getChannelSelectTable()), BorderLayout.CENTER);

                CompoundBorder border = new CompoundBorder(
                        new TitledBorder(messageSource.getMessage("openMonitor.channelSelectPanelTitle")),
                        new EmptyBorder(3, 3, 3, 3));
                setBorder(border);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                buttonPanel.add(getSelectAllButton());
                buttonPanel.add(getClearSelectionButton());

                add(buttonPanel, BorderLayout.SOUTH);

        }

        /**
         * Returns the list of channels which were selected using this panel.
         * @return the list of selected channels
         */
        public ChannelSelectTable getChannelSelectTable() {
                if (channelSelectTable == null) {
                        channelSelectTable = new ChannelSelectTable();
                }
                return channelSelectTable;
        }

        /**
         * Returns the button for selecting all channels.
         * @return the button which is useful for selecting all channels from
         * the list.
         */
        public JButton getSelectAllButton() {
                if (selectAllButton == null) {
                        selectAllButton = new JButton(new AbstractAction(messageSource.getMessage("openMonitor.channelSelectPanel.selectAll")) {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        setAllSelected(true);
                                }
                        });
                }
                return selectAllButton;
        }

        /**
         * Returns the button for deselecting all positions in the list.
         * @return the button which can be used to clear all selections made
         * on the list.
         */
        public JButton getClearSelectionButton() {
                if (clearSelectionButton == null) {
                        clearSelectionButton = new JButton(new AbstractAction(messageSource.getMessage("openMonitor.channelSelectPanel.clearSelection")) {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        setAllSelected(false);
                                }
                        });
                }
                return clearSelectionButton;
        }

        /**
         * Fills this panel from an {@link AmplifierConnectionDescriptor} object.
         * @param descriptor
         */
        public void fillPanelFromModel(AmplifierConnectionDescriptor descriptor) {
                if (descriptor.getAmplifierInstance() == null || descriptor.isBciStarted()) {
                        setEnabledAll(false);
                } else {
                        setEnabledAll(true);
                }
                getChannelSelectTable().fillTableFromModel(descriptor);
        }

        /**
         * Sets all channels to be selected or not.
         * @param selected selected
         */
        protected void setAllSelected(boolean selected) {
                getChannelSelectTable().setAllSelected(selected);
        }

        /**
         * Fills an {@link AmplifierConnectionDescriptor} object from this panel.
         * @param descriptor
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) {
                getChannelSelectTable().fillModelFromTable(descriptor);
        }

        /**
         * Sets enabled to this panel and all it's children.
         * Clears all fields if enabled == false.
         *
         * @param enabled true or false
         */
        public void setEnabledAll(boolean enabled) {

                setEnabledToChildren(this, enabled);
        }

        /**
         * Sets enabled to a component and all of it's children.
         *
         * @param component target component
         * @param enabled true or false
         * @param omit wheter to omit component
         */
        private void setEnabledToChildren(Component component, boolean enabled) {

                component.setEnabled(enabled);
                if (component instanceof Container) {
                        Component[] children = ((Container) component).getComponents();
                        for (Component child : children) {
                                setEnabledToChildren(child, enabled);
                        }
                }
        }
}
