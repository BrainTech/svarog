/* ExportSamplesToFileAction.java created 2008-01-15
 *
 */

package org.signalml.app.action;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.signalml.app.action.document.ExportSamplesAction;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.util.Util;

/** ExportSamplesToFileAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportSamplesToFileAction extends ExportSamplesAction {

	protected static final Logger logger = Logger.getLogger(ExportSamplesToFileAction.class);

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	public ExportSamplesToFileAction() {
		super();
		setText(_("Save samples as txt file"));
		setIconPath("org/signalml/app/icon/script_save.png");
		setToolTip(_("Save samples as a txt file"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		String samplesAsString = getSamplesAsString();
		if (samplesAsString != null) {

			File file;
			boolean hasFile = false;
			do {

				file = fileChooser.chooseSamplesSaveAsTextFile(optionPaneParent);
				if (file == null) {
					return;
				}
				String ext = Util.getFileExtension(file,false);
				if (ext == null) {
					file = new File(file.getAbsolutePath() + ".txt");
				}

				hasFile = true;

				if (file.exists()) {
					int res = OptionPane.showFileAlreadyExists(optionPaneParent);
					if (res != OptionPane.OK_OPTION) {
						hasFile = false;
					}
				}

			} while (!hasFile);

			Writer writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.append(samplesAsString);
			} catch (IOException ex) {
				logger.error("Failed to save to file - i/o exception", ex);
				Dialogs.showExceptionDialog((Window) null, ex);
				return;
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
						// ignore
					}
				}
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
