/* VisualReferenceEditorScrollable.java created 2007-12-02
 * 
 */

package org.signalml.app.view.montage;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/** VisualReferenceEditorScrollable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceEditorScrollable extends JPanel implements Scrollable {

	// XXX currently not used
	
	private static final long serialVersionUID = 1L;
	
	private VisualReferenceEditor editor;

	public VisualReferenceEditorScrollable(VisualReferenceEditor editor) {
		this.editor = editor;
		setBackground(editor.getBackground());
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		add(editor);
	}

	public VisualReferenceEditor getEditor() {
		return editor;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension editorSize = editor.getPreferredSize();
		JViewport viewport = editor.getViewport();
		if( viewport == null ) {
			return super.getPreferredSize();
		}
		Dimension viewportSize = viewport.getExtentSize();
		Dimension size = new Dimension( Math.max( editorSize.width, viewportSize.width ), Math.max( editorSize.height, viewportSize.height ) );
		return size;
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 100;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}	

}
