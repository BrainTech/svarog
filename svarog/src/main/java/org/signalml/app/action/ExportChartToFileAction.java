/* ExportChartToFileAction.java created 2007-12-18
 *
 */

package org.signalml.app.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;

/** ExportChartToFileAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportChartToFileAction extends AbstractSignalMLAction {

	protected static final Logger logger = Logger.getLogger(ExportChartToFileAction.class);

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	public ExportChartToFileAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.exportChartToFile");
		setIconPath("org/signalml/app/icon/picture_save.png");
		setToolTip("action.exportChartToFileToolTip");
	}

	protected abstract Dimension getImageSize();

	protected abstract JFreeChart getChart();

	@Override
	public void actionPerformed(ActionEvent ev) {

		JFreeChart chart = getChart();
		if (chart != null) {

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

			Dimension imageSize = getImageSize();

			try {
				ChartUtilities.saveChartAsPNG(file, chart, imageSize.width, imageSize.height);
			} catch (IOException ex) {
				logger.error("Failed to save to file - i/o exception", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}

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