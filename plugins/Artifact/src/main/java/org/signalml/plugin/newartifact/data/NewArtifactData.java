/* ArtifactData.java created 2007-11-01
 *
 */

package org.signalml.plugin.newartifact.data;

import java.io.File;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.method.AbstractData;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/** ArtifactData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactData extends AbstractData {

	private static final long serialVersionUID = 1L;

	private NewArtifactParameters parameters;

	private String projectPath;
	private String patientName;

	private boolean processedProject;

	private String signalPath;

	private int pageSize;
	private int blocksPerPage;

	@XStreamOmitField
	private MultichannelSampleSource sampleSource;

	@XStreamOmitField
	private File projectFile;

	public NewArtifactData() {
		this(new NewArtifactParameters());
	}

	public NewArtifactData(NewArtifactParameters parameters) {
		super();
		this.parameters = parameters;
	}

	public NewArtifactParameters getParameters() {
		return parameters;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getSignalPath() {
		return signalPath;
	}

	public void setSignalPath(String signalPath) {
		this.signalPath = signalPath;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getBlocksPerPage() {
		return blocksPerPage;
	}

	public void setBlocksPerPage(int blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	public MultichannelSampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(MultichannelSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public boolean isProcessedProject() {
		return processedProject;
	}

	public void setProcessedProject(boolean processedProject) {
		this.processedProject = processedProject;
	}

	public void validate(Errors errors) {
		// TODO maybe do, not needed now
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public File getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(File projectFile) {
		this.projectFile = projectFile;
	}

}