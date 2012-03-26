package org.signalml.app.view.components.presets;

import static org.signalml.app.util.i18n.SvarogI18n._;

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

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.config.preset.PresetManagerEvent;
import org.signalml.app.config.preset.PresetManagerListener;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.AbstractPanel;
import org.signalml.app.view.components.AnyChangeDocumentAdapter;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.app.view.components.dialogs.OptionPane;

import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

public abstract class AbstractPresetPanel extends AbstractPanel {

	static final long serialVersionUID = 1L;

	/**
	 * the panel with OK and CANCEL button
	 */
	protected JPanel buttonPane;

	/**
	 * the {@link ViewerFileChooser file chooser}
	 */
	protected ViewerFileChooser fileChooser;

	protected PresetControlsPanel presetControlsPanel;

	/**
	 * Constructor. Sets message source and the {@link PresetManager preset
	 * manager}.
	 * @param presetManager the preset manager to set
	 */
	public AbstractPresetPanel(PresetManager presetManager) {
		super();
		presetControlsPanel = new PresetControlsPanel(this, presetManager);
	}
	
	protected PresetControlsPanel getPresetControlsPanel() {
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
	public abstract Preset getPreset() throws SignalMLException;

	/**
	 * Sets the given preset as the current {@link Preset preset}.
	 * Fills all necessary fields of the dialog with the data from this preset.
	 * Must be specified in the implementing class.
	 * @param preset the preset to use as the new current preset.
	 * @return true if preset was set
	 * @throws SignalMLException TODO never thrown in implementations
	 */
	public abstract boolean setPreset(Preset preset) throws SignalMLException;

	public void resetPreset() {
		getPresetControlsPanel().resetPanel();
	}

}
