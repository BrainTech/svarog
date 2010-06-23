package org.signalml.app.document;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DocumentFlowIntegratorTest {

	class Metadata {
		private String signalType;
		private String sampleType;
		private String byteOrder;
		private int channelCount;
		private List<String> channelLabels = new ArrayList<String>();
		private double pageSize;
		private double samplingFrequency;
		private double calibrationGain;
		private double calibrationOffset;
		public Metadata(String signalType, String sampleType, String byteOrder,
				int channelCount, double pageSize, double samplingFrequency,
				double calibrationGain, double calibrationOffset) {
			super();
			this.signalType = signalType;
			this.sampleType = sampleType;
			this.byteOrder = byteOrder;
			this.channelCount = channelCount;
			this.pageSize = pageSize;
			this.samplingFrequency = samplingFrequency;
			this.calibrationGain = calibrationGain;
			this.calibrationOffset = calibrationOffset;
		}
		public String getSignalType() {
			return signalType;
		}
		public void setSignalType(String signalType) {
			this.signalType = signalType;
		}
		public String getSampleType() {
			return sampleType;
		}
		public void setSampleType(String sampleType) {
			this.sampleType = sampleType;
		}
		public String getByteOrder() {
			return byteOrder;
		}
		public void setByteOrder(String byteOrder) {
			this.byteOrder = byteOrder;
		}
		public int getChannelCount() {
			return channelCount;
		}
		public void setChannelCount(int channelCount) {
			this.channelCount = channelCount;
		}
		public void addChannelLabel( String channelLabel) {
			channelLabels.add( channelLabel);
		}
		public List< String> getChannelLabels() {
			return channelLabels;
		}
		public double getPageSize() {
			return pageSize;
		}
		public void setPageSize(double pageSize) {
			this.pageSize = pageSize;
		}
		public double getSamplingFrequency() {
			return samplingFrequency;
		}
		public void setSamplingFrequency(double samplingFrequency) {
			this.samplingFrequency = samplingFrequency;
		}
		public double getCalibrationGain() {
			return calibrationGain;
		}
		public void setCalibrationGain(double calibrationGain) {
			this.calibrationGain = calibrationGain;
		}
		public double getCalibrationOffset() {
			return calibrationOffset;
		}
		public void setCalibrationOffset(double calibrationOffset) {
			this.calibrationOffset = calibrationOffset;
		}
	}
	@Test
	public void testSaveMetaFile() throws Exception {
		OpenMonitorDescriptor monitorDescriptor = new OpenMonitorDescriptor();
		monitorDescriptor.setFileName( "test");
		monitorDescriptor.setType( SignalType.EEG_10_20);
		monitorDescriptor.setSampleType( RawSignalSampleType.DOUBLE);
		monitorDescriptor.setByteOrder( RawSignalByteOrder.BIG_ENDIAN);
		monitorDescriptor.setChannelCount( 20);
		String[] channelLabels = new String[20];
		for (int i=0; i<channelLabels.length; i++)
			channelLabels[i] = "ch" + i;
		monitorDescriptor.setChannelLabels( channelLabels);
		monitorDescriptor.setPageSize( 10.0);
		monitorDescriptor.setSamplingFrequency( 11.0F);
//		monitorDescriptor.setDataScale( 1.0F);
		float[] gain = new float[20];
		for (int i=0; i<gain.length; i++)
			gain[i] = i + 1.5f;
		monitorDescriptor.setCalibrationGain( gain);
		float[] offset = new float[20];
		for (int i=0; i<offset.length; i++)
			offset[i] = i + .3f;
		monitorDescriptor.setCalibrationOffset( offset);
		DocumentFlowIntegrator.saveMetaFile( monitorDescriptor);
		FileInputStream in = new FileInputStream( new File( monitorDescriptor.getFileName() + ".xml"));
		InputStreamReader ir = new InputStreamReader( in);
		BufferedReader br = new BufferedReader( ir);
		StringBuffer buf = new StringBuffer();
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			buf.append( line).append( "\n");
		}
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("metadata", Metadata.class);
		xstream.alias("channelLabel", String.class);
		Metadata metadata = (Metadata) xstream.fromXML( buf.toString());
		assertEquals( SignalType.EEG_10_20.name(), metadata.getSignalType());
		assertEquals( RawSignalSampleType.DOUBLE.name(), metadata.getSampleType());
		assertEquals( RawSignalByteOrder.BIG_ENDIAN.name(), metadata.getByteOrder());
		assertEquals( 20, metadata.getChannelCount());
		assertEquals( 10.0, metadata.getPageSize(), 0.001);
		assertEquals( 11.0, metadata.getSamplingFrequency(), 0.001);
		assertEquals( 1.0, metadata.getCalibrationGain(), 0.001); //TODO
		assertEquals( 0.0, metadata.getCalibrationOffset(), 0.001); // TODO
		for (int i=0; i<20; i++)
			assertEquals( "ch" + i, metadata.getChannelLabels().get(i));
	}

}
