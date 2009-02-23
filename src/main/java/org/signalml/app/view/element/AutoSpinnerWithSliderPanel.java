package org.signalml.app.view.element;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.context.support.MessageSourceAccessor;

public class AutoSpinnerWithSliderPanel extends AutoSpinnerPanel {
		
	private static final long serialVersionUID = 1L;

	private JSlider slider;
	
	private boolean lock = false;
	
	public AutoSpinnerWithSliderPanel(MessageSourceAccessor messageSource, double value, double min, double max,
			double step, boolean compact) {
		super(messageSource, value, min, max, step, compact);
	}

	public AutoSpinnerWithSliderPanel(MessageSourceAccessor messageSource, float value, float min, float max,
			float step, boolean compact) {
		super(messageSource, value, min, max, step, compact);
	}

	public AutoSpinnerWithSliderPanel(MessageSourceAccessor messageSource, int value, int min, int max, int step,
			boolean compact) {
		super(messageSource, value, min, max, step, compact);
	}



	@Override
	protected void commonInit(double value, double min, double max, double step) {
			super.commonInit(value, min, max, step);
					
		slider = new JSlider((int) min, (int) max, (int) value);
		int range = (((int) max)-((int) min));
		slider.setMajorTickSpacing( range );
		slider.setMinorTickSpacing( range / 10 );
		slider.setFont( slider.getFont().deriveFont(Font.PLAIN, 10) );
		slider.setExtent(0);
		slider.setPaintTicks(true);
		slider.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		
		slider.addChangeListener( new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				
				if( lock ) {
					return;
				}
				
				try {
					lock = true;
					
					spinner.setValue( slider.getValue() );
					
				} finally {
					lock = false;
				}
				
			}
						
		});
		
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if( lock ) {
					return;
				}
				
				try {
					lock = true;
					
					slider.setValue( ((Number) spinner.getValue()).intValue() );
					
				} finally {
					lock = false;
				}
				
			}
			
		});
		
		add(slider);
		
	}
	
	@Override
	protected void setNonAutoControlsEnabled(boolean enabled) {
		super.setNonAutoControlsEnabled(enabled);
		if (slider != null) slider.setEnabled(enabled);
	}
	
	
	public double getValue() {		
		return super.getValueWithAuto();
	}
	
	public void setValue( double value ) {
		super.setValueWithAuto(value);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		slider.setEnabled(enabled);
	}
	
}
