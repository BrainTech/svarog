/* ApplicationConfiguration.java created 2007-09-19
 *
 */

package org.signalml.app.config;

import java.io.File;
import javax.swing.ToolTipManager;

import org.signalml.app.view.book.WignerMapPalette;
import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.domain.book.WignerMapScaleType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ApplicationConfiguration
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author Stanislaw Findeisen
 */
@XStreamAlias("application")
public class ApplicationConfiguration extends AbstractXMLConfiguration implements org.signalml.plugin.export.config.SvarogConfiguration {

	private boolean dontShowDynamicCompilationWarning;

	private String[] favouriteDirs;
	private String[] lastDirs;
	
	private String lastFileChooserPath;
	private String lastPresetPath;
	private String lastSaveMP5ConfigPath;
	private String lastLibraryPath;

	private boolean rightClickPagesForward;
	private boolean autoLoadDefaultMontage;
	private boolean precalculateSignalChecksums;

	private boolean saveConfigOnEveryChange;
	private boolean restoreWorkspace;

	private boolean antialiased;
	private boolean clamped;
	private boolean offscreenChannelsDrawn;
	private boolean tagToolTipsVisible;

	private boolean pageLinesVisible;
	private boolean blockLinesVisible;
	private boolean channelLinesVisible;

	private TagPaintMode tagPaintMode;
	private SignalColor signalColor;
	private boolean signalXOR;

	private float pageSize;
	private int blocksPerPage;
	private boolean saveFullMontageWithTag;

	private boolean viewModeHidesMainToolBar;
	private boolean viewModeHidesLeftPanel;
	private boolean viewModeHidesBottomPanel;
	private boolean viewModeCompactsPageTagBars;
	private boolean viewModeSnapsToPage;

	private int minChannelHeight;
	private int maxChannelHeight;
	private int minValueScale;
	private int maxValueScale;
	private double minTimeScale;
	private double maxTimeScale;

	// milliseconds
	private int toolTipInitialDelay;
	private int toolTipDismissDelay;

	private ZoomSignalSettings zoomSignalSettings = new ZoomSignalSettings();

	private WignerMapPalette palette;
	private WignerMapScaleType scaleType;

	private boolean signalAntialiased;
	private boolean originalSignalVisible;
	private boolean fullReconstructionVisible;
	private boolean reconstructionVisible;
	private boolean legendVisible;
	private boolean scaleVisible;
	private boolean axesVisible;
	private boolean atomToolTipsVisible;

	private int mapAspectRatioUp = 1;
	private int mapAspectRatioDown = 1;

	private int reconstructionHeight;

	private float backupFrequency;

	private String openbciIPAddress;
	private int openbciPort;

	private float monitorPageSize;

	public void applySystemSettings() {

		// apply tooltip settings
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.setInitialDelay(toolTipInitialDelay);
		toolTipManager.setDismissDelay(toolTipDismissDelay);

	}

	@Override
	public String getStandardFilename() {
		return "application-config.xml";
	}

	public String[] getFavouriteDirs() {
		return favouriteDirs;
	}

	public void setFavouriteDirs(String[] dirs) {
		this.favouriteDirs = dirs;
	}

	public float getBackupFrequency() {
		return backupFrequency;
	}

	public void setBackupFrequency(float backupFrequency) {
		this.backupFrequency = backupFrequency;
	}

	public String[] getLastDirs() {
		return lastDirs;
	}

	public void setLastDirs(String[] dirs) {
		this.lastDirs = dirs;
	}

	public String getLastSaveMP5ConfigPath() {
		return lastSaveMP5ConfigPath;
	}

	public void setLastSaveMP5ConfigPath(String lastSaveConfigPath) {
		this.lastSaveMP5ConfigPath = lastSaveConfigPath;
	}

	public boolean isRightClickPagesForward() {
		return rightClickPagesForward;
	}

