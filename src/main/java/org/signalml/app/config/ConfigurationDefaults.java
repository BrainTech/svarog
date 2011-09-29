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
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ConfigurationDefaults {

	protected static final Logger logger = Logger.getLogger(ConfigurationDefaults.class);

	private static final Properties properties;

	private static final String GREYSCALE_PALETTE = "greyscale";

	static {
		properties = new Properties();
		try {
			InputStream is = ConfigurationDefaults.class.getResourceAsStream("signalml_defaults.properties");
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

	public static void setMainFrameConfigurationDefaults(MainFrameConfiguration config) {

		config.setMaximized(Boolean.parseBoolean(properties.getProperty("mainframe.maximized")));
		config.setXSize(Integer.parseInt(properties.getProperty("mainframe.xSize")));
		config.setYSize(Integer.parseInt(properties.getProperty("mainframe.ySize")));
		config.setViewMode(Boolean.parseBoolean(properties.getProperty("mainframe.documentMaximized")));
		config.setHDividerLocation(Integer.parseInt(properties.getProperty("mainframe.hDividerLocation")));
		config.setVDividerLocation(Integer.parseInt(properties.getProperty("mainframe.vDividerLocation")));

	}

	public static void setApplicationConfigurationDefaults(ApplicationConfiguration config) {

		config.setRightClickPagesForward(Boolean.parseBoolean(properties.getProperty("application.rightClickPagesForward")));
		config.setAutoLoadDefaultMontage(Boolean.parseBoolean(properties.getProperty("application.autoLoadDefaultMontage")));
		config.setPrecalculateSignalChecksums(Boolean.parseBoolean(properties.getProperty("application.precalculateSignalChecksums")));

		config.setSaveConfigOnEveryChange(Boolean.parseBoolean(properties.getProperty("application.saveConfigOnEveryChange")));
		config.setRestoreWorkspace(Boolean.parseBoolean(properties.getProperty("application.restoreWorkspace")));

		config.setAntialiased(Boolean.parseBoolean(properties.getProperty("application.antialiased")));
		config.setClamped(Boolean.parseBoolean(properties.getProperty("application.clamped")));
		config.setOffscreenChannelsDrawn(Boolean.parseBoolean(properties.getProperty("application.offscreenChannelsDrawn")));
		config.setTagToolTipsVisible(Boolean.parseBoolean(properties.getProperty("application.tagToolTipsVisible")));

		config.setPageLinesVisible(Boolean.parseBoolean(properties.getProperty("application.pageLinesVisible")));
		config.setBlockLinesVisible(Boolean.parseBoolean(properties.getProperty("application.blockLinesVisible")));
		config.setChannelLinesVisible(Boolean.parseBoolean(properties.getProperty("application.channelLinesVisible")));

		config.setTagPaintMode(TagPaintMode.valueOf(properties.getProperty("application.tagPaintMode")));
		config.setSignalColor(SignalColor.valueOf(properties.getProperty("application.signalColor")));
		config.setSignalXOR(Boolean.parseBoolean(properties.getProperty("application.signalXOR")));

		config.setPageSize(Float.parseFloat(properties.getProperty("application.pageSize")));
		config.setBlocksPerPage(Integer.parseInt(properties.getProperty("application.blocksPerPage")));
		config.setSaveFullMontageWithTag(Boolean.parseBoolean(properties.getProperty("application.saveFullMontageWithTag")));

		config.setViewModeHidesMainToolBar(Boolean.parseBoolean(properties.getProperty("application.viewModeHidesMainToolBar")));
		config.setViewModeHidesLeftPanel(Boolean.parseBoolean(properties.getProperty("application.viewModeHidesLeftPanel")));
		config.setViewModeHidesBottomPanel(Boolean.parseBoolean(properties.getProperty("application.viewModeHidesBottomPanel")));
		config.setViewModeCompactsPageTagBars(Boolean.parseBoolean(properties.getProperty("application.viewModeCompactsPageTagBars")));
		config.setViewModeSnapsToPage(Boolean.parseBoolean(properties.getProperty("application.viewModeSnapsToPage")));

		// these defaults are taken from Swing defaults
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		config.setToolTipInitialDelay(toolTipManager.getInitialDelay());
		config.setToolTipDismissDelay(toolTipManager.getDismissDelay());

		setZoomSignalSettingsDefaults(config.getZoomSignalSettings());

		config.setMinChannelHeight(Integer.parseInt(properties.getProperty("application.minChannelHeight")));
		config.setMaxChannelHeight(Integer.parseInt(properties.getProperty("application.maxChannelHeight")));
		config.setMinValueScale(Integer.parseInt(properties.getProperty("application.minValueScale")));
		config.setMaxValueScale(Integer.parseInt(properties.getProperty("application.maxValueScale")));
		config.setMinTimeScale(Double.parseDouble(properties.getProperty("application.minTimeScale")));
		config.setMaxTimeScale(Double.parseDouble(properties.getProperty("application.maxTimeScale")));

		config.setDisableSeriousWarnings(Boolean.parseBoolean(properties.getProperty("application.disableSeriousWarnings")));

		String paletteString = properties.getProperty("application.palette");
		if (GREYSCALE_PALETTE.equalsIgnoreCase(paletteString)) {
			config.setPalette(GrayscaleMapPalette.getInstance());
		} else {
			config.setPalette(RainbowMapPalette.getInstance());
		}

		config.setScaleType( WignerMapScaleType.valueOf( properties.getProperty("application.scaleType") ) );
		
		config.setSignalAntialiased( Boolean.parseBoolean(properties.getProperty("application.signalAntialiased")) );
		config.setOriginalSignalVisible( Boolean.parseBoolean(properties.getProperty("application.originalSignalVisible")) );
		config.setFullReconstructionVisible( Boolean.parseBoolean(properties.getProperty("application.fullReconstructionVisible")) );
		config.setReconstructionVisible( Boolean.parseBoolean(properties.getProperty("application.reconstructionVisible")) );
		config.setLegendVisible( Boolean.parseBoolean(properties.getProperty("application.legendVisible")) );
		config.setScaleVisible( Boolean.parseBoolean(properties.getProperty("application.scaleVisible")) );
		config.setAxesVisible( Boolean.parseBoolean(properties.getProperty("application.axesVisible")) );
		config.setAtomToolTipsVisible( Boolean.parseBoolean(properties.getProperty("application.atomToolTipsVisible")) );
		
		config.setMapAspectRatioUp( Integer.parseInt(properties.getProperty("application.mapAspectRatioUp")) );
		config.setMapAspectRatioDown( Integer.parseInt(properties.getProperty("application.mapAspectRatioDown")) );
		config.setReconstructionHeight( Integer.parseInt(properties.getProperty("application.reconstructionHeight")) );
		
                config.setBackupFrequency( Float.parseFloat(properties.getProperty("application.signalRecording.frequency")));

		setMultiplexerDefaultParameters( config);
		setMonitorDefaultParameters( config);
	}

	public static void setZoomSignalSettingsDefaults(ZoomSignalSettings settings) {

		int width = Integer.parseInt(properties.getProperty("application.zoomSettings.zoomSize.width"));
		int height = Integer.parseInt(properties.getProperty("application.zoomSettings.zoomSize.height"));

		settings.setZoomSize(new Dimension(width, height));
		settings.setFactor(Float.parseFloat(properties.getProperty("application.zoomSettings.factor")));
		settings.setChannelSwitching(Boolean.parseBoolean(properties.getProperty("application.zoomSettings.channelSwitching")));

	}

	public static void setStagerParameters(StagerParameters parameters) {

		parameters.setRules(SleepStagingRules.valueOf(properties.getProperty("stager.rules")));

		MinMaxRange range;

		range = parameters.getDeltaAmplitude();
		range.setMin(Double.parseDouble(properties.getProperty("stager.deltaAmplitudeMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.deltaAmplitudeMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.deltaAmplitudeMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.deltaAmplitudeMaxUnlimited")));

		range = parameters.getDeltaFrequency();
		range.setMin(Double.parseDouble(properties.getProperty("stager.deltaFrequencyMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.deltaFrequencyMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.deltaFrequencyMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.deltaFrequencyMaxUnlimited")));

		range = parameters.getDeltaScale();
		range.setMin(Double.parseDouble(properties.getProperty("stager.deltaScaleMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.deltaScaleMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.deltaScaleMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.deltaScaleMaxUnlimited")));

		range = parameters.getThetaAmplitude();
		range.setMin(Double.parseDouble(properties.getProperty("stager.thetaAmplitudeMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.thetaAmplitudeMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.thetaAmplitudeMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.thetaAmplitudeMaxUnlimited")));

		range = parameters.getThetaFrequency();
		range.setMin(Double.parseDouble(properties.getProperty("stager.thetaFrequencyMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.thetaFrequencyMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.thetaFrequencyMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.thetaFrequencyMaxUnlimited")));

		range = parameters.getThetaScale();
		range.setMin(Double.parseDouble(properties.getProperty("stager.thetaScaleMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.thetaScaleMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.thetaScaleMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.thetaScaleMaxUnlimited")));

		range = parameters.getAlphaAmplitude();
		range.setMin(Double.parseDouble(properties.getProperty("stager.alphaAmplitudeMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.alphaAmplitudeMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.alphaAmplitudeMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.alphaAmplitudeMaxUnlimited")));

		range = parameters.getAlphaFrequency();
		range.setMin(Double.parseDouble(properties.getProperty("stager.alphaFrequencyMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.alphaFrequencyMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.alphaFrequencyMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.alphaFrequencyMaxUnlimited")));

		range = parameters.getAlphaScale();
		range.setMin(Double.parseDouble(properties.getProperty("stager.alphaScaleMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.alphaScaleMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.alphaScaleMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.alphaScaleMaxUnlimited")));

		range = parameters.getSpindleAmplitude();
		range.setMin(Double.parseDouble(properties.getProperty("stager.spindleAmplitudeMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.spindleAmplitudeMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.spindleAmplitudeMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.spindleAmplitudeMaxUnlimited")));

		range = parameters.getSpindleFrequency();
		range.setMin(Double.parseDouble(properties.getProperty("stager.spindleFrequencyMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.spindleFrequencyMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.spindleFrequencyMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.spindleFrequencyMaxUnlimited")));

		range = parameters.getSpindleScale();
		range.setMin(Double.parseDouble(properties.getProperty("stager.spindleScaleMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.spindleScaleMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.spindleScaleMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.spindleScaleMaxUnlimited")));

		range = parameters.getKComplexAmplitude();
		range.setMin(Double.parseDouble(properties.getProperty("stager.kComplexAmplitudeMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexAmplitudeMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.kComplexAmplitudeMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexAmplitudeMaxUnlimited")));

		range = parameters.getKComplexFrequency();
		range.setMin(Double.parseDouble(properties.getProperty("stager.kComplexFrequencyMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexFrequencyMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.kComplexFrequencyMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexFrequencyMaxUnlimited")));

		range = parameters.getKComplexScale();
		range.setMin(Double.parseDouble(properties.getProperty("stager.kComplexScaleMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexScaleMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.kComplexScaleMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexScaleMaxUnlimited")));

		range = parameters.getKComplexPhase();
		range.setMin(Double.parseDouble(properties.getProperty("stager.kComplexPhaseMin")));
		range.setMinUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexPhaseMinUnlimited")));
		range.setMax(Double.parseDouble(properties.getProperty("stager.kComplexPhaseMax")));
		range.setMaxUnlimited(Boolean.parseBoolean(properties.getProperty("stager.kComplexPhaseMaxUnlimited")));

		parameters.setEmgToneThreshold(Double.parseDouble(properties.getProperty("stager.emgToneThreshold")));
		parameters.setMtEegThreshold(Double.parseDouble(properties.getProperty("stager.mtEegThreshold")));
		parameters.setMtEegThresholdEnabled(Boolean.parseBoolean(properties.getProperty("stager.mtEegThresholdEnabled")));
		parameters.setMtArtifactsThresholdEnabled(Boolean.parseBoolean(properties.getProperty("stager.mtArtifactsThresholdEnabled")));
		parameters.setMtEmgThreshold(Double.parseDouble(properties.getProperty("stager.mtEmgThreshold")));
		parameters.setMtToneEmgThreshold(Double.parseDouble(properties.getProperty("stager.mtToneEmgThreshold")));
		parameters.setRemEogDeflectionThreshold(Double.parseDouble(properties.getProperty("stager.remEogDeflectionThreshold")));
		parameters.setSemEogDeflectionThreshold(Double.parseDouble(properties.getProperty("stager.semEogDeflectionThreshold")));

	}

	public static void setStagerFixedParameters(StagerFixedParameters parameters) {

		parameters.setSwaWidthCoeff(Double.parseDouble(properties.getProperty("stager.fixed.swaWidthCoeff")));
		parameters.setAlphaPerc1(Double.parseDouble(properties.getProperty("stager.fixed.alphaPerc1")));
		parameters.setAlphaPerc2(Double.parseDouble(properties.getProperty("stager.fixed.alphaPerc2")));
		parameters.setCorrCoeffRems(Double.parseDouble(properties.getProperty("stager.fixed.corrCoeffRems")));
		parameters.setCorrCoeffSems(Double.parseDouble(properties.getProperty("stager.fixed.corrCoeffSems")));

	}
	
	public static void setMultiplexerDefaultParameters(ApplicationConfiguration config) {

		config.setMultiplexerAddress(properties.getProperty("multiplexer.address"));
		config.setDefaultMultiplexerAddress(properties.getProperty("default.multiplexer.address"));

		int port = Integer.parseInt(properties.getProperty("multiplexer.port"));
		config.setMultiplexerPort( port);

		port = Integer.parseInt(properties.getProperty("default.multiplexer.port"));
		config.setDefaultMultiplexerPort(port);

	}
	
	public static void setMonitorDefaultParameters( ApplicationConfiguration config ) {

		config.setMonitorPageSize( Float.parseFloat( properties.getProperty( "monitor.pageSize")));

	}

	public static String getDefaultEegPlRegisterURL() {
		return properties.getProperty("eeg.pl.registerUrl");
	}

	public static String getDefaultEegPlSignalmlWsURL() {
		return properties.getProperty("eeg.pl.signalmlwsUrl");
	}

}
