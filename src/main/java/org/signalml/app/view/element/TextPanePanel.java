/* TextPanePanel.java created 2007-10-24
 * 
 */

package org.signalml.app.view.element;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

/** TextPanePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TextPanePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTextPane textPane;
	private JScrollPane scrollPane;
	
	public TextPanePanel(String title) {
		super(new BorderLayout());
		
		if( title != null && !title.isEmpty() ) {
			setBorder( new TitledBorder( title ) );
		}
		
		textPane = new JTextPane();
		
		scrollPane = new JScrollPane(textPane);
		
		add(scrollPane);		
		
	}
	
	public JTextPane getTextPane() {
		return textPane;
	}	

}
