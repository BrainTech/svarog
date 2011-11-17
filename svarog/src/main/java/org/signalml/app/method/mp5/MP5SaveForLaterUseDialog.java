/* SaveForLaterUseDialog.java created 2008-01-31
 *
 */

package org.signalml.app.method.mp5;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.SignalMLException;

/** SaveForLaterUseDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5SaveForLaterUseDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final long serialVersionUID = 1L;

	private JCheckBox saveConfigCheckBox;
	private JCheckBox saveSignalCheckBox;

	public MP5SaveForLaterUseDialog() {
		super();
	}

	public MP5SaveForLaterUseDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Save for later use"));
		setIconImage(IconUtils.loadClassPathImage(MP5MethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new GridLayout(2,1,3,3));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(_("Choose what to save")),
		        new EmptyBorder(3,3,3,3)
		);

		interfacePanel.setBorder(border);

		interfacePanel.add(getSaveSignalCheckBox(), BorderLayout.NORTH);
		interfacePanel.add(getSaveConfigCheckBox(), BorderLayout.CENTER);

		return interfacePanel;

	}

	public JCheckBox getSaveConfigCheckBox() {
		if (saveConfigCheckBox == null) {
			saveConfigCheckBox = new JCheckBox(_("Save MP configuration"));
		}
		return saveConfigCheckBox;
	}

	public JCheckBox getSaveSignalCheckBox() {
		if (saveSignalCheckBox == null) {
			saveSignalCheckBox = new JCheckBox(_("Save signal for MP computation"));
		}
		return saveSignalCheckBox;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		MP5SaveForLaterUseDescriptor descriptor = (MP5SaveForLaterUseDescriptor) model;

		getSaveConfigCheckBox().setSelected(descriptor.isSaveConfig());
		getSaveSignalCheckBox().setSelected(descriptor.isSaveSignal());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MP5SaveForLaterUseDescriptor descriptor = (MP5SaveForLaterUseDescriptor) model;

		descriptor.setSaveConfig(getSaveConfigCheckBox().isSelected());
		descriptor.setSaveSignal(getSaveSignalCheckBox().isSelected());

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MP5SaveForLaterUseDescriptor.class.isAssignableFrom(clazz);
	}

}
