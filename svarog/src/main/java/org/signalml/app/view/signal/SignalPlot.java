/* SignalPlot.java created 2007-09-21
 *
 */

package org.signalml.app.view.signal;

import static java.lang.String.format;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.components.ChannelPlotOptionsModel;
import org.signalml.app.model.components.ChannelsPlotOptionsModel;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.tag.TagAttributesRenderer;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.app.view.tag.TagRenderer;
import org.signalml.app.view.tag.comparison.TagDifferenceRenderer;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.system.ChannelFunction;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.samplesource.ChangeableMultichannelSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.OriginalMultichannelSampleSource;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagDifference;
import org.signalml.domain.tag.TagDifferenceSet;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.signal.tagStyle.TagAttributeValue;
import org.signalml.plugin.export.signal.tagStyle.TagAttributes;
import org.signalml.plugin.export.view.ExportedSignalPlot;
import org.signalml.util.Util;

/** SignalPlot
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 		based on code Copyright (C) 2003 Dobieslaw Ircha <dircha@eranet.pl> Artur Biesiadowski <abies@adres.pl> Piotr J. Durka     <Piotr-J.Durka@fuw.edu.pl>
 */
public class SignalPlot extends JComponent implements PropertyChangeListener, ChangeListener, Scrollable, ExportedSignalPlot {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalPlot.class);

	private static final Dimension MINIMUM_SIZE = new Dimension(0,0);

	private SignalProcessingChain signalChain;

	private SignalDocument document;
	private Montage localMontage;

	private TagRenderer tagRenderer;
	/**
	 * Renderer capable of rendering the list of visible tag attributes on
	 * a tag.
	 */
	private TagAttributesRenderer tagAttributesRenderer;
	private TagDifferenceRenderer tagDifferenceRenderer;

	private float samplingFrequency;

	private double voltageZoomFactor;
	private double voltageZoomFactorRatio;
	private double timeZoomFactor; // equiv to "samplesPerPixel"

	private boolean antialiased;
	private boolean clamped;
	private boolean offscreenChannelsDrawn;
	private boolean tagToolTipsVisible;
	private boolean optimizeSignalDisplaying;

	private boolean pageLinesVisible;
	private boolean blockLinesVisible;
	private boolean channelLinesVisible;

	private double pixelPerSecond;
	private double pixelPerBlock;
	private double pixelPerPage;
	private int pixelPerChannel;
	private double pixelPerValue;

	private int[] sampleCount;
	private int maxSampleCount;
	private int channelCount;

	private double[] samples;

	private int[] channelLevel;
	private int clampLimit;

	private int pageCount;
	private int wholePageCount;
	private float pageSize;
	private int blockCount;
	private float blockSize;
	private float maxTime;
	private int blocksPerPage;

	private GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD,50000);

	private SignalPlotColumnHeader signalPlotColumnHeader = null;
	private SignalPlotRowHeader signalPlotRowHeader = null;
	private SignalPlotCorner signalPlotCorner = null;

	private JLabel signalPlotTitleLabel = null;
	private JLabel signalPlotSynchronizationLabel = null;

	private DefaultBoundedRangeModel timeScaleRangeModel;
	private DefaultBoundedRangeModel valueScaleRangeModel;
	private DefaultBoundedRangeModel channelHeightRangeModel;

	private ChannelsPlotOptionsModel channelsPlotOptionsModel;

	// the plot must be aware of its own viewport to draw fixed-position elements properly
	private JViewport viewport;

	private SignalPlotPopupProvider popupMenuProvider;
	private SignalView view;
	private SignalPlot masterPlot;

	private boolean horizontalLock;
	private boolean verticalLock;

	private float horizontalTimeLead;
	private float verticalValueLead;

	private int horizontalPixelLead;
	private int verticalPixelLead;

	private boolean compensationEnabled = true;
	private boolean ignoreSliderEvents = false;

	private ArrayList<PositionedTag> tempTagList;

	private int tempTagCnt;
	private boolean tempComparing;
	private TagDocument[] tempComparedTags;
	private Point tempViewportLocation;
	private Dimension tempViewportSize;
	private Dimension tempPlotSize;
	private ArrayList<SortedSet<Tag>> tempTagsToDrawList = new ArrayList<SortedSet<Tag>>();

	private TagPaintMode tagPaintMode;
	private SignalColor signalColor;
	private boolean signalXOR;

	private Rectangle tempBounds = new Rectangle();

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Initialization & setup */
	public SignalPlot(SignalDocument document, SignalView view, SignalPlot masterPlot) throws SignalMLException {
		super();
		this.document = document;
		this.view = view;
		this.masterPlot = masterPlot;

		setBackground(Color.WHITE);
		setFocusable(true);

		signalChain = SignalProcessingChain.createFilteredChain(document.getSampleSource());

		Montage montage = document.getMontage();
		if (montage != null)
			signalChain.applyMontageDefinition(montage);

		signalChain.addPropertyChangeListener(this);
		document.addPropertyChangeListener(this);

		ApplicationConfiguration config = view.getApplicationConfig();

		if (masterPlot == null) {

			timeScaleRangeModel = new DefaultBoundedRangeModel();
			valueScaleRangeModel = new DefaultBoundedRangeModel();
			channelHeightRangeModel = new DefaultBoundedRangeModel();

			pixelPerChannel = 80;
			voltageZoomFactor = 0.95;
			timeZoomFactor = 0.5;

			antialiased = config.isAntialiased();
			clamped = config.isClamped();
			offscreenChannelsDrawn = config.isOffscreenChannelsDrawn();
			optimizeSignalDisplaying = config.isOptimizeSignalDisplay();

			pageLinesVisible = config.isPageLinesVisible();
			blockLinesVisible = config.isBlockLinesVisible();
			channelLinesVisible = config.isChannelLinesVisible();

			tagPaintMode = config.getTagPaintMode();
			signalColor = config.getSignalColor();
			signalXOR = config.isSignalXOR();

			signalPlotCorner = new MasterSignalPlotCorner(this);

		} else {

			timeScaleRangeModel = masterPlot.getTimeScaleRangeModel();
			valueScaleRangeModel = masterPlot.getValueScaleRangeModel();
			channelHeightRangeModel = masterPlot.getChannelHeightRangeModel();

			pixelPerChannel = masterPlot.getPixelPerChannel();
			voltageZoomFactor = masterPlot.getVoltageZoomFactor();
			timeZoomFactor = masterPlot.getTimeZoomFactor();

			antialiased = masterPlot.isAntialiased();
			clamped = masterPlot.isClamped();
			offscreenChannelsDrawn = masterPlot.isOffscreenChannelsDrawn();
			optimizeSignalDisplaying = masterPlot.isOptimizeSignalDisplaying();

			pageLinesVisible = masterPlot.isPageLinesVisible();
			blockLinesVisible = masterPlot.isBlockLinesVisible();
			channelLinesVisible = masterPlot.isChannelLinesVisible();

			tagPaintMode = masterPlot.getTagPaintMode();
			signalColor = masterPlot.getSignalColor();
			signalXOR = masterPlot.isSignalXOR();

			SlaveSignalPlotCorner slaveSignalPlotCorner = new SlaveSignalPlotCorner(this);
			slaveSignalPlotCorner.setSlavePlotSettingsPopupDialog(view.getSlavePlotSettingsPopupDialog());
			signalPlotCorner = slaveSignalPlotCorner;

		}

		signalPlotColumnHeader = new SignalPlotColumnHeader(this);
		signalPlotRowHeader = new SignalPlotRowHeader(this);
		channelsPlotOptionsModel = new ChannelsPlotOptionsModel(this);
		signalPlotRowHeader.setChannelOptionsPopupDialog(view.getChannelOptionsPopupDialog());

		if (masterPlot == null) {
			setTagToolTipsVisible(config.isTagToolTipsVisible());
			setOptimizeSignalDisplaying(config.isOptimizeSignalDisplay());

		} else {
			setTagToolTipsVisible(masterPlot.isTagToolTipsVisible());
			setOptimizeSignalDisplaying(masterPlot.isOptimizeSignalDisplaying());
		}

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SignalPlot.this.view.getApplicationConfig().isRightClickPagesForward() && SwingUtilities.isRightMouseButton(e)) {
					if (e.isShiftDown()) {
						maybeShowPopupMenu(e);
					} else {
						pageForward();
					}
				} else {
					maybeShowPopupMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (!SignalPlot.this.view.getApplicationConfig().isRightClickPagesForward() || e.isShiftDown()) {
					maybeShowPopupMenu(e);
				}
			}

			private void maybeShowPopupMenu(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JPopupMenu popupMenu = getPlotPopupMenu();
					if (popupMenu != null) {
						popupMenu.show(e.getComponent(),e.getX(),e.getY());
					}
				}
			}

		});

	}

	private double condMaxValue(double mv) {
		double result = Math.min(2000.0, mv);
		if (Math.abs(result) < 0.000001)
			result = 2000.0;
		return result;
	}

	/**
	 * Returns source channel for given montage channel.
	 * @param index index of montage channel.
	 * @return SourceChannel for given montage channel.
	 */
	public SourceChannel getSourceChannelFor(int index) {
		return document.getMontage().getSourceChannelForMontageChannel(index);
	}

	public void initialize() throws SignalMLException {

		calculateParameters();

		if (masterPlot == null) {

			calculateVoltageZoomFactorRatio();

			ApplicationConfiguration config = view.getApplicationConfig();

			// update models
			timeScaleRangeModel.setRangeProperties((int)(timeZoomFactor*1000), 0, (int)(config.getMinTimeScale()*1000), (int)(config.getMaxTimeScale()*1000), false);
			valueScaleRangeModel.setRangeProperties(100, 0, config.getMinValueScale(), config.getMaxValueScale(), false);
			channelHeightRangeModel.setRangeProperties(pixelPerChannel, 0, config.getMinChannelHeight(), config.getMaxChannelHeight(), false);

			timeScaleRangeModel.addChangeListener(this);
			valueScaleRangeModel.addChangeListener(this);
			valueScaleRangeModel.addChangeListener(this.channelsPlotOptionsModel);
			channelHeightRangeModel.addChangeListener(this);

		} else {
			samples = new double[1024];
			masterPlot.addPropertyChangeListener(this);
		}
		this.channelsPlotOptionsModel.reset(channelCount);
		calculateParameters();
	}

	/**
	 * Calculates and returns ZoomFactorRatio for given 'index' channel.
	 * If 'index' == -1 then returns 'global' ZoomFactorRatio defined by
	 * EEG channel's type maxValue (if exists) or MAX from all channels` types
	 * maxValues.
	 * @param index an index of a channel for which calculations will be made
	 * @return voltage zoom ratio for given channel (or globally for all channels)
	 */
	public double getVoltageZoomFactorRatioFor(int index) {
		double v;
		if (index == -1)
			v = ChannelFunction.EEG.getMaxValue(); //global voltage scale is for EEG by default
		else
			v = this.getSourceChannelFor(index).getFunction().getMaxValue();
		return ((1.0 / (condMaxValue(v) * 2)) * 0.95) / 100;
	}

	/**
	 * Recalculates the voltageZoomFactorRatio according to the maximum
	 * value assumed from the signal.
	 */
	protected void calculateVoltageZoomFactorRatio() {
		voltageZoomFactorRatio = this.getVoltageZoomFactorRatioFor(-1);
		voltageZoomFactor = voltageZoomFactorRatio * 100;
	}

	private void calculateParameters() {

		if (document == null) {
			return;
		}

		samplingFrequency = signalChain.getSamplingFrequency();

		pageSize = document.getPageSize();
		blockSize = document.getBlockSize();
		blocksPerPage = document.getBlocksPerPage();

		pixelPerSecond = samplingFrequency * timeZoomFactor;
		pixelPerPage = pixelPerSecond * pageSize;
		pixelPerBlock = pixelPerPage / blocksPerPage;

		int oldChannelCount = channelCount;
		channelCount = signalChain.getChannelCount();
		if (oldChannelCount != channelCount)
			this.channelsPlotOptionsModel.reset(channelCount);
		sampleCount = new int[channelCount];
		int i, j, k;

		maxSampleCount = 0;
		for (i=0; i<channelCount; i++) {
			sampleCount[i] = signalChain.getSampleCount(i);
			if (maxSampleCount < sampleCount[i]) {
				maxSampleCount = sampleCount[i];
			}
		}

		maxTime = maxSampleCount / samplingFrequency;

		pageCount = (int) Math.ceil(maxTime / pageSize);
		blockCount = (int) Math.ceil(maxTime / blockSize);

		if (pageCount != ((int) Math.floor(maxTime / pageSize))) {
			wholePageCount = pageCount-1;
		} else {
			wholePageCount = pageCount;
		}

		pixelPerValue = pixelPerChannel * voltageZoomFactor;
		clampLimit = (pixelPerChannel / 2) - 2;

		channelLevel = new int[channelCount];

		j = 0;
		int prevVisibleLevel = 0, prevVisibleIndex = -1, invisibleCount = 0;
		for (i=0; i<channelCount; i++) {
			ChannelPlotOptionsModel channelModel = this.channelsPlotOptionsModel.getModelAt(i);
			//recalculate channel levels
			if (!channelModel.getVisible()) {
				invisibleCount ++;
			} else {
				//determine positions of last invisibleCount channels
				if (invisibleCount > 0) {
					if (prevVisibleIndex == -1)
						for (k=1; k<=invisibleCount; k++)
							channelLevel[prevVisibleIndex+k] = prevVisibleLevel + k*((pixelPerChannel/2) / (invisibleCount+1));
					else
						for (k=1; k<=invisibleCount; k++)
							channelLevel[prevVisibleIndex+k] = prevVisibleLevel + k*(pixelPerChannel / (invisibleCount+1));
					invisibleCount = 0;
				}

				//determine position of the current i-th channel
				channelLevel[i] = j * pixelPerChannel + pixelPerChannel / 2;
				j++;
				prevVisibleLevel = channelLevel[i];
				prevVisibleIndex = i;
			}
		}
		for (k=1; k<=invisibleCount; k++)
			channelLevel[prevVisibleIndex+k] = prevVisibleLevel + k*((pixelPerChannel/2) / (invisibleCount+1));


		if (signalPlotColumnHeader != null) {
			signalPlotColumnHeader.reset();
		}

		if (signalPlotRowHeader != null) {
			signalPlotRowHeader.reset();
		}
	}

	public void destroy() {
		setVisible(false);
		document.removePropertyChangeListener(this);
		document = null;
		signalChain.removePropertyChangeListener(this);
		signalChain.destroy();
		signalChain = null;
		view = null;
		signalPlotColumnHeader = null;
		signalPlotRowHeader = null;
		signalPlotCorner = null;
		viewport = null;
		timeScaleRangeModel = null;
		valueScaleRangeModel = null;
		channelHeightRangeModel = null;
		if (masterPlot != null) {
			masterPlot.removePropertyChangeListener(this);
			masterPlot = null;
		}
	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* JComponent & Scrollable implementations */

	private void prepareToPaintTags() {

		List<TagDocument> tagDocuments = document.getTagDocuments();
		tempTagCnt = tagDocuments.size();
		if (tempTagCnt == 0) {
			return;
		}

		if (tagRenderer == null) {
			tagRenderer = new TagRenderer();
		}
		if (tagAttributesRenderer == null) {
			tagAttributesRenderer = new TagAttributesRenderer();
		}

		tempViewportLocation = viewport.getViewPosition();
		tempViewportSize = viewport.getExtentSize();
		tempPlotSize = getSize();

		tempComparing = view.isComparingTags();
		tempComparedTags = view.getComparedTags();

		if (tempComparing && tagDifferenceRenderer == null) {
			tagDifferenceRenderer = new TagDifferenceRenderer();
		}

	}

	private void useTagPaintMode(Graphics2D g) {

		switch (tagPaintMode) {

		case XOR :

			g.setXORMode(Color.WHITE);
			break;

		case ALPHA_50 :

			g.setComposite(AlphaComposite.SrcOver.derive(0.5F));
			break;

		case ALPHA_80 :

			g.setComposite(AlphaComposite.SrcOver.derive(0.8F));
			break;
		case OVERLAY :
		default :
			g.setComposite(AlphaComposite.SrcOver);
			break;

		}

	}

	// note - this relies on class-local variables (optimization), see prepareToPaintTags
	private void paintTagOrTagSelection(Graphics2D g, Tag tag, int tagNumber, boolean active, boolean selected, boolean selectionOnly) {

		SignalSelectionType type = tag.getType();
		if (type == SignalSelectionType.PAGE) {
			return;
		} else if (tag.getChannel() != -1 && !isChannelVisible(tag.getChannel())) {
			return;
		}

		Component rendererComponent;
		Component attributesRendererComponent;

		if (selectionOnly) {
			rendererComponent = tagRenderer.getTagSelectionRendererComponent();
		} else {
			rendererComponent = tagRenderer.getTagRendererComponent(tag.getStyle(), active, selected);
		}
		attributesRendererComponent = tagAttributesRenderer.getTagAttributesRendererComponent(tag);

		if (type == SignalSelectionType.BLOCK) {
			Rectangle tagBounds = getPixelBlockTagBounds(tag, tag.isMarker(), tempTagCnt, tagNumber, tempViewportLocation, tempViewportSize, tempPlotSize, tempComparing, tempBounds);
			rendererComponent.setBounds(tagBounds);
			rendererComponent.paint(g.create(tagBounds.x, tagBounds.y, tagBounds.width, tagBounds.height));
			attributesRendererComponent.setBounds(tagBounds);
			attributesRendererComponent.paint(g.create(tagBounds.x, tagBounds.y, 400, tagBounds.height));
		}
		else if (type == SignalSelectionType.CHANNEL) {
			Rectangle[] tagBoundsArr = getPixelChannelTagBounds(tag, tag.isMarker(), tempTagCnt, tagNumber, tempComparing);
			for (int i=0; i<tagBoundsArr.length; i++) {
				if (tagBoundsArr[i].intersects(g.getClipBounds())) {
					rendererComponent.setBounds(tagBoundsArr[i]);
					rendererComponent.paint(g.create(tagBoundsArr[i].x, tagBoundsArr[i].y, tagBoundsArr[i].width, tagBoundsArr[i].height));
					attributesRendererComponent.setBounds(tagBoundsArr[i]);
					attributesRendererComponent.paint(g.create(tagBoundsArr[i].x, tagBoundsArr[i].y, 400, tagBoundsArr[i].height));
				}
			}
		} else {
			throw new SanityCheckException("Bad tag type");
		}

	}

	// note - this relies on class-local variables (optimization), see prepareToPaintTags
	private void paintTagDifference(Graphics2D g, TagDifference tagDifference) {

		if (!tempComparing) {
			return;
		}

		SignalSelectionType type = tagDifference.getType();
		if (type == SignalSelectionType.PAGE) {
			return;
		}

		Component rendererComponent = tagDifferenceRenderer.getTagDifferenceRendererComponent(tagDifference.getDifferenceType());

		if (type == SignalSelectionType.BLOCK) {
			Rectangle tagBounds = getPixelBlockTagBounds(tagDifference, false, tempTagCnt, 2, tempViewportLocation, tempViewportSize, tempPlotSize, true, tempBounds);
			rendererComponent.setBounds(tagBounds);
			rendererComponent.paint(g.create(tagBounds.x, tagBounds.y, tagBounds.width, tagBounds.height));
		}
		else if (type == SignalSelectionType.CHANNEL) {
			Rectangle[] tagBoundsArr = getPixelChannelTagBounds(tagDifference, false, tempTagCnt, 2, true);
			for (int i=0; i<tagBoundsArr.length; i++) {
				if (tagBoundsArr[i].intersects(g.getClipBounds())) {
					rendererComponent.setBounds(tagBoundsArr[i]);
					rendererComponent.paint(g.create(tagBoundsArr[i].x, tagBoundsArr[i].y, tagBoundsArr[i].width, tagBoundsArr[i].height));
				}
			}
		} else {
			throw new SanityCheckException("Bad tag difference type");
		}

	}

	// note - this relies on class-local variables (optimization), see prepareToPaintTags
	private void paintBlockAndChannelTags(Graphics2D g, PositionedTag tagSelection) {

		// note - this doesn't paint the selected tag, see paintSelectedBlockOrChannelTag

		List<TagDocument> tagDocuments = document.getTagDocuments();

		StyledTagSet tagSet;
		SortedSet<Tag> tagsToDraw;

		Tag highlightedTag = (tagSelection != null ? tagSelection.tag : null);

		Rectangle clip = g.getClipBounds();
		float start = (float)(clip.x / pixelPerSecond);
		float end = (float)((clip.x+clip.width) / pixelPerSecond);

		boolean active;
		boolean showActivity = (tempTagCnt > 1);


		useTagPaintMode(g);
		tempTagsToDrawList.clear();

		// draw block tags first
		int cnt = 0;
		for (TagDocument tagDocument : tagDocuments) {

			if (tempComparing && tagDocument != tempComparedTags[0] && tagDocument != tempComparedTags[1]) {
				// in comparing mode paint only the compared tags
				continue;
			}

			active = showActivity && (tagDocument == document.getActiveTag());
			tagSet = tagDocument.getTagSet();
			tagsToDraw = tagSet.getTagsBetween(start, end);
			tempTagsToDrawList.add(tagsToDraw);

			for (Tag tag : tagsToDraw) {
				if (tag == highlightedTag) {
					continue;
				}
				if (tag.getType() == SignalSelectionType.BLOCK) {
					paintTagOrTagSelection(g, tag, cnt, active, false, false);
				}
			}

			cnt++;

		}

		// draw channel tags second
		cnt = 0;
		for (TagDocument tagDocument : tagDocuments) {

			if (tempComparing && tagDocument != tempComparedTags[0] && tagDocument != tempComparedTags[1]) {
				// in comparing mode paint only the compared tags
				continue;
			}

			active = showActivity && (tagDocument == document.getActiveTag());
			tagsToDraw = tempTagsToDrawList.get(cnt);

			for (Tag tag : tagsToDraw) {
				if (tag == highlightedTag) {
					continue;
				}
				if (tag.getType() == SignalSelectionType.CHANNEL) {
					paintTagOrTagSelection(g, tag, cnt, active, false, false);
				}
			}

			cnt++;

		}

		// page tags are drawn in the column header

		g.setComposite(AlphaComposite.SrcOver);

		// differences go here
		if (tempComparing) {

			TagDifferenceSet differenceSet = view.getDifferenceSet();
			if (differenceSet != null) {
				SortedSet<TagDifference> differencesToDraw = differenceSet.getDifferencesBetween(start,end);
				for (TagDifference difference : differencesToDraw) {
					paintTagDifference(g, difference);
				}
			}

		}

	}

	// note - this relies on class-local variables (optimization), see prepareToPaintTags
	private void paintSelectedBlockOrChannelTag(Graphics2D g, PositionedTag tagSelection, boolean selectionOnly) {

		if (tagSelection == null) {
			return;
		}
		TagDocument tagDocument = document.getTagDocuments().get(tagSelection.tagPositionIndex);
		if (tempComparing && tagDocument != tempComparedTags[0] && tagDocument != tempComparedTags[1]) {
			// in comparing mode paint only the compared tags
			return;
		}

		SignalSelectionType type = tagSelection.tag.getType();
		if (type == SignalSelectionType.BLOCK || type == SignalSelectionType.CHANNEL) {

			boolean active = (tempTagCnt > 1) && (document.getTagDocuments().get(tagSelection.tagPositionIndex) == document.getActiveTag());

			useTagPaintMode(g);

			paintTagOrTagSelection(g, tagSelection.tag, tagSelection.tagPositionIndex, active, false, selectionOnly);

			g.setComposite(AlphaComposite.SrcOver);

		}

	}

	/*
	 * For given top x-value (in pixels) computes start channel index.
	 * @param topBoundary top x-value (in pixels) of the plot
	 */
	public int computePaintStartChannel(int topBoundary) {
		int startChannel = 0;
		int prevChannelsPix = 0;
		while (prevChannelsPix <= topBoundary && startChannel<channelCount) {
			if (this.getChannelsPlotOptionsModel().getModelAt(startChannel).getVisible())
				prevChannelsPix += pixelPerChannel;
			startChannel++;
		}
		startChannel--;
		return startChannel;
	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		int i;
		Graphics2D g = (Graphics2D)gOrig;
		Rectangle clip = g.getClipBounds();

		g.setColor(getBackground());
		g.fillRect(clip.x,clip.y,clip.width,clip.height);

		int clipEndX = clip.x + clip.width - 1;
		int clipEndY = clip.y + clip.height - 1;

		prepareToPaintTags();

		PositionedTag tagSelection = view.getTagSelection(this);

		if (tempTagCnt > 0) {
			paintBlockAndChannelTags(g, tagSelection);
		}

		if (blockLinesVisible && pixelPerBlock > 4) {
			// this draws block boundaries
			int startBlock = (int) Math.floor(clip.x / pixelPerBlock);
			if (startBlock == 0) {
				startBlock++;
			}
			int endBlock = (int) Math.ceil(clipEndX / pixelPerBlock);

			g.setColor(Color.GRAY);
			for (i=startBlock; i <= endBlock; i++) {
				g.drawLine((int)(i * pixelPerBlock), clip.y, (int)(i * pixelPerBlock), clipEndY);
			}
		}

		if (pageLinesVisible && pixelPerPage > 4) {
			// this draws page boundaries
			int startPage = (int) Math.floor(clip.x / pixelPerPage);
			if (startPage == 0) {
				startPage++;
			}
			int endPage = (int) Math.ceil(clipEndX / pixelPerPage);

			g.setColor(Color.RED);
			for (i=startPage; i <= endPage; i++) {
				g.drawLine((int)(i * pixelPerPage), clip.y, (int)(i * pixelPerPage), clipEndY);
			}
		}

		int channel, visibleCount;
		int startChannel = this.computePaintStartChannel(clip.y);
		int maxNumberOfChannels = (int) Math.min(channelCount, Math.ceil(((double)(clip.height - 1)) / pixelPerChannel));

		if (channelLinesVisible && pixelPerChannel > 10) {
			g.setColor(Color.BLUE);
			visibleCount = 0;
			channel=startChannel;
			while (visibleCount < maxNumberOfChannels && channel<channelCount) {
				if (isChannelVisible(channel)) {
					visibleCount ++;
					g.drawLine(clip.x, channelLevel[channel], clipEndX, channelLevel[channel]);
				}
				channel++;
			}
		}

		// draw the highlighted tag as is
		if (tempTagCnt > 0 && tagSelection != null) {
			paintSelectedBlockOrChannelTag(g, tagSelection, false);
		}

		if (!clamped) {
			if (offscreenChannelsDrawn) {
				// draw all
				startChannel = 0;
				maxNumberOfChannels = channelCount;
			} else {
				// determine on screen channels
				// NOTE: not the channels within the clip, the channels within the viewport
				Point viewportPoint = viewport.getViewPosition();
				Dimension viewportSize = viewport.getExtentSize();

				startChannel = this.computePaintStartChannel(viewportPoint.y);
				maxNumberOfChannels = (int) Math.min(channelCount, Math.ceil(((double)(viewportSize.height-1)) / pixelPerChannel));
			}
		}

		if (antialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g.setColor(signalColor.getColor());
		if (signalXOR) {
			g.setXORMode(Color.WHITE);
		} else {
			g.setComposite(AlphaComposite.SrcOver);
		}

		int firstSample, lastSample, length;
		double realX, x, y;
		double lastX = 0;
		double lastY = 0;

		visibleCount = 0;
		channel=startChannel;
		while (visibleCount < maxNumberOfChannels && channel<channelCount) {
			if (!isChannelVisible(channel)) {
				channel++;
				continue;
			}
			visibleCount ++;
			// those must be offset by one to get correct partial redraw
			// offset again by one, this time in terms of samples
			firstSample = (int) Math.max(0, Math.floor((clip.x-1) / timeZoomFactor) - 1);
			lastSample = (int) Math.min(sampleCount[channel] - 1, Math.ceil((clipEndX+1) / timeZoomFactor) + 1);
			if (lastSample < firstSample) {
				continue;
			}
			length = 1 + lastSample - firstSample;
			if (samples == null || samples.length < length) {
				samples = new double[length];
			}

			try {

				signalChain.getSamples(channel, samples, firstSample, length, 0);
			} catch (RuntimeException ex) {
				logger.error(format("failed to read %d samples starting at %d, till %d, channel %d",
									length, firstSample, lastSample, sampleCount[channel]));
				setVisible(false);
				throw ex;
			}

			realX = firstSample * timeZoomFactor;

			double pixelPerValueForChannel= channelsPlotOptionsModel.getPixelsPerValue(channel);
			y = samples[0] * pixelPerValueForChannel;

			if (clamped)
			{
				if (y > clampLimit) {
					y = channelLevel[channel] - clampLimit;
				} else if (y < -clampLimit) {
					y = channelLevel[channel] + clampLimit;
				} else {
					y = channelLevel[channel] - y;
				}
			} else {
				y = channelLevel[channel] - y;
			}

			generalPath.reset();

			if (!antialiased) {

				x = StrictMath.floor(realX + 0.5);
				y = StrictMath.floor(y + 0.5);

				generalPath.moveTo(x, y);

				lastX = x;
				lastY = y;

			} else {

				generalPath.moveTo(realX, y);

			}

			int sampleSkip = 1;

			if (optimizeSignalDisplaying) {
				//optimize signal display displays at most two sample for each pixel.
				sampleSkip = (int) (1/timeZoomFactor);
				sampleSkip /= 2;
				if (sampleSkip < 1)
					sampleSkip = 1;
			}

			//if the user selects a piece of the signal, we shouldn't break the rule
			//that we always take the sampleSkip's sample!
			int startingFrom = sampleSkip - firstSample % sampleSkip;
			if (firstSample % sampleSkip == 0)
				startingFrom = 0;

			if (optimizeSignalDisplaying) {
				//in each step we want to display the same samples even though few new were added
				OriginalMultichannelSampleSource source = signalChain.getSource();
				if (source instanceof ChangeableMultichannelSampleSource) {
					ChangeableMultichannelSampleSource changeableSource = (ChangeableMultichannelSampleSource) source;
					long addedSamples = changeableSource.getAddedSamplesCount();

					int changeableCorrection = 0;
					if (addedSamples % sampleSkip != 0)
						changeableCorrection = (int) (sampleSkip - addedSamples % sampleSkip);
					startingFrom += changeableCorrection;
					if (startingFrom >= sampleSkip)
						startingFrom -= sampleSkip;
				}
			}

			for (i=startingFrom; i<length; i += sampleSkip) {

				y = samples[i] * pixelPerValueForChannel;

				if (clamped)
				{
					if (y > clampLimit) {
						y = channelLevel[channel] - clampLimit;
					} else if (y < -clampLimit) {
						y = channelLevel[channel] + clampLimit;
					} else {
						y = channelLevel[channel] - y;
					}
				} else {
					y = channelLevel[channel] - y;
				}

				realX = ((firstSample+i) * timeZoomFactor);

				if (antialiased) {

					generalPath.lineTo(realX, y);

				} else {

					// if not antialiased then round to integer in order to prevent aliasing affects
					// (which cause slave plots to display the signal slightly differently)
					// expand Math.round for performance, StrictMath.floor is native
					x = StrictMath.floor(realX + 0.5);
					y = StrictMath.floor(y + 0.5);

					if (x != lastX || y != lastY) {
						generalPath.lineTo(x, y);
					}

					lastX = x;
					lastY = y;

				}


			}

			g.draw(generalPath);
			channel++;
		}

		if (signalXOR) {
			g.setComposite(AlphaComposite.SrcOver);
		}

		// finally draw the highlighted tags selection outline
		if (tempTagCnt > 0 && tagSelection != null) {
			paintSelectedBlockOrChannelTag(g, tagSelection, true);
		}

		SignalSelection signalSelection = view.getSignalSelection(this);

		if (signalSelection != null) {

			g.setColor(Color.BLUE);
			g.setStroke(new BasicStroke(3.0F,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 10F, new float[] {5,5}, 0F));

			Rectangle r = getPixelSelectionBounds(signalSelection, tempBounds);
			r = r.intersection(new Rectangle(new Point(0,0), getSize()));

			g.drawRect(r.x+1,r.y+1,r.width-2,r.height-2); // draw the selection completely _inside_ the selected area (pen width of 3 must be compenstated)

		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int)(maxSampleCount*timeZoomFactor),this.channelsPlotOptionsModel.getVisibleChannelsCount()*pixelPerChannel);
	}

	@Override
	public Dimension getMinimumSize() {
		if (masterPlot == null) {
			return new Dimension((int)(maxSampleCount*timeZoomFactor),pixelPerChannel);
		} else {
			return MINIMUM_SIZE;
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public JPopupMenu getPlotPopupMenu() {
		if (view.isToolEngaged()) {
			return null;
		}
		if (popupMenuProvider == null) {
			return null;
		}
		return popupMenuProvider.getPlotPopupMenu();
	}

	@Override
	public boolean isDoubleBuffered() {
		return true;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		switch (orientation) {

		case SwingConstants.VERTICAL :

			return pixelPerChannel;

		case SwingConstants.HORIZONTAL :
		default :

			if (direction > 0) {
				return getPageForwardSkip(viewport.getViewPosition());
			} else {
				return -getPageBackwardSkip(viewport.getViewPosition());
			}

		}
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		switch (orientation) {

		case SwingConstants.VERTICAL :

			return pixelPerChannel/8;

		case SwingConstants.HORIZONTAL :
		default :

			if (direction > 0) {
				return getBlockForwardSkip(viewport.getViewPosition());
			} else {
				return -getBlockBackwardSkip(viewport.getViewPosition());
			}
		}
	}

	@Override
	public int getPageForwardSkip(Point position) {
		int currentPage = (int) Math.floor(position.x / pixelPerPage);
		int pageOffset = position.x - ((int)(currentPage*pixelPerPage));
		if (pageOffset > pixelPerPage / 2) {
			// this trick prevent innacuracies caused by rounding
			currentPage++;
			pageOffset = position.x - ((int)(currentPage*pixelPerPage));
		}
		currentPage++;
		return (pageOffset + ((int)(currentPage*pixelPerPage))) - position.x;
	}

	@Override
	public int getPageBackwardSkip(Point position) {
		int currentPage = (int) Math.floor(position.x / pixelPerPage);
		int pageOffset = position.x - ((int)(currentPage*pixelPerPage));
		if (pageOffset < pixelPerPage / 2) {
			// this trick prevent innacuracies caused by rounding
			currentPage--;
			pageOffset = position.x - ((int)(currentPage*pixelPerPage));
		}
		currentPage--;
		return (pageOffset + ((int)(currentPage*pixelPerPage))) - position.x;
	}

	@Override
	public int getBlockForwardSkip(Point position) {
		int currentBlock = (int) Math.floor(position.x / pixelPerBlock);
		int blockOffset = position.x - ((int)(currentBlock*pixelPerBlock));
		if (blockOffset > pixelPerBlock / 2) {
			// this trick prevent innacuracies caused by rounding
			currentBlock++;
			blockOffset = position.x - ((int)(currentBlock*pixelPerBlock));
		}
		currentBlock++;
		return (blockOffset + ((int)(currentBlock*pixelPerBlock))) - position.x;
	}

	@Override
	public int getBlockBackwardSkip(Point position) {
		int currentBlock = (int) Math.floor(position.x / pixelPerBlock);
		int blockOffset = position.x - ((int)(currentBlock*pixelPerBlock));
		if (blockOffset < pixelPerBlock / 2) {
			// this trick prevent innacuracies caused by rounding
			currentBlock--;
			blockOffset = position.x - ((int)(currentBlock*pixelPerBlock));
		}
		currentBlock--;
		return (blockOffset + ((int)(currentBlock*pixelPerBlock))) - position.x;
	}

	@Override
	public void pageForward() {
		Point position = viewport.getViewPosition();
		position.x += getPageForwardSkip(position);
		position.x = Math.max(0, Math.min(getSize().width - viewport.getExtentSize().width, position.x));
		viewport.setViewPosition(position);
	}

	@Override
	public void pageBackward() {
		Point position = viewport.getViewPosition();
		position.x += getPageBackwardSkip(position);
		position.x = Math.max(0, Math.min(getSize().width - viewport.getExtentSize().width, position.x));
		viewport.setViewPosition(position);
	}

	public void snapPageToView() {

		Dimension extent = viewport.getExtentSize();
		Point position = viewport.getViewPosition();

		int currentPage = (int) Math.floor(position.x / pixelPerPage);

		if (masterPlot == null) {
			double timeZoomFactor = ((double) extent.width) / (samplingFrequency*pageSize);
			setTimeZoomFactor(timeZoomFactor);
		}

		// viewport needs to be validated after change, so that getSize returns a valid value
		viewport.validate();
		Dimension size = getSize();

		position.x = (int)(currentPage * pixelPerPage);
		position.x = Math.min(size.width-extent.width, position.x);

		boolean oldHorizontalLock = horizontalLock;
		boolean oldVerticalLock = verticalLock;

		try {

			// prevent re-synchronization
			horizontalLock = false;
			verticalLock = false;
			viewport.setViewPosition(position);

		} finally {
			horizontalLock = oldHorizontalLock;
			verticalLock = oldVerticalLock;
		}

		if (horizontalLock && masterPlot != null) {
			// reset alignment
			Point masterPosition = masterPlot.getViewport().getViewPosition();
			horizontalTimeLead = toTimeSpace(position) - masterPlot.toTimeSpace(masterPosition);
			horizontalPixelLead = position.x - masterPosition.x;
		}

	}

	@Override
	public String getToolTipText(MouseEvent event) {

		if (!tagToolTipsVisible) {
			return null;
		}

		Point p = event.getPoint();

		tempTagList = getTagsAtPoint(p, tempTagList);
		if (tempTagList.isEmpty()) {
			return null;
		}

		String locationMessage = _R("T: {0}, V:{1} [P: {2}, B: {3}, C: {4}]",
									toTimeSpace(p),
									toValueSpace(p),
									toPageSpace(p),
									toBlockSpace(p),
									signalChain.getLabel(toChannelSpace(p)));
		return getTagListToolTip(locationMessage, tempTagList);

	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Listener implementations */

	public void reset() {
		calculateParameters();
		revalidateAndRepaintAll();
	}

	public void revalidateAndRepaintAll() {
		if (signalPlotColumnHeader != null) {
			signalPlotColumnHeader.revalidate();
			signalPlotColumnHeader.repaint();
		}
		if (signalPlotRowHeader != null) {
			signalPlotRowHeader.revalidate();
			signalPlotRowHeader.repaint();
		}
		revalidate();
		repaint();
	}

	public void updateScales(double timeZoomFactor, double voltageZoomFactor, int pixelPerChannel, boolean compensate) {

		Point viewportPoint = null;
		Dimension viewportSize = null;
		Point p = null;
		Dimension plotSize = null;
		Point2D.Float p2 = null;

		if (compensate) {
			viewportPoint = viewport.getViewPosition();
			viewportSize = viewport.getExtentSize();
			p = new Point(viewportPoint.x + viewportSize.width/2, viewportPoint.y + viewportSize.height/2);
			p2 = toSignalSpace(p);
		}

		if (timeZoomFactor >= 0) {
			setTimeZoomFactor(timeZoomFactor);
		}
		if (voltageZoomFactor >= 0) {
			setVoltageZoomFactor(voltageZoomFactor);
		}
		if (pixelPerChannel >= 0) {
			setPixelPerChannel(pixelPerChannel);
		}

		if (compensate) {

			// viewport needs to be validated after change, so that getSize returns a valid value
			viewport.validate();
			plotSize = getSize();

			Point newP = toPixelSpace(p2);
			newP.x = newP.x - viewportSize.width/2;
			newP.y = newP.y - viewportSize.height/2;

			newP.x = Math.max(0, Math.min(plotSize.width - viewportSize.width, newP.x));
			newP.y = Math.max(0, Math.min(plotSize.height - viewportSize.height, newP.y));

			viewport.setViewPosition(newP);

		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		Object source = evt.getSource();
		String name = evt.getPropertyName();

		if (source == document) {
			if (name.equals(SignalDocument.MONTAGE_PROPERTY)) {
				this.setLocalMontage((Montage) evt.getNewValue());
				this.channelsPlotOptionsModel.reset(this.getChannelCount());
			}
		}
		else if (masterPlot != null && source == masterPlot) {
			if (TIME_ZOOM_FACTOR_PROPERTY.equals(name)) {
				updateScales(masterPlot.getTimeZoomFactor(), -1, -1, compensationEnabled);
			}
			else if (VOLTAGE_ZOOM_FACTOR_PROPERTY.equals(name)) {
				updateScales(-1, masterPlot.getVoltageZoomFactor(), -1, false);
			}
			else if (PIXEL_PER_CHANNEL_PROPERTY.equals(name)) {
				updateScales(-1, -1, masterPlot.getPixelPerChannel(), compensationEnabled);
			}

		}
		else if (OriginalMultichannelSampleSource.CALIBRATION_PROPERTY.equals(name)) {

			calculateVoltageZoomFactorRatio();
			reset();

		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {

		Object source = e.getSource();

		if (source == viewport) {
			PositionedTag pTag = view.getTagSelection(this);
			if (pTag != null) {
				if (!isTagOnScreen(pTag)) {
					view.clearTagSelection();
				}
			}
			SignalSelection selection = view.getSignalSelection(this);
			if (selection != null) {
				if (!isSelectionOnScreen(selection)) {
					view.clearSignalSelection();
				}
			}
		}
		else if (!ignoreSliderEvents) {
			if (source == timeScaleRangeModel) {
				double timeZoomFactor = ((double) timeScaleRangeModel.getValue()) / 1000F;
				updateScales(timeZoomFactor, -1, -1, compensationEnabled);
			}
			else if (source == valueScaleRangeModel) {
				double voltageZoomFactor = (valueScaleRangeModel.getValue()) * voltageZoomFactorRatio;
				//this.channelsPlotOptionsModel.globalScaleChanged(valueScaleRangeModel.getValue());
				updateScales(-1, voltageZoomFactor, -1, false);
			}
			else if (source == channelHeightRangeModel) {
				updateScales(-1, -1, channelHeightRangeModel.getValue(), compensationEnabled);
			}
		}

	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Conversions */

	@Override
	public Point2D.Float toSignalSpace(Point p) {
		float time = (float)((p.x) / pixelPerSecond);
		float value = (float)((p.y) / pixelPerValue);

		return new Point2D.Float(time,value);
	}

	/**
	 * Returns the distance (in the signal space) between two points (in pixels).
	 * @param p1 point 1 (pixels)
	 * @param p2 point 2 (pixels)
	 * @return the distance between two points (signal space)
	 */
	public Point2D.Float getDistanceInSignalSpace(Point p1, Point p2) {
		Point pixelSize = new Point(
				Math.abs(p1.x - p2.x),
				Math.abs(p1.y - p2.y)
			);

		int channel = toChannelSpace(p1);
		double pixelPerValueForChannel= channelsPlotOptionsModel.getPixelsPerValue(channel);
		float value = (float)((pixelSize.y) / pixelPerValueForChannel);
		float time = (float)((pixelSize.x) / pixelPerSecond);

		return new Point2D.Float(time,value);
	}

	/**
	 * Distance is controversial when it has different scaling for each point.
	 * @param p1 point 1
	 * @param p2 point 2
	 * @return true if for each point we have a different scaling.
	 */
	public boolean isDistanceControversial(Point p1, Point p2) {

		int channel1 = Math.min(toChannelSpace(p1), toChannelSpace(p2));
		int channel2 = Math.max(toChannelSpace(p1), toChannelSpace(p2));

		double p1PixelPerValueForChannel= channelsPlotOptionsModel.getPixelsPerValue(channel1);

		for (int channel = channel1; channel <= channel2; channel++) {
			double p2PixelPerValueForChannel= channelsPlotOptionsModel.getPixelsPerValue(channel);

			if (p1PixelPerValueForChannel != p2PixelPerValueForChannel)
				return true;
		}

		return false;
	}

	@Override
	public Point toPixelSpace(Point2D.Float p) {
		int x = (int) Math.round(p.getX() * pixelPerSecond);
		int y = (int) Math.round(p.getY() * pixelPerValue);

		return new Point(x,y);
	}

	@Override
	public int toPageSpace(Point p) {
		return (int) Math.max(0, Math.min(pageCount-1, Math.floor(p.x / pixelPerPage)));
	}

	@Override
	public int toBlockSpace(Point p) {
		return (int) Math.max(0, Math.min(blockCount-1, Math.floor(p.x / pixelPerBlock)));
	}

	@Override
	public float toTimeSpace(Point p) {
		return Math.max(0, Math.min(maxTime, (float)((p.x) / pixelPerSecond)));
	}

	@Override
	public int toSampleSpace(Point p) {
		return Math.max(0, Math.min(maxSampleCount-1, (int)((p.x) / timeZoomFactor)));
	}

	@Override
	public float toValueSpace(Point p) {
		int channel = toChannelSpace(p);
		int y = channelLevel[channel] - p.y;
		return Math.round((y) / pixelPerValue);
	}

	@Override
	public int timeToPixel(double time) {
		return (int) Math.round((time) * pixelPerSecond);
	}

	@Override
	public int channelToPixel(int channel) {

		int invisibleChannels = 0;
		for (int i = 0; i < channel; i++)
			if (!isChannelVisible(i))
				invisibleChannels++;

		return ((channel-invisibleChannels) * pixelPerChannel);
	}

	public int getInvisibleChannelsBeforeChannel(int channel) {
		int numberOfInvisibleChannels = 0;
		for (int i = 0; i < channel + numberOfInvisibleChannels && i < getChannelCount(); i++) {
			if (!isChannelVisible(i))
				numberOfInvisibleChannels++;
		}
		return numberOfInvisibleChannels;
	}

	protected boolean isChannelVisible(int channel) {
		return this.channelsPlotOptionsModel.getModelAt(channel).getVisible();
	}

	@Override
	public int toChannelSpace(Point p) {
		int channel = (int) Math.max(0, Math.min(channelCount-1, Math.floor(p.y / pixelPerChannel)));

		int numberOfInvisibleChannels = 0;
		for (int i = 0; i <= channel + numberOfInvisibleChannels && i < getChannelCount(); i++) {
			if (!isChannelVisible(i))
				numberOfInvisibleChannels++;
		}

		return channel + numberOfInvisibleChannels;
	}

	public Rectangle getPixelSelectionBounds(SignalSelection selection, Rectangle useRect) {

		double position = selection.getPosition();
		double length = selection.getLength();
		SignalSelectionType type = selection.getType();

		int selLeft = (int) Math.floor(selection.getPosition() * pixelPerSecond);
		int selRight = (int) Math.ceil(selection.getLength() * pixelPerSecond);
		int selTop;
		int selBottom;

		if (type == SignalSelectionType.PAGE) {
			selLeft = (int)((position / pageSize) * pixelPerPage);
			selRight = (int)(((position + length) / pageSize) * pixelPerPage);
		} else if (type == SignalSelectionType.BLOCK) {
			selLeft = (int)((position / blockSize) * pixelPerBlock);
			selRight = (int)(((position + length) / blockSize) * pixelPerBlock);
		} else {
			selLeft = (int) Math.round(position * pixelPerSecond);
			selRight = (int) Math.round((position+length) * pixelPerSecond) - 1;
		}

		int selChannel = selection.getChannel();
		if (selChannel == SignalSelection.CHANNEL_NULL) {
			selTop = 0;
			selBottom = getSize().height - 1;
		} else {
			selChannel -= getInvisibleChannelsBeforeChannel(selChannel);
			selTop = selChannel*pixelPerChannel;
			selBottom = selTop + pixelPerChannel - 1;
		}

		Rectangle rect;
		if (useRect == null) {
			rect = new Rectangle();
		} else {
			rect = useRect;
		}
		rect.x = selLeft;
		rect.y = selTop;
		rect.width = selRight-selLeft;
		rect.height = selBottom-selTop;

		return rect;

	}

	private boolean isSelectionOnScreen(SignalSelection selection) {
		return viewport.getViewRect().intersects(getPixelSelectionBounds(selection, tempBounds));
	}

	public Rectangle getPixelBlockTagBounds(SignalSelection tag, boolean marker,int tagCnt, int tagNumber, Point viewportPoint, Dimension viewportSize, Dimension plotSize, boolean comparing, Rectangle useRect) {

		Rectangle rect = getTagSelectionRectangle(tag, marker, tagCnt, useRect);

		if (rect.x > 0 && blockLinesVisible && pixelPerBlock > 4) {
			int linePosition = (int)((int)((tag.getPosition() / blockSize)) * pixelPerBlock);
			if (linePosition == rect.x) {
				rect.x++; // block tags are drawn only inside the block
			}
		}

		if (tagCnt > 1) {

			int height = (plotSize.height <= viewportSize.height ? plotSize.height : viewportSize.height);

			if (comparing) {

				// 0 - top, 1 - bottom, 2 - comparison
				int tagHeight = (height-COMPARISON_STRIP_HEIGHT) / 2;
				if (tagNumber == 0) {
					rect.y = viewportPoint.y;
					rect.height = tagHeight;
				} else if (tagNumber == 1) {
					rect.y = viewportPoint.y + COMPARISON_STRIP_HEIGHT + tagHeight;
					rect.height = tagHeight;
				} else {
					rect.y = viewportPoint.y + tagHeight + COMPARISON_STRIP_MARGIN;
					rect.height = COMPARISON_STRIP_HEIGHT - (2 * COMPARISON_STRIP_MARGIN);
				}


			} else {
				float pixerPerTag = ((float) height) / tagCnt;
				rect.y = viewportPoint.y + (int)((tagNumber) * pixerPerTag);
				int endY = viewportPoint.y + (int)((tagNumber+1) * pixerPerTag);
				rect.height = endY - rect.y;
			}

		} else {
			rect.y = 0;
			rect.height = plotSize.height;
		}

		return rect;

	}

	public Rectangle getTagSelectionRectangle(SignalSelection tag, boolean marker, int tagCnt, Rectangle useRect) {
		Rectangle rect;
		if (useRect == null) {
			rect = new Rectangle();
		} else {
			rect = useRect;
		}

		if (marker) {

			int rWidth = pixelPerChannel / (3 * tagCnt); // 1/3 of the height for this tag
			if (rWidth > 50) {
				rWidth = 50;
			} else if (rWidth < 5) {
				rWidth = 5;
			}
			rect.x = (int)(tag.getPosition() * pixelPerSecond) - rWidth/2;
			rect.width = rWidth;

		} else {
			rect.x = (int)(tag.getPosition() * pixelPerSecond);
			rect.width = (int)(tag.getLength() * pixelPerSecond);
		}

		return rect;
	}

	public Rectangle getPixelChannelTagBoundsInChannel(SignalSelection tag, boolean marker, int tagCnt, int tagNumber, int channel, boolean comparing, Rectangle useRect) {

		Rectangle rect = getTagSelectionRectangle(tag, marker, tagCnt, useRect);

		int invisibleChannels = getInvisibleChannelsBeforeChannel(channel);
		int channelOffset = (channel-invisibleChannels) * pixelPerChannel;

		if (comparing) {

			// 0 - top, 1 - bottom, 2 - comparison
			int tagHeight = (pixelPerChannel-COMPARISON_STRIP_HEIGHT) / 2;
			if (tagNumber == 0) {
				rect.y = channelOffset;
				rect.height = tagHeight;
			} else if (tagNumber == 1) {
				rect.y = channelOffset + COMPARISON_STRIP_HEIGHT + tagHeight;
				rect.height = tagHeight;
			} else {
				rect.y = channelOffset + tagHeight + COMPARISON_STRIP_MARGIN;
				rect.height = COMPARISON_STRIP_HEIGHT - (2 * COMPARISON_STRIP_MARGIN);
			}

		} else {

			float pixelPerTag = ((float) pixelPerChannel) / tagCnt;

			rect.y = channelOffset + (int)((tagNumber) * pixelPerTag);
			if (channelLinesVisible && (tagCnt % 2 == 0) && (tagNumber == (tagCnt/2))) {  // avoid obscuring channel line
				rect.y++;
			}
			int endY = channelOffset + (int)((tagNumber+1) * pixelPerTag);
			rect.height = endY - rect.y;

		}

		return rect;

	}

	public Rectangle[] getPixelChannelTagBounds(SignalSelection tag, boolean marker, int tagCnt, int tagNumber, boolean comparing) {
		if (tag.getChannel() == -1) {
			Rectangle[] rects = new Rectangle[1];
			rects[0] = getPixelBlockTagBounds(tag, marker, tagCnt, tagNumber, tempViewportLocation, tempViewportSize, tempPlotSize, tempComparing, tempBounds);
			return rects;
		};
		int[] channels = signalChain.getDependantChannelIndices(tag.getChannel());

		Rectangle[] rects = new Rectangle[channels.length];

		for (int i=0; i<channels.length; i++) {
			rects[i] = getPixelChannelTagBoundsInChannel(tag, marker, tagCnt, tagNumber, channels[i], comparing, null);
		}

		return rects;

	}

	private boolean isTagOnScreen(PositionedTag pTag) {

		Rectangle viewRect = viewport.getViewRect();
		SignalSelectionType type = pTag.tag.getType();
		if (type.isBlock()) {
			return getPixelBlockTagBounds(pTag.tag, pTag.tag.isMarker(), document.getTagDocuments().size(), pTag.tagPositionIndex, viewRect.getLocation(), viewRect.getSize(), getSize(), view.isComparingTags(), tempBounds).intersects(viewRect);
		}
		else if (type.isChannel()) {
			Rectangle[] bounds = getPixelChannelTagBounds(pTag.tag, pTag.tag.isMarker(), document.getTagDocuments().size(), pTag.tagPositionIndex, view.isComparingTags());
			for (int i=0; i<bounds.length; i++) {
				if (viewRect.intersects(bounds[i])) {
					return true;
				}
			}
			return false;
		}
		else if (type.isPage()) {
			Rectangle bounds = signalPlotColumnHeader.getPixelPageTagBounds(pTag.tag, document.getTagDocuments().size(), pTag.tagPositionIndex, view.isComparingTags(), tempBounds);
			// we assume the selected page tag to be always visible in vertical direction, only horizontal is checked
			bounds.y = viewRect.y;
			bounds.height = 1;
			return bounds.intersects(viewRect);
		} else {
			throw new SanityCheckException("Bad tag type");
		}

	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Local montage support */

	public Montage getLocalMontage() {
		return localMontage;
	}

	public void setLocalMontage(Montage localMontage) {
		this.setLocalMontage(localMontage, false);
	}

	public void setLocalMontage(Montage localMontage, boolean withoutFilters) {
		if (this.localMontage != localMontage) {
			this.localMontage = localMontage;
			updateSignalPlotTitleLabel();
			try {
				Montage m = null;
				if (localMontage == null)
					m = document.getMontage();
				else
					m = localMontage;
				if (withoutFilters)
					signalChain.applyMontageDefinitionWithoutfilters(m);
				else
					signalChain.applyMontageDefinition(m);

			} catch (MontageMismatchException ex) {
				logger.error("Failed to set montage", ex);
				Dialogs.showExceptionDialog(this, ex);
				return;
			}
			if (view.getSignalSelection(this) != null) {
				view.clearSignalSelection();
			}
			if (view.getTagSelection(this) != null) {
				view.clearTagSelection();
			}
			reset();
			// clone plots must revalidate to compensate for possible label length chnage
			// which might have resized the row header
			for (SignalPlot plot : view.getPlots()) {
				if (plot != this) {
					plot.revalidateAndRepaintAll();
				}
			}
		}
	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Selection support */

	public void repaintSelectionBounds(SignalSelection signalSelection) {
		Rectangle selectionBounds = getPixelSelectionBounds(signalSelection, tempBounds);
		selectionBounds.grow(3,3);
		repaint(selectionBounds);
	}

	@Override
	public SignalSelection getPageSelection(int fromPage, int toPage) {
		if (fromPage > toPage) {
			int temp = toPage;
			toPage = fromPage;
			fromPage = temp;
		}
		SignalSelection selection = new SignalSelection(
			SignalSelectionType.PAGE,
			fromPage * pageSize,
			((toPage+1)-fromPage) * pageSize
		);
		return selection;
	}

	@Override
	public SignalSelection getBlockSelection(int fromBlock, int toBlock) {
		if (fromBlock > toBlock) {
			int temp = toBlock;
			toBlock = fromBlock;
			fromBlock = temp;
		}
		SignalSelection selection = new SignalSelection(
			SignalSelectionType.BLOCK,
			fromBlock * blockSize,
			((toBlock+1)-fromBlock) * blockSize
		);
		return selection;
	}

	@Override
	public SignalSelection getChannelSelection(float fromPosition, float toPosition, int channel) {
		if (fromPosition > toPosition) {
			float temp = toPosition;
			toPosition = fromPosition;
			fromPosition = temp;
		}
		return new SignalSelection(SignalSelectionType.CHANNEL, fromPosition, toPosition-fromPosition, channel);
	}

	/**
	 * Transforms a given {@link SignalSelection}to a marker selection type.
	 * The result of the transformation is a one-sample wide signal selection
	 * positioned at the beginning of the original signal selection.
	 * @param selection the selection to be transformed
	 * @return the result of the transformation - a one-sample wide signal
	 * selection position at the beginning of the given signal selection.
	 */
	protected SignalSelection transformToMarkerSelection(SignalSelection selection) {

		double startPosition = selection.getPosition();
		int sampleAtPoint = (int)(startPosition * samplingFrequency);
		float newStartPosition = sampleAtPoint / samplingFrequency;

		return getChannelSelection(newStartPosition, newStartPosition + 1/samplingFrequency, selection.getChannel());

	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Tagging support */

	public void repaintTagBounds(PositionedTag tag, int tagCnt) {
		SignalSelectionType type = tag.tag.getStyle().getType();
		if (type == SignalSelectionType.PAGE) {
			Rectangle tagBounds;
			tagBounds = signalPlotColumnHeader.getPixelPageTagBounds(tag.tag, tagCnt, tag.tagPositionIndex, view.isComparingTags(), tempBounds);
			tagBounds.grow(3,3);
			signalPlotColumnHeader.repaint(tagBounds);
		}
		else if (type == SignalSelectionType.BLOCK) {
			Rectangle tagBounds;
			tagBounds = getPixelBlockTagBounds(tag.tag, tag.tag.isMarker(), tagCnt, tag.tagPositionIndex, viewport.getViewPosition(), viewport.getExtentSize(), getSize(), view.isComparingTags(), tempBounds);
			tagBounds.grow(3,3);
			repaint(tagBounds);
		}
		else if (type == SignalSelectionType.CHANNEL) {
			Rectangle[] tagBounds;
			tagBounds = getPixelChannelTagBounds(tag.tag, tag.tag.isMarker(), tagCnt, tag.tagPositionIndex, view.isComparingTags());
			for (int i=0; i<tagBounds.length; i++) {
				tagBounds[i].grow(3,3);
				repaint(tagBounds[i]);
			}
		}
	}

	public void tagSelection(TagDocument tagDocument, TagStyle style, SignalSelection selection, boolean selectNew) {
		SignalSelectionType type = selection.getType();
		if (type == SignalSelectionType.PAGE) {
			tagPageSelection(tagDocument, style, selection, selectNew);
		}
		else if (type == SignalSelectionType.BLOCK) {
			tagBlockSelection(tagDocument, style, selection, selectNew);
		} else {
			tagChannelSelection(tagDocument, style, selection, selectNew);
		}
	}

	public void eraseTagsFromSelection(TagDocument tagDocument, SignalSelection selection) {
		logger.debug("Erasing tags from [" + selection.toString() + "]");

		if (selection.getType().isChannel()) {
			// channel must be converted to source channel
			SignalSelection corrSelection;

			int channel = selection.getChannel();
			if (channel != SignalSelection.CHANNEL_NULL)
				channel = signalChain.getDocumentChannelIndex(channel);

			corrSelection = new SignalSelection(SignalSelectionType.CHANNEL, selection.getPosition(), selection.getLength(), channel);
			tagDocument.getTagSet().eraseTags(corrSelection);
		} else {
			tagDocument.getTagSet().eraseTags(selection);
		}

		tagDocument.invalidate();

	}

	public void tagPageSelection(TagDocument tagDocument, TagStyle style, SignalSelection selection, boolean selectNew) {

		if (!style.getType().isPage()) {
			throw new SanityCheckException("Not a page style");
		}
		if (!selection.getType().isPage()) {
			throw new SanityCheckException("Not a page selection");
		}

		Tag tag = null;
		int startPage = selection.getStartSegment(pageSize);
		int endPage = selection.getEndSegment(pageSize);
		for (int page=startPage; page<endPage; page++) {

			tag = new Tag(style, page*pageSize, pageSize, SignalSelection.CHANNEL_NULL, null);
			logger.debug("Adding page tag [" + tag.toString() + "]");

			tagDocument.getTagSet().replaceSameTypeTags(tag);
		}
		tagDocument.invalidate();

		if (selectNew && tag != null) {
			view.setTagSelection(this, new PositionedTag(tag, document.getTagDocuments().indexOf(tagDocument)));
		}

	}

	public void tagBlockSelection(TagDocument tagDocument, TagStyle style, SignalSelection selection, boolean selectNew) {

		if (!style.getType().isBlock()) {
			throw new SanityCheckException("Not a block style");
		}
		if (!selection.getType().isBlock()) {
			throw new SanityCheckException("Not a block selection");
		}

		Tag tag = null;
		int startBlock = selection.getStartSegment(blockSize);
		int endBlock = selection.getEndSegment(blockSize);
		for (int block=startBlock; block<endBlock; block++) {

			tag = new Tag(style, block*blockSize, blockSize, SignalSelection.CHANNEL_NULL, null);
			logger.debug("Adding block tag [" + tag.toString() + "]");

			tagDocument.getTagSet().replaceSameTypeTags(tag);
		}
		tagDocument.invalidate();

		if (selectNew && tag != null) {
			view.setTagSelection(this, new PositionedTag(tag, document.getTagDocuments().indexOf(tagDocument)));
		}

	}

	public void tagChannelSelection(TagDocument tagDocument, TagStyle style, SignalSelection selection, boolean selectNew) {

		if (!style.getType().isChannel()) {
			throw new SanityCheckException("Not a channel style");
		}
		if (!selection.getType().isChannel()) {
			throw new SanityCheckException("Not a channel selection");
		}

		if (style.isMarker())
			selection = transformToMarkerSelection(selection);

		int channel = selection.getChannel();
		if (channel != Tag.CHANNEL_NULL)
			channel = signalChain.getDocumentChannelIndex(channel);

		Tag tag = new Tag(style,selection.getPosition(), selection.getLength(), channel, null);
		logger.debug("Adding channel tag [" + tag.toString() + "]");
		tagDocument.getTagSet().mergeSameTypeChannelTags(tag);
		tagDocument.invalidate();

		if (selectNew) {
			view.setTagSelection(this, new PositionedTag(tag, document.getTagDocuments().indexOf(tagDocument)));
		}

	}

	public void selectTagAtPoint(Point point) {

		tempTagList = getTagsAtPoint(point, tempTagList);
		if (tempTagList.isEmpty()) {
			view.clearTagSelection();
			return;
		}
		Collections.sort(tempTagList);

		PositionedTag oldSelection = view.getTagSelection(this);
		if (oldSelection == null) {
			view.setTagSelection(this,tempTagList.get(0));
		} else {
			int index = -1;
			int cnt = tempTagList.size();
			for (int i=0; i<cnt; i++) {
				// this must check reference equality
				if (tempTagList.get(i).getTag() == oldSelection.getTag()) {
					index = i;
					break;
				}
			}
			index = (index + 1) % tempTagList.size();
			view.setTagSelection(this,tempTagList.get(index));
		}

	}

	public ArrayList<PositionedTag> getTagsAtPoint(Point point, ArrayList<PositionedTag> list) {

		List<TagDocument> tagDocuments = document.getTagDocuments();

		int tagCnt = tagDocuments.size();
		if (list == null) {
			list = new ArrayList<PositionedTag>();
		} else {
			list.clear();
		}
		if (tagCnt == 0) {
			return list;
		}

		int tagIndex;
		SortedSet<Tag> tagSet = null;

		float time = toTimeSpace(point);
		int viewChannel = toChannelSpace(point);
		int channel = signalChain.getDocumentChannelIndex(viewChannel);

		boolean comparing = view.isComparingTags();
		TagDocument[] comparedTags = null;
		if (comparing) {
			comparedTags = view.getComparedTags();
		}

		int cnt = 0;
		Rectangle tagBounds;

		Point viewportPoint = viewport.getViewPosition();
		Dimension viewportSize = viewport.getExtentSize();
		Dimension plotSize = getSize();

		for (TagDocument tagDocument : tagDocuments) {

			if (comparing && tagDocument != comparedTags[0] && tagDocument != comparedTags[1]) {
				// in comparing mode scan only the compared tags
				continue;
			}

			tagIndex = tagDocuments.indexOf(tagDocument);

			// use a 1 s margin to capture any marker tags
			tagSet = tagDocument.getTagSet().getTagsBetween(time-1, time+1);
			for (Tag tag : tagSet) {
				if (!tag.getStyle().isVisible())
					continue;

				if (tag.getStyle().getType() == SignalSelectionType.BLOCK) {
					if (time >= tag.getPosition() && time < (tag.getPosition() + tag.getLength())) {
						tagBounds = getPixelBlockTagBounds(tag, tag.isMarker(), tagCnt, cnt, viewportPoint, viewportSize, plotSize, comparing, tempBounds);
						if (tagBounds.contains(point)) {
							list.add(new PositionedTag(tag,tagIndex));
						}
					}
				}
				else if (tag.getStyle().getType() == SignalSelectionType.CHANNEL) {
					if (tag.getChannel() == channel) {
						if (tag.isMarker() || (time >= tag.getPosition() && time < tag.getEndPosition())) {
							tagBounds = getPixelChannelTagBoundsInChannel(tag, tag.isMarker(), tagCnt, cnt, viewChannel, comparing, tempBounds);
							if (tagBounds.contains(point)) {
								list.add(new PositionedTag(tag,tagIndex));
							}
						}
					}
					else if (tag.getChannel() == Tag.CHANNEL_NULL) {
						if (tag.isMarker() || (time >= tag.getPosition() && time < (tag.getPosition() + tag.getLength()))) {
							tagBounds = getPixelBlockTagBounds(tag, tag.isMarker(), tagCnt, cnt, viewportPoint, viewportSize, plotSize, comparing, tempBounds);
							if (tagBounds.contains(point)) {
								list.add(new PositionedTag(tag,tagIndex));
							}
						}
					}
				}

			}


			cnt++;
		}

		return list;

	}

	public String getTagMessage(Tag tag) {
		SignalSelectionType type = tag.getType();
		if (type == SignalSelectionType.PAGE || type == SignalSelectionType.BLOCK
				|| (type == SignalSelectionType.CHANNEL && tag.getChannel() == Tag.CHANNEL_NULL)) {
			return _R("{0} [{1}->{3}]",
					  tag.getStyle().getDescriptionOrName(),
					  tag.getPosition(),
					  tag.getLength(),
					  tag.getPosition()+tag.getLength());
		} else {
			if (tag.isMarker()) {
				return _R("{0} [{1} in channel {2}]",
						  tag.getStyle().getDescriptionOrName(),
						  tag.getPosition(),
						  signalChain.getPrimaryLabel(tag.getChannel()));
			} else {
				return _R("{0} [{1}->{3} in channel {4}]",
						  tag.getStyle().getDescriptionOrName(),
						  tag.getPosition(),
						  tag.getLength(),
						  tag.getPosition()+tag.getLength(),
						  signalChain.getPrimaryLabel(tag.getChannel()));
			}
		}
	}

	public String getTagToolTip(String title, PositionedTag tag) {
		if (tempTagList != null) {
			tempTagList.clear();
		} else {
			tempTagList = new ArrayList<PositionedTag>();
		}
		tempTagList.add(tag);
		return getTagListToolTip(title, tempTagList);
	}

	public String getTagListToolTip(String title, ArrayList<PositionedTag> tags) {

		StringBuilder buffer = new StringBuilder("<html><head></head><body>");
		buffer.append(title).append("<br />&nbsp;<br />");
		String annotation;

		PositionedTag tagSelection = view.getTagSelection(this);
		TagAttributes tagAttributes = null;

		if (tagSelection != null && tags.contains(tagSelection)) {
			buffer.append("<b>");
			buffer.append(getTagMessage(tagSelection.tag)).append("</b><br />");
			annotation = tagSelection.tag.getAnnotation();
			if (annotation != null && !annotation.isEmpty()) {
				buffer.append("<div style=\"padding-left: 20px; width: 300px; font-style: italic;\">").append(annotation).append("</div>");
			}
			tagAttributes = tagSelection.tag.getAttributes();

		}
		for (PositionedTag tag : tags) {
			if (tagSelection != null && tag.tag == tagSelection.tag) {
				continue;
			}
			buffer.append(getTagMessage(tag.tag)).append("<br />");
			annotation = tag.tag.getAnnotation();
			if (annotation != null && !annotation.isEmpty()) {
				buffer.append("<div style=\"padding-left: 20px; width: 300px; font-style: italic;\">").append(annotation).append("</div>");
			}
			tagAttributes = tag.tag.getAttributes();
		}

		for (TagAttributeValue attributeValue: tagAttributes.getAttributesList()) {
			String value = attributeValue.getAttributeValue();
			if (value.length() > 15)
				value = value.substring(0, 15);
			String code = attributeValue.getAttributeDefinition().getCode();
			buffer.append("<div style=\"padding-left: 20px; width: 300px; font-style: italic;\">").append(code).append(": ").append(value).append("</div>");
		}
		buffer.append("</body></html>");

		return buffer.toString();

	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Other getters and setters */

	public SignalProcessingChain getSignalChain() {
		return signalChain;
	}

	public void setSignalChain(SignalProcessingChain signalChain) {
		this.signalChain = signalChain;

	}

	public OriginalMultichannelSampleSource getSignalSource() {
		return signalChain.getSource();
	}

	public MultichannelSampleSource getSignalOutput() {
		return signalChain.getOutput();
	}

	public DefaultBoundedRangeModel getTimeScaleRangeModel() {
		return timeScaleRangeModel;
	}

	public DefaultBoundedRangeModel getValueScaleRangeModel() {
		return valueScaleRangeModel;
	}

	public DefaultBoundedRangeModel getChannelHeightRangeModel() {
		return channelHeightRangeModel;
	}

	public boolean isHorizontalLock() {
		return horizontalLock;
	}

	public void setHorizontalLock(boolean horizontalLock) {
		if (this.horizontalLock != horizontalLock) {
			this.horizontalLock = horizontalLock;
			if (horizontalLock && masterPlot != null) {
				Point viewportPosition = viewport.getViewPosition();
				Point masterViewportPosition = masterPlot.getViewport().getViewPosition();
				horizontalTimeLead = toTimeSpace(viewportPosition) - masterPlot.toTimeSpace(masterViewportPosition);
				horizontalPixelLead = viewportPosition.x - masterViewportPosition.x;
			}
			updateSignalPlotTitleLabel();
		}
	}

	public boolean isVerticalLock() {
		return verticalLock;
	}

	public void setVerticalLock(boolean verticalLock) {
		if (this.verticalLock != verticalLock) {
			this.verticalLock = verticalLock;
			if (verticalLock && masterPlot != null) {
				Point viewportPosition = viewport.getViewPosition();
				Point masterViewportPosition = masterPlot.getViewport().getViewPosition();
				verticalValueLead = toValueSpace(viewportPosition) - masterPlot.toValueSpace(masterViewportPosition);
				verticalPixelLead = viewportPosition.y - masterViewportPosition.y;
			}
			updateSignalPlotTitleLabel();
		}
	}

	public float getHorizontalTimeLead() {
		return horizontalTimeLead;
	}

	public float getVerticalValueLead() {
		return verticalValueLead;
	}

	public void setHorizontalTimeLead(float horizontalTimeLead) {
		if (this.horizontalTimeLead != horizontalTimeLead) {
			if (masterPlot != null) {

				this.horizontalTimeLead = horizontalTimeLead;

				boolean oldHorizontalLock = horizontalLock;
				boolean oldVerticalLock = verticalLock;

				try {
					horizontalLock = false;
					verticalLock = false;
					horizontalPixelLead = (int) Math.round(horizontalTimeLead * pixelPerSecond);
					Point masterPosition = masterPlot.getViewport().getViewPosition();
					Point newPosition = new Point(masterPosition.x + horizontalPixelLead, masterPosition.y);
					newPosition.x = Math.max(0, Math.min(getSize().width - viewport.getExtentSize().width, newPosition.x));
					viewport.setViewPosition(newPosition);
				} finally {
					horizontalLock = oldHorizontalLock;
					verticalLock = oldVerticalLock;
				}

			}
		}
	}

	public void setVerticalValueLead(float verticalValueLead) {
		if (this.verticalValueLead != verticalValueLead) {
			if (masterPlot != null) {

				this.verticalValueLead = verticalValueLead;

				boolean oldHorizontalLock = horizontalLock;
				boolean oldVerticalLock = verticalLock;

				try {
					horizontalLock = false;
					verticalLock = false;
					verticalPixelLead = (int) Math.round(verticalValueLead * pixelPerValue);
					Point masterPosition = masterPlot.getViewport().getViewPosition();
					Point newPosition = new Point(masterPosition.x, masterPosition.y + verticalPixelLead);
					newPosition.y = Math.max(0, Math.min(getSize().height - viewport.getExtentSize().height, newPosition.y));
					viewport.setViewPosition(newPosition);
				} finally {
					horizontalLock = oldHorizontalLock;
					verticalLock = oldVerticalLock;
				}

			}
		}
	}

	public int getHorizontalPixelLead() {
		return horizontalPixelLead;
	}

	public int getVerticalPixelLead() {
		return verticalPixelLead;
	}

	public TagPaintMode getTagPaintMode() {
		return tagPaintMode;
	}

	public void setTagPaintMode(TagPaintMode tagPaintMode) {
		if (this.tagPaintMode != tagPaintMode) {
			this.tagPaintMode = tagPaintMode;
			repaint();
		}
	}

	public SignalColor getSignalColor() {
		return signalColor;
	}

	public void setSignalColor(SignalColor signalColor) {
		if (!Util.equalsWithNulls(this.signalColor, signalColor)) {
			this.signalColor = signalColor;
			repaint();
		}
	}

	public boolean isSignalXOR() {
		return signalXOR;
	}

	public void setSignalXOR(boolean signalXOR) {
		if (this.signalXOR != signalXOR) {
			this.signalXOR = signalXOR;
			repaint();
		}
	}

	public boolean isPageLinesVisible() {
		return pageLinesVisible;
	}

	public void setPageLinesVisible(boolean pageLinesVisible) {
		if (this.pageLinesVisible != pageLinesVisible) {
			this.pageLinesVisible = pageLinesVisible;
			if (signalPlotColumnHeader != null) {
				signalPlotColumnHeader.reset();
				signalPlotColumnHeader.repaint();
			}
			repaint();

		}
	}

	public boolean isBlockLinesVisible() {
		return blockLinesVisible;
	}

	public void setBlockLinesVisible(boolean blockLinesVisible) {
		if (this.blockLinesVisible != blockLinesVisible) {
			this.blockLinesVisible = blockLinesVisible;
			repaint();
		}
	}

	public boolean isChannelLinesVisible() {
		return channelLinesVisible;
	}

	public void setChannelLinesVisible(boolean channelLinesVisible) {
		if (this.channelLinesVisible != channelLinesVisible) {
			this.channelLinesVisible = channelLinesVisible;
			repaint();
		}
	}

	public double getVoltageZoomFactor() {
		return voltageZoomFactor;
	}

	public double getVoltageZoomFactorRatio() {
		return voltageZoomFactorRatio;
	}

	public void setVoltageZoomFactor(double voltageZoomFactor) {
		if (this.voltageZoomFactor != voltageZoomFactor) {
			double oldValue = this.voltageZoomFactor;
			this.voltageZoomFactor = voltageZoomFactor;
			calculateParameters();
			if (masterPlot == null) {
				try {
					ignoreSliderEvents = true;
					int rangeModelValue = (int)(voltageZoomFactor / voltageZoomFactorRatio);
					if (rangeModelValue > valueScaleRangeModel.getMaximum()) {
						valueScaleRangeModel.setMaximum(rangeModelValue);
					}
					if (rangeModelValue < valueScaleRangeModel.getMinimum()) {
						valueScaleRangeModel.setMinimum(rangeModelValue);
					}
					valueScaleRangeModel.setValue(rangeModelValue);
					//todo mati - rebuild gui in side panel

				} finally {
					ignoreSliderEvents = false;
				}
			}
			if (signalPlotRowHeader != null) {
				signalPlotRowHeader.repaint();
			}
			repaint();
			firePropertyChange(VOLTAGE_ZOOM_FACTOR_PROPERTY, oldValue, voltageZoomFactor);
		}
	}

	@Override
	public double getTimeZoomFactor() {
		return timeZoomFactor;
	}

	public void setTimeZoomFactor(double timeZoomFactor) {
		if (this.timeZoomFactor != timeZoomFactor) {
			double oldValue = this.timeZoomFactor;
			this.timeZoomFactor = timeZoomFactor;
			calculateParameters();

			if (horizontalLock) {
				horizontalPixelLead = (int) Math.round(horizontalTimeLead * pixelPerSecond);
			}
			if (masterPlot == null) {
				try {
					ignoreSliderEvents = true;
					int rangeModelValue = (int)(timeZoomFactor*1000);
					if (rangeModelValue > timeScaleRangeModel.getMaximum()) {
						timeScaleRangeModel.setMaximum(rangeModelValue);
					}
					if (rangeModelValue < timeScaleRangeModel.getMinimum()) {
						timeScaleRangeModel.setMinimum(rangeModelValue);
					}
					timeScaleRangeModel.setValue(rangeModelValue);
				} finally {
					ignoreSliderEvents = false;
				}
			}
			if (signalPlotColumnHeader != null) {
				signalPlotColumnHeader.revalidate();
				signalPlotColumnHeader.repaint();
			}
			revalidate();
			repaint();
			firePropertyChange(TIME_ZOOM_FACTOR_PROPERTY, oldValue, timeZoomFactor);
		}
	}

	@Override
	public int getPixelPerChannel() {

		return pixelPerChannel;
	}


	public void setPixelPerChannel(int pixelPerChannel) {
		if (this.pixelPerChannel != pixelPerChannel) {
			if (verticalLock) {
				verticalPixelLead = (int) Math.round(verticalValueLead * pixelPerValue);
			}
			int oldValue = this.pixelPerChannel;
			this.pixelPerChannel = pixelPerChannel;
			calculateParameters();
			if (masterPlot == null) {
				try {
					ignoreSliderEvents = true;
					if (pixelPerChannel > channelHeightRangeModel.getMaximum()) {
						channelHeightRangeModel.setMaximum(pixelPerChannel);
					}
					if (pixelPerChannel < channelHeightRangeModel.getMinimum()) {
						channelHeightRangeModel.setMinimum(pixelPerChannel);
					}
					channelHeightRangeModel.setValue(pixelPerChannel);
				} finally {
					ignoreSliderEvents = false;
				}
			}
			if (signalPlotRowHeader != null) {
				signalPlotRowHeader.revalidate();
				signalPlotRowHeader.repaint();
			}
			revalidate();
			repaint();
			firePropertyChange(PIXEL_PER_CHANNEL_PROPERTY, oldValue, pixelPerChannel);
		}

	}

	@Override
	public boolean isAntialiased() {
		return antialiased;
	}

	public void setAntialiased(boolean antialiased) {
		this.antialiased = antialiased;
		repaint();
	}

	public boolean isCompensationEnabled() {
		return compensationEnabled;
	}

	public void setCompensationEnabled(boolean compensationEnabled) {
		this.compensationEnabled = compensationEnabled;
	}

	public boolean isClamped() {
		return clamped;
	}

	public void setClamped(boolean clamped) {
		this.clamped = clamped;
		repaint();
	}

	public boolean isOffscreenChannelsDrawn() {
		return offscreenChannelsDrawn;
	}

	public void setOffscreenChannelsDrawn(boolean offscreenChannelsDrawn) {
		this.offscreenChannelsDrawn = offscreenChannelsDrawn;
		repaint();
	}

	public boolean isTagToolTipsVisible() {
		return tagToolTipsVisible;
	}

	public void setTagToolTipsVisible(boolean tagToolTipsVisible) {
		if (this.tagToolTipsVisible != tagToolTipsVisible) {
			this.tagToolTipsVisible = tagToolTipsVisible;
			if (tagToolTipsVisible) {
				setToolTipText("");
				signalPlotColumnHeader.setToolTipText("");
			} else {
				setToolTipText(null);
				signalPlotColumnHeader.setToolTipText(null);
			}
		}
	}

	public boolean isOptimizeSignalDisplaying() {
		return optimizeSignalDisplaying;
	}

	public void setOptimizeSignalDisplaying(boolean optimizeSignalDisplaying) {
		this.optimizeSignalDisplaying = optimizeSignalDisplaying;
		repaint();
	}

	@Override
	public double getPixelPerSecond() {
		return pixelPerSecond;
	}

	@Override
	public double getPixelPerBlock() {
		return pixelPerBlock;
	}

	@Override
	public double getPixelPerPage() {
		return pixelPerPage;
	}

	@Override
	public double getPixelPerValue() {
		return pixelPerValue;
	}

	public double getPixelPerValue(int channel) {
		return channelsPlotOptionsModel.getPixelsPerValue(channel);
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public int getPageCount() {
		return pageCount;
	}

	public int getWholePageCount() {
		return wholePageCount;
	}

	@Override
	public int getBlockCount() {
		return blockCount;
	}

	@Override
	public float getMaxTime() {
		return maxTime;
	}

	@Override
	public int getBlocksPerPage() {
		return blocksPerPage;
	}

	@Override
	public float getPageSize() {
		return pageSize;
	}

	@Override
	public float getBlockSize() {
		return blockSize;
	}

	@Override
	public float getSamplingFrequency() {
		return signalChain.getSamplingFrequency();
	}

	public SignalPlotColumnHeader getSignalPlotColumnHeader() {
		return signalPlotColumnHeader;
	}

	public SignalPlotRowHeader getSignalPlotRowHeader() {
		return signalPlotRowHeader;
	}

	public SignalPlotCorner getSignalPlotCorner() {
		return signalPlotCorner;
	}

	public JLabel getSignalPlotTitleLabel() {
		if (signalPlotTitleLabel == null) {
			signalPlotTitleLabel = new JLabel();
			signalPlotTitleLabel.setFont(signalPlotTitleLabel.getFont().deriveFont(Font.PLAIN, 10F));
			updateSignalPlotTitleLabel();
		}
		return signalPlotTitleLabel;
	}

	public void updateSignalPlotTitleLabel() {

		if (signalPlotTitleLabel != null) {

			String title;

			if (masterPlot == null) {

				title = "";

			} else {

				String montageString;
				if (localMontage == null) {
					montageString = _("montage from document");
				} else {
					montageString = _("modified montage");
				}

				String hSynchroString = horizontalLock ? _("on") : _("off");
				String vSynchroString = verticalLock ? _("on") : _("off");

				title = _R("Auxiliary signal plot ({0}, horizontal synchro {1}, vertical synchro {2})", montageString, hSynchroString, vSynchroString);

			}

			signalPlotTitleLabel.setText(title);

		}

	}

	public JLabel getSignalPlotSynchronizationLabel() {
		if (signalPlotSynchronizationLabel == null) {
			signalPlotSynchronizationLabel = new JLabel();
			signalPlotSynchronizationLabel.setFont(signalPlotSynchronizationLabel.getFont().deriveFont(Font.PLAIN, 10F));
			signalPlotSynchronizationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			updateSignalPlotSynchronizationLabel();
		}
		return signalPlotSynchronizationLabel;
	}

	public void updateSignalPlotSynchronizationLabel() {
		if (signalPlotSynchronizationLabel != null) {

			String text;
			Color color;

			if (masterPlot == null) {

				text = "";
				color = Color.BLACK;

			} else {

				Point viewportPosition = viewport.getViewPosition();
				Point masterViewportPosition = masterPlot.getViewport().getViewPosition();

				int pixelDiff = viewportPosition.x - masterViewportPosition.x;
				if (horizontalLock && pixelDiff != horizontalPixelLead) {
					color = Color.RED;
				} else {
					color = Color.BLACK;
				}

				if (viewportPosition.x == masterViewportPosition.x) {
					text = _("synchronized");
				} else {
					float timeDiff = toTimeSpace(viewportPosition) - masterPlot.toTimeSpace(masterViewportPosition);
					if (timeDiff < 0) {
						text = _R("trailing by {0,number,#.##}s", -timeDiff);
					} else {
						text = _R("leading by {0,number,#.##}s", timeDiff);
					}
				}

			}

			signalPlotSynchronizationLabel.setForeground(color);
			signalPlotSynchronizationLabel.setText(text);

		}
	}

	public void synchronizeToMaster() {
		if (masterPlot != null) {

			boolean oldHorizontalLock = horizontalLock;
			boolean oldVerticalLock = verticalLock;

			try {
				horizontalLock = false;
				verticalLock = false;
				viewport.setViewPosition(masterPlot.getViewport().getViewPosition());
				horizontalTimeLead = 0;
				horizontalPixelLead = 0;
				verticalValueLead = 0;
				verticalPixelLead = 0;
			} finally {
				horizontalLock = oldHorizontalLock;
				verticalLock = oldVerticalLock;
			}

		}
	}

	public JViewport getViewport() {
		return viewport;
	}

	public void setViewport(JViewport viewport) {
		if (viewport == null) {
			throw new NullPointerException("No viewport");
		}
		if (this.viewport != viewport) {
			if (this.viewport != null) {
				this.viewport.removeChangeListener(this);
			}
			this.viewport = viewport;
			if (viewport != null) {
				viewport.addChangeListener(this);
			}
			synchronizeToMaster();
		}
	}

	@Override
	public int getMaxSampleCount() {
		return maxSampleCount;
	}

	@Override
	public SignalDocument getDocument() {
		return document;
	}

	public int[] getChannelLevel() {
		return channelLevel;
	}

	@Override
	public SignalView getView() {
		return view;
	}

	public SignalPlotPopupProvider getPopupMenuProvider() {
		return popupMenuProvider;
	}

	public void setPopupMenuProvider(SignalPlotPopupProvider popupMenuProvider) {
		this.popupMenuProvider = popupMenuProvider;
		signalPlotColumnHeader.setSignalViewPopupProvider(popupMenuProvider);
	}



	@Override
	public SignalPlot getMasterPlot() {
		return masterPlot;
	}

	public boolean isMaster() {
		return(masterPlot == null);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedSignalPlot#tagSelection(org.signalml.plugin.export.signal.ExportedTagDocument, org.signalml.plugin.export.signal.TagStyle, org.signalml.plugin.export.signal.ExportedSignalSelection, boolean)
	 */
	@Override
	public void tagSelection(ExportedTagDocument tagDocument, ExportedTagStyle style,
							 ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException {
		if (tagDocument instanceof TagDocument)
			tagSelection((TagDocument) tagDocument, new TagStyle(style), new SignalSelection(selection), selectNew);
		else throw new InvalidClassException("only document got from SvarogAccess can be used");

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedSignalPlot#eraseTagsFromSelection(org.signalml.plugin.export.signal.ExportedTagDocument, org.signalml.plugin.export.signal.ExportedSignalSelection)
	 */
	@Override
	public void eraseTagsFromSelection(ExportedTagDocument tagDocument,
									   ExportedSignalSelection selection) throws InvalidClassException {
		if (tagDocument instanceof TagDocument)
			eraseTagsFromSelection((TagDocument) tagDocument,  new SignalSelection(selection));
		else throw new InvalidClassException("only document got from SvarogAccess can be used");

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedSignalPlot#tagPageSelection(org.signalml.plugin.export.signal.ExportedTagDocument, org.signalml.plugin.export.signal.TagStyle, org.signalml.plugin.export.signal.ExportedSignalSelection, boolean)
	 */
	@Override
	public void tagPageSelection(ExportedTagDocument tagDocument,
								 ExportedTagStyle style, ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException {
		if (tagDocument instanceof TagDocument)
			tagPageSelection((TagDocument) tagDocument, new TagStyle(style), new SignalSelection(selection), selectNew);
		else throw new InvalidClassException("only document got from SvarogAccess can be used");

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedSignalPlot#tagBlockSelection(org.signalml.plugin.export.signal.ExportedTagDocument, org.signalml.plugin.export.signal.TagStyle, org.signalml.plugin.export.signal.ExportedSignalSelection, boolean)
	 */
	@Override
	public void tagBlockSelection(ExportedTagDocument tagDocument,
								  ExportedTagStyle style, ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException {
		if (tagDocument instanceof TagDocument)
			tagBlockSelection((TagDocument) tagDocument, new TagStyle(style), new SignalSelection(selection), selectNew);
		else throw new InvalidClassException("only document got from SvarogAccess can be used");

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedSignalPlot#tagChannelSelection(org.signalml.plugin.export.signal.ExportedTagDocument, org.signalml.plugin.export.signal.TagStyle, org.signalml.plugin.export.signal.ExportedSignalSelection, boolean)
	 */
	@Override
	public void tagChannelSelection(ExportedTagDocument tagDocument,
									ExportedTagStyle style, ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException {
		if (tagDocument instanceof TagDocument)
			tagChannelSelection((TagDocument) tagDocument, new TagStyle(style), new SignalSelection(selection), selectNew);
		else throw new InvalidClassException("only document got from SvarogAccess can be used");
	}

	public ChannelsPlotOptionsModel getChannelsPlotOptionsModel() {
		return this.channelsPlotOptionsModel;
	}
}
