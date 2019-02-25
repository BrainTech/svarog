/* ConfigurationDefaults.java created 2007-09-14
 *
 */
package org.signalml.app.config;

import java.awt.Dimension;
import java.util.Properties;

import javax.swing.ToolTipManager;

import org.apache.log4j.Logger;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.domain.book.WignerMapScaleType;

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
		properties = ConfigurationDefaultsLoader.Load(ConfigurationDefaults.class, "signalml_defaults.properties");
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
		config.setAutoAddHighpassFilter(getBoolean("application.autoAddHighpassFilter"));
		config.setAutoTryToLoadSignalWithTags(getBoolean("application.autoTryToLoadSignalWithTags"));
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
		config.setPalette(WignerMapPalette.valueOf(paletteString));

		config.setScaleType(WignerMapScaleType.valueOf(getString("application.scaleType")));

		config.setSignalInBookAntialiased(getBoolean("application.signalInBookAntialiased"));
		config.setOriginalSignalVisible(getBoolean("application.originalSignalVisible"));
		config.setFullReconstructionVisible(getBoolean("application.fullReconstructionVisible"));
		config.setReconstructionVisible(getBoolean("application.reconstructionVisible"));
		config.setLegendVisible(getBoolean("application.legendVisible"));
		config.setScaleVisible(getBoolean("application.scaleVisible"));
		config.setAxesVisible(getBoolean("application.axesVisible"));
		config.setAtomToolTipsVisible(getBoolean("application.atomToolTipsVisible"));

		config.setMapAspectRatioUp(getInt("application.mapAspectRatioUp"));
		config.setMapAspectRatioDown(getInt("application.mapAspectRatioDown"));
		config.setReconstructionHeight(getInt("application.reconstructionHeight"));

		config.setBackupFrequency(getFloat("application.signalRecording.frequency"));

		config.setMonitorPageSize(getFloat("monitor.pageSize"));
		config.setOpenbciIPAddress(getString("monitor.openbciIPAddress"));
		config.setOpenbciPort(getInt("monitor.openbciPort"));
	}

	public static void setZoomSignalSettingsDefaults(ZoomSignalSettings settings) {

		int width = getInt("application.zoomSettings.zoomSize.width");
		int height = getInt("application.zoomSettings.zoomSize.height");
		settings.setZoomSize(new Dimension(width, height));

		settings.setFactor(getFloat("application.zoomSettings.factor"));
		settings.setChannelSwitching(getBoolean("application.zoomSettings.channelSwitching"));

	}

	public static String getDefaultEegPlRegisterURL() {
		return getString("eeg.pl.registerUrl");
	}

	public static String getDefaultEegPlSignalmlWsURL() {
		return getString("eeg.pl.signalmlwsUrl");
	}

}
