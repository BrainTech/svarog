package org.signalml.plugin.newartifact.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.signalml.plugin.export.SignalMLException;

public class NewArtifactAlgorithmWriter implements INewArtifactAlgorithmWriter {
	private static int BUFFER_SIZE = 8192;

	private DataOutputStream stream;
	private final ByteBuffer byteBuffer;

	public NewArtifactAlgorithmWriter(File targetFile) throws SignalMLException {
		try {
			this.stream = new DataOutputStream(new FileOutputStream(targetFile));
		} catch (FileNotFoundException e) {
			this.stream = null;
			throw new SignalMLException(e);
		}

		this.byteBuffer = ByteBuffer.allocate(NewArtifactAlgorithmWriter.BUFFER_SIZE);
		this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}

	@Override
	public void write(double[][] buffer) throws IOException {
		this.byteBuffer.rewind();

		for (int i = 0; i < buffer.length; ++i)
		{
			double subArray[] = buffer[i];
			for (int j = 0; j < subArray.length; ++j) {
				if ((this.byteBuffer.remaining() >> 2) >= 1) {
					this.byteBuffer.putFloat((float) subArray[j]);
				} else {
					this.stream.write(this.byteBuffer.array(), 0,
							  this.byteBuffer.capacity() - this.byteBuffer.remaining());
					this.byteBuffer.rewind();
				}
			}
		}
		if (this.byteBuffer.remaining() < this.byteBuffer.capacity()) {
			this.stream.write(this.byteBuffer.array(), 0,
					  this.byteBuffer.capacity() - this.byteBuffer.remaining());
		}

		this.stream.flush();	//TODO
	}

	public void close() throws IOException {
		if (this.stream != null) {
			this.stream.close();
		}
	}

}
