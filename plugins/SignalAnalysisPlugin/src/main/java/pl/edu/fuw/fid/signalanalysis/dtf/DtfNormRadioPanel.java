package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Panel with a single switch, allowing to select "normalized"
 * or "not normalized" version of DTF method.
 *
 * @author ptr@mimuw.edu.pl
 */
public class DtfNormRadioPanel extends JPanel {

	private DtfNormSelectionListener listener;

	private class RadioListener implements ActionListener {

		private final boolean value;

		public RadioListener(boolean value) {
			this.value = value;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			DtfNormSelectionListener list = listener;
			if (list != null) {
				list.normalizedChanged(value);
			}
		}
	}

	public DtfNormRadioPanel() {
		super(new FlowLayout(FlowLayout.LEADING));

		JLabel label = new JLabel(_("Type of DTF:"));
		JRadioButton radioYes = new JRadioButton(_("normalized"));
		JRadioButton radioNo = new JRadioButton(_("not normalized"));
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioYes);
		buttonGroup.add(radioNo);
		radioYes.setSelected(true);

		radioYes.addActionListener(new RadioListener(true));
		radioNo.addActionListener(new RadioListener(false));

		add(label);
		add(radioYes);
		add(radioNo);
	}

	public void setListener(DtfNormSelectionListener listener) {
		this.listener = listener;
	}

}
