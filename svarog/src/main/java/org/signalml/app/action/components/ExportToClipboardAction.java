/* ExportToClipboardAction.java created 2007-12-18
 *
 */

package org.signalml.app.action.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import org.signalml.app.model.components.TableToTextExporter;
import org.signalml.app.model.components.WriterExportableTable;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** ExportToClipboardAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportToClipboardAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;
	private TableToTextExporter tableToTextExporter;

	public ExportToClipboardAction(TableToTextExporter tableToTextExporter) {
		super();
		setText(_("Copy to clipboard"));
		setIconPath("org/signalml/app/icon/clipboard.png");
		setToolTip(_("Copy contents to clipboard"));
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
			tableToTextExporter.exportToClipboard(exportableTable,getUserObject());
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

}
