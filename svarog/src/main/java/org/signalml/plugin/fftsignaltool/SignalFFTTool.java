package org.signalml.plugin.fftsignaltool;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.InvalidClassException;
import java.util.Calendar;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.math.fft.FourierTransform;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.events.PluginSignalChangeEvent;
import org.signalml.plugin.export.change.listeners.PluginSignalChangeListener;
import org.signalml.plugin.export.signal.AbstractSignalTool;
import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.view.ExportedSignalPlot;
import org.signalml.plugin.export.view.ExportedSignalView;
import org.signalml.plugin.fftsignaltool.actions.SaveToCSV;

/**
 * {@link SignalTool Signal tool} which displays the {@link SignalFFTPlot plot}
 * with the {@link FourierTransform#powerSpectrumReal(double[], double) power
 * spectrum} of the part of the signal near the cursor when the left button of
 * the mouse is pressed.<p>
 * The settings how the power spectrum should be displayed are stored in
 * {@link SignalFFTSettings}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o., Marcin Szumski
 */
public class SignalFFTTool extends AbstractSignalTool implements PluginSignalChangeListener {

	protected static final Logger logger = Logger
										   .getLogger(SignalFFTPlot.class);

	/**
	 * How often should FFT be recalculated. This is useful for online signals.
	 */
	private static long MILLISECONDS_BETWEEN_FFT_RECALCULATIONS = 1000;

	/**
	 * the {@link ExportedSignalPlot signal plot} for which this FFT tool is
	 * created
	 */
	private ExportedSignalPlot plot;
	/**
	 * the {@link SignalFFTPlot plot} on which the power spectrum is displayed
	 */
	private SignalFFTPlot fftPlot;

	/**
	 * the {@link SvarogAccess access} to Svarog logic and GUI
	 */
	private SvarogAccess svarogAccess;

	/**
	 * {@link SignalFFTSettings settings} how the power spectrum should be
	 * displayed
	 */
	private SignalFFTSettings settings;
	
	private double[] powerSpectrum;
	private double[] frequencies;

	private SaveToCSV popupAction;

	/**
	 * Constructor. Sets the source of messages and the {@link
	 * ExportedSignalView signal view} for which this FFT tool is
	 * created.
	 * @param signalView the signal view
	 */
	public SignalFFTTool(SaveToCSV popupAction, ExportedSignalView signalView) {
		super(signalView);
		fftPlot = new SignalFFTPlot();
		settings = new SignalFFTSettings();
		this.popupAction = popupAction;
	}

	/**
	 * Constructor. Sets the source of messages. The {@link
	 * ExportedSignalView signal view} for which this FFT tool is
	 * created must be set separately.
	 */
	public SignalFFTTool(SaveToCSV popupAction) {
		super();
		fftPlot = new SignalFFTPlot();
		settings = new SignalFFTSettings();
		this.popupAction = popupAction;
	}

	@Override
	public SignalFFTTool createCopy() {
		SignalFFTTool copy = new SignalFFTTool(popupAction);
		copy.setSvarogAccess(svarogAccess);
		copy.setSettings(getSettings());
		return copy;
	}

	@Override
	public Cursor getDefaultCursor() {
		return IconUtils.getCrosshairCursor();
	}

	/**
	 * Displays the {@link SignalFFTPlot plot} with the power spectrum of the
	 * part of the signal near the cursor.
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {
			Object source = e.getSource();
			if (!(source instanceof ExportedSignalPlot)) {
				plot = null;
				return;
			}
			plot = (ExportedSignalPlot) source;

			Point point = e.getPoint();
			int channel = plot.toChannelSpace(point);
			fftPlot.setSettings(settings);
			if (e.isControlDown()) {
				Dimension plotSize = settings.getPlotSize();
				int width = Math.min(800, plotSize.width * 2);
				int height = Math.min(600, plotSize.height * 2);
				Dimension size = new Dimension(width, height);
				fftPlot.setPlotSize(size);
				fftPlot.setWindowWidth(2 * settings.getWindowWidth());
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
			popupAction.setPowerAndFrequencies(fftPlot.getPowerSpectrum(), fftPlot.getFrequencies());
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
				((ExportedSignalPlot) e.getSource()).scrollRectToVisible(r);
				if (settings.isChannelSwitching()) {
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

	/**
	 * Returns the {@link SignalFFTSettings settings} how the power spectrum
	 * should be displayed.
	 * @return the settings how the power spectrum should be displayed
	 */
	public SignalFFTSettings getSettings() {
		return settings;
	}

