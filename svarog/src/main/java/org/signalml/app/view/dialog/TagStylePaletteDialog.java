/* TagStylePaletteDialog.java created 2007-11-10
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import java.util.LinkedHashMap;
import java.util.List;
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
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;

import org.signalml.app.model.TagStylePaletteDescriptor;
import org.signalml.app.model.TagStyleTreeModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.TagStylePropertiesPanel;
import org.signalml.app.view.element.TagStyleTree;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import org.springframework.validation.Errors;

/**
 * Dialog which allows to add, remove and edit {@link TagStyle tag styles}.
 * This dialog contains two panels:
 * <ul>
 * <li>on the left - the panel with the {@link TagStyleTree tree} with
 * {@link TagStyle tag styles} and buttons to add/remove styles,</li>
 * <li>on the right - the {@link TagStylePropertiesPanel panel} with
 * the properties of the selected style.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStylePaletteDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link TagStyleTreeModel model} for the {@link
	 * #getTagStyleTree() tree} with {@link TagStyle tag styles}
	 */
	private TagStyleTreeModel tagStyleTreeModel;
	/**
	 * the {@link TagStyleTree tree} with {@link TagStyle tag styles}
	 */
	private TagStyleTree tagStyleTree;
	/**
	 * the panel with the {@link #getTagStyleTree() tree} of {@link
	 * TagStyle tag styles}
	 */
	private JScrollPane treeScrollPane;
	/**
	 * The left panel in this dialog.
	 * The panel contains two elements:
	 * <ul>
	 * <li>the {@link #getTreeScrollPane() panel} with the {@link TagStyleTree
	 * tree} of {@link TagStyle tag styles},</li>
	 * <li>the {@link #getButtonPanel() panel} with control buttons.</li></ul>
	 */
	private JPanel tagTreePanel;

	/**
	 * the {@link TagStylePropertiesPanel panel} with
	 * the properties of the selected style
	 */
	private TagStylePropertiesPanel tagStylePropertiesPanel;

	/**
	 * the {@link AddPageStyleAction action} which adds a new
	 * {@link SignalSelectionType#PAGE page} {@link TagStyle style}.
	 * If the action doesn't exist it is created
	 */
	private AddPageStyleAction addPageStyleAction;
	/**
	 * the {@link AddBlockStyleAction action} which adds a new
	 * {@link SignalSelectionType#BLOCK block} {@link TagStyle style}
	 */
	private AddBlockStyleAction addBlockStyleAction;
	/**
	 * the {@link AddChannelStyleAction action} which adds a new
	 * {@link SignalSelectionType#CHANNEL channel} {@link TagStyle style}
	 */
	private AddChannelStyleAction addChannelStyleAction;
	/**
	 * the {@link RemoveStyleAction action} which removes the
	 * currently selected {@link TagStyle style}
	 */
	private RemoveStyleAction removeStyleAction;

	/**
	 * the button which calls the {@link #getAddChannelStyleAction()
	 * action} which adds a new {@link SignalSelectionType#PAGE page}
	 * {@link TagStyle style}
	 */
	private JButton addPageStyleButton;
	/**
	 * the button which calls the {@link #getAddChannelStyleAction()
	 * action} which adds a new {@link SignalSelectionType#BLOCK block}
	 * {@link TagStyle style}
	 */
	private JButton addBlockStyleButton;
	/**
	 * the button which calls the {@link #getAddChannelStyleAction()
	 * action} which adds a new {@link SignalSelectionType#CHANNEL channel}
	 * {@link TagStyle style}
	 */
	private JButton addChannelStyleButton;
	/**
	 * the button which calls the {@link #getRemoveStyleAction() action}
	 * which removes the currently selected {@link TagStyle style}
	 */
	private JButton removeStyleButton;

	/**
	 * the {@link ApplyChangesActionAction action} which applies changes
	 */
	private ApplyChangesActionAction applyChangesActionAction;

	/**
	 * the button which calls the {@link ApplyChangesActionAction action}
	 * which applies changes
	 */
	private JButton applyChangesButton;

	/**
	 * the panel with buttons:
	 * <ul>
	 * <li>the button which removes the
	 * selected {@link TagStyle style},</li>
	 * <li>the button which adds a {@link SignalSelectionType#PAGE page} style,
	 * </li>
	 * <li>the button which adds a {@link SignalSelectionType#BLOCK block}
	 * style,</li>
	 * <li>the button which adds a {@link SignalSelectionType#CHANNEL channel}
	 * style</li>
	 */
	private JPanel buttonPanel;

	/**
	 * the {@link KeyStrokeCaptureDialog dialog} which captures the key stroke
	 * for the style
	 */
	private KeyStrokeCaptureDialog keyStrokeCaptureDialog = null;

	/**
	 * the {@link StyledTagSet set} of tag styles that is currently used
	 */
	protected StyledTagSet currentTagSet;
	/**
	 * the currently selected {@link TagStyle style}
	 */
	private TagStyle currentStyle;

	/**
	 * {@code true} if the current {@link TagStyle style} was changed or
	 * if the style was added/removed,
	 * {@code false} otherwise
	 */
	private boolean changed = false;

	/**
	 * Constructor. Sets message source.
	 */
	public TagStylePaletteDialog( PresetManager presetManager) {
		super( presetManager);
	}

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public TagStylePaletteDialog( PresetManager presetManager, Window w, boolean isModal) {
		super( presetManager, w, isModal);
	}

	/**
	 * Initializes this panel:
	 * <ul>
	 * <li>sets the title and the icon,</li>
	 * <li>calls the {@link AbstractDialog#initialize() initialization} in
	 * {@link AbstractDialog parent},</li>
	 * <li>adds the listener to the {@link TagStyleTree tree} with {@link
	 * TagStyle tag styles}, which is called when the selection is changed and:
	 * <ul>
	 * <li>checks if the last selected style has not been edited - if yes
	 * {@link OptionPane#showTagStyleModified(java.awt.Component) asks} the
	 * user what to do,</li>
	 * <li>changes the current style in the {@link TagStylePropertiesPanel
	 * properties panel}.</li></ul></li></ul>
	 */
	@Override
	protected void initialize() {
		setTitle(_("Tag style palette"));
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

	/**
	 * Creates the control pane.
	 * Adds OK button, the CANCEL button and the button which calls the
	 * {@link ApplyChangesActionAction action} which applies changes.
	 */
	@Override
	protected JPanel createButtonPane() {

		JPanel buttonPane =  super.createButtonPane();

		applyChangesActionAction = new ApplyChangesActionAction();
		applyChangesButton = new JButton(applyChangesActionAction);
		buttonPane.add(Box.createHorizontalStrut(5), 1);
		buttonPane.add(applyChangesButton, 1);

		return buttonPane;

	}

	/**
	 * Creates the interface for this dialog.
	 * This dialog contains two panels:
	 * <ul>
	 * <li>on the left - the panel with the {@link TagStyleTree tree} with
	 * {@link TagStyle tag styles} and buttons to add/remove styles,</li>
	 * <li>on the right - the {@link TagStylePropertiesPanel panel} with
	 * the properties of the selected style.</li>
	 * </ul>
	 */
	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		tagStylePropertiesPanel = new TagStylePropertiesPanel();

		tagStylePropertiesPanel.getCaptureKeyButton().setAction(new CaptureKeyStrokeAction());

		interfacePanel.add(getTagTreePanel(), BorderLayout.WEST);
		interfacePanel.add(tagStylePropertiesPanel, BorderLayout.CENTER);

		return interfacePanel;

	}

	/**
	 * Returns the {@link TagStyleTreeModel model} for the {@link
	 * #getTagStyleTree() tree} with {@link TagStyle tag styles}.
	 * @return the model for the tree with tag styles
	 */
	private TagStyleTreeModel getTagStyleTreeModel() {
		if (tagStyleTreeModel == null) {
			tagStyleTreeModel = new TagStyleTreeModel();
		}
		return tagStyleTreeModel;
	}

	/**
	 * Returns the {@link TagStyleTree tree} with {@link TagStyle tag styles}.
	 * If the tree doesn't exist it is created.
	 * @return the tree with tag styles
	 */
	private TagStyleTree getTagStyleTree() {
		if (tagStyleTree == null) {
			tagStyleTree = new TagStyleTree(getTagStyleTreeModel());
		}
		return tagStyleTree;
	}

	/**
	 * Returns the panel with the {@link #getTagStyleTree() tree} of {@link
	 * TagStyle tag styles}.
	 * If the panel doesn't exist it is created.
	 * @return the panel with the tree of tag styles
	 */
	private JScrollPane getTreeScrollPane() {
		if (treeScrollPane == null) {
			treeScrollPane = new JScrollPane(getTagStyleTree());
			treeScrollPane.setPreferredSize(new Dimension(200,400));
		}
		return treeScrollPane;
	}

	/**
	 * Returns the left panel in this dialog.
	 * The panel contains two elements:
	 * <ul>
	 * <li>the {@link #getTreeScrollPane() panel} with the {@link TagStyleTree
	 * tree} of {@link TagStyle tag styles},</li>
	 * <li>the {@link #getButtonPanel() panel} with control buttons.</li></ul>
	 * If the panel doesn't exist it is created.
	 * @return the left panel
	 */
	private JPanel getTagTreePanel() {
		if (tagTreePanel == null) {

			tagTreePanel = new JPanel(new BorderLayout());
			tagTreePanel.setBorder(new TitledBorder(_("Current styles")));
			tagTreePanel.add(getTreeScrollPane(), BorderLayout.CENTER);

			tagTreePanel.add(getButtonPanel(), BorderLayout.SOUTH);

		}
		return tagTreePanel;
	}

	/**
	 * Returns the {@link AddPageStyleAction action} which adds a new
	 * {@link SignalSelectionType#PAGE page} {@link TagStyle style}.
	 * If the action doesn't exist it is created.
	 * @return the action which adds a new page style
	 */
	private AddPageStyleAction getAddPageStyleAction() {
		if (addPageStyleAction == null) {
			addPageStyleAction = new AddPageStyleAction();
		}
		return addPageStyleAction;
	}

	/**
	 * Returns the {@link AddBlockStyleAction action} which adds a new
	 * {@link SignalSelectionType#BLOCK block} {@link TagStyle style}.
	 * If the action doesn't exist it is created.
	 * @return the action which adds a new block style
	 */
	private AddBlockStyleAction getAddBlockStyleAction() {
		if (addBlockStyleAction == null) {
			addBlockStyleAction = new AddBlockStyleAction();
		}
		return addBlockStyleAction;
	}

	/**
	 * Returns the {@link AddChannelStyleAction action} which adds a new
	 * {@link SignalSelectionType#CHANNEL channel} {@link TagStyle style}.
	 * If the action doesn't exist it is created.
	 * @return the action which adds a new channel style
	 */
	private AddChannelStyleAction getAddChannelStyleAction() {
		if (addChannelStyleAction == null) {
			addChannelStyleAction = new AddChannelStyleAction();
		}
		return addChannelStyleAction;
	}

	/**
	 * Returns the {@link RemoveStyleAction action} which removes the
	 * currently selected {@link TagStyle style}.
	 * If the action doesn't exist it is created.
	 * @return the action which removes the currently selected style
	 */
	private RemoveStyleAction getRemoveStyleAction() {
		if (removeStyleAction == null) {
			removeStyleAction = new RemoveStyleAction();
		}
		return removeStyleAction;
	}

	/**
	 * Returns the button which calls the {@link #getAddChannelStyleAction()
	 * action} which adds a new {@link SignalSelectionType#PAGE page}
	 * {@link TagStyle style}.
	 * If the button doesn't exist it is created.
	 * @return the button which calls the action which adds a new page style
	 */
	private JButton getAddPageStyleButton() {
		if (addPageStyleButton == null) {
			addPageStyleButton = new JButton(getAddPageStyleAction());
		}
		return addPageStyleButton;
	}

	/**
	 * Returns the button which calls the {@link #getAddChannelStyleAction()
	 * action} which adds a new {@link SignalSelectionType#BLOCK block}
	 * {@link TagStyle style}.
	 * If the button doesn't exist it is created.
	 * @return the button which calls the action which adds a new block style
	 */
	private JButton getAddBlockStyleButton() {
		if (addBlockStyleButton == null) {
			addBlockStyleButton = new JButton(getAddBlockStyleAction());
		}
		return addBlockStyleButton;
	}

	/**
	 * Returns the button which calls the {@link #getAddChannelStyleAction()
	 * action} which adds a new {@link SignalSelectionType#CHANNEL channel}
	 * {@link TagStyle style}.
	 * If the button doesn't exist it is created.
	 * @return the button which calls the action which adds a new channel style
	 */
	private JButton getAddChannelStyleButton() {
		if (addChannelStyleButton == null) {
			addChannelStyleButton = new JButton(getAddChannelStyleAction());
		}
		return addChannelStyleButton;
	}

	/**
	 * Returns the button which calls the {@link #getRemoveStyleAction() action}
	 * which removes the currently selected {@link TagStyle style}.
	 * If the button doesn't exist it is created.
	 * @return the button which calls the action which removes the currently
	 * selected style
	 */
	private JButton getRemoveStyleButton() {
		if (removeStyleButton == null) {
			removeStyleButton = new JButton(getRemoveStyleAction());
		}
		return removeStyleButton;
	}

	/**
	 * Returns the panel with buttons:
	 * <ul>
	 * <li>the button which removes the
	 * selected {@link TagStyle style},</li>
	 * <li>the button which adds a {@link SignalSelectionType#PAGE page} style,
	 * </li>
	 * <li>the button which adds a {@link SignalSelectionType#BLOCK block}
	 * style,</li>
	 * <li>the button which adds a {@link SignalSelectionType#CHANNEL channel}
	 * style,</li>
	 * @return the panel with buttons
	 */
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

	/**
	 * Fills the fields of this dialog:
	 * <ul>
	 * <li>sets the {@link TagStylePaletteDescriptor#getTagSet() tag set} from
	 * {@link TagStylePaletteDescriptor model} in the {@link TagStyleTreeModel
	 * tree model},</li>
	 * <li>sets the {@link TagStylePaletteDescriptor#getStyle() initial style}
	 * as the current style.</li></ul>
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		TagStylePaletteDescriptor descriptor = (TagStylePaletteDescriptor) model;

		currentTagSet = descriptor.getTagSet();

		if (currentTagSet == null)
			currentTagSet = new StyledTagSet();

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

	/**
	 * Stores the changes in the tag style.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		applyChanges();

		TagStylePaletteDescriptor descriptor = (TagStylePaletteDescriptor) model;
		descriptor.setChanged(changed);

	}

	/**
	 * Validates this dialog.
	 * This dialog is valid if the name and the key stroke are unique.
	 */
	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		if (tagStylePropertiesPanel.isChanged()) {
			Errors styleErrors = validateChanges();
			errors.addAllErrors(styleErrors);
		}
	}

	/**
	 * Sets that there is no {@link StyledTagSet tag set} in the
	 * {@link TagStyleTreeModel tree model}.
	 */
	@Override
	protected void onDialogClose() {
		tagStyleTreeModel.setTagSet(null);
	}

	/**
	 * The model for this dialog must be of type {@link
	 * TagStylePaletteDescriptor}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return TagStylePaletteDescriptor.class.isAssignableFrom(clazz);
	}

	/**
	 * Adds the given {@link TagStyle style} to the set and sets it as the
	 * current style in the {@link #getTagStyleTree() tree} and in the
	 * {@link #tagStylePropertiesPanel}.
	 * @param newStyle the style to be added
	 */
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

	/**
	 * Creates and returns the new {@link TagStyle tag style} of the given
	 * {@link SignalSelectionType type} with the default name and color.
	 * @param type the type of the style
	 * @return the created style
	 */
	private TagStyle getNewStyle(SignalSelectionType type) {
		String name = _("New");
		int cnt = 2;
		while (currentTagSet.getStyle(name) != null) {
			name = _("New") + " (" + cnt + ")";
			cnt++;
		}
		return new TagStyle(type, name, null, Color.GREEN, Color.GREEN, 1, null, null, false);
	}

	/**
	 * Validates the changes in this dialog.
	 * The changes are valid if the name and the key stroke are unique.
	 * @return the object in which errors are stored
	 */
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

	/**
	 * Stores the changes (user input) in the {@link TagStyle tag style}.
	 */
	protected void applyChanges() {

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

	/**
	 * {@link #validateChanges() Validates} changes and, if they are valid,
	 * {@link #applyChanges() applies} them.
	 * @return {@code true} if the changes were valid, {@code false} otherwise
	 */
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

	/**
	 * If there were unsaved changes {@link OptionPane#showTagStyleModified(
	 * java.awt.Component) shows} the dialog and asks the user what to do.
	 * If the user answers:
	 * <ul>
	 * <li>to save them - validates and saves them and returns {@code true},
	 * </li>
	 * <li>to discard them - sets that there were no changes and returns
	 * {@code true},</li>
	 * <li>otherwise - returns {@code false}.</li>
	 * @return {@code true} if there were no changes or the user chose to
	 * save or discard the changes, {@code false} otherwise
	 */
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

	/**
	 * Sets that no change has occurred.
	 */
	@Override
	protected void resetDialog() {
		changed = false;
	}

	@Override
	public Preset getPreset() throws SignalMLException {
		applyChanges();
		LinkedHashMap<String, TagStyle> stylesWithNames = (LinkedHashMap<String, TagStyle>) currentTagSet.getStylesWithNames().clone();

		StyledTagSet sts = new StyledTagSet(stylesWithNames);
		return sts;
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		StyledTagSet newStyles = ((StyledTagSet) preset).clone();

		List<String> stylesThatCouldNotBeDeleted = currentTagSet.copyStylesFrom(newStyles);

		if (stylesThatCouldNotBeDeleted.size() > 0) {

			String styles = "";
			for (int i = 0; i < stylesThatCouldNotBeDeleted.size(); i++) {
				styles += stylesThatCouldNotBeDeleted.get(i);
				if (i < stylesThatCouldNotBeDeleted.size() - 1) {
					styles += ", ";
				}
			}

			ErrorsDialog errorsDialog = new ErrorsDialog( this, true);
			MessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{"tagStylePalette.preset.stylesCouldNotBeDeleted"}, new Object[]{styles});
			errorsDialog.showDialog(messageSourceResolvable, true);
		}

		currentStyle = null;

		tagStyleTreeModel.setTagSet(null);

		TagStylePaletteDescriptor descriptor = new TagStylePaletteDescriptor(currentTagSet, null);
		fillDialogFromModel(descriptor);

	}

	/**
	 * Action which adds a {@link SignalSelectionType#PAGE PAGE} {@link TagStyle
	 * style}.
	 */
	protected class AddPageStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tooltip.
		 */
		public AddPageStyleAction() {
			super(_("New page style"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/pagetag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Add new page style"));
		}

		/**
		 * When this action is performed the {@link SignalSelectionType#PAGE
		 * PAGE} {@link TagStyle style} is added and the boolean is set that
		 * the change has occurred.
		 */
		public void actionPerformed(ActionEvent ev) {

			addNewStyle(getNewStyle(SignalSelectionType.PAGE));
			changed = true;

		}

	}

	/**
	 * Action which adds a {@link SignalSelectionType#BLOCK BLOCK} {@link
	 * TagStyle style}.
	 */
	protected class AddBlockStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tooltip.
		 */
		public AddBlockStyleAction() {
			super(_("New block style"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/blocktag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Add new block style"));
		}

		/**
		 * When this action is performed the {@link SignalSelectionType#BLOCK
		 * BLOCK} {@link TagStyle style} is added and the boolean is set that
		 * the change has occurred.
		 */
		public void actionPerformed(ActionEvent ev) {

			addNewStyle(getNewStyle(SignalSelectionType.BLOCK));
			changed = true;

		}

	}

	/**
	 * Action which adds a {@link SignalSelectionType#CHANNEL CHANNEL} {@link
	 * TagStyle style}.
	 */
	protected class AddChannelStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip.
		 */
		public AddChannelStyleAction() {
			super(_("New channel style"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/channeltag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Add new channel style"));
		}

		/**
		 * When this action is performed the {@link SignalSelectionType#CHANNEL
		 * CHANNEL} {@link TagStyle style} is added and the boolean is set that
		 * the change has occurred.
		 */
		public void actionPerformed(ActionEvent ev) {

			addNewStyle(getNewStyle(SignalSelectionType.CHANNEL));
			changed = true;

		}

	}

	/**
	 * Action which removes the currently selected {@link TagStyle style}.
	 * <p>
	 * If the style is used (there exist {@link Tag tags} with that style)
	 * appropriate information is shown and no action is taken.
	 * Otherwise removes that style and sets that there is no active style.
	 */
	protected class RemoveStyleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip.
		 */
		public RemoveStyleAction() {
			super(_("Remove style"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removetag.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Remove selected style"));
		}

		/**
		 * If the style is used (there exist {@link Tag tags} with that style)
		 * appropriate information is shown and no action is taken.
		 * Otherwise removes that style and sets that there is no active style.
		 */
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

		/**
		 * If the last component on the given path is of type {@link TagStyle}
		 * enables this action, otherwise disables it.
		 * @param selection the path
		 */
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

	/**
	 * Action which validates and applies the changes (user input).
	 */
	protected class ApplyChangesActionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip.
		 */
		public ApplyChangesActionAction() {
			super(_("Apply changes"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/apply.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Apply changes to the tag style"));
		}

		/**
		 * When the action is performed validates and applies the changes
		 * (user input).
		 */
		public void actionPerformed(ActionEvent ev) {

			validateAndApplyChanges();

		}

	}

	/**
	 * Action which displays the {@link KeyStrokeCaptureDialog dialog} which
	 * captures the key stroke which activates this {@link TagStyle tag style}.
	 * Displays the captured key stroke in the text field.
	 */
	protected class CaptureKeyStrokeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip.
		 */
		public CaptureKeyStrokeAction() {
			super(_("Capture key"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/keyboard.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Change the key stroke associated with this style"));
		}

		/**
		 * Then this action is performed displays the {@link
		 * KeyStrokeCaptureDialog dialog} which captures the key stroke which
		 * activates this {@link TagStyle tag style}.
		 * Displays the captured key stroke in the text field.
		 */
		public void actionPerformed(ActionEvent ev) {

			if (keyStrokeCaptureDialog == null) {
				keyStrokeCaptureDialog = new KeyStrokeCaptureDialog( TagStylePaletteDialog.this);
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
