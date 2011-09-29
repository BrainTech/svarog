/* ConfigurationDefaults.java created 2007-09-14
 *
 */
package org.signalml.app.config;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ToolTipManager;

import org.apache.log4j.Logger;
import org.signalml.app.view.book.GrayscaleMapPalette;
import org.signalml.app.view.book.RainbowMapPalette;
import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.domain.book.WignerMapScaleType;
import org.signalml.fft.WindowType;
import org.signalml.method.stager.SleepStagingRules;
import org.signalml.method.stager.StagerFixedParameters;
import org.signalml.method.stager.StagerParameters;
import org.signalml.util.MinMaxRange;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/** ConfigurationDefaults
 *
 *
 * @author Michał Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author Zbigniew Jędrzejewski-Szmek
 */
public class ConfigurationDefaults {

	protected static final Logger logger = Logger.getLogger(ConfigurationDefaults.class);

	private static final Properties properties;

	private static final String GREYSCALE_PALETTE = "greyscale";

	static {
		properties = new Properties();
		try {
			InputStream is = ConfigurationDefaults.class.getResourceAsStream(
							"signalml_defaults.properties");
			properties.load(is);
		} catch (IOException ex) {
			logger.error("Failed to load default properties - i/o exception", ex);
			throw new RuntimeException(ex);
		}
	}

	public static void setGeneralConfigurationDefaults(GeneralConfiguration config) {

		config.setProfileDefault(true);
		config.setProfilePath(null);

	}

	// property accessor methods

	static String getString(String name) {
		return properties.getProperty(name);
	}

	static boolean getBoolean(String name) {
		return Boolean.parseBoolean(getString(name));
	}

	static int getInt(String name) {
		return Integer.parseInt(getString(name));
	}

	static double getDouble(String name) {
		return Double.parseDouble(getString(name));
	}

	static float getFloat(String name) {
		return Float.parseFloat(getString(name));
	}

	// configuration methods

	public static void setMainFrameConfigurationDefaults(MainFrameConfiguration config) {

		config.setMaximized(getBoolean("mainframe.maximized"));
		config.setXSize(getInt("mainframe.xSize"));
		config.setYSize(getInt("mainframe.ySize"));
		config.setViewMode(getBoolean("mainframe.documentMaximized"));
		config.setHDividerLocation(getInt("mainframe.hDividerLocation"));
		config.setVDividerLocation(getInt("mainframe.vDividerLocation"));

	}

