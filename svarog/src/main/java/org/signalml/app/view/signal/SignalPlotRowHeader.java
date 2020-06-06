/* SignalPlotRowHeader.java created 2007-10-15
 *
 */
package org.signalml.app.view.signal;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.signalml.app.model.components.ChannelPlotOptionsModel;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.signal.popup.ChannelOptionsPopupDialog;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.system.ChannelFunction;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * SignalPlotRowHeader
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 * Sp. z o.o.
 */
public class SignalPlotRowHeader extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final Dimension MINIMUM_SIZE = new Dimension(0, 0);
	/*
	 * width of channel's options button (for every channel) in pixels
	 */
	private static final int CHANNEL_BUTTON_WIDTH = 10;
	/*
	 * width of value scale's tick (horizontal line) in pixels
	 */
	private static final int SCALE_HORIZONTAL_LINE_WIDTH = 4;
	/*
	 * Infinity value for pixel per row unit
	 */
	private static final double PIXEL_PER_ROW_UNIT_INF = -1.0;
	/*
	 * distance (in pixels) from value scale to its label
	 */
	private static final int LABEL_LINE_DISTANCE = 10;

	private boolean calculated = false;

	private Font normalFont;
	private Font verticalFont;

	private int channelCount;
	private double pixelPerValue;
	private int pixelPerChannel;
	private int[] channelLevel;

	private double pixelPerRowUnit;
	private double[] pixelPerRowUnitForChannels;
	private String rowUnitLabel;
	private String[] rowUnitLabelForChannels;

	private Rectangle2D unitLabelBounds;
	private Rectangle2D[] channelLabelBounds;
	private CompactButton[] channelOptionsButtons;
	private int maxChannelLabelWidth = 0;

	private SignalPlot plot;
	private ImageIcon channelOptionsVisibleIcon;
	private ImageIcon channelOptionsInvisibleIcon;

	private MultichannelSampleSource labelSource;
	private ChannelOptionsPopupDialog channelOptionsPopupDialog;

	private boolean active = true;

	public SignalPlotRowHeader(SignalPlot plot) {
		super();
		this.plot = plot;
		Image iconImage;
		ImageIcon ic;
		iconImage = IconUtils.loadClassPathImage("org/signalml/app/icon/channelOptionsVisible.png");
		ic = new ImageIcon(iconImage);
		this.channelOptionsVisibleIcon = ic;

		iconImage = IconUtils.loadClassPathImage("org/signalml/app/icon/channelOptionsInvisible.png");
		ic = new ImageIcon(iconImage);
		this.channelOptionsInvisibleIcon = ic;

	}

	/*
	 * Sets calculated to false and creates ChannelOptions Buttons.
	 */
	public void reset() {

		calculated = false;

		if (channelOptionsButtons != null) {
			for (int i = 0; i < channelOptionsButtons.length; i++) {
				if (channelOptionsButtons[i] != null) {
					this.remove(channelOptionsButtons[i]);
				}
			}
		}
		channelOptionsButtons = new CompactButton[this.plot.getChannelCount()];
		for (int i = 0; i < this.plot.getChannelCount(); i++) {
			CompactButton b = new CompactButton(
					new ChannelOptionsAction(this.channelOptionsVisibleIcon,
							this.channelOptionsInvisibleIcon,
							_("Edit channel's options"),
							_("Show channel"),
							i));
			channelOptionsButtons[i] = b;
			this.add(b);
		}

		revalidate();
		repaint();
	}

	/*
	 * Determine if there is some channel with its own value scale
	 * @returns true if there exists a channel with its own value scale, false otherwise
	 */
	private boolean hasSpecialChannels() {
		for (int i = 0; i < channelCount; i++) {
			if (pixelPerRowUnitForChannels[i] >= 0) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Fill channels`es data structures: channelLabelBounds,
	 * pixelPerRowUnitForChannels, rowUnitLabelForChannels.
	 */
	private void calculateChannelsData(Graphics2D g) {
		StringBuilder sb;
		ChannelPlotOptionsModel m;
		double max = 0;

		for (int i = 0; i < channelCount; i++) {
			//calculate label bound
			channelLabelBounds[i] = normalFont.getStringBounds(labelSource.getLabel(i), g.getFontRenderContext());
			if (max < channelLabelBounds[i].getWidth()) {
				max = channelLabelBounds[i].getWidth();
			}

			//calculate scaling
			m = this.plot.getChannelsPlotOptionsModel().getModelAt(i);

			double localPixelsPerValue = this.plot.getChannelsPlotOptionsModel().getPixelsPerValue(i);
			double globalPixelPerValue = this.plot.getPixelPerValue();

			if ((localPixelsPerValue != globalPixelPerValue) && (m.getVisible())) {
				pixelPerRowUnitForChannels[i] = this.plot.getPixelPerChannel() * m.getVoltageScale() * this.plot.getVoltageZoomFactorRatioFor(i);
				sb = new StringBuilder("1");
				if (pixelPerRowUnitForChannels[i] <= 0.0) {
					sb.append("000...");
					pixelPerRowUnitForChannels[i] = 0.0;
				} else {
					while (pixelPerRowUnitForChannels[i] <= 5) {
						pixelPerRowUnitForChannels[i] *= 10;
						sb.append("0");
					}
				};
				sb.append(" " + plot.getSourceChannelFor(i).getFunction().getUnitOfMeasurementSymbol());
				rowUnitLabelForChannels[i] = sb.toString();
			} else {
				pixelPerRowUnitForChannels[i] = PIXEL_PER_ROW_UNIT_INF;
			}
		}
		maxChannelLabelWidth = (int) Math.ceil(max);
	}

	private void calculate(Graphics2D g) {

		if (calculated) {
			return;
		}

		channelCount = plot.getChannelCount();
		pixelPerValue = plot.getPixelPerValue();
		pixelPerChannel = plot.getPixelPerChannel();
		labelSource = plot.getSignalOutput();
		channelLevel = plot.getChannelLevel();

		pixelPerRowUnit = pixelPerValue;

		StringBuilder sb = new StringBuilder("1");
		if (pixelPerRowUnit <= 0.0) {
			pixelPerRowUnit = 0.0;
			sb.append("000...");
		} else {

			while (pixelPerRowUnit <= 20) {
				pixelPerRowUnit *= 10;
				sb.append("0");
			}
		};

		sb.append(" uV");
		rowUnitLabel = sb.toString();

		normalFont = g.getFont();
		verticalFont = normalFont.deriveFont(AffineTransform.getQuadrantRotateInstance(1));
		unitLabelBounds = verticalFont.getStringBounds(rowUnitLabel, g.getFontRenderContext());

		rowUnitLabelForChannels = new String[channelCount];
		pixelPerRowUnitForChannels = new double[channelCount];
		channelLabelBounds = new Rectangle2D[channelCount];
		this.calculateChannelsData(g);

		calculated = true;

	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D) gOrig;
		calculate(g);

		Point viewportPoint = plot.getViewport().getViewPosition();
		Dimension viewportSize = plot.getViewport().getExtentSize();
		Dimension size = getSize();

		Rectangle clip = g.getClipBounds();
		JPanel color_source = new JPanel();
		Color color = color_source.getBackground();
		g.setColor(color);

		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		size.width -= SignalPlot.SCALE_TO_SIGNAL_GAP;

		int i;
		int y;

		// this draws value ticks
		g.setColor(Color.GRAY);
		g.drawLine(size.width - SCALE_HORIZONTAL_LINE_WIDTH, viewportPoint.y,
				size.width - SCALE_HORIZONTAL_LINE_WIDTH, viewportPoint.y + viewportSize.height);
		int tickCnt = 1 + ((int) (((float) (viewportSize.height + 1)) / pixelPerRowUnit));

		for (i = 0; i < tickCnt; i++) {
			y = viewportPoint.y + ((int) (i * pixelPerRowUnit));
			g.drawLine(size.width - SCALE_HORIZONTAL_LINE_WIDTH + 1, y, size.width - 1, y);
		}
		this.drawChannelsValueTicks(g, size);

		for (i = 0; i < tickCnt; i++) {
			if (i % 2 == 0) {
				g.setColor(Color.LIGHT_GRAY);
			} else {
				g.setColor(Color.BLACK);
			}
			y = viewportPoint.y + ((int) (i * pixelPerRowUnit));
			g.fillRect(size.width - 2 * SCALE_HORIZONTAL_LINE_WIDTH + 1, y, SCALE_HORIZONTAL_LINE_WIDTH, (int) (pixelPerRowUnit));
		}

		//determine start channel number and number of channels to draw (to be precise - theirs labels)
		int startChannel = this.plot.computePaintStartChannel(clip.y);

		//take care of invisible labels above first visible drawable channel...
		for (i = startChannel - 1; i >= 0; i--) {
			if (!this.plot.getChannelsPlotOptionsModel().getModelAt(i).getVisible()) {
				startChannel--;
			} else {
				break;
			}
		}

		int maxNumberOfChannels = (int) Math.min(channelCount, Math.ceil(((double) clip.height - 1) / pixelPerChannel));

		//initialise canvas
		if (active) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.GRAY);
		}
		g.setFont(normalFont);

		//draw visible labels and channel's buttons
		boolean visible;
		int visibleCount = 0;
		i = startChannel;
		while (visibleCount <= maxNumberOfChannels && i <= channelCount - 1) {
			visible = this.plot.getChannelsPlotOptionsModel().getModelAt(i).getVisible();
			String channelLabelAndUnitString = getChannelLabelAndUnitString(i);

			if (visible) {
				visibleCount++;
				if (active) {
					g.setColor(Color.BLUE);
				}
				g.drawString(channelLabelAndUnitString, CHANNEL_BUTTON_WIDTH + 2, channelLevel[i] + ((int) -channelLabelBounds[i].getY() / 2));
			} else {
				g.setColor(Color.GRAY);//todo - make font smaller
				g.drawString(channelLabelAndUnitString, CHANNEL_BUTTON_WIDTH + 2, channelLevel[i] + ((int) -channelLabelBounds[i].getY() / 2));
			}
			channelOptionsButtons[i].setBounds(1, channelLevel[i] - 2, CHANNEL_BUTTON_WIDTH, CHANNEL_BUTTON_WIDTH);
			((ChannelOptionsAction) channelOptionsButtons[i].getAction()).setButtonVisible(visible);
			i++;

		}

		g.setColor(Color.GRAY);
		g.setFont(verticalFont);
		g.drawString(rowUnitLabel, size.width + ((float) unitLabelBounds.getY()) - LABEL_LINE_DISTANCE, viewportPoint.y + 3);

	}

	/**
	 * Returns a String containing the channel label and a unit of measurement
	 * for the channel (e.g. "SD3 [uV]). If the channel function is EEG, then
	 * the unit of measurement is not shown.
	 *
	 * @param montageChannelNumber the channel number in the current montage.
	 * @return a String describing the current channel
	 */
	protected String getChannelLabelAndUnitString(int montageChannelNumber) {
		String channelLabelAndUnitString = labelSource.getLabel(montageChannelNumber);
		SourceChannel sourceChannel = this.plot.getDocument().getMontage().getSourceChannelForMontageChannel(montageChannelNumber);

		String units = sourceChannel.getFunction().getUnitOfMeasurementSymbol();
		if (units != null && !units.isEmpty() && !(sourceChannel.getFunction() == ChannelFunction.EEG)) {
			channelLabelAndUnitString += " [" + units + "]";
		}

		return channelLabelAndUnitString;
	}

	/*
	 * For every channel that have its individual value scale draws it.
	 * @param g graphics on which scales will be drawn
	 * @param size panel's size needed to determine value scale X position
	 */
	private void drawChannelsValueTicks(Graphics2D g, Dimension size) {
		//prepare canvas to draw labels
		g.setColor(Color.GRAY);
		g.setFont(verticalFont);

		int i = 0, tp = 0, bt = 0, x = 0, y = 0;
		for (int j = 0; j < channelCount; j++) {
			if (pixelPerRowUnitForChannels[j] >= 0) { // if channel has its own value scale

				tp = channelLevel[j] - pixelPerChannel / 2 + 3; //scale's top position
				bt = channelLevel[j] + pixelPerChannel / 2 - 3; //scale's bottom position
				x = size.width - this.getScaleWidth(); // scale's x position
				//draw scale's line...
				g.drawLine(x - SCALE_HORIZONTAL_LINE_WIDTH, tp, x - SCALE_HORIZONTAL_LINE_WIDTH, bt);

				//determine number of ticks (scales horizontal lines)
				int tickCnt = 1 + ((int) (((double) (bt - tp)) / pixelPerRowUnitForChannels[j]));

				//draw half of tick obove the channel, half below
				for (i = 0; i < tickCnt / 2; i++) {
					y = channelLevel[j] + ((int) (i * pixelPerRowUnitForChannels[j]));
					g.drawLine(x - SCALE_HORIZONTAL_LINE_WIDTH + 1, y, x - 1, y);
				}
				for (i = 0; i < tickCnt / 2; i++) {
					y = channelLevel[j] - ((int) (i * pixelPerRowUnitForChannels[j]));
					g.drawLine(x - SCALE_HORIZONTAL_LINE_WIDTH + 1, y, x - 1, y);
				}
				//draw informative label, eg. 100uV
				g.drawString(rowUnitLabelForChannels[j], x - SCALE_HORIZONTAL_LINE_WIDTH + ((float) unitLabelBounds.getY()), tp + 3);

			}
		}
	}

	/*
	 * Returns width (in pixels) of single channel value scale (often presented in uV).
	 * @returns width (in pixels) of single channel value scale (often presented in uV)
	 */
	private int getScaleWidth() {
		return (int) Math.ceil(unitLabelBounds.getHeight() + SignalPlot.SCALE_TO_SIGNAL_GAP + LABEL_LINE_DISTANCE + SCALE_HORIZONTAL_LINE_WIDTH);
	}

	public int getPreferredWidth() {
		calculate((Graphics2D) getGraphics());
		if (this.hasSpecialChannels()) //we have individual scales for some channels
		{
			return maxChannelLabelWidth + this.getScaleWidth() * 2 + CHANNEL_BUTTON_WIDTH;
		} else {
			return maxChannelLabelWidth + this.getScaleWidth() + CHANNEL_BUTTON_WIDTH;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		// preferred widths must be coordinated!
		calculate((Graphics2D) getGraphics());
		return new Dimension(plot.getView().getSynchronizedRowHeaderWidth(), channelCount * pixelPerChannel);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	public SignalPlot getPlot() {
		return plot;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			repaint();
		}
	}

	protected class ChannelOptionsAction extends AbstractAction {

		/*
		 * An action performed on ChannelOptionsButton clicked.
		 */

		private static final long serialVersionUID = 1L;
		private int channel;
		private String visibleTooltip;
		private String invisibleTooltip;
		private ImageIcon visibleIcon;
		private ImageIcon invisibleIcon;

		/*
		 * Creates an action for ChannelOptions button.
		 * @param visibleIcon button's visible icon
		 * @param invisibleIcon button's invisible icon
		 * @param visibleTooltip button's visible tooltip
		 * @param invisible button's invisible tooltip
		 * @param channel index of channel the action is connected to
		 */
		public ChannelOptionsAction(ImageIcon visibleIcon, ImageIcon invisibleIcon, String visibleTooltip, String invisibleTooltip, int channel) {
			super();
			this.channel = channel;
			this.visibleTooltip = visibleTooltip;
			this.invisibleTooltip = invisibleTooltip;
			this.visibleIcon = visibleIcon;
			this.invisibleIcon = invisibleIcon;
			putValue(AbstractAction.SMALL_ICON, visibleIcon);
			putValue(AbstractAction.SHORT_DESCRIPTION, visibleTooltip);
		}

		/*
		 * Sets button's visibility attributes - icon and tooltip.
		 * @param visible ...
		 */
		public void setButtonVisible(boolean visible) {
			if (visible) {
				putValue(AbstractAction.SHORT_DESCRIPTION, this.visibleTooltip);
				putValue(AbstractAction.SMALL_ICON, this.visibleIcon);
			} else {
				putValue(AbstractAction.SHORT_DESCRIPTION, this.invisibleTooltip);
				putValue(AbstractAction.SMALL_ICON, this.invisibleIcon);
			};
		}

		/*
		 * Initializes channelOptions dialog and set it to appropriate channel.
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			Container ancestor = getTopLevelAncestor();
			Point containerLocation = ancestor.getLocation();
			CompactButton b = channelOptionsButtons[channel];
			Point location = SwingUtilities.convertPoint(b, new Point(0, 0), ancestor);

			channelOptionsPopupDialog.setChannel(this.channel);
			channelOptionsPopupDialog.setCurrentPlot(plot);
			channelOptionsPopupDialog.initializeNow();
			if (location.y < ancestor.getHeight() / 2) {
				location.translate(containerLocation.x, containerLocation.y);
			} else {
				location.translate(containerLocation.x, containerLocation.y + channelOptionsPopupDialog.getHeight() - channelOptionsPopupDialog.getHeight());
			}
			channelOptionsPopupDialog.setLocation(location);

			channelOptionsPopupDialog.showDialog(plot);
		}

	}

	/*
	 * Sets pop-up dialog for channelDisplay options.
	 * @param channelOptionsPopupDialog channelDisplay options dialog
	 */
	public void setChannelOptionsPopupDialog(
			ChannelOptionsPopupDialog channelOptionsPopupDialog) {
		this.channelOptionsPopupDialog = channelOptionsPopupDialog;
	}

}
