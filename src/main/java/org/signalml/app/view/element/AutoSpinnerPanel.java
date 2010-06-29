/* UnlimitedSpinnerPanel.java created 2008-02-14
 *
 */

package org.signalml.app.view.element;

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
import org.springframework.context.support.MessageSourceAccessor;

/** UnlimitedSpinnerPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AutoSpinnerPanel extends JPanel {

	protected static final long serialVersionUID = 1L;

	public static final int AUTO_VALUE = -1111;

	protected MessageSourceAccessor messageSource;
	protected boolean compact;

	protected JRadioButton spinnerRadio;
	protected JRadioButton autoRadio;

	protected ButtonGroup buttonGroup;

	protected JSpinner spinner;

	protected JPanel visualPanel;

	protected AutoSpinnerPanel(MessageSourceAccessor messageSource, boolean compact) {
		super();
		this.messageSource = messageSource;
		this.compact = compact;
	}

	public AutoSpinnerPanel(MessageSourceAccessor messageSource, double value, double min, double max, double step, boolean compact) {
		this(messageSource, compact);
		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

		commonInit(value, min, max, step);
	}

	public AutoSpinnerPanel(MessageSourceAccessor messageSource, float value, float min, float max, float step, boolean compact) {
		this(messageSource, compact);
		spinner = new JSpinner(new SpinnerNumberModel((double) value, (double) min, (double) max, (double) step));

		commonInit(value, min, max, step);
	}

	public AutoSpinnerPanel(MessageSourceAccessor messageSource, int value, int min, int max, int step, boolean compact) {
		this(messageSource, compact);
		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

		commonInit(value, min, max, step);
	}

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
			autoRadio = new JRadioButton(messageSource.getMessage("unlimitedSpinnerPanel.unlimitedCompact"));
		} else {
			autoRadio = new JRadioButton(messageSource.getMessage("unlimitedSpinnerPanel.auto"));
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


	protected void setNonAutoControlsEnabled(boolean enabled) {
		spinner.setEnabled(enabled);
	}

	public double getValue() {
		return ((Number) spinner.getValue()).doubleValue();
	}

	public float getFloatValue() {
		return ((Number) spinner.getValue()).floatValue();
	}

	public int getIntegerValue() {
		return ((Number) spinner.getValue()).intValue();
	}

	public void setValue(double value) {
		spinner.setValue(value);
	}

	public void setFloatValue(float value) {
		spinner.setValue(value);
	}

	public void setIntegerValue(int value) {
		spinner.setValue(value);
	}

	public boolean isUnlimited() {
		return autoRadio.isSelected();
	}

	public void setAuto(boolean auto) {
		if (auto) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
		}
	}

	public double getValueWithAuto() {
		if (autoRadio.isSelected()) {
			return AUTO_VALUE;
		} else {
			return ((Number) spinner.getValue()).doubleValue();
		}
	}

	public float getFloatValueWithAuto() {
		if (autoRadio.isSelected()) {
			return AUTO_VALUE;
		} else {
			return ((Number) spinner.getValue()).floatValue();
		}
	}

	public int getIntegerValueWithUnlimited() {
		if (autoRadio.isSelected()) {
			return AUTO_VALUE;
		} else {
			return ((Number) spinner.getValue()).intValue();
		}
	}

	public void setValueWithAuto(double value) {
		if (value < 0) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	public void setFloatValueWithAuto(float value) {
		if (value < 0) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	public void setIntegerValueWithAuto(int value) {
		if (value < 0) {
			autoRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	public void getMinValue(MinMaxRange range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMin(((Number) spinner.getValue()).doubleValue());
		range.setMinUnlimited(autoRadio.isSelected());
	}

	public void setMinValue(MinMaxRange range) {
		autoRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	public void getMaxValue(MinMaxRange range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMax(((Number) spinner.getValue()).doubleValue());
		range.setMaxUnlimited(autoRadio.isSelected());
	}

	public void setMaxValue(MinMaxRange range) {
		autoRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	public void getMinValue(MinMaxRangeFloat range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMin(((Number) spinner.getValue()).floatValue());
		range.setMinUnlimited(autoRadio.isSelected());
	}

	public void setMinValue(MinMaxRangeFloat range) {
		autoRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	public void getMaxValue(MinMaxRangeFloat range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMax(((Number) spinner.getValue()).floatValue());
		range.setMaxUnlimited(autoRadio.isSelected());
	}

	public void setMaxValue(MinMaxRangeFloat range) {
		autoRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	public void getMinValue(MinMaxRangeInteger range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMin(((Number) spinner.getValue()).intValue());
		range.setMinUnlimited(autoRadio.isSelected());
	}

	public void setMinValue(MinMaxRangeInteger range) {
		autoRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	public void getMaxValue(MinMaxRangeInteger range) {
		range.setUnlimitedValue(AUTO_VALUE);
		range.setMax(((Number) spinner.getValue()).intValue());
		range.setMaxUnlimited(autoRadio.isSelected());
	}

	public void setMaxValue(MinMaxRangeInteger range) {
		autoRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		autoRadio.setEnabled(enabled);
		spinner.setEnabled(enabled);
	}


}
