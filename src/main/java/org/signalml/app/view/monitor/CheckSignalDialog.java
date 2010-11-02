/* CheckSignalDialog.java created 2010-10-24
 *
 */

package org.signalml.app.view.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.app.model.MontageDescriptor;
import org.signalml.app.view.montage.VisualReferenceModel;
import org.signalml.util.SvarogConstants;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * Dialog which checks and represents the signal state
 *
 * @author Tomasz Sawicki
 */

public class CheckSignalDialog extends AbstractDialog {

        /**
         * The delay between each check in miliseconds
         */
        private static final int DELAY = 1000;
        private static final String AMP_TYPE = "TMSI-porti7";        

        private Montage currentMontage;
        private MonitorSignalDocument monitorSignalDocument;

        private CheckSignalDisplay checkSignalDisplay;	
        private VisualReferenceModel visualReferenceModel;
        
        private Timer timer;
        private TimerClass timerClass;


        public CheckSignalDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
                                
                super(messageSource, w, isModal);              
        }

	@Override
	protected void initialize() {

		setTitle(messageSource.getMessage("checkSignal.title"));
		setPreferredSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		super.initialize();
		setMinimumSize(new Dimension(650, 650));
	}

        @Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
                JPanel editorPanel = new JPanel(new BorderLayout());

                visualReferenceModel = new VisualReferenceModel(messageSource);
		checkSignalDisplay = new CheckSignalDisplay(visualReferenceModel);
		checkSignalDisplay.setBackground(Color.WHITE);

                JScrollPane editorScrollPane = new JScrollPane(checkSignalDisplay, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		checkSignalDisplay.setViewport(editorScrollPane.getViewport());

		editorPanel.setBorder(new CompoundBorder(new TitledBorder(messageSource.getMessage("checkSignal.label")), new EmptyBorder(3,3,3,3)));
                editorPanel.add(editorScrollPane, BorderLayout.CENTER);

		interfacePanel.add(editorPanel, BorderLayout.CENTER);
		return interfacePanel;
                
	}

        @Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		MontageDescriptor descriptor = (MontageDescriptor) model;
		Montage montage = descriptor.getMontage();
		SignalDocument signalDocument = descriptor.getSignalDocument();
		
		if (montage == null) {

                        currentMontage = new Montage(new SourceMontage(signalDocument));
                } else {

                        currentMontage = new Montage(montage);
                }
			
		getOkButton().setVisible(true);
		getRootPane().setDefaultButton(getOkButton());

		setMontage(currentMontage);

                monitorSignalDocument = (MonitorSignalDocument) signalDocument;

                timerClass = new TimerClass(checkSignalDisplay, monitorSignalDocument, AMP_TYPE);
                timerClass.actionPerformed(null);

                timer = new Timer(DELAY, timerClass);
                timer.start();
	}

	private void setMontage(Montage montage) {

		visualReferenceModel.setMontage(montage);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MontageDescriptor descriptor = (MontageDescriptor) model;
		descriptor.setMontage(currentMontage);

	}

        @Override
        public boolean isCancellable() {

		return false;
	}
        
        @Override
	protected void onDialogClose() {

		super.onDialogClose();
                timer.stop();
		setMontage(null);
	}

        @Override
	public boolean supportsModelClass(Class<?> clazz) {
                
		return MontageDescriptor.class.isAssignableFrom(clazz);
	}

        /**
         * This class' instance is passed to the {@link Timer} object of a {@link CheckSignalDialog}.
         * It gets a {@link GenericAmplifierDiagnosis} object and calls it's {@link GenericAmplifierDiagnosis#signalState()} method
         * constantly in a seperate thread with a given delay
         */
        private class TimerClass implements ActionListener, Runnable {

                private GenericAmplifierDiagnosis amplifierDiagnosis;
                private CheckSignalDisplay checkSignalDisplay;

                /**
                 * Constructor which gets a {@link GenericAmplifierDiagnosis} based on a {@link MonitorSignalDocument} object
                 * for a given amplifier model.
                 *
                 * @param checkSignalDisplay A {@link CheckSignalDisplay} object to which the information from a amplifier diagnosis will be passed
                 * @param monitorSignalDocument A document representing the signal from an amplifier
                 * @param ampType Name of an amplifier model
                 */
                public TimerClass(CheckSignalDisplay checkSignalDisplay, MonitorSignalDocument monitorSignalDocument, String ampType) {

                        this.checkSignalDisplay = checkSignalDisplay;                       
                        amplifierDiagnosis = AmplifierDignosisManufacture.getAmplifierDiagnosis(ampType, monitorSignalDocument);
                }

                @Override
                public void actionPerformed(ActionEvent e) {

                        (new Thread(this)).start();
                }

                @Override
                public void run() {

                        checkSignalDisplay.setChannelsState(amplifierDiagnosis.signalState());
                }
        }
}