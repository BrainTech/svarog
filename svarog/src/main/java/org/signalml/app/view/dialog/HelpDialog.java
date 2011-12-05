/* HelpDialog.java created 2007-11-14
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.Document;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.core.io.ClassPathResource;

/**
 * Dialog which displays the help for Svarog.
 * Contains two elements:
 * <ul>
 * <li>the web-browser style tool bar with 4 buttons ({@link BackAction back},
 * {@link ForwardAction forward}, {@link ReloadAction reload} and
 * {@link HomeAction home}),</li>
 * <li>the {@link #getScrollPane() scroll pane} with the {@link
 * #getHelpPane() help pane}.</li>
 * </ul>
 * User can use this dialog in the way similar to the web browser.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class HelpDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the text pane in which the actual help is displayed
	 */
	private JTextPane helpPane;
	/**
	 * the scroll pane which contains {@link #helpPane}
	 */
	private JScrollPane scrollPane;

	/**
	 * the stack of URLs to that can be used to go to the previously visited
	 * pages (as back in web browser)
	 */
	private Stack<URL> backURLs = new Stack<URL>();
	/**
	 * the stack of URLs to that can be used to go to pages from which we went
	 * back (as forward in web browser)
	 */
	private Stack<URL> forwardURLs = new Stack<URL>();

	/**
	 * the URL to the page that is currently displayed
	 */
	private URL currentURL = null;

	/**
	 * the URL to the contents of the help
	 */
	private URL helpContentsURL = null;

	/**
	 * the {@link ReloadAction action} which reloads the current help page
	 */
	private ReloadAction reloadAction;
	/**
	 * the {@link HomeAction action} which shows the contents of the help
	 */
	private HomeAction homeAction;
	/**
	 * the {@link BackAction action} which shows the previously visited page
	 */
	private BackAction backAction;
	/**
	 * the {@link ForwardAction action} which shows the page from which we went
	 * back
	 */
	private ForwardAction forwardAction;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public HelpDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	/**
	 * Initializes this panel:
	 * <ul>
	 * <li>sets the icon and the title,</li>
	 * <li>adds the {@code HyperlinkListener} to the {@link #getHelpPane()
	 * help panel} (the listener {@link #setPage(URL) changes} the page when
	 * the link is clicked.</li>
	 * </ul>
	 */
	@Override
	protected void initialize() {

		setTitle(_("Help for signalml"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/help.png"));

		super.initialize();

		getHelpPane().addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED) {
					logger.debug("link activated: [" + e.getURL() + "]");
					try {
						setPage(e.getURL());
					} catch (SignalMLException ex) {
						logger.error("Failed to display URL [" + e.getURL().toString() + "]");
					}
				}
			}

		});

	}

	/**
	 * Creates the interface for this dialog:
	 * <ul>
	 * <li>the tool bar with 4 buttons ({@link BackAction back},
	 * {@link ForwardAction forward}, {@link ReloadAction reload} and
	 * {@link HomeAction home}),</li>
	 * <li>the {@link #getScrollPane() scroll pane} with the {@link
	 * #getHelpPane() help pane}.</li>
	 * </ul>
	 */
	@Override
	public JComponent createInterface() {

		reloadAction = new ReloadAction();
		homeAction = new HomeAction();
		backAction = new BackAction();
		forwardAction = new ForwardAction();

		setActionsEnabled();

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(backAction);
		toolBar.add(forwardAction);
		toolBar.addSeparator();
		toolBar.add(reloadAction);
		toolBar.addSeparator();
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(homeAction);

		JPanel interfacePanel = new JPanel(new BorderLayout());
		interfacePanel.setBorder(new EmptyBorder(1,1,1,1));

		interfacePanel.add(toolBar, BorderLayout.NORTH);
		interfacePanel.add(getScrollPane(), BorderLayout.CENTER);

		return interfacePanel;

	}

	/**
	 * Returns the text pane in which the actual help is displayed.
	 * If the pane doesn't exist it is created.
	 * @return the text pane in which the actual help is displayed
	 */
	private JTextPane getHelpPane() {
		if (helpPane == null) {
			helpPane = new JTextPane();
			helpPane.setEditable(false);
		}
		return helpPane;
	}

	/**
	 * Returns the scroll pane which contains the {@link #getHelpPane() help
	 * pane}.
	 * If the pane doesn't exist it is created.
	 * @return the scroll pane which contains the help pane
	 */
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getHelpPane());
			scrollPane.setPreferredSize(new Dimension(800,600));
		}
		return scrollPane;
	}

	/**
	 * Returns the URL to the contents of the help.
	 * If the URL doens't exist it is set to the default location.
	 * @return the URL to the contents of the help
	 * @throws SignalMLException if IO exception occurs
	 */
	public URL getHomeURL() throws SignalMLException {
		if (helpContentsURL == null) {
			try {
				helpContentsURL = (new ClassPathResource("org/signalml/help/contents.html")).getURL();
			} catch (IOException ex) {
				logger.error("Failed to get help contents", ex);
				throw new SignalMLException(ex);
			}
		}
		return helpContentsURL;
	}

	/**
	 * Changes the page to the specified URL.
	 * @param url the URL to the page
	 * @throws SignalMLException for a null or invalid page specification,
	 * or exception from the stream being read
	 * @see JTextPane#setPage(URL)
	 */
	private void setPageInternal(URL url) throws SignalMLException {

		JTextPane helpPane = getHelpPane();
		try {
			helpPane.setPage(url);
		} catch (IOException ex) {
			logger.error("Failed to display URL [" + url.toString() + "]");
			throw new SignalMLException(ex);
		}

	}

	/**
	 * Changes the displayed page to the specified URL.
	 * If no URL is provided the URL to the contents of the help is used.
	 * <p>
	 * Performs operations necessary when the page is chagned:
	 * <ul>
	 * <li>adds the current URL to the stack of closed pages,</li>
	 * <li>clears the stack of 'forward' pages,</li>
	 * <li>{@link #setActionsEnabled() sets} which buttons should be
	 * active.</li></ul>
	 * @param url the URL to the page
	 * @throws SignalMLException for a null or invalid page specification,
	 * or exception from the stream being read
	 */
	public void setPage(URL url) throws SignalMLException {

		URL targetURL = (url != null ? url : getHomeURL());

		setPageInternal(targetURL);

		if (currentURL != null) {
			backURLs.push(currentURL);
		}
		forwardURLs.clear();
		currentURL = targetURL;

		setActionsEnabled();

	}

	/**
	 * Clears the stacks of back and forward pages and {@link
	 * #setActionsEnabled() sets} the state of buttons according to it.
	 */
	public void reset() {
		backURLs.clear();
		forwardURLs.clear();
		if (isInitialized()) {
			setActionsEnabled();
		}
	}

	/**
	 * Changes the currently displayed page to the previously visited one
	 * (from the 'back' stack):
	 * <ul>
	 * <li>if there is no URL in this stack does nothing, otherwise</li>
	 * <li>adds the current URL to the stack of 'forward' pages,</li>
	 * <li>{@link #setActionsEnabled() sets} which buttons should be
	 * active.</li>
	 * </ul>
	 * @throws SignalMLException for a null or invalid page specification,
	 * or exception from the stream being read
	 */
	public void back() throws SignalMLException {

		if (backURLs.isEmpty()) {
			return;
		}

		URL targetURL = backURLs.pop();

		setPageInternal(targetURL);

		if (currentURL != null) {
			forwardURLs.push(currentURL);
		}
		currentURL = targetURL;

		setActionsEnabled();

	}

	/**
	 * Changes the currently displayed page to the page from which the user
	 * went back (first from the 'forward' stack):
	 * <ul>
	 * <li>if there is no URL in this stack does nothing, otherwise</li>
	 * <li>adds the current URL to the stack of 'back' pages,</li>
	 * <li>{@link #setActionsEnabled() sets} which buttons should be
	 * active.</li>
	 * </ul>
	 * @throws SignalMLException for a null or invalid page specification,
	 * or exception from the stream being read
	 */
	public void forward() throws SignalMLException {

		if (forwardURLs.isEmpty()) {
			return;
		}

		URL targetURL = forwardURLs.pop();

		setPageInternal(targetURL);

		if (currentURL != null) {
			backURLs.push(currentURL);
		}
		currentURL = targetURL;

		setActionsEnabled();

	}

	/**
	 * Reloads the current page. If there is no page does nothing.
	 * @throws SignalMLException for invalid page specification,
	 * or exception from the stream being read
	 */
	public void reload() throws SignalMLException {

		if (currentURL == null) {
			return;
		}

		// this forces reload
		Document doc = getHelpPane().getDocument();
		doc.putProperty(Document.StreamDescriptionProperty, null);

		setPageInternal(currentURL);

	}

	/**
	 * Sets if the following actions should be enabled:
	 * <ul>
	 * <li>the {@link ReloadAction#setEnabled() reload action},</li>
	 * <li>the {@link BackAction#setEnabled() back action},</li>
	 * <li>the {@link ForwardAction#setEnabled() forward action}.</li>
	 * </ul>
	 */
	public void setActionsEnabled() {
		reloadAction.setEnabled();
		backAction.setEnabled();
		forwardAction.setEnabled();
	}

	/**
	 * {@link #setPage(URL) Sets} the current page to the URL provided as
	 * a model.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		setPage((URL) model);
	}

	/**
	 * Does nothing, beacuse it is a read only dialog.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	/**
	 * The model for this dialog must be either null or of type URL.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null || URL.class.isAssignableFrom(clazz));
	}

	/**
	 * This dialog can not be canceled (has no CANCEL) button. 
	 */
	@Override
	public boolean isCancellable() {
		return false;
	}

	/**
	 * Action which {@link HelpDialog#reload() reloads} the current page.
	 */
	protected class ReloadAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool tip.
		 */
		public ReloadAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/reload.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Reload current page"));
		}

		/**
		 * When the action is performed the current page is
		 * {@link HelpDialog#reload() reloaded}
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				reload();
			} catch (SignalMLException ex) {
				logger.error("Failed to reload", ex);
			}
		}

		/**
		 * If the current URL is different the null this action is enabled,
		 * otherwise it is disabled.
		 */
		public void setEnabled() {
			setEnabled(currentURL != null);
		}

	}

	/**
	 * The action which changes the current page to the home page (contents of
	 * the help).
	 */
	protected class HomeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool tip.
		 */
		public HomeAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/help.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Help contents"));
		}

		/**
		 * When this action is performed the current page is changed to the
		 * home page.
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				setPage(null);
			} catch (SignalMLException ex) {
				logger.error("Failed to go to contents", ex);
			}
		}

	}

	/**
	 * The action which changes the current page to the previously visited page
	 * (the first page from 'back' stack).
	 */
	protected class BackAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool tip.
		 */
		public BackAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/back.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Previous topic"));
		}

		/**
		 * When this action is performed the {@link HelpDialog#back()} function
		 * is called.
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				back();
			} catch (SignalMLException ex) {
				logger.error("Failed to go back", ex);
			}
		}

		/**
		 * If there is at least one URL in the 'back' stack enables this action,
		 * otherwise disables it.
		 */
		public void setEnabled() {
			setEnabled(!backURLs.isEmpty());
		}

	}

	/**
	 * The action which changes the current page to the page from which te user
	 * went back (the first page from 'forward' stack).
	 */
	protected class ForwardAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the tool tip.
		 */
		public ForwardAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/forward.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Next topic"));
		}

		/**
		 * When this action is performed the {@link HelpDialog#forward()} function
		 * is called.
		 */
		public void actionPerformed(ActionEvent ev) {
			try {
				forward();
			} catch (SignalMLException ex) {
				logger.error("Failed to go forward", ex);
			}
		}

		/**
		 * If there is at least one URL in the 'forward' stack enables this
		 * action, otherwise disables it.
		 */
		public void setEnabled() {
			setEnabled(!forwardURLs.isEmpty());
		}

	}


}
