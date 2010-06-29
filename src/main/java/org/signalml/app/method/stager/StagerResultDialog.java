/* StagerResultDialog.java created 2008-02-20
 *
 */

package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerResultDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerResultDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private JCheckBox primaryOpenInWindowCheckBox;
	private JCheckBox primarySaveToFileCheckBox;

	private StagerResultTagPanel primaryTagPanel;

	private JList additionalTagList;
	private JScrollPane additionalTagScrollPane;

	private JCheckBox additionalOpenInWindowCheckBox;
	private JCheckBox additionalSaveToFileCheckBox;

	private StagerResultReviewDialog resultReviewDialog = null;
	private StagerResultTargetDescriptor currentDescriptor = null;

	private SignalDocument signalDocument;

	public StagerResultDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public StagerResultDialog(MessageSourceAccessor messageSource, Window w, SignalDocument signalDocument, boolean isModal) {
		super(messageSource, w, isModal);
		this.signalDocument = signalDocument;
	}

	@Override
	protected JPanel createControlPane() {
		JPanel controlPane = super.createControlPane();
		controlPane.add(Box.createHorizontalStrut(10), 1);
		controlPane.add(new JButton(new ReviewResultAction()), 1);
		return controlPane;
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("stagerMethod.dialog.result.title"));
		setIconImage(IconUtils.loadClassPathImage(StagerMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("stagerMethod.dialog.result.actionTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		checkBoxPanel.setBorder(border);

		checkBoxPanel.add(getPrimaryOpenInWindowCheckBox());
		checkBoxPanel.add(getPrimarySaveToFileCheckBox());

		JPanel additionalTagPanel = new JPanel(new BorderLayout(3,3));
		border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("stagerMethod.dialog.result.additionalTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		additionalTagPanel.setBorder(border);

		JPanel additionalCheckBoxPanel = new JPanel();
		additionalCheckBoxPanel.setLayout(new BoxLayout(additionalCheckBoxPanel, BoxLayout.Y_AXIS));

		additionalCheckBoxPanel.add(getAdditionalOpenInWindowCheckBox());
		additionalCheckBoxPanel.add(getAdditionalSaveToFileCheckBox());

		additionalTagPanel.add(getAdditionalTagScrollPane(), BorderLayout.CENTER);
		additionalTagPanel.add(additionalCheckBoxPanel, BorderLayout.SOUTH);

		interfacePanel.add(checkBoxPanel, BorderLayout.NORTH);
		interfacePanel.add(getPrimaryTagPanel(), BorderLayout.CENTER);
		interfacePanel.add(additionalTagPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	public JCheckBox getPrimaryOpenInWindowCheckBox() {
		if (primaryOpenInWindowCheckBox == null) {
			primaryOpenInWindowCheckBox = new JCheckBox(messageSource.getMessage("stagerMethod.dialog.result.openInWindow"));
		}
		return primaryOpenInWindowCheckBox;
	}

	public JCheckBox getPrimarySaveToFileCheckBox() {
		if (primarySaveToFileCheckBox == null) {
			primarySaveToFileCheckBox = new JCheckBox(messageSource.getMessage("stagerMethod.dialog.result.saveToFile"), true);

			primarySaveToFileCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

					getPrimaryTagPanel().setEnabled(selected);

				}

			});

		}
		return primarySaveToFileCheckBox;
	}

	public StagerResultTagPanel getPrimaryTagPanel() {
		if (primaryTagPanel == null) {
			primaryTagPanel = new StagerResultTagPanel(messageSource,fileChooser);
		}
		return primaryTagPanel;
	}

	public JCheckBox getAdditionalOpenInWindowCheckBox() {
		if (additionalOpenInWindowCheckBox == null) {
			additionalOpenInWindowCheckBox = new JCheckBox(messageSource.getMessage("stagerMethod.dialog.result.additionalOpenInWindow"));

			additionalOpenInWindowCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					updateAdditionalTagListEnabled();
				}

			});

		}
		return additionalOpenInWindowCheckBox;
	}

	public JCheckBox getAdditionalSaveToFileCheckBox() {
		if (additionalSaveToFileCheckBox == null) {
			additionalSaveToFileCheckBox = new JCheckBox(messageSource.getMessage("stagerMethod.dialog.result.additionalSaveToFile"));

			additionalSaveToFileCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					updateAdditionalTagListEnabled();
				}

			});

		}
		return additionalSaveToFileCheckBox;
	}

	public JList getAdditionalTagList() {
		if (additionalTagList == null) {
			additionalTagList = new JList();
		}
		return additionalTagList;
	}

	public JScrollPane getAdditionalTagScrollPane() {
		if (additionalTagScrollPane == null) {
			additionalTagScrollPane = new JScrollPane(getAdditionalTagList());

			additionalTagScrollPane.setPreferredSize(new Dimension(300,200));
		}
		return additionalTagScrollPane;
	}

	private void updateAdditionalTagListEnabled() {

		boolean enabled = (getAdditionalOpenInWindowCheckBox().isSelected() || getAdditionalSaveToFileCheckBox().isSelected());
		getAdditionalTagList().setEnabled(enabled);

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		StagerResultTargetDescriptor descriptor = (StagerResultTargetDescriptor) model;

		JCheckBox openInWindowCheckBox = getPrimaryOpenInWindowCheckBox();
		if (descriptor.isSignalAvailable()) {
			openInWindowCheckBox.setSelected(descriptor.isPrimaryOpenInWindow());
			openInWindowCheckBox.setEnabled(true);
		} else {
			openInWindowCheckBox.setSelected(false);
			openInWindowCheckBox.setEnabled(false);
		}

		getPrimarySaveToFileCheckBox().setSelected(descriptor.isPrimarySaveToFile());
		getPrimaryTagPanel().fillPanelFromModel(descriptor);

		JList list = getAdditionalTagList();
		DefaultListModel listModel = new DefaultListModel();
		ArrayList<File> additionalTags = descriptor.getAdditionalTags();
		for (File f : additionalTags) {
			listModel.addElement(f.getName());
		}
		list.setModel(listModel);

		list.clearSelection();
		int index;
		for (File f : descriptor.getChosenAdditionalTags()) {
			index = additionalTags.indexOf(f);
			list.addSelectionInterval(index, index);
		}

		JCheckBox additionalOpenInWindowCheckBox = getAdditionalOpenInWindowCheckBox();
		if (descriptor.isSignalAvailable()) {
			additionalOpenInWindowCheckBox.setSelected(descriptor.isAdditionalOpenInWindow());
			additionalOpenInWindowCheckBox.setEnabled(true);
		} else {
			additionalOpenInWindowCheckBox.setSelected(false);
			additionalOpenInWindowCheckBox.setEnabled(false);
		}
		getAdditionalSaveToFileCheckBox().setSelected(descriptor.isAdditionalSaveToFile());

		updateAdditionalTagListEnabled();

		currentDescriptor = descriptor;

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		StagerResultTargetDescriptor descriptor = (StagerResultTargetDescriptor) model;

		descriptor.setPrimaryOpenInWindow(getPrimaryOpenInWindowCheckBox().isSelected());
		boolean primarySaveToFile = getPrimarySaveToFileCheckBox().isSelected();
		descriptor.setPrimarySaveToFile(primarySaveToFile);
		if (primarySaveToFile) {
			getPrimaryTagPanel().fillModelFromPanel(descriptor);
		} else {
			descriptor.setPrimaryTagFile(null);
		}

		JList list = getAdditionalTagList();
		ArrayList<File> additionalTags = descriptor.getAdditionalTags();

		ArrayList<File> chosenTags = new ArrayList<File>();

		int size = list.getModel().getSize();
		int i;
		for (i=0; i<size; i++) {
			if (list.isSelectedIndex(i)) {
				chosenTags.add(additionalTags.get(i));
			}
		}

		descriptor.setChosenAdditionalTags(chosenTags);

		descriptor.setAdditionalOpenInWindow(getAdditionalOpenInWindowCheckBox().isSelected());
		descriptor.setAdditionalSaveToFile(getAdditionalSaveToFileCheckBox().isSelected());

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		if (getPrimarySaveToFileCheckBox().isSelected()) {
			getPrimaryTagPanel().validatePanel(errors);
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return StagerResultTargetDescriptor.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	protected class ReviewResultAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ReviewResultAction() {
			super(messageSource.getMessage("stagerMethod.dialog.result.reviewResult"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/reviewresult.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("stagerMethod.dialog.result.reviewResultToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (resultReviewDialog == null) {
				resultReviewDialog = new StagerResultReviewDialog(messageSource, StagerResultDialog.this, signalDocument, true);
				resultReviewDialog.setFileChooser(fileChooser);
			}

			resultReviewDialog.showDialog(currentDescriptor, true);

		}

	}

}
