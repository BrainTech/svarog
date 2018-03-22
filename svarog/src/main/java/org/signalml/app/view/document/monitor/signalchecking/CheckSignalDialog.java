/* CheckSignalDialog.java created 2010-10-24
 *
 */

package org.signalml.app.view.document.monitor.signalchecking;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.montage.ElectrodeType;
import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.montage.visualreference.VisualReferenceModel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.plugin.export.SignalMLException;

/**
 * Checks and represents the signal state.
 *
 * @author Tomasz Sawicki
 */
public class CheckSignalDialog extends AbstractDialog  {

	/**
	 * Minimum windows size.
	 */
	private static final int WINDOW_HEIGHT = 750;
	private static final int WINDOW_WIDTH = 950;

	/**
	 * The current montage.
	 */
	private Montage currentMontage;

	/**
	 * The currently open document.
	 */
	private MonitorSignalDocument monitorSignalDocument;

	/**
	 * A component showing the signal status.
	 */
	private CheckSignalDisplay checkSignalDisplay;

	/**
	 * A current visual reference model.
	 */
	private VisualReferenceModel visualReferenceModel;

	private JComboBox electrodeTypeComboBox;

	/**
	 * Timer used to invoke {@link #timerClass} {@link TimerClass#run()}.
	 */
	private Timer timer;

	/**
	 * Object which periodically does all the checking.
	 */
	private TimerClass timerClass;

	AmplifierValidationRules validationRules;


	public CheckSignalDialog(Window w, boolean isModal) {

		super(w, isModal);
	}

	/**
	 * Sets window's title and size, then calls {@link AbstractDialog#initialize()}.
	 */
	@Override
	protected void initialize() {

		setTitle(_("Check Signal"));
		super.initialize();
		setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
	}

	/**
	 * Creates the interface - a panel with {@link #checkSignalDisplay}.
	 *
	 * @return the interface
	 */
	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
		JPanel editorPanel = new JPanel(new BorderLayout());

		visualReferenceModel = new VisualReferenceModel(true);
		checkSignalDisplay = new CheckSignalDisplay(visualReferenceModel);
		checkSignalDisplay.setBackground(Color.WHITE);

		JScrollPane editorScrollPane = new JScrollPane(checkSignalDisplay, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		checkSignalDisplay.setViewport(editorScrollPane.getViewport());

		editorPanel.setBorder(new CompoundBorder(new TitledBorder(_("Channels")), new EmptyBorder(3, 3, 3, 3)));
		editorPanel.add(editorScrollPane, BorderLayout.CENTER);

		JPanel parametersPanel = new AbstractPanel(_("Electrodes type"));
		/* This switch is specific to DCDiagnosis which is currently not used
		electrodeTypeComboBox = new JComboBox(ElectrodeType.values());
		electrodeTypeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				HashMap<String,Object> parameters = validationRules.getMethods().get(SignalCheckingMethod.DC);
				ElectrodeType electrodeType = (ElectrodeType) electrodeTypeComboBox.getSelectedItem();
				parameters.put(DCDiagnosis.ELECTRODE_TYPE, electrodeType);
				timerClass.actionPerformed(null);
			}
		});
		parametersPanel.add(electrodeTypeComboBox);
		*/

		interfacePanel.add(editorPanel, BorderLayout.CENTER);
		interfacePanel.add(parametersPanel, BorderLayout.EAST);
		return interfacePanel;

	}

	/**
	 * Sets the montage and starts the {@link #timer}.
	 *
	 * @param model  model the model from which this dialog will be filled
	 * @throws SignalMLException TODO when it is thrown
	 */
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

		validationRules = getAmplifierValidationRules();

		timerClass = new TimerClass(checkSignalDisplay, monitorSignalDocument, validationRules);
		timerClass.actionPerformed(null);

