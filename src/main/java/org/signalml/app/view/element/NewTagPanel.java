/* NewTagPanel.java created 2007-10-14
 * 
 */
package org.signalml.app.view.element;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import org.signalml.app.document.ManagedDocumentType;
import org.springframework.context.support.MessageSourceAccessor;

/** NewTagPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewTagPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private JRadioButton emptyRadio = null;
	private JRadioButton defaultSleepRadio = null;
	private JRadioButton fromFileRadio = null;
	
	private ButtonGroup radioGroup;
	private EmbeddedFileChooser fileChooser = null;
	
	/**
	 * This is the default constructor
	 */
	public NewTagPanel(MessageSourceAccessor messageSource) {
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
		
		setBorder(BorderFactory.createTitledBorder(messageSource.getMessage("newTag.title")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		radioGroup = new ButtonGroup();
		
		add(getEmptyRadio());
		add(getDefaultSleepRadio());
		add(getFromFileRadio());
		
		getDefaultSleepRadio().setSelected(true);
		
		getFileChooser().setVisible(false);					
		add(getFileChooser());

	}

	public JRadioButton getEmptyRadio() {
		if (emptyRadio == null) {
			emptyRadio = new JRadioButton();
			emptyRadio.setText(messageSource.getMessage("newTag.emptyRadio"));
			emptyRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(emptyRadio);
		}
		return emptyRadio;
	}
	
	public JRadioButton getDefaultSleepRadio() {
		if (defaultSleepRadio == null) {
			defaultSleepRadio = new JRadioButton();
			defaultSleepRadio.setText(messageSource.getMessage("newTag.defaultSleepRadio"));
			defaultSleepRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(defaultSleepRadio);
		}
		return defaultSleepRadio;
	}

	public JRadioButton getFromFileRadio() {
		if (fromFileRadio == null) {
			fromFileRadio = new JRadioButton();
			fromFileRadio.setText(messageSource.getMessage("newTag.fromFileRadio"));
			fromFileRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(fromFileRadio);
			fromFileRadio.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					getFileChooser().setVisible(e.getStateChange() == ItemEvent.SELECTED);
					
					NewTagPanel.this.revalidate();
					Dimension d = NewTagPanel.this.getTopLevelAncestor().getPreferredSize();
					NewTagPanel.this.getTopLevelAncestor().setSize(d);
					NewTagPanel.this.repaint();
				}
				
			});
		}
		return fromFileRadio;
	}

	public EmbeddedFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new EmbeddedFileChooser();
			fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
			fileChooser.setFileHidingEnabled(false);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.resetChoosableFileFilters();
			fileChooser.setAlignmentX(Component.LEFT_ALIGNMENT);			
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.resetChoosableFileFilters();
			FileFilter[] filters = ManagedDocumentType.TAG.getFileFilters(messageSource);
			for( FileFilter f : filters ) {
				fileChooser.addChoosableFileFilter(f);
			}
			fileChooser.setPreferredSize(new Dimension(500,350));
			
			fileChooser.setInvokeDefaultButtonOnApprove(true);
			
		}
		return fileChooser;
	}
	
}
