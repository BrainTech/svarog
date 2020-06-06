/* PropertySheetModel.java created 2007-09-11
 *
 */
package org.signalml.app.model.components;

import java.awt.Component;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.workspace.ViewerTabbedPane;
import org.signalml.plugin.export.view.ViewerTreePane;
import org.signalml.util.FormatUtils;
import org.springframework.context.MessageSourceResolvable;

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
	private DecimalFormat numberFormat;
	private TreePath treePath = null;

	@Override
	public String getColumnName(int col) {

		switch (col) {

		case 0 :
			return _("Property name");

		case 1 :
			return _("Value");

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
				labels[row] = descriptors[row].getDefaultMessage();
			}
			return labels[row];

		case 1 :
			Object value;
			try {
				Method method = descriptors[row].getReadMethod();
				if (method == null) {
					return "-";
				}
				value = method.invoke(subject);
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
			catch (java.lang.NullPointerException ex){
				logger.error("No getter", ex.getCause());
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
						return _("Yes");
					} else {
						return _("No");
					}
				}
				if (value instanceof Date) {
					return FormatUtils.formatTime((Date) value);
				}
				if (value instanceof File) {
					return ((File) value).getAbsolutePath();
				}
				if (value instanceof MessageSourceResolvable) {
					return ((MessageSourceResolvable) value).getDefaultMessage();
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

