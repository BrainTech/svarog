/* AbstractSignalSpaceAwarePresetDialog.java created 2008-01-27
 *
 */

package org.signalml.app.view.common.dialogs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.plugin.export.SignalMLException;

/**
 * Abstract {@link AbstractPresetDialog preset dialog} with the check-box that
 * tells if the {@link SignalSpace signal space} should be included in the
 * Preset.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalSpaceAwarePresetDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the check-box which tells if the {@link SignalSpace signal space} should
	 * be included in the {@link Preset}
	 */
	private JCheckBox includeSpaceCheckBox;

	/**
	 * Constructor. Sets message source, {@link PresetManager preset
	 * manager}, parent window and if this dialog blocks top-level windows.
	 * @param presetManager the preset manager to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public AbstractSignalSpaceAwarePresetDialog(PresetManager presetManager, Window w, boolean isModal) {
		super(presetManager, w, isModal);
	}

	/**
	 * Constructor. Sets message source and the {@link PresetManager preset
	 * manager}.
	 * @param presetManager the preset manager to set
	 */
	public AbstractSignalSpaceAwarePresetDialog(PresetManager presetManager) {
		super(presetManager);
	}

	@Override
	protected JPanel createControlPane() {
		buttonPane = createButtonPane();

		JPanel controlPane = new JPanel(new BorderLayout());
		controlPane.setBorder(new EmptyBorder(3,0,0,0));

		JPanel presetPanel = new JPanel(new BorderLayout());
		presetPanel.add(getPresetControlsPanel(), BorderLayout.CENTER);
		presetPanel.add(getIncludeSpaceCheckBox(), BorderLayout.SOUTH);
		Border previousBorder = getPresetControlsPanel().getBorder();
		getPresetControlsPanel().setBorder(null);
		presetPanel.setBorder(previousBorder);

		controlPane.add(presetPanel, BorderLayout.CENTER);
		controlPane.add(buttonPane, BorderLayout.SOUTH);

		return controlPane;
	}

	/**
	 * Returns the check-box which tells if the {@link SignalSpace signal
	 * space} should be included in the {@link Preset}.
	 * If the check-box doesn't exist, it is created.
	 * <p>NOTE: the state of the check-box must be set by implementation.
	 * @return the check-box which tells if the signal space should be included
	 * in the Preset
	 */
	public JCheckBox getIncludeSpaceCheckBox() {
		if (includeSpaceCheckBox == null) {
			includeSpaceCheckBox = new JCheckBox(_("Load signal selection from loaded preset"));
			includeSpaceCheckBox.setFont(includeSpaceCheckBox.getFont().deriveFont(Font.PLAIN,10F));
		}
		return includeSpaceCheckBox;
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		setPreset(preset, getIncludeSpaceCheckBox().isSelected());
	}

	/**
	 * Fills the fields of this dialog from the given {@link Preset preset}.
	 * @param preset the preset to be used
	 * @param includeSpace {@code true} if the information from the preset
	 * about the {@link SignalSpace signal space} should be used,
	 * {@code false} otherwise
	 * @throws SignalMLException depends on an implementation;
	 * now never thrown
	 */
	public abstract void setPreset(Preset preset, boolean includeSpace) throws SignalMLException;

}
