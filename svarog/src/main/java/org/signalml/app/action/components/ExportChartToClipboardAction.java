/* ExportChartToClipboardAction.java created 2007-12-18
 *
 */

package org.signalml.app.action.components;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.signalml.app.util.ImageTransferable;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** ExportChartToClipboardAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportChartToClipboardAction extends AbstractSignalMLAction implements ClipboardOwner {

	protected static final Logger logger = Logger.getLogger(ExportChartToClipboardAction.class);

	private static final long serialVersionUID = 1L;

	public ExportChartToClipboardAction() {
		super();
		setText(_("Copy chart to clipboard"));
		setIconPath("org/signalml/app/icon/clipboard.png");
		setToolTip(_("Copy chart to clipboard"));
	}

	protected abstract Dimension getImageSize();

	protected abstract JFreeChart getChart();

	@Override
	public void actionPerformed(ActionEvent ev) {

		JFreeChart chart = getChart();
		if (chart != null) {

			Dimension imageSize = getImageSize();
			BufferedImage image = chart.createBufferedImage(imageSize.width, imageSize.height);

			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new ImageTransferable(image), this);

		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// don't care
	}

}
