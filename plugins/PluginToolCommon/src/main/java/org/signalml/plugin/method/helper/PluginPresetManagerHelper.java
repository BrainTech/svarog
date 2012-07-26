package org.signalml.plugin.method.helper;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.tool.PluginResourceRepository;

import com.thoughtworks.xstream.XStream;

public class PluginPresetManagerHelper {

	protected static final Logger logger = Logger
			.getLogger(PluginPresetManagerHelper.class);

	public static MethodPresetManager GetPresetForMethod(
			ApplicationMethodManager applicationMethodManager,
			PluginMethodManager pluginMethodManager,
			String methodName, Class<? extends Preset> parameterClass) {

		MethodPresetManager presetManager = new MethodPresetManager(methodName,
				parameterClass);
		presetManager.setProfileDir(applicationMethodManager.getProfileDir());
		try {
			presetManager.setStreamer((XStream) PluginResourceRepository
					.GetResource("streamer"));
		} catch (PluginException e) {
			pluginMethodManager.handleException(e);
			logger.error("Can't get proper streamer (method: " + methodName
					+ ")", e);
			return presetManager;
		}
		try {
			presetManager.readFromPersistence(null);
		} catch (IOException ex) {
			if (ex instanceof FileNotFoundException) {
				logger.debug("Seems like preset configuration doesn't exist (method: "
						+ methodName + ")");
			} else {
				logger.error("Failed to read presets - presets lost (method: "
						+ methodName + ")", ex);
			}
		}

		return presetManager;
	}

}
