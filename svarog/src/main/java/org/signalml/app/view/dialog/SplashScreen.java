/* SplashScreen.java created 2007-11-22
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogApplication._;
import static org.signalml.app.SvarogApplication._R;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerMainFrame;
import org.signalml.util.SvarogConstants;

/**
 * Dialog that is shown when application is starting.
 * Contains two elements:
 * <ul>
 * <li>the progress bar on which the information about the current step is
 * displayed,</li>
 * <li>the logo of Svarog on which 3 labels are displayed:
 * <ul>
 * <li>the name and version of Svarog,</li>
 * <li>the URL to the web page,</li>
 * <li>the information that Svarog was financed from Polish science
 * funds.</li></ul></li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SplashScreen extends JDialog {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SplashScreen.class);

	/**
	 * the progress bar which displays the progress of loading Svarog with
	 * the information about the current loading step
	 */
	private JProgressBar progressBar;

	/**
	 * Constructor. Sets the source of messages and displays creates the view
	 * for this dialog:
	 * <ul>
	 * <li>the {@link SplashPanel panel} with the logo of Svarog,</li>
	 * <li>the {@link #getProgressBar() progress bar}.</li></ul>
	 */
	public  SplashScreen() {

		super((Frame) null, false);
		setUndecorated(true);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(new CompoundBorder(
		                               new BevelBorder(BevelBorder.RAISED, Color.BLUE, Color.BLUE.darker()),
		                               new EmptyBorder(3,3,3,3)
		                       ));

		JPanel labelPanel = new JPanel(new BorderLayout());
		labelPanel.setBorder(new EmptyBorder(0,0,5,0));

		labelPanel.add(new SplashPanel(), BorderLayout.CENTER);

		contentPanel.add(labelPanel, BorderLayout.CENTER);
		contentPanel.add(getProgressBar(), BorderLayout.SOUTH);

		getRootPane().setContentPane(contentPanel);

		pack();

		setLocationRelativeTo(null);

	}

	/**
	 * Returns the progress bar which displays the progress of loading Svarog
	 * with the information about the current loading step
	 * If it doesn't exist it is created with the string saying that
	 * application is being initialized.
	 * @return the progress bar
	 */
	public JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar(SwingConstants.HORIZONTAL,0,SvarogApplication.INITIALIZATION_STEP_COUNT+ViewerMainFrame.INITIALIZATION_STEP_COUNT);
			progressBar.setStringPainted(true);
			progressBar.setString(_("Initializing"));
			progressBar.setPreferredSize(new Dimension(400,20));
		}
		return progressBar;
	}

	/**
	 * Updates this dialog.
	 * Sets the new label on the progress bar and
	 * if {@code doStep} is set increases the value of the progress bar.
	 * This operation is performed in the Event Dispatch Thread (using
	 * Runnable).
	 * @param text the text that should be displayed on the progress bar
	 * @param doStep {@code true} if the value of the progress bar should be
	 * increased, {@code false} otherwise.
	 */
	public void updateSplash(final String text, final boolean doStep) {

		Runnable job = new Runnable() {
			@Override
			public void run() {
				JProgressBar progressBar = getProgressBar();
				if (doStep) {
					progressBar.setValue(progressBar.getValue() + 1);
				}
				if (text != null) {
					progressBar.setString(text);
				}
				progressBar.paintImmediately(new Rectangle(new Point(0,0), progressBar.getSize()));
			}
		};

		if (SwingUtilities.isEventDispatchThread()) {
			job.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(job);
			} catch (InterruptedException ex) {
				// ignore
			} catch (InvocationTargetException ex) {
				logger.error("Failed to update splash", ex);
			}
		}

	}

	/**
	 * Sets the new text on the progress bar.
	 * This operation is performed in the Event Dispatch Thread (using
	 * Runnable).
	 * @param text the text that should be displayed on the progress bar
	 */
	public void setStepTitle(final String text) {

		Runnable job = new Runnable() {
			@Override
			public void run() {
				JProgressBar progressBar = getProgressBar();
				progressBar.setString(text);
				progressBar.paintImmediately(new Rectangle(new Point(0,0), progressBar.getSize()));
			}
		};

		if (SwingUtilities.isEventDispatchThread()) {
			job.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(job);
			} catch (InterruptedException ex) {
				// ignore
			} catch (InvocationTargetException ex) {
				logger.error("Failed to set title", ex);
			}
		}

	}

	/**
	 * Increases the value of the progress bar.
	 * This operation is performed in the Event Dispatch Thread (using
	 * Runnable).
	 */
	public void stepCompleted() {

		Runnable job = new Runnable() {
			@Override
			public void run() {
				JProgressBar progressBar = getProgressBar();
				progressBar.setValue(progressBar.getValue() + 1);
				progressBar.paintImmediately(new Rectangle(new Point(0,0), progressBar.getSize()));
			}
		};

		if (SwingUtilities.isEventDispatchThread()) {
			job.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(job);
			} catch (InterruptedException ex) {
				// ignore
			} catch (InvocationTargetException ex) {
				logger.error("Failed to set step completed", ex);
			}
		}

	}

	/**
	 * The panel with logo of Svarog and 3 labels drawn on it:
	 * <ul>
	 * <li>the name and version of Svarog,</li>
	 * <li>the URL to the web page,</li>
	 * <li>the information that Svarog was financed from Polish science
	 * funds.</li></ul>
	 */
	private class SplashPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		/**
		 * the image with the logo of Svarog
		 */
		private Image splashImage;
		/**
		 * the size of this panel
		 */
		private Dimension size;

		/**
		 * Constructor. Creates this panel and adds the image to it
		 * (the image fills the entire panel).
		 */
		public SplashPanel() {
			super(null);

			splashImage = IconUtils.loadClassPathImage("org/signalml/app/icon/splash.png");

			int width = splashImage.getWidth(null);
			int height = splashImage.getHeight(null);

			size = new Dimension(width,height);

		}

		/**
		 * Draws the image and 3 strings on it:
		 * <ul>
		 * <li>the name and version of Svarog,</li>
		 * <li>the URL to the webpage,</li>
		 * <li>the information that Svarog was financed from Polish science
		 * funds.</li></ul>
		 */
		@Override
		protected void paintComponent(Graphics gOrig) {

			Graphics2D g = (Graphics2D) gOrig;

			g.drawImage(splashImage, 0, 0, null);

			int offset = 130; // vertical pixel offset for text
			Font font;
			FontMetrics fontMetrics;

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.BLUE.darker());

			font = new Font(Font.DIALOG, Font.ITALIC & Font.BOLD, 30);
			fontMetrics = g.getFontMetrics(font);
			g.setFont(font);

			String title = _R(("Loading Svarog v.{0}"), new Object[] {SvarogConstants.VERSION });

			Rectangle2D stringBounds = fontMetrics.getStringBounds(title, g);
			int width = (int) stringBounds.getWidth();
			int height = (int) stringBounds.getHeight();

			g.drawString(title, (size.width-width)/2, offset + fontMetrics.getAscent());

			offset += (height + 95);

			font = new Font(Font.DIALOG, Font.PLAIN, 10);
			fontMetrics = g.getFontMetrics(font);
			g.setFont(font);

			String url = _("http://signalml.org/");

			stringBounds = fontMetrics.getStringBounds(url, g);
			width = (int) stringBounds.getWidth();
			height = (int) stringBounds.getHeight();

			g.setColor(Color.WHITE);
			g.fillRect((size.width-width)/2 - 5, offset, width + 20, height);
			g.setColor(Color.BLUE.darker());
			g.drawString(url, (size.width-width)/2, offset + fontMetrics.getAscent());

			offset += (height + 5);

			String comment = _("Financed from Polish funds for science");

			stringBounds = fontMetrics.getStringBounds(comment, g);
			width = (int) stringBounds.getWidth();
			height = (int) stringBounds.getHeight();

			g.setColor(Color.WHITE);
			g.fillRect((size.width-width)/2 - 5, offset, width + 20, height);
			g.setColor(Color.BLUE.darker());
			g.drawString(comment, (size.width-width)/2, offset + fontMetrics.getAscent());

			offset += (height + 5);

		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}

		@Override
		public Dimension getMinimumSize() {
			return size;
		}

		@Override
		public Dimension getMaximumSize() {
			return size;
		}

		/**
		 * This panel is opaque.
		 */
		@Override
		public boolean isOpaque() {
			return true;
		}

		/**
		 * This panel doesn't use the buffer to paint.
		 */
		@Override
		public boolean isDoubleBuffered() {
			return false;
		}

	}

}
