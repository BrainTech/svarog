/* ExportAllChartsToFileAction.java created 2008-01-15
 *
 */

package org.signalml.app.action;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;

/** ExportAllChartsToFileAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportAllChartsToFileAction extends AbstractSignalMLAction {

	protected static final Logger logger = Logger.getLogger(ExportAllChartsToFileAction.class);

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	public ExportAllChartsToFileAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.exportAllChartsToFile");
		setIconPath("org/signalml/app/icon/picture_save.png");
		setToolTip("action.exportAllChartsToFileToolTip");
	}

	protected abstract int getChartCount();

	protected abstract Rectangle getChartBounds(int index);

	protected abstract JFreeChart getChart(int index);

	@Override
	public void actionPerformed(ActionEvent ev) {

		int chartCount = getChartCount();
		if (chartCount == 0) {
			return;
		}

		File file;
		boolean hasFile = false;
		do {

			file = fileChooser.chooseChartSaveAsPngFile(optionPaneParent);
			if (file == null) {
				return;
			}
			String ext = Util.getFileExtension(file,false);
			if (ext == null) {
				file = new File(file.getAbsolutePath() + ".png");
			}

			hasFile = true;

			if (file.exists()) {
				int res = OptionPane.showFileAlreadyExists(optionPaneParent);
				if (res != OptionPane.OK_OPTION) {
					hasFile = false;
				}
			}

		} while (!hasFile);

		Rectangle rect = new Rectangle(0,0,0,0);
		int i;

		for (i=0; i<chartCount; i++) {
			rect.add(getChartBounds(i));
		}

		BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();

		for (i=0; i<chartCount; i++) {

			getChart(i).draw(g, getChartBounds(i));

		}

		try {
			ImageIO.write(image, "png", file);
		} catch (IOException ex) {
			logger.error("Failed to save to file - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

}
