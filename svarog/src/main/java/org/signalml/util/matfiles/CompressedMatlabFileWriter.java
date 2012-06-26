package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.DeflaterOutputStream;

import org.signalml.util.matfiles.types.DataType;

public class CompressedMatlabFileWriter extends MatlabFileWriter {

	public CompressedMatlabFileWriter(File file) throws FileNotFoundException {
		super(file);
	}

	public void write() throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);

		writeHeader(fileOutputStream);

		//create compressed output stream
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fileOutputStream);
		DataOutputStream compressedDataOutputStream = new DataOutputStream(deflaterOutputStream);

		//write compressed data
		writeData(compressedDataOutputStream);
		deflaterOutputStream.finish(); //flushes data out, but doesn't close the outputStream

		//correct the compressed size
		writeCompressedSize(fileOutputStream);

		compressedDataOutputStream.close();
	}

	protected void writeHeader(FileOutputStream fileOutputStream) throws IOException {
		DataOutputStream uncompressedDataOutputStream = new DataOutputStream(fileOutputStream);

		header.write(uncompressedDataOutputStream);
		DataType.MI_COMPRESSED.write(uncompressedDataOutputStream);
		uncompressedDataOutputStream.writeInt(0); //this value is corrected in the writeCompressedSize method
	}

	protected void writeCompressedSize(FileOutputStream fileOutputStream) throws IOException {
		FileChannel channel = fileOutputStream.getChannel();
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		int size = (int) channel.size();
		size -= header.getTotalSizeInBytes() - DataType.MI_COMPRESSED.getTotalSizeInBytes() - 4 /*compressed size*/;
		byteBuffer.putInt(size);

        long previousPosition = channel.position();
		channel.position(header.getTotalSizeInBytes() + DataType.MI_COMPRESSED.getTotalSizeInBytes());
		byteBuffer.flip();
		channel.write(byteBuffer);

		channel.position(previousPosition);
	}

}
