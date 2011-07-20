/* SignalPlotRowHeader.java created 2007-10-15
 *
 */

package org.signalml.app.view.signal;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.signal.popup.ChannelOptionsPopupDialog;
import org.signalml.domain.signal.MultichannelSampleSource;

/** SignalPlotRowHeader
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPlotRowHeader extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final Dimension MINIMUM_SIZE = new Dimension(0,0);

	private boolean calculated = false;

	private Font normalFont;
	private Font verticalFont;

	private int channelCount;
	private double pixelPerValue;
	private int pixelPerChannel;
	private int[] channelLevel;

	private double pixelPerRowUnit;
	private String rowUnitLabel;

	private Rectangle2D unitLabelBounds;
	private Rectangle2D[] channelLabelBounds;
	private CompactButton[] channelOptionsButtons;
	private int maxChannelLabelWidth = 0;

	private SignalPlot plot;
	private ImageIcon channelOptionsIcon;

	private MultichannelSampleSource labelSource;
	private ChannelOptionsPopupDialog channelOptionsPopupDialog;

	private boolean active = true;

	public SignalPlotRowHeader(SignalPlot plot) {
		super();
		this.plot = plot;
	    Image iconImage;
	    ImageIcon ic;
	    iconImage = IconUtils.loadClassPathImage("org/signalml/app/icon/channelOptions.png");
	    ic = new ImageIcon(iconImage);
	    this.channelOptionsIcon = ic;
	}

	/*
	 * Sets calculated to false and creates ChannelOptions Buttons.
	 */
	public void reset() {
		calculated = false;
		
		if (channelOptionsButtons != null) {
			for (int i = 0; i < channelOptionsButtons.length; i++)
				if (channelOptionsButtons[i] != null)
					this.remove(channelOptionsButtons[i]);
		}
		channelOptionsButtons = new CompactButton[this.plot.getChannelCount()];
		for(int i = 0; i < this.plot.getChannelCount();i++) {
			CompactButton b = new CompactButton(
					new ChannelOptionsAction(this.channelOptionsIcon, 
									this.plot.getMessageSource().getMessage("signalView.editChannelOptions"), 
									this.plot.getMessageSource().getMessage("signalView.editChannelOptions")+" DUPA",
									i));
			channelOptionsButtons[i] = b;
			this.add(b);
		}
	}

	private void calculate(Graphics2D g) {

		if (calculated) {
			return;
		}

		int i;

		channelCount = plot.getChannelCount();
		pixelPerValue = plot.getPixelPerValue();
		pixelPerChannel = plot.getPixelPerChannel();
		labelSource = plot.getSignalOutput();
		channelLevel = plot.getChannelLevel();

		pixelPerRowUnit = pixelPerValue;

		StringBuilder sb = new StringBuilder("1");
		if (pixelPerRowUnit == 0.0)
			sb.append("000...");
		else {
			
			while (pixelPerRowUnit <= 5) {
				pixelPerRowUnit *= 10;
				sb.append("0");
			}
		};

		sb.append(" uV");
		rowUnitLabel = sb.toString();

		normalFont = g.getFont();
		verticalFont = normalFont.deriveFont(AffineTransform.getQuadrantRotateInstance(1));

		unitLabelBounds = verticalFont.getStringBounds(rowUnitLabel, g.getFontRenderContext());

		double max = 0;
		channelLabelBounds = new Rectangle2D[channelCount];
		
		for (i=0; i < channelCount; i++) {
			channelLabelBounds[i] = normalFont.getStringBounds(labelSource.getLabel(i), g.getFontRenderContext());
			if (max < channelLabelBounds[i].getWidth())  {
				max = channelLabelBounds[i].getWidth();
			}
		}
		maxChannelLabelWidth = (int) Math.ceil(max);

		calculated = true;

	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D)gOrig;
		calculate(g);

		Point viewportPoint = plot.getViewport().getViewPosition();
		Dimension viewportSize = plot.getViewport().getExtentSize();
		Dimension size = getSize();

		Rectangle clip = g.getClipBounds();
		g.setColor(getBackground());
		g.fillRect(clip.x,clip.y,clip.width,clip.height);

		int clipEndY = clip.y + clip.height - 1;

		size.width -= SignalPlot.SCALE_TO_SIGNAL_GAP;

		int i;
		int y;

		// this draws value ticks
		g.setColor(Color.GRAY);
		g.drawLine(size.width-4, viewportPoint.y, size.width-4, viewportPoint.y + viewportSize.height);
		int tickCnt = 1 + ((int)(((float)(viewportSize.height+1))  / pixelPerRowUnit));
		for (i=0; i<tickCnt; i++) {
			y = viewportPoint.y + ((int)(i*pixelPerRowUnit));
			g.drawLine(size.width-3, y, size.width-1, y);
		}
		System.out.println("GLOBAL TICK COUNT"+tickCnt);
		this.drawChannelsValueTicks(g, size, viewportSize, viewportPoint);

		// this draws channel labels
		int startChannel = (int) Math.max(0, Math.floor(clip.y / pixelPerChannel));
		int endChannel = (int) Math.min(channelCount-1, Math.ceil(clipEndY / pixelPerChannel));

		if (active) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.GRAY);
		}
		boolean visible;
		i=startChannel;
		int visibleCount=0;
		i=startChannel;
		while(visibleCount<=(endChannel-startChannel) && i<=channelCount-1){
			visible = this.plot.getChannelsPlotOptionsModel().getModelAt(i).getVisible();
			if (visible) {
				visibleCount ++;
				if (active)
					g.setColor(Color.BLUE);
				g.drawString(labelSource.getLabel(i), 12, channelLevel[i] + ((int) -channelLabelBounds[i].getY()/2));
			} else {
				g.setColor(Color.GRAY);//todo - make font smaller
				g.drawString(labelSource.getLabel(i), 12, channelLevel[i] + ((int) -channelLabelBounds[i].getY()/2));
			}
			channelOptionsButtons[i].setBounds(1, channelLevel[i]-2, 10, 10);
			((ChannelOptionsAction) channelOptionsButtons[i].getAction()).setButtonVisible(visible);
			i++;
		
		}

		g.setColor(Color.GRAY);
		g.setFont(verticalFont);
		g.drawString(rowUnitLabel, size.width+((float)unitLabelBounds.getY())-5, viewportPoint.y+3);

	}
	private void drawChannelsValueTicks(Graphics2D g, Dimension size, Dimension viewportSize, Point viewportPoint) {
		//g.setColor(Color.GRAY);
		int k = 10;
		for (int j = 0; j < this.channelCount; j++) {
			int v = this.plot.getChannelsPlotOptionsModel().getModelAt(j).getVoltageScale();
			if ((v != this.plot.getValueScaleRangeModel().getValue()) && (this.plot.getChannelsPlotOptionsModel().getModelAt(j).getVisible())) {
				//k+=5;
				double pixelPerRowUnit = this.plot.getPixelPerChannel() * v * this.plot.getVoltageZoomFactorRatio();
				while (pixelPerRowUnit <= 5)
					pixelPerRowUnit *= 10;
				g.drawLine(size.width-4-k, channelLevel[j]-(channelLevel[j]-channelLevel[j-1])/2, size.width-4-k, channelLevel[j]+ (channelLevel[j+1]-channelLevel[j])/2);
				int tickCnt = 1 + ((int)(((float)(channelLevel[j+1]-channelLevel[j-1])/2)  / pixelPerRowUnit));
				int y;
				for (int i=0; i<tickCnt/2; i++) {
					y = channelLevel[j] - ((int)(i*pixelPerRowUnit));
					g.drawLine(size.width-3-k, y, size.width-1-k, y);
				}
				for (int i=0; i<tickCnt/2; i++) {
					y = channelLevel[j] + ((int)(i*pixelPerRowUnit));
					g.drawLine(size.width-3-k, y, size.width-1-k, y);
				}
			}
		}
	}

	public int getPreferredWidth() {
		calculate((Graphics2D) getGraphics());
		return maxChannelLabelWidth + (int) Math.ceil(unitLabelBounds.getHeight() + SignalPlot.SCALE_TO_SIGNAL_GAP + 3 + 3 + 2) + 30;
	}

	@Override
	public Dimension getPreferredSize() {
		// preferred widths must be coordinated!
		calculate((Graphics2D) getGraphics());
		return new Dimension(plot.getView().getSynchronizedRowHeaderWidth(),channelCount*pixelPerChannel);
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

		/*
		 * Creates an action for ChannelOptions button.
		 * @param ic button`s icon
		 * @param tooltip button`s tooltip
		 * @param channel index of channel the action is connected to
		 */
		public ChannelOptionsAction(ImageIcon ic, String visibleTooltip, String invisibleTooltip, int channel) {
			super();
			this.channel = channel;
			this.visibleTooltip = visibleTooltip;
			this.invisibleTooltip = invisibleTooltip;
			putValue(AbstractAction.SMALL_ICON, ic);
			putValue(AbstractAction.SHORT_DESCRIPTION, visibleTooltip);
		}

		public void setButtonVisible(boolean visible) {
			if (visible)
				putValue(AbstractAction.SHORT_DESCRIPTION, this.visibleTooltip);
			else
				putValue(AbstractAction.SHORT_DESCRIPTION, this.invisibleTooltip);
		}
		/*
		 * Initializes channelOptions dialog and set it to appropriate channel.
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ev) {

			Container ancestor = getTopLevelAncestor();
			Point containerLocation = ancestor.getLocation();
			CompactButton b = channelOptionsButtons[channel];
			Point location = SwingUtilities.convertPoint(b, new Point(0,0), ancestor);
			
			channelOptionsPopupDialog.setChannel(this.channel);
			channelOptionsPopupDialog.setCurrentPlot(plot);
			channelOptionsPopupDialog.initializeNow();
			if (location.y < ancestor.getHeight()/2) {
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
