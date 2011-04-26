/**
 * 
 */
package org.signalml.plugin.exampleplugin;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractDialog;

/**
 * The dialog that allows user to input path to the book file.
 * As an initial path the path to the profile directory is set.
 * 
 * @author Marcin Szumski
 */
public class OpenBookDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;
	
	/**
	 * the field in which the user inputs the location of the signal
	 */
	private JTextField bookLocation;
	
	/**
	 * the {@link SvarogAccessSignal access} to signal options 
	 */
	private SvarogAccessSignal signalAccess;
	
	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access}.
	 * @param signalAccess access to set
	 */
	public OpenBookDialog(SvarogAccessSignal signalAccess) {
		super();
		this.signalAccess = signalAccess;
	}

	/**
	 * Creates the panel with the text field to which
	 * the user inputs the path to the file with the book.
	 */
	@Override
	protected JComponent createInterface() {
		JPanel panel = new JPanel();
		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder("The location of the book file"),
		        null
		);
		panel.setBorder(cb);
		
		bookLocation = new JTextField();
		panel.add(bookLocation);
		return panel;
	}

	/**
	 * The type of the model must be String.
	 * This function checks if model has type String.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	/**
	 * Puts the path to the profile directory to the text field.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		String profileDirPath = (String) model;
		bookLocation.setText(profileDirPath);
	}

	/**
	 * Reads the path to the book file and tries to open it.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		String bookPath = bookLocation.getText();
		try {
			signalAccess.openBook(new File(bookPath));
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "cannot access file");
		} catch (SignalMLException e1) {
			JOptionPane.showMessageDialog(null, "error occured while opening the file");
		}
	}

}
