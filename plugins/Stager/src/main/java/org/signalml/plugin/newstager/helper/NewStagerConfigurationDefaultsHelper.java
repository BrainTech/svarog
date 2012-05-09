package org.signalml.plugin.newstager.helper;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerFASPThreshold;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerRules;
import org.signalml.plugin.tool.PluginConfigurationDefaultsHelper;
import org.signalml.util.MinMaxRange;

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
		if (!this.hasProperties()) {
			return;
		}

		try {
			parameters.rules = NewStagerRules.valueOf(get("stager.rules"));
			parameters.analyseEEGChannelsFlag = bool_("stager.mtEegThresholdEnabled");
			parameters.analyseEMGChannelFlag = bool_("stager.mtArtifactsThresholdEnabled");
		} catch (ConfigurationDefaultsException e) {
			return;
		}

		this.setDefaults(parameters.thresholds);
	}

	public void setDefaults(NewStagerParameterThresholds thresholds) {
		if (!this.hasProperties()) {
			return;
		}

		try {
			thresholds.toneEMG = double_("stager.emgToneThreshold");
			thresholds.montageEEGThreshold = double_("stager.mtEegThreshold");
			thresholds.montageEMGThreshold = double_("stager.mtEmgThreshold");
			thresholds.montageToneEMGThreshold = double_("stager.mtToneEmgThreshold");
		} catch (NumberFormatException e) {
			logger.error("Invalid default value", e);
			return;
		} catch (ConfigurationDefaultsException e) {
			return;
		}
		
		this.setDefaults(thresholds.alphaThreshold, "alpha", false);
		this.setDefaults(thresholds.deltaThreshold, "delta", false);
		this.setDefaults(thresholds.thetaThreshold, "theta", false);
		this.setDefaults(thresholds.spindleThreshold, "spindle", false);
		this.setDefaults(thresholds.kCThreshold, "kComplex", true);
	}

	private void setDefaults(NewStagerFASPThreshold threshold, String type, boolean parsePhase) {
		try {
			setRange(threshold.amplitude, "stager." + type + "Amplitude");
			setRange(threshold.frequency, "stager." + type + "Frequency");
			setRange(threshold.scale, "stager." + type + "Scale");
			if (parsePhase) {
				setRange(threshold.phase, "stager." + type + "Phase");
			}
		} catch (NumberFormatException e) {
			logger.error("Invalid default value", e);
			return;
		} catch (ConfigurationDefaultsException e) {
			return;
		}
		
	}
	
}
