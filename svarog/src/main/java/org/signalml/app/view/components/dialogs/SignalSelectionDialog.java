/* SignalSelectionDialog.java created 2007-10-04
 *
 */
package org.signalml.app.view.components.dialogs;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.SignalSelectionPanel;
import org.signalml.domain.signal.BoundedSignalSelection;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelection;

import org.springframework.validation.Errors;

/**
 * The dialog which allows to select the parameters of the {@link
 * SignalSelection signal selection}.
 * For more details see - {@link SignalSelectionPanel}.
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelectionDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SignalSelectionPanel panel} which allows to select the
	 * parameters of the {@link SignalSelection signal selection}
	 */
	private SignalSelectionPanel signalSelectionPanel;

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param f the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public SignalSelectionDialog(Window f, boolean isModal) {
		super(f, isModal);
	}

	/**
	 * Sets the title, the icon and that this panel can not be resized and
	 * calls the {@link AbstractDialog#initialize() initialization} in parent.
	 */
	@Override
	protected void initialize() {
		setTitle(_("Signal selection"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/preciseselection.png"));
		setResizable(false);
		super.initialize();
	}

	/**
	 * Creates the interface for this dialog with only one panel - {@link
	 * SignalSelectionDialog}.
	 */
	@Override
	public JComponent createInterface() {

		signalSelectionPanel = new SignalSelectionPanel(true);

		return signalSelectionPanel;

	}

	/**
	 * Fills the {@link SignalSelectionPanel#fillPanelFromModel(
	 * BoundedSignalSelection) panel} for this dialog using the given
	 * {@link BoundedSignalSelection model}.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		BoundedSignalSelection bss = (BoundedSignalSelection) model;
		signalSelectionPanel.fillPanelFromModel(bss);

	}

	/**
	 * Fills the given {@link BoundedSignalSelection model} from the {@link
	 * SignalSelectionPanel#fillModelFromPanel(BoundedSignalSelection) panel}
	 * for this dialog.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		BoundedSignalSelection bss = (BoundedSignalSelection) model;
		signalSelectionPanel.fillModelFromPanel(bss);

	}

	/**
	 * Validates this dialog. This dialog is valid if the panel for this dialog
	 * {@link SignalSelectionPanel#validatePanel(Errors) is valid}.
	 */
	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		signalSelectionPanel.validatePanel(errors);

	}

	/**
	 * The model for this dialog must be of type {@link BoundedSignalSelection}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BoundedSignalSelection.class.isAssignableFrom(clazz);
	}

}
