/* CheckSignalDisplay.java created 2010-10-24
 *
 */

package org.signalml.app.view.monitor;

import java.awt.Graphics2D;
import java.awt.Color;

import java.util.HashMap;
import java.util.Iterator;

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
         * Channel info used when drawing the channel state.
         */
        private HashMap<String, Boolean> channels;

        public CheckSignalDisplay(VisualReferenceModel model) {

                super(model);
        }

        /**
         * Sets the given channels state, then repaints the component
         * to visualize any possible changes.
         *
         * @param channels HashMap representing channels state
         */
        public void setChannelsState(HashMap<String, Boolean> channels) {

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

		Iterator<VisualReferenceSourceChannel> it = bin.iterator();
		VisualReferenceSourceChannel channel;

                if (channels == null) {

                        while (it.hasNext()) {

                                channel = it.next();
                                paintUnknownChannel(channel, g);
                        }

                } else {

                        while (it.hasNext()) {

                                channel = it.next();
                                if (!channels.containsKey(channel.getLabel()))
                                	continue;
                                if (channels.get(channel.getLabel()))
                                        paintValidChannel(channel, g);
                                else
                                        paintInvalidChannel(channel, g);
                        }
                }
        }

        /**
         * Draws a valid channel - green fill and black thin outline.
         *
         * @param channel {@link VisualReferenceSourceChannel} object
         * @param g {@link Graphics2D} object
         */
        private void paintValidChannel(VisualReferenceSourceChannel channel, Graphics2D g) {

                paintGivenChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), Color.GREEN, Color.BLACK, false, g);
        }

        /**
         * Draws an invalid channel - red fill and black thick outline.
         * 
         * @param channel {@link VisualReferenceSourceChannel} object
         * @param g {@link Graphics2D} object
         */
        private void paintInvalidChannel(VisualReferenceSourceChannel channel, Graphics2D g) {

                paintGivenChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), Color.RED, Color.BLACK, true, g);
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
}