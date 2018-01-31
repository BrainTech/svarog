/* SignalView.java created 2007-09-19
 *
 */

package org.signalml.app.view.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.action.DisplayClockTimeAction;
import org.signalml.app.action.SnapToPageAction;
import org.signalml.app.action.document.monitor.StartMonitorRecordingAction;
import org.signalml.app.action.document.monitor.StopMonitorRecordingAction;
import org.signalml.app.action.document.monitor.StartVideoPreviewAction;
import org.signalml.app.action.montage.ApplyDefaultMontageAction;
import org.signalml.app.action.montage.EditSignalMontageAction;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.MontageFocusSelector;
import org.signalml.app.action.selector.SignalPlotFocusSelector;
import org.signalml.app.action.selector.TagFocusSelector;
import org.signalml.app.action.selector.TagStyleFocusSelector;
import org.signalml.app.action.signal.EditSignalParametersAction;
import org.signalml.app.action.signal.PreciseSelectionAction;
import org.signalml.app.action.signal.SignalFilterSwitchAction;
import org.signalml.app.action.tag.CloseTagAction;
import org.signalml.app.action.tag.EditTagAnnotationAction;
import org.signalml.app.action.tag.NewTagAction;
import org.signalml.app.action.tag.OpenTagAction;
import org.signalml.app.action.tag.RemoveTagAction;
import org.signalml.app.action.tag.SaveTagAction;
import org.signalml.app.action.tag.SaveTagAsAction;
import org.signalml.app.action.tag.TagSelectionAction;
import org.signalml.app.action.video.PlayPauseVideoAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.PresetManagerAdapter;
import org.signalml.app.config.preset.PresetManagerEvent;
import org.signalml.app.config.preset.PresetManagerListener;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.RawSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.components.LogarithmicJSlider;
import org.signalml.app.model.montage.MontagePresetManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.ResnapToPageRunnable;
import org.signalml.app.video.OfflineVideoFrame;
import org.signalml.app.view.common.components.LockableJSplitPane;
import org.signalml.app.view.common.components.panels.TitledSliderPanel;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.document.monitor.MonitorRecordingDurationPanel;
import org.signalml.app.view.document.monitor.StartMonitorRecordingDialog;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.app.view.signal.popup.ChannelOptionsPopupDialog;
import org.signalml.app.view.signal.popup.SignalPlotOptionsPopupDialog;
import org.signalml.app.view.signal.popup.SlavePlotSettingsPopupDialog;
import org.signalml.app.view.signal.popup.ZoomSettingsPopupDialog;
import org.signalml.app.view.tag.EditTagAnnotationDialog;
import org.signalml.app.view.tag.EditTagDescriptionDialog;
import org.signalml.app.view.tag.NewTagDialog;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.app.view.tag.TagStylePaletteDialog;
import org.signalml.app.view.tag.TagStyleSelector;
import org.signalml.app.view.tag.TagStyleToolBar;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.tag.MonitorTag;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagDifference;
import org.signalml.domain.tag.TagDifferenceDetector;
import org.signalml.domain.tag.TagDifferenceSet;
import org.signalml.domain.tag.TagEvent;
import org.signalml.domain.tag.TagListener;
import org.signalml.domain.tag.TagStyleEvent;
import org.signalml.domain.tag.TagStyleListener;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.export.view.ExportedPositionedTag;
import org.signalml.plugin.export.view.ExportedSignalPlot;
import org.signalml.plugin.export.view.ExportedSignalView;
import org.signalml.plugin.impl.PluginAccessClass;
import org.signalml.psychopy.view.PsychopyExperimentDialog;
import org.signalml.psychopy.action.ShowPsychopyDialogButton;
import org.signalml.util.Util;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/** SignalView
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalView extends DocumentView implements PropertyChangeListener, TagListener, TagStyleListener, TagFocusSelector, TagStyleFocusSelector, SignalPlotFocusSelector, MontageFocusSelector, ExportedSignalView {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalView.class);

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);

	private boolean closed = false;

	private ApplicationConfiguration applicationConfig;

	private JToolBar mainToolBar;
	private JToolBar tagToolBar;

	private SignalDocument document;
	private LinkedList<SignalPlot> plots;
	private LinkedList<SignalPlotPanel> plotPanels;
	private LinkedList<SignalPlotScrollPane> scrollPanes;
	private LinkedList<SignalPlotColumnHeader> columnHeaders;
	private LinkedList<SignalPlotRowHeader> rowHeaders;
	private LinkedList<SignalPlotCorner> corners;

	private LockableJSplitPane plotSplitPane;
	private JPanel contentPane;

	private FocusListener plotFocusListener;
	private MouseListener plotActivationMouseListener;
	private SignalPlot activePlot = null;

	private SignalSelection signalSelection;
	private PositionedTag tagSelection;

	private SignalPlot signalSelectionPlot;
	private SignalPlot tagSelectionPlot;

	private PlotScrollingCoordinator scrollingCoordinator;

	private JSlider timeScaleSlider;
	private JSlider valueScaleSlider;
	private JSlider channelHeightSlider;
	private ButtonGroup toolButtonGroup;

	private JButton plotOptionsButton;

	private JToggleButton selectToolButton;
	private JToggleButton moveToolButton;
	private JToggleButton selectPageToolButton;
	private JToggleButton selectBlockToolButton;
	private JToggleButton selectChannelToolButton;
	private JToggleButton rulerToolButton;
	private JToggleButton tagPageToolButton;
	private JToggleButton tagBlockToolButton;
	private JToggleButton tagChannelToolButton;
	private JToggleButton zoomSignalToolButton;

	private SignalTool currentSignalTool;

	private SelectTagSignalTool selectTagTool;
	private MoveSignalSignalTool moveSignalTool;
	private SelectPageSignalTool selectPageTool;
	private SelectBlockSignalTool selectBlockTool;
	private SelectChannelSignalTool selectChannelTool;
	private RulerSignalTool rulerSignalTool;
	private TagPageSignalTool tagPageSignalTool;
	private TagBlockSignalTool tagBlockSignalTool;
	private TagChannelSignalTool tagChannelSignalTool;
	private ZoomSignalTool zoomSignalTool;

	private Map<ButtonModel,SignalTool> toolMap = new HashMap<ButtonModel,SignalTool>();

	private DocumentFlowIntegrator documentFlowIntegrator;
	private NewTagDialog newTagDialog;
	private ViewerFileChooser fileChooser;
	private SignalSelectionDialog signalSelectionDialog;
	private PsychopyExperimentDialog psychopyExperimentDialog;
	private StartMonitorRecordingDialog startMonitorRecordingDialog;
	private SignalParametersDialog signalParametersDialog;
	private SignalMontageDialog signalMontageDialog;
	private EditTagAnnotationDialog editTagAnnotationDialog;
	private TagStylePaletteDialog tagStylePaletteDialog;
	private EditTagDescriptionDialog editTagDescriptionDialog;

	private NewTagAction newTagAction;
	private OpenTagAction openTagAction;
	private CloseTagAction closeTagAction;
	private SaveTagAction saveTagAction;
	private SaveTagAsAction saveTagAsAction;

	private ShowPsychopyDialogButton showPsychopyDialogButtonAction;
	/**
	 * An {@link Action} responsible for starting a monitor recording.
	 */
	private StartMonitorRecordingAction startMonitorRecordingAction;

	/**
	 * An {@link Action} responsible for stopping an ongoing monitor
	 * recording.
	 */
	private StopMonitorRecordingAction stopMonitorRecordingAction;

	private StartVideoPreviewAction startVideoPreviewAction;
	private MonitorRecordingDurationPanel monitorRecordingDurationPanel;

	private EditSignalParametersAction editSignalParametersAction;
	private EditSignalMontageAction editSignalMontageAction;
	private ApplyDefaultMontageAction applyDefaultMontageAction;
	private PreciseSelectionAction preciseSelectionAction;
	private TagSelectionAction tagSelectionAction;
	private RemoveTagAction removeTagAction;
	private DisplayClockTimeAction displayClockTimeAction;
	private EditTagAnnotationAction editTagAnnotationAction;
	private SnapToPageAction snapToPageAction;
	private SignalFilterSwitchAction signalFilterSwitchAction;

	private PlayPauseVideoAction playPauseVideoAction;

	private boolean displayClockTime = false;
	private boolean snapToPageMode = false;
	private boolean deferredSnapToPage = false;

	private MontagePresetManager montagePresetManager;
	private PresetManagerListener montagePresetManagerListener;

	private SignalPlotOptionsPopupDialog signalPlotOptionsPopupDialog;
	private ZoomSettingsPopupDialog zoomSettingsDialog;
	private SlavePlotSettingsPopupDialog slavePlotSettingsPopupDialog;
	private ChannelOptionsPopupDialog channelOptionsPopupDialog;

	private CardLayout tagToolBarLayout;
	private JPanel tagToolBarPanel;

	private Map<String,TagStyleToolBar> styleToolBarMap;

	private ActionFocusManager actionFocusManager;
	private TagIconProducer tagIconProducer;

	private HypnogramPlot hypnogramPlot = null;

