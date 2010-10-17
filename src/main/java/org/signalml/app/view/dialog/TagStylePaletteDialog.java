/* TagStylePaletteDialog.java created 2007-11-10
 *
 */

package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.signalml.app.model.TagStylePaletteDescriptor;
import org.signalml.app.model.TagStyleTreeModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.TagStylePropertiesPanel;
import org.signalml.app.view.element.TagStyleTree;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** TagStylePaletteDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStylePaletteDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private TagStyleTreeModel tagStyleTreeModel;
	private TagStyleTree tagStyleTree;
	private JScrollPane treeScrollPane;
	private JPanel tagTreePanel;

	private TagStylePropertiesPanel tagStylePropertiesPanel;

	private AddPageStyleAction addPageStyleAction;
	private AddBlockStyleAction addBlockStyleAction;
	private AddChannelStyleAction addChannelStyleAction;
	private RemoveStyleAction removeStyleAction;

	private JButton addPageStyleButton;
	private JButton addBlockStyleButton;
	private JButton addChannelStyleButton;
	private JButton removeStyleButton;

	private ApplyChangesActionAction applyChangesActionAction;
	private JButton applyChangesButton;

	private JPanel buttonPanel;

	private KeyStrokeCaptureDialog keyStrokeCaptureDialog = null;

	private StyledTagSet currentTagSet;
	private TagStyle currentStyle;

	private boolean changed = false;

	public TagStylePaletteDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public TagStylePaletteDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("tagStylePalette.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/palette.png"));
		super.initialize();

		tagStyleTree.addTreeSelectionListener(new TreeSelectionListener() {

			private boolean lock = false;

			@Override
			public void valueChanged(TreeSelectionEvent e) {

				if (lock) {
					return;
				}

				try {

					TreePath path = e.getNewLeadSelectionPath();
					if (tagStylePropertiesPanel.isChanged()) {
						TreePath oldPath = e.getOldLeadSelectionPath();
						if (oldPath != null && !oldPath.equals(path)) {
							int res = OptionPane.showTagStyleModified(TagStylePaletteDialog.this);
							if (res == OptionPane.YES_OPTION) {
								boolean ok = validateAndApplyChanges();
								if (!ok) {
									lock = true;
									getTagStyleTree().setSelectionPath(oldPath);
									return; // unlocked on exit
								}
							} else if (res == OptionPane.CANCEL_OPTION) {
								lock = true;
								getTagStyleTree().setSelectionPath(oldPath);
								return; // unlocked on exit
							}
						}
					}
					if (path != null) {
						Object comp = path.getLastPathComponent();
						if (comp != null && comp instanceof TagStyle) {
							currentStyle = (TagStyle) comp;
							tagStylePropertiesPanel.setCurrentStyle(currentStyle);
						}
					}

					removeStyleAction.setEnabled(path);

				} finally {
					lock = false;
				}

			}
		});

		tagStylePropertiesPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (TagStylePropertiesPanel.CHANGED_PROPERTY.equals(evt.getPropertyName())) {
					applyChangesActionAction.setEnabled((Boolean) evt.getNewValue());
				}
			}
		});

	}

	@Override
	protected JPanel createControlPane() {

		JPanel controlPane =  super.createControlPane();

		applyChangesActionAction = new ApplyChangesActionAction();
		applyChangesButton = new JButton(applyChangesActionAction);
		controlPane.add(Box.createHorizontalStrut(5), 1);
		controlPane.add(applyChangesButton, 1);

		return controlPane;

	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		tagStylePropertiesPanel = new TagStylePropertiesPanel(messageSource);

		tagStylePropertiesPanel.getCaptureKeyButton().setAction(new CaptureKeyStrokeAction());

		interfacePanel.add(getTagTreePanel(), BorderLayout.WEST);
		interfacePanel.add(tagStylePropertiesPanel, BorderLayout.CENTER);

		return interfacePanel;

	}

	private TagStyleTreeModel getTagStyleTreeModel() {
		if (tagStyleTreeModel == null) {
			tagStyleTreeModel = new TagStyleTreeModel();
		}
		return tagStyleTreeModel;
	}

	private TagStyleTree getTagStyleTree() {
		if (tagStyleTree == null) {
			tagStyleTree = new TagStyleTree(getTagStyleTreeModel(), messageSource);
		}
		return tagStyleTree;
	}

	private JScrollPane getTreeScrollPane() {
		if (treeScrollPane == null) {
			treeScrollPane = new JScrollPane(getTagStyleTree());
			treeScrollPane.setPreferredSize(new Dimension(200,400));
		}
		return treeScrollPane;
	}

	private JPanel getTagTreePanel() {
		if (tagTreePanel == null) {

			tagTreePanel = new JPanel(new BorderLayout());
			tagTreePanel.setBorder(new TitledBorder(messageSource.getMessage("tagStylePalette.treeTitle")));
			tagTreePanel.add(getTreeScrollPane(), BorderLayout.CENTER);

			tagTreePanel.add(getButtonPanel(), BorderLayout.SOUTH);

		}
		return tagTreePanel;
	}

	private AddPageStyleAction getAddPageStyleAction() {
		if (addPageStyleAction == null) {
			addPageStyleAction = new AddPageStyleAction();
		}
		return addPageStyleAction;
	}

	private AddBlockStyleAction getAddBlockStyleAction() {
		if (addBlockStyleAction == null) {
			addBlockStyleAction = new AddBlockStyleAction();
		}
		return addBlockStyleAction;
	}

	private AddChannelStyleAction getAddChannelStyleAction() {
		if (addChannelStyleAction == null) {
			addChannelStyleAction = new AddChannelStyleAction();
		}
		return addChannelStyleAction;
	}

	private RemoveStyleAction getRemoveStyleAction() {
		if (removeStyleAction == null) {
			removeStyleAction = new RemoveStyleAction();
		}
		return removeStyleAction;
	}

	private JButton getAddPageStyleButton() {
		if (addPageStyleButton == null) {
			addPageStyleButton = new JButton(getAddPageStyleAction());
		}
		return addPageStyleButton;
	}

	private JButton getAddBlockStyleButton() {
		if (addBlockStyleButton == null) {
			addBlockStyleButton = new JButton(getAddBlockStyleAction());
		}
		return addBlockStyleButton;
	}

	private JButton getAddChannelStyleButton() {
		if (addChannelStyleButton == null) {
			addChannelStyleButton = new JButton(getAddChannelStyleAction());
		}
		return addChannelStyleButton;
	}

	private JButton getRemoveStyleButton() {
		if (removeStyleButton == null) {
			removeStyleButton = new JButton(getRemoveStyleAction());
		}
		return removeStyleButton;
	}

	public JPanel getButtonPanel() {

		if (buttonPanel == null) {
			buttonPanel = new JPanel(new GridLayout(5,1,3,3));
			buttonPanel.setBorder(new EmptyBorder(3,0,0,0));
			buttonPanel.add(getRemoveStyleButton());
			buttonPanel.add(Box.createVerticalStrut(1));
			buttonPanel.add(getAddPageStyleButton());
			buttonPanel.add(getAddBlockStyleButton());
			buttonPanel.add(getAddChannelStyleButton());
		}

		return buttonPanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		TagStylePaletteDescriptor descriptor = (TagStylePaletteDescriptor) model;

		currentTagSet = descriptor.getTagSet();

		tagStyleTreeModel.setTagSet(currentTagSet);

		TagStyle initialStyle = descriptor.getStyle();
		if (initialStyle == null) {
			Collection<TagStyle> styles = currentTagSet.getStyles();
			if (!styles.isEmpty()) {
				initialStyle = styles.iterator().next();
			}
		}

		currentStyle = initialStyle;
		tagStylePropertiesPanel.setCurrentStyle(initialStyle);

		TreePath path = new TreePath(getTagStyleTreeModel().getTagStylePath(initialStyle));
		getTagStyleTree().setSelectionPath(path);

		getTagStyleTree().expandPath(new TreePath(getTagStyleTreeModel().getTagStyleParentPath(SignalSelectionType.PAGE)));
		getTagStyleTree().expandPath(new TreePath(getTagStyleTreeModel().getTagStyleParentPath(SignalSelectionType.BLOCK)));
		getTagStyleTree().expandPath(new TreePath(getTagStyleTreeModel().getTagStyleParentPath(SignalSelectionType.CHANNEL)));

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		applyChanges();

		TagStylePaletteDescriptor descriptor = (TagStylePaletteDescriptor) model;
		descriptor.setChanged(changed);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		if (tagStylePropertiesPanel.isChanged()) {
			Errors styleErrors = validateChanges();
			errors.addAllErrors(styleErrors);
		}
	}

	@Override
	protected void onDialogClose() {
		tagStyleTreeModel.setTagSet(null);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return TagStylePaletteDescriptor.class.isAssignableFrom(clazz);
	}

	private void addNewStyle(TagStyle newStyle) {

		currentTagSet.addStyle(newStyle);

		TreePath path = new TreePath(getTagStyleTreeModel().getTagStylePath(newStyle));
		getTagStyleTree().setSelectionPath(path);

		currentStyle = newStyle;
		tagStylePropertiesPanel.setCurrentStyle(newStyle);
		JTextField nameTextField = tagStylePropertiesPanel.getNameTextField();

		nameTextField.selectAll();
		nameTextField.requestFocusInWindow();

	}

	private TagStyle getNewStyle(SignalSelectionType type) {
		String name = messageSource.getMessage("new");
		int cnt = 2;
		while (currentTagSet.getStyle(name) != null) {
			name = messageSource.getMessage("new") + " (" + cnt + ")";
			cnt++;
		}
		return new TagStyle(type, name, null, Color.GREEN, Color.GREEN, 1, null, null, false);
	}

	private Errors validateChanges() {

		Errors errors = tagStylePropertiesPanel.validateChanges();

		String newName = tagStylePropertiesPanel.getNameTextField().getText();
		TagStyle oldStyle = currentTagSet.getStyle(newName);
		if (oldStyle != null && oldStyle != currentStyle) {
			errors.rejectValue("name", "error.style.nameDuplicate");
		}

		KeyStroke keyStroke = KeyStroke.getKeyStroke(tagStylePropertiesPanel.getKeyTextField().getText());
		if (keyStroke != null) {
			oldStyle = currentTagSet.getStyleByKeyStroke(keyStroke);
			if (oldStyle != null && oldStyle != currentStyle) {
				errors.rejectValue("name", "error.style.keyStrokeDuplicate");
			}
		}

		return errors;

	}

	private void applyChanges() {

		boolean changed = false;
		TagStyle style = tagStylePropertiesPanel.getCurrentStyle();
		if (style == null) {
			return;
		}

		if (tagStylePropertiesPanel.isChanged()) {
			changed = true;
		}
		tagStylePropertiesPanel.applyChanges();

		this.changed |= changed;
		currentTagSet.updateStyle(currentStyle.getName(), style);

	}

	private boolean validateAndApplyChanges() {

		Errors errors = validateChanges();
		if (errors.hasErrors()) {
			getErrorsDialog().showErrors(errors);
			return false;
		}

		TagStyleTree tree = getTagStyleTree();
		TreePath path = tree.getSelectionPath();
		applyChanges();
		tree.setSelectionPath(path);

		return true;

	}

	@Override
	protected boolean onCancel() {
		if (tagStylePropertiesPanel.isChanged()) {
			int res = OptionPane.showTagStyleModified(TagStylePaletteDialog.this);
			if (res == OptionPane.YES_OPTION) {
				boolean ok = validateAndApplyChanges();
				if (!ok) {
					return false;
				}
			} else if (res == OptionPane.CANCEL_OPTION) {
				return false;
			} else {
				tagStylePropertiesPanel.setChanged(false); // discard, prevent events from the tree
			}
		}
		return true;
	}

	@Override
	protected void resetDialog() {
		changed = false;
	}

	protected class AddPageStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddPageStyleAction() {
			super(messageSource.getMessage("tagStylePalette.newPageStyle"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/pagetag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("tagStylePalette.newPageStyleToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			addNewStyle(getNewStyle(SignalSelectionType.PAGE));
			changed = true;

		}

	}

	protected class AddBlockStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddBlockStyleAction() {
			super(messageSource.getMessage("tagStylePalette.newBlockStyle"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/blocktag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("tagStylePalette.newBlockStyleToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			addNewStyle(getNewStyle(SignalSelectionType.BLOCK));
			changed = true;

		}

	}

	protected class AddChannelStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddChannelStyleAction() {
			super(messageSource.getMessage("tagStylePalette.newChannelStyle"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/channeltag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("tagStylePalette.newChannelStyleToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			addNewStyle(getNewStyle(SignalSelectionType.CHANNEL));
			changed = true;

		}

	}

	protected class RemoveStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveStyleAction() {
			super(messageSource.getMessage("tagStylePalette.removeStyle"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removetag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("tagStylePalette.removeStyleToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			TreePath selection = getTagStyleTree().getSelectionPath();
			if (selection == null) {
				return;
			}
			Object comp = selection.getLastPathComponent();
			if (comp == null || !(comp instanceof TagStyle)) {
				return;
			}
			TagStyle style = (TagStyle) comp;
			String name = style.getName();

			if (currentTagSet.hasTagsWithStyle(name)) {
				OptionPane.showTagStyleInUse(TagStylePaletteDialog.this);
				return;
			}

			currentTagSet.removeStyle(name);
			changed = true;

			getTagStyleTree().setSelectionPath(null);
			currentStyle = null;
			tagStylePropertiesPanel.setCurrentStyle(null);

		}

		public void setEnabled(TreePath selection) {
			boolean enabled = false;
			if (selection != null) {
				if (selection.getLastPathComponent() instanceof TagStyle) {
					enabled = true;
				}
			}

			setEnabled(enabled);
		}

	}

	protected class ApplyChangesActionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ApplyChangesActionAction() {
			super(messageSource.getMessage("tagStylePalette.applyChanges"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/apply.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("tagStylePalette.applyChangesToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			validateAndApplyChanges();

		}

	}

	protected class CaptureKeyStrokeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CaptureKeyStrokeAction() {
			super(messageSource.getMessage("tagStylePalette.captureKeyStroke"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/keyboard.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("tagStylePalette.captureKeyStrokeToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (keyStrokeCaptureDialog == null) {
				keyStrokeCaptureDialog = new KeyStrokeCaptureDialog(messageSource, TagStylePaletteDialog.this);
			}

			KeyStroke keyStroke = keyStrokeCaptureDialog.captureKeyStrokeWithEscAsCancel();
			if (keyStroke == null) {
				tagStylePropertiesPanel.getKeyTextField().setText("");
				return;
			}

			String s = keyStroke.toString();
			s = s.replaceAll("pressed *", "");
			tagStylePropertiesPanel.getKeyTextField().setText(s);

		}

	}

}
