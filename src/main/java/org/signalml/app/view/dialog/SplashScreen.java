/* SplashScreen.java created 2007-11-22
 *
 */

package org.signalml.app.view.dialog;

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
import org.springframework.context.support.MessageSourceAccessor;

/** SplashScreen
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SplashScreen extends JDialog {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SplashScreen.class);

	private JProgressBar progressBar;
	private MessageSourceAccessor messageSource;

	public SplashScreen(MessageSourceAccessor messageSource) {

		super((Frame) null, false);

		this.messageSource = messageSource;
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

	public JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar(SwingConstants.HORIZONTAL,0,SvarogApplication.INITIALIZATION_STEP_COUNT+ViewerMainFrame.INITIALIZATION_STEP_COUNT);
			progressBar.setStringPainted(true);
			progressBar.setString(messageSource.getMessage("startup.initializing"));
			progressBar.setPreferredSize(new Dimension(400,20));
		}
		return progressBar;
	}

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

	private class SplashPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private Image splashImage;
		private Dimension size;

		public SplashPanel() {
			super(null);

			splashImage = IconUtils.loadClassPathImage("org/signalml/app/icon/splash.png");

			int width = splashImage.getWidth(null);
			int height = splashImage.getHeight(null);

			size = new Dimension(width,height);

		}

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

			String title = messageSource.getMessage("splash.loading", new Object[] {SvarogConstants.VERSION });

			Rectangle2D stringBounds = fontMetrics.getStringBounds(title, g);
			int width = (int) stringBounds.getWidth();
			int height = (int) stringBounds.getHeight();

			g.drawString(title, (size.width-width)/2, offset + fontMetrics.getAscent());

			offset += (height + 95);

			font = new Font(Font.DIALOG, Font.PLAIN, 10);
			fontMetrics = g.getFontMetrics(font);
			g.setFont(font);

			String url = messageSource.getMessage("splash.url");

			stringBounds = fontMetrics.getStringBounds(url, g);
			width = (int) stringBounds.getWidth();
			height = (int) stringBounds.getHeight();

			g.setColor(Color.WHITE);
			g.fillRect((size.width-width)/2 - 5, offset, width + 20, height);
			g.setColor(Color.BLUE.darker());
			g.drawString(url, (size.width-width)/2, offset + fontMetrics.getAscent());

			offset += (height + 5);

			String comment = messageSource.getMessage("splash.comment");

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

		@Override
		public boolean isOpaque() {
			return true;
		}

		@Override
		public boolean isDoubleBuffered() {
			return false;
		}

	}

}
