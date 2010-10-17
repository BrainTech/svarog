/* IconUtils.java created 2007-09-11
 *
 */
package org.signalml.app.util;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.task.TaskStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/** IconUtils
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class IconUtils {

	protected static final Logger logger = Logger.getLogger(IconUtils.class);

	private static ImageIcon errorIcon = null;
	private static ImageIcon warningIcon = null;
	private static ImageIcon infoIcon = null;
	private static ImageIcon questionIcon = null;

	private static ImageIcon signalPageIcon = null;
	private static ImageIcon pageTagIcon = null;
	private static ImageIcon blockTagIcon = null;
	private static ImageIcon channelTagIcon = null;

	private static ImageIcon bookAtomIcon = null;
	private static ImageIcon bookSegmentIcon = null;
	private static ImageIcon bookChannelIcon = null;

	private static ImageIcon paletteIcon = null;

	private static Cursor emptyCursor = null;
	private static Cursor crosshairCursor = null;
	private static Cursor handCursor = null;

	private static Map<TaskStatus,ImageIcon> taskIconMap = null;
	private static Map<TaskStatus,ImageIcon> largeTaskIconMap = null;

	private static Map<String,ImageIcon> iconMap = new HashMap<String, ImageIcon>();

	public static ImageIcon loadClassPathIcon(String classpath) {
		ImageIcon imageIcon = iconMap.get(classpath);
		if (imageIcon == null) {
			Resource icon = new ClassPathResource(classpath);
			try {
				imageIcon = new ImageIcon(icon.getURL());
			} catch (IOException ex) {
				logger.warn("WARNING: failed to open icon recource [" + icon.toString() + "]", ex);
				return null;
			}
			iconMap.put(classpath, imageIcon);
		}
		return imageIcon;
	}

	public static ImageIcon getErrorIcon() {
		if (errorIcon == null) {
			errorIcon = loadClassPathIcon("org/signalml/app/icon/metal-error.png");
		}
		return errorIcon;
	}

	public static ImageIcon getWarningIcon() {
		if (warningIcon == null) {
			warningIcon = loadClassPathIcon("org/signalml/app/icon/metal-warning.png");
		}
		return warningIcon;
	}

	public static ImageIcon getInfoIcon() {
		if (infoIcon == null) {
			infoIcon = loadClassPathIcon("org/signalml/app/icon/metal-info.png");
		}
		return infoIcon;
	}

	public static ImageIcon getQuestionIcon() {
		if (questionIcon == null) {
			questionIcon = loadClassPathIcon("org/signalml/app/icon/metal-question.png");
		}
		return questionIcon;
	}

	public static ImageIcon getSignalPageIcon() {
		if (signalPageIcon == null) {
			signalPageIcon = loadClassPathIcon("org/signalml/app/icon/signalpage.png");
		}
		return signalPageIcon;
	}

	public static ImageIcon getPageTagIcon() {
		if (pageTagIcon == null) {
			pageTagIcon = loadClassPathIcon("org/signalml/app/icon/pagetag.png");
		}
		return pageTagIcon;
	}

	public static ImageIcon getBlockTagIcon() {
		if (blockTagIcon == null) {
			blockTagIcon = loadClassPathIcon("org/signalml/app/icon/blocktag.png");
		}
		return blockTagIcon;
	}

	public static ImageIcon getChannelTagIcon() {
		if (channelTagIcon == null) {
			channelTagIcon = loadClassPathIcon("org/signalml/app/icon/channeltag.png");
		}
		return channelTagIcon;
	}

	public static ImageIcon getBookAtomIcon() {
		if (bookAtomIcon == null) {
			bookAtomIcon = loadClassPathIcon("org/signalml/app/icon/bookatom.png");
		}
		return bookAtomIcon;
	}

	public static ImageIcon getBookSegmentIcon() {
		if (bookSegmentIcon == null) {
			bookSegmentIcon = loadClassPathIcon("org/signalml/app/icon/booksegment.png");
		}
		return bookSegmentIcon;
	}

	public static ImageIcon getBookChannelIcon() {
		if (bookChannelIcon == null) {
			bookChannelIcon = loadClassPathIcon("org/signalml/app/icon/bookchannel.png");
		}
		return bookChannelIcon;
	}

	public static ImageIcon getPaletteIcon() {
		if (paletteIcon == null) {
			paletteIcon = loadClassPathIcon("org/signalml/app/icon/palette.png");
		}
		return paletteIcon;
	}

	public static ImageIcon getTagIcon(SignalSelectionType type) {
		if (type == SignalSelectionType.PAGE) {
			return getPageTagIcon();
		}
		else if (type == SignalSelectionType.BLOCK) {
			return getBlockTagIcon();
		}
		else if (type == SignalSelectionType.CHANNEL) {
			return getChannelTagIcon();
		}
		return null;
	}

	public static Image loadClassPathImage(String classpath) {
		return loadClassPathIcon(classpath).getImage();
	}

	public static Cursor getEmptyCursor() {
		if (emptyCursor == null) {
			Image image = loadClassPathImage("org/signalml/app/icon/transparent.png");
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension d = toolkit.getBestCursorSize(32, 32);
			emptyCursor = toolkit.createCustomCursor(image, new Point(d.width/2,d.height/2), "Invisible cursor");
		}
		return emptyCursor;
	}

	public static Cursor getCrosshairCursor() {
		if (crosshairCursor == null) {
			Image image = loadClassPathImage("org/signalml/app/icon/crosshair.png");
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension d = toolkit.getBestCursorSize(32, 32);
			int x = (int)(d.width * (15F/32F));
			int y = (int)(d.height * (16F/32F));
			crosshairCursor = toolkit.createCustomCursor(image, new Point(x,y), "Contrasting crosshair cursor");
		}
		return crosshairCursor;
	}

	public static Cursor getHandCursor() {
		if (handCursor == null) {
			Image image = loadClassPathImage("org/signalml/app/icon/handcursor.png");
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension d = toolkit.getBestCursorSize(32, 32);
			handCursor = toolkit.createCustomCursor(image, new Point(d.width/2,d.height/2), "All finger hand");
		}
		return handCursor;
	}

	public static ImageIcon getTaskIcon(TaskStatus taskStatus) {

		if (taskIconMap == null) {

			taskIconMap = new HashMap<TaskStatus,ImageIcon>();

			ImageIcon runningIcon = loadClassPathIcon("org/signalml/app/icon/running.png");
			ImageIcon waitingIcon = loadClassPathIcon("org/signalml/app/icon/waiting.png");
			ImageIcon finishedIcon = loadClassPathIcon("org/signalml/app/icon/finished.png");
			ImageIcon stoppedIcon = loadClassPathIcon("org/signalml/app/icon/stopped.png");
			ImageIcon suspendedIcon = loadClassPathIcon("org/signalml/app/icon/suspended.png");
			ImageIcon errorIcon = loadClassPathIcon("org/signalml/app/icon/error.png");

			taskIconMap.put(TaskStatus.NEW, runningIcon);
			taskIconMap.put(TaskStatus.ACTIVE, runningIcon);
			taskIconMap.put(TaskStatus.ACTIVE_WAITING, waitingIcon);
			taskIconMap.put(TaskStatus.REQUESTING_ABORT, runningIcon);
			taskIconMap.put(TaskStatus.REQUESTING_SUSPEND, runningIcon);
			taskIconMap.put(TaskStatus.FINISHED, finishedIcon);
			taskIconMap.put(TaskStatus.ABORTED, stoppedIcon);
			taskIconMap.put(TaskStatus.SUSPENDED, suspendedIcon);
			taskIconMap.put(TaskStatus.ERROR, errorIcon);

		}

		return taskIconMap.get(taskStatus);

	}

	public static ImageIcon getLargeTaskIcon(TaskStatus taskStatus) {

		if (largeTaskIconMap == null) {

			largeTaskIconMap = new HashMap<TaskStatus,ImageIcon>();

			ImageIcon runningIcon = loadClassPathIcon("org/signalml/app/icon/runninglarge.png");
			ImageIcon waitingIcon = loadClassPathIcon("org/signalml/app/icon/waitinglarge.png");
			ImageIcon finishedIcon = loadClassPathIcon("org/signalml/app/icon/finishedlarge.png");
			ImageIcon stoppedIcon = loadClassPathIcon("org/signalml/app/icon/stoppedlarge.png");
			ImageIcon suspendedIcon = loadClassPathIcon("org/signalml/app/icon/suspendedlarge.png");
			ImageIcon errorIcon = loadClassPathIcon("org/signalml/app/icon/errorlarge.png");

			largeTaskIconMap.put(TaskStatus.NEW, runningIcon);
			largeTaskIconMap.put(TaskStatus.ACTIVE, runningIcon);
			largeTaskIconMap.put(TaskStatus.ACTIVE_WAITING, waitingIcon);
			largeTaskIconMap.put(TaskStatus.REQUESTING_ABORT, runningIcon);
			largeTaskIconMap.put(TaskStatus.REQUESTING_SUSPEND, runningIcon);
			largeTaskIconMap.put(TaskStatus.FINISHED, finishedIcon);
			largeTaskIconMap.put(TaskStatus.ABORTED, stoppedIcon);
			largeTaskIconMap.put(TaskStatus.SUSPENDED, suspendedIcon);
			largeTaskIconMap.put(TaskStatus.ERROR, errorIcon);

		}

		return largeTaskIconMap.get(taskStatus);

	}

}
