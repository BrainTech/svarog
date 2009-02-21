/* SignalSelectionPanel.java created 2008-01-18
 * 
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.BlockSelectionModelProvider;
import org.signalml.app.model.ChannelSelectionModelProvider;
import org.signalml.app.model.PageSelectionModelProvider;
import org.signalml.app.util.SwingUtils;
import org.signalml.domain.signal.BoundedSignalSelection;
import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.exception.SanityCheckException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** SignalSelectionPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelectionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalSelectionPanel.class);
	
	private MessageSourceAccessor messageSource;

	private CardLayout cardLayout;
	private JPanel cardPanel;
	
	private SignalSelectionTypePanel signalSelectionTypePanel;
	
	private PageSignalSelectionPanel pageSignalSelectionPanel;
	private BlockSignalSelectionPanel blockSignalSelectionPanel;
	private ChannelSignalSelectionPanel channelSignalSelectionPanel;
	
	private SignalSpaceConstraints currentConstraints;

	private BoundedSignalSelection currentBss;
	
	private boolean withChannelSelection;
	
	public SignalSelectionPanel(MessageSourceAccessor messageSource, boolean withChannelSelection) {
		super();
		this.messageSource = messageSource;
		this.withChannelSelection = withChannelSelection;
		initialize();
	}

	private void initialize() {
		
		setLayout(new BorderLayout());
		
		signalSelectionTypePanel = new SignalSelectionTypePanel(messageSource);
		add(signalSelectionTypePanel, BorderLayout.NORTH);
		
		pageSignalSelectionPanel = new PageSignalSelectionPanel(messageSource);
		blockSignalSelectionPanel = new BlockSignalSelectionPanel(messageSource);
		channelSignalSelectionPanel = new ChannelSignalSelectionPanel(messageSource, withChannelSelection);
		
		cardLayout = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.setBorder(new TitledBorder( messageSource.getMessage("signalSelection.selectionParameters") ));
		
		cardPanel.add(pageSignalSelectionPanel, "page");
		cardPanel.add(blockSignalSelectionPanel, "block");
		cardPanel.add(channelSignalSelectionPanel, "channel");

		signalSelectionTypePanel.getPageRadio().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanel, "page");
			}
		});
		
		signalSelectionTypePanel.getBlockRadio().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanel, "block");
			}			
		});

		signalSelectionTypePanel.getChannelRadio().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanel, "channel");
			}			
		});
		
		add(cardPanel, BorderLayout.CENTER);
		
	}
	
	public void fillPanelFromModel(BoundedSignalSelection bss) {
				
		JSpinner startPageSpinner;
		JSpinner startBlockSpinner;
		JSpinner lengthSpinner;
		
		SignalSelection selection = bss.getSelection();
		SignalSelectionType type = null;
		if( selection != null ) {
			type = selection.getType();
		}
		PageSelectionModelProvider pageSelectionModelProvider;
		if( type != null && type.isPage() ) {
			pageSelectionModelProvider = new PageSelectionModelProvider(
					bss.getMaxPage(),
					selection.getStartSegment(bss.getPageSize())+1,
					selection.getSegmentLength(bss.getPageSize())
			);
			signalSelectionTypePanel.getPageRadio().setSelected(true);			
			cardLayout.show(cardPanel, "page");
		} else {
			pageSelectionModelProvider = new PageSelectionModelProvider(
					bss.getMaxPage(),
					1,
					1
			);
		}
				
		startPageSpinner = pageSignalSelectionPanel.getStartPageSpinner();
		lengthSpinner = pageSignalSelectionPanel.getLengthSpinner(); 

		SwingUtils.replaceSpinnerModel( startPageSpinner, pageSelectionModelProvider.getStartPageSpinnerModel());
		startPageSpinner.setEditor(new SpinnerNumberEditor(startPageSpinner));
		
		SwingUtils.replaceSpinnerModel( lengthSpinner, pageSelectionModelProvider.getLengthSpinnerModel());
		lengthSpinner.setEditor(new SpinnerNumberEditor(lengthSpinner));
		
		BlockSelectionModelProvider blockSelectionModelProvider;
		if( type != null && type.isBlock() ) {
			float blockSize = ((float) bss.getPageSize()) / bss.getBlocksPerPage();
			int startSegment = selection.getStartSegment(blockSize);
			blockSelectionModelProvider = new BlockSelectionModelProvider(
					bss.getMaxPage(), 
					bss.getMaxBlock(), 
					bss.getBlocksPerPage(), 
					(startSegment/bss.getBlocksPerPage())+1, 
					(startSegment%bss.getBlocksPerPage())+1, 
					selection.getSegmentLength(blockSize) 
			);
			signalSelectionTypePanel.getBlockRadio().setSelected(true);
			cardLayout.show(cardPanel, "block");		
		} else {
			blockSelectionModelProvider = new BlockSelectionModelProvider(
					bss.getMaxPage(), 
					bss.getMaxBlock(), 
					bss.getBlocksPerPage(), 
					1,
					1,
					1				
			);
		}
		
		startPageSpinner = blockSignalSelectionPanel.getStartPageSpinner();
		startBlockSpinner = blockSignalSelectionPanel.getStartBlockSpinner();
		lengthSpinner = blockSignalSelectionPanel.getLengthSpinner();
		
		SwingUtils.replaceSpinnerModel(startPageSpinner, blockSelectionModelProvider.getStartPageSpinnerModel());
		startPageSpinner.setEditor(new SpinnerNumberEditor(startPageSpinner));		

		SwingUtils.replaceSpinnerModel(startBlockSpinner, blockSelectionModelProvider.getStartBlockSpinnerModel());
		startBlockSpinner.setEditor(new SpinnerNumberEditor(startBlockSpinner));		

		SwingUtils.replaceSpinnerModel(lengthSpinner, blockSelectionModelProvider.getLengthSpinnerModel());
		lengthSpinner.setEditor(new SpinnerNumberEditor(lengthSpinner));

		ChannelSelectionModelProvider channelSelectionModelProvider;
		if( type != null && type.isChannel() ) {
			channelSelectionModelProvider = new ChannelSelectionModelProvider(
					bss.getMaxTime(),
					bss.getSamplingFrequency(),
					bss.getChannels(),
					selection.getPosition(),
					selection.getLength(),
					selection.getChannel()
			);
			signalSelectionTypePanel.getChannelRadio().setSelected(true);
			cardLayout.show(cardPanel, "channel");
		} else {
			channelSelectionModelProvider = new ChannelSelectionModelProvider(
					bss.getMaxTime(),
					bss.getSamplingFrequency(),
					bss.getChannels(),
					0,
					1,
					0 //select first channel
			);
		}
		
		JSpinner startTimeSpinner = channelSignalSelectionPanel.getStartTimeSpinner();
		lengthSpinner = channelSignalSelectionPanel.getLengthSpinner();
		
		SwingUtils.replaceSpinnerModel(startTimeSpinner, channelSelectionModelProvider.getStartTimeSpinnerModel());
		startTimeSpinner.setEditor(new SpinnerNumberEditor(startTimeSpinner));

		SwingUtils.replaceSpinnerModel(lengthSpinner, channelSelectionModelProvider.getLengthSpinnerModel());
		lengthSpinner.setEditor(new SpinnerNumberEditor(lengthSpinner));

		if( withChannelSelection ) {
			JComboBox channelComboBox = channelSignalSelectionPanel.getChannelComboBox();
			channelComboBox.setModel(channelSelectionModelProvider.getChannelComboBoxModel());
		}
		
		if( type == null ) {
			signalSelectionTypePanel.getPageRadio().setSelected(true);
			cardLayout.show(cardPanel, "page");
		}
		
	}

	public void fillModelFromPanel(BoundedSignalSelection bss) {
				
		SignalSelection selection = null;

		if( signalSelectionTypePanel.getPageRadio().isSelected() ) {

			int startPage = (Integer) pageSignalSelectionPanel.getStartPageSpinner().getValue();
			int length = (Integer) pageSignalSelectionPanel.getLengthSpinner().getValue();

			selection = new SignalSelection(SignalSelectionType.PAGE);
			selection.setPosition((startPage-1)*bss.getPageSize());
			selection.setLength(length*bss.getPageSize());
			selection.setChannel(SignalSelection.CHANNEL_NULL);
			
		} else if( signalSelectionTypePanel.getBlockRadio().isSelected() ) {
			
			int startPage = (Integer) blockSignalSelectionPanel.getStartPageSpinner().getValue();
			int startBlock = (Integer) blockSignalSelectionPanel.getStartBlockSpinner().getValue();
			int length = (Integer) blockSignalSelectionPanel.getLengthSpinner().getValue();
			float blockSize = ((float) bss.getPageSize()) / bss.getBlocksPerPage();
			
			selection = new SignalSelection(SignalSelectionType.BLOCK);
			selection.setPosition((startPage-1)*bss.getPageSize()+(startBlock-1)*blockSize);
			selection.setLength(length*blockSize);
			selection.setChannel(SignalSelection.CHANNEL_NULL);
			
		} else if( signalSelectionTypePanel.getChannelRadio().isSelected() ) {
			
			float startTime = (Float) channelSignalSelectionPanel.getStartTimeSpinner().getValue();
			float length = (Float) channelSignalSelectionPanel.getLengthSpinner().getValue();
			
			selection = new SignalSelection(SignalSelectionType.CHANNEL);
			selection.setPosition(startTime);
			selection.setLength(length);

			if( withChannelSelection ) {
				int channel = channelSignalSelectionPanel.getChannelComboBox().getSelectedIndex();
				selection.setChannel(channel);
			}
				
		} else {
			logger.error("Unexpected situation - nothing selected");
			throw new SanityCheckException();
		}
		
		bss.setSelection(selection);
		
	}

	public void fillPanelFromModel( SignalSpace space ) {

		BoundedSignalSelection bss = new BoundedSignalSelection(space.getSelectionTimeSpace());
		
		bss.setMaxTime( currentConstraints.getTimeSignalLength() );
		bss.setChannels( currentConstraints.getChannels() );
		
		bss.setPageSize( currentConstraints.getPageSize() );
		bss.setBlocksPerPage( currentConstraints.getBlocksPerPage() );
		
		bss.setMaxPage( currentConstraints.getMaxPage() );
		bss.setMaxBlock( currentConstraints.getMaxBlock() );
		
		bss.setSamplingFrequency( currentConstraints.getSamplingFrequency() );
		
		currentBss = bss;
		
		fillPanelFromModel(bss);
		
	}
	
	public void fillModelFromPanel( SignalSpace space ) {
		
		fillModelFromPanel(currentBss);
		
		space.setSelectionTimeSpace(currentBss.getSelection());
		
	}
	
	public void setConstraints( SignalSpaceConstraints constraints ) {		
		currentConstraints = constraints;		
	}
	
	public void validatePanel(Errors errors) {
		// there is no validation - the dialog elements enforce valid values themselves		
	}
	
}
