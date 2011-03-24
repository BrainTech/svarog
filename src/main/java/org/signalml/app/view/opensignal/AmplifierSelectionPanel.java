package org.signalml.app.view.opensignal;

import org.signalml.app.model.AmplifierConnectionDescriptor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.worker.amplifiers.AmplifierDefinition;
import org.signalml.app.worker.amplifiers.AmplifierDiscoveryWorker;
import org.signalml.app.worker.amplifiers.AmplifierInstance;
import org.signalml.app.worker.amplifiers.DiscoveryState;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Allows to choose an amplifier from a list.
 *
 * @author Tomasz Sawicki
 */
public class AmplifierSelectionPanel extends JPanel implements PropertyChangeListener {

        /**
         * List of frequencies.
         */
        private JList amplifiersList;
        /**
         * Messages text area.
         */
        private JTextArea messagesTextArea;
        /**
         * Refresh button.
         */
        private JButton refreshButton;

        /**
         * Message source.
         */
        private MessageSourceAccessor messageSource;
        /**
         * Viewer element manager.
         */
        private ViewerElementManager elementManager;
        /**
         * Amplifier signal source panel.
         */
        private AmplifierSignalSourcePanel sourcePanel;
        /**
         * Current descriptor.
         */
        private AmplifierConnectionDescriptor currentDescriptor;
        /**
         * Current discovery worker.
         */
        private AmplifierDiscoveryWorker currentWorker;
        /**
         * The selection listener for amp list.
         */
        private ListSelectionListener selectionListener;
        /**
         * The progress bar.
         */
        private JProgressBar progressBar;

        /**
         * Default constructor.
         *
         * @param messageSource {@link #messageSource}
         * @param elementManager {@link #elementManager}
         * @param sourcePanel {@link #sourcePanel}
         */
        public AmplifierSelectionPanel(MessageSourceAccessor messageSource,
                ViewerElementManager elementManager,
                AmplifierSignalSourcePanel sourcePanel) {

                super();

                this.messageSource = messageSource;
                this.elementManager = elementManager;
                this.sourcePanel = sourcePanel;

                createInterface();

                currentDescriptor = new AmplifierConnectionDescriptor();
        }

        /**
         * Fills an {@link AmplifierConnectionDescriptor} from this panel.
         * 
         * @param amplifierConnectionDescriptor the descriptor
         */
        public void fillModelFromPanel(AmplifierConnectionDescriptor descriptor) {

                int selectedIndex = getAmplifiersList().getSelectedIndex();

                if (selectedIndex < 0) {
                        descriptor.setAmplifierInstance(null);
                } else {
                        AmplifierInstance selectedInstance;
                        selectedInstance = (AmplifierInstance) getAmplifiersList().getModel().getElementAt(selectedIndex);
                        descriptor.setAmplifierInstance(selectedInstance);
                }
        }

        /**
         * Fills this panel from an {@link AmplifierConnectionDescriptor}.
         *
         * @param descriptor the descriptor
         * @throws AmplifierNotFoundException when the amplifier from the descriptor cannot be found
         */
        public void fillPanelFromModel(AmplifierConnectionDescriptor descriptor) throws SignalMLException {

                if (descriptor.isBciStarted()) {
                        setEnabledAll(false);
                } else {
                        setEnabledAll(true);
                }

                // TODO: refresh amp list and try to find the one from the descriptor

                currentDescriptor = descriptor;
        }

