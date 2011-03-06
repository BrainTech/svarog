package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.plugin.export.SignalMLException;
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

        private final int TEXT_AREA_ROWS = 10;
        private final int TEXT_AREA_COLS = 30;

        /**
         * The text area.
         */
        private JTextArea textArea = null;

        /**
         * The progress bar.
         */
        private JProgressBar progressBar = null;               

        /**
         * Default constructor.
         *
         * @param messageSource the message source
         * @param w parent window
         * @param isModal if this window is modal
         * @param caption window's caption
         */
        public ProgressDialog(MessageSourceAccessor messageSource, Window w, boolean isModal, String caption) {

                super(messageSource, w, isModal);
                setTitle(caption);                
        }

        /**
         * Creates the interface.
         *
         * @return the interface
         */
        @Override
        protected JComponent createInterface() {

                JPanel interfacePanel = new JPanel(new BorderLayout(10, 10));
                CompoundBorder panelBorder = new CompoundBorder(new TitledBorder(""), new EmptyBorder(3, 3, 3, 3));
                interfacePanel.setBorder(panelBorder);

                interfacePanel.add(new JScrollPane(getTextArea()), BorderLayout.CENTER);
                interfacePanel.add(getProgressBar(), BorderLayout.PAGE_END);

                return interfacePanel;
        }

        /**
         * Gets the text area.
         *
         * @return the text area
         */
        private JTextArea getTextArea() {
                
                if (textArea == null) {
                        textArea = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLS);                        
                        textArea.setEditable(false);
                }
                return textArea;
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
         * @throws SignalMLException never thrown
         */
        @Override
        public void fillDialogFromModel(Object model) throws SignalMLException {

                ProgressState state = (ProgressState) model;

                boolean error = (state.getCurrentProgress() < 0);
                boolean end = (state.getCurrentProgress() == state.getMaxProgress());

                getTextArea().setText(state.getProgressMsg());
                getProgressBar().setValue(state.getCurrentProgress());
                getProgressBar().setMaximum(state.getMaxProgress());

                if (error || end) {
                        getOkButton().setEnabled(true);
                        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                } else {
                        getOkButton().setEnabled(false);
                        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                }

                if (error) {
                        getTextArea().setForeground(Color.RED);
                } else if (end) {
                        getTextArea().setForeground(Color.GREEN);
                } else {
                        getTextArea().setForeground(Color.BLACK);
                }
        }

        /**
         * Dialog is read only.
         *
         * @param model not used
         * @throws SignalMLException never thrown
         */
        @Override
        public void fillModelFromDialog(Object model) throws SignalMLException {                
        }

        /**
         * Only supported property is progressState.
         *
         * @param evt property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {

                if ("progressState".equals(evt.getPropertyName())) {
                        try {
                                fillDialogFromModel(evt.getNewValue());
                        } catch (SignalMLException ex) {
                                Logger.getLogger(ProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }

        /**
         * Dialog can't be canceled.
         * 
         * @return false
         */
        @Override
        protected boolean onCancel() {
                return false;
        }

        /**
         * No cancel button.
         *
         * @return false
         */
        @Override
        public boolean isCancellable() {
                return false;
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
        public void showDialog() {
                showDialog(new ProgressState());
        }
}