/* CodecManagerConfigPanel.java created 2007-09-18
 * 
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.context.support.MessageSourceAccessor;

/** CodecManagerConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CodecManagerConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private JList codecList;
	private JScrollPane codeclListScrollPane;
	private JButton registerCodecButton;
	private JButton removeCodecButton;
	private JPanel buttonPanel;

	
	/**
	 * This is the default constructor
	 */
	public CodecManagerConfigPanel(MessageSourceAccessor messageSource) {
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
		
		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());
		
		add(getCodecListScrollPane(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);
		
	}

	public JList getCodecList() {
		if( codecList == null ) {
			// model must be filled in by parent
			codecList = new JList(new Object[0]);
			codecList.setBorder( new LineBorder(Color.LIGHT_GRAY));
			codecList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			codecList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					JList list = (JList) e.getSource();					
					getRemoveCodecButton().getAction().setEnabled(list.getSelectedIndex() >= 0);
					
				}
				
			});
			
		}
		return codecList;
	}
	
	public JScrollPane getCodecListScrollPane() {
		if( codeclListScrollPane == null ) {
			codeclListScrollPane = new JScrollPane(getCodecList(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			CompoundBorder cb = new CompoundBorder(
				new TitledBorder(messageSource.getMessage("preferences.codecs.installedCodecs")),
				new EmptyBorder(3,3,3,3)
			);
			codeclListScrollPane.setBorder(cb);

		}
		return codeclListScrollPane;
	}
	
	public JButton getRegisterCodecButton() {
		if( registerCodecButton == null ) {
			// action must be filled in by parent
			registerCodecButton = new JButton((Action) null);			
		}
		return registerCodecButton;
	}

	public JButton getRemoveCodecButton() {
		if( removeCodecButton == null ) {
			// action must be filled in by parent
			removeCodecButton = new JButton((Action) null);
		}
		return removeCodecButton;
	}

	private JPanel getButtonPanel() {
		if( buttonPanel == null ) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(new EmptyBorder(3,0,0,0));
			buttonPanel.add(Box.createHorizontalGlue());
			buttonPanel.add(getRemoveCodecButton());
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(getRegisterCodecButton());
		}
		return buttonPanel;
	}
		
}
