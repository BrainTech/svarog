package org.signalml.app.view.book;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.domain.book.SegmentReconstructionProvider;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.domain.book.WignerMapProvider;
import org.signalml.domain.book.WignerMapScaleType;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** BookPlot
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookPlot extends JComponent implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(BookPlot.class);

	public static final int RECONSTRUCTION_GAP = 5;
	public static final int RECONSTRUCTION_HEIGHT = 40;
	public static final int MIN_RECONSTRUCTION_HEIGHT = 20;
	public static final int MAX_RECONSTRUCTION_HEIGHT = 100;

	public static final int LEGEND_WIDTH = 75;
	public static final int SCALE_WIDTH = 15;

	private static final Dimension MINIMUM_SIZE = new Dimension(300, 3 *(RECONSTRUCTION_HEIGHT+RECONSTRUCTION_GAP) + 200);

	// 10 ranges + marginal
	public static final int Y_TICK_COUNT = 11;
	public static final int X_TICK_COUNT = 11;

	public static final int X_AXIS_HEIGHT = 6;
	public static final int Y_AXIS_WIDTH = 8;

	public static final int MAX_ASPECT_RATIO_SCALE = 4;

	private DecimalFormat axisFormat = new DecimalFormat("0.00");
	private DecimalFormat toolTipFormat = new DecimalFormat("0.00");

	private GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD,50000);

	private BookPlotPopupProvider popupMenuProvider;
	private BookView view;

	private MessageSourceAccessor messageSource;
	private PleaseWaitDialog pleaseWaitDialog;

	private StandardBookSegment segment;

	private WignerMapProvider wignerMapProvider;
	private SegmentReconstructionProvider reconstructionProvider;
	private WignerMapImageProvider imageProvider;
	private WignerMapPalette palette;

	private boolean signalAntialiased;
	private boolean originalSignalVisible;
	private boolean fullReconstructionVisible;
	private boolean reconstructionVisible;
	private boolean legendVisible;
	private boolean scaleVisible;
	private boolean axesVisible;
	private boolean atomToolTipsVisible;

	private int mapAspectRatioUp = 2;
	private int mapAspectRatioDown = 1;

	private double mapAspectRatio; // width/height

	private int reconstructionHeight = RECONSTRUCTION_HEIGHT;

	private double reconstructionPixelPerSample;
	private double reconstructionPixelPerValue;

	private double mapPixelPerSecond;
	private double mapPixelPerHz;

	private double mapPixelPerPoint;
	private double mapPixelPerNaturalFreq;

	private Rectangle mapRectangle;
	private Rectangle originalSignalRectangle;
	private Rectangle reconstructionRectangle;
	private Rectangle fullReconstructionRectangle;
	private Rectangle legendRectangle;
	private Rectangle scaleRectangle;
	private Rectangle xAxisRectangle;
	private Rectangle yAxisRectangle;

	private String[] yLabels;
	private Rectangle[] yLabelRectangles;
	private int[] yTickOffsets;

	private String[] xLabels;
	private Rectangle[] xLabelRectangles;
	private int[] xTickOffsets;

	private float samplingFrequency;

	private double maxPosition;
	private double minPosition;

	private double minFrequency;
	private double maxFrequency;

	private int reconstructionSampleCount;

	private BufferedImage cachedImage = null;

	private boolean calculated = false;

	private String cachedToolTipText;
	private StandardBookAtom cachedToolTipAtom;

	private StandardBookAtom outlinedAtom;

	private int segmentLength;

	private int naturalMinFrequency;

	private int naturalMaxFrequency;

	private int pointMinPosition;

	private int pointMaxPosition;

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Initialization & setup */

	public BookPlot(BookView view) throws SignalMLException {
		super();
		this.view = view;

		setBorder(new EmptyBorder(5,5,5,5));
		setFont(new Font(Font.DIALOG, Font.PLAIN, 10));

		messageSource = view.getMessageSource();

		view.addPropertyChangeListener(this);

		StandardBook book = view.getDocument().getBook();
		int segmentCount = book.getSegmentCount();
		samplingFrequency = book.getSamplingFrequency();

		wignerMapProvider = new WignerMapProvider(samplingFrequency);

		StandardBookSegment firstSegment = (segmentCount > 0 ? book.getSegmentAt(0,0) : null);
		wignerMapProvider.setRange(0, samplingFrequency/2, 0, firstSegment != null ? firstSegment.getSegmentTimeLength() : 20.0);

		reconstructionProvider = new SegmentReconstructionProvider();

		setBackground(Color.WHITE);

		setFocusable(true);

		ApplicationConfiguration config = view.getApplicationConfig();

		signalAntialiased = config.isSignalAntialiased();
		reconstructionVisible = config.isReconstructionVisible();
		fullReconstructionVisible = config.isFullReconstructionVisible();
		originalSignalVisible = config.isSignalAntialiased();
		legendVisible = config.isLegendVisible();
		scaleVisible = config.isScaleVisible();
		axesVisible = config.isAxesVisible();

		mapAspectRatioUp = config.getMapAspectRatioUp();
		mapAspectRatioDown = config.getMapAspectRatioDown();

		mapAspectRatio = ((double) mapAspectRatioUp) / ((double) mapAspectRatioDown);

		reconstructionHeight = config.getReconstructionHeight();

		palette = config.getPalette();
		setScaleType(config.getScaleType());
		imageProvider = new WignerMapImageProvider();

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopupMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopupMenu(e);
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

		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {

				Point point = e.getPoint();
				Rectangle outline;

				if (mapRectangle != null && mapRectangle.contains(point)) {
					StandardBookAtom atom = getNearestAtom(point, 20);
					if (outlinedAtom != atom) {
						if (outlinedAtom != null) {
							outline = getOutlineRectangle(outlinedAtom);
							if (outline != null) {
								outline.grow(3,3);
								repaint(outline);
							}
						}
						outlinedAtom = atom;
						if (atom != null) {
							outline = getOutlineRectangle(atom);
							if (outline != null) {
								outline.grow(3,3);
								repaint(outline);
							}
						}
					}
				}

			}

		});

	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		calculated = false;
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		calculated = false;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		calculated = false;
	}


	public void initialize() throws SignalMLException {
		reset();
	}

	private void calculateParameters() {

		if (calculated) {
			return;
		}

		if (segment == null) {
			return;
		}

		Graphics2D g = (Graphics2D) getGraphics();
		FontRenderContext fontRenderContext = g.getFontRenderContext();
		Font font = getFont();
		FontMetrics fontMetrics = g.getFontMetrics(font);
		int ascent = fontMetrics.getAscent();

		Dimension size = getSize();
		if (size == null || size.height == 0 || size.width == 0) {
			return;
		}

		float segmentTimeLength = segment.getSegmentTimeLength();
		maxPosition = wignerMapProvider.getMaxPosition();
		minPosition = wignerMapProvider.getMinPosition();
		minFrequency = wignerMapProvider.getMinFrequency();
		maxFrequency = wignerMapProvider.getMaxFrequency();
		if (maxPosition > segmentTimeLength) {
			maxPosition = segmentTimeLength;
			wignerMapProvider.setMaxPosition(segmentTimeLength);
		}

		Insets insets = getInsets();
		size = new Dimension(size.width-(insets.left+insets.right), size.height-(insets.top+insets.bottom));

		int availableWidth = size.width;
		int availableHeight = size.height;

		int axesWidth = 0;
		int axesHeight = 0;

		if (axesVisible) {

			// setup y axis;
			axesWidth = Y_AXIS_WIDTH; // for the axis itself;

			int maxLabelWidth = 0;

			double span = maxFrequency - minFrequency;
			double tick = span / (Y_TICK_COUNT-1);

			int i;
			Rectangle2D stringBounds;

			yLabels = new String[Y_TICK_COUNT];
			yLabelRectangles = new Rectangle[Y_TICK_COUNT];

			for (i=0; i<Y_TICK_COUNT; i++) {
				yLabels[i] = axisFormat.format(minFrequency+tick*i);
				stringBounds = font.getStringBounds(yLabels[i], fontRenderContext);
				yLabelRectangles[i] = new Rectangle();
				yLabelRectangles[i].width = (int) stringBounds.getWidth();
				yLabelRectangles[i].height = (int) stringBounds.getHeight();
				if (maxLabelWidth < yLabelRectangles[i].width) {
					maxLabelWidth = yLabelRectangles[i].width;
				}
			}

			axesWidth += maxLabelWidth;

			// setup x axis

			axesHeight = X_AXIS_HEIGHT;

			int maxLabelHeight = 0;

			span = maxPosition - minPosition;
			tick = span / (X_TICK_COUNT-1);

			xLabels = new String[X_TICK_COUNT];
			xLabelRectangles = new Rectangle[X_TICK_COUNT];

			for (i=0; i<X_TICK_COUNT; i++) {
				xLabels[i] = axisFormat.format(minPosition+tick*i);
				stringBounds = font.getStringBounds(xLabels[i], fontRenderContext);
				xLabelRectangles[i] = new Rectangle();
				xLabelRectangles[i].width = (int) stringBounds.getWidth();
				xLabelRectangles[i].height = (int) stringBounds.getHeight();
				if (maxLabelHeight < xLabelRectangles[i].height) {
					maxLabelHeight = xLabelRectangles[i].height;
				}
			}

			axesHeight += maxLabelHeight;

		}

		availableHeight -= axesHeight;

		int legendWidth = 0;

		if (legendVisible) {
			legendWidth = LEGEND_WIDTH;
		}

		int reservedLeftWidth = Math.max(axesWidth, legendWidth);
		if (axesVisible) {
			if (reservedLeftWidth < xLabelRectangles[0].width/2) {
				reservedLeftWidth = xLabelRectangles[0].width/2;
			}
		}

		availableWidth -= reservedLeftWidth;

		int scaleWidth = 0;

		if (scaleVisible) {
			scaleWidth = SCALE_WIDTH;
		}

		int reservedRightWidth = scaleWidth;
		if (axesVisible) {
			if (reservedRightWidth < xLabelRectangles[X_TICK_COUNT-1].width/2) {
				reservedRightWidth = xLabelRectangles[X_TICK_COUNT-1].width/2;
			}
		}

		availableWidth -= reservedRightWidth;

		int reconstructionTop = -1;
		int usedReconstructionHeight = 0;

		if (reconstructionVisible) {

			reconstructionRectangle = new Rectangle();

			reconstructionRectangle.x = insets.left + reservedLeftWidth;
			reconstructionRectangle.width = availableWidth;

			reconstructionRectangle.y = insets.top + availableHeight-reconstructionHeight;
			reconstructionRectangle.height = reconstructionHeight;

			availableHeight -= (reconstructionHeight + RECONSTRUCTION_GAP);

			reconstructionTop = reconstructionRectangle.y;
			usedReconstructionHeight += (reconstructionHeight + RECONSTRUCTION_GAP);

		} else {
			reconstructionRectangle = null;
		}

		if (fullReconstructionVisible) {

			fullReconstructionRectangle = new Rectangle();

			fullReconstructionRectangle.x = insets.left + reservedLeftWidth;
			fullReconstructionRectangle.width = availableWidth;

			fullReconstructionRectangle.y = insets.top + availableHeight-reconstructionHeight;
			fullReconstructionRectangle.height = reconstructionHeight;

			availableHeight -= (reconstructionHeight + RECONSTRUCTION_GAP);

			reconstructionTop = fullReconstructionRectangle.y;
			usedReconstructionHeight += (reconstructionHeight + RECONSTRUCTION_GAP);

		} else {
			fullReconstructionRectangle = null;
		}

		if (originalSignalVisible) {

			originalSignalRectangle = new Rectangle();

			originalSignalRectangle.x = insets.left + reservedLeftWidth;
			originalSignalRectangle.width = availableWidth;

			originalSignalRectangle.y = insets.top + availableHeight-reconstructionHeight;
			originalSignalRectangle.height = reconstructionHeight;

			availableHeight -= (reconstructionHeight + RECONSTRUCTION_GAP);

			reconstructionTop = originalSignalRectangle.y;
			usedReconstructionHeight += (reconstructionHeight + RECONSTRUCTION_GAP);

		} else {
			originalSignalRectangle = null;
		}

		// the map must vertically fit into availableHeight
		int width = (int) Math.round(((double) availableHeight) * mapAspectRatio);
		int height = availableHeight;
		int paddingY = 0;
		int paddingX = 0;

		if (width > availableWidth) {
			width = availableWidth;
			height = (int) Math.round(((double) width) / mapAspectRatio);
			paddingY = (availableHeight-height) / 2;
		} else if (width < size.width) {
			paddingX = (availableWidth-width) / 2;
		}

		int paddingXLeft;

		if (reservedLeftWidth > reservedRightWidth) {
			paddingXLeft = paddingX - Math.max(0, paddingX - reservedLeftWidth);
		} else if (reservedLeftWidth < reservedRightWidth) {
			paddingXLeft = paddingX + Math.max(0, paddingX - reservedRightWidth);
		} else {
			paddingXLeft = paddingX;
		}

		mapRectangle = new Rectangle(insets.left+reservedLeftWidth+paddingXLeft, insets.top+paddingY, width, height);

		if (legendVisible && reconstructionTop >= 0) {
			legendRectangle = new Rectangle(insets.left + paddingXLeft, reconstructionTop, reservedLeftWidth, usedReconstructionHeight);
		} else {
			legendRectangle = null;
		}

		if (scaleVisible) {
			scaleRectangle = new Rectangle(mapRectangle.x + mapRectangle.width, mapRectangle.y, scaleWidth, mapRectangle.height);
		} else {
			scaleRectangle = null;
		}

		if (axesVisible) {
			xAxisRectangle = new Rectangle(mapRectangle.x, mapRectangle.y+mapRectangle.height, mapRectangle.width, axesHeight);
			yAxisRectangle = new Rectangle(mapRectangle.x - axesWidth, mapRectangle.y, axesWidth, mapRectangle.height);
		} else {
			xAxisRectangle = null;
			yAxisRectangle = null;
		}

		if (reconstructionRectangle != null) {
			reconstructionRectangle.x += paddingXLeft;
			reconstructionRectangle.width -= (2 * paddingX);
			reconstructionRectangle.y += (axesHeight-paddingY);
		}

		if (fullReconstructionRectangle != null) {
			fullReconstructionRectangle.x += paddingXLeft;
			fullReconstructionRectangle.width -= (2 * paddingX);
			fullReconstructionRectangle.y += (axesHeight-paddingY);
		}

		if (originalSignalRectangle != null) {
			originalSignalRectangle.x += paddingXLeft;
			originalSignalRectangle.width -= (2 * paddingX);
			originalSignalRectangle.y += (axesHeight-paddingY);
		}

		// position labels
		if (axesVisible) {

			double tickSize = ((double)(width-1)) / (X_TICK_COUNT-1);
			int i;

			xTickOffsets = new int[X_TICK_COUNT];

			for (i=0; i<X_TICK_COUNT; i++) {
				xTickOffsets[i] = (int) Math.round((tickSize*i));
				xLabelRectangles[i].x = mapRectangle.x + xTickOffsets[i] - xLabelRectangles[i].width / 2;
				xLabelRectangles[i].y = mapRectangle.y + mapRectangle.height + X_AXIS_HEIGHT + ascent;
			}

			tickSize = ((double)(height-1)) / (Y_TICK_COUNT-1);

			yTickOffsets = new int[Y_TICK_COUNT];

			for (i=0; i<Y_TICK_COUNT; i++) {
				yTickOffsets[i] = (int) Math.round((tickSize*i));
				yLabelRectangles[i].x = mapRectangle.x - (Y_AXIS_WIDTH + yLabelRectangles[i].width);
				yLabelRectangles[i].y = (mapRectangle.y + mapRectangle.height - 1) + ascent/2 - yTickOffsets[i];
			}

		}

		// determine scales
		double maxDeflection = 0;

		reconstructionSampleCount = (int)((maxPosition-minPosition) * samplingFrequency);
		reconstructionPixelPerSample = ((double)(width-1)) / ((double)(reconstructionSampleCount-1));
		int i;

		double value;

		if (segment.hasSignal()) {
			float[] signalSamples = segment.getSignalSamples();
			for (i=0; i<signalSamples.length; i++) {
				value = Math.abs(signalSamples[i]);
				if (maxDeflection < value) {
					maxDeflection = value;
				}
			}
		}

		reconstructionProvider.setSegmentWithNaturalWidth(segment, samplingFrequency);
		double[] fullReconstruction = reconstructionProvider.getFullReconstruction();
		for (i=0; i<fullReconstruction.length; i++) {
			value = Math.abs(fullReconstruction[i]);
			if (maxDeflection < value) {
				maxDeflection = value;
			}
		}

		reconstructionPixelPerValue = ((double)(reconstructionHeight/2-1)) / maxDeflection;

		wignerMapProvider.setSize(width, height);
		if (wignerMapProvider.isDirty()) {
			cachedImage = null;
		}

		mapPixelPerSecond = ((double)(width-1)) / (maxPosition-minPosition);
		mapPixelPerHz = ((double)(height-1)) / (maxFrequency-minFrequency);

		segmentLength = segment.getSegmentLength();
		naturalMinFrequency = (int) Math.round((minFrequency / samplingFrequency) * segmentLength);
		naturalMaxFrequency = (int) Math.round((maxFrequency / samplingFrequency) * segmentLength);
		pointMinPosition = (int) Math.round(minPosition * samplingFrequency);
		pointMaxPosition = (int) Math.round(maxPosition * samplingFrequency);

		mapPixelPerPoint = ((double)(width-1)) / (pointMaxPosition-pointMinPosition);
		mapPixelPerNaturalFreq = ((double)(height-1)) / (naturalMaxFrequency-naturalMinFrequency);

		calculated = true;

	}

	public StandardBookSegment getSegment() {
		return segment;
	}

	public void setSegment(StandardBookSegment segment) {
		if (this.segment != segment) {
			this.segment = segment;
			wignerMapProvider.setSegment(segment);
			outlinedAtom = null;
			reset();
		}
	}

	public boolean isOriginalSignalVisible() {
		return originalSignalVisible;
	}

	public void setOriginalSignalVisible(boolean originalSignalVisible) {
		if (this.originalSignalVisible != originalSignalVisible) {
			this.originalSignalVisible = originalSignalVisible;
			reset();
		}
	}

	public boolean isReconstructionVisible() {
		return reconstructionVisible;
	}

	public void setReconstructionVisible(boolean reconstructionVisible) {
		if (this.reconstructionVisible != reconstructionVisible) {
			this.reconstructionVisible = reconstructionVisible;
			reset();
		}
	}

	public boolean isFullReconstructionVisible() {
		return fullReconstructionVisible;
	}

	public void setFullReconstructionVisible(boolean fullReconstructionVisible) {
		if (this.fullReconstructionVisible != fullReconstructionVisible) {
			this.fullReconstructionVisible = fullReconstructionVisible;
			reset();
		}
	}

	public boolean isLegendVisible() {
		return legendVisible;
	}

	public void setLegendVisible(boolean legendVisible) {
		if (this.legendVisible != legendVisible) {
			this.legendVisible = legendVisible;
			reset();
		}
	}

	public boolean isScaleVisible() {
		return scaleVisible;
	}

	public void setScaleVisible(boolean scaleVisible) {
		if (this.scaleVisible != scaleVisible) {
			this.scaleVisible = scaleVisible;
			reset();
		}
	}

	public boolean isAxesVisible() {
		return axesVisible;
	}

	public void setAxesVisible(boolean axesVisible) {
		if (this.axesVisible != axesVisible) {
			this.axesVisible = axesVisible;
			reset();
		}
	}

	public boolean isSignalAntialiased() {
		return signalAntialiased;
	}

	public void setSignalAntialiased(boolean signalAntialiased) {
		if (this.signalAntialiased != signalAntialiased) {
			this.signalAntialiased = signalAntialiased;
			if (originalSignalRectangle != null) {
				repaint(originalSignalRectangle);
			}
			if (fullReconstructionRectangle != null) {
				repaint(fullReconstructionRectangle);
			}
			if (reconstructionRectangle != null) {
				repaint(reconstructionRectangle);
			}
		}
	}

	public boolean isAtomToolTipsVisible() {
		return atomToolTipsVisible;
	}

	public void setAtomToolTipsVisible(boolean atomToolTipsVisible) {
		if (this.atomToolTipsVisible != atomToolTipsVisible) {
			this.atomToolTipsVisible = atomToolTipsVisible;
			if (atomToolTipsVisible) {
				setToolTipText("");
			} else {
				setToolTipText(null);
			}
		}
	}

	public int getMapAspectRatioUp() {
		return mapAspectRatioUp;
	}

	public void setMapAspectRatioUp(int mapAspectRatioUp) {
		if (mapAspectRatioUp < 1) {
			mapAspectRatioUp = 1;
		}
		else if (mapAspectRatioUp > MAX_ASPECT_RATIO_SCALE) {
			mapAspectRatioUp = MAX_ASPECT_RATIO_SCALE;
		}
		this.mapAspectRatioUp = mapAspectRatioUp;
		setMapAspectRatio(((double) mapAspectRatioUp) / ((double) mapAspectRatioDown));
	}

	public int getMapAspectRatioDown() {
		return mapAspectRatioDown;
	}

	public void setMapAspectRatioDown(int mapAspectRatioDown) {
		if (mapAspectRatioDown < 1) {
			mapAspectRatioDown = 1;
		}
		else if (mapAspectRatioDown > MAX_ASPECT_RATIO_SCALE) {
			mapAspectRatioDown = MAX_ASPECT_RATIO_SCALE;
		}
		this.mapAspectRatioDown = mapAspectRatioDown;
		setMapAspectRatio(((double) mapAspectRatioUp) / ((double) mapAspectRatioDown));
	}

	public double getMapAspectRatio() {
		return mapAspectRatio;
	}

	protected void setMapAspectRatio(double mapAspectRatio) {
		if (this.mapAspectRatio != mapAspectRatio) {
			this.mapAspectRatio = mapAspectRatio;
			reset();
		}
	}

	public WignerMapPalette getPalette() {
		return palette;
	}

	public void setPalette(WignerMapPalette palette) {
		if (this.palette != palette) {
			this.palette = palette;
			cachedImage = null;
			if (mapRectangle != null) {
				repaint(mapRectangle);
			}
			if (scaleRectangle != null) {
				repaint(scaleRectangle);
			}
		}
	}

	public WignerMapScaleType getScaleType() {
		return wignerMapProvider.getScaleType();
	}

	public void setScaleType(WignerMapScaleType type) {
		if (wignerMapProvider.getScaleType() != type) {
			wignerMapProvider.setScaleType(type);
			cachedImage = null;
			if (mapRectangle != null) {
				repaint(mapRectangle);
			}
		}
	}

	public int getReconstructionHeight() {
		return reconstructionHeight;
	}

	public void setReconstructionHeight(int reconstructionHeight) {
		if (this.reconstructionHeight != reconstructionHeight) {
			this.reconstructionHeight = reconstructionHeight;
			view.getReconstructionHeightSlider().setValue(reconstructionHeight);
			reset();
		}
	}

	public void setZoom(double minPosition, double maxPosition, double minFrequency, double maxFrequency) {

		this.reconstructionPixelPerSample *= (maxPosition - minPosition) / (this.maxPosition - this.minPosition);

		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.minPosition = minPosition;
		this.maxPosition = maxPosition;

		wignerMapProvider.setRange(minFrequency, maxFrequency, minPosition, maxPosition);
		if (wignerMapProvider.isDirty()) {
			calculated = false;
			repaint();
		}

	}

	public void destroy() {
		view.removePropertyChangeListener(this);
		setVisible(false);
		segment = null;
		view = null;
	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* JComponent implementation */

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D)gOrig;
		Rectangle clip = g.getClipBounds();

		g.setColor(getBackground());
		g.fillRect(clip.x,clip.y,clip.width,clip.height);

		if (!calculated) {
			calculateParameters();
		}

		if (segment == null) {
			return;
		}

		if (mapRectangle != null) {
			Rectangle mapToRepaint = clip.intersection(mapRectangle);
			if (!mapToRepaint.isEmpty()) {
				paintWignerMap(g, mapToRepaint);
			}
		}

		if (legendRectangle != null) {
			if (legendRectangle.intersects(clip)) {
				paintLegend(g);
			}
		}

		if (scaleRectangle != null) {
			if (scaleRectangle.intersects(clip)) {
				paintScale(g);
			}
		}

		if (xAxisRectangle != null) {
			if (xAxisRectangle.intersects(clip)) {
				paintXAxis(g);
			}
		}

		if (yAxisRectangle != null) {
			if (yAxisRectangle.intersects(clip)) {
				paintYAxis(g);
			}
		}

		if (reconstructionRectangle != null) {
			Rectangle reconstructionToRepaint = clip.intersection(reconstructionRectangle);
			if (!reconstructionToRepaint.isEmpty()) {
				double[] selectiveReconstruction = reconstructionProvider.getSelectiveReconstruction();
				paintReconstruction(g, selectiveReconstruction, reconstructionRectangle, reconstructionToRepaint);
			}
		}

		if (fullReconstructionRectangle != null) {
			Rectangle fullReconstructionToRepaint = clip.intersection(fullReconstructionRectangle);
			if (!fullReconstructionToRepaint.isEmpty()) {
				double[] reconstruction = reconstructionProvider.getFullReconstruction();
				paintReconstruction(g, reconstruction, fullReconstructionRectangle, fullReconstructionToRepaint);
			}
		}

		if (originalSignalRectangle != null) {
			Rectangle originalSignalToRepaint = clip.intersection(originalSignalRectangle);
			if (!originalSignalToRepaint.isEmpty()) {
				float[] signal = segment.getSignalSamples();
				// XXX not optimal
				double[] signalD = null;
				if (signal != null) {
					signalD = new double[signal.length];
					for (int i=0; i<signal.length; i++) {
						signalD[i] = signal[i];
					}
				}
				paintReconstruction(g, signalD, originalSignalRectangle, originalSignalToRepaint);
			}
		}

	}

	private void paintYAxis(Graphics2D g) {

		g.setColor(Color.BLACK);

		int axisLevel = mapRectangle.x-3;

		g.drawLine(axisLevel, mapRectangle.y, axisLevel, mapRectangle.y + mapRectangle.height - 1);

		for (int i=0; i<Y_TICK_COUNT; i++) {

			g.drawLine(axisLevel-2, mapRectangle.y + yTickOffsets[i], axisLevel+2, mapRectangle.y + yTickOffsets[i]);
			g.drawString(yLabels[i], yLabelRectangles[i].x, yLabelRectangles[i].y);

		}

	}

	private void paintXAxis(Graphics2D g) {

		g.setColor(Color.BLACK);

		int axisLevel = mapRectangle.y + mapRectangle.height + 2;

		g.drawLine(mapRectangle.x, axisLevel, mapRectangle.x + mapRectangle.width - 1, axisLevel);

		for (int i=0; i<X_TICK_COUNT; i++) {

			g.drawLine(mapRectangle.x + xTickOffsets[i], axisLevel-2, mapRectangle.x + xTickOffsets[i], axisLevel+2);
			g.drawString(xLabels[i], xLabelRectangles[i].x, xLabelRectangles[i].y);

		}

	}

	private void paintScale(Graphics2D g) {

		int[] pal = palette.getPalette();

		int height = Math.min(scaleRectangle.height, pal.length);
		double factor = 1;
		if (height < pal.length) {
			factor = ((double)(pal.length-1)) / (height-1);
		}

		int index;
		Color color;

		for (int i=0; i<height; i++) {
			if (factor != 1) {
				index = (int)(i * factor);
			} else {
				index = i;
			}
			color = new Color(pal[index], false);
			g.setColor(color);
			g.drawLine(scaleRectangle.x+5, scaleRectangle.y + (scaleRectangle.height-i), scaleRectangle.x + scaleRectangle.width-1, scaleRectangle.y + (scaleRectangle.height-i));

		}

	}

	private void paintReconstruction(Graphics2D gOrig, double[] samples, Rectangle area, Rectangle areaToRepaint) {

		Graphics2D g = (Graphics2D) gOrig.create();
		g.clip(area);

		if (samples == null) {

			String label = messageSource.getMessage("bookView.noSignalToPaint");
			Rectangle2D stringBounds;
			int width;
			int height;
			int level;

			Font font = g.getFont();
			FontRenderContext fontRenderContext = g.getFontRenderContext();
			FontMetrics fontMetrics = g.getFontMetrics(font);

			g.setColor(Color.BLACK);

			level = area.y + (area.height) / 2;

			stringBounds = font.getStringBounds(label, fontRenderContext);

			width = (int) stringBounds.getWidth();
			height = (int) stringBounds.getHeight();

			g.drawString(label, area.x + (area.width - width)/2, level + fontMetrics.getAscent() - height/2);

			return;

		}

		if (signalAntialiased) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		}

		int relX = areaToRepaint.x - area.x;
