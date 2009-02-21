/* ArtifactResultTargetDescriptor.java created 2008-02-21
 * 
 */

package org.signalml.app.method.artifact;

import java.io.File;
import java.util.ArrayList;

import org.signalml.app.document.TagDocument;

/** ArtifactResultTargetDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactResultTargetDescriptor {

	private boolean signalAvailable;
	
	private TagDocument primaryTag;

	private boolean primaryOpenInWindow;
	private boolean primarySaveToFile;
	
	private File primaryTagFile;
	
	private ArrayList<File> additionalTags;
	private ArrayList<File> chosenAdditionalTags;
	private boolean additionalOpenInWindow;
	private boolean additionalSaveToFile;
		
	public boolean isSignalAvailable() {
		return signalAvailable;
	}

	public void setSignalAvailable(boolean signalAvailable) {
		this.signalAvailable = signalAvailable;
	}

	public TagDocument getPrimaryTag() {
		return primaryTag;
	}	
	
	public void setPrimaryTag(TagDocument primaryTag) {
		this.primaryTag = primaryTag;
	}

	public boolean isPrimaryOpenInWindow() {
		return primaryOpenInWindow;
	}

	public void setPrimaryOpenInWindow(boolean primaryOpenInWindow) {
		this.primaryOpenInWindow = primaryOpenInWindow;
	}

	public boolean isPrimarySaveToFile() {
		return primarySaveToFile;
	}

	public void setPrimarySaveToFile(boolean primarySaveToFile) {
		this.primarySaveToFile = primarySaveToFile;
	}

	public File getPrimaryTagFile() {
		return primaryTagFile;
	}

	public void setPrimaryTagFile(File primaryTagFile) {
		this.primaryTagFile = primaryTagFile;
	}

	public ArrayList<File> getAdditionalTags() {
		return additionalTags;
	}

	public void setAdditionalTags(ArrayList<File> additionalTags) {
		this.additionalTags = additionalTags;
	}

	public ArrayList<File> getChosenAdditionalTags() {
		return chosenAdditionalTags;
	}

	public void setChosenAdditionalTags(ArrayList<File> chosenAdditionalTags) {
		this.chosenAdditionalTags = chosenAdditionalTags;
	}

	public boolean isAdditionalOpenInWindow() {
		return additionalOpenInWindow;
	}

	public void setAdditionalOpenInWindow(boolean additionalOpenInWindow) {
		this.additionalOpenInWindow = additionalOpenInWindow;
	}

	public boolean isAdditionalSaveToFile() {
		return additionalSaveToFile;
	}

	public void setAdditionalSaveToFile(boolean additionalSaveToFile) {
		this.additionalSaveToFile = additionalSaveToFile;
	}
	
}
