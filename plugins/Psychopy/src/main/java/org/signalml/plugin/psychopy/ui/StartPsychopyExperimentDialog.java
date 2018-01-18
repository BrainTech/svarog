package org.signalml.plugin.psychopy.ui;

import javax.swing.*;
import java.awt.event.*;

public class StartPsychopyExperimentDialog extends JDialog {
	private JPanel contentPane;
	private JButton buttonRun;
	private JButton buttonCancel;
	private JTextField experimentPath;
	private JButton selectExperimentPath;
	private JTextField outputDirectoryPath;
	private JButton selectOutputDirectoryPath;

	public StartPsychopyExperimentDialog() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonRun);

		buttonRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onOK() {
		// add your code here
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

	public static void main(String[] args) {
		StartPsychopyExperimentDialog dialog = new StartPsychopyExperimentDialog();
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}
}