//	private ZoomMouseWheelListener zoomMouseWheelListener;
	private SignalToolForwardingMouseAdapter toolMouseAdapter;
	private SignalToolForwardingMouseAdapter columnToolMouseAdapter;
	private SignalToolForwardingMouseAdapter rowToolMouseAdapter;

	private GridLayout plotPanelGridLayout;
	private JPanel plotPanel;

	private TagDifferenceDetector tagDifferenceDetector;
	private TagDocument[] comparedTags;
	private TagDifferenceSet differenceSet;

	private HashMap<KeyStroke,TagStyle> lastStylesByKeyStrokes;

	/**
	 * Internal listener, connected to the VideoFrame.
	 */
	private class VideoFrameListener extends MediaPlayerEventAdapter {

		private void scheduleColumnHeadersUpdate(long milliseconds) {
			final double time = 0.001 * milliseconds + getDocumentVideoOffset();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (SignalPlotColumnHeader columnHeader : columnHeaders) {
						columnHeader.setVideoMarkerTime(time);
					}
					SignalPlot plot = activePlot;
					if (plot != null) {
						int videoMarkerX = (int) Math.round(time * plot.getPixelPerSecond());
						if (plot.getViewport().getViewRect().getMaxX() <= videoMarkerX) {
							plot.pageForward();
						}
					}
				}
			});
		}

		@Override
		public void timeChanged(MediaPlayer mp, long milliseconds) {
			scheduleColumnHeadersUpdate(milliseconds);
		}

		@Override
		public void finished(MediaPlayer mp) {
			scheduleColumnHeadersUpdate(mp.getLength());
		}
	}

	public SignalView(SignalDocument document) {
		super(new BorderLayout());
		this.document = document;
		document.addPropertyChangeListener(this);

		if (document instanceof MonitorSignalDocument) {
			// for user tags in monitor mode
			StyledTagSet tagSet = document.getActiveTag().getTagSet();
			tagSet.addTagListener(this);
			tagSet.addTagStyleListener(this);
		}
	}

	public void initialize() throws SignalMLException {

		tagIconProducer = new TagIconProducer();

		plots = new LinkedList<SignalPlot>();
		plotPanels = new LinkedList<SignalPlotPanel>();
		scrollPanes = new LinkedList<SignalPlotScrollPane>();
		columnHeaders = new LinkedList<SignalPlotColumnHeader>();
		rowHeaders = new LinkedList<SignalPlotRowHeader>();
		corners = new LinkedList<SignalPlotCorner>();

		plotFocusListener = new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				logger.debug("Focus gained by plot [" + e.getSource().toString() + "]");
			}

			@Override
			public void focusLost(FocusEvent e) {
				logger.debug("Focus lost by plot [" + e.getSource().toString() + "]");
			}
		};

		plotActivationMouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				SignalPlot plot = (SignalPlot) e.getSource();
				setActivePlot(plot);
				plot.requestFocusInWindow();
			}
		};

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if (deferredSnapToPage) {
					snapPageToView();
				}
				if (activePlot != null) {
					activePlot.requestFocusInWindow();
				}
			}
		});

