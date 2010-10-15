/* IterationSetupDialog.java created 2007-12-05
 *
 */

package org.signalml.app.method.iterate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.IterableNumericParameter;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.ParameterIterationSettings;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** IterationSetupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class IterationSetupDialog extends AbstractDialog {

	private static final Dimension SPINNER_DIMENSION = new Dimension(250,25);

	private static final long serialVersionUID = 1L;

	private ApplicationMethodManager methodManager;

	private Action editBaseConfigurationAction;
	private JButton editBaseConfigurationButton;

	private JSpinner iterationCountSpinner;

	private ResolvableComboBox parameterComboBox;

	private JCheckBox iterateCheckBox;

	private SpinnerNumberModel startSpinnerNumberModel;
	private SpinnerNumberModel endSpinnerNumberModel;

	private JSpinner startSpinner;
	private JSpinner endSpinner;

	private MethodIteratorData currentData;
	private IterableMethod currentMethod;
	private ParameterIterationSettings[] currentParameters;

	public IterationSetupDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public IterationSetupDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("iterationSetup.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/iteratemethod.png"));
		setResizable(false);

		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel baseConfigurationPanel = new JPanel();
		baseConfigurationPanel.setLayout(new BoxLayout(baseConfigurationPanel, BoxLayout.X_AXIS));
		baseConfigurationPanel.setBorder(new CompoundBorder(
		                new TitledBorder(messageSource.getMessage("iterationSetup.baseConfigurationTitle")),
		                new EmptyBorder(3,3,3,3)
		                                 ));

		baseConfigurationPanel.add(new JLabel(messageSource.getMessage("iterationSetup.baseConfiguration")));
		baseConfigurationPanel.add(Box.createHorizontalStrut(5));
		baseConfigurationPanel.add(Box.createHorizontalGlue());
		baseConfigurationPanel.add(getEditBaseConfigurationButton());

		JPanel iterationPanel = new JPanel();
		iterationPanel.setBorder(new CompoundBorder(
		                                 new TitledBorder(messageSource.getMessage("iterationSetup.iterationConfigurationTitle")),
		                                 new EmptyBorder(3,3,3,3)
		                         ));

		GroupLayout layout = new GroupLayout(iterationPanel);
		iterationPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel iterationCountLabel = new JLabel(messageSource.getMessage("iterationSetup.iterationCount"));
		JLabel parameterLabel = new JLabel(messageSource.getMessage("iterationSetup.parameter"));
		JLabel iterateLabel = new JLabel(messageSource.getMessage("iterationSetup.iterate"));
		JLabel iterationStartLabel = new JLabel(messageSource.getMessage("iterationSetup.iterationStart"));
		JLabel iterationEndLabel = new JLabel(messageSource.getMessage("iterationSetup.iterationEnd"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(iterationCountLabel)
		        .addComponent(parameterLabel)
		        .addComponent(iterateLabel)
		        .addComponent(iterationStartLabel)
		        .addComponent(iterationEndLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.TRAILING)
		        .addComponent(getIterationCountSpinner())
		        .addComponent(getParameterComboBox())
		        .addComponent(getIterateCheckBox())
		        .addComponent(getStartSpinner())
		        .addComponent(getEndSpinner())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(iterationCountLabel)
				.addComponent(getIterationCountSpinner())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(parameterLabel)
				.addComponent(getParameterComboBox())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(iterateLabel)
				.addComponent(getIterateCheckBox())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(iterationStartLabel)
				.addComponent(getStartSpinner())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(iterationEndLabel)
				.addComponent(getEndSpinner())
			);
		
		layout.setVerticalGroup(vGroup);		
		
		interfacePanel.add( baseConfigurationPanel, BorderLayout.NORTH );
		interfacePanel.add( iterationPanel, BorderLayout.CENTER );
		
		return interfacePanel;

	}

	private ParameterIterationSettings[] getNewParameters(IterableParameter[] iterableParameters, ParameterIterationSettings[] oldParameters) {

		ParameterIterationSettings[] parameters = new ParameterIterationSettings[iterableParameters.length];
		int i, e;
		boolean copied;

		for (i=0; i<parameters.length; i++) {

			parameters[i] = new ParameterIterationSettings(iterableParameters[i]);
			copied = false;

			if (oldParameters != null) {

				for (e=0; e<oldParameters.length; e++) {
					if (iterableParameters[i].equals(oldParameters[e].getParameter())) {

						parameters[i].setIterated(oldParameters[e].isIterated());
						parameters[i].setStartValue(oldParameters[e].getStartValue());
						parameters[i].setEndValue(oldParameters[e].getEndValue());
						copied = true;
						break;
					}
				}

			}

			if (!copied) {

				parameters[i].setIterated(false);
				parameters[i].setStartValue(iterableParameters[i].getDefaultStartValue());
				parameters[i].setEndValue(iterableParameters[i].getDefaultEndValue());

			}

		}

		return parameters;

	}

	private void fillDialogFromCurrentParameters() {

		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(currentParameters);
		getParameterComboBox().setModel(comboBoxModel);
		if (currentParameters.length > 0) {
			getParameterComboBox().setSelectedIndex(0);
		}

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		IterationSetupDescriptor descriptor = (IterationSetupDescriptor) model;

		currentMethod = descriptor.getMethod();
		currentData = descriptor.getData();

		Object subjectData = currentData.getSubjectMethodData();

		getIterationCountSpinner().setValue(new Integer(currentData.getTotalIterations()));

		IterableParameter[] iterableParameters = currentMethod.getIterableParameters(subjectData);
		ParameterIterationSettings[] parameters = currentData.getParameters();

		currentParameters = getNewParameters(iterableParameters, parameters);

		fillDialogFromCurrentParameters();

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		IterationSetupDescriptor descriptor = (IterationSetupDescriptor) model;

		MethodIteratorData data = descriptor.getData();

		data.setTotalIterations((Integer) getIterationCountSpinner().getValue());

		data.setParameters(currentParameters);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		boolean anyIterated = false;
		for (int i=0; i<currentParameters.length; i++) {
			if (currentParameters[i].isIterated()) {
				anyIterated = true;
				break;
			}
		}

		if (!anyIterated) {
			errors.reject("error.nothingIterated");
		}

	}

	public Action getEditBaseConfigurationAction() {
		if (editBaseConfigurationAction == null) {
			editBaseConfigurationAction = new EditBaseConfiguration();
		}
		return editBaseConfigurationAction;
	}

	public JButton getEditBaseConfigurationButton() {
		if (editBaseConfigurationButton == null) {
			editBaseConfigurationButton = new JButton(getEditBaseConfigurationAction());
		}
		return editBaseConfigurationButton;
	}

	public JSpinner getIterationCountSpinner() {
		if (iterationCountSpinner == null) {
			iterationCountSpinner = new JSpinner(new SpinnerNumberModel(new Integer(2), new Integer(2), null, new Integer(1)));
			iterationCountSpinner.setPreferredSize(SPINNER_DIMENSION);
			iterationCountSpinner.setMinimumSize(SPINNER_DIMENSION);
			iterationCountSpinner.setMaximumSize(SPINNER_DIMENSION);
			iterationCountSpinner.setFont(iterationCountSpinner.getFont().deriveFont(Font.PLAIN));
		}
		return iterationCountSpinner;
	}

	public ResolvableComboBox getParameterComboBox() {
		if (parameterComboBox == null) {
			parameterComboBox = new ResolvableComboBox(messageSource);
			parameterComboBox.setModel(new DefaultComboBoxModel());
			parameterComboBox.setPreferredSize(SPINNER_DIMENSION);
			parameterComboBox.setMinimumSize(SPINNER_DIMENSION);
			parameterComboBox.setMaximumSize(SPINNER_DIMENSION);

			parameterComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					ParameterIterationSettings selected = (ParameterIterationSettings) parameterComboBox.getSelectedItem();
					if (selected == null) {
						return;
					}

					IterableParameter parameter = selected.getParameter();
					if (parameter instanceof IterableNumericParameter) {

						IterableNumericParameter numeric = (IterableNumericParameter) parameter;

						SpinnerNumberModel spinnerModel;

						spinnerModel = new SpinnerNumberModel((Number) selected.getStartValue(), numeric.getMinimum(), numeric.getMaximum(), numeric.getStepSize());
						SwingUtils.replaceSpinnerModel(getStartSpinner(), spinnerModel);

						spinnerModel = new SpinnerNumberModel((Number) selected.getEndValue(), numeric.getMinimum(), numeric.getMaximum(), numeric.getStepSize());
						SwingUtils.replaceSpinnerModel(getEndSpinner(), spinnerModel);

					} else {
						throw new SanityCheckException("Unuspported parameter type [" + parameter.getClass() + "]");
					}

					getIterateCheckBox().setSelected(selected.isIterated());


				}

			});
		}
		return parameterComboBox;
	}

	public JCheckBox getIterateCheckBox() {
		if (iterateCheckBox == null) {
			iterateCheckBox = new JCheckBox();
			iterateCheckBox.setPreferredSize(SPINNER_DIMENSION);

			iterateCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					boolean iterated = iterateCheckBox.isSelected();
					getStartSpinner().setEnabled(iterated);
					getEndSpinner().setEnabled(iterated);

					ParameterIterationSettings selected = (ParameterIterationSettings) getParameterComboBox().getSelectedItem();
					if (selected == null) {
						return;
					}

					selected.setIterated(iterated);

				}

			});

		}
		return iterateCheckBox;
	}

	public SpinnerNumberModel getStartSpinnerNumberModel() {
		if (startSpinnerNumberModel == null) {
			startSpinnerNumberModel = new SpinnerNumberModel();
		}
		return startSpinnerNumberModel;
	}

	public SpinnerNumberModel getEndSpinnerNumberModel() {
		if (endSpinnerNumberModel == null) {
			endSpinnerNumberModel = new SpinnerNumberModel();
		}
		return endSpinnerNumberModel;
	}

	public JSpinner getStartSpinner() {
		if (startSpinner == null) {
			startSpinner = new JSpinner(new SpinnerNumberModel(50,1,100,1));
			startSpinner.setPreferredSize(SPINNER_DIMENSION);
			startSpinner.setMinimumSize(SPINNER_DIMENSION);
			startSpinner.setMaximumSize(SPINNER_DIMENSION);

			startSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					ParameterIterationSettings selected = (ParameterIterationSettings) getParameterComboBox().getSelectedItem();
					if (selected == null) {
						return;
					}

					selected.setStartValue(startSpinner.getValue());

				}

			});

			startSpinner.setEnabled(false);
			startSpinner.setFont(startSpinner.getFont().deriveFont(Font.PLAIN));

		}
		return startSpinner;
	}

	public JSpinner getEndSpinner() {
		if (endSpinner == null) {
			endSpinner = new JSpinner(new SpinnerNumberModel(50,1,100,1));
			endSpinner.setPreferredSize(SPINNER_DIMENSION);
			endSpinner.setMinimumSize(SPINNER_DIMENSION);
			endSpinner.setMaximumSize(SPINNER_DIMENSION);

			endSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					ParameterIterationSettings selected = (ParameterIterationSettings) getParameterComboBox().getSelectedItem();
					if (selected == null) {
						return;
					}

					selected.setEndValue(endSpinner.getValue());

				}

			});

			endSpinner.setEnabled(false);
			endSpinner.setFont(endSpinner.getFont().deriveFont(Font.PLAIN));

		}
		return endSpinner;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return IterationSetupDescriptor.class.isAssignableFrom(clazz);
	}

	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}

	public void setMethodManager(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
	}

	protected class EditBaseConfiguration extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditBaseConfiguration() {
			super(messageSource.getMessage("iterationSetup.configure"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/baseconfiguration.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("iterationSetup.configureToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			ApplicationMethodDescriptor descriptor = methodManager.getMethodData(currentMethod);
			MethodConfigurer configurer = descriptor.getConfigurer(methodManager);

			Object subjectData = currentData.getSubjectMethodData();

			try {
				boolean ok = configurer.configure(currentMethod, subjectData);
				if (!ok) {
					return;
				}
			} catch (SignalMLException ex) {
				logger.error("Failed to configure base data", ex);
				getErrorsDialog().showException(ex);
				return;
			}

			IterableParameter[] iterableParameters = currentMethod.getIterableParameters(subjectData);

			currentParameters = getNewParameters(iterableParameters, currentParameters);

			fillDialogFromCurrentParameters();

		}

	}

}