	public static void setApplicationConfigurationDefaults(ApplicationConfiguration config) {

		config.setRightClickPagesForward(getBoolean("application.rightClickPagesForward"));
		config.setAutoLoadDefaultMontage(getBoolean("application.autoLoadDefaultMontage"));
		config.setPrecalculateSignalChecksums(getBoolean("application.precalculateSignalChecksums"));

		config.setSaveConfigOnEveryChange(getBoolean("application.saveConfigOnEveryChange"));
		config.setRestoreWorkspace(getBoolean("application.restoreWorkspace"));

		config.setAntialiased(getBoolean("application.antialiased"));
		config.setClamped(getBoolean("application.clamped"));
		config.setOffscreenChannelsDrawn(getBoolean("application.offscreenChannelsDrawn"));
		config.setTagToolTipsVisible(getBoolean("application.tagToolTipsVisible"));

		config.setPageLinesVisible(getBoolean("application.pageLinesVisible"));
		config.setBlockLinesVisible(getBoolean("application.blockLinesVisible"));
		config.setChannelLinesVisible(getBoolean("application.channelLinesVisible"));

		config.setTagPaintMode(TagPaintMode.valueOf(getString("application.tagPaintMode")));
		config.setSignalColor(SignalColor.valueOf(getString("application.signalColor")));
		config.setSignalXOR(getBoolean("application.signalXOR"));

		config.setPageSize(getFloat("application.pageSize"));
		config.setBlocksPerPage(getInt("application.blocksPerPage"));
		config.setSaveFullMontageWithTag(getBoolean("application.saveFullMontageWithTag"));

		config.setViewModeHidesMainToolBar(getBoolean("application.viewModeHidesMainToolBar"));
		config.setViewModeHidesLeftPanel(getBoolean("application.viewModeHidesLeftPanel"));
		config.setViewModeHidesBottomPanel(getBoolean("application.viewModeHidesBottomPanel"));
		config.setViewModeCompactsPageTagBars(getBoolean("application.viewModeCompactsPageTagBars"));
		config.setViewModeSnapsToPage(getBoolean("application.viewModeSnapsToPage"));

		// these defaults are taken from Swing defaults
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		config.setToolTipInitialDelay(toolTipManager.getInitialDelay());
		config.setToolTipDismissDelay(toolTipManager.getDismissDelay());

		setZoomSignalSettingsDefaults(config.getZoomSignalSettings());

		config.setMinChannelHeight(getInt("application.minChannelHeight"));
		config.setMaxChannelHeight(getInt("application.maxChannelHeight"));
		config.setMinValueScale(getInt("application.minValueScale"));
		config.setMaxValueScale(getInt("application.maxValueScale"));
		config.setMinTimeScale(getDouble("application.minTimeScale"));
		config.setMaxTimeScale(getDouble("application.maxTimeScale"));

		config.setDisableSeriousWarnings(getBoolean("application.disableSeriousWarnings"));

		final String paletteString = getString("application.palette");
		if (GREYSCALE_PALETTE.equalsIgnoreCase(paletteString)) {
			config.setPalette(GrayscaleMapPalette.getInstance());
		} else {
			config.setPalette(RainbowMapPalette.getInstance());
		}

		config.setScaleType(WignerMapScaleType.valueOf(getString("application.scaleType")));
		
		config.setSignalAntialiased( getBoolean("application.signalAntialiased") );
		config.setOriginalSignalVisible( getBoolean("application.originalSignalVisible") );
		config.setFullReconstructionVisible( getBoolean("application.fullReconstructionVisible") );
		config.setReconstructionVisible( getBoolean("application.reconstructionVisible") );
		config.setLegendVisible( getBoolean("application.legendVisible") );
		config.setScaleVisible( getBoolean("application.scaleVisible") );
		config.setAxesVisible( getBoolean("application.axesVisible") );
		config.setAtomToolTipsVisible( getBoolean("application.atomToolTipsVisible") );
		
		config.setMapAspectRatioUp( getInt("application.mapAspectRatioUp") );
		config.setMapAspectRatioDown( getInt("application.mapAspectRatioDown") );
		config.setReconstructionHeight( getInt("application.reconstructionHeight") );
		
                config.setBackupFrequency( getFloat("application.signalRecording.frequency") );

		setMultiplexerDefaultParameters(config);
		setMonitorDefaultParameters(config);
	}

	public static void setZoomSignalSettingsDefaults(ZoomSignalSettings settings) {

		int width = getInt("application.zoomSettings.zoomSize.width");
		int height = getInt("application.zoomSettings.zoomSize.height");
		settings.setZoomSize(new Dimension(width, height));

		settings.setFactor(getFloat("application.zoomSettings.factor"));
		settings.setChannelSwitching(getBoolean("application.zoomSettings.channelSwitching"));

	}

