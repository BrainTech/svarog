/* ExportToFileAction.java created 2007-12-18
 *
 */

package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.model.WriterExportableTable;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;

/** ExportToFileAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportToFileAction extends AbstractSignalMLAction {

	protected static final Logger logger = Logger.getLogger(ExportToFileAction.class);

	private static final long serialVersionUID = 1L;
	private TableToTextExporter tableToTextExporter;

	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;

	public ExportToFileAction(TableToTextExporter tableToTextExporter) {
		super();
		setText(_("Save to file"));
		setIconPath("org/signalml/app/icon/script_save.png");
		setToolTip(_("Save contents to file"));
		this.tableToTextExporter = tableToTextExporter;
	}

	protected abstract WriterExportableTable getExportableTable();

	protected Object getUserObject() {
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		WriterExportableTable exportableTable = getExportableTable();
		if (exportableTable != null) {

			File file;
			boolean hasFile = false;
			do {

				file = fileChooser.chooseTableSaveAsTextFile(optionPaneParent);
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

			try {
				tableToTextExporter.export(exportableTable,file,getUserObject());
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

	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

}