//		int relEndX = relX + areaToRepaint.width - 1;
		int level = area.y + area.height / 2;

		g.setColor(Color.BLUE);
		g.drawLine(areaToRepaint.x, level, areaToRepaint.x+areaToRepaint.width-1, level);

		g.setColor(Color.BLACK);

		//int firstSample = (int) Math.max( 0, Math.floor( ((double) (relX-1)) / reconstructionPixelPerSample ) - 1 );
		int firstSample = (int)((minPosition / segment.getSegmentLength()) * samples.length);
		int lastSample = (int) Math.min(samples.length - 1, (maxPosition / segment.getSegmentLength()) * samples.length);
		if (lastSample < firstSample) {
			return;
		}
		int length = 1 + lastSample - firstSample;

		double realX = area.x + relX * reconstructionPixelPerSample;
		double y = level - (samples[firstSample] * reconstructionPixelPerValue);

		generalPath.reset();

		double x;
		double lastX = 0;
		double lastY = 0;

		if (!signalAntialiased) {

			x = StrictMath.floor(realX + 0.5);
			y = StrictMath.floor(y + 0.5);

			generalPath.moveTo(x, y);

			lastX = x;
			lastY = y;

		} else {

			generalPath.moveTo(realX, y);

		}

		for (int i=0; i<length; i++) {

			y = level - (samples[firstSample+i] * reconstructionPixelPerValue);


			realX += reconstructionPixelPerSample;


			if (signalAntialiased) {

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

	}

	private void paintLegend(Graphics2D g) {

		String label;
		Rectangle2D stringBounds;
		int width;
		int height;
		int level;

		Font font = g.getFont();
		FontRenderContext fontRenderContext = g.getFontRenderContext();
		FontMetrics fontMetrics = g.getFontMetrics(font);

		g.setColor(Color.BLACK);

		if (originalSignalRectangle != null) {

			level = originalSignalRectangle.y + (originalSignalRectangle.height) / 2;

			label = messageSource.getMessage("bookView.originalSignal");
			stringBounds = font.getStringBounds(label, fontRenderContext);

			width = (int) stringBounds.getWidth();
			height = (int) stringBounds.getHeight();

			g.drawString(label, originalSignalRectangle.x - (width + 5), level + fontMetrics.getAscent() - height/2);

		}

		if (fullReconstructionRectangle != null) {

			level = fullReconstructionRectangle.y + (fullReconstructionRectangle.height) / 2;

			label = messageSource.getMessage("bookView.reconstructionSignal");
			stringBounds = font.getStringBounds(label, fontRenderContext);

			width = (int) stringBounds.getWidth();
			height = (int) stringBounds.getHeight();

			g.drawString(label, fullReconstructionRectangle.x - (width + 5), level + fontMetrics.getAscent() - height/2);

		}

		if (reconstructionRectangle != null) {

			level = reconstructionRectangle.y + (reconstructionRectangle.height) / 2;

			label = messageSource.getMessage("bookView.chosenSignal");
			stringBounds = font.getStringBounds(label, fontRenderContext);

			width = (int) stringBounds.getWidth();
			height = (int) stringBounds.getHeight();

			g.drawString(label, reconstructionRectangle.x - (width + 5), level + fontMetrics.getAscent() - height/2);

		}

	}

	private void paintWignerMap(Graphics2D gOrig, Rectangle mapToRepaint) {

		// limit clipping to the map to prevent elements from protruding outside the map
		Graphics2D g = (Graphics2D) gOrig.create();
		g.clip(new Rectangle(mapRectangle.x, mapRectangle.y, mapRectangle.width, mapRectangle.height));

		if (cachedImage == null) {

			double[][] map = null;
			map = wignerMapProvider.getMap();

			cachedImage = imageProvider.getImage(map, mapRectangle.width, mapRectangle.height, palette);

		}

		int imgX = mapToRepaint.x - mapRectangle.x;
		int imgY = mapToRepaint.y - mapRectangle.y;

		g.drawImage(
		        cachedImage,
		        mapToRepaint.x,
		        mapToRepaint.y,
		        mapToRepaint.x+mapToRepaint.width,
		        mapToRepaint.y+mapToRepaint.height,
		        imgX,
		        imgY,
		        imgX+mapToRepaint.width,
		        imgY+mapToRepaint.height,
		        null
		);

		Rectangle markCaptureRectangle = new Rectangle(mapToRepaint);
		markCaptureRectangle.grow(3,3);

		g.setColor(Color.WHITE);

		int atomCount = segment.getAtomCount();
		Point atomPoint;
		StandardBookAtom atom;

		for (int i=0; i<atomCount; i++) {
			atom = segment.getAtomAt(i);
			atomPoint = getAtomLocation(atom);
			if (atomPoint != null && markCaptureRectangle.contains(atomPoint)) {
				g.drawLine(atomPoint.x-2, atomPoint.y, atomPoint.x+2, atomPoint.y);
				g.drawLine(atomPoint.x, atomPoint.y-2, atomPoint.x, atomPoint.y+2);

				if (reconstructionProvider.isAtomInSelectiveReconstruction(atom)) {
					g.drawOval(atomPoint.x-3, atomPoint.y-3, 6, 6);
				}

			}

		}

		if (outlinedAtom != null) {

			Rectangle outline = getOutlineRectangle(outlinedAtom);

			if (outline != null) {

				Stroke stroke = g.getStroke();

				try {
					g.setStroke(new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[] { 1, 3 }, 0F));
					g.draw(outline);
				} finally {
					g.setStroke(stroke);
				}
			}

		}

	}

	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
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
	public String getToolTipText(MouseEvent event) {

		Point point = event.getPoint();

		StandardBookAtom nearestAtom = getNearestAtom(point, 20);

		if (nearestAtom != null) {

			if (cachedToolTipText == null || cachedToolTipAtom != nearestAtom) {

				StringBuilder sb = new StringBuilder("<html><body>");
				sb.append("<b>")
				.append(messageSource.getMessage("bookView.toolTip.atom", new Object[] { segment.indexOfAtom(nearestAtom)+1 }))
				.append("</b>");

				sb.append("<p><table cellpadding=\"0\">");
				sb.append("<tr><td>")
				.append(messageSource.getMessage("bookView.toolTip.position"))
				.append("</td><td>&nbsp;</td><td>")
				.append(toolTipFormat.format(nearestAtom.getTimePosition()))
				.append("</td></tr>");

				sb.append("<tr><td>")
				.append(messageSource.getMessage("bookView.toolTip.frequency"))
				.append("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>")
				.append(toolTipFormat.format(nearestAtom.getFrequency()))
				.append("</td></tr>");

				sb.append("<tr><td>")
				.append(messageSource.getMessage("bookView.toolTip.modulus"))
				.append("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>")
				.append(toolTipFormat.format(nearestAtom.getModulus()))
				.append("</td></tr>");

				sb.append("<tr><td>")
				.append(messageSource.getMessage("bookView.toolTip.amplitude"))
				.append("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>")
				.append(toolTipFormat.format(nearestAtom.getAmplitude()))
				.append("</td></tr>");

				sb.append("<tr><td>")
				.append(messageSource.getMessage("bookView.toolTip.scale"))
				.append("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>")
				.append(toolTipFormat.format(nearestAtom.getScale()))
				.append("</td></tr>");

				sb.append("<tr><td>")
				.append(messageSource.getMessage("bookView.toolTip.phase"))
				.append("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;</td><td>")
				.append(toolTipFormat.format(nearestAtom.getPhase()))
				.append("</td></tr>");

				sb.append("</table>");
				sb.append("</body></html>");

				cachedToolTipAtom = nearestAtom;
				cachedToolTipText = sb.toString();

			}

			return cachedToolTipText;

		} else {
			return messageSource.getMessage("bookView.toolTip.noNearbyAtom");
		}

	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Listener implementations */

	public void reset() {
		calculated = false;
		repaint();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Conversions */

	public StandardBookAtom getNearestAtom(Point point, int maxRadius) {

		if (mapRectangle == null || !mapRectangle.contains(point)) {
			return null;
		}

		if (segment == null) {
			return null;
		}

		int atomCount = segment.getAtomCount();
		double minDistance = Double.MAX_VALUE;
		double distance;
		Point atomPoint;
		StandardBookAtom atom;
		StandardBookAtom nearestAtom = null;
		for (int i=0; i<atomCount; i++) {
			atom = segment.getAtomAt(i);
			atomPoint = getAtomLocation(atom);
			if (atomPoint != null) {
				distance = point.distance(atomPoint);
				if (maxRadius == 0 || maxRadius >= distance) {
					if (distance < minDistance) {
						minDistance = distance;
						nearestAtom = atom;
					}
				}
			}
		}

		return nearestAtom;

	}

	public Point getAtomLocation(StandardBookAtom atom) {

		if (mapRectangle == null || segment == null) {
			return null;
		}

		int frequency = atom.getFrequency();
		if (frequency < naturalMinFrequency || frequency > naturalMaxFrequency) {
			return null;
		}

		int position = atom.getPosition();
		if (position < pointMinPosition || position > pointMaxPosition) {
			return null;
		}

		int x = mapRectangle.x + ((int) Math.round((position-pointMinPosition) * mapPixelPerPoint));
		int y = mapRectangle.y + ((mapRectangle.height-1) - (int) Math.round((frequency-naturalMinFrequency) * mapPixelPerNaturalFreq));

		return new Point(x, y);

	}

	public Rectangle getOutlineRectangle(StandardBookAtom atom) {

		// TODO maybe do this in a more intelligent way
		// can we compute a "width" in frequency

		Point atomPoint = getAtomLocation(atom);
		if (atomPoint != null) {
			return new Rectangle(atomPoint.x-20, atomPoint.y-20, 40, 40);
		} else {
			return null;
		}

	}

	// the following 4 functions use coordinates relative to map origin
	public double toPosition(int x) {
		return minPosition + (x / mapPixelPerSecond);
	}

	public double toFrequency(int y) {
		return minFrequency + (((mapRectangle.height-1)-y) / mapPixelPerHz);
	}

	// in time domain, seconds
	public int toX(double position) {
		return (int) Math.round((position-minPosition) * mapPixelPerSecond);
	}

	// in frequency domain, seconds
	public int toY(double frequency) {
		return (mapRectangle.height-1) - (int) Math.round((frequency-minFrequency) * mapPixelPerHz);
	}

	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Selection support */


	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */
	/* ***************** ***************** ***************** */

	/* Other getters and setters */

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public BookView getView() {
		return view;
	}

	public SegmentReconstructionProvider getReconstructionProvider() {
		return reconstructionProvider;
	}

	public BookPlotPopupProvider getPopupMenuProvider() {
		return popupMenuProvider;
	}

	public void setPopupMenuProvider(BookPlotPopupProvider popupMenuProvider) {
		this.popupMenuProvider = popupMenuProvider;
	}

	public Rectangle getMapRectangle() {
		return mapRectangle;
	}

	public Rectangle getReconstructionRectangle() {
		return reconstructionRectangle;
	}

	public WignerMapProvider getWignerMapProvider() {
		return wignerMapProvider;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	public double getMinPosition() {
		return minPosition;
	}

	public double getMaxPosition() {
		return maxPosition;
	}

	public double getMinFrequency() {
		return minFrequency;
	}

	public double getMaxFrequency() {
		return maxFrequency;
	}

}

