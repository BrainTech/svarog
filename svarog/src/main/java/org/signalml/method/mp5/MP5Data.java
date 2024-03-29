/* MP5Data.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.io.Serializable;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.signal.SignalProcessingChainDescriptor;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.space.SegmentedSampleSourceDescriptor;
import org.signalml.plugin.export.method.BaseMethodData;
import org.springframework.validation.Errors;

/** MP5Data
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mp5data")
public class MP5Data extends BaseMethodData implements Serializable {

	private static final long serialVersionUID = 1L;

	private MP5Parameters parameters;

	private transient MultichannelSegmentedSampleSource sampleSource;

	private File workingDirectory;
	private String executorUID;
	private String bookFilePath;

	private SignalProcessingChainDescriptor chainDescriptor;
	private SegmentedSampleSourceDescriptor sourceDescriptor;

	public MP5Data() {
		this.parameters = new MP5Parameters();
	}

	public MP5Data(MP5Parameters parameters) {
		this.parameters = parameters;
	}

	public MP5Parameters getParameters() {
		return parameters;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public String getBookFilePath() {
		return bookFilePath;
	}

	public void setBookFilePath(String bookFilePath) {
		this.bookFilePath = bookFilePath;
	}

	public String getExecutorUID() {
		return executorUID;
	}

	public void setExecutorUID(String executorUID) {
		this.executorUID = executorUID;
	}

	public MultichannelSegmentedSampleSource getSampleSource() {
		return sampleSource;
	}

	public void setSampleSource(MultichannelSegmentedSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}

	public SignalProcessingChainDescriptor getChainDescriptor() {
		return chainDescriptor;
	}

	public void setChainDescriptor(SignalProcessingChainDescriptor chainDescriptor) {
		this.chainDescriptor = chainDescriptor;
	}

	public SegmentedSampleSourceDescriptor getSourceDescriptor() {
		return sourceDescriptor;
	}

	public void setSourceDescriptor(SegmentedSampleSourceDescriptor sourceDescriptor) {
		this.sourceDescriptor = sourceDescriptor;
	}

	public void validate(Errors errors) {
		if (executorUID == null || executorUID.isEmpty()) {
			errors.rejectValue("executorUID", "error.mp5.noExecutor", _("No executor"));
		}
		errors.pushNestedPath("parameters");
		parameters.validate(errors);
		errors.popNestedPath();
		if (sampleSource == null) {
			errors.rejectValue("sampleSource", "error.mp5.noSampleSource", _("No sample source"));
		}
	}

}
