package org.signalml.app.util.logging;

import static java.lang.String.format;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Colour-coded console appender for Log4J.
 *
 * This file is licensed under the Creative Commons CC0 license.
 *
 * Idea taken from
 * http://blog.uncommons.org/2006/04/09/colour-coded-console-logging-with-log4j/.
 * This is mostly tabular data taken from ANSI documentation, which I assume is
 * in the public domain. The rest of the code is either directly dictated by the
 * public interface of console appender or mine (I rewrote the code because the
 * original didn't specify a license and I want to avoid copyright problems).
 */

public class ColorConsoleAppender extends ConsoleAppender
{
	private static final int
	NORMAL = 0,
	BRIGHT = 1,
	FOREGROUND_BLACK = 30,
	FOREGROUND_RED = 31,
	FOREGROUND_GREEN = 32,
	FOREGROUND_YELLOW = 33,
	FOREGROUND_BLUE = 34,
	FOREGROUND_MAGENTA = 35,
	FOREGROUND_CYAN = 36,
	FOREGROUND_WHITE = 37;

	private static String fmt(int bright, int color) {
		return format("\u001b[%d;%dm", bright, color);
	}

	private static final String
	FATAL_COLOR = fmt(BRIGHT, FOREGROUND_RED),
	ERROR_COLOR = fmt(NORMAL, FOREGROUND_RED),
	WARN_COLOR = fmt(NORMAL , FOREGROUND_YELLOW),
	INFO_COLOR = fmt(NORMAL, FOREGROUND_GREEN),
	DEBUG_COLOR = fmt(NORMAL, FOREGROUND_CYAN),
	TRACE_COLOR = fmt(NORMAL, FOREGROUND_BLUE),
	END_COLOR = "\u001b[m";

	private static final Map<Integer, String> colors = new HashMap<Integer, String>();
	static {
		colors.put(Priority.FATAL_INT, FATAL_COLOR);
		colors.put(Priority.ERROR_INT, ERROR_COLOR);
		colors.put(Priority.WARN_INT, WARN_COLOR);
		colors.put(Priority.INFO_INT, INFO_COLOR);
		colors.put(Priority.DEBUG_INT, DEBUG_COLOR);
	}

	/**
	 * Wraps the ANSI control characters around the output from the
	 * super-class Appender.
	 */
	protected void subAppend(LoggingEvent event) {
		this.qw.write(getColor(event.getLevel()));
		super.subAppend(event);
		this.qw.write(END_COLOR);

		if (this.immediateFlush)
			this.qw.flush();
	}

	/**
	 * Get the appropriate control characters to change
	 * the colour for the specified logging level.
	 */
	private String getColor(Level level) {
		String val = colors.get(level.toInt());
		return val != null ? val : TRACE_COLOR;
	}
}
