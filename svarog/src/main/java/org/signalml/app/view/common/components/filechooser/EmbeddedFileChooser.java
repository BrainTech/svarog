package org.signalml.app.view.common.components.filechooser;


/**
 * File chooser that can be embedded in the panel.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 * @author Piotr Szachewicz
 */
public class EmbeddedFileChooser extends SignalMLFileChooser {

	/**
	 * Constructs a <code>EmbeddedFileChooser</code> pointing to the user's
	 * default directory. This default depends on the operating system. It is
	 * typically the "My Documents" folder on Windows, and the user's home
	 * directory on Unix.
	 */
	public EmbeddedFileChooser() {
		super();
		setControlButtonsAreShown(false);
	}

}
