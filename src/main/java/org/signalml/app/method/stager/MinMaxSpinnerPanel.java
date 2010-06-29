/* MinMaxSpinnerPanel.java created 2008-02-14
 *
 */

package org.signalml.app.method.stager;

import java.awt.GridLayout;

import javax.swing.JPanel;

import org.signalml.app.view.element.UnlimitedSpinnerPanel;
import org.signalml.util.MinMaxRange;
import org.springframework.context.support.MessageSourceAccessor;

/** MinMaxSpinnerPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MinMaxSpinnerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private UnlimitedSpinnerPanel minPanel;
	private UnlimitedSpinnerPanel maxPanel;

	public MinMaxSpinnerPanel(MessageSourceAccessor messageSource, double minValue, double maxValue, double min, double max, double step) {
		super();
		setLayout(new GridLayout(2,1,0,3));

		minPanel = new UnlimitedSpinnerPanel(messageSource, minValue, min, max, step, true);
		maxPanel = new UnlimitedSpinnerPanel(messageSource, maxValue, min, max, step, true);

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
		minPanel.setValue(range.getMin());
		minPanel.setUnlimited(range.isMinUnlimited());
		maxPanel.setValue(range.getMax());
		maxPanel.setUnlimited(range.isMaxUnlimited());
	}

	public void getRange(MinMaxRange range) {
		range.setMin(minPanel.getValue());
		range.setMinUnlimited(minPanel.isUnlimited());
		range.setMax(maxPanel.getValue());
		range.setMaxUnlimited(maxPanel.isUnlimited());
	}

}
