package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Dialog that shows progress of some time consuming operation.
 * It's a Property Change Listener, the only property it reacts to is
 * progressState, it's value is a {@link ProgressState} object.
 *
 * @author Tomasz Sawicki
 */
public class ProgressDialog extends AbstractDialog implements PropertyChangeListener {

        public static final String PROGRESS_STATE = "progressState";
        public final int DIALOG_WIDTH = 380;
        public final int DIALOG_HEIGHT = 270;
        /**
         * The text area.
         */
        private ProgressStateList progressStateList = null;
        /**
         * The progress bar.
         */
        private JProgressBar progressBar = null;
        /**
         * If there was an error.
         */
        private boolean error;
        /**
         * If there was a success.
         */
        private boolean success;

        /**
         * Default constructor.
         *
         * @param messageSource the message source
         * @param w parent window
         * @param isModal if this window is modal
         * @param caption window's caption
         */
        public ProgressDialog(MessageSourceAccessor messageSource, Window w, boolean isModal, String title) {

                super(messageSource, w, isModal);
                setTitle(title);
                setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
                setLocation();
                
        }

        /**
         * Creates the interface.
         *
         * @return the interfacesize
         */
        @Override
        protected JComponent createInterface() {

                JPanel interfacePanel = new JPanel(new BorderLayout(10, 10));
                CompoundBorder panelBorder = new CompoundBorder(new TitledBorder(""), new EmptyBorder(3, 3, 3, 3));
                interfacePanel.setBorder(panelBorder);

                interfacePanel.add(new JScrollPane(getProgressStateList()), BorderLayout.CENTER);
                interfacePanel.add(getProgressBar(), BorderLayout.PAGE_END);

                return interfacePanel;
        }

        /**
         * Gets the text area.
         *
         * @return the text area
         */
        private ProgressStateList getProgressStateList() {

                if (progressStateList == null) {
                        progressStateList = new ProgressStateList();
                }
                return progressStateList;
        }

        /**
         * Gets the progress bar.
         *
         * @return the progress bar
         */
        private JProgressBar getProgressBar() {

                if (progressBar == null) {
                        progressBar = new JProgressBar();
                }
                return progressBar;
        }

        /**
         * Only supported model is {@link ProgressState}.
         *
         * @param clazz class
         * @return true if clazz can be assigned to a ProgressState object
         */
        @Override
        public boolean supportsModelClass(Class<?> clazz) {

                return ProgressState.class.isAssignableFrom(clazz);
        }

        /**
         * Fills all components from a {@link ProgressState} object.
         *
         * @param model a {@link ProgressState} object
         */
        @Override
        public void fillDialogFromModel(Object model) {

                ProgressState state = (ProgressState) model;

                error = (state.getCurrentProgress() < 0);
                success = (state.getCurrentProgress() == state.getMaxProgress());

                getProgressStateList().add(state);
                getProgressBar().setValue(state.getCurrentProgress());
                getProgressBar().setMaximum(state.getMaxProgress());

                if (error || success) {
                        getOkButton().setEnabled(true);
                        getCancelButton().setEnabled(false);
                } else {
                        getOkButton().setEnabled(false);
                        getCancelButton().setEnabled(true);
                }
        }

        /**
         * Dialog is read only.
         *
         * @param model not used
         */
        @Override
        public void fillModelFromDialog(Object model) {
        }

        /**
         * Only supported property is progressState.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if (PROGRESS_STATE.equals(evt.getPropertyName())) {
                        fillDialogFromModel(evt.getNewValue());
                }
        }

        /**
         * No cancel on escape.
         *
         * @return false
         */
        @Override
        public boolean isCancelOnEscape() {

                return false;
        }

        /**
         * Shows an empty dialog.
         */
        public boolean showDialog() {

                return showDialog(new ProgressState());
        }

        /**
         * Resets dialog.
         */
        @Override
        protected void resetDialog() {

                super.resetDialog();
                getProgressStateList().removeAll();
        }

        /**
         * Returns true if the window was cancelled.
         *
         * @return true if the window was cancelled
         */
        public boolean wasCancelled() {

                return (!(error || success));
        }

        /**
         * Sets the location to center of the parent window.
         */
        private void setLocation() {

                Point parentLocation = getParent().getLocation();
                Dimension parentSize = getParent().getSize();

                Point myLocation = new Point(parentLocation);
                Dimension mySize = getSize();

                myLocation.move((parentSize.width - mySize.width) / 2,
                                (parentSize.height - mySize.height) / 2);

                setLocation(myLocation);
        }
}

/**
 * Class responsible for showing the progress.
 *
 * @author Tomasz Sawicki
 */
class ProgressStateList extends JList {

        /**
         * Default constructor
         */
        public ProgressStateList() {

                setCellRenderer(new ProgressStateListCellRenderer());
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        /**
         * Adds an element to the list.
         *
         * @param state progress state
         */
        public void add(ProgressState state) {

                if (state.getProgressMsg().equals("")) {
                        return;
                }
                Dimension size = getSize();
                ArrayList<ProgressState> list = new ArrayList<ProgressState>();
                for (int i = 0; i < getModel().getSize(); i++) {
                        list.add((ProgressState) getModel().getElementAt(i));
                }
                list.add(state);
                setListData(list.toArray());
                setSize(size);
        }
}

/**
 * Renderer for {@link ProgressStateList}
 *
 * @author Tomasz Sawicki
 */
class ProgressStateListCellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                ProgressState state = (ProgressState) value;
                JTextArea textArea = new JTextArea(state.getProgressMsg());
                textArea.setBackground(Color.WHITE);

                if (state.getCurrentProgress() < 0) {
                        textArea.setForeground(Color.RED);
                } else if (state.getCurrentProgress() == state.getMaxProgress()) {
                        textArea.setForeground(Color.GREEN);
                } else {
                        textArea.setForeground(Color.BLACK);
                }

                return textArea;
        }
}
