/* ViewerDocumentTabbedPane.java created 2007-09-17
 * 
 */

package org.signalml.app.view;

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
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.exception.SignalMLException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerDocumentTabbedPane
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerDocumentTabbedPane extends JTabbedPane implements DocumentManagerListener, ActionFocusListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ViewerDocumentTabbedPane.class);
	
	private MessageSourceAccessor messageSource;
	private ActionFocusManager actionFocusManager;
	
	private View view;
	
	public ViewerDocumentTabbedPane() {
		super( JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT );
	}
	
	public void initialize() {
		setBorder(new EmptyBorder(3,3,3,3));		

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
		if( (document instanceof SignalDocument) || (document instanceof BookDocument) ) {
			addDocumentTab(document);
		}
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {
		
		Document document = e.getDocument();
		int index = indexOfComponent(document.getDocumentView());
		if( index >= 0 ) {
			
			String title = null;
			if( document instanceof MessageSourceResolvable ) {
				title = messageSource.getMessage((MessageSourceResolvable) document);
			} else {
				title = document.toString();
			}
			
			setTitleAt(index, title);

		}
		
	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		removeDocumentTab(e.getDocument());
	}

	
	@Override
	public void actionFocusChanged(ActionFocusEvent e) {
		Document document = actionFocusManager.getActiveDocument();
		if( document != null ) {
			showDocument(document);
		}
	}
	
	public void addDocumentTab(Document document) {

		DocumentView documentViewPanel;
		try {
			documentViewPanel = view.createDocumentViewPanel(document);
		} catch (SignalMLException ex) {
			logger.error("Failed to create signal view", ex);
			ErrorsDialog.showImmediateExceptionDialog(this, ex);
			return;
		}
				
		String title = null;
		if( document instanceof MessageSourceResolvable ) {
			title = messageSource.getMessage((MessageSourceResolvable) document);
		} else {
			title = document.toString();
		}
		
		ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
		Icon icon = null;
		if( type != null ) {
			icon = type.getIcon();
		}
		
		document.setDocumentView(documentViewPanel);
		addTab(title, icon, documentViewPanel);
				
	}
		
	public void removeDocumentTab(Document document) {
		
		DocumentView documentView = (DocumentView) document.getDocumentView();
		if( documentView != null ) {
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
		
	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
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
