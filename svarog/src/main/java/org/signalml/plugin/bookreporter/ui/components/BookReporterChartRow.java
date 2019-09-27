package org.signalml.plugin.bookreporter.ui.components;

import java.awt.GridLayout;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.plugin.bookreporter.chart.preset.BookReporterChartPreset;
import org.signalml.plugin.bookreporter.data.BookReporterConstants;
import org.signalml.plugin.bookreporter.data.BookReporterFASPThreshold;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterChartRow {

	private final CompactButton removeButton;
	private final BookReporterTitleLabel label;
	private final BookReporterChartPreset preset;

	private final BookReporterMinMaxSpinnerPanel amplitudePanel;
	private final BookReporterMinMaxSpinnerPanel frequencyPanel;
	private final BookReporterMinMaxSpinnerPanel scalePanel;
	private final BookReporterMinMaxSpinnerPanel phasePanel;
	
	public BookReporterChartRow(BookReporterChartPreset chartPreset) {
		removeButton = new CompactButton();
		label = new BookReporterTitleLabel(chartPreset.getCaption());
		preset = chartPreset;

		amplitudePanel = new BookReporterMinMaxSpinnerPanel(
			BookReporterConstants.MIN_AMPLITUDE, BookReporterConstants.MAX_AMPLITUDE,
			BookReporterConstants.MIN_AMPLITUDE, BookReporterConstants.MAX_AMPLITUDE,
			BookReporterConstants.INCR_AMPLITUDE, 0, 0);
		frequencyPanel = new BookReporterMinMaxSpinnerPanel(
			BookReporterConstants.MIN_FREQUENCY, BookReporterConstants.MAX_FREQUENCY,
			BookReporterConstants.MIN_FREQUENCY, BookReporterConstants.MAX_FREQUENCY,
			BookReporterConstants.INCR_FREQUENCY, 0, 0);
		scalePanel = new BookReporterMinMaxSpinnerPanel(
			BookReporterConstants.MIN_SCALE, BookReporterConstants.MAX_SCALE,
			BookReporterConstants.MIN_SCALE, BookReporterConstants.MAX_SCALE,
			BookReporterConstants.INCR_SCALE, 0, 0);
		phasePanel = new BookReporterMinMaxSpinnerPanel(
			BookReporterConstants.MIN_PHASE, BookReporterConstants.MAX_PHASE,
			BookReporterConstants.MIN_PHASE, BookReporterConstants.MAX_PHASE,
			BookReporterConstants.INCR_PHASE, 0, 0);
		
		BookReporterFASPThreshold fasp = chartPreset.getThreshold();
		amplitudePanel.setRange(fasp.amplitude);
		frequencyPanel.setRange(fasp.frequency);
		scalePanel.setRange(fasp.scale);
		phasePanel.setRange(fasp.phase);
	}

	public BookReporterChartPreset getPreset() {
		this.preset.setThreshold(new BookReporterFASPThreshold(
			frequencyPanel.getRange(),
			amplitudePanel.getRange(),
			scalePanel.getRange(),
			phasePanel.getRange()
		));
		return this.preset;
	}

	public void addToPanel(JPanel parent) {
		parent.add(removeButton);
		parent.add(label);
		parent.add(amplitudePanel);
		parent.add(frequencyPanel);
		parent.add(scalePanel);
		parent.add(phasePanel);
		parent.setLayout(new GridLayout(parent.getComponentCount()/6, 6, 6, 10));
	}
	
	public void removeFromPanel(JPanel parent) {
		parent.remove(removeButton);
		parent.remove(label);
		parent.remove(amplitudePanel);
		parent.remove(frequencyPanel);
		parent.remove(scalePanel);
		parent.remove(phasePanel);
		parent.setLayout(new GridLayout(parent.getComponentCount()/6, 6, 6, 10));
	}

	public void setRemoveAction(AbstractAction action) {
		this.removeButton.setAction(action);
	}
	
}
