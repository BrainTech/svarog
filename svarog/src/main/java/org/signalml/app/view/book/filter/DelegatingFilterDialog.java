/* DelegatingFilterDialog.java created 2008-03-04
 *
 */

package org.signalml.app.view.book.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.cellrenderers.FileListCellRenderer;
import org.signalml.domain.book.filter.DelegatingAtomFilter;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.util.Util;

/** DelegatingFilterDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DelegatingFilterDialog extends AbstractFilterDialog {

	private static final long serialVersionUID = 1L;

	private FileChooser fileChooser;

	private DefaultListModel classPathListModel;
	private JList classPathList;
	private JScrollPane classPathScrollPane;

	private JTextField fqClassNameTextField;

	private QuickFileAction quickFileAction;
	private AddDirectoryEntryAction addDirectoryEntryAction;
	private AddJarEntryAction addJarEntryAction;
	private RemoveEntryAction removeEntryAction;

	private JButton quickFileButton;
	private JButton addDirectoryEntryButton;
	private JButton addJarEntryButton;
	private JButton removeEntryButton;

	public DelegatingFilterDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {

		setTitle(_("Custom atom filter"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/filter.png"));

		quickFileAction = new QuickFileAction();
		addDirectoryEntryAction = new AddDirectoryEntryAction();
		addJarEntryAction = new AddJarEntryAction();
		removeEntryAction = new RemoveEntryAction();

		super.initialize();
		setResizable(false);

	}

	@Override
	public JComponent createInterface() {

		JPanel classPathPanel = new JPanel(new BorderLayout(5,5));

		classPathPanel.setBorder(new CompoundBorder(
									 new TitledBorder(_("Choose class path")),
									 new EmptyBorder(3,3,3,3)
								 ));

		JPanel rightPanel = new JPanel(new BorderLayout());

		JPanel buttonPanel = new JPanel(new GridLayout(3,1,3,3));

		SwingUtils.makeButtonsSameSize(new JButton[] { getQuickFileButton(), getAddDirectoryEntryButton(), getAddJarEntryButton(), getRemoveEntryButton() });

		buttonPanel.add(getAddDirectoryEntryButton());
		buttonPanel.add(getAddJarEntryButton());
		buttonPanel.add(getRemoveEntryButton());

		rightPanel.add(buttonPanel, BorderLayout.NORTH);
		rightPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		classPathPanel.add(getClassPathScrollPane(), BorderLayout.CENTER);
		classPathPanel.add(rightPanel, BorderLayout.EAST);

		JPanel fqClassNamePanel = new JPanel(new BorderLayout(5,5));
		fqClassNamePanel.setBorder(new CompoundBorder(
									   new TitledBorder(_("Choose fully qualified filter class name")),
									   new EmptyBorder(3,3,3,3)
								   ));

		fqClassNamePanel.add(getFqClassNameTextField(), BorderLayout.CENTER);
		fqClassNamePanel.add(getQuickFileButton(), BorderLayout.EAST);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getNamePanel(), BorderLayout.NORTH);
		interfacePanel.add(classPathPanel, BorderLayout.CENTER);
		interfacePanel.add(fqClassNamePanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	public DefaultListModel getClassPathListModel() {
		if (classPathListModel == null) {
			classPathListModel = new DefaultListModel();
		}
		return classPathListModel;
	}

	public JList getClassPathList() {
		if (classPathList == null) {
			classPathList = new JList(getClassPathListModel());
			classPathList.setCellRenderer(new FileListCellRenderer());
			classPathList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			classPathList.setFont(classPathList.getFont().deriveFont(Font.PLAIN, 12));

			classPathList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					removeEntryAction.setEnabled(classPathList.getSelectedIndex() >= 0);

				}

			});
		}
		return classPathList;
	}

	public JScrollPane getClassPathScrollPane() {
		if (classPathScrollPane == null) {
			classPathScrollPane = new JScrollPane(getClassPathList());
			classPathScrollPane.setPreferredSize(new Dimension(400,300));
		}
		return classPathScrollPane;
	}

	public JTextField getFqClassNameTextField() {
		if (fqClassNameTextField == null) {
			fqClassNameTextField = new JTextField();
			fqClassNameTextField.setPreferredSize(new Dimension(400,25));
		}
		return fqClassNameTextField;
	}

	public JButton getQuickFileButton() {
		if (quickFileButton == null) {
			quickFileButton = new JButton(quickFileAction);
		}
		return quickFileButton;
	}

	public JButton getAddDirectoryEntryButton() {
		if (addDirectoryEntryButton == null) {
			addDirectoryEntryButton = new JButton(addDirectoryEntryAction);
		}
		return addDirectoryEntryButton;
	}

	public JButton getAddJarEntryButton() {
		if (addJarEntryButton == null) {
			addJarEntryButton = new JButton(addJarEntryAction);
		}
		return addJarEntryButton;
	}

	public JButton getRemoveEntryButton() {
		if (removeEntryButton == null) {
			removeEntryButton = new JButton(removeEntryAction);
		}
		return removeEntryButton;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		DelegatingAtomFilter filter = (DelegatingAtomFilter) model;

		super.fillDialogFromFilter(filter);

		ArrayList<File> classPath = filter.getClassPath();
		DefaultListModel listModel = getClassPathListModel();

		listModel.clear();
		for (File file : classPath) {
			listModel.addElement(file);
		}

		getFqClassNameTextField().setText(filter.getFqClassName());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		DelegatingAtomFilter filter = (DelegatingAtomFilter) model;

		super.fillFilterFromDialog(filter);

		fillFilterFromDialog(filter);

		filter.initialize();

	}

	protected void fillFilterFromDialog(DelegatingAtomFilter filter) {

		DefaultListModel listModel = getClassPathListModel();
		int cnt = listModel.size();

		ArrayList<File> classPath = new ArrayList<>(cnt);
		for (int i=0; i<cnt; i++) {
			classPath.add((File) listModel.getElementAt(i));
		}

		filter.setClassPath(classPath);
		filter.setFqClassName(getFqClassNameTextField().getText());

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		if (getClassPathListModel().size() == 0) {
			errors.addError(_("At least one class path entry is required"));
		}

		String fqClassName = getFqClassNameTextField().getText();
		if (fqClassName == null || fqClassName.isEmpty()) {
			errors.addError(_("Fully qualified class name is required"));
		} else {
			fqClassName.trim();
			getFqClassNameTextField().setText(fqClassName);
			if (!Util.validateFqClassName(fqClassName)) {
				errors.addError(_("Invalid fully qualified class name"));
			}
		}

		if (!errors.hasErrors()) {
			DelegatingAtomFilter filter = new DelegatingAtomFilter();
			fillFilterFromDialog(filter);

			try {
				filter.initialize();
			} catch (Throwable t) {
				logger.error("Filter failed to initialize", t);
				errors.addError(_("Failed to initialize filter. See log file for details."));
			}
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return DelegatingAtomFilter.class.isAssignableFrom(clazz);
	}

	public FileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	protected class QuickFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public QuickFileAction() {
			super(_("Quick file"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Select a single java or class file. Contained class must be in default package"));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseCodeFile(DelegatingFilterDialog.this.getRootPane());
			if (file == null) {
				return;
			}

			String name = file.getName();
			int lastDot = name.lastIndexOf('.');
			if (lastDot >= 0) {
				name = name.substring(0, lastDot);
			}
			getFqClassNameTextField().setText(name);

			DefaultListModel listModel = getClassPathListModel();
			listModel.clear();
			File parent = file.getParentFile();
			if (parent != null) {
				listModel.addElement(parent);
			}

		}

	}

	protected class AddDirectoryEntryAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddDirectoryEntryAction() {
			super(_("Add directories"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/adddirectoryentry.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			File[] files = fileChooser.chooseClassPathDirectories(DelegatingFilterDialog.this.getRootPane());
			if (files == null) {
				return;
			}

			DefaultListModel listModel = getClassPathListModel();
			for (File file : files) {
				listModel.addElement(file);
			}

		}

	}

	protected class AddJarEntryAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddJarEntryAction() {
			super(_("Add jar files"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addjarentry.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			File[] files = fileChooser.chooseJarFiles(DelegatingFilterDialog.this.getRootPane());
			if (files == null) {
				return;
			}

			DefaultListModel listModel = getClassPathListModel();
			for (File file : files) {
				listModel.addElement(file);
			}

		}

	}

	protected class RemoveEntryAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveEntryAction() {
			super(_("Remove"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removeentry.png"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			JList list = getClassPathList();
			int selectedIndex = list.getSelectedIndex();

			if (selectedIndex < 0) {
				return;
			}

			DefaultListModel listModel = getClassPathListModel();
			listModel.remove(selectedIndex);

			int size = listModel.size();
			if (size > 0) {
				if (selectedIndex >= size) {
					selectedIndex--;
				}
				list.setSelectedIndex(selectedIndex);
			}

		}

	}

}
