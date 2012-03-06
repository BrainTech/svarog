package org.signalml.plugin.newstager.ui.components;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.util.MinMaxRange;

/**
 * Panel with two radio buttons, one spinner and one slider.
 * The buttons tell if the spinner (slider) should be enabled or the
 * {@link #AUTO_VALUE default value} should be used instead of it.
 * <p>
 * The value of this panel (either of the spinner/slider or auto) can be stored
 * in the {@link MinMaxRange min-max range} as the maximum or minimum value.
 * Also these values can be set as the value of the spinner/slider.
 *
 */
public class AutoSpinnerWithSliderPanel extends AutoSpinnerPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the slider
	 */
	private JSlider slider;

	/**
	 * the variable used to disable updating spinner when the slider is updated
	 * and vice versa
	 */
	private boolean lock = false;

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
	public AutoSpinnerWithSliderPanel( double value, double min, double max,
	                                  double step, boolean compact) {
		super(value, min, max, step, compact);
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
	public AutoSpinnerWithSliderPanel( float value, float min, float max,
	                                  float step, boolean compact) {
		super(value, min, max, step, compact);
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
	public AutoSpinnerWithSliderPanel( int value, int min, int max, int step,
	                                  boolean compact) {
		super(value, min, max, step, compact);
	}



	/**
	 * Performs the same {@link AutoSpinnerPanel#commonInit(double, double,
	 * double, double) initialization} as the {@link AutoSpinnerPanel parent}
	 * and:
	 * <ul>
	 * <li>creates the slider with the given {@code value}, {@code minimum},
	 * {@code maximum} and {@code step},</li>
	 * <li>sets the ticks of the slider,</li>
	 * <li>adds the change listener to the slider which updates the value of
	 * the spinner,</li>
	 * <li>adds the change listener to the spinner which updates the value of
	 * the slider.</li>
	 * </ul>
	 */
	@Override
	protected void commonInit(double value, double min, double max, double step) {
		super.commonInit(value, min, max, step);

		slider = new JSlider((int) min, (int) max, (int) value);
		int range = (((int) max)-((int) min));
		slider.setMajorTickSpacing(range);
		slider.setMinorTickSpacing(range / 10);
		slider.setFont(slider.getFont().deriveFont(Font.PLAIN, 10));
		slider.setExtent(0);
		slider.setPaintTicks(true);
		slider.setAlignmentY(JComponent.CENTER_ALIGNMENT);

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

		add(slider);

	}

	/**
	 * Enables ({@code enabled = true}) or disables the spinner and the
	 * slider.
	 * @param enabled {@code true} if the spinner and the slider should be
	 * enabled, {@code false} otherwise
	 */
	@Override
	protected void setNonAutoControlsEnabled(boolean enabled) {
		super.setNonAutoControlsEnabled(enabled);
		if (slider != null) slider.setEnabled(enabled);
	}

	/**
	 * Returns the value of this panel (the slider and the spinner).
	 */
	public double getValue() {
		return super.getValueWithAuto();
	}

	/**
	 * Sets the value of this panel  (the slider and the spinner).
	 */
	public void setValue(double value) {
		super.setValueWithAuto(value);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		slider.setEnabled(enabled);
	}

}