/* MP5ExecutablePanel.java created 2007-10-31
 *
 */
package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.SvarogApplication;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.method.mp5.MP5LocalProcessExecutor;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.util.Util;

/** MP5ExecutablePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExecutablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField executableTextField;
	private JButton chooseExecutableButton;

	private FileChooser fileChooser;

	private File mp5Executable;

	public MP5ExecutablePanel(FileChooser fileChooser) {
		super();
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("MP5 executable")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel executableLabel = new JLabel(_("Path to binary"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(executableLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getExecutableTextField())
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getChooseExecutableButton())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(executableLabel)
			.addComponent(getExecutableTextField())
			.addComponent(getChooseExecutableButton())
		);

		layout.setVerticalGroup(vGroup);

	}

	public JTextField getExecutableTextField() {
		if (executableTextField == null) {
			executableTextField = new JTextField();
			executableTextField.setPreferredSize(new Dimension(300,25));
			executableTextField.setEditable(false);
		}
		return executableTextField;
	}

	public JButton getChooseExecutableButton() {
		if (chooseExecutableButton == null) {
			chooseExecutableButton = new JButton(new ChooseExecutableAction());
		}
		return chooseExecutableButton;
	}

	public void fillPanelFromModel(MP5LocalProcessExecutor executor) {

		String path = executor.getMp5ExecutablePath();
		if (path == null) {

			mp5Executable = null;

			String osName = System.getProperty("os.name");
			if (Util.WINDOWS_OS_PATTERN.matcher(osName).matches()) {

				File executable = new File(SvarogApplication.getSharedInstance().getStartupDir(), "native/windows/bin/mp5.exe");
				if (executable.exists()) {
					if (executable.canExecute()) {
						mp5Executable = executable;
					}
				}

			}
			else if (Util.LINUX_OS_PATTERN.matcher(osName).matches()) {

				File executable = new File(SvarogApplication.getSharedInstance().getStartupDir(), "native/linux/bin/mp5");
				if (executable.exists()) {
					if (!executable.canExecute()) {
						executable.setExecutable(true, true);
					}
					if (executable.canExecute()) {
						mp5Executable = executable;
					}
				}

			}
			else if (Util.MAC_OS_PATTERN.matcher(osName).matches()) {

				File executable = new File(SvarogApplication.getSharedInstance().getStartupDir(), "native/mac/bin/mp5");
				if (executable.exists()) {
					if (!executable.canExecute()) {
						executable.setExecutable(true, true);
					}
					if (executable.canExecute()) {
						mp5Executable = executable;
					}
				}
			} else {
				// other os - do nothing
			}

			if (mp5Executable == null) {
				getExecutableTextField().setText("");
			} else {
				getExecutableTextField().setText(mp5Executable.getAbsolutePath());
			}
		} else {
			mp5Executable = new File(path);
			getExecutableTextField().setText(path);
		}

	}

	public void fillModelFromPanel(MP5LocalProcessExecutor executor) {

		if (mp5Executable == null) {
			executor.setMp5ExecutablePath(null);
		} else {
			executor.setMp5ExecutablePath(mp5Executable.getAbsolutePath());
		}

	}

	public void validatePanel(ValidationErrors errors) {

		if (mp5Executable == null || !mp5Executable.exists() || !mp5Executable.canExecute()) {
			errors.addError(_("MP5 executable not found or not accessible"));
		}

	}

	protected class ChooseExecutableAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseExecutableAction() {
			super(_("Choose..."));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Choose MP5 executable file"));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseExecutableFile(MP5ExecutablePanel.this.getTopLevelAncestor());
			if (file == null) {
				return;
			}

			mp5Executable = file;

			getExecutableTextField().setText(mp5Executable.getAbsolutePath());

		}

	}

}