		timer = new Timer(validationRules.getDelay(), timerClass);
		timer.start();
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MontageDescriptor descriptor = (MontageDescriptor) model;
		descriptor.setMontage(currentMontage);

	}

	/**
	 * Sets the montage for the component.
	 *
	 * @param montage {@link Montage} object
	 */
	private void setMontage(Montage montage) {

		visualReferenceModel.setMontage(montage);

	}

	/**
	 * Returns validation rules of current signal.
	 *
	 * @return validation rules of current signal.
	 */
	private AmplifierValidationRules getAmplifierValidationRules() {

		// TODO currently returning a constant object, should obtain
		// the rules from signal or something

		EnumMap<SignalCheckingMethod, HashMap<String, Object>> methodList =
			new EnumMap<SignalCheckingMethod, HashMap<String, Object>>(SignalCheckingMethod.class);

		// "amp null" diagnosis is commented out since it uses "idle" parameter
		// which is currently not being correctly sent by any amplifier
		/*
		HashMap<String, Object> ampNullParameters = new HashMap<String, Object>();
		ampNullParameters.put(GenericAmplifierDiagnosis.SAMPLES_TESTED_FACTOR, 1.0);
		ampNullParameters.put(AmplifierNullDiagnosis.TEST_TOLERANCE, 0.99);
		methodList.put(SignalCheckingMethod.AMPNULL, ampNullParameters);
		*/

		// DC diagnosis is commented out as it is questionable
		/*
		HashMap<String, Object> dcNullParameters = new HashMap<String, Object>();
		dcNullParameters.put(GenericAmplifierDiagnosis.SAMPLES_TESTED_FACTOR, 1.0);
		dcNullParameters.put(DCDiagnosis.ELECTRODE_TYPE, electrodeTypeComboBox.getSelectedItem());
		methodList.put(SignalCheckingMethod.DC, dcNullParameters);
		*/

		HashMap<String, Object> impedanceParameters = new HashMap<String, Object>();
		impedanceParameters.put(GenericAmplifierDiagnosis.SAMPLES_TESTED_FACTOR, 1.0);
		methodList.put(SignalCheckingMethod.IMPEDANCE, impedanceParameters);

		// FFT diagnosis has been commented out since 2012
		/*HashMap<String, Object> fftNullParameters = new HashMap<String, Object>();
		fftNullParameters.put(FFTDiagnosis.ELECTRODE_TYPE, electrodeTypeComboBox.getSelectedItem());
		fftNullParameters.put(GenericAmplifierDiagnosis.SAMPLES_TESTED_FACTOR, 0.5);
		methodList.put(SignalCheckingMethod.FFT, fftNullParameters);*/

		return new AmplifierValidationRules("TMSI-porti7", methodList, 3000);
	}

	/**
	 * Stops the timer on dialog close. Then calls {@link AbstractDialog#onDialogClose()}.
	 */
	@Override
	protected void onDialogClose() {

		super.onDialogClose();
		timer.stop();
		setMontage(null);
	}

	/**
	 * Is not cancellable.
	 * @return false
	 */
	@Override
	public boolean isCancellable() {

		return false;
	}

	/**
	 * Whether the model is a montage object.
	 * @param clazz model's class
	 * @return true if clazz is montage
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {

		return MontageDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * This class' instance is passed to the {@link Timer} object of a {@link CheckSignalDialog}.
	 * It gets a {@link GenericAmplifierDiagnosis} object and calls it's {@link GenericAmplifierDiagnosis#signalState()} method
	 * constantly in a seperate thread with a given delay.
	 */
	private class TimerClass implements ActionListener {

		/**
		 * Amplifier diagnosis' list.
		 */
		private List<GenericAmplifierDiagnosis> amplifierDiagnosis;
		/**
		 * The display to update.
		 */
		private CheckSignalDisplay checkSignalDisplay;

		/**
		 * Constructor which gets a {@link GenericAmplifierDiagnosis} based on a {@link MonitorSignalDocument} object
		 * for a given amplifier model.
		 *
		 * @param checkSignalDisplay A {@link CheckSignalDisplay} object to which the information from a amplifier diagnosis will be passed
		 * @param monitorSignalDocument A document representing the signal from an amplifier
		 * @param validationRules amplifier validation rules
		 */
		public TimerClass(CheckSignalDisplay checkSignalDisplay, MonitorSignalDocument monitorSignalDocument, AmplifierValidationRules validationRules) {

			this.checkSignalDisplay = checkSignalDisplay;
			amplifierDiagnosis = new ArrayList<GenericAmplifierDiagnosis>();

			for (SignalCheckingMethod method : validationRules.getMethods().keySet())
				amplifierDiagnosis.add(AmplifierDignosisManufacture.getAmplifierDiagnosis(method, monitorSignalDocument, validationRules.getMethods().get(method)));
		}

		/**
		 * Calls the {@link #run()} method in a seperate thread.
		 *
		 * @param e {@link ActionEvent} object
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			List<HashMap<String, ChannelState>> channels = new ArrayList<HashMap<String, ChannelState>>();
			for (GenericAmplifierDiagnosis diagnosis : amplifierDiagnosis)
				channels.add(diagnosis.signalState());

			checkSignalDisplay.setChannelsState(channels);
		}
	}
}