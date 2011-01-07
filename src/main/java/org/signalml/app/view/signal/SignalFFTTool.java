/* SignalFFTTool.java created 2007-12-16
 *
 */
package org.signalml.app.view.signal;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.signalml.app.config.SignalFFTSettings;
import org.signalml.app.util.IconUtils;

/** SignalFFTTool
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalFFTTool extends AbstractSignalTool {

	private SignalPlot plot;
	private SignalFFTPlot fftPlot;

	private SignalFFTSettings settings;

	public SignalFFTTool(SignalView signalView) {
		super(signalView);
		fftPlot = new SignalFFTPlot(signalView.getMessageSource());
		settings = new SignalFFTSettings();
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {

			Object source = e.getSource();
			if (!(source instanceof SignalPlot)) {
				plot = null;
				return;
			}
			plot = (SignalPlot) source;

			Point point = e.getPoint();
			int channel = plot.toChannelSpace(point);
			fftPlot.setSettings(settings);
			if (e.isControlDown()) {
				Dimension plotSize = settings.getPlotSize();
				int width = Math.min(800, plotSize.width * 2);
				int height = Math.min(600, plotSize.height * 2);
				Dimension size = new Dimension(width, height);
				fftPlot.setPlotSize(size);
				fftPlot.setWindowWidth(2*settings.getWindowWidth());
			}
			fftPlot.setParameters(plot, point, channel);
			showFFT(point);
			selectAround(point);
			setEngaged(true);
			e.consume();

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			hideFFT();
			setEngaged(false);
			plot = null;
			e.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (plot != null) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point point = e.getPoint();
				Rectangle r = new Rectangle(point.x, point.y, 1, 1);
				((SignalPlot)e.getSource()).scrollRectToVisible(r);
				if( settings.isChannelSwitching() ) {
					int channel = plot.toChannelSpace(point);
					fftPlot.setParameters(point, channel);
				} else {
					fftPlot.setFocusPoint(point);
				}
				positionFFT(point, null);
				selectAround(point);
			}
		}

	}

	public SignalFFTSettings getSettings() {
		return settings;
	}

	public void setSettings(SignalFFTSettings settings) {
		if (settings == null) {
			throw new NullPointerException("No settings");
		}
		this.settings = settings;
	}

	private void positionFFT(Point point, JLayeredPane layeredPane) {
		if (plot != null) {
			if (layeredPane == null) {
				layeredPane = plot.getRootPane().getLayeredPane();
			}
			Dimension size = fftPlot.getPreferredSize();
			int channel = fftPlot.getChannel();
			int channelY = plot.channelToPixel(channel);
			Point location = SwingUtilities.convertPoint(plot, new Point(point.x, channelY), layeredPane);
			int y;
			if (location.y > layeredPane.getHeight() / 2) {
				y = location.y - size.height;
			} else {
				y = location.y + plot.getPixelPerChannel();
			}
			fftPlot.setBounds(location.x-(size.width/2), y, size.width, size.height);
		}
	}

	private void showFFT(Point point) {
		if (plot != null) {
			fftPlot.setVisible(true);
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			positionFFT(point, layeredPane);
			layeredPane.add(fftPlot, new Integer(JLayeredPane.DRAG_LAYER));
		}
	}

	private void hideFFT() {
		if (plot != null) {
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			layeredPane.remove(fftPlot);
			plot.getRootPane().repaint();
		}
	}

	private void selectAround(Point point) {
		if (plot != null) {
			Float centerPosition = plot.toTimeSpace(point);
			if (centerPosition != null) {
				double offset = (((float) settings.getWindowWidth()) / plot.getSamplingFrequency()) / 2;
				Float startPosition = new Float(centerPosition.floatValue() - ((float) offset));
				Float endPosition = new Float(centerPosition.floatValue() + ((float) offset));
				if (startPosition.equals(endPosition)) {
				    getSignalView().clearSignalSelection();
				} else {
					Integer channel = fftPlot.getChannel();
					if (channel != null) {
					    getSignalView().setSignalSelection(plot,plot.getChannelSelection(startPosition, endPosition, channel));
					}
				}
			}
		}
	}

}
