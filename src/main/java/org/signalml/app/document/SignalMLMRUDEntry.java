/* SignalMLMRUDEntry.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SignalMLMRUDEntry
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mrud-signalml")
public class SignalMLMRUDEntry extends MRUDEntry {

	private String codecUID;
	private String formatName;

	private Float samplingFrequency;
	private Integer channelCount;
	private Float calibration;
	private Float pageSize;
	private Integer blocksPerPage;

	protected SignalMLMRUDEntry() {
		super();
	}

	public SignalMLMRUDEntry(ManagedDocumentType documentType, Class<?> documentClass, String path, String codecUID, String formatName) {
		super(documentType, documentClass, path);
		this.codecUID = codecUID;
		this.formatName = formatName;
	}

	public String getCodecUID() {
		return codecUID;
	}

	public void setCodecUID(String codecUID) {
		this.codecUID = codecUID;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public Float getSamplingFrequency() {
		return samplingFrequency;
	}

	public void setSamplingFrequency(Float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public Integer getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(Integer channelCount) {
		this.channelCount = channelCount;
	}

	public Float getCalibration() {
		return calibration;
	}

	public void setCalibration(Float calibration) {
		this.calibration = calibration;
	}

	public Float getPageSize() {
		return pageSize;
	}

	public void setPageSize(Float pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getBlocksPerPage() {
		return blocksPerPage;
	}

	public void setBlocksPerPage(Integer blocksPerPage) {
		this.blocksPerPage = blocksPerPage;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {
		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(2, new LabelledPropertyDescriptor("property.signalmlmrud.formatName", "formatName", SignalMLMRUDEntry.class));

		return list;

	}

}