	public void setRightClickPagesForward(boolean rightClickPagesForward) {
		this.rightClickPagesForward = rightClickPagesForward;
	}

	public boolean isAutoLoadDefaultMontage() {
		return autoLoadDefaultMontage;
	}

	public void setAutoLoadDefaultMontage(boolean autoLoadDefaultMontage) {
		this.autoLoadDefaultMontage = autoLoadDefaultMontage;
	}

	public boolean isPrecalculateSignalChecksums() {
		return precalculateSignalChecksums;
	}

	public void setPrecalculateSignalChecksums(boolean precalculateSignalChecksums) {
		this.precalculateSignalChecksums = precalculateSignalChecksums;
	}

	public boolean isSaveConfigOnEveryChange() {
		return saveConfigOnEveryChange;
	}

	public void setSaveConfigOnEveryChange(boolean saveConfigOnEveryChange) {
		this.saveConfigOnEveryChange = saveConfigOnEveryChange;
	}

	public boolean isAntialiased() {
		return antialiased;
	}

	public void setAntialiased(boolean antialiased) {
		this.antialiased = antialiased;
	}

	public boolean isClamped() {
		return clamped;
	}

	public void setClamped(boolean clamped) {
		this.clamped = clamped;
	}

	public boolean isOffscreenChannelsDrawn() {
		return offscreenChannelsDrawn;
	}

	public void setOffscreenChannelsDrawn(boolean offscreenChannelsDrawn) {
		this.offscreenChannelsDrawn = offscreenChannelsDrawn;
	}

	public boolean isTagToolTipsVisible() {
		return tagToolTipsVisible;
	}

	public void setTagToolTipsVisible(boolean tagToolTipsVisible) {
		this.tagToolTipsVisible = tagToolTipsVisible;
	}

	public boolean isPageLinesVisible() {
		return pageLinesVisible;
	}

	public void setPageLinesVisible(boolean pageLinesVisible) {
		this.pageLinesVisible = pageLinesVisible;
	}

	public boolean isBlockLinesVisible() {
		return blockLinesVisible;
	}

	public void setBlockLinesVisible(boolean blockLinesVisible) {
		this.blockLinesVisible = blockLinesVisible;
	}

	public boolean isChannelLinesVisible() {
		return channelLinesVisible;
	}

	public void setChannelLinesVisible(boolean channelLinesVisible) {
		this.channelLinesVisible = channelLinesVisible;
	}

	public TagPaintMode getTagPaintMode() {
		return tagPaintMode;
	}

	public void setTagPaintMode(TagPaintMode tagPaintMode) {
		this.tagPaintMode = tagPaintMode;
	}

	public SignalColor getSignalColor() {
		return signalColor;
	}

	public void setSignalColor(SignalColor signalColor) {
		this.signalColor = signalColor;
	}

	public boolean isSignalXOR() {
		return signalXOR;
	}

	public void setSignalXOR(boolean signalXOR) {
		this.signalXOR = signalXOR;
	}

