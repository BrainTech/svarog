/* AbstractSignalSpaceAwarePresetDialog.java created 2008-01-27
 *
 */

package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** AbstractSignalSpaceAwarePresetDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalSpaceAwarePresetDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private JCheckBox includeSpaceCheckBox;

	public AbstractSignalSpaceAwarePresetDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	public AbstractSignalSpaceAwarePresetDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	@Override
	protected JPanel createPresetPane() {

		JPanel parentPane = super.createPresetPane();

		JPanel presetPane = new JPanel(new BorderLayout());

		presetPane.add(parentPane, BorderLayout.CENTER);
		presetPane.add(getIncludeSpaceCheckBox(), BorderLayout.SOUTH);

		presetPane.setBorder(parentPane.getBorder());
		parentPane.setBorder(null);

		return presetPane;

	}

	public JCheckBox getIncludeSpaceCheckBox() {
		if (includeSpaceCheckBox == null) {
			includeSpaceCheckBox = new JCheckBox(messageSource.getMessage("spacePresetDialog.includeSpace"));
			includeSpaceCheckBox.setFont(includeSpaceCheckBox.getFont().deriveFont(Font.PLAIN,10F));
		}
		return includeSpaceCheckBox;
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		setPreset(preset, getIncludeSpaceCheckBox().isSelected());
	}

	public abstract void setPreset(Preset preset, boolean includeSpace) throws SignalMLException;

}
