/* ViewerConsoleAppender.java created 2007-09-13
 *
 */
package org.signalml.app.util.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.signalml.app.view.workspace.ViewerConsolePane;

/** ViewerConsoleAppender
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerConsoleAppender extends AppenderSkeleton {

	private ViewerConsolePane console;

	@Override
	protected void append(LoggingEvent ev) {
		String message = null;
		if(layout.ignoresThrowable()) {
			String[] s = ev.getThrowableStrRep();
			if (s != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(layout.format(ev));
				int len = s.length;
				for(int i = 0; i < len; i++) {
					sb.append(s[i]);
					sb.append("\n");
				}
				message = sb.toString();
			} else {
				message = layout.format(ev);
			}
		} else {
			message = layout.format(ev);
		}
		console.addText(message);
	}

	@Override
	public void close() {
		// nothing to do
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	public ViewerConsolePane getConsole() {
		return console;
	}

	public void setConsole(ViewerConsolePane console) {
		this.console = console;
	}

}
