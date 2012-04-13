/* AbstractPresetDialog.java created 2007-10-24
 *
 */

package org.signalml.app.view.components.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.view.components.presets.PresetControlsPanel;
import org.signalml.app.view.components.presets.PresetableView;
import org.signalml.app.view.workspace.ViewerFileChooser;

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
public abstract class AbstractPresetDialog extends AbstractDialog implements PresetableView {

	static final long serialVersionUID = 1L;

	/**
	 * the panel with OK and CANCEL button
	 */
	protected JPanel buttonPane;

	/**
	 * The panel with all the controls to load/save presets.
	 */
	protected PresetControlsPanel presetControlsPanel;
	private PresetManager presetManager;

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

	public void setFileChooser(ViewerFileChooser fileChooser) {
		getPresetControlsPanel().setFileChooser(fileChooser);
	}

	public ViewerFileChooser getFileChooser() {
		return getPresetControlsPanel().getFileChooser();
	}

	protected PresetControlsPanel getPresetControlsPanel() {
		if (presetControlsPanel == null) {
			presetControlsPanel = new PresetControlsPanel(this, presetManager);
		}
		return presetControlsPanel;
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

		JPanel controlPane = new JPanel(new BorderLayout());
		controlPane.setBorder(new EmptyBorder(3,0,0,0));

		controlPane.add(getPresetControlsPanel(), BorderLayout.CENTER);
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
	 * Returns the {@link PresetManager preset manager}.
	 * @return the preset manager
	 */
	public PresetManager getPresetManager() {
		return presetManager;
	}

	/**
	 * Returns the {@link ApplicationConfiguration configuration} of Svarog.
	 * @return the configuration of Svarog
	 */
	public ApplicationConfiguration getApplicationConfig() {
		return SvarogApplication.getApplicationConfiguration();
	}

	@Override
	protected void resetDialog() {
		super.resetDialog();
		getPresetControlsPanel().resetPresetComboBoxSelection();
	}

	@Override
	public boolean isPresetCompatible(Preset preset) {
		/* compatible by default - if there's a need
		 * for validation, please override this method.
		 */
		return true;
	}

}
