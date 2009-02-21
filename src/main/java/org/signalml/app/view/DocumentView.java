/* DocumentViewComponent.java created 2007-10-16
 * 
 */

package org.signalml.app.view;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.signalml.app.document.Document;

/** DocumentViewComponent
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class DocumentView extends JPanel {

	static final long serialVersionUID = 1L;

	public DocumentView() {
		super();
	}

	public DocumentView(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public DocumentView(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public DocumentView(LayoutManager layout) {
		super(layout);
	}

	public abstract Document getDocument();
	
	public abstract void destroy();

}
