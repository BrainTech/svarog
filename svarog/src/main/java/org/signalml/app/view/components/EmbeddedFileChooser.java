/* EmbeddedFileChooser.java created 2008-01-17
 *
 */

package org.signalml.app.view.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.components.validation.ValidationErrors;

import org.springframework.validation.Errors;

import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * File chooser that can be embedded in the panel.
 * Provides ugly hack fixes for what appears to be bugs or bad design
 * in JFileChooser.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EmbeddedFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	/**
	 * the boolean used to create a hack
	 * @see #forceApproveSelection()
	 */
	private boolean suppressActionEvent = false;
	/**
	 * boolean which tells if the default button should be invoked when
	 * {@code APPROVE_SELECTION} action is performed
	 */
	private boolean invokeDefaultButtonOnApprove = false;

	/**
	 * the listener that is invoked by default when {@code APPROVE_SELECTION}
	 * action is performed;
	 * invokes the default button
	 */
	private ActionListener defaultInvoker = null;

	/**
     * Constructs a <code>EmbeddedFileChooser</code> pointing to the user's
     * default directory. This default depends on the operating system.
     * It is typically the "My Documents" folder on Windows, and the
     * user's home directory on Unix.
     */
	public EmbeddedFileChooser() {
		super();
	}
	
	public void lastDirectoryChanged(String dir){
		if(getAccessory() != null)
			getAccessory().lastDirectoryChanged(dir);
	}

	/**
     * Constructs a <code>EmbeddedFileChooser</code> pointing to the user's
     * default directory. This default depends on the operating system.
     * It is typically the "My Documents" folder on Windows, and the
     * user's home directory on Unix.
     */
	public EmbeddedFileChooser(ApplicationConfiguration applicationConfiguration) {
		super();
		EmbeddedFileChooserFavorites f = new EmbeddedFileChooserFavorites(this, applicationConfiguration);
		this.setAccessory(f);
	}

	/**
     * Constructs a <code>EmbeddedFileChooser</code> using the given <code>File</code>
     * as the path and the given {@code FileSystemView}.
     * Passing in a <code>null</code> file
     * causes the file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * @param currentDirectory  a <code>File</code> object specifying
     *				the path to a file or directory
	 * @param fsv the {@code FileSystemView}
     */
	public EmbeddedFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	/**
     * Constructs a <code>EmbeddedFileChooser</code> using the given <code>File</code>
     * as the path. Passing in a <code>null</code> file
     * causes the file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * @param currentDirectory  a <code>File</code> object specifying
     *				the path to a file or directory
     */
	public EmbeddedFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	/**
     * Constructs a <code>EmbeddedFileChooser</code> pointing to the user's
     * default directory. This default depends on the operating system.
     * It is typically the "My Documents" folder on Windows, and the
     * user's home directory on Unix.
     * <p>
     * Uses the given {@code FileSystemView}
     * @param fsv the {@code FileSystemView}
     */
	public EmbeddedFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	/**
     * Constructs a <code>EmbeddedFileChooser</code> using the given path
     * and the given {@code FileSystemView}.
     * Passing in a <code>null</code>
     * string causes the file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * @param currentDirectoryPath a <code>String</code> giving the path
     *				to a file or directory
	 * @param fsv the {@code FileSystemView}
     */
	public EmbeddedFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	/**
     * Constructs a <code>EmbeddedFileChooser</code> using the given path.
     * Passing in a <code>null</code>
     * string causes the file chooser to point to the user's default directory.
     * This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's
     * home directory on Unix.
     *
     * @param currentDirectoryPath a <code>String</code> giving the path
     *				to a file or directory
     */
	public EmbeddedFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	/**
	 * Calls the {@link JFileChooser#setup(FileSystemView) setup} in parent
	 * and sets that the control buttons should be disabled.
	 */
	@Override
	protected void setup(FileSystemView view) {
		super.setup(view);

		super.setControlButtonsAreShown(true);
	}

	/**
	 * Normally sets if control buttons should be shown, but in this case
	 * it is ignored.
	 */
	@Override
	public void setControlButtonsAreShown(boolean b) {
		// XXX ugly hack
		// this is ignored on purpose
	}

	/**
	 * Notifies all listeners that have registered interest for
     * notification on this event type. The event instance
     * is lazily created using the <code>command</code> parameter.
     * <p>
     * To allow for suppression, if {@code suppressActionEvent} is {@code true}
     * no action is taken.
	 */
	@Override
	protected void fireActionPerformed(String command) {
		// hacked to allow for supression
		if (!suppressActionEvent) {
			super.fireActionPerformed(command);
		}
	}

	/**
	 * If the control buttons are hidden, then there is no way to type in file name
	 * rather than selecting a file from the list.
	 * Forms using EmbeddedFileChooser must call this before trying to obtain
	 * the selected file because otherwise the typed filename is ignored.
	 * <p>
	 * This is done by invoking the action because the processing in this 
	 * action is very complicated and copying all that code here would
	 * not be practical. However the resulting action event needs to be
	 * suppressed to allow some use cases.
	 * <p>
	 * Action name is appended with "-auto" to allow any action listeners
	 * to differentiate the two cases.
	 */
	public void forceApproveSelection() {

		// XXX ugly hack

		// 
		FileChooserUI ui = getUI();
		if (ui instanceof BasicFileChooserUI) {
			Action approveSelectionAction = ((BasicFileChooserUI) ui).getApproveSelectionAction();
			try {
				suppressActionEvent = true;
				approveSelectionAction.actionPerformed(new ActionEvent(this,0,JFileChooser.APPROVE_SELECTION + "-auto"));
			} finally {
				suppressActionEvent = false;
			}
		}

	}

	/**
	 * Validates the chosen file. The file is valid if following occurs
	 * (conjunction):
	 * <ul>
	 * <li>the file is selected or {@code acceptNone} is {@code true},</li>
	 * <li>the file exists or {@code acceptMissing} is {@code true},</li>
	 * <li>the file is not a directory or {@code acceptDirectory} is {@code
	 * true},</li>
	 * <li>the file can be read or {@code acceptUnreadable} is {@code true},
	 * </li>
	 * <li>the file can be written or {@code acceptReadOnly} is {@code true},
	 * </li></ul>
	 * @param errors the variable in which errors are stored
	 * @param property the name of the property
	 * @param acceptNone if no file selected should be accepted
	 * @param acceptMissing if not existing files should be accepted
	 * @param acceptDirectory if directories should be accepted
	 * @param acceptUnreadable if unreadable files should be accepted
	 * @param acceptReadOnly if read only files should be accepted
	 */
	public void validateFile(ValidationErrors errors, String property, boolean acceptNone, boolean acceptMissing, boolean acceptDirectory, boolean acceptUnreadable, boolean acceptReadOnly) {

		File file = getSelectedFile();
		if (file == null || file.getPath().length() == 0) {
			if (!acceptNone) {
				errors.addError(_("A file must be chosen"));
			}
		} else {
			if (!file.exists()) {
				if (!acceptMissing) {
					errors.addError(_("File not found"));
				}
			} else {
				if (!acceptDirectory && file.isDirectory()) {
					errors.addError(_("File is not a regular file"));
				}
				if (!acceptUnreadable && !file.canRead()) {
					errors.addError(_("File is not readable"));
				}
				if (!acceptReadOnly && !file.canWrite()) {
					errors.addError(_("File is not writable"));
				}
			}
		}

	}

	/**
	 * Returns if the default button should be invoked when {@code
	 * APPROVE_SELECTION} action is performed.
	 * @return {@code true} if the default button should be invoked when {@code
	 * APPROVE_SELECTION} action is performed, {@code false} otherwise
	 */
	public boolean isInvokeDefaultButtonOnApprove() {
		return invokeDefaultButtonOnApprove;
	}

	/**
	 * Sets if the default button should be invoked when {@code
	 * APPROVE_SELECTION} action is performed.
	 * @param invokeDefaultButtonOnApprove {@code true} if the default button
	 * should be invoked when {@code APPROVE_SELECTION} action is performed,
	 * {@code false} otherwise
	 */
	public void setInvokeDefaultButtonOnApprove(boolean invokeDefaultButtonOnApprove) {
		if (this.invokeDefaultButtonOnApprove != invokeDefaultButtonOnApprove) {
			if (this.invokeDefaultButtonOnApprove) {
				this.removeActionListener(defaultInvoker);
			}
			this.invokeDefaultButtonOnApprove = invokeDefaultButtonOnApprove;
			if (invokeDefaultButtonOnApprove) {
				if (defaultInvoker == null) {
					defaultInvoker = new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
								getRootPane().getDefaultButton().getAction().actionPerformed(e);
							}
						}

					};
					addActionListener(defaultInvoker);
				}
			}
		}
	}

	@Override
	public EmbeddedFileChooserFavorites getAccessory(){
		return (EmbeddedFileChooserFavorites) super.getAccessory();
	}
	
}
