package org.signalml.app.document.signal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Generic class for reading text files fast.
 * It stores line offset for random-access performance.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class IndexedTextFile {

	protected static final Logger logger = Logger.getLogger(IndexedTextFile.class);
	
	private static class BlockOfLines {
		public final int index;
		public final String[] lines;
		public BlockOfLines(int index, String[] lines) {
			this.index = index;
			this.lines = lines;
		}
	}

	private final static int SCAN_BUFFER_SIZE = 1048576; // 1 MiB
	private final static byte ENDLINE_ASCII_CODE = (byte) '\n';

	private final int blockSize;
	private final RandomAccessFile input;
	private final List<Long> blockEndOffsets = new ArrayList<>();

	private int lineCount;
	private BlockOfLines lastBlock;

	/**
	 * Create a new random-access text file.
	 * Parameter "blockSize" is a parameter defining trade-off between memory
	 * usage and speed. Value of 1 means that every line offset is stored.
	 * Greater value (N, for example) mean that every Nth line offset is stored.
	 *
	 * @param file  file handle
	 * @param blockSize  memory-vs-speed parameter (10 is a reasonable value)
	 * @throws IOException 
	 */
	public IndexedTextFile(File file, int blockSize) throws IOException {
		this.blockSize = blockSize;
		input = new RandomAccessFile(file, "r");
		computeBlockOffsets(input);
	}

	public void close() {
		try {
			input.close();
		} catch (IOException ex) {
			logger.debug("error while closing indexed text file", ex);
		}
	}
	
	public String getLine(int lineIndex) throws IOException {
		if (lineIndex < 0 || lineIndex >= lineCount) {
			throw new IOException("trying to read outside end-of-file");
		}
		int lineIndexInBlock = lineIndex % blockSize;
		BlockOfLines block = getBlockOfLines(lineIndex / blockSize);
		return block.lines[lineIndexInBlock];
	}
	
	public int getLineCount() {
		return lineCount;
	}
	
	private void computeBlockOffsets(RandomAccessFile input) throws FileNotFoundException, IOException {
		int bytesRead;
		byte[] buffer = new byte[SCAN_BUFFER_SIZE];
		long totalBytesRead = 0, lastLineOffset = 0;
		while ((bytesRead = input.read(buffer)) > 0) {
			for (int i=0; i<bytesRead; ++i) {
				if (buffer[i] == ENDLINE_ASCII_CODE) {
					lastLineOffset = totalBytesRead + i;
					if (++lineCount % blockSize == 0) {
						blockEndOffsets.add(lastLineOffset);
					}
				}
			}
			totalBytesRead += bytesRead;
		}
		if (totalBytesRead > lastLineOffset + 1) {
			// last line was not empty
			++lineCount;
		}
		blockEndOffsets.add(totalBytesRead);
	}

	private BlockOfLines getBlockOfLines(int blockIndex) throws IOException {
		if (lastBlock == null || lastBlock.index != blockIndex) {
			long blockStartInBytes = (blockIndex == 0) ? 0 : blockEndOffsets.get(blockIndex-1) + 1;
			int blockLength = (int) (blockEndOffsets.get(blockIndex) - blockStartInBytes);
			byte[] data = new byte[blockLength];
			input.seek(blockStartInBytes);
			if (input.read(data) != blockLength) {
				throw new IOException("premature end-of-file");
			}
			String[] lines = new String(data).split("\n");
			lastBlock = new BlockOfLines(blockIndex, lines);
		}
		return lastBlock;
	}

}
