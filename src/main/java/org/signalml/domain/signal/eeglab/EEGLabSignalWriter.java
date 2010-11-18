package org.signalml.domain.signal.eeglab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLInt64;
import com.jmatio.types.MLStructure;

public class EEGLabSignalWriter {

	private int sampleCount;
	private int channelCount;
//	private int sampleByteSize;
	private double freq;
	private byte[] byteBuffer;
	private double[] chunk;
	private ByteBuffer bBuffer;
	private DoubleBuffer dBuffer;

	public EEGLabSignalWriter(int sampleCount, double freq, int channelCount,
			int sampleByteSize, ByteOrder byteOrder) {
		this.channelCount = channelCount;
		this.sampleCount = sampleCount;
		this.freq = freq;
		chunk = new double[channelCount];
		byteBuffer = new byte[channelCount * sampleByteSize];
		bBuffer = ByteBuffer.wrap(byteBuffer).order(byteOrder);
		dBuffer = bBuffer.asDoubleBuffer();
	}

	public void writeSignal(File input, File output) throws IOException {

		FileInputStream in = new FileInputStream(input);

		MLStructure eegStruct = new MLStructure("EEG", new int[]{1, 1});
		eegStruct.setField("filename", new MLChar("filename", "struct_file.mat"));
		eegStruct.setField("pnts", new MLInt64("pnts", new long[]{sampleCount}, 1));
		eegStruct.setField("nbchan", new MLInt64("nbchan", new long[]{channelCount}, 1));
		eegStruct.setField("trials", new MLInt64("trials", new long[]{1}, 1));
		eegStruct.setField("srate", new MLDouble("srate", new double[]{freq}, 1));

		double[][] data = new double[channelCount][sampleCount];
		for (int i = 0; i < sampleCount; i++) {
			System.out.println(i);
			readChunk(in);
			for (int j = 0; j < channelCount; j++) {
				data[j][i] = chunk[j];
			}
		}
		eegStruct.setField("data", new MLDouble("data", data));

		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add(eegStruct);
		new MatFileWriter(output, list);

	}

	private void readChunk(InputStream in) throws IOException {
		dBuffer.clear();
		in.read(byteBuffer);
		dBuffer.get(chunk, 0, chunk.length);
	}

}
