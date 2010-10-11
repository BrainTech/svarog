/* SignalSelectionDialog.java created 2007-10-04
 *
 */
package org.signalml.app.view.dialog;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.SignalSelectionPanel;
import org.signalml.domain.signal.BoundedSignalSelection;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** SignalSelectionDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelectionDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private SignalSelectionPanel signalSelectionPanel;

	public SignalSelectionDialog(MessageSourceAccessor messageSource, Window f, boolean isModal) {
		super(messageSource, f, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("signalSelection.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/preciseselection.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		signalSelectionPanel = new SignalSelectionPanel(messageSource, true);

		return signalSelectionPanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		BoundedSignalSelection bss = (BoundedSignalSelection) model;
		signalSelectionPanel.fillPanelFromModel(bss);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		BoundedSignalSelection bss = (BoundedSignalSelection) model;
		signalSelectionPanel.fillModelFromPanel(bss);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		signalSelectionPanel.validatePanel(errors);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BoundedSignalSelection.class.isAssignableFrom(clazz);
	}

}
