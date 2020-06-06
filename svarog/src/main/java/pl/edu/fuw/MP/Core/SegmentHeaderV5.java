package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Vector;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookAtomWriter;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.domain.book.StandardBookSegmentWriter;

public class SegmentHeaderV5 extends FormatComponentV5 implements StandardBookSegment, StandardBookSegmentWriter {
	public String comment = null;
	public int size;
	public int offsetNumber;
	public int offsetDimension;
	public int channelNumber;
	public float signal[] = null;
	public Vector<AtomV5> atoms = new Vector<AtomV5>();
	public BookLibraryV5 parent = null;

	public SegmentHeaderV5() {
	}

	public SegmentHeaderV5(BookLibraryV5 bookLibraryV5) {
		parent = bookLibraryV5;
	}

	public void skipSegment(RandomAccessFile stream) throws IOException {
		type = (int) stream.readByte();
		size = stream.readInt();

		Utils.log("READ_SEG: " + type + " " + FormatComponentV5.toName(type) + " SIZE: " + size + " POS: " + stream.getFilePointer());

		switch (type) {
		default:
		case COMMENT_SEGMENT_IDENTITY:
			stream.skipBytes(size);
			break;

		case ATOMS_SEGMENT_IDENTITY:
		case SIGNAL_SEGMENT_IDENTITY:
			channelNumber = stream.readShort();
			stream.skipBytes(size - 2);
			break;
		case OFFSET_SEGMENT_IDENTITY:
			offsetNumber = stream.readShort();
			offsetDimension = stream.readInt();

			parent.setDimBase(offsetDimension);

			int codeOfSecondarySegment = (int) stream.readByte();
			int sizeOfSecondaryDataSegment = stream.readInt();

			switch (codeOfSecondarySegment) {
			case ATOMS_SEGMENT_IDENTITY:
			case SIGNAL_SEGMENT_IDENTITY:
				channelNumber = stream.readShort();
				stream.skipBytes(sizeOfSecondaryDataSegment - 2);
				break;
			}
		}
	}

	protected void readAtomsSegment(RandomAccessFile stream, int segmentSize) throws IOException {
		channelNumber = stream.readShort();

		Utils.log("ATOMS_SEGMENT_IDENTITY: " + channelNumber);

		atoms = new Vector<AtomV5>();
		int pos = 2;

		while (pos < segmentSize) {
			AtomV5 atom = new AtomV5();

			atom.Read(stream);

			pos += atom.Size();
			atoms.add(atom);
		}
		Utils.log("end: " + pos);
	}

	protected void readSignalSegment(RandomAccessFile stream, int segmentSize) throws IOException {
		channelNumber = stream.readShort();

		int len = (segmentSize - 2) / 4;

		Utils.log("SIGNAL_SEGMENT_IDENTITY: " + len);

		signal = new float[len];
		for (int i = 0; i < len; i++) {
			signal[i] = stream.readFloat();
		}
	}

	protected void readCommentSegment(RandomAccessFile stream, int segmentSize) throws IOException {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < segmentSize; i++) {
			char c = (char) stream.readByte();
			if (c != '\0') {
				buff.append(c);
			}
		}
		comment = buff.toString();

