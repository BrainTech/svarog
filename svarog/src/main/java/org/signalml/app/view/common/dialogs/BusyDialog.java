package org.signalml.app.view.common.dialogs;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class BusyDialog extends JDialog implements IBusyDialog {

	public static String CANCEL_BUTTON_PRESSED = "cancelButtonPressed";

	private JLabel label;
	private JProgressBar progressBar;
	private JButton cancelButton;

	public BusyDialog(Container parentContainer) {
		super();
		setPreferredSize(new Dimension(280, 90));
		this.setLayout(new BorderLayout());
		setLocationRelativeTo(parentContainer);
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(getLabel());
		panel.add(Box.createVerticalStrut(4));
		panel.add(getProgressBar());
		panel.add(Box.createVerticalStrut(4));
		panel.add(getCancelButton());
		this.add(panel);

		pack();
	}

	public JLabel getLabel() {
		if (label == null) {
			label = new JLabel(_("Please wait"));
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return label;
	}

	public JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
			progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return progressBar;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					fireCancelButtonPressed();
				}
			});
			cancelButton.setText(_("Cancel"));
			cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return cancelButton;
	}

	protected void fireCancelButtonPressed() {
		firePropertyChange(CANCEL_BUTTON_PRESSED, null, null);
	}

	public void setText(String text) {
		this.label.setText(text);
	}

	public void setCancellable(boolean cancellable) {
		getCancelButton().setVisible(cancellable);
	}

}
