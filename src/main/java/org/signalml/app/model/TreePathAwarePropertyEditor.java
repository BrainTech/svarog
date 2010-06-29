/* TreePathAwarePropertyEditor.java created 2007-11-04
 *
 */

package org.signalml.app.model;

import java.beans.PropertyEditor;

import javax.swing.tree.TreePath;

/** TreePathAwarePropertyEditor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TreePathAwarePropertyEditor extends PropertyEditor {

	void setTreePath(TreePath treePath);

}
