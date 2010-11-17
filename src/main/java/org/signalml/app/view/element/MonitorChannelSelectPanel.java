package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.util.logging.Level;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.OpenMonitorDescriptor;
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
	 */
	public MonitorChannelSelectPanel( MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setLayout( new BorderLayout());
		add( new JScrollPane( getChannelList()), BorderLayout.CENTER);

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("openMonitor.channelSelectPanelTitle")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);
	}

	/**
	 * Returns the list of channels which were selected using this panel.
	 * @return the list of selected channels
	 */
	public JList getChannelList() {
		if (channelList == null) {
			channelList = new JList();
			channelList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return channelList;
	}

	/**
	 * Fills the fields of this panel from the given model.
	 * @param openMonitorDescriptor the model from which this dialog will be
	 * filled.
	 */
	public void fillPanelFromModel(OpenMonitorDescriptor openMonitorDescriptor) {

		String[] channelLabels = openMonitorDescriptor.getChannelLabels();

		if (channelLabels == null) {
			Integer channelCount = openMonitorDescriptor.getChannelCount();
			if (channelCount == null)
				channelCount = 0;
			channelLabels = new String[channelCount];
			for (int i=0; i<channelCount; i++)
				channelLabels[i] = Integer.toBinaryString( i);
		}

		getChannelList().setListData(channelLabels);

	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param openMonitorDescriptor the model to be filled.
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor openMonitorDescriptor) {
		try {
			openMonitorDescriptor.setSelectedChannelList(getChannelList().getSelectedValues());
		} catch (Exception ex) {
			java.util.logging.Logger.getLogger(MonitorChannelSelectPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
