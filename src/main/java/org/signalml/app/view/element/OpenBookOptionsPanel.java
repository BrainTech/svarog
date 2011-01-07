/* OpenBookOptionsPanel.java created 2008-02-23
 *
 */

package org.signalml.app.view.element;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.document.BookDocument;
import org.springframework.context.support.MessageSourceAccessor;


/**
 * Panel with options for opening a {@link BookDocument}.
 * For a time being there are no options for that, so this panel is empty. 
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenBookOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * Constructor. Sets the {@link MessageSourceAccessor message source} and
	 * initializes this panel.
	 * @param messageSource the source of messages (labels)
	 */
	public OpenBookOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * Initializes this panel with a group layout and empty vertical and
	 * horizontal groups.
	 * For a time being there is no elements of this panel.
	 */
	private void initialize() {

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("openBook.options.title")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();


		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();


		layout.setVerticalGroup(vGroup);

	}

}
