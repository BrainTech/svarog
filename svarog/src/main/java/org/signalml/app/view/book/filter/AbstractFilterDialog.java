/* AbstractFilterDialog.java created 2008-03-04
 *
 */

package org.signalml.app.view.book.filter;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.domain.book.filter.AbstractAtomFilter;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

import org.springframework.validation.Errors;

/** AbstractFilterDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractFilterDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private JTextField nameTextField;

	private JPanel namePanel;

	public AbstractFilterDialog() {
		super();
	}

	public AbstractFilterDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	public JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setPreferredSize(new Dimension(200,25));
		}
		return nameTextField;
	}

	public JPanel getNamePanel() {
		if (namePanel == null) {
			namePanel = new JPanel(new BorderLayout());
			namePanel.setBorder(new CompoundBorder(
			                            new TitledBorder(_("Filter name")),
			                            new EmptyBorder(3,3,3,3)
			                    ));

			namePanel.add(getNameTextField());

		}
		return namePanel;
	}

	public void fillDialogFromFilter(AbstractAtomFilter filter) throws SignalMLException {

		getNameTextField().setText(filter.getName());

	}

	public void fillFilterFromDialog(AbstractAtomFilter filter) throws SignalMLException {

		filter.setName(getNameTextField().getText());

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		String name = getNameTextField().getText();
		if (name == null || name.isEmpty()) {
			errors.rejectValue("name", "error.atomFilter.nameEmpty");
		} else if (Util.hasSpecialChars(name)) {
			errors.rejectValue("name", "error.atomFilter.nameBadChars");
		}

	}


}
