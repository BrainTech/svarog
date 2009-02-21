/* ExportTableToClipboardAction.java created 2007-12-07
 * 
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;

import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TableFocusSelector;
import org.signalml.app.model.TableToTextExporter;
import org.springframework.context.support.MessageSourceAccessor;

/** ExportTableToClipboardAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportTableToClipboardAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(ExportTableToClipboardAction.class);
		
	private TableToTextExporter tableToTextExporter;
	
	public ExportTableToClipboardAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.exportTableToClipboard");
		setIconPath("org/signalml/app/icon/clipboard.png");
		setToolTip("action.exportTableToClipboardToolTip");
	}
		
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		TableFocusSelector tableFocusSelector = (TableFocusSelector) findFocusSelector(ev.getSource(), TableFocusSelector.class);
		if( tableFocusSelector == null ) {
			return;
		}
		JTable table = tableFocusSelector.getActiveTable();
		if( table == null ) {
			return;
		}

		tableToTextExporter.exportToClipboard(table.getModel());
	
	}
		
	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public TableToTextExporter getTableModelExporter() {
		return tableToTextExporter;
	}

	public void setTableModelExporter(TableToTextExporter tableToTextExporter) {
		this.tableToTextExporter = tableToTextExporter;
	}
	
}
