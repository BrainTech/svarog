/* SlavePlotSettingsPopupDialog.java created 2007-11-08
 *
 */

package org.signalml.app.view.signal.popup;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.TitledCrossBorder;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;

/** SlavePlotSettingsPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SlavePlotSettingsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private SignalPlot currentPlot;

	private JRadioButton inheritedMontageRadioButton;
	private JRadioButton localMontageRadioButton;

	private ButtonGroup montageButtonGroup;

	private JCheckBox horizontalLockCheckBox;
	private JCheckBox verticalLockCheckBox;

	private EditMontageAction editMontageAction;
	private JButton editMontageButton;

	private SynchronizeNowAction synchronizeNowAction;
	private JButton synchronizeNowButton;

	private SignalMontageDialog signalMontageDialog;
	private Montage currentMontage;

	public SlavePlotSettingsPopupDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel synchronizationPanel = new JPanel();
		synchronizationPanel.setLayout(new BoxLayout(synchronizationPanel, BoxLayout.Y_AXIS));

		CompoundBorder border = new CompoundBorder(
			new TitledCrossBorder(_("Synchronization"), true),
			new EmptyBorder(3,3,3,3)
		);
		synchronizationPanel.setBorder(border);

		synchronizationPanel.add(getHorizontalLockCheckBox());
		synchronizationPanel.add(Box.createVerticalStrut(3));
		synchronizationPanel.add(getVerticalLockCheckBox());
		synchronizationPanel.add(Box.createVerticalStrut(5));
		synchronizationPanel.add(getSynchronizeNowButton());

		JPanel montagePanel = new JPanel();
		montagePanel.setLayout(new BoxLayout(montagePanel, BoxLayout.Y_AXIS));

		border = new CompoundBorder(
			new TitledBorder(_("Montage")),
			new EmptyBorder(3,3,3,3)
		);
		montagePanel.setBorder(border);

		montagePanel.add(getInheritedMontageRadioButton());
		montagePanel.add(Box.createVerticalStrut(3));
		montagePanel.add(getLocalMontageRadioButton());
		montagePanel.add(Box.createVerticalStrut(5));
		montagePanel.add(getEditMontageButton());

		SwingUtils.makeButtonsSameSize(new JButton[] { getSynchronizeNowButton(), getEditMontageButton() });

		interfacePanel.add(synchronizationPanel, BorderLayout.NORTH);
		interfacePanel.add(montagePanel);

		Dimension size = interfacePanel.getPreferredSize();
		if (size.width < 150) {
			size.width = 150;
		}
		interfacePanel.setPreferredSize(size);

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		currentPlot = (SignalPlot) model;

		getHorizontalLockCheckBox().setSelected(currentPlot.isHorizontalLock());
		getVerticalLockCheckBox().setSelected(currentPlot.isVerticalLock());

		currentMontage = currentPlot.getLocalMontage();
		if (currentMontage == null) {
			getInheritedMontageRadioButton().setSelected(true);
			currentMontage = currentPlot.getDocument().getMontage();
		} else {
			getLocalMontageRadioButton().setSelected(true);
		}

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		SignalPlot plot = (SignalPlot) model;

		plot.setHorizontalLock(getHorizontalLockCheckBox().isSelected());
		plot.setVerticalLock(getVerticalLockCheckBox().isSelected());

		boolean local = getLocalMontageRadioButton().isSelected();
		if (local) {
			plot.setLocalMontage(currentMontage);
		} else {
			plot.setLocalMontage(null);
		}

	}

	public ButtonGroup getMontageButtonGroup() {
		if (montageButtonGroup == null) {
			montageButtonGroup = new ButtonGroup();
		}
		return montageButtonGroup;
	}

	public JRadioButton getInheritedMontageRadioButton() {
		if (inheritedMontageRadioButton == null) {
			inheritedMontageRadioButton = new JRadioButton(_("Inherited from document"));
			getMontageButtonGroup().add(inheritedMontageRadioButton);
			inheritedMontageRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return inheritedMontageRadioButton;
	}

	public JRadioButton getLocalMontageRadioButton() {
		if (localMontageRadioButton == null) {
			localMontageRadioButton = new JRadioButton(_("Modified"));
			getMontageButtonGroup().add(localMontageRadioButton);
			localMontageRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			localMontageRadioButton.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
					getEditMontageAction().setEnabled(selected);
					if (selected) {
						currentPlot.setLocalMontage(currentMontage);
					} else {
						currentPlot.setLocalMontage(null);
					}

				}
			});
		}
		return localMontageRadioButton;
	}

	public JCheckBox getHorizontalLockCheckBox() {
		if (horizontalLockCheckBox == null) {
			horizontalLockCheckBox = new JCheckBox(_("Horizontal scroll lock"));
			horizontalLockCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			horizontalLockCheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					currentPlot.setHorizontalLock(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
		}
		return horizontalLockCheckBox;
	}

	public JCheckBox getVerticalLockCheckBox() {
		if (verticalLockCheckBox == null) {
			verticalLockCheckBox = new JCheckBox(_("Vertical scroll lock"));
			verticalLockCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			verticalLockCheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					currentPlot.setVerticalLock(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
		}
		return verticalLockCheckBox;
	}

	public EditMontageAction getEditMontageAction() {
		if (editMontageAction == null) {
			editMontageAction = new EditMontageAction();
		}
		return editMontageAction;
	}

	public JButton getEditMontageButton() {
		if (editMontageButton == null) {
			editMontageButton = new JButton(getEditMontageAction());
			editMontageButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return editMontageButton;
	}

	public SynchronizeNowAction getSynchronizeNowAction() {
		if (synchronizeNowAction == null) {
			synchronizeNowAction = new SynchronizeNowAction();
		}
		return synchronizeNowAction;
	}

	public JButton getSynchronizeNowButton() {
		if (synchronizeNowButton == null) {
			synchronizeNowButton = new JButton(getSynchronizeNowAction());
			synchronizeNowButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return synchronizeNowButton;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalPlot.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	public SignalMontageDialog getSignalMontageDialog() {
		return signalMontageDialog;
	}

	public void setSignalMontageDialog(SignalMontageDialog signalMontageDialog) {
		this.signalMontageDialog = signalMontageDialog;
	}

	protected class EditMontageAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditMontageAction() {
			super(_("Edit modified montage"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/montage.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Edit montage for this plot"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			MontageDescriptor descriptor = new MontageDescriptor(currentMontage, currentPlot.getDocument());

			boolean ok = signalMontageDialog.showDialog(descriptor, true);
			if (!ok) {
				return;
			}

			currentMontage = descriptor.getMontage();

			boolean local = getLocalMontageRadioButton().isSelected();
			if (local) {
				currentPlot.setLocalMontage(currentMontage);
			}

		}

	}

	protected class SynchronizeNowAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SynchronizeNowAction() {
			super(_("Synchronize now"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/synchronizenow.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Synchronize plot with master plot"));
		}

		public void actionPerformed(ActionEvent ev) {

			currentPlot.synchronizeToMaster();
			setVisible(false);

		}

	}

}
