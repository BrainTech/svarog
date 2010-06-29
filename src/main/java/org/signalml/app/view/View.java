/* View.java created 2007-09-10
 *
 */
package org.signalml.app.view;

import org.signalml.app.document.Document;
import org.signalml.exception.SignalMLException;

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

	void setMainToolBarVisible(boolean visible);
	boolean isMainToolBarVisible();

	void setStatusBarVisible(boolean visible);
	boolean isStatusBarVisible();

	void setLeftPanelVisible(boolean visible);
	boolean isLeftPanelVisible();

	void setBottomPanelVisible(boolean visible);
	boolean isBottomPanelVisible();

}
