/* SourceMontageDialog.java created 2007-11-02
 *
 */

package org.signalml.app.view.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.model.montage.SourceMontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/** SourceMontageDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	private SourceMontageChannelsPanel editMontagePanel;

	private SourceMontage currentMontage;

	public SourceMontageDialog() {
		super();
	}

	public SourceMontageDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Signal montage"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/montage.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		editMontagePanel = new SourceMontageChannelsPanel();

		return editMontagePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		SourceMontageDescriptor descriptor = (SourceMontageDescriptor) model;

		currentMontage = new SourceMontage(descriptor.getMontage());

		editMontagePanel.setMontage(currentMontage);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		SourceMontageDescriptor descriptor = (SourceMontageDescriptor) model;

		descriptor.setMontage(currentMontage);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SourceMontageDescriptor.class.isAssignableFrom(clazz);
	}

}
