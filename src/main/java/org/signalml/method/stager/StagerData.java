/* StagerData.java created 2008-02-08
 * 
 */

package org.signalml.method.stager;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.method.AbstractData;
import org.springframework.validation.Errors;

/** StagerData
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerData extends AbstractData {

	private static final long serialVersionUID = 1L;

	private String projectPath;
	private String patientName;
	private String signalPath;	

	private StagerParameters parameters;	
	private StagerFixedParameters fixedParameters;
	
	private MultichannelSampleSource sampleSource;
	
	public StagerData() {
		parameters = new StagerParameters();
		fixedParameters = new StagerFixedParameters();		
	}
	
	public void validate( Errors errors ) {
		errors.pushNestedPath("parameters");
		parameters.validate(errors);
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

	public StagerParameters getParameters() {
		return parameters;
	}

	public void setParameters(StagerParameters parameters) {
		this.parameters = parameters;
	}

	public StagerFixedParameters getFixedParameters() {
		return fixedParameters;
	}

	public void setFixedParameters(StagerFixedParameters fixedParameters) {
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
