/* ChannelSpacePanel.java created 2008-01-25
 * 
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.action.util.ListSelectAllAction;
import org.signalml.app.action.util.ListSelectInvertAction;
import org.signalml.app.action.util.ListSelectNoneAction;
import org.signalml.domain.signal.space.ChannelSpace;
import org.signalml.domain.signal.space.ChannelSpaceType;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ChannelSpacePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSpacePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ChannelSpacePanel.class);
	
	private MessageSourceAccessor messageSource;
	
	private JList channelList;
	private JScrollPane channelScrollPane;
	
	private JButton channelSelectAllButton;
	private JButton channelSelectInvertButton;
	private JButton channelSelectNoneButton;
	
	private String[] sourceChannels;
	private String[] channels;

	private SignalSourceLevel currentLevel;
	private String[] currentChannels;
	
	public ChannelSpacePanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {
		
		setLayout(new BorderLayout());
		
		setBorder( new CompoundBorder( 
				new TitledBorder( messageSource.getMessage("signalSpace.channelSpace.title" ) ),
				new EmptyBorder( 3,3,3,3 )
		));

		JPanel channelButtonPanel = new JPanel( new FlowLayout( FlowLayout.TRAILING, 3, 3 ) );
		channelButtonPanel.add( getChannelSelectAllButton() );
		channelButtonPanel.add( getChannelSelectNoneButton() );
		channelButtonPanel.add( getChannelSelectInvertButton() );
		
		add( getChannelScrollPane(), BorderLayout.CENTER );
		add( channelButtonPanel, BorderLayout.SOUTH );

	}
	
	public JList getChannelList() {
		if( channelList == null ) {
			
			channelList = new JList();
			
			channelList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );			
			
		}
		return channelList;
	}

	public JScrollPane getChannelScrollPane() {
		if( channelScrollPane == null ) {
			channelScrollPane = new JScrollPane( getChannelList() );
			channelScrollPane.setPreferredSize( new Dimension(300,200) );			
		}
		return channelScrollPane;
	}
	
	public JButton getChannelSelectAllButton() {
		if( channelSelectAllButton == null ) {						
			channelSelectAllButton = new JButton( new ListSelectAllAction(messageSource, getChannelList()) );
		}
		return channelSelectAllButton;
	}

	public JButton getChannelSelectNoneButton() {
		if( channelSelectNoneButton == null ) {
			channelSelectNoneButton = new JButton( new ListSelectNoneAction(messageSource, getChannelList()) );
		}
		return channelSelectNoneButton;
	}
	
	public JButton getChannelSelectInvertButton() {
		if( channelSelectInvertButton == null ) {			
			channelSelectInvertButton = new JButton( new ListSelectInvertAction(messageSource, getChannelList()) );
		}
		return channelSelectInvertButton;
	}
	
	public void fillPanelFromModel(SignalSpace space) {

		ChannelSpaceType channelSpaceType = space.getChannelSpaceType();
		JList list = getChannelList();
		if( channelSpaceType == ChannelSpaceType.WHOLE_SIGNAL ) {
			list.setSelectionInterval(0, list.getModel().getSize()-1);			
		}
		else {
			
			ChannelSpace channelSpace = space.getChannelSpace();
			list.clearSelection();
			
			if( channels != null && channelSpace != null ) {
				
				for( int i=0; i<channels.length; i++ ) {
					if( channelSpace.isChannelSelected(i) ) {
						list.addSelectionInterval(i, i);
					}
				}
				
			}
						
		}
		
	}

	public void fillModelFromPanel(SignalSpace space) {
		
		boolean all = true;
		
		JList list = getChannelList();
		ChannelSpace channelSpace = space.getChannelSpace();
		if( channelSpace != null ) {
			channelSpace.clear();
		} else {
			channelSpace = new ChannelSpace();
		}
		
		if( channels != null ) {
			
			for( int i=0; i<channels.length; i++ ) {
				
				if( list.isSelectedIndex(i) ) {
					channelSpace.addChannel(i);
				}
				else {
					all = false;
				}
				
			}
			
		}
		
		if( all ) {
			space.setChannelSpaceType(ChannelSpaceType.WHOLE_SIGNAL);
			space.setChannelSpace(null);
		}
		else {
			space.setChannelSpaceType(ChannelSpaceType.SELECTED);
			space.setChannelSpace(channelSpace);
		}
				
	}
	
	public String[] getSourceChannels() {
		return sourceChannels;
	}

	public void setSourceChannels(String[] sourceChannels) {
		if( this.sourceChannels != sourceChannels ) {
			
			this.sourceChannels = sourceChannels;
			
			if( currentLevel == SignalSourceLevel.RAW ) {
				setCurrentChannels(sourceChannels);
			}			
		}
	}

	public String[] getCurrentChannels() {
		return currentChannels;
	}

	public void setCurrentChannels(String[] currentChannels) {
		if( this.currentChannels != currentChannels ) {
			
			this.currentChannels = currentChannels;
			
			DefaultListModel listModel = new DefaultListModel();
			for( int i=0; i<currentChannels.length; i++ ) {
				listModel.addElement( currentChannels[i] );
			}
			
			JList list = getChannelList();
			list.setModel( listModel );
			list.clearSelection();
			
		}
	}

	public String[] getChannels() {
		return channels;
	}
	
	public void setChannels(String[] channels) {
		if( this.channels != channels ) {
			this.channels = channels;
			
			if( currentLevel != SignalSourceLevel.RAW ) {
				setCurrentChannels(channels);
			}
		
		}
	}
	
	public SignalSourceLevel getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(SignalSourceLevel currentLevel) {
		if( this.currentLevel != currentLevel ) {
			
			this.currentLevel = currentLevel;
			
			if( currentLevel == SignalSourceLevel.RAW ) {
				setCurrentChannels( sourceChannels );
			} else {
				setCurrentChannels(channels);
			}
			
		}
	}

	public void setConstraints( SignalSpaceConstraints constraints ) {
		
		setSourceChannels( constraints.getSourceChannels() );
		setChannels( constraints.getChannels() );
		
	}
	
	public void validatePanel(Errors errors) {

		if( getChannelList().isSelectionEmpty() ) {
			errors.rejectValue( "channelSpace", "error.signalSpace.noChannels");
		}
		
	}
	
}
