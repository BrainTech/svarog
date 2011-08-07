/* CheckSignalDisplay.java created 2010-10-24
 *
 */

package org.signalml.app.view.monitor.signalchecking;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.signalml.app.view.montage.VisualReferenceBin;
import org.signalml.app.view.montage.VisualReferenceDisplay;
import org.signalml.app.view.montage.VisualReferenceModel;
import org.signalml.app.view.montage.VisualReferenceSourceChannel;

/**
 * A component which - based on information from a {@link VisualReferenceModel} object
 * and a {@link GenericAmplifierDiagnosis} object - draws the state of each channel.
 *
 * @author Tomasz Sawicki
 */
public class CheckSignalDisplay extends VisualReferenceDisplay {

        /**
         * Space between two bars or bar and a channel.
         */
        public static final int BAR_PADDING = 12;
        /**
         * Width of a bar.
         */
        public static final int BAR_WIDTH = 20;
        /**
         * Height of a bar.
         */
        public static final int BAR_HEIGHT = 50;
        /**
         * Bar border size.
         */
        public static final int BAR_BORDER = 2;
        /**
         * Text space between text and bar.
         */
        public static final int TEXT_SPACE = 3;
        /**
         * How much the bar is moved vertically.
         */
        public static final int BAR_VERTICAL_OFFSET = -14;
        /**
         * Channel info used when drawing the channel state.
         */
        private List<HashMap<String, ChannelState>> channels;

        /**
         * Default construcot only calls super.
         *
         * @param model super parameter
         */
        public CheckSignalDisplay(VisualReferenceModel model) {

                super(model);
        }

        /**
         * Sets the given channels state, then repaints the component
         * to visualize any possible changes.
         *
         * @param channels HashMap representing channels state
         */
        public void setChannelsState(List<HashMap<String, ChannelState>> channels) {

                this.channels = channels;
                this.repaint();
        }

        /**
         * Draws bin contents based on the channel state.
         *
         * @param bin {@link VisualReferenceBin} object
         * @param g {@link Graphics2D} object
         */
        @Override
        protected void paintBinContents(VisualReferenceBin bin, Graphics2D g) {

                HashMap<String, Boolean> validData = null;
                HashMap<String, List<AdditionalChannelData>> additionalData = null;

                boolean stateKnown = isStateKnown();
                if (stateKnown) {

                        validData = getValidData();
                        additionalData = getAdditionalData();
                }
                        

		Iterator<VisualReferenceSourceChannel> it = bin.iterator();
		VisualReferenceSourceChannel channel;

                if (!stateKnown) {

                        while (it.hasNext()) {

                                channel = it.next();
                                paintUnknownChannel(channel, g);
                        }

                } else {

                        while (it.hasNext()) {

                                channel = it.next();
                                paintKnownChannel(channel, g, validData.get(channel.getLabel()), additionalData.get(channel.getLabel()));
                        }
                }
        }

