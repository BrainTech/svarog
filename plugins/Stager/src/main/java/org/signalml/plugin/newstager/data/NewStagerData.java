package org.signalml.plugin.newstager.data;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.method.AbstractData;
import org.springframework.validation.Errors;

/** StagerData
*
*
* @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
*/
public class NewStagerData extends AbstractData {

	private static final long serialVersionUID = 1L;

	private String projectPath;
	private String patientName;
	private String signalPath;

	private NewStagerParameters parameters;
	private NewStagerFixedParameters fixedParameters;

	private MultichannelSampleSource sampleSource;

	public NewStagerData() {
		this.parameters = new NewStagerParameters();
		this.fixedParameters = new NewStagerFixedParameters();
	}

	public void validate(Errors errors) {
		errors.pushNestedPath("parameters");
		//parameters.validate(errors);
		errors.popNestedPath();
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public NewStagerParameters getParameters() {
		return parameters;
	}

	public void setParameters(NewStagerParameters parameters) {
		this.parameters = parameters;
	}

	public NewStagerFixedParameters getFixedParameters() {
		return fixedParameters;
	}

	public void setFixedParameters(NewStagerFixedParameters fixedParameters) {
		this.fixedParameters = fixedParameters;
	}

	public MultichannelSampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(MultichannelSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public String getSignalPath() {
		return signalPath;
	}

	public void setSignalPath(String signalPath) {
		this.signalPath = signalPath;
	}
}
