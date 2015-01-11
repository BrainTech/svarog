package org.signalml.plugin.bookreporter.ui.components;

import java.awt.GridLayout;
import javax.swing.JPanel;
import org.signalml.app.view.common.components.panels.UnlimitedSpinnerPanel;
import org.signalml.plugin.bookreporter.data.BookReporterMinMaxRange;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerMinMaxSpinnerPanel)
 */
public class BookReporterMinMaxSpinnerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final UnlimitedSpinnerPanel minPanel;
	private final UnlimitedSpinnerPanel maxPanel;

	public BookReporterMinMaxSpinnerPanel(double minValue, double maxValue, double min,
							  double max, double step, double minAuto, double maxAuto) {
		super();
		setLayout(new GridLayout(2, 1, 0, 3));

		minPanel = new UnlimitedSpinnerPanel(minValue, min, max, step, true);
		maxPanel = new UnlimitedSpinnerPanel(maxValue, min, max, step, true);

		add(minPanel);
		add(maxPanel);

	}

	public double getMin() {
		return minPanel.isUnlimited() ? Double.NEGATIVE_INFINITY : minPanel.getValue();
	}

	public void setMin(double min) {
		if (min == Double.NEGATIVE_INFINITY) {
			minPanel.setUnlimited(true);
		} else {
			minPanel.setValue(min);
			minPanel.setUnlimited(false);
			
		}
	}

	public boolean isMinUnlimited() {
		return minPanel.isUnlimited();
	}

	public double getMax() {
		return maxPanel.isUnlimited() ? Double.POSITIVE_INFINITY : maxPanel.getValue();
	}

	public void setMax(double max) {
		if (max == Double.POSITIVE_INFINITY) {
			maxPanel.setUnlimited(true);
		} else {
			maxPanel.setValue(max);
			maxPanel.setUnlimited(false);
			
		}
	}

	public boolean isMaxUnlimited() {
		return maxPanel.isUnlimited();
	}

	public void setRange(BookReporterMinMaxRange range) {
		this.setMin(range.getMin());
		this.setMax(range.getMax());
	}

	public BookReporterMinMaxRange getRange() {
		return new BookReporterMinMaxRange(getMin(), getMax());
	}

}
