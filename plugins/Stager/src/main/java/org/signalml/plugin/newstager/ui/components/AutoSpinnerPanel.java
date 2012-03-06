/* UnlimitedSpinnerPanel.java created 2008-02-14
 *
 */

package org.signalml.plugin.newstager.ui.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.signalml.util.MinMaxRange;
import org.signalml.util.MinMaxRangeFloat;
import org.signalml.util.MinMaxRangeInteger;

/**
 * Panel with two radio buttons and one spinner.
 * The buttons tell if the spinner should be enabled or the {@link #AUTO_VALUE
 * default value} should be used instead of it.
 * <p>
 * The value of this panel (either of the spinner or auto) can be stored
 * in the {@link MinMaxRange min-max range} as the maximum or minimum value.
 * Also these values can be set as the value of the spinner.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AutoSpinnerPanel extends JPanel {

	protected static final long serialVersionUID = 1L;

	/**
	 * the default value used if the spinner is disabled
	 */
	public static final int AUTO_VALUE = -1111;

	/**
	 * {@code true} if this panel should be thinner (60 pixel),
	 * {@code false} otherwise (150 pixel)
	 */
	protected boolean compact;

	/**
	 * the radio button which tells that the {@link #spinner} should be
	 * enable and used
	 */
	protected JRadioButton spinnerRadio;
	/**
	 * the radio button which tells that the {@link #spinner} should be
	 * disabled and that the default value should be used
	 */
	protected JRadioButton autoRadio;

	/**
	 * the group of radio buttons containing {@link #spinnerRadio} and
	 * {@link #autoRadio}
	 */
	protected ButtonGroup buttonGroup;

	/**
	 * the spinner
	 */
	protected JSpinner spinner;

	/**
	 * TODO  not used
	 */
	protected JPanel visualPanel;

	/**
	 * Constructor. Sets the source of messages and if this panel should be
	 * compact (thinner).
	 * @param compact @code true} if this panel should be thinner (60 pixel),
	 * {@code false} otherwise (150 pixel)
	 */
	protected  AutoSpinnerPanel(boolean compact) {
		super();
		this.compact = compact;
	}

	/**
	 * Constructor. Sets the source of messages, if this panel should be
	 * compact (thinner), creates the spinner and initializes this panel.
	 * @param value the value of the spinner
	 * @param min the minimum value that can be used in spinner
	 * @param max the maximum value that can be used in spinner
	 * @param step the step of the spinner (the difference between consecutive
	 * values of the spinner)
	 * @param compact @code true} if this panel should be thinner (60 pixel),
	 * {@code false} otherwise (150 pixel)
	 */
	public AutoSpinnerPanel(double value, double min, double max, double step, boolean compact) {
		this(compact);
		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

		commonInit(value, min, max, step);
	}

	/**
	 * Constructor. Sets the source of messages, if this panel should be
	 * compact (thinner), creates the spinner and initializes this panel.
	 * @param value the value of the spinner
	 * @param min the minimum value that can be used in spinner
	 * @param max the maximum value that can be used in spinner
	 * @param step the step of the spinner (the difference between consecutive
	 * values of the spinner)
	 * @param compact @code true} if this panel should be thinner (60 pixel),
	 * {@code false} otherwise (150 pixel)
	 */
	public AutoSpinnerPanel(float value, float min, float max, float step, boolean compact) {
		this(compact);
		spinner = new JSpinner(new SpinnerNumberModel((double) value, (double) min, (double) max, (double) step));

		commonInit(value, min, max, step);
	}

	/**
	 * Constructor. Sets the source of messages, if this panel should be
	 * compact (thinner), creates the spinner and initializes this panel.
	 * @param value the value of the spinner
	 * @param min the minimum value that can be used in spinner
	 * @param max the maximum value that can be used in spinner
	 * @param step the step of the spinner (the difference between consecutive
	 * values of the spinner)
	 * @param compact @code true} if this panel should be thinner (60 pixel),
	 * {@code false} otherwise (150 pixel)
	 */
	public AutoSpinnerPanel(int value, int min, int max, int step, boolean compact) {
		this(compact);
		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

		commonInit(value, min, max, step);
	}

	/**
	 * Initializes this panel:
	 * <ul>
	 * <li>sets the width of the panel (depending on the fact if the parameter
	 * should be compact),</li>
	 * <li>creates the buttons and sets their messages,</li>
	 * <li>adds {@link #spinnerRadio} and {@link #autoRadio} to the
	 * group ({@link #buttonGroup}),</li>
	 * <li>adds the listener to the {@link #autoRadio auto button}, which
	 * activates (auto button selected) or disactivates the spinner
	 * (otherwise),</li>
	 * </ul>
	 * @param value the value of of the spinner TODO not used
	 * @param min the minimum value of the spinner TODO not used
	 * @param max the maximum value of the spinner TODO not used
	 * @param step the step of the spinner TODO not used
	 */
	protected void commonInit(double value, double min, double max, double step) {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		Dimension spinnerDimension;
		if (compact) {
			spinnerDimension = new Dimension(60,25);
		} else {
			spinnerDimension = new Dimension(150,25);
		}
		spinner.setPreferredSize(spinnerDimension);
		spinner.setMinimumSize(spinnerDimension);
		spinner.setMaximumSize(spinnerDimension);

		buttonGroup = new ButtonGroup();

		spinnerRadio = new JRadioButton();
		if (compact) {
			autoRadio = new JRadioButton(_("un."));
		} else {
			autoRadio = new JRadioButton(_("auto"));
		}
		autoRadio.setFont(autoRadio.getFont().deriveFont(Font.PLAIN, 10));

		buttonGroup.add(spinnerRadio);
		buttonGroup.add(autoRadio);

		spinnerRadio.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				setNonAutoControlsEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}

		});

		add(autoRadio);
		add(spinnerRadio);
		add(spinner);

		spinnerRadio.setSelected(true);

	}


	/**
	 * Enables ({@code enabled = true}) or disables the spinner.
	 * @param enabled {@code true} if the spinner should be enabled,
	 * {@code false} otherwise
	 */
	protected void setNonAutoControlsEnabled(boolean enabled) {
		spinner.setEnabled(enabled);
	}

	/**
	 * Returns the value of the spinner in the form of {@code double}.
	 * @return the value of the spinner in the form of {@code double}.
	 */
	public double getValue() {
		return ((Number) spinner.getValue()).doubleValue();
	}

	/**
	 * Returns the value of the spinner in the form of {@code float}.
	 * @return the value of the spinner in the form of {@code float}.
	 */
	public float getFloatValue() {
		return ((Number) spinner.getValue()).floatValue();
	}

	/**
	 * Returns the value of the spinner in the form of {@code int}.
	 * @return the value of the spinner in the form of {@code int}.
	 */
	public int getIntegerValue() {
		return ((Number) spinner.getValue()).intValue();
	}

	/**
	 * Sets the value of the spinner.
	 * @param value the value to be set
	 */
	public void setValue(double value) {
		spinner.setValue(value);
	}

	/**
	 * Sets the value of the spinner.
	 * @param value the value in the form of {@code float} to be set
	 */
	public void setFloatValue(float value) {
		spinner.setValue(value);
	}

	/**
	 * Sets the value of the spinner.
	 * @param value the value in the form of {@code int} to be set
	 */
	public void setIntegerValue(int value) {
		spinner.setValue(value);
	}

	/**
	 * Returns if auto button is selected.
	 * @return {@code true} if auto button is selected,
	 * {@code false} otherwise
	 */
	public boolean isUnlimited() {
		return autoRadio.isSelected();
	}

	/**
	 * Sets which button should be selected:
	 * <ul>
	 * <li>auto button - if {@code auto == true},</li>
	 * <li>spinner button - if {@code auto == false}.</li>
	 * </ul>
	 * @param auto if auto button should be selected
	 */
	public void setAuto(boolean auto) {
		if (auto) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
		}
	}

	/**
	 * Returns the value of the spinner or {@code AUTO_VALUE} if
	 * auto button is selected (and spinner is disabled).
	 * @return the value of the spinner or {@code AUTO_VALUE} if
	 * auto button is selected
	 */
	public double getValueWithAuto() {
		if (autoRadio.isSelected()) {
			return AUTO_VALUE;
		} else {
			return ((Number) spinner.getValue()).doubleValue();
		}
	}

	/**
	 * Returns the value of the spinner in the form of {@code float} or
	 * {@code AUTO_VALUE} if auto button is selected (and spinner is disabled).
	 * @return the value of the spinner in the form of {@code float} or
	 * {@code AUTO_VALUE} if auto button is selected
	 */
	public float getFloatValueWithAuto() {
		if (autoRadio.isSelected()) {
			return AUTO_VALUE;
		} else {
			return ((Number) spinner.getValue()).floatValue();
		}
	}

	/**
	 * Returns the value of the spinner in the form of {@code int} or
	 * {@code AUTO_VALUE} if auto button is selected (and spinner is disabled).
	 * @return the value of the spinner or {@code AUTO_VALUE} if
	 * auto button is selected
	 */
	public int getIntegerValueWithUnlimited() {
		if (autoRadio.isSelected()) {
			return AUTO_VALUE;
		} else {
			return ((Number) spinner.getValue()).intValue();
		}
	}

	/**
	 * Depending on the {@code value}:
	 * <ul>
	 * <li>if the {@code value} is negative sets the auto button and
	 * disables the spinner,</li>
	 * <li>otherwise sets the value of the spinner, sets the spinner button and
	 * activates the spinner.</li></ul>
	 * @param value the value to be set
	 */
	public void setValueWithAuto(double value) {
		if (value < 0) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	/**
	 * Depending on the {@code value}:
	 * <ul>
	 * <li>if the {@code value} is negative sets the auto button and
	 * disables the spinner,</li>
	 * <li>otherwise sets the value of the spinner, sets the spinner button and
	 * activates the spinner.</li></ul>
	 * @param value the value to be set
	 */
	public void setFloatValueWithAuto(float value) {
		if (value < 0) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	/**
	 * Depending on the {@code value}:
	 * <ul>
	 * <li>if the {@code value} is negative sets the auto button and
	 * disables the spinner,</li>
	 * <li>otherwise sets the value of the spinner, sets the spinner button and
	 * activates the spinner.</li></ul>
	 * @param value the value to be set
	 */
	public void setIntegerValueWithAuto(int value) {
		if (value < 0) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	/**
	 * Stores the value of the spinner as the minimum value in the {@code
	 * MinMaxRange}.
	 * @param range the range in which the minimum value will be stored
	 */
	public void getMinValue(MinMaxRange range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMin(((Number) spinner.getValue()).doubleValue());
		range.setMinUnlimited(autoRadio.isSelected());
	}

	/**
	 * Sets the minimum value from {@code MinMaxRange} as the value of the
	 * spinner. Or if the minimum value is unlimited sets the auto button. 
	 * @param range the range to be used
	 */
	public void setMinValue(MinMaxRange range) {
		autoRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	/**
	 * Stores the value of the spinner as the maximum value in the {@code
	 * MinMaxRange}.
	 * @param range the range in which the maximum value will be stored
	 */
	public void getMaxValue(MinMaxRange range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMax(((Number) spinner.getValue()).doubleValue());
		range.setMaxUnlimited(autoRadio.isSelected());
	}

	/**
	 * Sets the maximum value from {@code MinMaxRange} as the value of the
	 * spinner. Or if the maximum value is unlimited sets the auto button. 
	 * @param range the range to be used
	 */
	public void setMaxValue(MinMaxRange range) {
		autoRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	/**
	 * Stores the value of the spinner as the minimum value in the {@code
	 * MinMaxRangeFloat}.
	 * @param range the range in which the minimum value will be stored
	 */
	public void getMinValue(MinMaxRangeFloat range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMin(((Number) spinner.getValue()).floatValue());
		range.setMinUnlimited(autoRadio.isSelected());
	}

	/**
	 * Sets the minimum value from {@code MinMaxRangeFloat} as the value of the
	 * spinner. Or if the minimum value is unlimited sets the auto button. 
	 * @param range the range to be used
	 */
	public void setMinValue(MinMaxRangeFloat range) {
		autoRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	/**
	 * Stores the value of the spinner as the maximum value in the {@code
	 * MinMaxRangeFloat}.
	 * @param range the range in which the maximum value will be stored
	 */
	public void getMaxValue(MinMaxRangeFloat range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMax(((Number) spinner.getValue()).floatValue());
		range.setMaxUnlimited(autoRadio.isSelected());
	}

	/**
	 * Sets the maximum value from {@code MinMaxRangeFloat} as the value of the
	 * spinner. Or if the maximum value is unlimited sets the auto button. 
	 * @param range the range to be used
	 */
	public void setMaxValue(MinMaxRangeFloat range) {
		autoRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	/**
	 * Stores the value of the spinner as the minimum value in the {@code
	 * MinMaxRangeInteger}.
	 * @param range the range in which the minimum value will be stored
	 */
	public void getMinValue(MinMaxRangeInteger range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMin(((Number) spinner.getValue()).intValue());
		range.setMinUnlimited(autoRadio.isSelected());
	}

	/**
	 * Sets the minimum value from {@code MinMaxRangeInteger} as the value of the
	 * spinner. Or if the minimum value is unlimited sets the auto button. 
	 * @param range the range to be used
	 */
	public void setMinValue(MinMaxRangeInteger range) {
		autoRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	/**
	 * Stores the value of the spinner as the maximum value in the {@code
	 * MinMaxRangeInteger}.
	 * @param range the range in which the maximum value will be stored
	 */
	public void getMaxValue(MinMaxRangeInteger range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMax(((Number) spinner.getValue()).intValue());
		range.setMaxUnlimited(autoRadio.isSelected());
	}

	/**
	 * Sets the maximum value from {@code MinMaxRangeInteger} as the value of the
	 * spinner. Or if the maximum value is unlimited sets the auto button. 
	 * @param range the range to be used
	 */
	public void setMaxValue(MinMaxRangeInteger range) {
		autoRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	/**
	 * Sets if this panel (all its components) should be enabled.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		autoRadio.setEnabled(enabled);
		spinner.setEnabled(enabled);
	}


}