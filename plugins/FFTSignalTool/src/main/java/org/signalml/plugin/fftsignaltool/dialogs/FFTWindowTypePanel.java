/* FFTWindowTypePanel.java created 2008-02-04
 *
 */
package org.signalml.plugin.fftsignaltool.dialogs;

import static org.signalml.plugin.fftsignaltool.FFTSignalTool._;
import static org.signalml.plugin.fftsignaltool.FFTSignalTool.i18n;

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

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.math.fft.WindowType;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.signalml.plugin.fftsignaltool.FFTWindowTypeSettings;

/**
 * Panel to select the {@link WindowType type} of the FFT Window and the
 * parameter for it. Contains two types of elements:
 * <ul>
 * <li>{@link #windowTypeRadioButtons radio buttons} for every
 * {@link #windowTypes possible type} of FFT window,</li>
 * <li>the text field in which the parameters for the selected type of the
 * window can be entered.</li>
 * </ul>
 * This panel can be either wide (3 columns, 3 rows) or high (2 columns, 5
 * rows).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class FFTWindowTypePanel extends JPanel {

	/**
	 * the serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the array with possible {@link WindowType types} of FFT window
	 */
	private WindowType[] windowTypes = WindowType.values();
	/**
	 * the array with radio buttons for {@link #windowTypes possible types} of a
	 * FFT window
	 */
	private JRadioButton[] windowTypeRadioButtons;
	/**
	 * the text field in which the parameters for the selected type of the
	 * window can be entered
	 */
	private JTextField windowParameterTextField;

	/**
	 * the group containing
	 */
	private ButtonGroup windowTypeButtonGroup;

	/**
	 * Constructor. Sets the {@link SvarogAccessI18n message source} and
	 * initializes this panel.
	 *
	 * @param wide
	 *            {@code true} if this panel should be wide (have 3 columns) or
	 *            {@code false} if high (2 columns, 5 rows)
	 */
	public FFTWindowTypePanel(boolean wide) {
		super();
		initialize(wide);
	}

	/**
	 * Initializes this panel with GridLayout (with two or three columns
	 * depending on {@code wide}) and two types of elements:
	 * <ul>
	 * <li>{@link #windowTypeRadioButtons radio buttons} for every
	 * {@link #windowTypes possible type} of FFT window,</li>
	 * <li>the text field in which the parameters for the selected type of the
	 * window can be entered.</li>
	 * </ul>
	 * Adds listeners to the all buttons, which activate (or deactivate) the
	 * text field and set the default value for selected window type.
	 *
	 * @param wide
	 *            {@code true} if this panel should be wide (have 3 columns) or
	 *            {@code false} if high (2 columns, 5 rows)
	 */
	private void initialize(boolean wide) {

		windowTypeButtonGroup = new ButtonGroup();

		if (wide) {
			setLayout(new GridLayout(3, 3, 3, 3));
		} else {
			setLayout(new GridLayout(5, 2, 3, 3));
		}

		CompoundBorder border = new CompoundBorder(new TitledBorder(
					_("Window type")),
				new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		windowParameterTextField = new JTextField("");
		windowParameterTextField.setPreferredSize(new Dimension(150, 25));

		ItemListener windowTypeListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (selected) {
					WindowType type = getWindowTypeForRadio((JRadioButton) e
															.getSource());
					if (type == null) {
						throw new NullPointerException("No type for radio");
					}
					boolean parametrized = type.isParametrized();
					if (parametrized) {
						if (windowParameterTextField.getText().isEmpty()) {
							windowParameterTextField.setText(Double
															 .toString(type.getParameterDefault()));
						} else {
							try {
								double value = Double
											   .parseDouble(windowParameterTextField
															.getText());
								if (value < type.getParameterMin()
								|| value > type.getParameterMax()) {
									windowParameterTextField.setText(Double
																	 .toString(type
																			 .getParameterDefault()));
								}
							} catch (NumberFormatException ex) {
								windowParameterTextField.setText(Double
																 .toString(type.getParameterDefault()));
							}
						}
					}
					windowParameterTextField.setEnabled(parametrized);
				}

			}

		};

		windowTypeRadioButtons = new JRadioButton[windowTypes.length];
		for (int i = 0; i < windowTypes.length; i++) {
			windowTypeRadioButtons[i] = new JRadioButton(
				i18n().getMessage("fft.windowType."
								  + windowTypes[i].toString()));
			windowTypeButtonGroup.add(windowTypeRadioButtons[i]);
			add(windowTypeRadioButtons[i]);
			windowTypeRadioButtons[i].addItemListener(windowTypeListener);
		}

		windowTypeRadioButtons[0].setSelected(true);

		if (!wide) {
			add(new JLabel(""));
		}
		add(new JLabel(
				_("Window parameter")));
		add(windowParameterTextField);

	}

	/**
	 * Returns the {@link WindowType window type} for the given radio button.
	 *
	 * @param source
	 *            the button
	 * @return the type which is represented by this radio button
	 */
	private WindowType getWindowTypeForRadio(JRadioButton source) {
		for (int i = 0; i < windowTypes.length; i++) {
			if (windowTypeRadioButtons[i] == source) {
				return windowTypes[i];
			}
		}
		return null;
	}

	/**
	 * Sets as selected the radio button for the
	 * {@link FFTWindowTypeSettings#getWindowType() type} of the FFT window and
	 * if this {@link WindowType type} {@link WindowType#isParametrized() is
	 * parameterized} the {@link FFTWindowTypeSettings#getWindowParameter()
	 * parameter}.
	 *
	 * @param settings
	 *            the FFT window type {@link FFTWindowTypeSettings settings}
	 */
	public void fillPanelFromModel(FFTWindowTypeSettings settings) {

		WindowType windowType = settings.getWindowType();
		for (int i = 0; i < windowTypes.length; i++) {
			if (windowType == windowTypes[i]) {
				windowTypeRadioButtons[i].setSelected(true);
				break;
			}
		}
		if (windowType.isParametrized()) {
			windowParameterTextField.setText(Double.toString(settings
											 .getWindowParameter()));
		} else {
			windowParameterTextField.setText("");
		}

	}

	/**
	 * Stores in the setting the selected {@link WindowType type} of FFT window
	 * and if this type {@link WindowType#isParametrized() is parameterized} the
	 * {@link FFTWindowTypeSettings#setWindowParameter(double) parameter}.
	 *
	 * @param settings
	 *            the FFT window type {@link FFTWindowTypeSettings settings}
	 */
	public void fillModelFromPanel(FFTWindowTypeSettings settings) {

		for (int i = 0; i < windowTypes.length; i++) {
			if (windowTypeRadioButtons[i].isSelected()) {
				if (windowTypes[i].isParametrized()) {
					settings.setWindowParameter(Double
												.parseDouble(windowParameterTextField.getText()));
				}
				settings.setWindowType(windowTypes[i]);
			}
		}

	}

	/**
	 * Validates this panel. This panel is valid if the parameter for the
	 * selected {@link WindowType type} of the FFT window is a valid double and
	 * within the range
	 * {@code [WindowType#getParameterMin(), WindowType#getParameterMax()]}.
	 *
	 * @param errors
	 *            the object in which the errors are stored
	 */
	public void validatePanel(ValidationErrors errors) {

		for (int i = 0; i < windowTypes.length; i++) {
			if (windowTypeRadioButtons[i].isSelected()) {
				if (windowTypes[i].isParametrized()) {
					try {
						double parameter = Double
										   .parseDouble(windowParameterTextField.getText());
						if (parameter < windowTypes[i].getParameterMin()
								|| parameter > windowTypes[i].getParameterMax()) {
							double parameterMin = windowTypes[i]
												  .getParameterMin();
							double parameterMax = windowTypes[i]
												  .getParameterMax();
							String parameterMinString;
							String parameterMaxString;
							if (parameterMin > Double.MIN_VALUE) {
								parameterMinString = Double
													 .toString(parameterMin);
							} else {
								parameterMinString = "";
							}
							if (parameterMax < Double.MAX_VALUE) {
								parameterMaxString = Double
													 .toString(parameterMax);
							} else {
								parameterMaxString = "";
							}

							errors.addError(_("Bad window parameter. Allowed range: ") + parameterMinString + " - " + parameterMaxString);
						}
					} catch (NumberFormatException ex) {
						errors.addError(_("Bad window parameter. Must be a double precision value"));
					}

				}
			}
		}

	}

}
