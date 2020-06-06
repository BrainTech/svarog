package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.DeflaterOutputStream;
import org.signalml.util.matfiles.types.DataType;

/**
 * This class can be used to write some data to Matlab MAT files.
 * The written data is compressed - if you want it to be uncompressed,
 * please use {@link MatlabFileWriter} instead.
 *
 * @author Piotr Szachewicz
 */
public class CompressedMatlabFileWriter extends MatlabFileWriter {

	public CompressedMatlabFileWriter(File file) {
		super(file);
	}

	@Override
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

	/**
	 * Writes the header for compressed data.
	 *
	 * It consists of the header used for uncompressed data plus
	 * a MI_COMPRESSED file and compressed data size.
	 *
	 * The compressed data size is set to 0 in the beginning,
	 * this value is updated in the {@link CompressedMatlabFileWriter#writeCompressedSize(FileOutputStream)}
	 * method.
	 *
	 * @param fileOutputStream
	 * @throws IOException
	 */
	protected void writeHeader(FileOutputStream fileOutputStream) throws IOException {
		DataOutputStream uncompressedDataOutputStream = new DataOutputStream(fileOutputStream);

		header.write(uncompressedDataOutputStream);
		DataType.MI_COMPRESSED.write(uncompressedDataOutputStream);
		uncompressedDataOutputStream.writeInt(0); //this value is corrected in the writeCompressedSize method
	}

	/**
	 * Corrects the compressed data size which was written
	 * in the {@link CompressedMatlabFileWriter#writeHeader(FileOutputStream)} method.
	 *
	 * @param fileOutputStream
	 * @throws IOException
	 */
	protected void writeCompressedSize(FileOutputStream fileOutputStream) throws IOException {
		/*
		 * This method checks how much data was written to the file
		 * and finds the place where the compressed size
		 * should be written (it is set to 0 by now) and appropriately
		 * changes the value which was written there.
		 */

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
