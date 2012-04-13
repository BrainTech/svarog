package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Container;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class BusyDialog extends JDialog {

	private JLabel label;
	private JProgressBar progressBar;

	public BusyDialog(Container parentContainer) {
		super();
		setLocationRelativeTo(parentContainer);
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(200, 70);
		this.setUndecorated(true);
		this.setResizable(false);

		label = new JLabel(_("Please wait"));
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);

		JPanel panel = new JPanel();
		panel.add(label);
		panel.add(progressBar);
		this.add(panel);

	}
}
