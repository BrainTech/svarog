package org.signalml.app.view.element;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 */
public class MonitorChannelSelectPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MonitorChannelSelectPanel.class);
	
	private MessageSourceAccessor messageSource;

	private JList channelList;

	/**
	 * This is the default constructor
	 * @param messageSource the source of messages (labels)
	 */
	public MonitorChannelSelectPanel( MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		JLabel label = new JLabel( messageSource.getMessage( "openMonitor.channelsListLabel"));
		setLayout( new BorderLayout());
		add( label, BorderLayout.NORTH);
		add( new JScrollPane( getChannelList()), BorderLayout.CENTER);
	}

	public JList getChannelList() {
		if (channelList == null) {
			channelList = new JList();
			channelList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return channelList;
	}

}
