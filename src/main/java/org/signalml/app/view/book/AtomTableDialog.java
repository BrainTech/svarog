/* AtomTableDialog.java created 2008-03-01
 * 
 */

package org.signalml.app.view.book;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.CompoundBorder;

import org.signalml.app.document.BookDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.domain.book.BookFilterProcessor;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** AtomTableDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AtomTableDialog extends AbstractDialog implements PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;

	private AtomTableModel atomTableModel;
	private AtomTable atomTable;
	private JScrollPane atomTableScrollPane;
		
	private JToggleButton filterSwitchButton;
	
	private JButton previousSegmentButton;
	private JButton nextSegmentButton;
	private JButton previousChannelButton;
	private JButton nextChannelButton;
	
	private SegmentTextField segmentTextField;
	private ChannelTextField channelTextField;
	
	private JToolBar toolBar;
	
	private BookView currentView;
	
	public AtomTableDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public AtomTableDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle( messageSource.getMessage("atomTable.title") );
		setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/atomtable.png"));
		super.initialize();
		setResizable(true);
	}
	
	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public JComponent createInterface() {
		
		JPanel interfacePanel = new JPanel( new BorderLayout() );
		
		JPanel tablePanel = new JPanel( new BorderLayout() );
		
		CompoundBorder border = new CompoundBorder( 
				new TitledBorder( messageSource.getMessage("atomTable.tableTitle") ),
				new EmptyBorder( 3,3,3,3 )
		);
		tablePanel.setBorder(border);
		
		tablePanel.add( getAtomTableScrollPane(), BorderLayout.CENTER );
		
		interfacePanel.add( getToolBar(), BorderLayout.NORTH );
		interfacePanel.add( tablePanel, BorderLayout.CENTER );
				
		return interfacePanel;
		
	}
	
	public AtomTableModel getAtomTableModel() {
		if( atomTableModel == null ) {
			atomTableModel = new AtomTableModel(messageSource);
		}
		return atomTableModel;
	}
	
	public AtomTable getAtomTable() {
		if( atomTable == null ) {
			atomTable = new AtomTable( getAtomTableModel(), messageSource );
		}
		return atomTable;
	}
	
	public JScrollPane getAtomTableScrollPane() {
		if( atomTableScrollPane == null ) {
			atomTableScrollPane = new JScrollPane( getAtomTable() );
			atomTableScrollPane.setPreferredSize( new Dimension(800,600) );
		}
		return atomTableScrollPane;
	}
	
	public JToolBar getToolBar() {
		if( toolBar == null ) {
			toolBar = new JToolBar( JToolBar.HORIZONTAL );
			toolBar.setFloatable(false);
			
			toolBar.add( getPreviousSegmentButton() );
			toolBar.addSeparator(new Dimension(2,2));
			toolBar.add( getSegmentTextField() );
			toolBar.addSeparator(new Dimension(2,2));
			toolBar.add( getNextSegmentButton() );
			
			toolBar.addSeparator();
			
			toolBar.add( getPreviousChannelButton() );
			toolBar.addSeparator(new Dimension(2,2));
			toolBar.add( getChannelTextField() );
			toolBar.addSeparator(new Dimension(2,2));
			toolBar.add( getNextChannelButton() );
			
			toolBar.addSeparator();
			
			toolBar.add( Box.createHorizontalGlue() );
			toolBar.add( getFilterSwitchButton() );
		}
		return toolBar;
	}
	
	public JToggleButton getFilterSwitchButton() {
		if( filterSwitchButton == null ) {
			filterSwitchButton = new JToggleButton();
			filterSwitchButton.setHideActionText(true);
			filterSwitchButton.setSelectedIcon( IconUtils.loadClassPathIcon("org/signalml/app/icon/filteron.png") );
		}
		return filterSwitchButton;
	}
	
	public JButton getPreviousSegmentButton() {
		if( previousSegmentButton == null ) {
			previousSegmentButton = new JButton();
			previousSegmentButton.setHideActionText(true);
		}
		return previousSegmentButton;
	}

	public JButton getNextSegmentButton() {
		if( nextSegmentButton == null ) {
			nextSegmentButton = new JButton();
			nextSegmentButton.setHideActionText(true);
		}
		return nextSegmentButton;
	}
	
	public JButton getPreviousChannelButton() {
		if( previousChannelButton == null ) {
			previousChannelButton = new JButton();
			previousChannelButton.setHideActionText(true);
		}
		return previousChannelButton;
	}

	public JButton getNextChannelButton() {
		if( nextChannelButton == null ) {
			nextChannelButton = new JButton();
			nextChannelButton.setHideActionText(true);
		}
		return nextChannelButton;
	}

	public SegmentTextField getSegmentTextField() {
		if( segmentTextField == null ) {
			segmentTextField = new SegmentTextField();
			
			segmentTextField.setPreferredSize( new Dimension( 100, 25 ) );
		}
		return segmentTextField;
	}
	
	public ChannelTextField getChannelTextField() {
		if( channelTextField == null ) {
			channelTextField = new ChannelTextField();
			
			channelTextField.setPreferredSize( new Dimension(100,25) );
		}
		return channelTextField;
	}
	
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		setCurrentView( (BookView) model );
				
	}
	
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	protected BookView getCurrentView() {
		return currentView;
	}

	protected void setCurrentView(BookView currentView) {
		if( this.currentView != currentView ) {
			if( this.currentView != null ) {
				this.currentView.removePropertyChangeListener(this);
				this.currentView.getDocument().removePropertyChangeListener(this);
			}
		}
		this.currentView = currentView;
		if( currentView != null ) {			
			
			getFilterSwitchButton().setAction( currentView.getFilterSwitchAction() );
			getPreviousSegmentButton().setAction( currentView.getPreviousSegmentAction() );
			getNextSegmentButton().setAction( currentView.getNextSegmentAction() );
			getPreviousChannelButton().setAction( currentView.getPreviousChannelAction() );
			getNextChannelButton().setAction( currentView.getNextChannelAction() );
			getSegmentTextField().setBookView(currentView);
			getChannelTextField().setBookView(currentView);
			
			refreshAfterChange();
			
			currentView.getDocument().addPropertyChangeListener(this);
			currentView.addPropertyChangeListener(this);
			
		} else {
			
			getFilterSwitchButton().setAction(null);
			getPreviousSegmentButton().setAction( null );
			getNextSegmentButton().setAction( null );
			getPreviousChannelButton().setAction( null );
			getNextChannelButton().setAction( null );
			getSegmentTextField().setBookView(null);
			getChannelTextField().setBookView(null);
			
			AtomTableModel atomModel = getAtomTableModel();
			atomModel.setSegment( null );
			atomModel.setReconstruction( null );
			
		}
	}
	
	protected void refreshAfterChange() {

		BookFilterProcessor filter = currentView.getFilter();
		
		int currentSegment = currentView.getCurrentSegment();
		int currentChannel = currentView.getCurrentChannel();
		
		StandardBookSegment segment = filter.getSegmentAt(currentSegment, currentChannel);
		
		AtomTableModel atomModel = getAtomTableModel();
		atomModel.setSegment( segment );
		atomModel.setReconstruction( currentView.getPlot().getReconstructionProvider() );		
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if( currentView == null ) {
			return;
		}
		
		Object source = evt.getSource();
		String propertyName = evt.getPropertyName();		
		
		if( source == currentView.getDocument() ) {
			
			if( BookDocument.FILTER_CHAIN_PROPERTY.equals( propertyName ) ) {
				refreshAfterChange();
			}
			
		}
		else if( source == currentView ) {
			
			if( BookView.CURRENT_SEGMENT_PROPERTY.equals( propertyName ) ) {
				refreshAfterChange();
			}
			else if( BookView.CURRENT_CHANNEL_PROPERTY.equals( propertyName ) ) {
				refreshAfterChange();
			}
			
		}
		
	}

	@Override
	protected void onDialogClose() {
		super.onDialogClose();
		setCurrentView(null);
	}
	
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BookView.class.isAssignableFrom(clazz);
	}
	
}
