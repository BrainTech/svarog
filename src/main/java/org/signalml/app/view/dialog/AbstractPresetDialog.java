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
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;

/** AbstractPresetDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractPresetDialog extends AbstractDialog {

	static final long serialVersionUID = 1L;

	protected JPanel buttonPane;

	protected PresetManager presetManager;
	protected ViewerFileChooser fileChooser;
	protected ApplicationConfiguration applicationConfig;

	private PresetComboBoxModel presetComboBoxModel;
	private JComboBox presetComboBox;

	private ChoosePresetDialog choosePresetDialog;
	private SeriousWarningDialog seriousWarningDialog;

	private Action loadDefaultPresetAction;
	private Action saveDefaultPresetAction;
	private Action removeDefaultPresetAction;

	private Action savePresetAction;
	private Action loadPresetAction;
	private Action removePresetAction;

	private Action saveFileAction;
	private Action loadFileAction;

	private JButton loadDefaultPresetButton;
	private JButton saveDefaultPresetButton;
	private JButton removeDefaultPresetButton;

	private JButton savePresetButton;
	private JButton removePresetButton;

	private JButton saveFileButton;
	private JButton loadFileButton;

	private boolean changed = false;

	protected JPanel presetPane;

	public AbstractPresetDialog(MessageSourceAccessor messageSource,PresetManager presetManager) {
		super(messageSource);
		this.presetManager = presetManager;
	}

	public AbstractPresetDialog(MessageSourceAccessor messageSource,PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, w, isModal);
		this.presetManager = presetManager;
	}

	@Override
	protected void initialize() {
		super.initialize();
		presetManager.addPresetManagerListener(new PresetManagerChangeListener());
	}

	protected JPanel createButtonPane() {
		return super.createControlPane();
	}

	protected JPanel createPresetPane() {

		JPanel presetPane = new JPanel();
		presetPane.setLayout(new BoxLayout(presetPane, BoxLayout.X_AXIS));
		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("presetDialog.title")),
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

	protected boolean isTrackingChanges() {
		return false;
	}

	protected boolean showLoadDefaultButton() {
		return false;
	}

	protected boolean showSaveDefaultButton() {
		return false;
	}

	protected boolean showRemoveDefaultButton() {
		return false;
	}

	protected JComboBox getPresetComboBox() {
		if (presetComboBox == null) {

			presetComboBoxModel = new PresetComboBoxModel(messageSource.getMessage("presetDialog.selectToLoad"), presetManager);
			presetComboBox = new JComboBox(presetComboBoxModel);
			presetComboBox.setSelectedIndex(0);
			presetComboBox.setPreferredSize(new Dimension(200,20));

			presetComboBox.addActionListener(loadPresetAction);

		}
		return presetComboBox;
	}

	protected ChoosePresetDialog getChoosePresetDialog() {
		if (choosePresetDialog == null) {
			choosePresetDialog = new ChoosePresetDialog();
		}
		return choosePresetDialog;
	}

	protected SeriousWarningDialog getSeriousWarningDialog() {
		if (seriousWarningDialog == null) {
			seriousWarningDialog = new SeriousWarningDialog(messageSource,this,true);
			seriousWarningDialog.setApplicationConfig(applicationConfig);
		}
		return seriousWarningDialog;
	}

	public PresetManager getPresetManager() {
		return presetManager;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public abstract Preset getPreset() throws SignalMLException;

	public abstract void setPreset(Preset preset) throws SignalMLException;

	public boolean isChanged() {
		return changed;
	}

	public void invalidateChanged() {
		this.changed = true;
	}

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

	protected void setEnableds() {

		boolean hasDefault = (presetManager.getDefaultPreset() != null);
		boolean hasPresets = (presetManager.getPresetCount() > 0);

		removePresetAction.setEnabled(hasPresets);
		loadDefaultPresetAction.setEnabled(hasDefault);
		removeDefaultPresetAction.setEnabled(hasDefault);

	}

	protected class PresetManagerChangeListener implements PresetManagerListener {

		@Override
		public void defaultPresetChanged(PresetManagerEvent ev) {
			setEnableds();
		}

		@Override
		public void presetAdded(PresetManagerEvent ev) {
			setEnableds();
		}

		@Override
		public void presetRemoved(PresetManagerEvent ev) {
			setEnableds();
		}

		@Override
		public void presetReplaced(PresetManagerEvent ev) {
			// ignored
		}

	}

	protected class SaveDefaultPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveDefaultPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/default_preset_save.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.saveDefaultPreset"));
		}

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

				String warning =  messageSource.getMessage("presetDialog.onReplaceDefaultPreset");
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

	protected class LoadDefaultPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public LoadDefaultPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/default_preset_load.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.loadDefaultPreset"));
		}

		public void actionPerformed(ActionEvent ev) {

			Preset preset = presetManager.getDefaultPreset();
			if (preset == null) {
				return;
			}

			if (!isTrackingChanges() || isChanged()) {

				String warning =  messageSource.getMessage("presetDialog.onLoadDefaultPreset");
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

	protected class RemoveDefaultPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveDefaultPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/default_preset_remove.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.removeDefaultPreset"));
		}

		public void actionPerformed(ActionEvent ev) {

			Preset preset = presetManager.getDefaultPreset();
			if (preset == null) {
				return;
			}

			String warning =  messageSource.getMessage("presetDialog.onRemoveDefaultPreset");
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

	protected class SavePresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SavePresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/preset_save.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.savePreset"));
		}

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

				String warning =  messageSource.getMessage("presetDialog.onReplacePreset", new Object[] { newName });
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

	protected class LoadPresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public LoadPresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/preset_load.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.loadPreset"));
		}

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

				String warning =  messageSource.getMessage("presetDialog.onLoadPreset", new Object[] { preset.getName() });
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

	protected class RemovePresetAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemovePresetAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/preset_remove.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.removePreset"));
		}

		public void actionPerformed(ActionEvent ev) {

			String name = getChoosePresetDialog().getName(null, false);
			if (name == null) {
				return;
			}

			Preset preset = presetManager.getPresetByName(name);
			if (preset == null) {
				return;
			}

			String warning =  messageSource.getMessage("presetDialog.onRemovePreset", new Object[] { name });
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

	protected class SaveFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveFileAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_save.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.saveFile"));
		}

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

	protected class LoadFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public LoadFileAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_load.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("presetDialog.loadFile"));
		}

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

				String warning =  messageSource.getMessage("presetDialog.onLoadPreset", new Object[] { preset.getName() });
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

				String warning =  messageSource.getMessage("presetDialog.onReplacePreset", new Object[] { newName });
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

	protected class ChoosePresetDialog extends AbstractDialog {

		private static final long serialVersionUID = 1L;

		private PresetComboBoxModel presetComboBoxModel;
		protected JComboBox presetComboBox;
		private JTextField nameTextField;

		protected boolean editable = true;

		protected ChoosePresetDialog() {
			super(AbstractPresetDialog.this.messageSource, AbstractPresetDialog.this, true);
		}

		public boolean isEditable() {
			return editable;
		}

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
			setTitle(messageSource.getMessage("presetDialog.selectPreset"));
			super.initialize();
		}

		@Override
		public JComponent createInterface() {

			JPanel interfacePanel = new JPanel(new BorderLayout());
			interfacePanel.setBorder(new CompoundBorder(
			                                 new TitledBorder(messageSource.getMessage("presetDialog.selectPresetName")),
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

		public PresetComboBoxModel getPresetComboBoxModel() {
			if (presetComboBoxModel == null) {
				presetComboBoxModel = new PresetComboBoxModel(messageSource.getMessage("presetDialog.selectToChoose"), presetManager);
			}
			return presetComboBoxModel;
		}

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
