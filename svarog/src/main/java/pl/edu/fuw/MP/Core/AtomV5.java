package pl.edu.fuw.MP.Core;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import org.signalml.domain.book.StandardBookAtom;
//import org.signalml.domain.book.StandardBookAtomWriter;
import org.signalml.domain.book.StandardBookAtomWriter;

public class AtomV5 implements StandardBookAtom, StandardBookAtomWriter {
	public int   iteration=0;
	public float modulus=0.0F;
	public float amplitude=0.0F;
	public float position=0.0F;
	public float scale=0.0F;
	public float frequency=0.0F;
	public float phase=0.0F;
	public int type=0;
	public int sizeOfAtomsField=0;
	public boolean conv=false;

	public int baseSize;
	public float samplingFreq;

	public AtomV5() {}

	public AtomV5(Object o) {
		AtomV5 atom = (AtomV5) o;
		this.iteration = atom.iteration;
		this.modulus = atom.modulus;
		this.amplitude = atom.amplitude;
		this.position = atom.position;
		this.scale = atom.scale;
		this.frequency = atom.frequency;
		this.phase = atom.phase;
		this.type = atom.type;
		this.sizeOfAtomsField = atom.sizeOfAtomsField;
		this.conv = atom.conv;
		this.baseSize = atom.baseSize;
		this.samplingFreq = atom.samplingFreq;
	}

	public int Size() {
		return sizeOfAtomsField;
	}

	public int SizeOfAtom() {
		int size=0;

		switch (type) {
		case DIRACDELTA_IDENTITY:
			size=3;
			break;
		case SINCOSWAVE_IDENTITY:
		case GAUSSFUNCTION_IDENTITY:
			size=4;
			break;
		case GABORWAVE_IDENTITY:
			size=6;
			break;
		}
		return 4*size + 2;
	}

	public void Read(RandomAccessFile stream) throws IOException {
		type=(int)stream.readByte();
		int sizeOfAtom=(int)(stream.readByte())&0xff;

		sizeOfAtomsField=2+sizeOfAtom;
		switch (type) {
		case DIRACDELTA_IDENTITY:
			modulus=stream.readFloat();
			amplitude=stream.readFloat();
			position=stream.readFloat();
			break;
		case GAUSSFUNCTION_IDENTITY:
			modulus=stream.readFloat();
			amplitude=stream.readFloat();
			position=stream.readFloat();
			scale=stream.readFloat();
			break;
		case SINCOSWAVE_IDENTITY:
			modulus=stream.readFloat();
			amplitude=stream.readFloat();
			frequency=stream.readFloat();
			phase=stream.readFloat();
			break;
		case GABORWAVE_IDENTITY:
			modulus=stream.readFloat();
			amplitude=stream.readFloat();
			position=stream.readFloat();
			scale=stream.readFloat();
			frequency=stream.readFloat();
			phase=stream.readFloat();
			break;
		}
	}

	public float getAmplitude() {
		return this.amplitude;
	}

	public int getNaturalFrequency() {
		return (int)(this.frequency * getBaseLength() / 2.0);
	}

	public float getFrequency() {
		return this.frequency;
	}

	public float getModulus() {
		return this.modulus;
	}

	public float getPhase() {
		return this.phase;
	}

	public int getPosition() {
		return (int)this.position;
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("No properties");
	}

	public Enumeration<String> getPropertyNames() {
		Vector<String> names = new Vector<String>();
		return names.elements();
	}

	public int getScale() {
		return (int)this.scale;
	}

	public int getType() {
		return this.type;
	}

	public void setAmplitude(float amplitude) {
		this.amplitude=amplitude;
	}

	public void setFrequency(float freq) {
		this.frequency=freq;

	}

	public void setModulus(float modulus) {
		this.modulus=modulus;
	}

	public void setPhase(float phase) {
		this.phase=phase;
	}

	public void setPosition(float position) {
		this.position=position;
	}

	public void setScale(float scale) {
		this.scale=scale;

	}

	public void setType(int type) {
		this.type=type;
	}

	public void set(StandardBookAtom atom) {
		if (atom!=null) {
			setAmplitude(atom.getAmplitude());
			setFrequency(atom.getFrequency());
			setModulus(atom.getModulus());
			setPhase(atom.getPhase());
			setPosition(atom.getPosition());
			setScale(atom.getScale());
			setType(atom.getType());
		}
	}

	public void Write(DataOutputStream stream) throws IOException  {
		stream.writeByte(type);
		stream.writeByte((byte)SizeOfAtom());

		switch (type) {
		case DIRACDELTA_IDENTITY:
			stream.writeFloat(modulus);
			stream.writeFloat(amplitude);
			stream.writeFloat(position);
			break;
		case GAUSSFUNCTION_IDENTITY:
			stream.writeFloat(modulus);
			stream.writeFloat(amplitude);
			stream.writeFloat(position);
			stream.writeFloat(scale);
			break;
		case SINCOSWAVE_IDENTITY:
			stream.writeFloat(modulus);
			stream.writeFloat(amplitude);
			stream.writeFloat(frequency);
			stream.writeFloat(phase);
			break;
		case GABORWAVE_IDENTITY:
			stream.writeFloat(modulus);
			stream.writeFloat(amplitude);
			stream.writeFloat(position);
			stream.writeFloat(scale);
			stream.writeFloat(frequency);
			stream.writeFloat(phase);
			break;
		}
	}

	@Override
	public int getBaseLength() {
		return baseSize;
	}

	@Override
	public float getHzFrequency() {
		return getNaturalFrequency()*getSamplingFrequency()/getBaseLength();
	}

	@Override
	public int getIteration() {
		return iteration;
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFreq;
	}

	@Override
	public float getTimePosition() {
		return position*getSamplingFrequency();
	}

	@Override
	public float getTimeScale() {
		return scale*getSamplingFrequency();
	}

}
