package org.signalml.domain.signal.raw;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.signalml.exception.SignalMLException;

public class RawSignalDescriptorTest {

    float[] gain = new float[] { 1f, 1.5f, 2.0f, -1f, 0f};
    float[] offset = new float[] { -2f, 0f, 2.0f, -3f, 4f};
    String[] labels = new String[] { "a", "b", "c", "d", "e"};
    
    static final String testFileName = "test_raw.xml";
    
    File testFile = null;

    @Before
    public void setUp() throws Exception {
        testFile = new File( testFileName);
    }

    @After
    public void tearDown() throws Exception {
        if (testFile.exists())
            testFile.delete();
    }

    @Test
    public void testIncrNextInsertPos() throws IOException, SignalMLException {
        RawSignalDescriptor d = new RawSignalDescriptor();
        d.setChannelCount( 5);
        d.setBlocksPerPage( 5);
        d.setByteOrder( RawSignalByteOrder.LITTLE_ENDIAN);
        d.setCalibrationGain( gain);
        d.setCalibrationOffset( offset);
        d.setChannelLabels( labels);
        d.setExportDate( new Date());
        d.setExportFileName( "a_file.txt");
        d.setPageSize( 20.0f);
        d.setSampleCount( 100);
        d.setSampleType( RawSignalSampleType.DOUBLE);
        d.setSamplingFrequency( 11f);
//        d.setSourceFileName( "aaa.eee");
        d.setSourceSignalType( RawSignalDescriptor.SourceSignalType.RAW);
        RawSignalDescriptorWriter writer = new RawSignalDescriptorWriter();
        writer.writeDocument( d, testFile);
        RawSignalDescriptorReader reader = new RawSignalDescriptorReader();
        RawSignalDescriptor rd = reader.readDocument( testFile);
        assertEquals( d.getBlocksPerPage(), rd.getBlocksPerPage());
        assertEquals( d.getByteOrder(), rd.getByteOrder());
//        assertEquals( d.getCalibration(), rd.getCalibration(), 0.0000001);
        for (int i=0; i<gain.length; i++)
            assertEquals( d.getCalibrationGain()[i], rd.getCalibrationGain()[i], 0.0000001);
        for (int i=0; i<offset.length; i++)
            assertEquals( d.getCalibrationOffset()[i], rd.getCalibrationOffset()[i], 0.0000001);
        assertEquals( d.getChannelCount(), rd.getChannelCount());
        for (int i=0; i<labels.length; i++)
            assertEquals( d.getChannelLabels()[i], rd.getChannelLabels()[i]);
//        assertEquals( d.getExportDate(), rd.getExportDate());
        assertEquals( d.getExportFileName(), rd.getExportFileName());
        assertEquals( d.getMarkerOffset(), rd.getMarkerOffset(), 0.000001);
//        assertEquals( d.getMaximumValue().floatValue(), rd.getMaximumValue().floatValue(), 0.0000001);
//        assertEquals( d.getMinimumValue().floatValue(), rd.getMinimumValue().floatValue(), 0.0000001);
        assertEquals( d.getPageSize(), rd.getPageSize(), 0.0000001);
        assertEquals( d.getSampleCount(), rd.getSampleCount());
        assertEquals( d.getSampleType(), rd.getSampleType());
        assertEquals( d.getSamplingFrequency(), rd.getSamplingFrequency(), 0.0000001);
        assertEquals( d.getSourceFileName(), rd.getSourceFileName());
        assertEquals( d.getSourceSignalMLFormat(), rd.getSourceSignalMLFormat());
        assertEquals( d.getSourceSignalMLSourceUID(), rd.getSourceSignalMLSourceUID());
        assertEquals( d.getSourceSignalType(), rd.getSourceSignalType());
        
    }

}
