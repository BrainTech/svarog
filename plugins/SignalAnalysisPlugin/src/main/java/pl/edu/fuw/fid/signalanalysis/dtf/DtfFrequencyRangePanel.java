package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Panel consisting of two labeled text fields for entering frequency range.
 *
 * @author ptr@mimuw.edu.pl
 */
public class DtfFrequencyRangePanel extends JPanel {

	private JTextField freqMin;
	private JTextField freqMax;
	private DtfFrequencyRangeListener listener;

	private static Double parseDoubleOrNull(String s) {
		try {
			return Double.valueOf(s);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	public DtfFrequencyRangePanel(double freqMinDefault, double freqMaxDefault) {
		super(new FlowLayout(FlowLayout.LEADING));
		freqMin = new JTextField(Double.toString(freqMinDefault), 8);
		freqMax = new JTextField(Double.toString(freqMaxDefault), 8);

		add(new JLabel("Frequency range [Hz]:"));
		add(freqMin);
		add(new JLabel("–"));
		add(freqMax);

		DocumentListener dl = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				DtfFrequencyRangeListener list = listener;
				if (list != null) {
					Double min = parseDoubleOrNull(freqMin.getText());
					Double max = parseDoubleOrNull(freqMax.getText());
					list.frequencyRangeChanged(min, max);
				}
			}
		};
		freqMin.getDocument().addDocumentListener(dl);
		freqMax.getDocument().addDocumentListener(dl);
	}

	public void setListener(DtfFrequencyRangeListener listener) {
		this.listener = listener;
	}

}
