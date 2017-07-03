package org.signalml.app.view.document.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.plugin.export.signal.Document;

/**
 * A panel which is able to display the duration of the current monitor
 * recording. It registers itself to listen for event on MonitorSignalDocument
 * and starts/stops to measure time when the appropriate event happens.
 *
 * For offline signals it does nothing.
 *
 * @author Piotr Szachewicz
 */
public class MonitorRecordingDurationPanel extends AbstractPanel {

	/**
	 * The document for which this panel is displayed.
	 */
	private MonitorSignalDocument document;

	/**
	 * The label displaying the duration in the format "hh:mm:ss".
	 */
	private JLabel durationLabel;

	/**
	 * Timer which every second performs a task adding one second to the
	 * duration.
	 */
	private Timer timer;

	public MonitorRecordingDurationPanel(Document document) {

		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		this.setMaximumSize(new Dimension(150, 25));
		this.setPreferredSize(new Dimension(150, 25));

		Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		this.setBorder(emptyBorder);

		if (!(document instanceof MonitorSignalDocument))
			return;

		this.document = (MonitorSignalDocument) document;
		document.addPropertyChangeListener(this);

		createInterface();
	}

	protected void createInterface() {
		durationLabel = new JLabel();
		this.add(durationLabel, BorderLayout.WEST);
		durationLabel.setHorizontalAlignment(SwingConstants.LEFT);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (MonitorSignalDocument.IS_RECORDING_PROPERTY.equals(evt.getPropertyName())) {
			boolean recordingStarted = (Boolean) evt.getNewValue();
			if (recordingStarted)
				startCountingTime();
			else
				stopCountingTime();
		}
		if (MonitorSignalDocument.IS_VIDEO_SAVING_PROPERTY.equals(evt.getPropertyName())) {
			boolean videoIsSaving = (Boolean) evt.getNewValue();
			if (videoIsSaving) {
				stopCountingTime();
				startVideoSavingIndication();
			}
			else
				stopCountingTime();
		}
	}

	protected void displayTime(int hours, int minutes, int seconds) {
		durationLabel.setText(formatNumber(hours) + ":" + formatNumber(minutes) + ":" + formatNumber(seconds));
	}
	
	protected void displayText(String text) {
		durationLabel.setText(text);
	}

	protected String formatNumber(int number) {
		return String.format("%02d", number);
	}

	/**
	 * Starts the timer which is responsible to increment the seconds every
	 * second.
	 */
	protected void startCountingTime() {
		timer = new Timer();
		TimerTask task = new RecordingDurationTimerTask();
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	
	/**
	 * Starts the timer which is responsible for displaying animated
	 * word "Saving..."
	 */
	protected void startVideoSavingIndication() {
		timer = new Timer();
		TimerTask task = new VideoSavingTimerTask();
		timer.scheduleAtFixedRate(task, 0, 1000);
		durationLabel.setForeground(Color.red);

	}

	/**
	 * Stops the timer and resets the value of the durationLabel.
	 */
	protected void stopCountingTime() {
		if (timer != null)
			timer.cancel();
		durationLabel.setText("");
		durationLabel.setForeground(Color.black);
	}

	/**
	 * Task for measuring recording time. It is invoked once a second by the
	 * timer and increments the duration.
	 */
	class RecordingDurationTimerTask extends TimerTask {

		private int seconds;
		private int minutes;
		private int hours;

		@Override
		public void run() {
			incrementSeconds();
			displayTime(hours, minutes, seconds);
		}

		protected void incrementSeconds() {
			seconds++;

			if (seconds == 60) {
				minutes++;
				seconds = 0;
			}

			if (minutes == 60) {
				hours++;
				minutes = 0;
			}
		}

	}
	
	class VideoSavingTimerTask extends TimerTask {
		private String main = "Saving";
		private String additional = ".";
		private int number;
		
		@Override
		public void run() {
			String suffix = "";
			for(int i = 0; i < number; i++)
				suffix += additional;

			number++;
			if (number > 3)
				number = 0;

			displayText(main + suffix);
			
		}
	}

}
