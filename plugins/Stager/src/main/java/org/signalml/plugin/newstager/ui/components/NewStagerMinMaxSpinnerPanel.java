/* MinMaxSpinnerPanel.java created 2008-02-14
 *
 */

package org.signalml.plugin.newstager.ui.components;

import java.awt.GridLayout;

import javax.swing.JPanel;

import org.signalml.util.MinMaxRange;

/**
 * MinMaxSpinnerPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerMinMaxSpinnerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private NewStagerUnlimitedAutoSpinnerPanel minPanel;
	private NewStagerUnlimitedAutoSpinnerPanel maxPanel;

	public NewStagerMinMaxSpinnerPanel(double minValue, double maxValue, double min,
							  double max, double step, double minAuto, double maxAuto) {
		super();
		setLayout(new GridLayout(2, 1, 0, 3));

		minPanel = new NewStagerUnlimitedAutoSpinnerPanel(minValue, min, max, step, minAuto, true, false);
		maxPanel = new NewStagerUnlimitedAutoSpinnerPanel(maxValue, min, max, step, maxAuto, true, false);

		add(minPanel);
		add(maxPanel);

	}

	public double getMin() {
		return minPanel.getValue();
	}

	public void setMin(double min) {
		minPanel.setValue(min);
	}

	public boolean isMinUnlimited() {
		return minPanel.isUnlimited();
	}

	public void setMinUnlimited(boolean unlimited) {
		minPanel.setUnlimited(unlimited);
	}

	public double getMinWithUnlimited() {
		return minPanel.getValueWithUnlimited();
	}

	public void setMinWithUnlimited(double min) {
		minPanel.setValueWithUnlimited(min);
	}

	public double getMax() {
		return maxPanel.getValue();
	}

	public void setMax(double max) {
		maxPanel.setValue(max);
	}

	public boolean isMaxUnlimited() {
		return maxPanel.isUnlimited();
	}

	public void setMaxUnlimited(boolean unlimited) {
		maxPanel.setUnlimited(unlimited);
	}

	public double getMaxWithUnlimited() {
		return maxPanel.getValueWithUnlimited();
	}

	public void setMaxWithUnlimited(double max) {
		maxPanel.setValueWithUnlimited(max);
	}

	public void setRange(MinMaxRange range) {
		this.setRange(range, null, null);
	}

	public void setRange(MinMaxRange range, Double autoMin, Double autoMax) {
		this.setPanel(minPanel, range.getMin(), range.isMinUnlimited(), autoMin);
		this.setPanel(maxPanel, range.getMax(), range.isMaxUnlimited(), autoMax);
	}

	private void setPanel(NewStagerUnlimitedAutoSpinnerPanel panel, double value, boolean unlimited, Double autoValue) {
		panel.setValue(value);
		panel.setUnlimited(unlimited);
		if (autoValue != null) {
			panel.setAuto();
			panel.setAutoValue(autoValue);
		}

	}

	public void getRange(MinMaxRange range) {
		range.setMin(minPanel.getValue());
		range.setMinUnlimited(minPanel.isUnlimited());
		range.setMax(maxPanel.getValue());
		range.setMaxUnlimited(maxPanel.isUnlimited());
	}

	public boolean isMinAuto() {
		return minPanel.isAuto();
	}

	public boolean isMaxAuto() {
		return maxPanel.isAuto();
	}
}
