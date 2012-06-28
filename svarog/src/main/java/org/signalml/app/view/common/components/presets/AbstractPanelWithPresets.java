package org.signalml.app.view.common.components.presets;

import javax.swing.JPanel;

import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.workspace.ViewerFileChooser;

/**
 * This is the base class for all panels that want to use the preset functionality.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractPanelWithPresets extends AbstractPanel implements PresetableView {

	static final long serialVersionUID = 1L;

	/**
	 * the panel with OK and CANCEL button
	 */
	protected JPanel buttonPane;

	/**
	 * the {@link ViewerFileChooser file chooser}
	 */
	protected ViewerFileChooser fileChooser;

	protected ComplexPresetControlsPanel presetControlsPanel;

	protected PresetManager presetManager;

	/**
	 * Constructor. Sets message source and the {@link PresetManager preset
	 * manager}.
	 * @param presetManager the preset manager to set
	 */
	public AbstractPanelWithPresets(PresetManager presetManager) {
		super();
		this.presetManager = presetManager;
	}

	protected ComplexPresetControlsPanel getPresetControlsPanel() {
		if (presetControlsPanel == null) {
			presetControlsPanel = new ComplexPresetControlsPanel(this, presetManager);
		}
		return presetControlsPanel;
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
		getPresetControlsPanel().setFileChooser(fileChooser);
	}

	public void resetPreset() {
		getPresetControlsPanel().resetPanel();
	}

}
