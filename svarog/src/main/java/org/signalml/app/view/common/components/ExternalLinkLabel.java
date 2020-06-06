/* ExternalLinkLabel.java created 2008-02-14
 *
 */

package org.signalml.app.view.common.components;

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

/**
 * The label with the link, which has following features:
 * <ul>
 * <li>the blue foreground color,</li>
 * <li>underline,</li>
 * <li>hand cursor,</li>
 * <li>the listener which browses to the address of this link.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExternalLinkLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExternalLinkLabel.class);

	/**
	 * the address of this link
	 */
	private URI link;

	/**
	 * Constructor. Creates the {@link ExternalLinkLabel} with:
	 * <ul>
	 * <li>the blue foreground color,</li>
	 * <li>underline,</li>
	 * <li>hand cursor,</li>
	 * <li>the listener which browses to the address of this link.</li>
	 * </ul>
	 * @param text the text of the link
	 * @param link the address of the link
	 */
	@SuppressWarnings("unchecked")
	public ExternalLinkLabel(String text, URI link) {
		super(text);
		this.link = link;

		setForeground(Color.BLUE);
		Font f = getFont().deriveFont(Font.PLAIN, 10);
		Map map = f.getAttributes();
		map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		setFont(new Font(map));

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					try {
						Desktop.getDesktop().browse(ExternalLinkLabel.this.link);
					} catch (IOException ex) {
						logger.error("Failed to browse", ex);
					}
				}
			}

		});

	}

}
