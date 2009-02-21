/* DashListCellRenderer.java created 2007-11-13
 * 
 */

package org.signalml.app.view.element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/** DashListCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DashListCellRenderer extends JComponent implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

    private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    private static final Dimension PREFERRED_SIZE = new Dimension(100,18);
	
    private float[] dash;
    
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		
		setEnabled(list.isEnabled());
		setFont(list.getFont());

        Border border = null;
        if (cellHasFocus) {
            if (isSelected) {
                border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("List.focusCellHighlightBorder");
            }
        } else {
            border = NO_FOCUS_BORDER;
        }

        setBorder(border);
		
        TagStylePropertiesPanel.Dash dashObj = (TagStylePropertiesPanel.Dash) value;
        dash = ( dashObj != null ? dashObj.dash : null );
        
		return this;
		
	}
	
	@Override
	protected void paintComponent(Graphics gOrig) {
		super.paintComponent(gOrig);
		
		Graphics2D g = (Graphics2D) gOrig; 
		
		g.setColor(Color.BLUE);
		g.setStroke( new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, dash, 0 ) );
		
		Insets insets = getInsets();		
		Dimension size = getSize();
		int level = (size.height - (insets.top+insets.bottom)) / 2;
				
		g.drawLine( insets.left + 3, level, size.width - (insets.left+insets.right+7), level );
		
	}
	
	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

}
