package org.signalml.app.view.common.components.presets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.AnyChangeDocumentAdapter;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.common.dialogs.AbstractPresetDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

public class CompactPresetControlsPanel extends AbstractPanel {

	public static String CHOOSE_PRESET_OPTION = _("<< select to load preset >>");

	/**
	 * The dialog/panel which is controlled by this preset control panel
	 * (e.g. the presets selected in this PresetControlsPanel affects
	 * the presetPanel).
	 */
	protected PresetableView presetPanel;
	/**
	 * the {@link PresetManager manager} of {@link Preset presets}
	 */
	protected PresetManager presetManager;

	/**
	 * the model for {@link #presetComboBox}
	 */
	private PresetComboBoxModel presetComboBoxModel;

	/**
	 * the combo box which allows to select the {@link Preset preset}
	 */
	private JComboBox presetComboBox;

	protected JButton savePresetButton;
	protected JButton removePresetButton;

	/**
	 * the dialog that allows to select the {@link #getPresetComboBox preset}
	 * and specify the name for it
	 */
	private ChoosePresetDialog choosePresetDialog;

	public CompactPresetControlsPanel(PresetManager presetManager, PresetableView presetPanel) {
		super();
		this.presetManager = presetManager;
		this.presetPanel = presetPanel;

		createInterface();
	}

	protected void createInterface() {
		setBorder(new TitledBorder("Preset"));

		add(getPresetComboBox());
		add(getSavePresetButton());
		add(getRemovePresetButton());
	}
	
	public JComboBox getPresetComboBox() {
		if (presetComboBox == null) {
			presetComboBox = new JComboBox(getPresetComboBoxModel());
			presetComboBox.setPreferredSize(new Dimension(340, 26));

			presetComboBox.addActionListener(new LoadPresetAction());
			resetPresetComboBoxSelection();
		}
		return presetComboBox;
	}

	public PresetComboBoxModel getPresetComboBoxModel() {
		if (presetComboBoxModel == null) {
			presetComboBoxModel = new PresetComboBoxModel(CHOOSE_PRESET_OPTION, presetManager);
		}
		return presetComboBoxModel;
	}

	public JButton getSavePresetButton() {
		if (savePresetButton == null) {
			savePresetButton = new JButton(new SavePresetAction());
		}
		return savePresetButton;
	}

	public JButton getRemovePresetButton() {
		if (removePresetButton == null)
			removePresetButton = new JButton(new RemovePresetAction());
		return removePresetButton;
	}

	public ChoosePresetDialog getChoosePresetDialog() {
		if (choosePresetDialog == null)
			choosePresetDialog = new ChoosePresetDialog();
		return choosePresetDialog;
	}

	/**
	 * Returns the {@link ApplicationConfiguration configuration} of Svarog.
	 * @return the configuration of Svarog
	 */
	public ApplicationConfiguration getApplicationConfig() {
		return SvarogApplication.getApplicationConfiguration();
	}

	/**
	 * (Creates and) returns the current {@link Preset preset}.
	 * Must be specified in the implementing class.
	 * @return the current preset
	 * @throws SignalMLException TODO never thrown in implementations (???)
	 */
	public Preset getPresetFromMainPanel() throws SignalMLException {
		return presetPanel.getPreset();
	}

	/**
	 * Sets the given preset as the current {@link Preset preset}.
	 * Fills all necessary fields of the dialog with the data from this preset.
	 * Must be specified in the implementing class.
	 * @param preset the preset to use as the new current preset.
	 * @throws SignalMLException TODO never thrown in implementations
	 */
	private void setPresetToMainPanel(Preset preset) throws SignalMLException {
		if (presetPanel.isPresetCompatible(preset))
			presetPanel.setPreset(preset);
		else
			resetPresetComboBoxSelection();
	}

	public void resetPresetComboBoxSelection() {
		getPresetComboBox().setSelectedIndex(0);
	}

