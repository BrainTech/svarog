package org.signalml.codec.precompiled;

import org.signalml.codec.SMLCodec;
import org.signalml.codec.generator.xml.XMLCodecException;

public class RAW extends SMLCodec {

	// ---- BEGIN USER CODE ---



	// ---- END USER CODE   ---

	public String getFormatID() {
	   return "RAW";
	}

	public String getFormatDescription() {
	   return "Przykladowy format testowy";
	}

	public RAW() throws XMLCodecException {
	   super();
	}
	
	public void open(String name) throws XMLCodecException {
	   super.open(name);
	   if(!getConstraints()) {
		  throw new XMLCodecException("CONSTRAINTS ERROR !");
	   }
	}
	public boolean is_number_of_channels() {
		return true;
	}
	
	public short get_number_of_channels() throws XMLCodecException {
		short theResult=read_short(4);
		return theResult;
	}
	
	public boolean is_sampling_frequency() {
		return true;
	}
	
	public float get_sampling_frequency() throws XMLCodecException {
		float theResult=read_float(6);
		return theResult;
	}
	
	public boolean is_calibration() {
		return false;
	}
	
	public boolean is_channel_names() {
		return false;
	}
	

	public float get_magic2() throws XMLCodecException {
		short theResult=read_short(6);
		return to_float(theResult/100.0F);
	}
	
	public String get_magic() throws XMLCodecException {
		String theResult=read_String(0, 4);
		return theResult;
	}
	

	private boolean getConstraints() throws XMLCodecException {
	   if(!(get_magic().equals("RAW1"))) {
		 return false;
	   }
	   return true;
	}

	public float[] getSample(long offset) throws XMLCodecException {
	   return getMultiplexSample_short(10, offset, get_number_of_channels());
	}

	public int get_max_offset() throws XMLCodecException {
	   long __offset__=(getFileLength()-(10))/(get_number_of_channels()*2);
	   return (int)__offset__;
	}
	

	public float getChannelSample(long offset, int chn) throws XMLCodecException {
	   return getMultiplexChannelSample_short(10, offset, chn, get_number_of_channels());
	}
}

