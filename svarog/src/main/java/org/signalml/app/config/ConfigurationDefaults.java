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
import org.signalml.math.fft.WindowType;
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
