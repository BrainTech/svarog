/* UnlimitedSpinnerPanel.java created 2008-02-14
 *
 */

package org.signalml.app.view.common.components.panels;

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
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.util.MinMaxRange;
import org.signalml.util.MinMaxRangeFloat;
import org.signalml.util.MinMaxRangeInteger;

/** TODO it is almost the same as {@link AutoSpinnerPanel}
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class UnlimitedSpinnerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private boolean compact;
	private JRadioButton spinnerRadio;
	private JRadioButton unlimitedRadio;
	private ButtonGroup buttonGroup;
	private JSpinner spinner;

	protected  UnlimitedSpinnerPanel(boolean compact) {
		super();
		this.compact = compact;
	}

	public UnlimitedSpinnerPanel(double value, double min, double max, double step, boolean compact) {
		this(compact);
		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

		commonInit();
	}

	public UnlimitedSpinnerPanel(float value, float min, float max, float step, boolean compact) {
		this(compact);
		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

		commonInit();
	}

	public UnlimitedSpinnerPanel(int value, int min, int max, int step, boolean compact) {
		this(compact);
		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));

		commonInit();
	}

	private void commonInit() {

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
			unlimitedRadio = new JRadioButton(_("un."));
		} else {
			unlimitedRadio = new JRadioButton(_("unlimited"));
		}
		unlimitedRadio.setFont(unlimitedRadio.getFont().deriveFont(Font.PLAIN, 10));

		buttonGroup.add(spinnerRadio);
		buttonGroup.add(unlimitedRadio);

		spinnerRadio.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					spinner.setEnabled(true);
				} else {
					spinner.setEnabled(false);
				}
			}

		});

		add(unlimitedRadio);
		add(spinnerRadio);
		add(spinner);

		spinnerRadio.setSelected(true);

	}

	protected JSpinner getSpinner() {
		return this.spinner;
	}

	protected JRadioButton getUnlimitedRadio() {
		return this.unlimitedRadio;
	}

	protected JRadioButton getSpinnerRadio() {
		return this.spinnerRadio;
	}

	protected ButtonGroup getButtonGroup() {
		return this.buttonGroup;
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
		return unlimitedRadio.isSelected();
	}

	public void setUnlimited(boolean unlimited) {
		if (unlimited) {
			unlimitedRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
		}
	}

	public double getValueWithUnlimited() {
		if (unlimitedRadio.isSelected()) {
			return MinMaxRange.UNLIMITED;
		} else {
			return ((Number) spinner.getValue()).doubleValue();
		}
	}

	public float getFloatValueWithUnlimited() {
		if (unlimitedRadio.isSelected()) {
			return MinMaxRange.UNLIMITED;
		} else {
			return ((Number) spinner.getValue()).floatValue();
		}
	}

	public int getIntegerValueWithUnlimited() {
		if (unlimitedRadio.isSelected()) {
			return MinMaxRange.UNLIMITED;
		} else {
			return ((Number) spinner.getValue()).intValue();
		}
	}

	public void setValueWithUnlimited(double value) {
		if (value == MinMaxRange.UNLIMITED) {
			unlimitedRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	public void setFloatValueWithUnlimited(float value) {
		if (value == MinMaxRange.UNLIMITED) {
			unlimitedRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	public void setIntegerValueWithUnlimited(int value) {
		if (value == MinMaxRange.UNLIMITED) {
			unlimitedRadio.setSelected(true);
		} else {
			spinnerRadio.setSelected(true);
			spinner.setValue(value);
		}
	}

	public void getMinValue(MinMaxRange range) {
		range.setMin(((Number) spinner.getValue()).doubleValue());
		range.setMinUnlimited(unlimitedRadio.isSelected());
	}

	public void setMinValue(MinMaxRange range) {
		unlimitedRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	public void getMaxValue(MinMaxRange range) {
		range.setMax(((Number) spinner.getValue()).doubleValue());
		range.setMaxUnlimited(unlimitedRadio.isSelected());
	}

	public void setMaxValue(MinMaxRange range) {
		unlimitedRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	public void getMinValue(MinMaxRangeFloat range) {
		range.setMin(((Number) spinner.getValue()).floatValue());
		range.setMinUnlimited(unlimitedRadio.isSelected());
	}

	public void setMinValue(MinMaxRangeFloat range) {
		unlimitedRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	public void getMaxValue(MinMaxRangeFloat range) {
		range.setMax(((Number) spinner.getValue()).floatValue());
		range.setMaxUnlimited(unlimitedRadio.isSelected());
	}

	public void setMaxValue(MinMaxRangeFloat range) {
		unlimitedRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

	public void getMinValue(MinMaxRangeInteger range) {
		range.setMin(((Number) spinner.getValue()).intValue());
		range.setMinUnlimited(unlimitedRadio.isSelected());
	}

	public void setMinValue(MinMaxRangeInteger range) {
		unlimitedRadio.setSelected(range.isMinUnlimited());
		spinner.setValue(range.getMin());
	}

	public void getMaxValue(MinMaxRangeInteger range) {
		range.setMax(((Number) spinner.getValue()).intValue());
		range.setMaxUnlimited(unlimitedRadio.isSelected());
	}

	public void setMaxValue(MinMaxRangeInteger range) {
		unlimitedRadio.setSelected(range.isMaxUnlimited());
		spinner.setValue(range.getMax());
	}

}
