/* HelpDialog.java created 2007-11-14
 *
 */

package org.signalml.app.view.dialog;

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
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;

/** HelpDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class HelpDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private JTextPane helpPane;
	private JScrollPane scrollPane;

	private Stack<URL> backURLs = new Stack<URL>();
	private Stack<URL> forwardURLs = new Stack<URL>();

	private URL currentURL = null;

	private URL helpContentsURL = null;

	private ReloadAction reloadAction;
	private HomeAction homeAction;
	private BackAction backAction;
	private ForwardAction forwardAction;

	public HelpDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public HelpDialog(MessageSourceAccessor messageSource,Window w, boolean isModal) {
		super(messageSource,w, isModal);
	}

	@Override
	protected void initialize() {

		setTitle(messageSource.getMessage("help.title"));
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

	private JTextPane getHelpPane() {
		if (helpPane == null) {
			helpPane = new JTextPane();
			helpPane.setEditable(false);
		}
		return helpPane;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getHelpPane());
			scrollPane.setPreferredSize(new Dimension(800,600));
		}
		return scrollPane;
	}

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

	private void setPageInternal(URL url) throws SignalMLException {

		JTextPane helpPane = getHelpPane();
		try {
			helpPane.setPage(url);
		} catch (IOException ex) {
			logger.error("Failed to display URL [" + url.toString() + "]");
			throw new SignalMLException(ex);
		}

	}

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

	public void reset() {
		backURLs.clear();
		forwardURLs.clear();
		if (isInitialized()) {
			setActionsEnabled();
		}
	}

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

	public void reload() throws SignalMLException {

		if (currentURL == null) {
			return;
		}

		// this forces reload
		Document doc = getHelpPane().getDocument();
		doc.putProperty(Document.StreamDescriptionProperty, null);

		setPageInternal(currentURL);

	}

	public void setActionsEnabled() {
		reloadAction.setEnabled();
		backAction.setEnabled();
		forwardAction.setEnabled();
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		setPage((URL) model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null || URL.class.isAssignableFrom(clazz));
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	protected class ReloadAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ReloadAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/reload.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("help.reloadToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				reload();
			} catch (SignalMLException ex) {
				logger.error("Failed to reload", ex);
			}
		}

		public void setEnabled() {
			setEnabled(currentURL != null);
		}

	}

	protected class HomeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public HomeAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/help.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("help.homeToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				setPage(null);
			} catch (SignalMLException ex) {
				logger.error("Failed to go to contents", ex);
			}
		}

	}

	protected class BackAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public BackAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/back.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("help.backToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				back();
			} catch (SignalMLException ex) {
				logger.error("Failed to go back", ex);
			}
		}

		public void setEnabled() {
			setEnabled(!backURLs.isEmpty());
		}

	}

	protected class ForwardAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ForwardAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/forward.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("help.forwardToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {
			try {
				forward();
			} catch (SignalMLException ex) {
				logger.error("Failed to go forward", ex);
			}
		}

		public void setEnabled() {
			setEnabled(!forwardURLs.isEmpty());
		}

	}


}
