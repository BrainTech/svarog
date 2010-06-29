/* FFTWindowTypePanel.java created 2008-02-04
 *
 */
package org.signalml.app.view.element;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.FFTWindowTypeSettings;
import org.signalml.fft.WindowType;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** FFTWindowTypePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FFTWindowTypePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private WindowType[] windowTypes = WindowType.values();
	private JRadioButton[] windowTypeRadioButtons;
	private JTextField windowParameterTextField;
	private ButtonGroup windowTypeButtonGroup;

	private MessageSourceAccessor messageSource;

	public FFTWindowTypePanel(MessageSourceAccessor messageSource, boolean wide) {
		super();
		this.messageSource = messageSource;
		initialize(wide);
	}

	private void initialize(boolean wide) {

		windowTypeButtonGroup = new ButtonGroup();

		if (wide) {
			setLayout(new GridLayout(3,3,3,3));
		} else {
			setLayout(new GridLayout(5,2,3,3));
		}

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("fftWindowTypeSettings.windowTypeTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		windowParameterTextField = new JTextField("");
		windowParameterTextField.setPreferredSize(new Dimension(150,25));

		ItemListener windowTypeListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (selected) {
					WindowType type = getWindowTypeForRadio((JRadioButton) e.getSource());
					if (type == null) {
						throw new NullPointerException("No type for radio");
					}
					boolean parametrized = type.isParametrized();
					if (parametrized) {
						if (windowParameterTextField.getText().isEmpty()) {
							windowParameterTextField.setText(Double.toString(type.getParameterDefault()));
						} else {
							try {
								double value = Double.parseDouble(windowParameterTextField.getText());
								if (value < type.getParameterMin() || value > type.getParameterMax()) {
									windowParameterTextField.setText(Double.toString(type.getParameterDefault()));
								}
							} catch (NumberFormatException ex) {
								windowParameterTextField.setText(Double.toString(type.getParameterDefault()));
							}
						}
					}
					windowParameterTextField.setEnabled(parametrized);
				}

			}

		};

		windowTypeRadioButtons = new JRadioButton[windowTypes.length];
		for (int i=0; i<windowTypes.length; i++) {
			windowTypeRadioButtons[i] = new JRadioButton(messageSource.getMessage(windowTypes[i]));
			windowTypeButtonGroup.add(windowTypeRadioButtons[i]);
			add(windowTypeRadioButtons[i]);
			windowTypeRadioButtons[i].addItemListener(windowTypeListener);
		}

		windowTypeRadioButtons[0].setSelected(true);

		if (!wide) {
			add(new JLabel(""));
		}
		add(new JLabel(messageSource.getMessage("fftWindowTypeSettings.windowParameter")));
		add(windowParameterTextField);

	}

	private WindowType getWindowTypeForRadio(JRadioButton source) {
		for (int i=0; i<windowTypes.length; i++) {
			if (windowTypeRadioButtons[i] == source) {
				return windowTypes[i];
			}
		}
		return null;
	}

	public void fillPanelFromModel(FFTWindowTypeSettings settings) {

		WindowType windowType = settings.getWindowType();
		for (int i=0; i<windowTypes.length; i++) {
			if (windowType == windowTypes[i]) {
				windowTypeRadioButtons[i].setSelected(true);
				break;
			}
		}
		if (windowType.isParametrized()) {
			windowParameterTextField.setText(Double.toString(settings.getWindowParameter()));
		} else {
			windowParameterTextField.setText("");
		}

	}

	public void fillModelFromPanel(FFTWindowTypeSettings settings) {

		for (int i=0; i<windowTypes.length; i++) {
			if (windowTypeRadioButtons[i].isSelected()) {
				if (windowTypes[i].isParametrized()) {
					settings.setWindowParameter(Double.parseDouble(windowParameterTextField.getText()));
				}
				settings.setWindowType(windowTypes[i]);
			}
		}

	}

	public void validatePanel(Errors errors) {

		for (int i=0; i<windowTypes.length; i++) {
			if (windowTypeRadioButtons[i].isSelected()) {
				if (windowTypes[i].isParametrized()) {
					try {
						double parameter = Double.parseDouble(windowParameterTextField.getText());
						if (parameter < windowTypes[i].getParameterMin() || parameter > windowTypes[i].getParameterMax()) {
							double parameterMin = windowTypes[i].getParameterMin();
							double parameterMax = windowTypes[i].getParameterMax();
							String parameterMinString;
							String parameterMaxString;
							if (parameterMin > Double.MIN_VALUE) {
								parameterMinString = Double.toString(parameterMin);
							} else {
								parameterMinString = "";
							}
							if (parameterMax < Double.MAX_VALUE) {
								parameterMaxString = Double.toString(parameterMax);
							} else {
								parameterMaxString = "";
							}

							errors.rejectValue("windowParameter", "fftWindowTypeSettings.error.windowParameterOutOfRange", new Object[] { parameterMinString, parameterMaxString }, "signalFFTSettings.error.windowParameterOutOfRange");
						}
					} catch (NumberFormatException ex) {
						errors.rejectValue("windowParameter", "fftWindowTypeSettings.error.badWindowParameter");
					}

				}
			}
		}

	}

}
