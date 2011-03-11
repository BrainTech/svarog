/* FileOpenMethodPanel.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class FileOpenMethodPanel extends JPanel {

	protected MessageSourceAccessor messageSource;

	/**
	 * the combo-box which allows to select the method using which the signal
	 * document will be opened (raw or signalML)
	 */
	private JComboBox methodComboBox;

	public FileOpenMethodPanel(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
		createInterface();
	}

	private void createInterface() {
			CompoundBorder cb = new CompoundBorder(
			        new TitledBorder(messageSource.getMessage("openSignal.options.methodTitle")),
			        new EmptyBorder(3,3,3,3)
			);
			setBorder(cb);
			add(getMethodComboBox(),BorderLayout.CENTER);
	}


	/**
	 * Returns the combo-box allows to select the method which will be used
	 * to open a document with the signal (SignalML or RAW).
	 * If the combo-box doesn't exist it is created and a listener is added
	 * to it.
	 * The listener changes the active card in {@link #getOptionsPanel()
	 * options panel} when the selected method changes.
	 * @return the combo-box allows to select the method which will be used
	 * to open a document with the signal
	 */
	public JComboBox getMethodComboBox() {
		if (methodComboBox == null) {
			methodComboBox = new JComboBox();
			methodComboBox.addItem(messageSource.getMessage("openSignal.options.methodSignalML"));
			methodComboBox.addItem(messageSource.getMessage("openSignal.options.methodRaw"));

			/*methodComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {

						int index = methodComboBox.getSelectedIndex();
						switch (index) {

						case 0 :
							getCardLayout().show(getOptionsPanel(), "signalml");
							break;

						case 1 :
							getCardLayout().show(getOptionsPanel(), "raw");
							break;

						default :
							throw new SanityCheckException("Bad setting [" + index + "]");

						}

					}
				}

			});*/
		}
		return methodComboBox;
	}
}
