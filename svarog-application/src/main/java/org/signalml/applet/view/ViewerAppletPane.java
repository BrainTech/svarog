/* ViewerAppletPane.java created 2008-02-20
 * 
 */

package org.signalml.applet.view;

import java.awt.BorderLayout;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.util.SnapToPageRunnable;
import org.signalml.app.view.DocumentView;
import org.signalml.app.view.View;
import org.signalml.app.view.ViewerDocumentTabbedPane;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.applet.SignalMLApplet;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerAppletPane
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerAppletPane extends JPanel implements View, DocumentManagerListener {

	private static final long serialVersionUID = 1L;
	
	private SignalMLApplet applet;
	
	private ViewerElementManager elementManager;

	private MessageSourceAccessor messageSource;
	
	private boolean viewMode = false;
	private boolean mainToolBarVisible = true;
	private boolean statusBarVisible = true;
	
	public ViewerAppletPane(SignalMLApplet applet) {
		super();
		this.applet = applet;
	}

	public void initialize() {
		
		setLayout(new BorderLayout());
		
		elementManager.setView(this);
		elementManager.setOptionPaneParent(applet.getRootPane());
		elementManager.setDialogParent(null);
		
		add(elementManager.getMainToolBar(), BorderLayout.NORTH);
		
		add(elementManager.getStatusBar(), BorderLayout.SOUTH);
		
		add(elementManager.getRightPane(), BorderLayout.CENTER);
		
		elementManager.configureAcceletators();
		
		ActionFocusManager actionFocusManager = elementManager.getActionFocusManager();
		ViewerDocumentTabbedPane documentTabbedPane = elementManager.getDocumentTabbedPane();
		DocumentManager documentManager = elementManager.getDocumentManager();

		documentManager.addDocumentManagerListener(documentTabbedPane);		
		documentManager.addDocumentManagerListener(this);
		
		actionFocusManager.addActionFocusListener(documentTabbedPane);		
		documentTabbedPane.addChangeListener(actionFocusManager);		
		
	}
	
	@Override
	public void closeView() {
		// ignored
	}

	@Override
	public DocumentView createDocumentViewPanel(Document document) throws SignalMLException {
		return elementManager.createDocumentViewPanel(document);
	}

	@Override
	public boolean isBottomPanelVisible() {
		return false;
	}

	@Override
	public boolean isLeftPanelVisible() {
		return false;
	}

	@Override
	public boolean isMainToolBarVisible() {
		return mainToolBarVisible;
	}

	@Override
	public boolean isStatusBarVisible() {
		return statusBarVisible;
	}

	@Override
	public boolean isViewMode() {
		return viewMode;
	}

	@Override
	public void setBottomPanelVisible(boolean visible) {
		// ignored
	}

	@Override
	public void setLeftPanelVisible(boolean visible) {
		// ignored
	}

	@Override
	public void setMainToolBarVisible(boolean visible) {
		if( this.mainToolBarVisible != visible ) {
			this.mainToolBarVisible = visible;
			elementManager.getMainToolBar().setVisible(visible);
			elementManager.getShowMainToolBarAction().putValue( AbstractAction.SELECTED_KEY, visible );
		}		
	}

	@Override
	public void setStatus(String status) {
		elementManager.getStatusBar().setStatus(status);
	}

	@Override
	public void setStatusBarVisible(boolean visible) {
		if( this.statusBarVisible != visible ) {
			this.statusBarVisible = visible;
			elementManager.getStatusBar().setVisible(visible);
			elementManager.getShowStatusBarAction().putValue( AbstractAction.SELECTED_KEY, visible );
		}
	}

	@Override
	public void setViewMode(boolean viewMode) {
		if( this.viewMode != viewMode ) {
			this.viewMode = viewMode;
			elementManager.getViewModeAction().putValue(AbstractAction.SELECTED_KEY, viewMode);			
			ApplicationConfiguration applicationConfig = elementManager.getApplicationConfig();
			if( applicationConfig.isViewModeHidesMainToolBar() ) {
				setMainToolBarVisible(!viewMode);
			}
			if( applicationConfig.isViewModeCompactsPageTagBars() || applicationConfig.isViewModeSnapsToPage() ) {
				
				DocumentManager documentManager = elementManager.getDocumentManager();
				synchronized( documentManager ) {
					int cnt = documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
					SignalDocument signalDocument;
					SignalView signalView;
					
					for( int i=0; i<cnt; i++ ) {
						signalDocument = (SignalDocument) documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, i);
						signalView = (SignalView) signalDocument.getDocumentView();
						if( applicationConfig.isViewModeCompactsPageTagBars() ) {
							for( SignalPlot plot : signalView.getPlots() ) {
								plot.getSignalPlotColumnHeader().setCompact(viewMode);
							}
						}
						if( applicationConfig.isViewModeSnapsToPage() ) {
							SwingUtilities.invokeLater( new SnapToPageRunnable(signalView, viewMode) );
						}
					}
				}
				
			}			
		}
	}
	
	@Override
	public void documentAdded(DocumentManagerEvent e) {
		elementManager.getSaveAllDocumentsAction().setEnabledAsNeeded();
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {
		// this is not interesting
	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		elementManager.getSaveAllDocumentsAction().setEnabledAsNeeded();
	}
	
	public ViewerElementManager getElementManager() {
		return elementManager;
	}

	public void setElementManager(ViewerElementManager elementManager) {
		this.elementManager = elementManager;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}
	
}
