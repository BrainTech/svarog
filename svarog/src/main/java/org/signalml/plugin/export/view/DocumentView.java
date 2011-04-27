/* DocumentViewComponent.java created 2007-10-16
 *
 */

package org.signalml.plugin.export.view;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.signalml.plugin.export.signal.Document;

/**
 * This class represents a view that is attached to the document.
 * Allows to return this document and destroy this view.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class DocumentView extends JPanel{

	static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public DocumentView() {
		super();
	}

	/**
	 * Creates a new document view with <code>FlowLayout</code>.
	 * If <code>isDoubleBuffered</code> is true, this view
     * will use a double buffer.
	 * @param isDoubleBuffered true for double-buffering, which
     * uses additional memory space to achieve fast, flicker-free 
     * updates
     * @see JPanel#JPanel(boolean)
	 */
	public DocumentView(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	/**
	 * Creates a new document view with the specified layout manager.
	 * If <code>isDoubleBuffered</code> is true, this view
     * will use a double buffer.
	 * @param layout the LayoutManager to use
	 * @param isDoubleBuffered true for double-buffering, which
     * uses additional memory space to achieve fast, flicker-free 
     * updates
     * @see JPanel#JPanel(LayoutManager, boolean)
	 */
	public DocumentView(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	/**
	 * Create a new document view with the specified layout manager
	 * and double buffering.
	 * @param layout the LayoutManager to use
	 * @see JPanel#JPanel(LayoutManager)
	 */
	public DocumentView(LayoutManager layout) {
		super(layout);
	}
	
	/**
	 * Returns a document to which this view is attached.
	 * @return a document to which this view is attached
	 */
	public abstract Document getDocument();

	/**
	 * Destroys this view. Removes listeners and destroys
	 * plot.
	 */
	public abstract void destroy();

}