        /**
         * Refreshes the list.
         */
        private void refresh() {

                getAmplifiersList().setListData(new String[]{});
                getMessageTextArea().setText("");
                amplifierSelected();

                List<AmplifierDefinition> definitions;
                try {
                        definitions = elementManager.getAmplifierDefinitionPresetManager().getDefinitionList();
                } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, messageSource.getMessage("amplifierSelection.consistency"));
                        return;
                }

                if (currentWorker != null) {
                        currentWorker.cancelSearch();
                }

                getProgressBar().setVisible(true);

                currentWorker = new AmplifierDiscoveryWorker(messageSource, definitions);
                currentWorker.addPropertyChangeListener(this);
                currentWorker.execute();
        }

        /**
         * Called when device search is over.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (AmplifierDiscoveryWorker.DISCOVERY_STATE.equals(evt.getPropertyName())) {

                        DiscoveryState discoveryState = (DiscoveryState) evt.getNewValue();

                        if (discoveryState.getInstance() != null) {
                                addToList(discoveryState.getInstance());
                        }

                        if (discoveryState.getMessage() != null) {
                                addMessage(discoveryState.getMessage());
                                if (discoveryState.getMessage().equals(messageSource.getMessage("amplifierSelection.searchCompleted")))
                                        getProgressBar().setVisible(false);
                        }
                }
        }

        /**
         * Creates the interface.
         */
        private void createInterface() {

                setLayout(new BorderLayout(10, 10));

                CompoundBorder amplifiersListBorder = new CompoundBorder(
                        new TitledBorder(messageSource.getMessage("amplifierSelection.amplifiersList")),
                        new EmptyBorder(3, 3, 3, 3));

                JPanel amplifiersListPanel = new JPanel(new BorderLayout(10, 10));
                amplifiersListPanel.setLayout(new BorderLayout(10, 10));
                amplifiersListPanel.setBorder(amplifiersListBorder);
                amplifiersListPanel.add(new JScrollPane(getAmplifiersList()), BorderLayout.CENTER);

                JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
                JScrollPane scrollPane = new JScrollPane(getMessageTextArea());
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                bottomPanel.add(scrollPane, BorderLayout.CENTER);

                JPanel bottomMostPanel = new JPanel(new GridLayout(1, 2));
                JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                progressPanel.add(getProgressBar());
                JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                refreshPanel.add(getRefreshButton());
                bottomMostPanel.add(progressPanel);
                bottomMostPanel.add(refreshPanel);

                bottomPanel.add(bottomMostPanel, BorderLayout.PAGE_END);

                amplifiersListPanel.add(bottomPanel, BorderLayout.PAGE_END);
                add(amplifiersListPanel, BorderLayout.CENTER);
        }        

        /**
         * Gets the amplifiers list.
         *
         * @return the amplifiers list
         */
        private JList getAmplifiersList() {

                if (amplifiersList == null) {
                        amplifiersList = new JList();
                        amplifiersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        amplifiersList.addListSelectionListener(getSelectionListener());
                }
                return amplifiersList;
        }

        /**
         * Adds an amplifier instance to the list.
         *
         * @param instance instance to be added
         */
        private void addToList(AmplifierInstance instance) {

                getAmplifiersList().removeListSelectionListener(getSelectionListener());
                int selectedIndex = getAmplifiersList().getSelectedIndex();                

                List<AmplifierInstance> instances = new ArrayList<AmplifierInstance>();
                for (int i = 0; i < getAmplifiersList().getModel().getSize(); i++) {
                        instances.add((AmplifierInstance) getAmplifiersList().getModel().getElementAt(i));
                }
                instances.add(instance);

                getAmplifiersList().setListData(instances.toArray());                
                getAmplifiersList().setSelectedIndex(selectedIndex);
                getAmplifiersList().addListSelectionListener(getSelectionListener());
        }

        /**
         * Gets the message text area.
         * 
         * @return the message text area
         */
        private JTextArea getMessageTextArea() {

                if (messagesTextArea == null) {
                        messagesTextArea = new JTextArea(2, 1);
                        messagesTextArea.setEditable(false);
                }
                return messagesTextArea;
        }

        /**
         * Adds a message to the message text area.
         *
         * @param message the message
         */
        private void addMessage(String message) {

                if (!getMessageTextArea().getText().equals("")) {
                        message = "\n" + message;
                }
                getMessageTextArea().append(message);
        }

        /**
         * Called when an amplifier is selected.
         */
        private void amplifierSelected() {

                fillModelFromPanel(currentDescriptor);
                if (currentDescriptor.getAmplifierInstance() != null) {
                        currentDescriptor.getOpenMonitorDescriptor().fillFromAnAmplifierDefinition(
                                currentDescriptor.getAmplifierInstance().getDefinition());
                }
                try {
                        sourcePanel.fillPanelFromModel(currentDescriptor, true);
                } catch (SignalMLException ex) {
                }
        }

        /**
         * Gets the refresh button.
         *
         * @return the refresh button
         */
        private JButton getRefreshButton() {

                if (refreshButton == null) {
                        refreshButton = new JButton(new AbstractAction() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                        refresh();
                                }
                        });
                        refreshButton.setText(messageSource.getMessage("amplifierSelection.refresh"));
                }
                return refreshButton;
        }

        /**
        }

        /**
         * Gets the progress bar.
         *
         * @return the progress bar
         */
        public JProgressBar getProgressBar() {
                
                if (progressBar == null) {
                        progressBar = new JProgressBar();
                        progressBar.setIndeterminate(true);
                        progressBar.setVisible(false);
                }
                return progressBar;
        }

        /**
         * Gets the selection listener.
         *
         * @return the selection listener
         */
        private ListSelectionListener getSelectionListener() {

                if (selectionListener == null) {
                        selectionListener = new ListSelectionListener() {

                                @Override
                                public void valueChanged(ListSelectionEvent e) {
                                        amplifierSelected();
                                }
                        };
                }
                return selectionListener;
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
