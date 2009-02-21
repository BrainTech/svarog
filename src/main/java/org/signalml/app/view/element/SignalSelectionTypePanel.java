/* SignalSelectionTypePanel.java created 2007-10-04
 * 
 */
package org.signalml.app.view.element;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.springframework.context.support.MessageSourceAccessor;

/** SignalSelectionTypePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelectionTypePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private JRadioButton pageRadio = null;
	private JRadioButton blockRadio = null;
	private JRadioButton channelRadio = null;
	
	private ButtonGroup radioGroup;
	
	/**
	 * This is the default constructor
	 */
	public SignalSelectionTypePanel(MessageSourceAccessor messageSource) {
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
		
		setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("signalSelection.selectionType")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		radioGroup = new ButtonGroup();
		
		add(getPageRadio());
		add(getBlockRadio());
		add(getChannelRadio());
		
		getPageRadio().setSelected(true);
		
	}

	public JRadioButton getPageRadio() {
		if (pageRadio == null) {
			pageRadio = new JRadioButton();
			pageRadio.setText(messageSource.getMessage("signalSelection.pageSelection"));
			pageRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(pageRadio);
		}
		return pageRadio;
	}

	public JRadioButton getBlockRadio() {
		if (blockRadio == null) {
			blockRadio = new JRadioButton();
			blockRadio.setText(messageSource.getMessage("signalSelection.blockSelection"));
			blockRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(blockRadio);
		}
		return blockRadio;
	}

	public JRadioButton getChannelRadio() {
		if (channelRadio == null) {
			channelRadio = new JRadioButton();
			channelRadio.setText(messageSource.getMessage("signalSelection.channelSelection"));
			channelRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(channelRadio);
		}
		return channelRadio;
	}
	
}
