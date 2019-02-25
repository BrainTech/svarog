package org.signalml.app.view.common.components.filechooser;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;

import com.alee.extended.list.FileListViewType;
import com.alee.laf.filechooser.FileChooserViewType;
import com.alee.laf.filechooser.WebFileChooser;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

import org.signalml.app.model.components.validation.ValidationErrors;

/**
 * A file chooser which should be used by all Svarog components. Adds a
 * favourites panel to the file chooser.
 *
 * @author Piotr Szachewicz
 */
public class SignalMLFileChooser extends WebFileChooser {

	/**
	 * Constructs a <code>EmbeddedFileChooser</code> pointing to the user's
	 * default directory. This default depends on the operating system. It is
	 * typically the "My Documents" folder on Windows, and the user's home
	 * directory on Unix.
	 */
	public SignalMLFileChooser() {
		getFileChooserPanel().setViewType(FileChooserViewType.table);
		EmbeddedFileChooserFavorites f = new EmbeddedFileChooserFavorites(this);
		this.setAccessory(f);
	}

	public void lastDirectoryChanged() {
		if (getAccessory() != null) {
			String dir = getSelectedFile().getParent();
			getAccessory().lastDirectoryChanged(dir);
		}
	}

	/**
	 * Validates the chosen file. The file is valid if following occurs
	 * (conjunction):
	 * <ul>
	 * <li>the file is selected or {@code acceptNone} is {@code true},</li>
	 * <li>the file exists or {@code acceptMissing} is {@code true},</li>
	 * <li>the file is not a directory or {@code acceptDirectory} is
	 * {@code true},</li>
	 * <li>the file can be read or {@code acceptUnreadable} is {@code true},</li>
	 * <li>the file can be written or {@code acceptReadOnly} is {@code true},</li>
	 * </ul>
	 *
	 * @param errors
	 *            the variable in which errors are stored
	 * @param property
	 *            the name of the property
	 * @param acceptNone
	 *            if no file selected should be accepted
	 * @param acceptMissing
	 *            if not existing files should be accepted
	 * @param acceptDirectory
	 *            if directories should be accepted
	 * @param acceptUnreadable
	 *            if unreadable files should be accepted
	 * @param acceptReadOnly
	 *            if read only files should be accepted
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

	@Override
	public EmbeddedFileChooserFavorites getAccessory() {
		return (EmbeddedFileChooserFavorites) super.getAccessory();
	}

}
