/* FileOpenMethodPanel.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.view.element.ResolvableComboBox;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class FileOpenMethodPanel extends JPanel {

	public static String FILE_OPEN_METHOD_PROPERTY_CHANGED = "fileOpenMethodPropertyChanged";

	protected MessageSourceAccessor messageSource;

	/**
	 * the combo-box which allows to select the method using which the signal
	 * document will be opened (raw or signalML)
	 */
	private ResolvableComboBox methodComboBox;

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
	public ResolvableComboBox getMethodComboBox() {
		if (methodComboBox == null) {
			methodComboBox = new ResolvableComboBox(messageSource);

			FileOpenSignalMethod[] allAvailableMethods = FileOpenSignalMethod.values();
			DefaultComboBoxModel model = new DefaultComboBoxModel(allAvailableMethods);
			methodComboBox.setModel(model);

			methodComboBox.addItemListener(new ItemListener() {

				private int previouslySelectedIndex = -1;
				@Override
				public void itemStateChanged(ItemEvent e) {
					int currentlySelectedIndex = methodComboBox.getSelectedIndex();
					if (currentlySelectedIndex != previouslySelectedIndex) {
						fireFileOpenMethodPropertyChanged();
						previouslySelectedIndex = currentlySelectedIndex;
					}
				}
			});

			//methodComboBox.addItem(messageSource.getMessage("openSignal.options.methodSignalML"));
			//methodComboBox.addItem(messageSource.getMessage("openSignal.options.methodRaw"));

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

	public FileOpenSignalMethod getSelectedOpenSignalMethod() {
		return (FileOpenSignalMethod) methodComboBox.getSelectedItem();
	}

	public void setSelectedOpenSignalMethod(FileOpenSignalMethod method) {
		methodComboBox.setSelectedItem(method);
	}

	protected void fireFileOpenMethodPropertyChanged() {
		firePropertyChange(FILE_OPEN_METHOD_PROPERTY_CHANGED, null, methodComboBox.getSelectedItem());
	}
}