	public static void setStagerParameters(StagerParameters parameters) {

		parameters.setRules(SleepStagingRules.valueOf(getString("stager.rules")));

		MinMaxRange range;

		range = parameters.getDeltaAmplitude();
		range.setMin(getDouble("stager.deltaAmplitudeMin"));
		range.setMinUnlimited(getBoolean("stager.deltaAmplitudeMinUnlimited"));
		range.setMax(getDouble("stager.deltaAmplitudeMax"));
		range.setMaxUnlimited(getBoolean("stager.deltaAmplitudeMaxUnlimited"));

		range = parameters.getDeltaFrequency();
		range.setMin(getDouble("stager.deltaFrequencyMin"));
		range.setMinUnlimited(getBoolean("stager.deltaFrequencyMinUnlimited"));
		range.setMax(getDouble("stager.deltaFrequencyMax"));
		range.setMaxUnlimited(getBoolean("stager.deltaFrequencyMaxUnlimited"));

		range = parameters.getDeltaScale();
		range.setMin(getDouble("stager.deltaScaleMin"));
		range.setMinUnlimited(getBoolean("stager.deltaScaleMinUnlimited"));
		range.setMax(getDouble("stager.deltaScaleMax"));
		range.setMaxUnlimited(getBoolean("stager.deltaScaleMaxUnlimited"));

		range = parameters.getThetaAmplitude();
		range.setMin(getDouble("stager.thetaAmplitudeMin"));
		range.setMinUnlimited(getBoolean("stager.thetaAmplitudeMinUnlimited"));
		range.setMax(getDouble("stager.thetaAmplitudeMax"));
		range.setMaxUnlimited(getBoolean("stager.thetaAmplitudeMaxUnlimited"));

		range = parameters.getThetaFrequency();
		range.setMin(getDouble("stager.thetaFrequencyMin"));
		range.setMinUnlimited(getBoolean("stager.thetaFrequencyMinUnlimited"));
		range.setMax(getDouble("stager.thetaFrequencyMax"));
		range.setMaxUnlimited(getBoolean("stager.thetaFrequencyMaxUnlimited"));

		range = parameters.getThetaScale();
		range.setMin(getDouble("stager.thetaScaleMin"));
		range.setMinUnlimited(getBoolean("stager.thetaScaleMinUnlimited"));
		range.setMax(getDouble("stager.thetaScaleMax"));
		range.setMaxUnlimited(getBoolean("stager.thetaScaleMaxUnlimited"));

		range = parameters.getAlphaAmplitude();
		range.setMin(getDouble("stager.alphaAmplitudeMin"));
		range.setMinUnlimited(getBoolean("stager.alphaAmplitudeMinUnlimited"));
		range.setMax(getDouble("stager.alphaAmplitudeMax"));
		range.setMaxUnlimited(getBoolean("stager.alphaAmplitudeMaxUnlimited"));

		range = parameters.getAlphaFrequency();
		range.setMin(getDouble("stager.alphaFrequencyMin"));
		range.setMinUnlimited(getBoolean("stager.alphaFrequencyMinUnlimited"));
		range.setMax(getDouble("stager.alphaFrequencyMax"));
		range.setMaxUnlimited(getBoolean("stager.alphaFrequencyMaxUnlimited"));

		range = parameters.getAlphaScale();
		range.setMin(getDouble("stager.alphaScaleMin"));
		range.setMinUnlimited(getBoolean("stager.alphaScaleMinUnlimited"));
		range.setMax(getDouble("stager.alphaScaleMax"));
		range.setMaxUnlimited(getBoolean("stager.alphaScaleMaxUnlimited"));

		range = parameters.getSpindleAmplitude();
		range.setMin(getDouble("stager.spindleAmplitudeMin"));
		range.setMinUnlimited(getBoolean("stager.spindleAmplitudeMinUnlimited"));
		range.setMax(getDouble("stager.spindleAmplitudeMax"));
		range.setMaxUnlimited(getBoolean("stager.spindleAmplitudeMaxUnlimited"));

		range = parameters.getSpindleFrequency();
		range.setMin(getDouble("stager.spindleFrequencyMin"));
		range.setMinUnlimited(getBoolean("stager.spindleFrequencyMinUnlimited"));
		range.setMax(getDouble("stager.spindleFrequencyMax"));
		range.setMaxUnlimited(getBoolean("stager.spindleFrequencyMaxUnlimited"));

		range = parameters.getSpindleScale();
		range.setMin(getDouble("stager.spindleScaleMin"));
		range.setMinUnlimited(getBoolean("stager.spindleScaleMinUnlimited"));
		range.setMax(getDouble("stager.spindleScaleMax"));
		range.setMaxUnlimited(getBoolean("stager.spindleScaleMaxUnlimited"));

		range = parameters.getKComplexAmplitude();
		range.setMin(getDouble("stager.kComplexAmplitudeMin"));
		range.setMinUnlimited(getBoolean("stager.kComplexAmplitudeMinUnlimited"));
		range.setMax(getDouble("stager.kComplexAmplitudeMax"));
		range.setMaxUnlimited(getBoolean("stager.kComplexAmplitudeMaxUnlimited"));

		range = parameters.getKComplexFrequency();
		range.setMin(getDouble("stager.kComplexFrequencyMin"));
		range.setMinUnlimited(getBoolean("stager.kComplexFrequencyMinUnlimited"));
		range.setMax(getDouble("stager.kComplexFrequencyMax"));
		range.setMaxUnlimited(getBoolean("stager.kComplexFrequencyMaxUnlimited"));

		range = parameters.getKComplexScale();
		range.setMin(getDouble("stager.kComplexScaleMin"));
		range.setMinUnlimited(getBoolean("stager.kComplexScaleMinUnlimited"));
		range.setMax(getDouble("stager.kComplexScaleMax"));
		range.setMaxUnlimited(getBoolean("stager.kComplexScaleMaxUnlimited"));

		range = parameters.getKComplexPhase();
		range.setMin(getDouble("stager.kComplexPhaseMin"));
		range.setMinUnlimited(getBoolean("stager.kComplexPhaseMinUnlimited"));
		range.setMax(getDouble("stager.kComplexPhaseMax"));
		range.setMaxUnlimited(getBoolean("stager.kComplexPhaseMaxUnlimited"));

		parameters.setEmgToneThreshold(getDouble("stager.emgToneThreshold"));
		parameters.setMtEegThreshold(getDouble("stager.mtEegThreshold"));
		parameters.setMtEegThresholdEnabled(getBoolean("stager.mtEegThresholdEnabled"));
		parameters.setMtArtifactsThresholdEnabled(getBoolean("stager.mtArtifactsThresholdEnabled"));
		parameters.setMtEmgThreshold(getDouble("stager.mtEmgThreshold"));
		parameters.setMtToneEmgThreshold(getDouble("stager.mtToneEmgThreshold"));
		parameters.setRemEogDeflectionThreshold(getDouble("stager.remEogDeflectionThreshold"));
		parameters.setSemEogDeflectionThreshold(getDouble("stager.semEogDeflectionThreshold"));

	}

