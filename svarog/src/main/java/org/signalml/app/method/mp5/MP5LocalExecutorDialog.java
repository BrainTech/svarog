/* MP5LocalExecutorDialog.java created 2008-02-08
 *
 */

package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.mp5.MP5LocalProcessExecutor;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.util.Util;

import org.springframework.validation.Errors;

/** MP5LocalExecutorDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5LocalExecutorDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	private FileChooser fileChooser;

	private JTextField nameTextField;

	private MP5ExecutablePanel executablePanel;

	public MP5LocalExecutorDialog() {
		super();
	}

	public MP5LocalExecutorDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Configure local executor"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/configure.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel namePanel = new JPanel(new BorderLayout());

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Executor name")),
			new EmptyBorder(3,3,3,3)
		);
		namePanel.setBorder(border);

		namePanel.add(getNameTextField(), BorderLayout.CENTER);

		interfacePanel.add(namePanel, BorderLayout.NORTH);
		interfacePanel.add(getExecutablePanel(), BorderLayout.CENTER);

		return interfacePanel;

	}

	public JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setPreferredSize(new Dimension(300,25));
		}
		return nameTextField;
	}

	public MP5ExecutablePanel getExecutablePanel() {
		if (executablePanel == null) {
			executablePanel = new MP5ExecutablePanel(fileChooser);
		}
		return executablePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		MP5LocalProcessExecutor executor = (MP5LocalProcessExecutor) model;

		String name = executor.getName();
		if (name == null) {
			name = _("New local executor");
		}

		JTextField nameField = getNameTextField();
		nameField.setText(name);
		nameField.selectAll();

		getExecutablePanel().fillPanelFromModel(executor);
		nameField.requestFocusInWindow();

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MP5LocalProcessExecutor executor = (MP5LocalProcessExecutor) model;
		executor.setName(getNameTextField().getText());

		getExecutablePanel().fillModelFromPanel(executor);

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		if (Util.hasSpecialChars(getNameTextField().getText())) {
			errors.addError(_("Name must not contain control characters"));
		}

		getExecutablePanel().validatePanel(errors);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MP5LocalProcessExecutor.class.isAssignableFrom(clazz);
	}

	public FileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}