	/**
	 * Sets the {@link SignalFFTSettings settings} how the power spectrum should be
	 * displayed.
	 * @param settings the settings how the power spectrum should be displayed
	 */
	public void setSettings(SignalFFTSettings settings) {
		if (settings == null) {
			throw new NullPointerException(_("No settings"));
		}
		this.settings = settings;
	}

	/**
	 * Calculates the position on the screen where the {@link SignalFFTPlot
	 * plot} with the power spectrum should be displayed and displays it at
	 * that place.
	 * @param point the location of the cursor
	 * @param layeredPane the pane on which the fft plot should be displayed
	 */
	private void positionFFT(Point point, JLayeredPane layeredPane) {
		if (plot != null) {
			if (layeredPane == null) {
				layeredPane = plot.getRootPane().getLayeredPane();
			}
			Dimension size = fftPlot.getPreferredSize();
			int channel = fftPlot.getChannel();
			int channelY = plot.channelToPixel(channel);
			Point location = SwingUtilities.convertPoint((Component) plot,
							 new Point(point.x, channelY), layeredPane);
			int y;
			if (location.y > layeredPane.getHeight() / 2) {
				y = location.y - size.height;
			} else {
				y = location.y + plot.getPixelPerChannel();
			}
			fftPlot.setBounds(location.x - (size.width / 2), y, size.width,
							  size.height);
		}
	}

	/**
	 * Displays the {@link SignalFFTPlot fft plot} with the power spectrum of
	 * the part of the signal near cursor ({@code point})
	 * @param point the location of the cursor
	 */
	private void showFFT(Point point) {
		if (plot != null) {
			fftPlot.setVisible(true);
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			positionFFT(point, layeredPane);
			layeredPane.add(fftPlot, new Integer(JLayeredPane.DRAG_LAYER));
		}
	}

	/**
	 * Hides the {@link SignalFFTPlot fft plot} (removes it from
	 * {@link ExportedSignalPlot signal plot}).
	 */
	private void hideFFT() {
		if (plot != null) {
			JLayeredPane layeredPane = plot.getRootPane().getLayeredPane();
			layeredPane.remove(fftPlot);
			plot.getRootPane().repaint();
		}
	}

	/**
	 * Draws the rectangle around point on the currently selected channel which
	 * is nearest to the specified {@code point}.
	 * If the point is not located on this channel,
	 * @param point the point
	 */
	private void selectAround(Point point) {
		if (plot != null) {
			Float centerPosition = plot.toTimeSpace(point);
			if (centerPosition != null) {
				double offset = (((float) settings.getWindowWidth()) / plot
								 .getSamplingFrequency()) / 2;
				Float startPosition = new Float(centerPosition.floatValue()
												- ((float) offset));
				Float endPosition = new Float(centerPosition.floatValue()
											  + ((float) offset));
				if (startPosition.equals(endPosition)) {
					getSignalView().clearSignalSelection();
				} else {
					Integer channel = fftPlot.getChannel();
					if (channel != null) {
						try {
							getSignalView().setSignalSelection(
								plot,
								plot.getChannelSelection(startPosition,
														 endPosition, channel));
						} catch (InvalidClassException e) {
							throw new RuntimeException(_("invalid plot"));
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the {@link SvarogAccess access} to elements of Svarog in this class
	 * and in the {@link SignalFFTPlot plot}.
	 * @param access the access to elements of Svarog
	 */
	public void setSvarogAccess(SvarogAccess access) {
		svarogAccess = access;
		svarogAccess.getChangeSupport().addSignalChangeListener(this);
		fftPlot.setSvarogAccess(access);
	}

	@Override
	public void newSamplesAdded(PluginSignalChangeEvent e) {
		if (isEngaged()) {
			Calendar now = Calendar.getInstance();
			Calendar lastFFTRecalculationTime = fftPlot.getLastFFTRecalculationTime();
			if (lastFFTRecalculationTime == null)
				return;

			long differenceInMillis = now.getTimeInMillis() - lastFFTRecalculationTime.getTimeInMillis();

			if (differenceInMillis > MILLISECONDS_BETWEEN_FFT_RECALCULATIONS) {
				fftPlot.recalculateAndRepaint();
			}
		}
	}
}
