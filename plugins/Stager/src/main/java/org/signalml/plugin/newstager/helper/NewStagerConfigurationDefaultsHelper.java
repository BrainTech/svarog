package org.signalml.plugin.newstager.helper;

import java.util.Properties;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerRules;
import org.signalml.plugin.tool.PluginConfigurationDefaultsHelper;

public class NewStagerConfigurationDefaultsHelper extends
		PluginConfigurationDefaultsHelper {

	private static NewStagerConfigurationDefaultsHelper SharedInstance = new NewStagerConfigurationDefaultsHelper();

	public static NewStagerConfigurationDefaultsHelper GetSharedInstance() {
		return SharedInstance;
	}

	public static void SetSharedInstance(
			NewStagerConfigurationDefaultsHelper helper) {
		SharedInstance = helper;
	}

	@Override
	protected String getConfigurationDefaultsPath(
			Class<? extends Plugin> pluginClass) {
		if (pluginClass != NewStagerPlugin.class) {
			return null;
		}

		return "stager_defaults.properties";
	}

	public void setDefaults(NewStagerParameters parameters) {
		Properties properties;
		try {
			properties = this.getProperties();
		} catch (ConfigurationDefaultsException e) {
			// do nothing
			return;
		}

		parameters.rules = NewStagerRules.valueOf(properties
				.getProperty("stager.rules"));
		this.setDefaults(parameters.thresholds);
	}

	public void setDefaults(NewStagerParameterThresholds thresholds) {
		Properties properties;
		try {
			properties = this.getProperties();
		} catch (ConfigurationDefaultsException e) {
			// do nothing
			return;
		}

		thresholds.toneEMG = Double.parseDouble(properties
				.getProperty("stager.emgToneThreshold"));
		thresholds.montageEEGThreshold = Double.parseDouble(properties
				.getProperty("stager.mtEegThreshold"));
		thresholds.montageEMGThreshold = Double.parseDouble(properties
				.getProperty("stager.mtEmgThreshold"));
		thresholds.montageToneEMGThreshold = Double.parseDouble(properties
				.getProperty("stager.mtToneEmgThreshold"));
	}
}
