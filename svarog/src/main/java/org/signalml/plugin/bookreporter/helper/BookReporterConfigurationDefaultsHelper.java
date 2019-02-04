package org.signalml.plugin.bookreporter.helper;

import org.signalml.plugin.bookreporter.BookReporterPlugin;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.tool.PluginConfigurationDefaultsHelper;

/**
 * @author piotr@develancer.pl
 * (based on NewStagerConfigurationDefaultsHelper)
 */
public class BookReporterConfigurationDefaultsHelper extends PluginConfigurationDefaultsHelper {

	private static final BookReporterConfigurationDefaultsHelper SharedInstance = new BookReporterConfigurationDefaultsHelper();

	public static BookReporterConfigurationDefaultsHelper GetSharedInstance() {
		return SharedInstance;
	}

	@Override
	protected String getConfigurationDefaultsPath(Class<? extends Plugin> pluginClass) {
		if (pluginClass != BookReporterPlugin.class) {
			return null;
		}
		return "bookReporter_defaults.properties";
	}

//	public void setDefaults(BookReporterParameters parameters) {
//		if (!this.hasProperties()) {
//			return;
//		}
//
//		this.setDefaults(parameters.thresholds);
//	}

//	public void setDefaults(BookReporterFixedParameters fixedParameters) {
//		if (!this.hasProperties()) {
//			return;
//		}
//
//		try {
//			fixedParameters.widthCoeff = double_("bookReporter.fixed.widthCoeff");
//			fixedParameters.swaWidthCoeff = double_("bookReporter.fixed.swaWidthCoeff");
//			fixedParameters.alphaPerc1 = double_("bookReporter.fixed.alphaPerc1");
//			fixedParameters.alphaPerc2 = double_("bookReporter.fixed.alphaPerc2");
//			fixedParameters.corrCoeffRems = double_("bookReporter.fixed.corrCoeffRems");
//			fixedParameters.corrCoeffSems = double_("bookReporter.fixed.corrCoeffSems");
//		} catch (ConfigurationDefaultsException e) {
//			return;
//		}
//	}

//	public void setDefaults(BookReporterParameterThresholds thresholds) {
//		if (!this.hasProperties()) {
//			return;
//		}
//
//		try {
//			thresholds.toneEMG = double_("bookReporter.emgToneThreshold");
//			thresholds.montageEEGThreshold = double_("bookReporter.mtEegThreshold");
//			thresholds.montageEMGThreshold = double_("bookReporter.mtEmgThreshold");
//			thresholds.montageToneEMGThreshold = double_("bookReporter.mtToneEmgThreshold");
//			thresholds.remEogDeflectionThreshold = double_("bookReporter.remEogDeflectionThreshold");
//			thresholds.semEogDeflectionThreshold = double_("bookReporter.semEogDeflectionThreshold");
//		} catch (NumberFormatException e) {
//			logger.error("Invalid default value", e);
//			return;
//		} catch (ConfigurationDefaultsException e) {
//			return;
//		}
//
//		thresholds.alphaThreshold = this.getAlphaThresholdDefaults();
//		thresholds.deltaThreshold = this.getDeltaThresholdDefaults();
//		thresholds.thetaThreshold = this.getThetaThresholdDefaults();
//		thresholds.spindleThreshold = this.getSpindleThresholdDefaults();
//		thresholds.kCThreshold = this.getkCThresholdDefaults();
//	}

//	public BookReporterFASPThreshold getAlphaThresholdDefaults() {
//		return this.thresholdFromKey("alpha", false);
//	}
//
//	public BookReporterFASPThreshold getDeltaThresholdDefaults() {
//		return this.thresholdFromKey("delta", false);
//	}
//
//	public BookReporterFASPThreshold getThetaThresholdDefaults() {
//		return this.thresholdFromKey("theta", false);
//	}
//
//	public BookReporterFASPThreshold getSpindleThresholdDefaults() {
//		return this.thresholdFromKey("spindle", false);
//	}
//
//	public BookReporterFASPThreshold getkCThresholdDefaults() {
//		return this.thresholdFromKey("kComplex", true);
//	}
//
//	public BookReporterMinMaxRange rangeFromKey(String key) throws ConfigurationDefaultsException {
//		String minStr = this.get(key + "Min");
//		String maxStr = this.get(key + "Max");
//		Double min = (minStr != null) ? Double.parseDouble(minStr) : null;
//		Double max = (maxStr != null) ? Double.parseDouble(maxStr) : null;
//		return new BookReporterMinMaxRange(min, max);
//	}
//	
//	private BookReporterFASPThreshold thresholdFromKey(String type, boolean parsePhase) {
//		BookReporterFASPThreshold threshold = BookReporterFASPThreshold.UNLIMITED;
//		try {
//			threshold = new BookReporterFASPThreshold(
//				rangeFromKey("bookReporter." + type + "Frequency"),
//				rangeFromKey("bookReporter." + type + "Amplitude"),
//				rangeFromKey("bookReporter." + type + "Scale"),
//				rangeFromKey("bookReporter." + type + "Phase")
//			);
//		} catch (NumberFormatException e) {
//			logger.error("Invalid default value", e);
//			// nothing here
//		} catch (ConfigurationDefaultsException e) {
//			// nothing here
//		}
//		return threshold;
//	}

}
