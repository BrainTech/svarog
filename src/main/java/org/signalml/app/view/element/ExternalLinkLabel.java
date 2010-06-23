/* ExternalLinkLabel.java created 2008-02-14
 * 
 */

package org.signalml.app.view.element;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/** ExternalLinkLabel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExternalLinkLabel extends JLabel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExternalLinkLabel.class);
	
	private URI link;

	@SuppressWarnings("unchecked")
	public ExternalLinkLabel(String text, URI link) {
		super(text);
		this.link = link;

		setForeground( Color.BLUE );
		Font f = getFont().deriveFont( Font.PLAIN, 10 );
		Map map = f.getAttributes();
		map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);		
		setFont( new Font(map) );
		
		setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		
		addMouseListener( new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if( SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1 ) {
					try {
						Desktop.getDesktop().browse(ExternalLinkLabel.this.link);
					} catch (IOException ex) {
						logger.error( "Failed to browse", ex );
					}
				}
			}
			
		});
	
	}
		
}