	public float getPageSize() {
		return pageSize;
	}

	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}

	public int getBlocksPerPage() {
		return blocksPerPage;
	}

	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	public boolean isRestoreWorkspace() {
		return restoreWorkspace;
	}

	public void setRestoreWorkspace(boolean restoreWorkspace) {
		this.restoreWorkspace = restoreWorkspace;
	}

	public boolean isSaveFullMontageWithTag() {
		return saveFullMontageWithTag;
	}

	public void setSaveFullMontageWithTag(boolean saveFullMontageWithTag) {
		this.saveFullMontageWithTag = saveFullMontageWithTag;
	}

	public boolean isViewModeHidesMainToolBar() {
		return viewModeHidesMainToolBar;
	}

	public void setViewModeHidesMainToolBar(boolean viewModeHidesMainToolBar) {
		this.viewModeHidesMainToolBar = viewModeHidesMainToolBar;
	}

	public boolean isViewModeHidesLeftPanel() {
		return viewModeHidesLeftPanel;
	}

	public void setViewModeHidesLeftPanel(boolean viewModeHidesLeftPanel) {
		this.viewModeHidesLeftPanel = viewModeHidesLeftPanel;
	}

	public boolean isViewModeHidesBottomPanel() {
		return viewModeHidesBottomPanel;
	}

	public void setViewModeHidesBottomPanel(boolean viewModeHidesBottomPanel) {
		this.viewModeHidesBottomPanel = viewModeHidesBottomPanel;
	}

	public boolean isViewModeCompactsPageTagBars() {
		return viewModeCompactsPageTagBars;
	}

	public void setViewModeCompactsPageTagBars(boolean viewModeCompactsPageTagBars) {
		this.viewModeCompactsPageTagBars = viewModeCompactsPageTagBars;
	}

	public boolean isViewModeSnapsToPage() {
		return viewModeSnapsToPage;
	}

	public void setViewModeSnapsToPage(boolean viewModeSnapsToPage) {
		this.viewModeSnapsToPage = viewModeSnapsToPage;
	}

	public int getToolTipInitialDelay() {
		return toolTipInitialDelay;
	}

	public void setToolTipInitialDelay(int toolTipInitialDelay) {
		this.toolTipInitialDelay = toolTipInitialDelay;
	}

	public int getToolTipDismissDelay() {
		return toolTipDismissDelay;
	}

	public void setToolTipDismissDelay(int toolTipDismissDelay) {
		this.toolTipDismissDelay = toolTipDismissDelay;
	}

	public ZoomSignalSettings getZoomSignalSettings() {
		return zoomSignalSettings;
	}

	public void setZoomSignalSettings(ZoomSignalSettings zoomSignalSettings) {
		this.zoomSignalSettings = zoomSignalSettings;
	}

	public int getMinChannelHeight() {
		return minChannelHeight;
	}

	public void setMinChannelHeight(int minChannelHeight) {
		this.minChannelHeight = minChannelHeight;
	}

	public int getMaxChannelHeight() {
		return maxChannelHeight;
	}

	public void setMaxChannelHeight(int maxChannelHeight) {
		this.maxChannelHeight = maxChannelHeight;
	}

	public int getMinValueScale() {
		return minValueScale;
	}

	public void setMinValueScale(int minValueScale) {
		this.minValueScale = minValueScale;
	}

	public int getMaxValueScale() {
		return maxValueScale;
	}

	public void setMaxValueScale(int maxValueScale) {
		this.maxValueScale = maxValueScale;
	}

	public double getMinTimeScale() {
		return minTimeScale;
	}

	public void setMinTimeScale(double minTimeScale) {
		this.minTimeScale = minTimeScale;
	}

	public double getMaxTimeScale() {
		return maxTimeScale;
	}

	public void setMaxTimeScale(double maxTimeScale) {
		this.maxTimeScale = maxTimeScale;
	}

	public boolean isDontShowDynamicCompilationWarning() {
		return dontShowDynamicCompilationWarning;
	}

	public void setDontShowDynamicCompilationWarning(boolean dontShowDynamicCompilationWarning) {
		this.dontShowDynamicCompilationWarning = dontShowDynamicCompilationWarning;
	}

	public String getLastLibraryPath() {
		return lastLibraryPath;
	}

	public void setLastLibraryPath(String lastLibraryPath) {
		this.lastLibraryPath = lastLibraryPath;
	}

	public WignerMapPalette getPalette() {
		return palette;
	}

	public void setPalette(WignerMapPalette palette) {
		this.palette = palette;
	}

	public WignerMapScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(WignerMapScaleType scaleType) {
		this.scaleType = scaleType;
	}

	public boolean isSignalAntialiased() {
		return signalAntialiased;
	}

	public void setSignalAntialiased(boolean signalAntialiased) {
		this.signalAntialiased = signalAntialiased;
	}

	public boolean isOriginalSignalVisible() {
		return originalSignalVisible;
	}

	public void setOriginalSignalVisible(boolean originalSignalVisible) {
		this.originalSignalVisible = originalSignalVisible;
	}

	public boolean isFullReconstructionVisible() {
		return fullReconstructionVisible;
	}

	public void setFullReconstructionVisible(boolean fullReconstructionVisible) {
		this.fullReconstructionVisible = fullReconstructionVisible;
	}

	public boolean isReconstructionVisible() {
		return reconstructionVisible;
	}

	public void setReconstructionVisible(boolean reconstructionVisible) {
		this.reconstructionVisible = reconstructionVisible;
	}

	public boolean isLegendVisible() {
		return legendVisible;
	}

	public void setLegendVisible(boolean legendVisible) {
		this.legendVisible = legendVisible;
	}

	public boolean isScaleVisible() {
		return scaleVisible;
	}

	public void setScaleVisible(boolean scaleVisible) {
		this.scaleVisible = scaleVisible;
	}

	public boolean isAxesVisible() {
		return axesVisible;
	}

	public void setAxesVisible(boolean axesVisible) {
		this.axesVisible = axesVisible;
	}

	public boolean isAtomToolTipsVisible() {
		return atomToolTipsVisible;
	}

	public void setAtomToolTipsVisible(boolean atomToolTipsVisible) {
		this.atomToolTipsVisible = atomToolTipsVisible;
	}

	public int getMapAspectRatioUp() {
		return mapAspectRatioUp;
	}

	public void setMapAspectRatioUp(int mapAspectRatioUp) {
		this.mapAspectRatioUp = mapAspectRatioUp;
	}

	public int getMapAspectRatioDown() {
		return mapAspectRatioDown;
	}

	public void setMapAspectRatioDown(int mapAspectRatioDown) {
		this.mapAspectRatioDown = mapAspectRatioDown;
	}

	public int getReconstructionHeight() {
		return reconstructionHeight;
	}

	public void setReconstructionHeight(int reconstructionHeight) {
		this.reconstructionHeight = reconstructionHeight;
	}

	public float getMonitorPageSize() {
		return monitorPageSize;
	}

	public void setMonitorPageSize(float monitorPageSize) {
		this.monitorPageSize = monitorPageSize;
	}

	/**
	 * Query configuration of various paths by name.
	 * @return configuration value or null
	 * @param name name is the same as function name without get
	 *
	 */
	public String getPath(String name){
		if ("LastPresetPath".equals(name))
			return this.getLastPresetPath();
		else if ("LastLibraryPath".equals(name))
			return this.getLastLibraryPath();
		return this.getLastFileChooserPath();
	}

	/**
	 * Set configuration value of paths by name.
	 * @return configuration value or null
	 * @param name name is the same as function name without set
	 *
	 */
	public void setPath(String name, String path){
		if ("LastPresetPath".equals(name))
			this.setLastPresetPath(path);
		else if("LastLibraryPath".equals(name))
			this.setLastLibraryPath(path);
		else
			this.setLastFileChooserPath(path);
	}

	public String getLastFileChooserPath() {
		if (lastFileChooserPath == null)
			lastFileChooserPath = System.getProperty("user.dir");
		return lastFileChooserPath;
	}

	public void setLastFileChooserPath(String lastFileChooserPath) {
		this.lastFileChooserPath = lastFileChooserPath;
	}

	public String getLastPresetPath() {
		return lastPresetPath;
	}

	public void setLastPresetPath(String lastPresetPath) {
		this.lastPresetPath = lastPresetPath;
	}

	public String getOpenbciIPAddress() {
		return openbciIPAddress;
	}

	public void setOpenbciIPAddress(String openbciIPAddress) {
		this.openbciIPAddress = openbciIPAddress;
	}

	public int getOpenbciPort() {
		return openbciPort;
	}

	public void setOpenbciPort(int openbciPort) {
		this.openbciPort = openbciPort;
	}

}
