/* AbstractPresetDialog.java created 2007-10-24
 *
 */

package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.config.preset.PresetManagerEvent;
import org.signalml.app.config.preset.PresetManagerListener;
import org.signalml.app.model.SeriousWarningDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.AnyChangeDocumentAdapter;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;
import static org.signalml.app.SvarogApplication._;
import static org.signalml.app.SvarogApplication._R;

/**
 * Dialog which data can be stored in a {@link Preset preset}.
 * Contains the panel that allows management of presets:
 * <ul>
 * <li>open a preset from file and save it to file,</li>
 * <li>set the current preset as default and load it from default,</li>
 * <li>remove the default preset,</li>
 * <li>save the current preset in {@link PresetManager} and get a preset from
 * there.</li>
 * </ul>
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractPresetDialog extends AbstractSvarogDialog {

	static final long serialVersionUID = 1L;

	/**
	 * the panel with OK and CANCEL button
	 */
	protected JPanel buttonPane;

	/**
	 * the {@link PresetManager manager} of {@link Preset presets}
	 */
	protected PresetManager presetManager;
	
	/**
	 * the {@link ViewerFileChooser file chooser}
	 */
	protected ViewerFileChooser fileChooser;
	
	/**
	 * the {@link ApplicationConfiguration configuration} of Svarog
	 */
	protected ApplicationConfiguration applicationConfig;

	/**
	 * the model for {@link #presetComboBox}
	 */
	private PresetComboBoxModel presetComboBoxModel;
	
	/**
	 * the combo box which allows to select the {@link Preset preset}
	 */
	private JComboBox presetComboBox;

	/**
	 * the dialog that allows to select the {@link #getPresetComboBox preset}
	 * and specify the name for it
	 */
	private ChoosePresetDialog choosePresetDialog;
	
	/**
	 * the {@link SeriousWarningDialog dialog} which displays the
	 * warning to the user and allows him to decide if the operation should
	 * be terminated
	 */
	private SeriousWarningDialog seriousWarningDialog;

	/**
	 * @see LoadDefaultPresetAction
	 */
	private Action loadDefaultPresetAction;
	
	/**
	 * @see SaveDefaultPresetAction
	 */
	private Action saveDefaultPresetAction;
	
	/**
	 * @see RemoveDefaultPresetAction
	 */
	private Action removeDefaultPresetAction;

	/**
	 * @see SavePresetAction
	 */
	private Action savePresetAction;
	
	/**
	 * @see LoadPresetAction
	 */
	private Action loadPresetAction;
	
	/**
	 * @see RemovePresetAction
	 */
	private Action removePresetAction;

	/**
	 * @see SaveFileAction
	 */
	private Action saveFileAction;
	
	/**
	 * @see LoadFileAction
	 */
	private Action loadFileAction;

	/**
	 * the button for {@link #loadDefaultPresetAction}
	 */
	private JButton loadDefaultPresetButton;
	
	/**
	 * the button for {@link #saveDefaultPresetAction}
	 */
	private JButton saveDefaultPresetButton;
	
	/**
	 * the button for {@link #removeDefaultPresetAction}
	 */
	private JButton removeDefaultPresetButton;

	/**
	 * the button for {@link #savePresetAction}
	 */
	private JButton savePresetButton;
	/**
	 * the button for {@link #removePresetAction}
	 */
	private JButton removePresetButton;

	/**
	 * the button for {@link #saveFileAction}
	 */
	private JButton saveFileButton;
	/**
	 * the button for {@link #loadFileAction}
	 */
	private JButton loadFileButton;

	/**
	 * boolean if the fields of this dialog have been changed
	 */
	private boolean changed = false;

	/**
	 * @see #createPresetPane()
	 */
	protected JPanel presetPane;

	/**
	 * Constructor. Sets message source and the {@link PresetManager preset
	 * manager}.
	 * @param presetManager the preset manager to set
	 */
	public AbstractPresetDialog(PresetManager presetManager) {
		super();
		this.presetManager = presetManager;
	}

	/**
	 * Constructor. Sets message source, {@link PresetManager preset
	 * manager}, parent window and if this dialog blocks top-level windows.
	 * @param presetManager the preset manager to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public AbstractPresetDialog(PresetManager presetManager, Window w, boolean isModal) {
		super(w, isModal);
		this.presetManager = presetManager;
	}

	@Override
	protected void initialize() {
		super.initialize();
		presetManager.addPresetManagerListener(new PresetManagerChangeListener());
	}

	/**
	 * Creates the panel with OK and CANCEL button.
	 * @see AbstractDialog#createControlPane()
	 * @return the created panel
	 */
	protected JPanel createButtonPane() {
		return super.createControlPane();
	}

	/**
	 * Creates the panel located in the {@link #createControlPane() control
	 * panel}.
	 * Contains (from left to right):
	 * <ul>
	 * <li>{@link #getPresetComboBox() preset combo box},</li>
	 * <li>button to save the current preset,</li>
	 * <li>button to remove preset selected in preset combo box,</li>
	 * <li>button to load default preset if it {@link #showLoadDefaultButton()
	 * should} be displayed,</li>
	 * <li>button to set the current preset as the default one if it
	 * {@link #showSaveDefaultButton() should} be displayed},</li>
	 * <li>button to remove the default preset if it
	 * {@link #showRemoveDefaultButton() should} be displayed,</li>
	 * <li>button to load preset from file,</li>
	 * <li>button to save the preset to file.</li>
	 * </ul>
	 * @return the created panel
	 */
	protected JPanel createPresetPane() {

		JPanel presetPane = new JPanel();
		presetPane.setLayout(new BoxLayout(presetPane, BoxLayout.X_AXIS));
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Presets")),
			new EmptyBorder(3,3,3,3)
		);
		presetPane.setBorder(border);

		loadDefaultPresetAction = new LoadDefaultPresetAction();
		saveDefaultPresetAction = new SaveDefaultPresetAction();
		removeDefaultPresetAction = new RemoveDefaultPresetAction();

		savePresetAction = new SavePresetAction();
		loadPresetAction = new LoadPresetAction();
		removePresetAction = new RemovePresetAction();
		saveFileAction = new SaveFileAction();
		loadFileAction = new LoadFileAction();

		savePresetButton = new JButton(savePresetAction);
		removePresetButton = new JButton(removePresetAction);
		saveFileButton = new JButton(saveFileAction);
		loadFileButton = new JButton(loadFileAction);

		presetPane.add(getPresetComboBox());
		presetPane.add(Box.createHorizontalStrut(3));
		presetPane.add(savePresetButton);
		presetPane.add(Box.createHorizontalStrut(3));
		presetPane.add(removePresetButton);
		presetPane.add(Box.createHorizontalStrut(6));
		presetPane.add(Box.createHorizontalGlue());

		boolean extraSpacerNeeded = false;

		if (showLoadDefaultButton()) {
			loadDefaultPresetButton = new JButton(loadDefaultPresetAction);
			presetPane.add(loadDefaultPresetButton);
			presetPane.add(Box.createHorizontalStrut(3));
			extraSpacerNeeded = true;
		}

		if (showSaveDefaultButton()) {
			saveDefaultPresetButton = new JButton(saveDefaultPresetAction);
			presetPane.add(saveDefaultPresetButton);
			presetPane.add(Box.createHorizontalStrut(3));
			extraSpacerNeeded = true;
		}

		if (showRemoveDefaultButton()) {
			removeDefaultPresetButton = new JButton(removeDefaultPresetAction);
			presetPane.add(removeDefaultPresetButton);
			presetPane.add(Box.createHorizontalStrut(3));
			extraSpacerNeeded = true;
		}

		if (extraSpacerNeeded) {
			presetPane.add(Box.createHorizontalStrut(3));
			presetPane.add(Box.createHorizontalGlue());
		}

		presetPane.add(loadFileButton);
		presetPane.add(Box.createHorizontalStrut(3));
		presetPane.add(saveFileButton);

		return presetPane;

	}

	/**
	 * Creates the control panel, which contains two sub-panels (from top to
	 * bottom):
	 * <ul>
	 * <li>the {@link #createButtonPane() button panel},</li>
	 * <li>the {@link #createPresetPane() preset panel}.</li>
	 * </ul>
	 */
	@Override
	protected JPanel createControlPane() {

		buttonPane = createButtonPane();
		presetPane = createPresetPane();

		JPanel controlPane = new JPanel(new BorderLayout());
		controlPane.setBorder(new EmptyBorder(3,0,0,0));

		controlPane.add(presetPane, BorderLayout.CENTER);
		controlPane.add(buttonPane, BorderLayout.SOUTH);

		return controlPane;

	}

	@Override
	protected void addContextHelp() {

		// overriden to add to the button pane rather than control pane

		URL contextHelpURL = getContextHelpURL();
		if (contextHelpURL != null) {

			buttonPane.add(Box.createHorizontalStrut(5), 0);
			ContextHelpAction helpAction = new ContextHelpAction(contextHelpURL);
			KeyStroke f1 = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f1, "HELP");
			getRootPane().getActionMap().put("HELP", helpAction);
			buttonPane.add(new JButton(helpAction), 0);

		}

	}

	/**
	 * Returns if this dialog knows if there are any changes in it (and
	 * therefore supports {@link #isChanged()}).
	 * @return {@code true} if this dialog knows if there are any changes in
	 * it, {@code false} otherwise
	 */
	protected boolean isTrackingChanges() {
		return false;
	}

	/**
	 * Returns if this dialog should show the button to load the default
	 * {@link Preset preset}.
	 * @return {@code true} if this dialog should show the button to load the
	 * default preset, {@code false} otherwise
	 */
	protected boolean showLoadDefaultButton() {
		return false;
	}

	/**
	 * Returns if this dialog should show the button to save the default
	 * {@link Preset preset}.
	 * @return {@code true} if this dialog should show the button to save the
	 * default preset, {@code false} otherwise
	 */
	protected boolean showSaveDefaultButton() {
		return false;
	}

	/**
	 * Returns if this dialog should show the button to remove the default
	 * {@link Preset preset}.
	 * @return {@code true} if this dialog should show the button to remove the
	 * default preset, {@code false} otherwise
	 */
	protected boolean showRemoveDefaultButton() {
		return false;
	}

	/**
	 * Creates the combo box which allows to select the {@link Preset preset}.
	 * @return created combo box
	 */
	protected JComboBox getPresetComboBox() {
		if (presetComboBox == null) {

			presetComboBoxModel = new PresetComboBoxModel(_("<< select to load preset >>"), presetManager);
			presetComboBox = new JComboBox(presetComboBoxModel);
			presetComboBox.setSelectedIndex(0);
			presetComboBox.setPreferredSize(new Dimension(200,20));

			presetComboBox.addActionListener(loadPresetAction);

		}
		return presetComboBox;
	}

	/**
	 * Returns the {@link ChoosePresetDialog}.
	 * @return the ChoosePresetDialog
	 */
	protected ChoosePresetDialog getChoosePresetDialog() {
		if (choosePresetDialog == null) {
			choosePresetDialog = new ChoosePresetDialog();
		}
		return choosePresetDialog;
	}

	/**
	 * Returns the {@link SeriousWarningDialog dialog} which displays the
	 * warning to the user and allows him to decide if the operation should
	 * be terminated.
	 * If the dialog doesn't exist, it is created.
	 * @return the warning dialog
	 */
	protected SeriousWarningDialog getSeriousWarningDialog() {
		if (seriousWarningDialog == null) {
			seriousWarningDialog = new SeriousWarningDialog(this,true);
			seriousWarningDialog.setApplicationConfig(applicationConfig);
		}
		return seriousWarningDialog;
	}

	/**
	 * Returns the {@link PresetManager preset manager}.
	 * @return the preset manager
	 */
	public PresetManager getPresetManager() {
		return presetManager;
	}

	/**
	 * Returns the {@link ViewerFileChooser file chooser}.
	 * @return the file chooser
	 */
	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Sets the {@link ViewerFileChooser file chooser}.
	 * @param fileChooser the file chooser to set
	 */
	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	/**
	 * Returns the {@link ApplicationConfiguration configuration} of Svarog.
	 * @return the configuration of Svarog
	 */
	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	/**
	 * Sets the {@link ApplicationConfiguration configuration} of Svarog. 
	 * @param applicationConfig the configuration of Svarog
	 */
	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	/**
	 * (Creates and) returns the current {@link Preset preset}.
	 * Must be specified in the implementing class.
	 * @return the current preset
	 * @throws SignalMLException TODO never thrown in implementations (???)
	 */
	public abstract Preset getPreset() throws SignalMLException;

	/**
	 * Sets the given preset as the current {@link Preset preset}.
	 * Fills all necessary fields of the dialog with the data from this preset.
	 * Must be specified in the implementing class.
	 * @param preset the preset to use as the new current preset.
	 * @throws SignalMLException TODO never thrown in implementations
	 */
	public abstract void setPreset(Preset preset) throws SignalMLException;

	/**
	 * Returns if the fields of this dialog have changed.
	 * @return {@code true} if the fields of this dialog have changed,
	 * {@code false} otherwise
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Sets that the fields of this dialog have changed.
	 */
	public void invalidateChanged() {
		this.changed = true;
	}

	/**
	 * Sets if the fields of this dialog have changed.
	 * @param changed {@code true} if the fields of this dialog have changed,
	 * {@code false} otherwise
	 */
	protected void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	protected void resetDialog() {
		super.resetDialog();
		presetComboBox.setSelectedIndex(0);
		presetComboBox.repaint();
		changed = false;
		setEnableds();
	}

	/**
	 * Sets if buttons should be enabled:
	 * <ul>
	 * <li>removePresetAction - if there is at least one {@link Preset preset}
	 * </li>
	 * <li>loadDefaultPresetAction & removeDefaultPresetAction -
	 * if the default preset exists</li>
	 * </ul>
	 */
	protected void setEnableds() {

		boolean hasDefault = (presetManager.getDefaultPreset() != null);
		boolean hasPresets = (presetManager.getPresetCount() > 0);

		presetComboBox.setEnabled(hasPresets);
		removePresetAction.setEnabled(hasPresets);
		loadDefaultPresetAction.setEnabled(hasDefault);
		removeDefaultPresetAction.setEnabled(hasDefault);
	}

	/**
	 * Listens on changes in the {@link PresetManager preset manager} and
	 * {@link AbstractPresetDialog#setEnabled(boolean) updates} the states of
	 * buttons.
	 */
	protected class PresetManagerChangeListener implements PresetManagerListener {

		/**
		 * {@link AbstractPresetDialog#setEnabled(boolean) Updates} the states
		 * of buttons
		 */
		@Override
		public void defaultPresetChanged(PresetManagerEvent ev) {
			setEnableds();
		}

		/**
		 * {@link AbstractPresetDialog#setEnabled(boolean) Updates} the states
		 * of buttons
		 */
		@Override
		public void presetAdded(PresetManagerEvent ev) {
			setEnableds();
		}

		/**
		 * {@link AbstractPresetDialog#setEnabled(boolean) Updates} the states
		 * of buttons
		 */
		@Override
		public void presetRemoved(PresetManagerEvent ev) {
			setEnableds();
		}

		/**
		 * Does nothing.
		 */
		@Override
		public void presetReplaced(PresetManagerEvent ev) {
			// ignored
		}

	}

	/**
	 * Action that sets the {@link AbstractPresetDialog#getPreset() current}
	 * {@link Preset preset} as the
	 * {@link PresetManager#setDefaultPreset(Preset) default} one.
	 */
	protected class SaveDefaultPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public SaveDefaultPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/default_preset_save.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Make default"));
		}

		/**
		 * Called when this action is performed:
		 * <ul>
		 * <li>{@link AbstractPresetDialog#getPreset() gets} the current
		 * {@link Preset preset} from the dialog,</li>
		 * <li>if the default preset already exists asks the user if it should
		 * be replaced,</li>
		 * <li>{@link PresetManager#setDefaultPreset(Preset) sets} the preset
		 * as the default one.</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			Preset preset;
			try {
				preset = getPreset();
			} catch (SignalMLException ex) {
				logger.error("Failed to get preset", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
				return;
			}
			if (preset == null) {
				return;
			}
			if (!presetManager.getPresetClass().isAssignableFrom(preset.getClass())) {
				throw new ClassCastException("Bad preset class");
			}

			Preset existingPreset = presetManager.getDefaultPreset();
			if (existingPreset != null) {

				String warning =  _("The default preset will be permanently overwritten.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently overwrite</b> the default?");
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			presetManager.setDefaultPreset(preset);

			if (applicationConfig.isSaveConfigOnEveryChange()) {
				try {
					presetManager.writeToPersistence(null);
				} catch (IOException ex) {
					logger.error("Failed to save preset configuration", ex);
				}
			}

		}

	}

	/**
	 * Action that replaces the current {@link Preset preset} in the dialog
	 * with the {@link PresetManager#getDefaultPreset()} default preset
	 * (fills the dialog using the default preset).
	 */
	protected class LoadDefaultPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public LoadDefaultPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/default_preset_load.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Load default"));
		}

		/**
		 * Called when this action is performed:
		 * <ul>
		 * <li>{@link PresetManager#getDefaultPreset() gets} the default
		 * {@link Preset preset},</li>
		 * <li>if there were changes in the dialog or dialog doesn't know if
		 * there were, shows the warning to the user that the current preset
		 * will be overridden and allows him to cancel the operation,</li>
		 * <li>{@link AbstractPresetDialog#setPreset(Preset) fills} the dialog
		 * from the preset.</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			Preset preset = presetManager.getDefaultPreset();
			if (preset == null) {
				return;
			}

			if (!isTrackingChanges() || isChanged()) {

				String warning =  _("The settings currently on the dialog will be permanently overwritten with the default preset.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently overwrite</b> current settings?");
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 3);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			try {
				setPreset(preset);
			} catch (SignalMLException ex) {
				logger.error("Failed to set preset", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
				return;
			}

		}

	}

	/**
	 * Action that sets that there is no default {@link Preset preset}.
	 */
	protected class RemoveDefaultPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public RemoveDefaultPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/default_preset_remove.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Remove default"));
		}

		/**
		 * Called when this action is performed:
		 * <ul>
		 * <li>if there is no default {@link Preset preset} does nothing,</li>
		 * <li>displays the warning to the user,</li>
		 * <li>if user accepted the warning sets that there is no active
		 * preset.</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			Preset preset = presetManager.getDefaultPreset();
			if (preset == null) {
				return;
			}

			String warning =  _("The default preset will be permanently removed.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently remove</b> the default?");
			SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

			boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
			if (!ok) {
				return;
			}

			presetManager.setDefaultPreset(null);

			if (applicationConfig.isSaveConfigOnEveryChange()) {
				try {
					presetManager.writeToPersistence(null);
				} catch (IOException ex) {
					logger.error("Failed to save preset configuration", ex);
				}
			}

		}

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
				preset = getPreset();
			} catch (SignalMLException ex) {
				logger.error("Failed to get preset", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
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

				String warning =  _R("Preset named ''<b>{0}</b>'' will be permanently overwritten.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently overwrite</b> this preset?", new Object[] { newName });
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			preset.setName(newName);

			presetManager.setPreset(preset);

			if (applicationConfig.isSaveConfigOnEveryChange()) {
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

			getPresetComboBox().setSelectedIndex(0);
			getPresetComboBox().repaint();

			if (preset == null) {
				return;
			}

			if (!isTrackingChanges() || isChanged()) {

				String warning =  _R("The settings currently on the dialog will be permanently overwritten with preset named ''<b>{0}</b>''.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently overwrite</b> current settings?", new Object[] { preset.getName() });
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 3);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			try {
				setPreset(preset);
			} catch (SignalMLException ex) {
				logger.error("Failed to set preset", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
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

			String warning =  _R("Selected preset named ''<b>{0}</b>'' will be permanently removed.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently remove</b> this preset?", new Object[] { name });
			SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

			boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
			if (!ok) {
				return;
			}

			presetManager.removePresetByName(name);

			if (applicationConfig.isSaveConfigOnEveryChange()) {
				try {
					presetManager.writeToPersistence(null);
				} catch (IOException ex) {
					logger.error("Failed to save preset configuration", ex);
				}
			}

		}

	}

	/**
	 * Action of saving the {@link Preset preset} to the file.
	 * {@link AbstractPresetDialog#getPreset() Obtains} the preset and saves it
	 * to the file selected by user.
	 */
	protected class SaveFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public SaveFileAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_save.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Export preset to file..."));
		}

		/**
		 * Called when the action is performed:
		 * <ul>
		 * <li>{@link AbstractPresetDialog#getPreset() Obtains} the
		 * {@link Preset preset} from dialog,</li>
		 * <li>asks the user to select the file to which the preset should be
		 * saved</li>
		 * <li>{@link PresetManager#writeToFile(File, Preset) saves} the preset
		 * to the selected file.</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			Preset preset;
			try {
				preset = getPreset();
			} catch (SignalMLException ex) {
				logger.error("Failed to get preset", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
				return;
			}
			if (preset == null) {
				return;
			}
			if (!presetManager.getPresetClass().isAssignableFrom(preset.getClass())) {
				throw new ClassCastException("Bad preset class");
			}

			boolean hasFile = false;

			File file = null;
			do {

				file = fileChooser.chooseSavePresetFile(AbstractPresetDialog.this);
				if (file == null) {
					return;
				}

				hasFile = true;

				if (file.exists()) {
					int res = OptionPane.showFileAlreadyExists(AbstractPresetDialog.this);
					if (res != OptionPane.OK_OPTION) {
						hasFile = false;
					}
				}

			} while (!hasFile);

			try {
				presetManager.writeToFile(file, preset);
			} catch (IOException ex) {
				logger.error("Exception when writing file", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
				return;
			}

		}

	}

	/**
	 * The action of loading the {@link Preset preset} from file.
	 * Asks the user to select the file and
	 * {@link PresetManager#readFromFile(File) loads} the preset from file.
	 */
	protected class LoadFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool-tip text for this action.
		 */
		public LoadFileAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_load.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Import preset from file..."));
		}

		/**
		 * Called when the button is clicked.
		 * <ul>
		 * <li>asks the user to select the file with {@link Preset preset},</li>
		 * <li>{@link PresetManager#readFromFile(File) loads} the preset from
		 * the selected file,</li>
		 * <li>if there were changes in the dialog or dialog doesn't know if
		 * there were, shows the warning to the user that the current preset
		 * will be overridden and allows him to cancel the operation,</li>
		 * <li>{@link AbstractPresetDialog#setPreset(Preset) sets} this preset
		 * as the active one (fills the dialog with the data from it),</li>
		 * <li>{@link ChoosePresetDialog#getName(String, boolean) gets} the
		 * name for this preset,</li>
		 * <li>if the preset of such name exists asks if it should be replaced,
		 * </li>
		 * <li>saves the preset to the {@link PresetManager}.</li>
		 * </ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseLoadPresetFile(AbstractPresetDialog.this);
			if (file == null) {
				return;
			}

			Preset preset;
			try {
				preset = presetManager.readFromFile(file);
			} catch (IOException ex) {
				logger.error("Exception when reading file", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
				return;
			}

			if (!isTrackingChanges() || isChanged()) {

				String warning =  _R("The settings currently on the dialog will be permanently overwritten with preset named ''<b>{0}</b>''.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently overwrite</b> current settings?", new Object[] { preset.getName() });
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 3);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			try {
				setPreset(preset);
			} catch (SignalMLException ex) {
				logger.error("Failed to set preset", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractPresetDialog.this, ex);
				return;
			}

			String newName = getChoosePresetDialog().getName(preset.getName(), true);
			if (newName == null) {
				return;
			}

			Preset existingPreset = presetManager.getPresetByName(newName);
			if (existingPreset != null) {

				String warning =  _R("Preset named ''<b>{0}</b>'' will be permanently overwritten.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>permanently overwrite</b> this preset?", new Object[] { newName });
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			preset.setName(newName);

			presetManager.setPreset(preset);

			if (applicationConfig.isSaveConfigOnEveryChange()) {
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
			super(AbstractPresetDialog.this, true);
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
