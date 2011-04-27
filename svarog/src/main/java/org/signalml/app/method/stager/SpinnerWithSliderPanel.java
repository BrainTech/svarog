/* SpinnerWithSliderPanel.java created 2008-02-14
 *
 */

package org.signalml.app.method.stager;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** SpinnerWithSliderPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SpinnerWithSliderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JSpinner spinner;
	private JSlider slider;

	private boolean lock = false;

	public SpinnerWithSliderPanel(double value, double min, double max, double step) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		slider = new JSlider((int) min, (int) max, (int) value);
		int range = (((int) max)-((int) min));
		slider.setMajorTickSpacing(range);
		slider.setMinorTickSpacing(range / 10);
		slider.setFont(slider.getFont().deriveFont(Font.PLAIN, 10));
		slider.setExtent(0);
		slider.setPaintTicks(true);
		slider.setAlignmentY(JComponent.CENTER_ALIGNMENT);

		spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
		Dimension spinnerDimension = new Dimension(120,25);
		spinner.setPreferredSize(spinnerDimension);
		spinner.setMinimumSize(spinnerDimension);
		spinner.setMaximumSize(spinnerDimension);
		spinner.setAlignmentY(JComponent.CENTER_ALIGNMENT);

		add(spinner);
		add(Box.createHorizontalStrut(3));
		add(slider);

		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if (lock) {
					return;
				}

				try {
					lock = true;

					spinner.setValue(slider.getValue());

				} finally {
					lock = false;
				}

			}

		});

		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if (lock) {
					return;
				}

				try {
					lock = true;

					slider.setValue(((Number) spinner.getValue()).intValue());

				} finally {
					lock = false;
				}

			}

		});

	}

	public double getValue() {
		return ((Number) spinner.getValue()).doubleValue();
	}

	public void setValue(double value) {
		spinner.setValue(value);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		spinner.setEnabled(enabled);
		slider.setEnabled(enabled);
	}

}
