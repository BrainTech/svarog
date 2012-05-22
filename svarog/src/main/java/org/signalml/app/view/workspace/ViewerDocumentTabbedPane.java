/* ViewerDocumentTabbedPane.java created 2007-09-17
 *
 */
package org.signalml.app.view.workspace;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.View;
import org.signalml.app.view.components.ViewerDocumentTabbedPaneTabComponent;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.DocumentView;
import org.springframework.context.MessageSourceResolvable;

/** ViewerDocumentTabbedPane
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerDocumentTabbedPane extends JTabbedPane implements DocumentManagerListener, ActionFocusListener {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(ViewerDocumentTabbedPane.class);
	/**
	 * The maximum length a tab title can have. Document names that are longer
	 * than this will be shortened.
	 */
	private static int MAXIMUM_TAB_TITLE_LENGTH = 35;
	private ActionFocusManager actionFocusManager;
	private View view;

	/**
	 * DocumentFlowIntegrator for closing documents using the cross
	 * on the tabs.
	 */
	private DocumentFlowIntegrator documentFlowIntegrator;

	public ViewerDocumentTabbedPane() {
		super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	public void initialize() {
		setBorder(new EmptyBorder(3, 3, 3, 3));

		KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrlTab, "nextDocument");
		getActionMap().put("nextDocument", new NextDocumentAction());

		KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrlShiftTab, "previousDocument");
		getActionMap().put("previousDocument", new PreviousDocumentAction());
	}

	@Override
	public void documentAdded(DocumentManagerEvent e) {
		Document document = e.getDocument();
		if ((document instanceof SignalDocument)
				|| (document instanceof BookDocument)
				|| (document instanceof MonitorSignalDocument)) {
			addDocumentTab(document);
		}
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {

		Document document = e.getDocument();
		int index = indexOfComponent(document.getDocumentView());
		if (index >= 0) {

			String tabTitle = getTabTitle(document);
			String tabTooltip = getTabTooltip(document);
			setTitleAt(index, tabTitle);
			setToolTipTextAt(index, tabTooltip);

		}

	}

	/**
	 * Returns the document name of the given document.
	 * @param document the document
	 * @return the name of the document
	 */
	protected String getDocumentName(Document document) {

		String documentName = null;
		if (document instanceof MessageSourceResolvable) {
			documentName = ((MessageSourceResolvable) document).getDefaultMessage();
		} else {
			documentName = document.toString();
		}

		return documentName;

	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		removeDocumentTab(e.getDocument());
	}

	@Override
	public void actionFocusChanged(ActionFocusEvent e) {
		Document document = actionFocusManager.getActiveDocument();
		if (document != null) {
			showDocument(document);
		}
	}

	public void addDocumentTab(Document document) {

		DocumentView documentViewPanel;
		try {
			documentViewPanel = view.createDocumentViewPanel(document);
		} catch (SignalMLException ex) {
			logger.error("Failed to create signal view", ex);
			Dialogs.showExceptionDialog(this, ex);
			return;
		}

		ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
		Icon icon = null;
		if (type != null) {
			icon = type.getIcon();
		}

		document.setDocumentView(documentViewPanel);

		String tabTitle = getTabTitle(document);
		String tabTooltip = getTabTooltip(document);
		addTab(tabTitle, icon, documentViewPanel, tabTooltip);

		this.setTabComponentAt(this.getTabCount()-1, new ViewerDocumentTabbedPaneTabComponent(this));

	}

	/**
	 * Returns a tab title for a given document.
	 * @param document a document for which the tab title will be returned
	 * @return a tab title for the document.
	 */
	private String getTabTitle(Document document) {

		String documentName = getDocumentName(document);
		String tabTitle;

		if (documentName.length() > MAXIMUM_TAB_TITLE_LENGTH) {

			String dots = "...";
			int substringLengthWithoutDots = MAXIMUM_TAB_TITLE_LENGTH - dots.length();

			tabTitle = documentName.substring(0, substringLengthWithoutDots);
			tabTitle += dots;

		} else {
			tabTitle = documentName;
		}

		return tabTitle;

	}

	/**
	 * Returns a tab tooltip for a given document.
	 * @param document a document for which the tooltip will be returned
	 * @return a tooltip for the document
	 */
	private String getTabTooltip(Document document) {
		String documentName = getDocumentName(document);
		String tabTooltip = documentName;
		return tabTooltip;
	}

	public void removeDocumentTab(Document document) {

		DocumentView documentView = (DocumentView) document.getDocumentView();
		if (documentView != null) {
			remove(documentView);
			document.setDocumentView(null);
			documentView.destroy();
		}

	}

	public void showDocument(Document document) {
		setSelectedComponent(document.getDocumentView());
	}

	public Document getDocumentInTab(int index) {
		DocumentView dv = (DocumentView) getComponentAt(index);
		return dv.getDocument();
	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	/**
	 * Sets the DocumentFlowIntegrator to be used for closing documents
	 * using the cross on the tabs.
	 * @param documentFlowIntegrator
	 */
	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	/**
	 * Returns the DocumentFlowIntegrator which is used for closing documents
	 * using the cross on the tabs.
	 * @return
	 */
	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	private class NextDocumentAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			int cnt = getTabCount();
			int index = (getSelectedIndex() + 1) % cnt;
			setSelectedIndex(index);

		}
	}

	private class PreviousDocumentAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			int cnt = getTabCount();
			int index = (cnt + getSelectedIndex() - 1) % cnt;
			setSelectedIndex(index);

		}
	}

}
