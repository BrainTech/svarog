package pl.edu.fuw.MP.Core;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;

public class BookHeader implements StandardBookSegment {
	public short file_offset;
	public short book_size;
	public int signal_size;
	public float points_per_micro_V;
	public float FREQUENCY;
	public float signal_energy;
	public float book_energy;
	private Vector<BookAtom> atoms=new Vector<BookAtom>();

	public void addAtom(BookAtom atom) {
		atoms.addElement(atom);
	}

	public final void Read(RandomAccessFile file) throws IOException {
		file_offset = file.readShort();
		book_size = file.readShort();
		signal_size = file.readInt();
		points_per_micro_V = (float) Math.abs(file.readFloat());
		FREQUENCY = (float) Math.abs(file.readFloat());
		signal_energy = file.readFloat();
		book_energy = file.readFloat();
	}

	public String getString() {
		return new String("\nFile offset    : " + file_offset
		                  + "\nBook size      : " + book_size + "\nSignal size    : "
		                  + signal_size + "\nConv factor    : " + points_per_micro_V
		                  + "\nFrequency      : " + FREQUENCY + "\nSignal energy  : "
		                  + signal_energy + "\nBook   energy  : " + book_energy);
	}

	public StandardBookAtom getAtomAt(int index) {
		return atoms.elementAt(index);
	}

	public int getAtomCount() {
		return atoms.size();
	}

	public int getChannelNumber() {
		return -1;
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
		return signal_size;
	}

	public int getSegmentNumber() {
		return file_offset;
	}

	public float getSegmentTime() {
		return (file_offset * signal_size) / FREQUENCY;
	}

	public float getSignalEnergy() {
		return signal_energy;
	}

	public float[] getSignalSamples() {
		return null;
	}

	public boolean hasSignal() {
		return false;
	}

	public int indexOfAtom(StandardBookAtom atom) {
		return atoms.indexOf(atom);
	}

	public void setSignalSamples(float[] signalSamples) {
		;
	}

	public void reset() {
		atoms.clear();
	}

	public double getSampliegFreq() {
		return FREQUENCY;
	}

	@Override
	public float getSamplingFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSegmentTimeLength() {
		// TODO Auto-generated method stub
		return 0;
	}
}