        /**
         * Draws an unknown channel - white fill and red thick outline.
         * 
         * @param channel {@link VisualReferenceSourceChannel} object
         * @param g {@link Graphics2D} object
         */
        private void paintUnknownChannel(VisualReferenceSourceChannel channel, Graphics2D g) {

                paintGivenChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), Color.WHITE, Color.RED, true, g);
        }

        /**
         * Draws a known channel - green fill and black thin outline if valid, red fill
         * and black thick outline if invalid. Also draws the additional data.
         *
         * @param channel {@link VisualReferenceSourceChannel} object
         * @param g {@link Graphics2D} object
         * @param valid whether channel is valid
         * @param additionalData list of additional data to draw
         */
        private void paintKnownChannel(VisualReferenceSourceChannel channel, Graphics2D g, boolean valid, List<AdditionalChannelData> additionalData) {

                paintGivenChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), (valid) ? Color.GREEN : Color.RED, Color.BLACK, false, g);

                if (!additionalData.isEmpty()) {

                        int startX = channel.getLocation().x + VisualReferenceSourceChannel.CIRCLE_DIAMETER + BAR_PADDING;
                        int startY = channel.getLocation().y + BAR_VERTICAL_OFFSET + (VisualReferenceSourceChannel.CIRCLE_DIAMETER - BAR_HEIGHT) / 2;
                        Point location = new Point(startX, startY);
                        for (AdditionalChannelData data : additionalData) {

                                paintAdditionalDataBar(location, g, data);
                                startX += BAR_WIDTH + BAR_PADDING;
                                location = new Point(startX, startY);
                        }
                        
                }
        }

        /**
         * Paints a value bar.
         * @param location location of the bar
         * @param g {@link Graphics2D} object
         * @param data data to draw
         */
        private void paintAdditionalDataBar(Point location, Graphics2D g, AdditionalChannelData data) {

                paintBar(location, g, data);
                paintLabels(location, g, data);
        }

        /**
         * Paints a value bar.
         * @param location location of the bar
         * @param g {@link Graphics2D} object
         * @param data data to draw
         */
        private void paintBar(Point location, Graphics2D g, AdditionalChannelData data) {

                int validHeight = (int)(BAR_HEIGHT * ((data.getLimit() - data.getMin()) / (data.getMax() - data.getMin())));
                int validStart = BAR_HEIGHT - validHeight;
                int currentHeight = (int)( (data.getCurrent() < data.getMax()) ? (BAR_HEIGHT * ((data.getCurrent() - data.getMin()) / (data.getMax() - data.getMin()))) : BAR_HEIGHT );
                int currentEnd = BAR_HEIGHT - currentHeight;

                g.setPaint(Color.RED);
                g.fillRect(location.x, location.y, BAR_WIDTH, BAR_HEIGHT - validHeight);

                g.setPaint(new GradientPaint(location.x, location.y + validStart, Color.RED, location.x, location.y + BAR_HEIGHT, Color.GREEN));
                g.fillRect(location.x, location.y + validStart, BAR_WIDTH, validHeight);

                g.setPaint(Color.WHITE);
                g.fillRect(location.x, location.y, BAR_WIDTH, currentEnd);

                g.setPaint(Color.BLACK);
                g.setStroke(new BasicStroke(BAR_BORDER));
                g.drawRect(location.x, location.y, BAR_WIDTH, BAR_HEIGHT);
                g.drawLine(location.x, location.y + validStart, location.x + BAR_WIDTH, location.y + validStart);


        }

        /**
         * Paints bar labels: max, min, current, and method name
         * @param location location of the bar
         * @param g {@link Graphics2D} object
         * @param data data to draw
         */
        private void paintLabels(Point location, Graphics2D g, AdditionalChannelData data) {

                g.setFont(channelLabelFont);

                String toWrite = formatDouble(data.getMax());
                Rectangle2D rect = channelLabelFontMetrics.getStringBounds(toWrite, g);
                int textHeight = (int)rect.getHeight();
                int textWidth = (int)(rect.getWidth());
                int offset = (BAR_WIDTH - textWidth) / 2;
                int textX = location.x + offset;
                int textY = location.y - TEXT_SPACE;
                paintLabelWithBackground(toWrite, textX, textY, textWidth, textHeight, g);

                toWrite = data.getMethod().toString();
                rect = channelLabelFontMetrics.getStringBounds(toWrite, g);
                textHeight = (int)rect.getHeight();
                textWidth = (int)rect.getWidth();
                offset = (BAR_WIDTH - textWidth) / 2;
                textX = location.x + offset;
                textY = textY - textHeight;
                paintLabelWithBackground(toWrite, textX, textY, textWidth, textHeight, g);

                toWrite = formatDouble(data.getCurrent());
                rect = channelLabelFontMetrics.getStringBounds(toWrite, g);
                textHeight = (int)rect.getHeight();
                textWidth = (int)rect.getWidth();
                offset = (BAR_WIDTH - textWidth) / 2;
                textX = location.x + offset;
                textY = textY - textHeight;
                paintLabelWithBackground(toWrite, textX, textY, textWidth, textHeight, g);

                toWrite = formatDouble(data.getMin());
                rect = channelLabelFontMetrics.getStringBounds(toWrite, g);
                textHeight = (int)rect.getHeight();
                textWidth = (int)rect.getWidth();
                offset = (BAR_WIDTH - textWidth) / 2;
                textX = location.x + offset;
                textY = location.y + textHeight + BAR_HEIGHT + TEXT_SPACE;
                paintLabelWithBackground(toWrite, textX, textY, textWidth, textHeight, g);
        }

        /**
         * Paints given String with white background.
         * @param toWrite String to write
         * @param textX x position
         * @param textY y position
         * @param textWidth text width
         * @param textHeight text height
         * @param g {@link Graphics2D} object
         */
        private void paintLabelWithBackground(String toWrite, int textX, int textY, int textWidth, int textHeight, Graphics2D g) {

                g.setPaint(Color.WHITE);
                g.fillRect(textX, textY - textHeight, textWidth, textHeight);
                g.setPaint(Color.BLACK);
                g.drawString(toWrite, textX, textY);
        }

        /**
         * Formats given double.
         * @param input input value
         * @return input as {@link String}
         */
        private String formatDouble(double input) {

                NumberFormat formatter = new DecimalFormat("00E0");
                return formatter.format(input);
        }

        /**
         * Whether the state is known - all diagnosis objects returned non-null value.
         *
         * @return true if the state is known, false otherwise
         */
        private boolean isStateKnown() {

                for (Object diagnosisResult : channels)
                        if (diagnosisResult == null)
                                return false;
                return true;
        }

        /**
         * For each channel, checks if that channel is valid.
         * 
         * @return valid data on each channel
         */
        private HashMap<String, Boolean> getValidData() {

             HashMap<String, Boolean> validData = new HashMap<String, Boolean>();
             Set<String> channelNames = channels.get(0).keySet();

             for (String channel : channelNames) {

                     boolean valid = true;
                     for (HashMap<String, ChannelState> diagnosisResult : channels) {

                             if (!diagnosisResult.get(channel).isValid()) {

                                     valid = false;
                                     break;
                             }
                     }

                     validData.put(channel, valid);
             }

             return validData;
        }

        /**
         * Gets list additional channel data for each channel.
         * 
         * @return additional data
         */
        private HashMap<String, List<AdditionalChannelData>> getAdditionalData() {

                HashMap<String, List<AdditionalChannelData>> additionalData =
                        new HashMap<String, List<AdditionalChannelData>>();

                Set<String> channelNames = channels.get(0).keySet();

                for (String channel : channelNames) {

                        List<AdditionalChannelData> channelsAdditionalData = new ArrayList<AdditionalChannelData>();
                        for (HashMap<String, ChannelState> diagnosisResult : channels) {

                              AdditionalChannelData currentData = diagnosisResult.get(channel).getAdditionalChannelData();
                              if (currentData != null)
                                      channelsAdditionalData.add(currentData);
                        }

                        additionalData.put(channel, channelsAdditionalData);
                }

                return additionalData;
        }
}