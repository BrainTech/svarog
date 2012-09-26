package org.signalml.uninstaller;

import java.io.File;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.izforge.izpack.event.UninstallerListener;
import com.izforge.izpack.util.AbstractUIHandler;
import com.izforge.izpack.util.AbstractUIProgressHandler;

public class Uninstaller implements UninstallerListener {

	@Override
	public void afterDelete(File arg0, AbstractUIProgressHandler arg1)
	throws Exception {

	}

	@Override
	public void afterDeletion(List arg0, AbstractUIProgressHandler arg1)
	throws Exception {
		int answer = arg1.askQuestion("Remove configuration files?", "Do you want to remove the configuration files for Svarog?", AbstractUIHandler.CHOICES_YES_NO, AbstractUIHandler.ANSWER_NO);
		if (answer == AbstractUIHandler.ANSWER_YES) {
			answer = arg1.askQuestion("Curren user or all?",
									  "Do you want to remove configuration files only for the current user (YES) or for all users (NO)?",
									  AbstractUIHandler.CHOICES_YES_NO_CANCEL,
									  AbstractUIHandler.ANSWER_CANCEL);
			if (answer == AbstractUIHandler.ANSWER_YES) {
				removeCurrentUser();
			} else if (answer == AbstractUIHandler.ANSWER_NO)
				removeAllProfiles();
		}
	}

	@Override
	public void beforeDelete(File arg0, AbstractUIProgressHandler arg1)
	throws Exception {
	}

	public void removeCurrentUser() {
		Preferences preferences = Preferences.userRoot().node("org/signalml");
		String nazwa = preferences.get("profilePath".toString(), null);
		if (nazwa != null)
			deleteDirectoryRecursive(new File(nazwa));
		else {
			nazwa = System.getProperty("user.home");
			removeProfile(new File(nazwa));
		}
		try {
			preferences.removeNode();
		} catch (BackingStoreException e) {
		}
	}

	private void deleteDirectoryRecursive(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			for (int i=0; i<files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectoryRecursive(files[i]);
				} else {
					try {
						files[i].delete();
					} catch (SecurityException e) {
					}

				}
			}
		}
		try {
			directory.delete();
		} catch (SecurityException e) {
		}
	}

	private void removeProfile(File directory) {
		File svarogProfile = new File(directory.getAbsolutePath() + File.separator + "signalml");
		if (svarogProfile.exists() && svarogProfile.isDirectory())
			deleteDirectoryRecursive(svarogProfile);
	}

	private void removeAllProfiles() {
		removeCurrentUser();
		File profileDirectory = new File(System.getProperty("user.home"));
		File profileParent = profileDirectory.getParentFile();
		File[] profileDirs = profileParent.listFiles();
		for (File profileDir : profileDirs) {
			removeProfile(profileDir);
		}
	}

	@Override
	public void beforeDeletion(List arg0, AbstractUIProgressHandler arg1) throws Exception {
	}

	@Override
	public boolean isFileListener() {
		return false;
	}
}
