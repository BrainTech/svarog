package org.signalml.plugin.newstager.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.plugin.newstager.data.NewStagerBookAtom;
import org.signalml.plugin.newstager.data.NewStagerBookData;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;
import org.signalml.plugin.newstager.data.NewStagerDecomposingInfo;
import org.signalml.plugin.newstager.data.NewStagerSignalInfo;
import org.signalml.plugin.newstager.exception.NewStagerBookReaderException;

import pl.edu.fuw.MP.Core.FormatComponentV5;

public class NewStagerFastBookV5AtomReader implements INewStagerAtomReader {

	protected static final Logger logger = Logger
										   .getLogger(NewStagerFastBookV5AtomReader.class);

	private File bookFile;
	private MappedByteBuffer fileBuffer;
	private byte lastCode;

	private NewStagerSignalInfo signalInfo;
	private NewStagerDecomposingInfo decomposingInfo;

	private int offsetDimension;

	private int maxOffsetNumber;

	private ArrayList<ArrayList<NewStagerBookAtom[]>> books;

	private static int FLOAT_SIZE = Float.SIZE >> 3;
	private static int BYTE_SIZE = Byte.SIZE >> 3;
	private static int SHORT_SIZE = Short.SIZE >> 3;

	public NewStagerFastBookV5AtomReader(File bookFile) {
		this.fileBuffer = null;
		this.signalInfo = null;
		this.decomposingInfo = null;
		this.lastCode = 0;

		this.bookFile = bookFile;
	}

	@Override
	public NewStagerBookData read() throws NewStagerBookReaderException {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(this.bookFile, "r");
		} catch (FileNotFoundException e) {
			throw new NewStagerBookReaderException(e);
		}

		try {
			FileChannel channel = file.getChannel();

			try {
				this.fileBuffer = channel.map(MapMode.READ_ONLY, 0,
											  channel.size());

				this.skipMagicAndComment();
				this.readFileHeader();

				this.readSegments();
			} catch (IOException e) {
				throw new NewStagerBookReaderException(e);
			}
		} finally {
			try {
				file.close();
			} catch (IOException e) {
				throw new NewStagerBookReaderException(e);
			}
		}

