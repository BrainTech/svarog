package pl.edu.fuw.MP.Core;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import org.signalml.domain.book.StandardBookAtom;

public class NewAtom implements StandardBookAtom {
	public float scale;
	public float frequency;
	public int   position;
	public float modulus;
	public float amplitude;
	public float phase;
	public int   index;
	private boolean versionIV;

	public NewAtom() {
		versionIV=false;
	}

	public NewAtom(int v) {
		if(v==NewBookLibrary.VERSION_IV) {
		   versionIV=true;
		} else {
		   versionIV=false;
		}
	}

	public final void export(BookAtom atom) {
		atom.frequency=frequency;
		atom.position=(short)position;
		atom.modulus=modulus;
		atom.amplitude=amplitude;
		atom.phase=phase;
		atom.scale=(int)(0.5+scale);
		atom.index=index;
	}

	public final static int sizeOf() {
		return 6*4;
	}

	public void Read(DataArrayInputStream stream) throws IOException {
		scale=versionIV ? stream.readFloat() : (float)stream.readInt();
		frequency=versionIV ? stream.readFloat() : (float)stream.readInt();
		position=(int)(0.5F+(versionIV ? stream.readFloat()
									   : (float)stream.readInt()));
		modulus=stream.readFloat();
		amplitude=stream.readFloat();
		phase=stream.readFloat();
	}

	public float getAmplitude() {
		return this.amplitude;
	}

	public int getFrequency() {
		return (int)this.frequency;
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
		if(scale==0.0) {
		   return DIRACDELTA_IDENTITY; 	
		}
		return GABORWAVE_IDENTITY;
	}

	public float getIFrequency() {
		return this.frequency;
	}

	@Override
	public int getBaseLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHzFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIteration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSamplingFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getTimePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getTimeScale() {
		// TODO Auto-generated method stub
		return 0;
	}
}