//		zoomMouseWheelListener = new ZoomMouseWheelListener(this);
		toolMouseAdapter = new SignalToolForwardingMouseAdapter();
		columnToolMouseAdapter = new SignalToolForwardingMouseAdapter(true,false);
		rowToolMouseAdapter = new SignalToolForwardingMouseAdapter(false,true);

		hypnogramPlot = new HypnogramPlot(this);
		document.addPropertyChangeListener(hypnogramPlot);

		plotPanel = new JPanel();
		plotPanelGridLayout = new GridLayout(1,0,0,5);
		plotPanel.setLayout(new PlotPanelLayout());

		contentPane = new JPanel(new BorderLayout());

		plotSplitPane = new LockableJSplitPane(JSplitPane.VERTICAL_SPLIT, true, null, plotPanel);
		plotSplitPane.setResizeWeight(0.5);
		plotSplitPane.setDividerSize(8);
		plotSplitPane.setOneTouchExpandable(false);
		plotSplitPane.setBorder(null);

		SignalPlot masterPlot = createSignalPlot(null);
		masterPlot.getViewport().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (snapToPageMode) {
					SwingUtilities.invokeLater(new ResnapToPageRunnable(SignalView.this));
				}
			}
		});

		scrollingCoordinator = new PlotScrollingCoordinator(plots.getFirst());

		buildMainToolBar();
		buildTagToolBar();

		buildTools();

		JPanel hypnogramPanel = new JPanel(new BorderLayout());
		hypnogramPanel.setBorder(new CompoundBorder(
									 new EmptyBorder(3,0,5,0),
									 new LineBorder(Color.LIGHT_GRAY)
								 ));
		hypnogramPanel.add(hypnogramPlot, BorderLayout.CENTER);

		contentPane.add(hypnogramPanel, BorderLayout.NORTH);

		add(mainToolBar, BorderLayout.NORTH);
		add(tagToolBar, BorderLayout.WEST);
		add(contentPane, BorderLayout.CENTER);

		KeyStroke del = KeyStroke.getKeyStroke("DELETE");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(del, "removeTagOrEraseSelection");
		getActionMap().put("removeTagOrEraseSelection", new DelKeyRedirectAction());

		KeyStroke plus = KeyStroke.getKeyStroke('+');
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(plus, "pageForward");
		getActionMap().put("pageForward", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (activePlot != null) {
					activePlot.pageForward();
				}
			}

		});

		KeyStroke minus = KeyStroke.getKeyStroke('-');
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(minus, "pageBackward");
		getActionMap().put("pageBackward", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (activePlot != null) {
					activePlot.pageBackward();
				}
			}

		});

		// cycle input map to protect it from overwriting default bindings by tag bindings
		InputMap map = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		InputMap topMap = new InputMap();
		topMap.setParent(map);
		setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, topMap);

		// listen to video frame events to update video markers
		OfflineVideoFrame videoFrame = getDocumentVideoFrame();
		if (videoFrame != null) {
			videoFrame.addListener(new VideoFrameListener());
		}
	}

	private SignalPlot createSignalPlot(SignalPlot masterPlot) throws SignalMLException {

		SignalPlot plot = new SignalPlot(document, this, masterPlot);

		SignalPlotPopupProvider signalPlotPopupProvider = new SignalPlotPopupProvider(plot);
		signalPlotPopupProvider.setTagIconProducer(tagIconProducer);
		signalPlotPopupProvider.setPreciseSelectionAction(getPreciseSelectionAction());
		signalPlotPopupProvider.setTagSelectionAction(getTagSelectionAction());
		signalPlotPopupProvider.setRemoveTagAction(getRemoveTagAction());
		signalPlotPopupProvider.setEditTagAnnotationAction(getEditTagAnnotationAction());

		plot.setPopupMenuProvider(signalPlotPopupProvider);

		plot.initialize();

		plots.add(plot);

		plot.addMouseListener(plotActivationMouseListener);

		SignalPlotScrollPane scrollPane = new SignalPlotScrollPane(plot,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		if (document.getTagDocuments().size() > 1) {
			scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		} else {
			scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		}
		plot.setViewport(scrollPane.getViewport());

		SignalPlotColumnHeader columnHeader = plot.getSignalPlotColumnHeader();
		SignalPlotRowHeader rowHeader = plot.getSignalPlotRowHeader();
		SignalPlotCorner corner = plot.getSignalPlotCorner();

		scrollPane.setColumnHeaderView(columnHeader);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);
		scrollPane.setWheelScrollingEnabled(false);
		scrollPane.setMinimumSize(new Dimension(200,200));

		columnHeader.setVideoMarkerTime(getDocumentVideoOffset());
		columnHeader.setListener(new SignalPlotColumnHeaderListener() {
			@Override
			public void timeSelected(double time) {
				OfflineVideoFrame videoFrame = getDocumentVideoFrame();
				if (videoFrame != null) {
					// accounting for video offset
					time -= getDocumentVideoOffset();
					// converting to milliseconds
					videoFrame.setTime( (int) Math.round(1000*time) );
				}
			}
		});

		scrollPanes.add(scrollPane);
		columnHeaders.add(columnHeader);
		rowHeaders.add(rowHeader);
		corners.add(corner);

//		plot.addMouseWheelListener(zoomMouseWheelListener);

		plot.addMouseMotionListener(toolMouseAdapter);
		plot.addMouseListener(toolMouseAdapter);
		plot.addMouseWheelListener(toolMouseAdapter);

		columnHeader.addMouseMotionListener(columnToolMouseAdapter);
		columnHeader.addMouseListener(columnToolMouseAdapter);
		columnHeader.addMouseWheelListener(columnToolMouseAdapter);

		rowHeader.addMouseMotionListener(rowToolMouseAdapter);
		rowHeader.addMouseListener(rowToolMouseAdapter);
		rowHeader.addMouseWheelListener(rowToolMouseAdapter);

		plot.setAutoscrolls(true);

		SignalPlotPanel signalPlotPanel = new SignalPlotPanel(plot,scrollPane);
		plotPanels.add(signalPlotPanel);

		if (masterPlot == null) {

			plot.getViewport().addChangeListener(hypnogramPlot);

			contentPane.add(scrollPane, BorderLayout.CENTER);

		} else {

			scrollingCoordinator.addPlot(plot);

			plotPanelGridLayout.setRows(plots.size()-1);
			plotPanel.add(signalPlotPanel);

			if (plots.size() <= 2) {
				// if this is the first slave plot
				contentPane.remove(scrollPanes.getFirst());
				plotSplitPane.setLeftComponent(scrollPanes.getFirst());
				contentPane.add(plotSplitPane, BorderLayout.CENTER);
			} // else is not needed

		}

		plot.addFocusListener(plotFocusListener);

		setActivePlot(plot);
		plot.requestFocusInWindow();

		return plot;

	}

	public SignalPlot addSlavePlot(SignalPlot masterPlot) {

		SignalPlot plot;
		try {
			plot = createSignalPlot(masterPlot);
		} catch (SignalMLException ex) {
			logger.error("Failed to create signal plot", ex);
			Dialogs.showExceptionDialog((Window) getTopLevelAncestor(), ex);
			return null;
		}

		if (plots.size() >=4) {
			((MasterSignalPlotCorner) corners.getFirst()).setAddSlavePlotEnabled(false);
		} else {
			((MasterSignalPlotCorner) corners.getFirst()).setAddSlavePlotEnabled(true);
		}

		revalidate();
		repaint();

		return plot;

	}

	public void removeSlavePlot(SignalPlot slavePlot) {

		if (!plots.contains(slavePlot)) {
			logger.warn("WARNING: Plot not in plots");
			return;
		}

		int index = plots.indexOf(slavePlot);
		if (index == 0) {
			logger.warn("WARNING: Cannot remove master plot");
			return;
		}

		if (signalSelectionPlot == slavePlot) {
			clearSignalSelection();
		}
		if (tagSelectionPlot == slavePlot) {
			clearTagSelection();
		}

		scrollingCoordinator.removePlot(slavePlot);

		plots.remove(index);
		plotPanels.remove(index);
		scrollPanes.remove(index);
		columnHeaders.remove(index);
		rowHeaders.remove(index);
		corners.remove(index);

		plotPanel.remove(index-1);
		plotPanelGridLayout.setRows(Math.max(1, plots.size()-1));

		if (plots.size() == 1) {
			contentPane.remove(plotSplitPane);
			plotSplitPane.setLeftComponent(null);
			contentPane.add(scrollPanes.getFirst(), BorderLayout.CENTER);
		}

		if (slavePlot == activePlot) {
			setActivePlot(getMasterPlot());
		}

		slavePlot.destroy();

		if (plots.size() >=4) {
			((MasterSignalPlotCorner) corners.getFirst()).setAddSlavePlotEnabled(false);
		} else {
			((MasterSignalPlotCorner) corners.getFirst()).setAddSlavePlotEnabled(true);
		}

		revalidate();
		repaint();

	}

	public int getSynchronizedRowHeaderWidth() {
		int maxWidth = 0;
		int width;
		for (SignalPlotRowHeader rowHeader : rowHeaders) {
			width = rowHeader.getPreferredWidth();
			if (maxWidth < width) {
				maxWidth = width;
			}
		}
		for (SignalPlotCorner corner : corners) {
			width = corner.getPreferredWidth();
			if (maxWidth < width) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public TagStyle getActiveTagStyle() {
		SignalSelectionType type = getCurrentTagType();
		if (type != null) {
			return getCurrentTagStyle(type);
		}
		return null;
	}

	@Override
	public SignalDocument getActiveSignalDocument() {
		return document;
	}

	@Override
	public TagDocument getActiveTagDocument() {
		return document.getActiveTag();
	}

	@Override
	public Document getActiveDocument() {
		return document;
	}

	@Override
	public PositionedTag getActiveTag() {
		return getTagSelection();
	}

	@Override
	public SignalPlot getActiveSignalPlot() {
		return activePlot;
	}

	@Override
	public Montage getActiveMontage() {
		return actionFocusManager.getActiveMontage();
	}

	public void setActivePlot(SignalPlot activePlot) {
		if (this.activePlot != activePlot) {
			if (this.activePlot != null) {
				this.activePlot.getSignalPlotRowHeader().setActive(false);
			}
			this.activePlot = activePlot;
			if (activePlot != null) {
				activePlot.getSignalPlotRowHeader().setActive(true);
			}
			logger.debug("Plot [" + activePlot.toString() + "] activated");
			afSupport.fireActionFocusChanged();
		}
	}

	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		afSupport.addActionFocusListener(listener);
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		afSupport.removeActionFocusListener(listener);
	}

	@Override
	public SignalSelection getSignalSelection() {
		return signalSelection;
	}

	public SignalPlot getSignalSelectionPlot() {
		return signalSelectionPlot;
	}

	public SignalSelection getSignalSelection(SignalPlot plot) {
		if (signalSelection == null || signalSelectionPlot != plot) {
			return null;
		}
		return signalSelection;
	}

	public void setSignalSelection(SignalPlot plot, SignalSelection signalSelection) {
		if ((plot != this.signalSelectionPlot) || !Util.equalsWithNulls(this.signalSelection, signalSelection)) {
			SignalSelection oldSelection = this.signalSelection;
			SignalPlot oldPlot = this.signalSelectionPlot;
			this.signalSelection = signalSelection;
			this.signalSelectionPlot = plot;
			if (oldSelection != null) {
				oldPlot.repaintSelectionBounds(oldSelection);
			}
			if (signalSelection != null) {
				plot.repaintSelectionBounds(signalSelection);
				clearTagSelection();
			}
			afSupport.fireActionFocusChanged();
		}
	}

	@Override
	public void clearSignalSelection() {
		setSignalSelection(null,null);
	}

	@Override
	public PositionedTag getTagSelection() {
		return tagSelection;
	}

	public SignalPlot getTagSelectionPlot() {
		return tagSelectionPlot;
	}

	public PositionedTag getTagSelection(SignalPlot plot) {
		if (tagSelection == null || tagSelectionPlot != plot) {
			return null;
		}
		return tagSelection;
	}

	public void setTagSelection(SignalPlot plot, PositionedTag tagSelection) {
		if ((plot != this.signalSelectionPlot) || !Util.equalsWithNulls(this.tagSelection, tagSelection)) {
			int tagCnt = document.getTagDocuments().size();
			PositionedTag oldSelection = this.tagSelection;
			SignalPlot oldPlot = this.tagSelectionPlot;
			this.tagSelection = tagSelection;
			this.tagSelectionPlot = plot;
			if (oldSelection != null) {
				// when tagCnt is 0 division by zero inside
				if (tagCnt > 0) oldPlot.repaintTagBounds(oldSelection, tagCnt);
			}
			if (tagSelection != null) {
				// when tagCnt is 0 division by zero inside
				if (tagCnt > 0) plot.repaintTagBounds(tagSelection, tagCnt);
				clearSignalSelection();
			}
			afSupport.fireActionFocusChanged();
		}
	}

	@Override
	public void clearTagSelection() {
		setTagSelection(null, null);
	}

	/**
	 * Return video frame connected with the signal document,
	 * or NULL if no video frame exists.
	 *
	 * @return video frame instance or NULL
	 */
	public OfflineVideoFrame getDocumentVideoFrame() {
		OfflineVideoFrame videoFrame = null;
		if (document instanceof RawSignalDocument) {
			RawSignalDocument rawDocument = (RawSignalDocument) document;
			videoFrame = rawDocument.getVideoFrame();
		}
		return videoFrame;
	}

	/**
	 * Return time offset of video file, relative to the signal's start.
	 *
	 * @return video offset in seconds (0 if unknown)
	 */
	public float getDocumentVideoOffset() {
		float videoOffset = 0;
		if (document instanceof RawSignalDocument) {
			RawSignalDocument rawDocument = (RawSignalDocument) document;
			videoOffset = rawDocument.getVideoOffset();
		}
		return videoOffset;
	}

	public LockableJSplitPane getPlotSplitPane() {
		return plotSplitPane;
	}

	private void buildTools() {

		selectTagTool = new SelectTagSignalTool(this);
		moveSignalTool = new MoveSignalSignalTool(this);
		selectPageTool = new SelectPageSignalTool(this);
		selectBlockTool = new SelectBlockSignalTool(this);
		selectChannelTool = new SelectChannelSignalTool(this);
		rulerSignalTool = new RulerSignalTool(this);
		tagPageSignalTool = new TagPageSignalTool(this);
		tagBlockSignalTool = new TagBlockSignalTool(this);
		tagChannelSignalTool = new TagChannelSignalTool(this);
		zoomSignalTool = new ZoomSignalTool(this);
		zoomSignalTool.setSettings(applicationConfig.getZoomSignalSettings());

		toolMouseAdapter.setSelectTagSignalTool(selectTagTool);
		columnToolMouseAdapter.setSelectTagSignalTool(selectTagTool);

		currentSignalTool = selectTagTool;

		toolMouseAdapter.setSignalTool(currentSignalTool);
		columnToolMouseAdapter.setSignalTool(currentSignalTool);
		rowToolMouseAdapter.setSignalTool(currentSignalTool);

		toolMap.put(selectToolButton.getModel(), selectTagTool);
		toolMap.put(moveToolButton.getModel(), moveSignalTool);
		toolMap.put(selectPageToolButton.getModel(), selectPageTool);
		toolMap.put(selectBlockToolButton.getModel(), selectBlockTool);
		toolMap.put(selectChannelToolButton.getModel(), selectChannelTool);
		toolMap.put(rulerToolButton.getModel(), rulerSignalTool);
		toolMap.put(tagPageToolButton.getModel(), tagPageSignalTool);
		toolMap.put(tagBlockToolButton.getModel(), tagBlockSignalTool);
		toolMap.put(tagChannelToolButton.getModel(), tagChannelSignalTool);
		toolMap.put(zoomSignalToolButton.getModel(), zoomSignalTool);

		toolButtonGroup = new ButtonGroup();
		toolButtonGroup.add(selectToolButton);
		toolButtonGroup.add(moveToolButton);
		toolButtonGroup.add(selectPageToolButton);
		toolButtonGroup.add(selectBlockToolButton);
		toolButtonGroup.add(selectChannelToolButton);
		toolButtonGroup.add(rulerToolButton);
		toolButtonGroup.add(tagPageToolButton);
		toolButtonGroup.add(tagBlockToolButton);
		toolButtonGroup.add(tagChannelToolButton);
		toolButtonGroup.add(zoomSignalToolButton);

		ActionListener toolSelectionListener = new ToolSelectionListener();
		selectToolButton.addActionListener(toolSelectionListener);
		moveToolButton.addActionListener(toolSelectionListener);
		selectPageToolButton.addActionListener(toolSelectionListener);
		selectBlockToolButton.addActionListener(toolSelectionListener);
		selectChannelToolButton.addActionListener(toolSelectionListener);
		rulerToolButton.addActionListener(toolSelectionListener);
		tagPageToolButton.addActionListener(toolSelectionListener);
		tagBlockToolButton.addActionListener(toolSelectionListener);
		tagChannelToolButton.addActionListener(toolSelectionListener);
		zoomSignalToolButton.addActionListener(toolSelectionListener);

		PluginAccessClass.getGUIImpl().registerSignalTools(toolMap, toolButtonGroup, toolSelectionListener, this);

		selectToolButton.setSelected(true);

	}
	public void notifyApplicationConfigChanged(ApplicationConfiguration config){
		timeScaleSlider.setMinimum((int)(config.getMinTimeScale()*1000));
		timeScaleSlider.setMaximum((int)(config.getMaxTimeScale()*1000));
		valueScaleSlider.setMinimum(config.getMinValueScale());
		valueScaleSlider.setMaximum(config.getMaxValueScale());
		channelHeightSlider.setMinimum(config.getMinChannelHeight());
		channelHeightSlider.setMaximum(config.getMaxChannelHeight());
	}

	private void buildMainToolBar() {

		SignalPlot plot = plots.getFirst();

		mainToolBar = new JToolBar();
		mainToolBar.setFloatable(false);

		timeScaleSlider = new JSlider(plot.getTimeScaleRangeModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent ev) {
				return _R("{0} pixels/sample", ((double) getValue())/1000);
			}

		};
		timeScaleSlider.setToolTipText("");
		Dimension d = timeScaleSlider.getPreferredSize();
		d.width = 100;
		timeScaleSlider.setPreferredSize(d);
		timeScaleSlider.setMinimumSize(d);
		timeScaleSlider.setMaximumSize(d);

		valueScaleSlider = new LogarithmicJSlider(plot.getValueScaleRangeModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent ev) {
				return getValue() + "%";
			}

		};
		valueScaleSlider.setToolTipText("");
		valueScaleSlider.setPreferredSize(d);
		valueScaleSlider.setMinimumSize(d);
		valueScaleSlider.setMaximumSize(d);

		channelHeightSlider = new JSlider(plot.getChannelHeightRangeModel()) {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent ev) {
				return _R("{0} px", getValue());
			}

		};
		channelHeightSlider.setToolTipText("");
		channelHeightSlider.setPreferredSize(d);
		channelHeightSlider.setMinimumSize(d);
		channelHeightSlider.setMaximumSize(d);

		channelHeightSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (plots.size() > 1) {
					plotPanel.revalidate();
				}
			}
		});

		plotOptionsButton = new JButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/plotoptions.png"));
		plotOptionsButton.setToolTipText(_("Change plot options"));
		plotOptionsButton.addActionListener(new PlotOptionsButtonListener());

		moveToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/hand.png"));
		moveToolButton.setToolTipText(_("Move signal with the mouse"));
		selectToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/arrow.png"));
		selectToolButton.setToolTipText(_("Select tags"));

		selectPageToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/pageselection.png"));
		selectPageToolButton.setToolTipText(_("Select signal pages"));
		selectBlockToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/blockselection.png"));
		selectBlockToolButton.setToolTipText(_("Select signal blocks"));
		selectChannelToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/channelselection.png"));
		selectChannelToolButton.setToolTipText(_("Select single channel or multichannel custom size signal fragments"));

		zoomSignalToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/zoom.png"));
		zoomSignalToolButton.setToolTipText(_("Magnify the signal (for settings press and hold the mouse button here)"));
		zoomSignalToolButton.addMouseListener(new ZoomSignalToolButtonMouseListener());

		rulerToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/ruler.png"));
		rulerToolButton.setToolTipText(_("Measure the signal"));

		mainToolBar.add(selectToolButton);
		selectToolButton.setSelected(true);
		mainToolBar.add(moveToolButton);
		mainToolBar.add(selectPageToolButton);
		mainToolBar.add(selectBlockToolButton);
		mainToolBar.add(selectChannelToolButton);
		mainToolBar.add(zoomSignalToolButton);
		mainToolBar.add(rulerToolButton);

		PluginAccessClass.getGUIImpl().toolsToMainMenu(mainToolBar, this);

		mainToolBar.addSeparator();
		mainToolBar.add(getNewTagAction());
		mainToolBar.add(getOpenTagAction());
		mainToolBar.add(getSaveTagAction());
		mainToolBar.add(getSaveTagAsAction());
		mainToolBar.add(getCloseTagAction());

		PlayPauseVideoAction playPauseAction = getPlayPauseVideoAction();
		if (playPauseAction != null) {
			mainToolBar.addSeparator();
			mainToolBar.add(playPauseAction);
			mainToolBar.add(playPauseAction.getVideoRateSlider());
		}

		mainToolBar.add(Box.createHorizontalGlue());
		mainToolBar.add(getShowPsychopyDialogButtonAction());
		mainToolBar.add(getStartMonitorRecordingAction());
		mainToolBar.add(getStopMonitorRecordingAction());
		mainToolBar.add(getStartVideoPreviewAction());
		mainToolBar.add(getMonitorRecordingDurationPanel());


		PluginAccessClass.getGUIImpl().addToMainSignalToolBar(mainToolBar);

		mainToolBar.add(Box.createHorizontalGlue());

		//mainToolBar.add(getPreciseSelectionAction());
		mainToolBar.addSeparator();

		mainToolBar.add(new TitledSliderPanel(_("Time scale"), timeScaleSlider));
		JToggleButton snapToPageButton = new JToggleButton(getSnapToPageAction());
		snapToPageButton.setHideActionText(true);
		mainToolBar.add(snapToPageButton);
		JToggleButton displayClockTimeButton = new JToggleButton(getDisplayClockTimeAction());
		displayClockTimeButton.setHideActionText(true);
		mainToolBar.add(displayClockTimeButton);
		mainToolBar.addSeparator();

		mainToolBar.add(new TitledSliderPanel(_("Value scale"), valueScaleSlider));
		mainToolBar.add(new TitledSliderPanel(_("Channel height"), channelHeightSlider));
		mainToolBar.addSeparator();
		mainToolBar.addSeparator();


		mainToolBar.add(getEditSignalParametersAction());
		mainToolBar.add(getEditSignalMontageAction());
		mainToolBar.add(getApplyDefaultMontageAction());
		mainToolBar.add(plotOptionsButton);

		JToggleButton filterSwitchButton = new JToggleButton(getFilterSwitchAction());
		filterSwitchButton.setHideActionText(true);
		filterSwitchButton.setSelectedIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/filteron.png"));
		filterSwitchButton.setSelected(document.getMontage().isFiltered());
		mainToolBar.add(filterSwitchButton);

	}

	private void buildTagToolBar() {

		styleToolBarMap = new HashMap<String, TagStyleToolBar>();

		tagToolBar = new JToolBar(JToolBar.VERTICAL);
		tagToolBar.setFloatable(false);
		tagToolBar.setVisible(false);

		tagPageToolButton = new JToggleButton(IconUtils.getPageTagIcon());
		tagPageToolButton.setToolTipText(_("Tag signal pages"));
		tagBlockToolButton = new JToggleButton(IconUtils.getBlockTagIcon());
		tagBlockToolButton.setToolTipText(_("Tag signal blocks"));
		tagChannelToolButton = new JToggleButton(IconUtils.getChannelTagIcon());
		tagChannelToolButton.setToolTipText(_("Tag single channel or multichannel custom size signal fragments"));

		tagToolBar.addSeparator(new Dimension(0,5));

		tagToolBar.add(getEditTagAnnotationAction());
		tagToolBar.add(getRemoveTagAction());

		tagToolBar.addSeparator(new Dimension(0,5));

		tagToolBar.add(tagPageToolButton);
		tagToolBar.add(tagBlockToolButton);
		tagToolBar.add(tagChannelToolButton);

		tagToolBar.addSeparator(new Dimension(0,5));

		tagToolBarLayout = new CardLayout();
		tagToolBarPanel = new JPanel(tagToolBarLayout);
		tagToolBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		tagToolBarPanel.add(new JPanel(), "none");

		tagToolBar.add(tagToolBarPanel);

	}

	public void snapPageToView() {

		for (SignalPlot plot : plots) {
			plot.snapPageToView();
		}

		deferredSnapToPage = false;

	}

	public void showTime(float itimeme) {

		JScrollPane scrollPane = scrollPanes.getFirst();
		SignalPlot plot = plots.getFirst();

		JViewport viewport = scrollPane.getViewport();

		Point point = viewport.getViewPosition();
		Point2D p2D = plot.toSignalSpace(point);
		Point2D.Float newP2D = new Point2D.Float(itimeme,(float) p2D.getY());
		Point newP = plot.toPixelSpace(newP2D);

		Dimension viewportSize = viewport.getExtentSize();
		Dimension plotSize = plot.getSize();

		newP.x = Math.max(0, Math.min(plotSize.width - viewportSize.width, newP.x));
		newP.y = Math.max(0, Math.min(plotSize.height - viewportSize.height, newP.y));

		viewport.setViewPosition(newP);

	}

	public void showTimeCentered(float time) {

		JScrollPane scrollPane = scrollPanes.getFirst();
		SignalPlot plot = plots.getFirst();

		JViewport viewport = scrollPane.getViewport();

		Point point = viewport.getViewPosition();
		Point2D p2D = plot.toSignalSpace(point);
		Point2D.Float newP2D = new Point2D.Float(time,(float) p2D.getY());
		Point newP = plot.toPixelSpace(newP2D);

		Dimension viewportSize = viewport.getExtentSize();
		Dimension plotSize = plot.getSize();

		newP.x -= viewportSize.width/2;

		newP.x = Math.max(0, Math.min(plotSize.width - viewportSize.width, newP.x));
		newP.y = Math.max(0, Math.min(plotSize.height - viewportSize.height, newP.y));

		viewport.setViewPosition(newP);

	}

	public void showTag(Tag tag) {

		JScrollPane scrollPane = scrollPanes.getFirst();
		SignalPlot plot = plots.getFirst();

		JViewport viewport = scrollPane.getViewport();

		Dimension viewportSize = viewport.getExtentSize();
		int startX = plot.timeToPixel(tag.getPosition());
		int endX = plot.timeToPixel(tag.getPosition()+tag.getLength());

		int optimalX = startX - (viewportSize.width - (endX-startX)) / 2;

		Point newP = null;
		SignalSelectionType type = tag.getStyle().getType();
		if (type.isPage() || type.isBlock()) {
			Point point = viewport.getViewPosition();
			newP = new Point(optimalX, point.y);
		} else { // channel
			int startY = plot.channelToPixel(tag.getChannel());
			int endY = startY + plot.getPixelPerChannel();
			int optimalY  = startY - (viewportSize.height - (endY-startY)) / 2;
			newP = new Point(optimalX, optimalY);
		}

		Dimension plotSize = plot.getSize();

		newP.x = Math.max(0, Math.min(plotSize.width - viewportSize.width, newP.x));
		newP.y = Math.max(0, Math.min(plotSize.height - viewportSize.height, newP.y));

		viewport.setViewPosition(newP);

	}

	public boolean isToolEngaged() {
		return ((null != currentSignalTool) && (currentSignalTool.isEngaged()));
	}

	@Override
	public SignalDocument getDocument() {
		return document;
	}

	public TagDifferenceDetector getTagDifferenceDetector() {
		if (tagDifferenceDetector == null) {
			tagDifferenceDetector = new TagDifferenceDetector();
		}
		return tagDifferenceDetector;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public NewTagAction getNewTagAction() {
		if (newTagAction == null) {
			newTagAction = new NewTagAction(this);
			newTagAction.setDocumentFlowIntegrator(documentFlowIntegrator);
			newTagAction.setNewTagDialog(newTagDialog);
		}
		return newTagAction;
	}

	public OpenTagAction getOpenTagAction() {
		if (openTagAction == null) {
			openTagAction = new OpenTagAction(this);
			openTagAction.setDocumentFlowIntegrator(documentFlowIntegrator);
			openTagAction.setFileChooser(fileChooser);
			openTagAction.setOptionPaneParent(this);
		}
		return openTagAction;
	}

	public CloseTagAction getCloseTagAction() {
		if (closeTagAction == null) {
			closeTagAction = new CloseTagAction(this);
			closeTagAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return closeTagAction;
	}

	public SaveTagAction getSaveTagAction() {
		if (saveTagAction == null) {
			saveTagAction = new SaveTagAction(this);
			saveTagAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return saveTagAction;
	}

	public SaveTagAsAction getSaveTagAsAction() {
		if (saveTagAsAction == null) {
			saveTagAsAction = new SaveTagAsAction(this);
			saveTagAsAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return saveTagAsAction;
	}

	public PreciseSelectionAction getPreciseSelectionAction() {
		if (preciseSelectionAction == null) {
			preciseSelectionAction = new PreciseSelectionAction(this);
			preciseSelectionAction.setSignalSelectionDialog(signalSelectionDialog);
		}
		return preciseSelectionAction;
	}

	public TagSelectionAction getTagSelectionAction() {
		if (tagSelectionAction == null) {
			tagSelectionAction = new TagSelectionAction(this);
		}
		return tagSelectionAction;
	}

	public RemoveTagAction getRemoveTagAction() {
		if (removeTagAction == null) {
			removeTagAction = new RemoveTagAction(this);
		}
		return removeTagAction;
	}

	/**
	 * @return play/pause action or NULL if not available for this signal
	 */
	public PlayPauseVideoAction getPlayPauseVideoAction() {
		if (playPauseVideoAction == null) {
			OfflineVideoFrame videoFrame = getDocumentVideoFrame();
			if (videoFrame != null) {
				playPauseVideoAction = new PlayPauseVideoAction(videoFrame);
			}
		}
		return playPauseVideoAction;
	}

	public ShowPsychopyDialogButton getShowPsychopyDialogButtonAction() {
		if (showPsychopyDialogButtonAction == null) {
			showPsychopyDialogButtonAction = new ShowPsychopyDialogButton(getActionFocusManager());
			showPsychopyDialogButtonAction.setSelectPsychopyExperimentDialog(psychopyExperimentDialog);
		}
		return showPsychopyDialogButtonAction;
	}

	/**
	 * Returns an {@link Action} responsible for starting a new monitor
	 * recording (it shows a dialog which allows to select recording target
	 * files and starts the recording).
	 * @return an {@link Action} responsible for starting a new monitor
	 * recording
	 */
	public StartMonitorRecordingAction getStartMonitorRecordingAction() {
		if (startMonitorRecordingAction == null) {
			startMonitorRecordingAction = new StartMonitorRecordingAction(getActionFocusManager());
			startMonitorRecordingAction.setStartMonitorRecordingDialog(startMonitorRecordingDialog);
		}
		return startMonitorRecordingAction;
	}

	/**
	 * Returns an {@link Action} responsible for stopping a monitor recording.
	 * @return an {@link Action} responsible for stopping an  monitor
	 * recording
	 */
	public StopMonitorRecordingAction getStopMonitorRecordingAction() {
		if (stopMonitorRecordingAction == null) {
			stopMonitorRecordingAction = new StopMonitorRecordingAction(getActionFocusManager());
		}
		return stopMonitorRecordingAction;
	}

	/**
	 * Returns an {@link Action} responsible for stopping a monitor recording.
	 * @return an {@link Action} responsible for stopping an  monitor
	 * recording
	 */
	public StartVideoPreviewAction getStartVideoPreviewAction() {
		if (startVideoPreviewAction == null) {
			startVideoPreviewAction = new StartVideoPreviewAction(document);
		}
		return startVideoPreviewAction;
	}

	public MonitorRecordingDurationPanel getMonitorRecordingDurationPanel() {
		if (monitorRecordingDurationPanel == null) {
			monitorRecordingDurationPanel = new MonitorRecordingDurationPanel(document);
		}
		return monitorRecordingDurationPanel;
	}

	public EditSignalParametersAction getEditSignalParametersAction() {
		if (editSignalParametersAction == null) {
			editSignalParametersAction = new EditSignalParametersAction(this);
			editSignalParametersAction.setSignalParametersDialog(signalParametersDialog);
		}
		return editSignalParametersAction;
	}

	public EditSignalMontageAction getEditSignalMontageAction() {
		if (editSignalMontageAction == null) {
			editSignalMontageAction = new EditSignalMontageAction(this);
			editSignalMontageAction.setSignalMontageDialog(signalMontageDialog);
		}
		return editSignalMontageAction;
	}

	public ApplyDefaultMontageAction getApplyDefaultMontageAction() {
		if (applyDefaultMontageAction == null) {
			applyDefaultMontageAction = new ApplyDefaultMontageAction(this);
		}
		return applyDefaultMontageAction;
	}

	public DisplayClockTimeAction getDisplayClockTimeAction() {
		if (displayClockTimeAction == null) {
			displayClockTimeAction = new DisplayClockTimeAction(this);
		}
		return displayClockTimeAction;
	}

	public EditTagAnnotationAction getEditTagAnnotationAction() {
		if (editTagAnnotationAction == null) {
			editTagAnnotationAction = new EditTagAnnotationAction(this);
			editTagAnnotationAction.setEditTagAnnotationDialog(editTagAnnotationDialog);
			KeyStroke ctrla = KeyStroke.getKeyStroke("ctrl A");
			getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrla, "editTagAnnotationAction");
			getActionMap().put("editTagAnnotationAction", editTagAnnotationAction);
		}
		return editTagAnnotationAction;
	}

	public SnapToPageAction getSnapToPageAction() {
		if (snapToPageAction == null) {
			snapToPageAction = new SnapToPageAction(this);
		}
		return snapToPageAction;
	}

	public SignalFilterSwitchAction getFilterSwitchAction() {
		if (signalFilterSwitchAction == null) {
			signalFilterSwitchAction = new SignalFilterSwitchAction(this);
		}
		return signalFilterSwitchAction;
	}

	public boolean isDisplayClockTime() {
		return displayClockTime;
	}

	public void setDisplayClockTime(boolean displayClockTime) {
		if (this.displayClockTime != displayClockTime) {
			this.displayClockTime = displayClockTime;
			for (SignalPlot plot : plots) {
				plot.revalidateAndRepaintAll();
			}
		}
	}

	public boolean isSnapToPageMode() {
		return snapToPageMode;
	}

	public void setSnapToPageMode(boolean snapToPageMode) {
		if (this.snapToPageMode != snapToPageMode) {
			this.snapToPageMode = snapToPageMode;
			getSnapToPageAction().putValue(AbstractAction.SELECTED_KEY, new Boolean(snapToPageMode));

			timeScaleSlider.setEnabled(!snapToPageMode);
//			zoomMouseWheelListener.setTimeEnabled(!snapToPageMode);

			if (snapToPageMode) {
				if (isVisible()) {
					snapPageToView();
				} else {
					deferredSnapToPage = true;
				}
			}

		}
	}

	public ZoomSignalTool getZoomSignalTool() {
		return zoomSignalTool;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	public MontagePresetManager getMontagePresetManager() {
		return montagePresetManager;
	}

	public void setMontagePresetManager(MontagePresetManager montagePresetManager) {
		if (this.montagePresetManager != montagePresetManager) {
			if (this.montagePresetManager != null) {
				this.montagePresetManager.removePresetManagerListener(montagePresetManagerListener);
			}
			this.montagePresetManager = montagePresetManager;
			if (montagePresetManager != null) {
				if (montagePresetManagerListener == null) {
					montagePresetManagerListener = new PresetManagerAdapter() {
						@Override
						public void defaultPresetChanged(PresetManagerEvent ev) {
							afSupport.fireActionFocusChanged();
						}
					};
				}
				montagePresetManager.addPresetManagerListener(montagePresetManagerListener);
			}
		}
	}

	public NewTagDialog getNewTagDialog() {
		return newTagDialog;
	}

	public void setNewTagDialog(NewTagDialog newTagDialog) {
		this.newTagDialog = newTagDialog;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public SignalSelectionDialog getSignalSelectionDialog() {
		return signalSelectionDialog;
	}

	public void setSignalSelectionDialog(SignalSelectionDialog signalSelectionDialog) {
		this.signalSelectionDialog = signalSelectionDialog;
	}

	public PsychopyExperimentDialog getPsychopyExperimentDialog() {
		return psychopyExperimentDialog;
	}

	public StartMonitorRecordingDialog getStartMonitorRecordingDialog() {
		return startMonitorRecordingDialog;
	}

	public void setPsychopyExperimentDialog(PsychopyExperimentDialog psychopyExperimentDialog) {
		this.psychopyExperimentDialog = psychopyExperimentDialog;
		this.getShowPsychopyDialogButtonAction().setSelectPsychopyExperimentDialog(psychopyExperimentDialog);
	}

	public void setStartMonitorRecordingDialog(StartMonitorRecordingDialog startMonitorRecordingDialog) {
		this.startMonitorRecordingDialog = startMonitorRecordingDialog;
		getStartMonitorRecordingAction().setStartMonitorRecordingDialog(startMonitorRecordingDialog);
	}

	public SignalParametersDialog getSignalParametersDialog() {
		return signalParametersDialog;
	}

	public void setSignalParametersDialog(SignalParametersDialog signalParametersDialog) {
		this.signalParametersDialog = signalParametersDialog;
	}

	public SignalMontageDialog getSignalMontageDialog() {
		return signalMontageDialog;
	}

	public void setSignalMontageDialog(SignalMontageDialog signalMontageDialog) {
		this.signalMontageDialog = signalMontageDialog;
	}

	public EditTagAnnotationDialog getEditTagAnnotationDialog() {
		return editTagAnnotationDialog;
	}

	public void setEditTagAnnotationDialog(EditTagAnnotationDialog editTagAnnotationDialog) {
		this.editTagAnnotationDialog = editTagAnnotationDialog;
	}

	public TagStylePaletteDialog getTagStylePaletteDialog() {
		return tagStylePaletteDialog;
	}

	public void setTagStylePaletteDialog(TagStylePaletteDialog tagStylePaletteDialog) {
		this.tagStylePaletteDialog = tagStylePaletteDialog;
	}

	public EditTagDescriptionDialog getEditTagDescriptionDialog() {
		return editTagDescriptionDialog;
	}

	public void setEditTagDescriptionDialog(EditTagDescriptionDialog editTagDescriptionDialog) {
		this.editTagDescriptionDialog = editTagDescriptionDialog;
	}

	public JScrollPane getMasterScrollPane() {
		return scrollPanes.getFirst();
	}

	@Override
	public SignalPlot getMasterPlot() {
		return plots.getFirst();
	}

	public LinkedList<SignalPlot> getPlots() {
		return plots;
	}

	public LinkedList<SignalPlotScrollPane> getScrollPanes() {
		return scrollPanes;
	}

	public SlavePlotSettingsPopupDialog getSlavePlotSettingsPopupDialog() {
		return slavePlotSettingsPopupDialog;
	}

	public void setSlavePlotSettingsPopupDialog(SlavePlotSettingsPopupDialog slavePlotSettingsPopupDialog) {
		this.slavePlotSettingsPopupDialog = slavePlotSettingsPopupDialog;
	}

	public ChannelOptionsPopupDialog getChannelOptionsPopupDialog() {
		return this.channelOptionsPopupDialog;
	}
	public void setChannelOptionsPopupDialog(ChannelOptionsPopupDialog channelOptionsPopupDialog) {
		this.channelOptionsPopupDialog = channelOptionsPopupDialog;
	}

	private SignalPlotOptionsPopupDialog getPlotOptionsDialog() {
		if (signalPlotOptionsPopupDialog == null) {
			signalPlotOptionsPopupDialog = new SignalPlotOptionsPopupDialog((Window) getTopLevelAncestor(), true);
			signalPlotOptionsPopupDialog.setSignalView(this);
		}

		return signalPlotOptionsPopupDialog;
	}

	private ZoomSettingsPopupDialog getZoomSettingsDialog() {
		if (zoomSettingsDialog == null) {
			zoomSettingsDialog = new ZoomSettingsPopupDialog((Window) getTopLevelAncestor(), true);
		}
		return zoomSettingsDialog;
	}

	@Override
	public void destroy() {
		document.removePropertyChangeListener(hypnogramPlot);
		document.removePropertyChangeListener(this);
		if (this.montagePresetManager != null) {
			this.montagePresetManager.removePresetManagerListener(montagePresetManagerListener);
		}
		for (SignalPlot plot : plots) {
			plot.destroy();
		}
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public SignalSelectionType getCurrentTagType() {
		if ((currentSignalTool != null) && (currentSignalTool instanceof TaggingSignalTool)) {
			return ((TaggingSignalTool) currentSignalTool).getTagType();
		}
		return null;
	}

	private TagStyleToolBar getTagStyleToolBar(SignalSelectionType type) {
		TagDocument activeTag = document.getActiveTag();
		if (activeTag != null) {
			String hash = Integer.toHexString(activeTag.hashCode());
			return styleToolBarMap.get(hash+"-"+type.getName());
		}
		return null;
	}

	@Override
	public TagStyle getCurrentTagStyle(SignalSelectionType type) {
		if (type != null) {
			TagStyleToolBar toolBar = getTagStyleToolBar(type);
			if (toolBar != null) {
				return toolBar.getSelectedStyle();
			}
		}
		return null;
	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public TagDocument[] getComparedTags() {
		return comparedTags;
	}

	public void setComparedTags(TagDocument tag1, TagDocument tag2) {
		if (tag1 == null || tag2 == null) {
			comparedTags = null;
		} else {
			if (!document.getTagDocuments().contains(tag1) || !document.getTagDocuments().contains(tag2)) {
				throw new SanityCheckException("Tag to compare not in presented document");
			}
			comparedTags = new TagDocument[] { tag1, tag2 };
			TagDocument activeTag = document.getActiveTag();
			if (tag1 != activeTag && tag2 != activeTag) {
				document.setActiveTag(tag1);
			}
		}
		clearTagSelection();
		for (SignalPlotColumnHeader columnHeader : columnHeaders) {
			columnHeader.reset();
			columnHeader.revalidate();
		}
		onTagsChanged();
	}

	public void updateTagComparison() {

		if (comparedTags == null) {
			differenceSet = null;
			return;
		}

		TagDifferenceDetector detector = getTagDifferenceDetector();
		SortedSet<Tag> tags1 = comparedTags[0].getTagSet().getTags();
		SortedSet<Tag> tags2 = comparedTags[1].getTagSet().getTags();

		TreeSet<TagDifference> differences = new TreeSet<TagDifference>();

		detector.getDifferences(tags1, tags2, SignalSelectionType.PAGE, SignalSelection.CHANNEL_NULL, differences);
		detector.getDifferences(tags1, tags2, SignalSelectionType.BLOCK, SignalSelection.CHANNEL_NULL, differences);
		detector.getDifferences(tags1, tags2, SignalSelectionType.CHANNEL, SignalSelection.CHANNEL_NULL, differences);

		int cnt = document.getChannelCount();
		for (int i=0; i<cnt; i++) {
			detector.getDifferences(tags1, tags2, SignalSelectionType.CHANNEL, i, differences);
		}

		differenceSet = new TagDifferenceSet(differences);

	}

	public boolean isComparingTags() {
		return(comparedTags != null);
	}

	public TagDifferenceSet getDifferenceSet() {
		return differenceSet;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		String name = evt.getPropertyName();

		if (source == document) {
			if (name.equals(SignalDocument.TAG_DOCUMENTS_PROPERTY)) {

				TagDocument newDocument = (TagDocument) evt.getNewValue();
				TagDocument oldDocument = (TagDocument) evt.getOldValue();

				if (newDocument != null) {

					TagStyleToolBar pageToolBar = new TagStyleToolBar(newDocument.getTagSet(), SignalSelectionType.PAGE, tagIconProducer, getTagSelectionAction());
					TagStyleToolBar blockToolBar = new TagStyleToolBar(newDocument.getTagSet(), SignalSelectionType.BLOCK, tagIconProducer, getTagSelectionAction());
					TagStyleToolBar channelToolBar = new TagStyleToolBar(newDocument.getTagSet(), SignalSelectionType.CHANNEL, tagIconProducer, getTagSelectionAction());

					String hash = Integer.toHexString(newDocument.hashCode());

					tagToolBarPanel.add(pageToolBar, hash+"-page");
					tagToolBarPanel.add(blockToolBar, hash+"-block");
					tagToolBarPanel.add(channelToolBar, hash+"-channel");

					styleToolBarMap.put(hash+"-page", pageToolBar);
					styleToolBarMap.put(hash+"-block", blockToolBar);
					styleToolBarMap.put(hash+"-channel", channelToolBar);

					StyledTagSet tagSet = newDocument.getTagSet();
					tagSet.addTagListener(this);
					tagSet.addTagStyleListener(this);
					tagSet.addTagListener(hypnogramPlot);

					newDocument.addPropertyChangeListener(this);

				}

				if (oldDocument != null) {

					String hash = Integer.toHexString(oldDocument.hashCode());

					TagStyleToolBar pageToolBar = styleToolBarMap.get(hash+"-page");
					TagStyleToolBar blockToolBar = styleToolBarMap.get(hash+"-block");
					TagStyleToolBar channelToolBar = styleToolBarMap.get(hash+"-channel");

					if (pageToolBar != null) {
						tagToolBar.remove(pageToolBar);
						styleToolBarMap.remove(hash+"-page");
					}

					if (blockToolBar != null) {
						tagToolBar.remove(blockToolBar);
						styleToolBarMap.remove(hash+"-block");
					}

					if (channelToolBar != null) {
						tagToolBar.remove(channelToolBar);
						styleToolBarMap.remove(hash+"-channel");
					}

					StyledTagSet tagSet = oldDocument.getTagSet();
					tagSet.removeTagListener(this);
					tagSet.removeTagStyleListener(this);
					tagSet.removeTagListener(hypnogramPlot);

					oldDocument.removePropertyChangeListener(this);

					if (isComparingTags()) {
						if (oldDocument == comparedTags[0] || oldDocument == comparedTags[1]) {
							setComparedTags(null,null);
						}
					}

				}

				int tagCnt = document.getTagDocuments().size();

				if (tagCnt > 1) {
					for (SignalPlotScrollPane scrollPane : scrollPanes) {
						scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
					}
				} else {
					for (SignalPlotScrollPane scrollPane : scrollPanes) {
						scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
					}
				}

				hypnogramPlot.revalidateAndReset();

			}
			else if (name.equals(SignalDocument.ACTIVE_TAG_PROPERTY)) {

				TagDocument newDocument = (TagDocument) evt.getNewValue();

				if (newDocument != null) {

					updateTagStylePanel(newDocument);

					tagToolBar.setVisible(true);

				} else {

					tagToolBar.setVisible(false);

				}

				if (signalSelection != null && !signalSelection.getType().isChannel()) {
					// clear page or block signal selection because page/block parameters may have changed
					clearSignalSelection();
				}
				clearTagSelection();

				rebindTagKeys();

				afSupport.fireActionFocusChanged();

				// resnap to page for new tag
				if (snapToPageMode) {
					SwingUtilities.invokeLater(new ResnapToPageRunnable(this));
				}

			}
			else if (name.equals(SignalDocument.MONTAGE_PROPERTY)) {

				getFilterSwitchAction().putValue(AbstractAction.SELECTED_KEY, new Boolean(((Montage) evt.getNewValue()).isFilteringEnabled()));

			}

		} else if (source instanceof TagDocument) {
			TagDocument tagDocument = (TagDocument) source;
			if (!document.getTagDocuments().contains(tagDocument)) {
				logger.warn("Stray tag document change event?");
				return;
			}
			if (TagDocument.SAVED_PROPERTY.equals(name)) {
				if (tagDocument == document.getActiveTag()) {
					afSupport.fireActionFocusChanged();
				}
			}
		}

	}

	private void updateTagStylePanel(TagDocument tagDocument) {

		String hash = Integer.toHexString(tagDocument.hashCode());

		ButtonModel model = toolButtonGroup.getSelection();
		if (model != null) {
			SignalTool tool = toolMap.get(model);
			String key = null;
			boolean tagSelection = false;

			if (tool != null) {
				if (tool == tagPageSignalTool || tool == selectPageTool) {
					key = hash+"-page";
				} else if (tool == tagBlockSignalTool || tool == selectBlockTool) {
					key = hash+"-block";
				} else if (tool == tagChannelSignalTool || tool == selectChannelTool) {
					key = hash+"-channel";
				} else {
					key = "none";
				}
				tagSelection = (tool instanceof SelectionSignalTool);
				TagStyleToolBar toolBar = styleToolBarMap.get(key);
				if (toolBar != null) {
					toolBar.setTagSelectionOnButtonClick(tagSelection);
					if (tagSelection) {
						toolBar.clearSelection();
					} else {
						toolBar.assertAnySelection();
					}
				}
				tagToolBarLayout.show(tagToolBarPanel, key);
			}
		}

	}

	private void onTagsChanged() {
		// TODO consider either incremental update or incremental comparison (compare only visible areas)
		updateTagComparison();
		for (SignalPlot plot : plots) {
			plot.repaint();
		}
		for (SignalPlotColumnHeader columnHeader : columnHeaders) {
			columnHeader.repaint();
		}
	}

	private void onTagStylesChanged() {
		updateTagComparison();
		for (SignalPlot plot : plots) {
			plot.repaint();
		}
		for (SignalPlotColumnHeader columnHeader : columnHeaders) {
			columnHeader.repaint();
		}
		rebindTagKeys();
	}

	@Override
	public void tagAdded(TagEvent e) {
		onTagsChanged();
	}

	@Override
	public void tagChanged(TagEvent e) {
		onTagsChanged();
	}

	@Override
	public void tagRemoved(TagEvent e) {
		PositionedTag selectedTag = getTagSelection();
		if (selectedTag != null && selectedTag.tag == e.getTag()) {
			clearTagSelection();
		}
		onTagsChanged();
	}

	@Override
	public void tagStyleAdded(TagStyleEvent e) {
		onTagStylesChanged();
	}

	@Override
	public void tagStyleChanged(TagStyleEvent e) {
		onTagStylesChanged();
		onTagsChanged();
	}

	@Override
	public void tagStyleRemoved(TagStyleEvent e) {
		onTagStylesChanged();
		onTagsChanged();
	}

	private void rebindTagKeys() {

		InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = getActionMap();

		// clear previous bindings
		if (lastStylesByKeyStrokes != null) {

			Set<KeyStroke> keySet = lastStylesByKeyStrokes.keySet();
			for (KeyStroke keyStroke : keySet) {
				actionMap.remove("keyTagPressed-" + keyStroke.hashCode());
				inputMap.remove(keyStroke);
			}
			lastStylesByKeyStrokes = null;

		}

		TagDocument activeTag = document.getActiveTag();
		if (activeTag != null) {

			StyledTagSet tagSet = activeTag.getTagSet();
			HashMap<KeyStroke,TagStyle> stylesByKeyStrokes = tagSet.getStylesByKeyStrokes();

			Set<Entry<KeyStroke,TagStyle>> entrySet = stylesByKeyStrokes.entrySet();
			KeyStroke keyStroke;
			String actionName;

			for (Entry<KeyStroke,TagStyle> entry : entrySet) {

				keyStroke = entry.getKey();
				actionName = "keyTagPressed-" + keyStroke.hashCode();

				inputMap.put(keyStroke, actionName);
				actionMap.put(actionName, new TagKeyRedirectAction(entry.getValue()));

			}

			lastStylesByKeyStrokes = stylesByKeyStrokes;

		}

	}

	private void setCurrentSignalTool(SignalTool tool) {

		currentSignalTool = tool;

		for (SignalPlot plot : plots) {
			plot.setCursor(currentSignalTool.getDefaultCursor());
		}
		if (currentSignalTool.supportsColumnHeader()) {
			for (SignalPlotColumnHeader columnHeader : columnHeaders) {
				columnHeader.setCursor(currentSignalTool.getDefaultCursor());
			}
		} else {
			for (SignalPlotColumnHeader columnHeader : columnHeaders) {
				columnHeader.setCursor(Cursor.getDefaultCursor());
			}
		}
		if (currentSignalTool.supportsRowHeader()) {
			for (SignalPlotRowHeader rowHeader : rowHeaders) {
				rowHeader.setCursor(currentSignalTool.getDefaultCursor());
			}
		} else {
			for (SignalPlotRowHeader rowHeader : rowHeaders) {
				rowHeader.setCursor(Cursor.getDefaultCursor());
			}
		}

		TagDocument activeTag = document.getActiveTag();

		if (activeTag != null) {

			updateTagStylePanel(activeTag);

		} else {

			tagToolBarLayout.show(tagToolBarPanel, "none");

		}

		if (tagSelection != null) {
			if (currentSignalTool instanceof SelectionSignalTool) {
				clearTagSelection();
			}
			else if (currentSignalTool instanceof TaggingSignalTool) {
				SignalSelectionType type = tagSelection.tag.getType();
				if (type != ((TaggingSignalTool) currentSignalTool).getTagType()) {
					clearTagSelection();
				}
			}
		}

		if (signalSelection != null) {
			if (currentSignalTool instanceof TaggingSignalTool) {
				clearSignalSelection();
			}
			else if (currentSignalTool instanceof SelectionSignalTool) {
				SignalSelectionType type = signalSelection.getType();
				if (type != ((SelectionSignalTool) currentSignalTool).getSelectionType()) {
					clearSignalSelection();
				}
			}
		}

		toolMouseAdapter.setSignalTool(currentSignalTool);
		columnToolMouseAdapter.setSignalTool(currentSignalTool);
		rowToolMouseAdapter.setSignalTool(currentSignalTool);

	}

	public SignalSpaceConstraints createSignalSpaceConstraints() {

		SignalSpaceConstraints constraints = new SignalSpaceConstraints();
		constraints.setTagIconProducer(getTagIconProducer());

		SignalPlot masterPlot = getMasterPlot();

		MultichannelSampleSource montage = masterPlot.getSignalOutput();
		MultichannelSampleSource source = masterPlot.getSignalSource();

		int channelCount = source.getChannelCount();

		String[] labels = new String[channelCount];
		int i;
		for (i=0; i<channelCount; i++) {
			labels[i] = source.getLabel(i);
		}
		constraints.setSourceChannels(labels);

		channelCount = montage.getChannelCount();

		labels = new String[channelCount];
		for (i=0; i<channelCount; i++) {
			labels[i] = montage.getLabel(i);
		}
		constraints.setChannels(labels);

		constraints.setSignalLength(masterPlot.getMaxSampleCount());
		constraints.setTimeSignalLength(masterPlot.getMaxTime());

		constraints.setSamplingFrequency(masterPlot.getSamplingFrequency());
		constraints.setPageSize(masterPlot.getPageSize());
		constraints.setBlocksPerPage(masterPlot.getBlocksPerPage());
		constraints.setBlockSize(masterPlot.getBlockSize());
		constraints.setMaxBlock(masterPlot.getBlockCount()-1);
		constraints.setMaxPage(masterPlot.getPageCount()-1);
		constraints.setMaxWholePage(masterPlot.getWholePageCount()-1);

		return constraints;

	}

	private class PlotOptionsButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SignalPlotOptionsPopupDialog dialog = getPlotOptionsDialog();
			dialog.initializeNow();
			dialog.showDialog(null, true);
		}

	}

	private class ZoomSignalToolButtonMouseListener extends MouseAdapter {

		private Timer timer;

		ActionListener timerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ZoomSettingsPopupDialog dialog = getZoomSettingsDialog();
				Container ancestor = getTopLevelAncestor();
				Point containerLocation = ancestor.getLocation();
				Point location = SwingUtilities.convertPoint(zoomSignalToolButton, new Point(0,0), ancestor);
				location.translate(containerLocation.x, containerLocation.y);
				dialog.setLocation(location);
				zoomSignalToolButton.doClick();
				dialog.showDialog(zoomSignalTool);
			}
		};

		@Override
		public void mousePressed(MouseEvent e) {

			if (timer == null) {
				timer = new Timer(400, timerListener); // popup after 400 ms
				timer.setRepeats(false);
			}

			timer.start();

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (timer != null) {
				timer.stop();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (timer != null) {
				timer.stop();
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedSignalView#setSignalSelection(org.signalml.plugin.export.view.ExportedSignalPlot, org.signalml.plugin.export.signal.ExportedSignalSelection)
	 */
	@Override
	public void setSignalSelection(ExportedSignalPlot plot,	ExportedSignalSelection signalSelection) throws InvalidClassException {
		if (plot instanceof SignalPlot)
			setSignalSelection((SignalPlot) plot, new SignalSelection(signalSelection));
		else throw new InvalidClassException("only plot got from Svarog can be used");

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedSignalView#setTagSelection(org.signalml.plugin.export.view.ExportedSignalPlot, org.signalml.plugin.export.view.ExportedPositionedTag)
	 */
	@Override
	public void setTagSelection(ExportedSignalPlot plot,
								ExportedPositionedTag tagSelection) throws InvalidClassException {
		if (plot instanceof SignalPlot) {
			SignalPlot signalPlot = (SignalPlot) plot;
			setTagSelection(signalPlot, new PositionedTag(tagSelection));
		}
		else throw new InvalidClassException("only plot got from Svarog can be used");

	}

	private class ToolSelectionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SignalTool signalTool = toolMap.get(toolButtonGroup.getSelection());
			if (signalTool != null) {
				setCurrentSignalTool(signalTool);
			} else {
				logger.warn("WARNING: unbound signal tool");
			}

		}

	}

	protected class DelKeyRedirectAction extends AbstractAction implements TagStyleSelector {

		private static final long serialVersionUID = 1L;

		@Override
		public TagStyle getTagStyle() {
			return null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentSignalTool instanceof SelectionSignalTool) {
				getTagSelectionAction().actionPerformed(new ActionEvent(this, 0, "delete"));
			} else {
				getRemoveTagAction().actionPerformed(e);
			}

		}

		@Override
		public boolean isEnabled() {

			if (currentSignalTool instanceof SelectionSignalTool) {
				return getTagSelectionAction().isEnabled();
			} else {
				return getRemoveTagAction().isEnabled();
			}

		}

	}

	protected class TagKeyRedirectAction extends AbstractAction implements TagStyleSelector {

		private static final long serialVersionUID = 1L;

		private TagStyle tagStyle;

		public TagKeyRedirectAction(TagStyle tagStyle) {
			if (tagStyle == null) {
				throw new NullPointerException("No style");
			}
			this.tagStyle = tagStyle;
		}

		@Override
		public TagStyle getTagStyle() {
			return tagStyle;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			if (document instanceof MonitorSignalDocument) {
				createUserTagInMonitorMode();
			}
			else if (currentSignalTool instanceof SelectionSignalTool && signalSelection != null) {
				createTagForSignalSelection();
			} else {
				if (currentSignalTool instanceof TaggingSignalTool) {
					changeSelectedTagType();
				}
				createTagForCurrentPageOrBlock();
			}
		}

		private void changeSelectedTagType() {
			SignalSelectionType currentTagType = getCurrentTagType();
			SignalSelectionType desiredTagType = tagStyle.getType();

			if (currentTagType != desiredTagType) {
				// change selected style type
				if (desiredTagType.isPage()) {
					tagPageToolButton.doClick();
				} else if (desiredTagType.isBlock()) {
					tagBlockToolButton.doClick();
				} else if (desiredTagType.isChannel()) {
					tagChannelToolButton.doClick();
				} else {
					throw new SanityCheckException("Bad selection type");
				}
			}

			TagStyleToolBar toolBar = getTagStyleToolBar(desiredTagType);
			if (toolBar == null) {
				return;
			}

			toolBar.setSelectedStyle(tagStyle);
		}

		private void createUserTagInMonitorMode() {
			MonitorSignalDocument monitor = (MonitorSignalDocument) document;
			if (tagStyle.getType() == SignalSelectionType.CHANNEL) {
				final MonitorTag tag = new MonitorTag(tagStyle, 0.001*System.currentTimeMillis(), 1.0, -1);
				monitor.getMonitorWorker().acceptUserTag(tag);
			}
		}

		private void createTagForCurrentPageOrBlock() {
			if (plots.size() != 1) {
				logger.debug("no active plot");
				return;
			}
			SignalPlot plot = plots.getFirst();
			TagDocument tagDocument = plot.getDocument().getActiveTag();
			if (tagDocument == null) {
				logger.debug("no active tag document");
				return;
			}
			SignalSelectionType desiredTagType = tagStyle.getType();
			Rectangle view = plot.getViewport().getViewRect();

			int segmentIndex;
			double segmentSize;

			if (desiredTagType.isBlock()) {
				segmentIndex = (int) (view.getCenterX() / plot.getPixelPerBlock());
				segmentSize = plot.getBlockSize();
			} else if (desiredTagType.isPage()) {
				segmentIndex = (int) (view.getCenterX() / plot.getPixelPerPage());
				segmentSize = plot.getPageSize();
			} else {
				// other tag types are not supported here
				return;
			}

			SignalSelection selection = new SignalSelection(desiredTagType, segmentIndex * segmentSize, segmentSize);
			plot.tagSelection(tagDocument, tagStyle, selection, false);
		}

		private void createTagForSignalSelection() {
			if (signalSelection == null) {
				return;
			}

			SignalSelectionType currentTagType = signalSelection.getType();
			SignalSelectionType desiredTagType = tagStyle.getType();

			if (currentTagType != desiredTagType) {
				// ignore bindings to other style types
				return;
			}

			getTagSelectionAction().actionPerformed(new ActionEvent(this, 0, "tag"));
		}

		@Override
		public boolean isEnabled() {

			if (document instanceof MonitorSignalDocument) {
				return true;
			} else if (currentSignalTool instanceof SelectionSignalTool) {
				return getTagSelectionAction().isEnabled();
			} else if (currentSignalTool instanceof TaggingSignalTool) {
				return true;
			}

			return false;

		}

	}

}