	/**
	 * Action that saves the {@link AbstractPresetDialog#getPreset() current
	 * preset} to the list of presets.
	 */
	protected class SavePresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public SavePresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/preset_save.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Save"));
		}

		/**
		 * Called when this action is performed:
		 * <ul>
		 * <li>{@link AbstractPresetDialog#getPreset() gets} the current
		 * {@link Preset preset} from the dialog,</li>
		 * <li>{@link ChoosePresetDialog#getName(String, boolean) gets} the
		 * name for this preset,</li>
		 * <li>if the preset of such name exists asks if it should be replaced,
		 * </li>
		 * <li>saves the preset to the {@link PresetManager}.</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			Preset preset;
			try {
				preset = getPresetFromMainPanel();
			} catch (SignalMLException ex) {
				logger.error("Failed to get preset", ex);
				Dialogs.showExceptionDialog(CompactPresetControlsPanel.this, ex);
				return;
			}
			if (preset == null) {
				return;
			}
			if (!presetManager.getPresetClass().isAssignableFrom(preset.getClass())) {
				throw new ClassCastException("Bad preset class");
			}

			String newName = getChoosePresetDialog().getName(preset.getName(), true);
			if (newName == null) {
				return;
			}

			Preset existingPreset = presetManager.getPresetByName(newName);
			if (existingPreset != null) {
				final String msg = _("Preset already exists, do you really want to overwrite this preset?");
				if (Dialogs.showWarningYesNoDialog(msg) == Dialogs.DIALOG_OPTIONS.NO) {
					return;
				}
			}

			preset.setName(newName);

			presetManager.setPreset(preset);
			presetComboBoxModel.setSelectedItem(preset);

			if (getApplicationConfig().isSaveConfigOnEveryChange()) {
				try {
					presetManager.writeToPersistence(null);
				} catch (IOException ex) {
					logger.error("Failed to save preset configuration", ex);
				}
			}


		}

	}

	/**
	 * Action that {@link AbstractPresetDialog#setPreset(Preset) sets}
	 * the selected {@link Preset preset} as active.
	 */
	protected class LoadPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public LoadPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/preset_load.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Load"));
		}

		/**
		 * Called when the action is performed:
		 * <ul>
		 * <li>gets the selected {@link Preset preset} or if there is no this
		 * functions ends,
		 * </li>
		 * <li>if there were changes in the dialog or dialog doesn't know if
		 * there were, shows the warning to the user that the current preset
		 * will be overridden and allows him to cancel the operation,</li>
		 * <li>{@link AbstractPresetDialog#setPreset(Preset) sets} this preset
		 * as the active one (fills the dialog with the data from it).</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			int index = getPresetComboBox().getSelectedIndex();
			if (index <= 0) {
				return;
			}

			Preset preset = presetManager.getPresetAt(index-1);

			getPresetComboBox().repaint();

			if (preset == null) {
				return;
			}

			try {
				setPresetToMainPanel(preset);
			} catch (SignalMLException ex) {
				logger.error("Failed to set preset", ex);
				Dialogs.showExceptionDialog(CompactPresetControlsPanel.this, ex);
				return;
			}

		}

	}


	/**
	 * Action that removes the selected preset.
	 * If there is no selected preset does nothing.
	 * If there is warns the user before the removal.
	 */
	protected class RemovePresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public RemovePresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/preset_remove.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Remove"));
		}

		/**
		 * Called when the action is performed:
		 * <ul>
		 * <li>gets the selected {@link Preset preset} or if there is no this
		 * functions ends,
		 * </li>
		 * <li>warns the user and if the user cancels operation this function
		 * ends,</li>
		 * <li>removes the selected preset.</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			String name = getChoosePresetDialog().getName(null, false);
			if (name == null) {
				return;
			}

			Preset preset = presetManager.getPresetByName(name);
			if (preset == null) {
				return;
			}

			presetManager.removePresetByName(name);

			if (getApplicationConfig().isSaveConfigOnEveryChange()) {
				try {
					presetManager.writeToPersistence(null);
				} catch (IOException ex) {
					logger.error("Failed to save preset configuration", ex);
				}
			}

		}

	}


	/**
	 * Dialog that allows to select the {@link #getPresetComboBox preset}
	 * and specify the name for it.
	 */
	protected class ChoosePresetDialog extends AbstractDialog {

		private static final long serialVersionUID = 1L;

		/**
		 * the model for a combo box that allows to select the preset
		 */
		private PresetComboBoxModel presetComboBoxModel;

		/**
		 * the combo box that allows to select the preset
		 */
		protected JComboBox presetComboBox;

		/**
		 * the text field to specify the name for the preset
		 */
		private JTextField nameTextField;

		protected boolean editable = true;

		/**
		 * Constructor. Sets the message source from enclosing class and
		 * uses the enclosing class as the parent window to this dialog.
		 */
		protected ChoosePresetDialog() {
			super(CompactPresetControlsPanel.this.getParentWindow(), true);
		}

		/**
		 * Returns  if the text field with the name of the preset should be
		 * editable.
		 * @return {@code true} if the text field with the name of the
		 * preset should be editable, {@code false} otherwise
		 */
		public boolean isEditable() {
			return editable;
		}

		/**
		 * Sets if the text field with the name of the preset should be
		 * editable.
		 * @param editable {@code true} if the text field with the name of the
		 * preset should be editable, {@code false} otherwise
		 */
		public void setEditable(boolean editable) {
			if (this.editable != editable) {
				this.editable = editable;
				getNameTextField().setEditable(editable);
			}
		}

		@Override
		public void fillDialogFromModel(Object model) {
			// do nothing
		}

		@Override
		public void fillModelFromDialog(Object model) {
			// do nothing
		}

		@Override
		protected void initialize() {
			setTitle(_("Select preset"));
			super.initialize();
		}

		/**
		 * Adds the panel with 3 elements:
		 * <ul>
		 * <li>icon with a question mark,</li>
		 * <li>{@link #getPresetComboBox() combo box} to select the preset,</li>
		 * <li>{@link #getNameTextField() text field} with the name of the
		 * preset (may be editable or not).</li>
		 * </ul>
		 */
		@Override
		public JComponent createInterface() {

			JPanel interfacePanel = new JPanel(new BorderLayout());
			interfacePanel.setBorder(new CompoundBorder(
										 new TitledBorder(_("Select preset name")),
										 new EmptyBorder(3,3,3,3)
									 ));

			JPanel inputPanel = new JPanel();
			inputPanel.setBorder(new EmptyBorder(0,8,0,0));
			inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

			inputPanel.add(getPresetComboBox());
			inputPanel.add(Box.createVerticalStrut(10));
			inputPanel.add(getNameTextField());

			JLabel iconLabel = new JLabel(IconUtils.getQuestionIcon());
			iconLabel.setVerticalAlignment(JLabel.TOP);
			interfacePanel.add(iconLabel, BorderLayout.WEST);
			interfacePanel.add(inputPanel, BorderLayout.CENTER);

			return interfacePanel;

		}

		/**
		 * Returns the {@link PresetComboBoxModel model} for a combo box
		 * to select presets.
		 * If the model doesn't exist it is created.
		 * @return the model
		 */
		public PresetComboBoxModel getPresetComboBoxModel() {
			if (presetComboBoxModel == null) {
				presetComboBoxModel = new PresetComboBoxModel(_("<< select to choose preset >>"), presetManager);
			}
			return presetComboBoxModel;
		}

		/**
		 * If the preset combo box already exists it is simply returned.
		 * If it doesn't, it is created.
		 * Created combo box contains the listener, which sets the
		 * {@link #getNameTextField() name field} depending on the selected
		 * preset.
		 * @return the preset combo box
		 */
		public JComboBox getPresetComboBox() {

			if (presetComboBox == null) {

				;
				presetComboBox = new JComboBox(getPresetComboBoxModel());
				presetComboBox.setSelectedIndex(0);
				presetComboBox.setPreferredSize(new Dimension(200,25));

				presetComboBox.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						int index = presetComboBox.getSelectedIndex();
						if (index <= 0) {
							return;
						}

						Preset p = presetManager.getPresetAt(index-1);
						if (p != null) {
							JTextField nameTextField = getNameTextField();
							nameTextField.setText(p.getName());
							if (editable) {
								nameTextField.selectAll();
								nameTextField.requestFocusInWindow();
							}
						}

						presetComboBox.setSelectedIndex(0);
						presetComboBox.repaint();

					}

				});

			}

			return presetComboBox;

		}

		/**
		 * Returns the text field with the name of the preset.
		 * If the field doesn't exist it is created.
		 * If this name contains at least one character the OK button is
		 * activated.
		 * @return the text field with the name of the preset.
		 */
		public JTextField getNameTextField() {
			if (nameTextField == null) {
				nameTextField = new JTextField();
				nameTextField.setPreferredSize(new Dimension(200,25));

				nameTextField.getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {
					@Override
					public void anyUpdate(DocumentEvent e) {
						getOkAction().setEnabled(e.getDocument().getLength() > 0);
					}
				});

			}
			return nameTextField;
		}

		/**
		 * Shows this dialog and returns the entered name.
		 * @param initialName the name that (if it is not empty) will be
		 * set as the value of {@link #getNameTextField() name text field}
		 * @param editable {@code true} if the name text field should be
		 * editable, {@code false} otherwise
		 * @return the specified name or null if there is no name (or the name
		 * is empty)
		 */
		public String getName(String initialName, boolean editable) {

			initializeNow();

			setEditable(editable);

			JTextField nameTextField = getNameTextField();

			if (initialName != null && !initialName.isEmpty()) {
				nameTextField.setText(initialName);
				getOkAction().setEnabled(true);
			} else {
				nameTextField.setText("");
				getOkAction().setEnabled(false);
			}

			if (editable) {
				nameTextField.selectAll();
				nameTextField.requestFocusInWindow();
			}

			boolean ok = showDialog(null, true);
			if (!ok) {
				return null;
			}

			String name = nameTextField.getText();
			name = Util.trimString(name);
			if (name == null || name.isEmpty()) {
				return null;
			}

			return name;

		}

		@Override
		public boolean supportsModelClass(Class<?> clazz) {
			return (clazz == null);
		}

	}
}
