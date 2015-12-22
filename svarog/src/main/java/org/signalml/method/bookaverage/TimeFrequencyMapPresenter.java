package org.signalml.method.bookaverage;

import java.awt.Window;
import javax.swing.JDialog;

/**
 * @author ptr@mimuw.edu.pl
 */
public class TimeFrequencyMapPresenter {

	private final Window dialogParent;

	public TimeFrequencyMapPresenter(Window dialogParent) {
		this.dialogParent = dialogParent;
	}

	public void showResults(double[][] data, double freqMin, double freqMax, double timeRange) {
		TimeFrequencyMapPanel panel = new TimeFrequencyMapPanel(data, freqMin, freqMax, timeRange);
		JDialog dialog = new JDialog(dialogParent, "Average time-frequency map");
		dialog.add(panel);
		dialog.pack();
		dialog.setVisible(true);
	}

}
