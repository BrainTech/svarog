/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.signalml.app.view.dialog;

import java.awt.Window;
import java.util.LinkedHashMap;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.TagStylePaletteDescriptor;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagStylePresetDialog extends TagStylePaletteDialog {

	/**
	 * Constructor. Sets message source.
	 * @param messageSource message source to set
	 */
	public TagStylePresetDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param messageSource message source to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public TagStylePresetDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	@Override
	public boolean arePresetsActive() {
		return true;
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

		StyledTagSet newStyles = (StyledTagSet) preset;
		TagStylePaletteDescriptor descriptor = new TagStylePaletteDescriptor(newStyles, null);

		fillDialogFromModel(descriptor);

	}

}
