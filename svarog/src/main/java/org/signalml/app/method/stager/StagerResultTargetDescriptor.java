/* StagerResultTargetDescriptor.java created 2008-02-20
 *
 */

package org.signalml.app.method.stager;

import java.io.File;
import java.util.ArrayList;

import org.signalml.app.document.TagDocument;
import org.signalml.method.stager.StagerResult;

/** StagerResultTargetDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerResultTargetDescriptor {

	private boolean signalAvailable;

	private int segmentCount;
	private float segmentLength;

	private TagDocument primaryTag;

	private StagerResult stagerResult;

	private boolean primaryOpenInWindow;
	private boolean primarySaveToFile;

	private File primaryTagFile;

	private ArrayList<File> additionalTags;
	private ArrayList<File> chosenAdditionalTags;
	private boolean additionalOpenInWindow;
	private boolean additionalSaveToFile;

	private File expertStageTagFile;
	private File expertArtifactTagFile;

	public boolean isSignalAvailable() {
		return signalAvailable;
	}

	public void setSignalAvailable(boolean signalAvailable) {
		this.signalAvailable = signalAvailable;
	}

	public int getSegmentCount() {
		return segmentCount;
	}

	public void setSegmentCount(int segmentCount) {
		this.segmentCount = segmentCount;
	}

	public float getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(float segmentLength) {
		this.segmentLength = segmentLength;
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

	public File getExpertStageTagFile() {
		return expertStageTagFile;
	}

	public void setExpertStageTagFile(File expertStageTagFile) {
		this.expertStageTagFile = expertStageTagFile;
	}

	public File getExpertArtifactTagFile() {
		return expertArtifactTagFile;
	}

	public void setExpertArtifactTagFile(File expertArtifactTagFile) {
		this.expertArtifactTagFile = expertArtifactTagFile;
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

	public StagerResult getStagerResult() {
		return stagerResult;
	}

	public void setStagerResult(StagerResult stagerResult) {
		this.stagerResult = stagerResult;
	}

}
