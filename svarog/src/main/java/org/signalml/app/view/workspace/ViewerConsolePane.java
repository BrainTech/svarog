/* ViewerConsolePane.java created 2007-09-11
 *
 */
package org.signalml.app.view.workspace;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.util.logging.ViewerConsoleAppender;
import org.signalml.app.view.Console;
import org.signalml.app.view.common.dialogs.OptionPane;

/** ViewerConsolePane
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerConsolePane extends JPanel implements Console {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(ViewerConsolePane.class);

	private JScrollPane scrollPane;
	private PlainDocument document;
	private JTextArea textArea;
	private boolean scrollLock = false;
	private int lockedCaretPosition = 0;

	private ViewerFileChooser fileChooser;
	private ViewerConsoleAppender consoleAppender;

	public void initialize() {

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(3,3,3,3));

		JToolBar consoleToolBar = new JToolBar(JToolBar.HORIZONTAL);
		consoleToolBar.setFloatable(false);
		consoleToolBar.add(Box.createHorizontalGlue());

		LogDivertLevelListener logDivertLevelListener = new LogDivertLevelListener();

		JToggleButton noLogDivertButton = new JToggleButton("", IconUtils.loadClassPathIcon("org/signalml/app/icon/stop.png"));
		noLogDivertButton.setActionCommand("0");
		noLogDivertButton.addActionListener(logDivertLevelListener);
		noLogDivertButton.setToolTipText(_("Do not show log messages in the console"));
		JToggleButton errorLogDivertButton = new JToggleButton("", IconUtils.loadClassPathIcon("org/signalml/app/icon/error.png"));
		errorLogDivertButton.setActionCommand("1");
		errorLogDivertButton.addActionListener(logDivertLevelListener);
		errorLogDivertButton.setToolTipText(_("Show only warning and error log messages in the console"));
		JToggleButton debugLogDivertButton = new JToggleButton("", IconUtils.loadClassPathIcon("org/signalml/app/icon/bug.png"));
		debugLogDivertButton.setActionCommand("2");
		debugLogDivertButton.addActionListener(logDivertLevelListener);
		debugLogDivertButton.setToolTipText(_("Show all log messages in the console, including debug messages"));

		ButtonGroup bg = new ButtonGroup();
		bg.add(noLogDivertButton);
		bg.add(errorLogDivertButton);
		bg.add(debugLogDivertButton);
		bg.setSelected(errorLogDivertButton.getModel(), true);

		consoleToolBar.add(noLogDivertButton);
		consoleToolBar.add(errorLogDivertButton);
		consoleToolBar.add(debugLogDivertButton);

		consoleToolBar.addSeparator(new Dimension(20,1));

		JToggleButton scrollLockButton = new JToggleButton(new ScrollLockAction());
		scrollLockButton.setText("");

		consoleToolBar.add(scrollLockButton);
		consoleToolBar.add(new ClearAction());
		if (fileChooser != null) {
			consoleToolBar.add(new SaveAsTextAction());
		}

		this.add(consoleToolBar,BorderLayout.NORTH);

		document = new PlainDocument();

		textArea = new JTextArea(document);
		textArea.setEditable(false);

		consoleAppender = new ViewerConsoleAppender();
		consoleAppender.setConsole(this);
		PatternLayout layout = new PatternLayout("%p [%c] - %m\n");
		consoleAppender.setLayout(layout);
		consoleAppender.setThreshold(Level.WARN);
		Logger.getRootLogger().addAppender(consoleAppender);

		scrollPane = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.add(scrollPane,BorderLayout.CENTER);

	}

	public void addText(final String text) {
		final Runnable task = new Runnable() {
			public void run() {
				if (null != document) {
					synchronized (document) {
						try {
							document.insertString(document.getLength(), text, null);
							if (scrollLock) {
								textArea.setCaretPosition(lockedCaretPosition);
							} else {
								textArea.setCaretPosition(document.getLength());
							}
						} catch (BadLocationException ex) {
							logger.error("Bad document location", ex);
						}
					}
				}
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			task.run();
		} else {
			SwingUtilities.invokeLater(task);
		}
	}

	/** Passes text to {@link #addText} first appending '\n' at the end if it isn't there. */
	public void addTextNL(String text) {
		if (text.endsWith("\n"))
			addText(text);
		else
			addText(text + "\n");
	}

	public void saveToFile(File file) throws IOException {
		Writer w = null;
		try {
			w = new FileWriter(file);
			synchronized (document) {
				try {
					w.write(document.getText(0, document.getLength()));
				} catch (BadLocationException ex) {
					logger.error("Bad document location", ex);
				}
			}
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}


	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	class ScrollLockAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ScrollLockAction() {
			super(_("Lock scroll"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/lock.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Prevent the console from scrolling when new text is appended"));
		}

		public void actionPerformed(ActionEvent ev) {
			JToggleButton tb = (JToggleButton) ev.getSource();
			scrollLock = tb.isSelected();
			if (scrollLock) {
				lockedCaretPosition = textArea.viewToModel(SwingUtilities.convertPoint(scrollPane.getViewport(), 0, scrollPane.getViewport().getHeight()/2, textArea));
			}
		}

	}

	class ClearAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ClearAction() {
			super(_("Clear"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/trash.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Clear the console"));
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				synchronized (document) {
					document.replace(0, document.getLength(), "", null);
				}
			} catch (BadLocationException ex) {
				logger.error("Bad document location", ex);
			}
			lockedCaretPosition = 0;
		}

	}

	class SaveAsTextAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveAsTextAction() {
			super(_("Save as text"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_save.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Save the contents of the console to a text file"));
		}

		public void actionPerformed(ActionEvent ev) {
			File file = fileChooser.chooseConsoleSaveAsTextFile(ViewerConsolePane.this);
			if (file != null) {
				try {
					saveToFile(file);
				} catch (IOException ex) {
					logger.error("Failed to save console text to file", ex);
					OptionPane.showException(ViewerConsolePane.this, "error.failedToSaveFile", ex);
				}
			}
		}

	}

	class LogDivertLevelListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int command = Integer.parseInt(e.getActionCommand());

			switch (command) {

			case 1:
				consoleAppender.setThreshold(Level.WARN);
				Logger.getRootLogger().addAppender(consoleAppender);
				break;

			case 2:
				consoleAppender.setThreshold(Level.DEBUG);
				Logger.getRootLogger().addAppender(consoleAppender);
				break;

			case 0:
			default:
				Logger.getRootLogger().removeAppender(consoleAppender);
				break;

			}

		}

	}

}
