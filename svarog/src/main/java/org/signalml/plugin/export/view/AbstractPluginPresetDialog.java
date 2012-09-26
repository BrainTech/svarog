package org.signalml.plugin.export.view;

import java.awt.Window;

import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.view.common.dialogs.AbstractPresetDialog;

public abstract class AbstractPluginPresetDialog extends AbstractPresetDialog {
	private static final long serialVersionUID = 1L;

	public AbstractPluginPresetDialog(PresetManager presetManager) {
		super(presetManager);
	}

	protected AbstractPluginPresetDialog(PresetManager presetManager, Window w, boolean isModal) {
		super(presetManager, w, isModal);
	}

}
