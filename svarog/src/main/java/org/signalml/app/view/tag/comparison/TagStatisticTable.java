/* TagStatisticTable.java created 2007-12-04
 *
 */

package org.signalml.app.view.tag.comparison;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.signalml.app.action.ExportToClipboardAction;
import org.signalml.app.action.ExportToFileAction;
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.model.WriterExportableTable;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.CenteringTableCellRenderer;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.plugin.export.signal.TagStyle;

/** TagStatisticTable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStatisticTable extends JTable {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(TagStatisticTable.class);

	private static final int CELL_SIZE = 50;

	private TagStyleTableCellRenderer tagStyleTableCellRenderer;
	private TagIconProducer tagIconProducer;
	private TableToTextExporter tableToTextExporter;
	private ViewerFileChooser fileChooser;
	private JPopupMenu popupMenu;

	public TagStatisticTable(TagStatisticTableModel model) {

		super(model);

		setTableHeader(null);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDefaultRenderer(String.class, new CenteringTableCellRenderer());
		tagStyleTableCellRenderer = new TagStyleTableCellRenderer();
		setDefaultRenderer(TagStyle.class, tagStyleTableCellRenderer);
		setCellSelectionEnabled(true);

		setRowHeight(CELL_SIZE);

		setToolTipText("");

	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
		tagStyleTableCellRenderer.setTagIconProducer(tagIconProducer);
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
	public TagStatisticTableModel getModel() {
		return (TagStatisticTableModel) super.getModel();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Point p = event.getPoint();
		int row = rowAtPoint(p);
		int col = columnAtPoint(p);
		if (row >= 0 && col >= 0) {
			Object value = getValueAt(row, col);
			if (value instanceof String) {
				return (String) value;
			}
			else if (value instanceof TagStyle) {
				return ((TagStyle) value).getDescriptionOrName();
			}
		}
		return null;
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(new ExportStatisticToClipboardAction( tableToTextExporter));
			popupMenu.add(new ExportStatisticToFileAction( tableToTextExporter));
		}
		return popupMenu;
	}

	protected class ExportStatisticToClipboardAction extends ExportToClipboardAction {

		private static final long serialVersionUID = 1L;

		public ExportStatisticToClipboardAction( TableToTextExporter tableToTextExporter) {
			super( tableToTextExporter);
		}

		@Override
		protected WriterExportableTable getExportableTable() {
			TagStatisticTableModel model = getModel();
			if (model != null) {
				return model.getStatistic();
			}
			return null;
		}

	}

	protected class ExportStatisticToFileAction extends ExportToFileAction {

		private static final long serialVersionUID = 1L;

		private ExportStatisticToFileAction( TableToTextExporter tableToTextExporter) {
			super( tableToTextExporter);
			setFileChooser(fileChooser);
			setOptionPaneParent(TagStatisticTable.this);
		}

		@Override
		protected WriterExportableTable getExportableTable() {
			TagStatisticTableModel model = getModel();
			if (model != null) {
				return model.getStatistic();
			}
			return null;
		}

	}

}
