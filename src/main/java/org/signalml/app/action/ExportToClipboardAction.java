/* ExportToClipboardAction.java created 2007-12-18
 * 
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.model.WriterExportableTable;
import org.springframework.context.support.MessageSourceAccessor;

/** ExportToClipboardAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportToClipboardAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;
	private TableToTextExporter tableToTextExporter;
	
	public ExportToClipboardAction(MessageSourceAccessor messageSource, TableToTextExporter tableToTextExporter) {
		super(messageSource);
		setText("action.exportTableToClipboard");
		setIconPath("org/signalml/app/icon/clipboard.png");
		setToolTip("action.exportTableToClipboardToolTip");
		this.tableToTextExporter = tableToTextExporter;
	}
		
	protected abstract WriterExportableTable getExportableTable();
	
	protected Object getUserObject() {
		return null;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		WriterExportableTable exportableTable = getExportableTable();
		if( exportableTable != null ) {
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
