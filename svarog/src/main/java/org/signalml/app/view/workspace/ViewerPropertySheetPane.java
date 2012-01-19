/* ViewerPropertySheetPane.java created 2007-09-14
 *
 */
package org.signalml.app.view.workspace;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.signalml.app.model.components.PropertySheetModel;

/** ViewerPropertySheetPane
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerPropertySheetPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private ViewerPropertySheet sheet;

	private PropertySheetModel propertySheetModel;

	public void initialize() {

		setLayout(new BorderLayout());

		sheet = new ViewerPropertySheet(propertySheetModel);
		scrollPane = new JScrollPane(sheet,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane,BorderLayout.CENTER);

	}

	public PropertySheetModel getPropertySheetModel() {
		return propertySheetModel;
	}

	public void setPropertySheetModel(PropertySheetModel propertySheetModel) {
		this.propertySheetModel = propertySheetModel;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public ViewerPropertySheet getSheet() {
		return sheet;
	}

}
