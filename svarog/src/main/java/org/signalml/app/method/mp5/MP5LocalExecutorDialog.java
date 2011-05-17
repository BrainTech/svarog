/* MP5LocalExecutorDialog.java created 2008-02-08
 *
 */

package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.method.mp5.MP5LocalProcessExecutor;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5LocalExecutorDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5LocalExecutorDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private JTextField nameTextField;

	private MP5ExecutablePanel executablePanel;

	public MP5LocalExecutorDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public MP5LocalExecutorDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("mp5Method.config.local.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/configure.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel namePanel = new JPanel(new BorderLayout());

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("mp5Method.config.local.nameTitle")),
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
			executablePanel = new MP5ExecutablePanel(messageSource,fileChooser);
		}
		return executablePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		MP5LocalProcessExecutor executor = (MP5LocalProcessExecutor) model;

		String name = executor.getName();
		if (name == null) {
			name = messageSource.getMessage("mp5Method.config.local.newNameSuggestion");
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
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		if (Util.hasSpecialChars(getNameTextField().getText())) {
			errors.rejectValue("name", "error.nameBadCharacters");
		}

		getExecutablePanel().validatePanel(errors);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MP5LocalProcessExecutor.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}
