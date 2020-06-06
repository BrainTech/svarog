/* View.java created 2007-09-10
 *
 */
package org.signalml.app.view;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.DocumentView;

/** View
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface View {

	void setStatus(String status);

	void closeView();

	DocumentView createDocumentViewPanel(Document document) throws SignalMLException;

	void setViewMode(boolean viewMode);
	boolean isViewMode();

	void setStatusBarVisible(boolean visible);
	boolean isStatusBarVisible();

	void setLeftPanelVisible(boolean visible);
	boolean isLeftPanelVisible();

	void setBottomPanelVisible(boolean visible);
	boolean isBottomPanelVisible();

}