		return this.createResult();
	}

	private NewStagerBookData createResult() {
		NewStagerBookAtom atoms[][];
		
		if (this.books.size() == 1) {
			atoms = this.books.get(0).toArray(new NewStagerBookAtom[0][]);
		} else {
			// TODO
			throw new RuntimeException("Not implemented");
		}

		return new NewStagerBookData(new NewStagerBookInfo(
										 atoms.length,
										 this.offsetDimension, this.signalInfo.samplingFrequency,
										 this.signalInfo.pointsPerMicrovolt), atoms);
	}

	private void readSegments() throws NewStagerBookReaderException {
		this.books = new ArrayList<ArrayList<NewStagerBookAtom[]>>(
			this.signalInfo.numberOfChannels);

		while (this.fileBuffer.hasRemaining()) {
			this.readSegmentCode();

			int size = this.fileBuffer.getInt();

			switch (this.lastCode) {
			case FormatComponentV5.OFFSET_SEGMENT_IDENTITY:
				this.readAtoms(size);
				break;
			case FormatComponentV5.COMMENT_SEGMENT_IDENTITY:
				// skip //$FALL-THROUGH$
			default:
				this.fileBuffer.position(this.fileBuffer.position() + size);
			}
		}

	}

	private void readAtoms(int segmentSize) throws NewStagerBookReaderException {
		int endPosition = this.fileBuffer.position() + segmentSize;

		short offsetNumber = this.fileBuffer.getShort();
		this.offsetDimension = this.fileBuffer.getInt();

		this.maxOffsetNumber = Math.max(this.maxOffsetNumber, offsetNumber);

		while (this.fileBuffer.position() < endPosition) {
			this.readSegmentCode();
			segmentSize = this.fileBuffer.getInt();

			switch (this.lastCode) {
			case FormatComponentV5.SIGNAL_SEGMENT_IDENTITY:
				this.fileBuffer.position(this.fileBuffer.position()
										 + segmentSize);
				break;
			case FormatComponentV5.ATOMS_SEGMENT_IDENTITY:
				this.readSingleAtomSegment(offsetNumber, segmentSize);
				break;
			default:
				logger.warn("Unknown segment code: " + this.lastCode);
				this.fileBuffer.position(this.fileBuffer.position()
										 + segmentSize);
			}
		}

	}

	private void readSingleAtomSegment(int offsetNumber, int segmentSize)
	throws NewStagerBookReaderException {
		int endPosition = this.fileBuffer.position() + segmentSize;

		List<NewStagerBookAtom> atoms = new LinkedList<NewStagerBookAtom>();

		short channelNumber = this.fileBuffer.getShort();

		while (this.fileBuffer.position() < endPosition) {
			NewStagerBookAtom atom = this.readSingleAtom();

			if (atom != null) {
				atoms.add(atom);
			}
		}

		ArrayList<NewStagerBookAtom[]> channelAtoms;
		if (channelNumber > this.books.size()
				|| this.books.get(channelNumber - 1) == null) {
			channelAtoms = new ArrayList<NewStagerBookAtom[]>(
				this.maxOffsetNumber);
			while (this.books.size() < channelNumber - 1) {
				this.books.add(null);
			}
			this.books.add(channelNumber - 1, channelAtoms);
		} else {
			channelAtoms = this.books.get(channelNumber - 1);
		}

		while (channelAtoms.size() < offsetNumber) {
			channelAtoms.add(null);
		}
		channelAtoms.set(offsetNumber - 1,
						 atoms.toArray(new NewStagerBookAtom[atoms.size()]));
	}

	private NewStagerBookAtom readSingleAtom()
	throws NewStagerBookReaderException {
		this.readSegmentCode();

		int atomSize = this.fileBuffer.get();

		switch (this.lastCode) {
		case StandardBookAtom.GABORWAVE_IDENTITY:
			AssertAtomSizeWorkaround(atomSize, 6 * FLOAT_SIZE);
			return NewStagerBookAtom.CreateGaborWave(
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat(),
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat(),
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat());
		case StandardBookAtom.DIRACDELTA_IDENTITY:
			AssertAtomSizeWorkaround(atomSize, 3 * FLOAT_SIZE);
			return NewStagerBookAtom.CreateDiracDelta(
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat(),
					   this.fileBuffer.getFloat());
		case StandardBookAtom.GAUSSFUNCTION_IDENTITY:
			AssertAtomSizeWorkaround(atomSize, 4 * FLOAT_SIZE);
			return NewStagerBookAtom.CreateGaussFunction(
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat(),
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat());
		case StandardBookAtom.SINCOSWAVE_IDENTITY:
			AssertAtomSizeWorkaround(atomSize, 4 * FLOAT_SIZE);
			return NewStagerBookAtom.CreateSinCosWave(
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat(),
					   this.fileBuffer.getFloat(), this.fileBuffer.getFloat());
		default:
			throw new NewStagerBookReaderException("Unknown atom code: "
												   + this.lastCode);
		}
	}

	private void readFileHeader() throws NewStagerBookReaderException {
		if (this.lastCode != FormatComponentV5.FILE_HEADER) {
			return;
		}

		int size = this.fileBuffer.getInt();
		int pos = 0;

		while (pos < size) {
			this.readSegmentCode();
			byte sizeOfField = this.fileBuffer.get();

			pos += sizeOfField + 2;

			switch (this.lastCode) {
			case FormatComponentV5.SIGNAL_INFO:
				AssertSize(sizeOfField, 2 * FLOAT_SIZE + SHORT_SIZE);
				this.signalInfo = new NewStagerSignalInfo(
					this.fileBuffer.getFloat(), this.fileBuffer.getFloat(),
					this.fileBuffer.getShort());
				break;
			case FormatComponentV5.DECOMP_INFO:
				AssertSize(sizeOfField, 3 * FLOAT_SIZE + BYTE_SIZE);
				this.decomposingInfo = new NewStagerDecomposingInfo(
					this.fileBuffer.getFloat(), this.fileBuffer.getInt(),
					this.fileBuffer.getInt(), (char) this.fileBuffer.get());
				break;
			case FormatComponentV5.WEB_SITE_INFO:
				// skip; //$FALL-THROUGH$
			case FormatComponentV5.TEXT_INFO:
				// skip; //$FALL-THROUGH$
			case FormatComponentV5.DATE_INFO:
				// skip; //$FALL-THROUGH$
			default:
				this.fileBuffer.position(this.fileBuffer.position()
										 + sizeOfField);
			}
		}
	}

	private void skipMagicAndComment() {
		this.fileBuffer.position(this.fileBuffer.position() + 6);
		this.readSegmentCode();
		if (this.lastCode == FormatComponentV5.COMMENT_SEGMENT_IDENTITY) {
			this.fileBuffer.position(this.fileBuffer.getInt()
									 + this.fileBuffer.position());
			this.readSegmentCode();
		}
	}

	private void readSegmentCode() {
		this.lastCode = this.fileBuffer.get();
	}

	private static void AssertSize(int size, int... desiredSizes)
	throws NewStagerBookReaderException {
		for (int desiredSize : desiredSizes) {
			if (size == desiredSize) {
				return;
			}
		}
		
		throw new NewStagerBookReaderException("Bad field size: " + size
												   + " expected one of: " + Arrays.toString(desiredSizes));
		
	}
	
	private static void AssertAtomSizeWorkaround(int atomSize, int desiredSize)
			throws NewStagerBookReaderException {
		AssertSize(atomSize, desiredSize + 2 * BYTE_SIZE, desiredSize);
	}


}
