/* FileOpenMethodPanel.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.view.element.ResolvableComboBox;

/**
 * A panel for selecting the file open method (RAW or using SignalML codec).
 *
 * @author Piotr Szachewicz
 */
public class FileOpenMethodPanel extends JPanel {

	/**
	 * Property telling whether the fileOpenMethod has changed.
	 */
	public static String FILE_OPEN_METHOD_PROPERTY_CHANGED = "fileOpenMethodPropertyChanged";

	/**
	 * The combo-box which allows to select the method using which the signal
	 * document will be opened (raw or signalML).
	 */
	private ResolvableComboBox methodComboBox;

	/**
	 * Constructor.
	 */
	public FileOpenMethodPanel() {
		createInterface();
	}

	/**
	 * Creates the GUI for this panel.
	 */
	private void createInterface() {
			CompoundBorder cb = new CompoundBorder(
			        new TitledBorder(_("Choose signal loading method")),
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
			methodComboBox = new ResolvableComboBox();

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
		}
		return methodComboBox;
	}

	/**
	 * Returns the selected method for opening the signal file.
	 * @return the selected method for opening the signal file
	 */
	public FileOpenSignalMethod getSelectedOpenSignalMethod() {
		return (FileOpenSignalMethod) methodComboBox.getSelectedItem();
	}

	/**
	 * Sets the open file method to be selected.
	 * @param method the method to be selected in this panel
	 */
	public void setSelectedOpenSignalMethod(FileOpenSignalMethod method) {
		methodComboBox.setSelectedItem(method);
	}

	/**
	 * Fires the propertyChange telling that the fileOpenMethod has changed.
	 */
	protected void fireFileOpenMethodPropertyChanged() {
		firePropertyChange(FILE_OPEN_METHOD_PROPERTY_CHANGED, null, methodComboBox.getSelectedItem());
	}

}
