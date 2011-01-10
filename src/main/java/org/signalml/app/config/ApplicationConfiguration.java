/* ApplicationConfiguration.java created 2007-09-19
 *
 */

package org.signalml.app.config;

import javax.swing.ToolTipManager;

import org.signalml.app.method.artifact.ArtifactConfiguration;
import org.signalml.app.method.stager.StagerConfiguration;
import org.signalml.app.view.book.WignerMapPalette;
import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.domain.book.WignerMapScaleType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ApplicationConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("application")
public class ApplicationConfiguration extends AbstractXMLConfiguration {

	private boolean dontShowDynamicCompilationWarning;

	private String lastOpenTagPath;
	private String lastImportTagPath;
	private String lastExportTagPath;
	private String lastExportSignalPath;
	private String lastExportBookPath;
	private String lastOpenDocumentPath;
	private String lastSaveDocumentPath;
	private String lastConsoleSaveAsTextPath;
	private String lastTableSaveAsTextPath;
	private String lastSamplesSaveAsTextPath;
	private String lastSamplesSaveAsFloatPath;
	private String lastChartSaveAsPngPath;
	private String lastPresetPath;
	private String lastArtifactProjectPath;
	private String lastSaveMP5ConfigPath;
	private String lastBookFilePath;
	private String lastSaveTagPath;
	private String lastExpertTagPath;
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

	private ArtifactConfiguration artifactConfig = new ArtifactConfiguration();
	private StagerConfiguration stagerConfig = new StagerConfiguration();

	private boolean disableSeriousWarnings;

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
	
	private String multiplexerAddress;
	private int multiplexerPort;

	private float  monitorPageSize;
	
	private String signalRecorderFileName;

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

	public String getLastOpenTagPath() {
		return lastOpenTagPath;
	}

	public String getLastImportTagPath() {
		return lastImportTagPath;
	}

	public void setLastImportTagPath(String lastImportTagPath) {
		this.lastImportTagPath = lastImportTagPath;
	}

	public String getLastExportTagPath() {
		return lastExportTagPath;
	}

	public void setLastExportTagPath(String lastExportTagPath) {
		this.lastExportTagPath = lastExportTagPath;
	}

	public void setLastOpenTagPath(String lastOpenTagPath) {
		this.lastOpenTagPath = lastOpenTagPath;
	}

	public String getLastOpenDocumentPath() {
		return lastOpenDocumentPath;
	}

	public void setLastOpenDocumentPath(String lastOpenDocumentPath) {
		this.lastOpenDocumentPath = lastOpenDocumentPath;
	}

	public String getLastSaveDocumentPath() {
		return lastSaveDocumentPath;
	}

	public void setLastSaveDocumentPath(String lastSaveDocumentPath) {
		this.lastSaveDocumentPath = lastSaveDocumentPath;
	}

	public String getLastConsoleSaveAsTextPath() {
		return lastConsoleSaveAsTextPath;
	}

	public void setLastConsoleSaveAsTextPath(String lastConsoleSaveAsTextPath) {
		this.lastConsoleSaveAsTextPath = lastConsoleSaveAsTextPath;
	}

	public String getLastTableSaveAsTextPath() {
		return lastTableSaveAsTextPath;
	}

	public void setLastTableSaveAsTextPath(String lastTableSaveAsTextPath) {
		this.lastTableSaveAsTextPath = lastTableSaveAsTextPath;
	}

	public String getLastPresetPath() {
		return lastPresetPath;
	}

	public void setLastPresetPath(String lastPresetPath) {
		this.lastPresetPath = lastPresetPath;
	}

	public String getLastArtifactProjectPath() {
		return lastArtifactProjectPath;
	}

	public void setLastArtifactProjectPath(String lastArtifactProjectPath) {
		this.lastArtifactProjectPath = lastArtifactProjectPath;
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

	public String getLastChartSaveAsPngPath() {
		return lastChartSaveAsPngPath;
	}

	public void setLastChartSaveAsPngPath(String lastChartSaveAsPngPath) {
		this.lastChartSaveAsPngPath = lastChartSaveAsPngPath;
	}

	public String getLastSamplesSaveAsTextPath() {
		return lastSamplesSaveAsTextPath;
	}

	public void setLastSamplesSaveAsTextPath(String lastSamplesSaveAsTextPath) {
		this.lastSamplesSaveAsTextPath = lastSamplesSaveAsTextPath;
	}

	public String getLastSamplesSaveAsFloatPath() {
		return lastSamplesSaveAsFloatPath;
	}

	public void setLastSamplesSaveAsFloatPath(String lastSamplesSaveAsFloatPath) {
		this.lastSamplesSaveAsFloatPath = lastSamplesSaveAsFloatPath;
	}

	public String getLastExportSignalPath() {
		return lastExportSignalPath;
	}

	public void setLastExportSignalPath(String lastExportSignalPath) {
		this.lastExportSignalPath = lastExportSignalPath;
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

	public ArtifactConfiguration getArtifactConfig() {
		return artifactConfig;
	}

	public void setArtifactConfig(ArtifactConfiguration artifactConfig) {
		this.artifactConfig = artifactConfig;
	}

	public StagerConfiguration getStagerConfig() {
		return stagerConfig;
	}

	public void setStagerConfig(StagerConfiguration stagerConfig) {
		this.stagerConfig = stagerConfig;
	}

	public boolean isDisableSeriousWarnings() {
		return disableSeriousWarnings;
	}

	public void setDisableSeriousWarnings(boolean disableSeriousWarnings) {
		this.disableSeriousWarnings = disableSeriousWarnings;
	}

	public String getLastBookFilePath() {
		return lastBookFilePath;
	}

	public void setLastBookFilePath(String lastBookFilePath) {
		this.lastBookFilePath = lastBookFilePath;
	}

	public String getLastSaveTagPath() {
		return lastSaveTagPath;
	}

	public void setLastSaveTagPath(String lastSaveTagPath) {
		this.lastSaveTagPath = lastSaveTagPath;
	}

	public String getLastExpertTagPath() {
		return lastExpertTagPath;
	}

	public void setLastExpertTagPath(String lastExpertTagPath) {
		this.lastExpertTagPath = lastExpertTagPath;
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

	public String getLastExportBookPath() {
		return lastExportBookPath;
	}

	public void setLastExportBookPath(String lastExportBookPath) {
		this.lastExportBookPath = lastExportBookPath;
	}

	public void setMultiplexerAddress(String multiplexerAddress) {
		this.multiplexerAddress = multiplexerAddress;
	}

	public String getMultiplexerAddress() {
		return multiplexerAddress;
	}

	public void setMultiplexerPort(int multiplexerPort) {
		this.multiplexerPort = multiplexerPort;
	}

	public int getMultiplexerPort() {
		return multiplexerPort;
	}

	public double getMonitorPageSize() {
		return monitorPageSize;
	}

	public void setMonitorPageSize(float monitorPageSize) {
		this.monitorPageSize = monitorPageSize;
	}

	public String getSignalRecorderFileName() {
		return signalRecorderFileName;
	}

	public void setSignalRecorderFileName(String signalRecorderFileName) {
		this.signalRecorderFileName = signalRecorderFileName;
	}
}
