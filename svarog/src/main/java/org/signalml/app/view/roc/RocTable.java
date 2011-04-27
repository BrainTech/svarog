/* RocTable.java created 2007-12-18
 *
 */
package org.signalml.app.view.roc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import org.signalml.app.action.ExportToClipboardAction;
import org.signalml.app.action.ExportToFileAction;
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.model.WriterExportableTable;
import org.signalml.app.view.ViewerFileChooser;
import org.springframework.context.support.MessageSourceAccessor;

/** RocTable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RocTable extends JTable {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private TableToTextExporter tableToTextExporter;
	private ViewerFileChooser fileChooser;

	private JPopupMenu popupMenu;

	public RocTable(RocTableModel model, MessageSourceAccessor messageSource) {
		super(model, (TableColumnModel) null);

		this.messageSource = messageSource;

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1)) {
					int index = rowAtPoint(e.getPoint());
					ListSelectionModel selectionModel = getSelectionModel();
					if (!selectionModel.isSelectedIndex(index)) {
						selectionModel.setSelectionInterval(index, index);
					}
				}
			}

		});

		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	}

	@Override
	public RocTableModel getModel() {
		return (RocTableModel) super.getModel();
	}

	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

	public void setTableToTextExporter(TableToTextExporter tableToTextExporter) {
		this.tableToTextExporter = tableToTextExporter;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(new ExportComparisonToClipboardAction(messageSource, tableToTextExporter));
			popupMenu.add(new ExportComparisonToFileAction(messageSource, tableToTextExporter));
		}
		return popupMenu;
	}

	protected class ExportComparisonToClipboardAction extends ExportToClipboardAction {

		private static final long serialVersionUID = 1L;

		public ExportComparisonToClipboardAction(MessageSourceAccessor messageSource, TableToTextExporter tableToTextExporter) {
			super(messageSource, tableToTextExporter);
		}

		@Override
		protected WriterExportableTable getExportableTable() {
			RocTableModel model = getModel();
			if (model != null) {
				return model.getRocData();
			}
			return null;
		}

	}

	protected class ExportComparisonToFileAction extends ExportToFileAction {

		private static final long serialVersionUID = 1L;

		private ExportComparisonToFileAction(MessageSourceAccessor messageSource, TableToTextExporter tableToTextExporter) {
			super(messageSource, tableToTextExporter);
			setFileChooser(fileChooser);
			setOptionPaneParent(RocTable.this);
		}

		@Override
		protected WriterExportableTable getExportableTable() {
			RocTableModel model = getModel();
			if (model != null) {
				return model.getRocData();
			}
			return null;
		}

	}

}
