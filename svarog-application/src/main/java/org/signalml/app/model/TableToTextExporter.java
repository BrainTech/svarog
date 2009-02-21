/* TableModelExporter.java created 2007-12-07
 * 
 */

package org.signalml.app.model;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.table.TableModel;

import org.signalml.exception.SanityCheckException;
import org.signalml.util.Util;

/** TableModelExporter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TableToTextExporter implements ClipboardOwner {

	public static final String DEFAULT_COLUMN_SEPARATOR = "\t";
	public static final String DEFAULT_ROW_SEPARATOR = Util.LINE_SEP;
	
	private String columnSeparator = DEFAULT_COLUMN_SEPARATOR;
	private String rowSeparator = DEFAULT_ROW_SEPARATOR;
	
	public String getColumnSeparator() {
		return columnSeparator;
	}
	
	public void setColumnSeparator(String columnSeparator) {
		this.columnSeparator = columnSeparator;
	}
	
	public String getRowSeparator() {
		return rowSeparator;
	}
	
	public void setRowSeparator(String rowSeparator) {
		this.rowSeparator = rowSeparator;
	}
	
	public void export( TableModel model, Writer writer ) throws IOException {
		export(model, null, null, writer);
	}
	
	public void export( TableModel model, TableModel columnHeaderModel, TableModel rowHeaderModel, Writer writer ) throws IOException {
		
		int rowCnt = model.getRowCount();
		int colCnt = model.getColumnCount();
		int i,e;
		boolean needSep = false;
		Object value;

		if( rowHeaderModel != null ) {
			needSep = true;
		}
		
		if( columnHeaderModel != null ) {
			for( i=0; i<colCnt; i++ ) {
				if( needSep || i > 0 ) {
					writer.append(columnSeparator);
				}
				value = columnHeaderModel.getValueAt(0, i);
				writer.append( value != null ? value.toString() : "(none)" );				
			}
		} else {
			String columnName;
			for( i=0; i<colCnt; i++ ) {
				if( needSep || i > 0 ) {
					writer.append(columnSeparator);
				}
				columnName = model.getColumnName(i);
				writer.append( columnName != null ? columnName : "" );
			}			
		}

		writer.append(rowSeparator);
		
		for( i=0; i<rowCnt; i++ ) {
			if( rowHeaderModel != null ) {
				value = rowHeaderModel.getValueAt(i, 0);
				writer.append( value != null ? value.toString() : "(none)" );				
			}
			for( e=0; e<colCnt; e++ ) {
				if( needSep || e > 0 ) {
					writer.append(columnSeparator);
				}
				value = model.getValueAt(i, e);
				writer.append( value != null ? value.toString() : "(none)" );
			}
			writer.append(rowSeparator);
		}
					
	}

	public void export( TableModel model, File file ) throws IOException {
		export(model, null, null, file);
	}
	
	public void export( TableModel model, TableModel columnHeaderModel, TableModel rowHeaderModel, File file ) throws IOException {
		
		Writer writer = null;
		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			export(model, columnHeaderModel, rowHeaderModel, writer);
		} finally {
			writer.close();
		}
		
	}
	
	public void exportToClipboard( TableModel model ) {
		exportToClipboard(model, null, null);
	}
	
	public void exportToClipboard( TableModel model, TableModel columnHeaderModel, TableModel rowHeaderModel ) {
		
		StringWriter writer = new StringWriter();
		
		try {
			export(model, columnHeaderModel, rowHeaderModel, writer);
		} catch (IOException ex) {
			throw new SanityCheckException("StringWriter threw IOException", ex);
		}
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				
		clipboard.setContents(new StringSelection(writer.toString()), this);
		
	}

	public void export( WriterExportableTable exportable, File file ) throws IOException {
		export( exportable, file, null );
	}
	
	public void export( WriterExportableTable exportable, File file, Object userObject ) throws IOException {
		
		Writer writer = null;
		try {
			writer = new BufferedWriter( new FileWriter( file ) );
			exportable.export(writer, columnSeparator, rowSeparator, userObject);
		} finally {
			if( writer != null ) {
				writer.close();
			}
		}
		
	}

	public void exportToClipboard(WriterExportableTable exportable) {
		exportToClipboard( exportable, null );
	}
	
	public void exportToClipboard(WriterExportableTable exportable, Object userObject) {
		
		StringWriter writer = new StringWriter();
		
		try {
			exportable.export(writer, columnSeparator, rowSeparator, userObject);
		} catch (IOException ex) {
			throw new SanityCheckException("StringWriter threw IOException", ex);
		}
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				
		clipboard.setContents(new StringSelection(writer.toString()), this);
		
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// don't care
	}
	
}