		Utils.log("COMMENT_SEGMENT_IDENTITY: " + comment);
	}

	public void read(RandomAccessFile stream) throws IOException {
		type = (int) stream.readByte();
		size = stream.readInt();

		Utils.log("READ_SEG: " + type + " " + FormatComponentV5.toName(type) + " " + size + " POS: " + stream.getFilePointer());

		switch (type) {
		case ATOMS_SEGMENT_IDENTITY:
			readAtomsSegment(stream, size);
			break;
		case SIGNAL_SEGMENT_IDENTITY:
			readSignalSegment(stream, size);
			break;
		case COMMENT_SEGMENT_IDENTITY:
			readCommentSegment(stream, size);
			break;
		case OFFSET_SEGMENT_IDENTITY:
			offsetNumber = stream.readShort();
			offsetDimension = stream.readInt();

			parent.setDimBase(offsetDimension);

			Utils.log("offsetNumber: " + offsetNumber + " " + "offsetDimension: " + offsetDimension);

			int codeOfSecondarySegment = (int) stream.readByte();
			int sizeOfSecondaryDataSegment = stream.readInt();

			Utils.log("SEG:" + codeOfSecondarySegment + " " + FormatComponentV5.toName(codeOfSecondarySegment) + " " + sizeOfSecondaryDataSegment);

			switch (codeOfSecondarySegment) {
			case SIGNAL_SEGMENT_IDENTITY:
				type = codeOfSecondarySegment;
				readSignalSegment(stream, sizeOfSecondaryDataSegment);
				break;
			case ATOMS_SEGMENT_IDENTITY:
				type = codeOfSecondarySegment;
				readAtomsSegment(stream, sizeOfSecondaryDataSegment);
				break;
			}
		}
	}

	public StandardBookAtom getAtomAt(int index) {
		AtomV5 atom = atoms.elementAt(index);

		if (atom.conv == false) {
			atom.conv = true;
			atom.iteration = index;
			atom.baseSize = parent.getDimBase();
			atom.samplingFreq = parent.getSamplingFreq();
			atom.amplitude *= parent.getConvFactor();
		}
		return atom;
	}

	public int getAtomCount() {
		return atoms.size();
	}

	public int getChannelNumber() {
		return channelNumber;
	}

	public float getDecompositionEnergy() {
		return -1.0F;
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("No properties");
	}

	public Enumeration<String> getPropertyNames() {
		Vector<String> names = new Vector<String>();
		return names.elements();
	}

	public int getSegmentLength() {
		return parent.getDimBase();
	}

	public int getSegmentNumber() {
		return offsetNumber;
	}

	public float getSegmentTime() {
		return offsetNumber / parent.getSamplingFreq();
	}

	public float getSignalEnergy() {
		return -1.0F;
	}

	public float[] getSignalSamples() {
		return signal;
	}

	public boolean hasSignal() {
		return signal != null;
	}

	public int indexOfAtom(StandardBookAtom atom) {
		return atoms.indexOf(atom);
	}

	public void setSignalSamples(float[] signalSamples) {
		signal = signalSamples;
	}

	public void addAtom(StandardBookAtomWriter atom) {
		atoms.addElement((AtomV5) atom);
	}

	public void clearAtoms() {
		atoms.clear();
	}

	public void setChannelNumber(int channel) {
		this.channelNumber = channel;
	}

	public void setSegmentNumber(int seg) {
		this.offsetNumber = seg;
	}

	private int getSizeOfAtoms() {
		int sum = 0, len = atoms.size();
		for (int i = 0; i < len; i++) {
			sum += 2 + atoms.elementAt(i).SizeOfAtom();
		}
		return sum;
	}

	private int getSizeOfSegment() {
		int sum = 0;

		if (signal != null) {
			sum += 4 * signal.length + 1 + 2 + 4;
		}
		if (atoms.size() != 0) {
			sum += getSizeOfAtoms() + 1 + 2 + 4;
		}
		return sum;
	}

	public int getSize() {
		return getSizeOfSegment();
	}

	public void Write(DataOutputStream stream) throws IOException {
		if (comment != null) {
			stream.writeByte(COMMENT_SEGMENT_IDENTITY);
			stream.writeInt(comment.length());
			stream.writeBytes(comment);
		}

		if (signal != null || atoms.size() != 0) {
			stream.writeByte(OFFSET_SEGMENT_IDENTITY);
			stream.writeInt(6 + getSizeOfSegment());
			stream.writeShort(this.offsetNumber);
			stream.writeInt(this.offsetDimension);
		}

		if (signal != null) {
			stream.writeByte(SIGNAL_SEGMENT_IDENTITY);
			stream.writeInt(4 * signal.length + 2);
			stream.writeShort(channelNumber);

			for (int i = 0; i < signal.length; i++) {
				stream.writeFloat(signal[i]);
			}
		}

		int len = atoms.size();
		if (len != 0) {
			stream.writeByte(ATOMS_SEGMENT_IDENTITY);
			stream.writeInt(getSizeOfAtoms() + 2);
			stream.writeShort(channelNumber);

			for (int i = 0; i < len; i++) {
				AtomV5 atom = atoms.elementAt(i);
				atom.Write(stream);
			}
		}
	}

	@Override
	public float getSamplingFrequency() {
		return parent.getSamplingFreq();
	}

	@Override
	public float getSegmentTimeLength() {
		return (float)(parent.getDimBase() / getSamplingFrequency());
	}

	public void setSegmentLength(int offsetDimension) {
		this.offsetDimension = offsetDimension;
	}
}
