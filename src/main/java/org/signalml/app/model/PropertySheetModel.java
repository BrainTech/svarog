/* PropertySheetModel.java created 2007-09-11
 *
 */
package org.signalml.app.model;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.signalml.app.view.ViewerTabbedPane;
import org.signalml.app.view.ViewerTreePane;
import org.signalml.util.Util;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** PropertySheetModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PropertySheetModel extends AbstractTableModel implements TreeSelectionListener, ChangeListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(PropertySheetModel.class);

	private PropertyProvider subject;
	private LabelledPropertyDescriptor[] descriptors;
	private String[] labels;
	private PropertyEditor[] editors;
	private MessageSourceAccessor messageSource;

	private DecimalFormat numberFormat;

	private TreePath treePath = null;

	@Override
	public String getColumnName(int col) {

		switch (col) {

		case 0 :
			return messageSource.getMessage("viewer.propertySheet.name");

		case 1 :
			return messageSource.getMessage("viewer.propertySheet.value");

		default:
			return "???";

		}

	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		if (subject == null) {
			return 0;
		}
		return descriptors.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {

		case 0 :
			if (labels[row] == null) {
				labels[row] = messageSource.getMessage(descriptors[row]);
			}
			return labels[row];

		case 1 :
			Object value;
			try {
				value = descriptors[row].getReadMethod().invoke(subject);
			} catch (IllegalArgumentException ex) {
				logger.error("Failed to invoke getter", ex);
				return "???";
			} catch (IllegalAccessException ex) {
				logger.error("Failed to invoke getter", ex);
				return "???";
			} catch (InvocationTargetException ex) {
				logger.error("Getter threw exception", ex.getCause());
				return "???";
			}
			if (value == null) {
				return "-";
			}
			if (editors[row] == null) {
				editors[row] = descriptors[row].createPropertyEditor(subject);
			}
			if (editors[row] == null) {
				if (value instanceof String) {
					return value;
				}
				if (value instanceof Number) {
					if (numberFormat == null) {
						return value.toString();
					} else {
						return numberFormat.format(value);
					}
				}
				if (value instanceof Boolean) {
					if (((Boolean) value).booleanValue()) {
						return messageSource.getMessage("yesCapital");
					} else {
						return messageSource.getMessage("noCapital");
					}
				}
				if (value instanceof Date) {
					return Util.formatTime((Date) value);
				}
				if (value instanceof File) {
					return ((File) value).getAbsolutePath();
				}
				if (value instanceof MessageSourceResolvable) {
					return messageSource.getMessage((MessageSourceResolvable) value);
				}
				editors[row] = PropertyEditorManager.findEditor(descriptors[row].getPropertyType());
				if (editors[row] == null) {
					editors[row] = new ToStringPropertyEditor(subject);
				}
			}
			if (editors[row] instanceof TreePathAwarePropertyEditor) {
				((TreePathAwarePropertyEditor) editors[row]).setTreePath(treePath);
			}
			editors[row].setValue(value);
			String text = editors[row].getAsText();
			return (text != null ? text : "???");

		default:
			return "???";

		}
	}

	public PropertyProvider getSubject() {
		return subject;
	}

	public void setSubject(PropertyProvider subject) {
		if (this.subject != subject) {
			this.subject = subject;
			if (subject != null) {
				List<LabelledPropertyDescriptor> list = null;
				try {
					list = subject.getPropertyList();
				} catch (IntrospectionException ex) {
					logger.error("Failed to get properties", ex);
					setSubject(null);
					return;
				}
				descriptors = new LabelledPropertyDescriptor[list.size()];
				list.toArray(descriptors);
				labels = new String[descriptors.length];
				editors = new PropertyEditor[descriptors.length];
			} else {
				subject = null;
				descriptors = new LabelledPropertyDescriptor[0];
				labels = new String[0];
				editors = new PropertyEditor[0];
			}
			fireTableDataChanged();
		}
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public void setTreePath(TreePath path) {
		this.treePath = path;
		if (path != null) {
			Object component = path.getLastPathComponent();
			if (component instanceof PropertyProvider) {
				setSubject((PropertyProvider) component);
				return;
			}
		}
		setSubject(null);
	}

	public DecimalFormat getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(DecimalFormat numberFormat) {
		if (this.numberFormat != numberFormat) {
			this.numberFormat = numberFormat;
			fireTableDataChanged();
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		setTreePath(e.getNewLeadSelectionPath());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source instanceof ViewerTabbedPane) {
			ViewerTabbedPane viewerTabbedPane = (ViewerTabbedPane) source;
			Component selection = viewerTabbedPane.getSelectedComponent();
			if (selection instanceof ViewerTreePane) {
				ViewerTreePane viewerTreePane = (ViewerTreePane) selection;
				JTree tree = viewerTreePane.getTree();
				setTreePath(tree.getSelectionPath());
			}
		}
	}

}

