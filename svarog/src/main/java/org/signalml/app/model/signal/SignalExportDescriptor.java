/* SignalExportDescriptor.java created 2008-01-27
 *
 */

package org.signalml.app.model.signal;

import org.signalml.app.config.preset.Preset;
import org.signalml.domain.signal.ExportFormatType;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.tag.StyledTagSet;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SignalExportDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("signalexport")
public class SignalExportDescriptor implements Preset {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private String separator;
	
	private ExportFormatType formatType;

	private boolean exportTag;

	private transient StyledTagSet tagSet;
	private transient float pageSize;
	private transient float blockSize;
	private transient double normalizationFactor;

	private SignalSpace signalSpace;

	private RawSignalSampleType sampleType;
	private RawSignalByteOrder byteOrder;

	private boolean saveXML;
	private boolean normalize;

	public SignalExportDescriptor() {
		signalSpace = new SignalSpace();
		formatType = ExportFormatType.RAW;
		separator = ";";
		sampleType = RawSignalSampleType.FLOAT;
		byteOrder = RawSignalByteOrder.LITTLE_ENDIAN;
		saveXML = true;
		normalize = false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public boolean isExportTag(){
		return exportTag;
	}

	public void setExportTag(boolean exportTag){
		this.exportTag = exportTag;
	}

	public String getSeparator(){
		return separator;
	}

	public void setSeparator(String separator){
		this.separator = separator;
	}
	
	public ExportFormatType getFormatType(){
		return formatType;
	}

	public void setFormatType(ExportFormatType formatType){
		this.formatType = formatType;
	}
	
	public SignalSpace getSignalSpace() {
		return signalSpace;
	}

	public void setSignalSpace(SignalSpace signalSpace) {
		this.signalSpace = signalSpace;
	}

	public RawSignalSampleType getSampleType() {
		return sampleType;
	}

	public void setSampleType(RawSignalSampleType sampleType) {
		this.sampleType = sampleType;
	}

	public RawSignalByteOrder getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(RawSignalByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	public boolean isSaveXML() {
		return saveXML;
	}

	public void setSaveXML(boolean saveXML) {
		this.saveXML = saveXML;
	}

	public StyledTagSet getTagSet() {
		return tagSet;
	}

	public void setTagSet(StyledTagSet tagSet) {
		this.tagSet = tagSet;
	}

	public float getPageSize() {
		return pageSize;
	}

	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}

	public float getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(float blockSize) {
		this.blockSize = blockSize;
	}

	public boolean isNormalize() {
		return normalize;
	}

	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}

	public double getNormalizationFactor() {
		return normalizationFactor;
	}

	public void setNormalizationFactor(double normalizationFactor) {
		this.normalizationFactor = normalizationFactor;
	}

	@Override
	public String toString() {
		return name;
	}

}
