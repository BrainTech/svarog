package org.signalml.codec.precompiled;

import org.signalml.codec.SMLCodec;
import org.signalml.codec.generator.xml.XMLCodecException;

public class EASYS extends SMLCodec {

	// ---- BEGIN USER CODE ---

	private int	 offset_value;
	   private boolean offset_value_set=false;
	  
	   private int easys_offset(int haveEx, int data_offset, int chn) throws XMLCodecException {
		 if(offset_value_set==false) {
		   if(haveEx==0) {
			  throw new XMLCodecException("ex. header not found !");
		   }
		   
		   int offset=32;
		   for( ; ; ) {
			  char  code1=(char)read_byte(offset), code2=(char)read_byte(offset+1);
			  short size=read_short(offset+2);
			  
			  offset+=4;
			  if(code1=='C' && code2=='N') {
				 break;
			  } else if(code1=='\0' && code2=='\0') {
				 throw new XMLCodecException("channel names not found !");
			  }
			  offset+=size;
			  if(offset > 16*data_offset) {
				 throw new XMLCodecException("channel names not found !");
			  }
		   }
		   
		   offset_value_set=true;
		   offset_value=offset;
		   //System.out.println(" OFFSET " + (16*offset));
		   return offset;
		 } else {
		   return offset_value;
		 }
	   }

	// ---- END USER CODE   ---

	public String getFormatID() {
	   return "EASYS";
	}



	public EASYS() throws XMLCodecException {
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
	
	private byte cached_number_of_channels = -1;
	public byte get_number_of_channels() throws XMLCodecException {
		if( cached_number_of_channels < 0 ) {
			cached_number_of_channels = read_byte(16);
		}
		return cached_number_of_channels;
	}
	
	public boolean is_sampling_frequency() {
		return true;
	}
	
	public float get_sampling_frequency() throws XMLCodecException {
		short theResult=read_short(18);
		return to_float(theResult);
	}
	
	public boolean is_calibration() {
		return false;
	}
	
	public boolean is_channel_names() {
		return true;
	}
	
	public String[] get_channel_names() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for(int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(easys_offset(get_ext_hdr(),get_data_offset(), get_number_of_channels())+4*(index-1), 4);
		}
		return theResult;
	}
	

	public String get_magic() throws XMLCodecException {
		String theResult=read_String(0, 6);
		return theResult;
	}
	
	private short cached_data_offset = -1;
	public short get_data_offset() throws XMLCodecException {
		if( cached_data_offset < 0 ) {
			cached_data_offset = read_short(28);
		}
		return cached_data_offset;
	}
	
	private int cached_ext_hdr = -1;
	public int get_ext_hdr() throws XMLCodecException {
		if( cached_ext_hdr < 0 ) {
			cached_ext_hdr = read_int(30);
		}
		return cached_ext_hdr;
	}
	
	private int cached_SamplesInFile = -1;
	public int get_SamplesInFile() throws XMLCodecException {
		if( cached_SamplesInFile < 0 ) {
			cached_SamplesInFile = read_int(20);
		}
		return cached_SamplesInFile;
	}
	

	private boolean getConstraints() throws XMLCodecException {
	   if(!(get_magic().equals("EASREC"))) {
		 return false;
	   }
	   return true;
	}

	public float[] getSample(long offset) throws XMLCodecException {
	   return getMultiplexSample_short(16*get_data_offset(), offset, get_number_of_channels());
	}

	public int get_max_offset() throws XMLCodecException {
		return get_SamplesInFile()-1;
	}
	

	public float getChannelSample(long offset, int chn) throws XMLCodecException {
	   return getMultiplexChannelSample_short(16*get_data_offset(), offset, chn, get_number_of_channels());
	}
}