	public static void setStagerFixedParameters(StagerFixedParameters parameters) {

		parameters.setSwaWidthCoeff(getDouble("stager.fixed.swaWidthCoeff"));
		parameters.setAlphaPerc1(getDouble("stager.fixed.alphaPerc1"));
		parameters.setAlphaPerc2(getDouble("stager.fixed.alphaPerc2"));
		parameters.setCorrCoeffRems(getDouble("stager.fixed.corrCoeffRems"));
		parameters.setCorrCoeffSems(getDouble("stager.fixed.corrCoeffSems"));

	}
	
	public static void setMultiplexerDefaultParameters(ApplicationConfiguration config) {

		config.setMultiplexerAddress(getString("multiplexer.address"));
		config.setDefaultMultiplexerAddress(getString("default.multiplexer.address"));
		config.setMultiplexerPort(getInt("multiplexer.port"));
		config.setDefaultMultiplexerPort(getInt("default.multiplexer.port"));

	}
	
	public static void setMonitorDefaultParameters( ApplicationConfiguration config ) {

		config.setMonitorPageSize(getFloat("monitor.pageSize"));

	}

	public static String getDefaultEegPlRegisterURL() {
		return getString("eeg.pl.registerUrl");
	}

	public static String getDefaultEegPlSignalmlWsURL() {
		return getString("eeg.pl.signalmlwsUrl");
	}

}
