package pl.edu.fuw.MP.Core;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

import org.signalml.domain.book.StandardBookAtom;

public class BookAtom implements StandardBookAtom {
	public short number_of_atom_in_book;
	public byte  octave;
	public byte  type;
	public float frequency;
	public float position;
	public float modulus;
	public float amplitude;
	public float phase;
	public float truePhase;
	public float scale;
	public int   index;

	public final void Read(DataArrayInputStream stream)
	throws IOException
	{
		number_of_atom_in_book=stream.readShort();
		octave=stream.readByte();
		if (octave!=0)
			scale=1<<octave;
		else
			scale=0;
		type=stream.readByte();
		frequency=stream.readShort();
		position=stream.readShort();
		modulus=stream.readFloat();
		amplitude=stream.readFloat();
		phase=stream.readFloat();
	}

	public BookAtom() {
		;
	}

	public BookAtom(NewAtom atom) {
		atom.export(this);
	}

	public void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) { }
	}

	public float Frequency(BookHeader head) {
		return (frequency/(float)(head.signal_size)*head.FREQUENCY);
	}

	public float NaturalFreq(BookHeader head) {
		return (float)(2.0*Math.PI*frequency/head.signal_size);
	}

	public final float HalfTime(BookHeader head) {
		final double Const=2.0*Math.sqrt(Math.log(2.0)/Math.PI);
		if (scale==0)
			return 0.0F;
		if (scale==head.signal_size)
			return (scale/head.FREQUENCY);
		return (float)(Const*scale/head.FREQUENCY);
	}

	private static String Format(String str,int Width) {
		String string=new String(str);
		for (int i=string.length() ; i<=Width ; i++)
			string+=" ";
		return string;
	}

	private static float Round(float x,int Mul) {
		double factor=Math.pow(10.0,Mul);
		return (float)(Math.floor(0.5+x*factor)/factor);
	}

	public String getAtomString(BookHeader head,float SecPP) {
		int hour,minute,sec;
		float sec_time;
		sec_time=head.file_offset*SecPP+position/head.FREQUENCY;
		hour=(int)(sec_time/3600.0F);
		minute=(int)((sec_time-3600.0F*hour)/60.0F);
		sec=(int)(sec_time-60.0F*minute-3600.0F*hour);

		return new String(Format(hour+"h"+((minute<10) ? "0"+minute : ""+minute)
		                         +"'"+((sec<10) ? "0"+sec : ""+sec)+"\" ",9)+
		                  Format(number_of_atom_in_book+" ",9)+
		                  Format(position+" ",9)+
		                  Format(Round(modulus,3)+" ",9)+
		                  Format(Round(amplitude,3)+" ",9)+
		                  Format(scale+" ",9)+
		                  Format(Round(HalfTime(head),2)+"\" ",9)+
		                  Format(Round(Frequency(head),4)+" Hz ",9)+
		                  Format((phase>=0.0 ? " " : "")+Round(phase,4)+" ",9)+
		                  Format((truePhase>=0.0 ? " " : "")+
		                         Round(truePhase,4)+" ",9));
	}

	public String toString() {
		return   Format(position+" ",9)+
		         Format(Round(modulus,3)+" ",9)+
		         Format(Round(amplitude,3)+" ",9)+
		         Format(scale+" ",9)+
		         Format(Round(frequency,3)+" ", 9)+
		         Format((phase>=0.0 ? " " : "")+Round(phase,4)+" ",9);

	}

	public float getAmplitude() {
		return amplitude;
	}

	public int getNaturalFrequency() {
		return (int)this.frequency;
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

