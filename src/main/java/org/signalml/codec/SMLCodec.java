/* Signal Copyright (C) 2003 Dobieslaw Ircha	<dircha@eranet.pl> 
							  Artur Biesiadowski <abies@adres.pl> 
							  Piotr J. Durka	 <Piotr-J.Durka@fuw.edu.pl>
							  
	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
	
	Linking Signal statically or dynamically with other modules is making a
	combined work based on Signal.  Thus, the terms and conditions of the GNU
	General Public License cover the whole combination.

	As a special exception, the copyright holders of Signal give you
	permission to link Signal with independent modules that communicate with
	Signal solely through the SignalBAR interface, regardless of the license
	terms of these independent modules, and to copy and distribute the
	resulting combined work under terms of your choice, provided that
	every copy of the combined work is accompanied by a complete copy of
	the source code of Signal (the version of Signal used to produce the
	combined work), being distributed under the terms of the GNU General
	Public License plus this exception.  An independent module is a module
	which is not derived from or based on Signal.

	Note that people who make modified versions of Signal are not obligated
	to grant this special exception for their modified versions; it is
	their choice whether to do so.  The GNU General Public License gives
	permission to release a modified version without this exception; this
	exception also makes it possible to release a modified version which
	carries forward this exception.
*/

package org.signalml.codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.signalml.codec.generator.xml.XMLCodecException;

public abstract class SMLCodec {
	private RandomAccessFile file=null;
	private ByteOrder byteOrder=ByteOrder.LITTLE_ENDIAN ;

	public abstract float[] getSample(long offset) throws XMLCodecException;
	public abstract float getChannelSample(long offset, int chn) throws XMLCodecException;
	
	private ByteBuffer shortBuffer;
	private byte[] shortByteArray;
	
	public void init() throws XMLCodecException {
	}
/*
	private void check() throws XMLCodecException {
	   if(file==null) {
		  throw new XMLCodecException("file not open");
	   }
	}
*/
	public SMLCodec() {
	}
	
	public final void setByteOrder(String order) {
	   if(order.equals("intel")) {
		  this.byteOrder=ByteOrder.LITTLE_ENDIAN;
	   } else {
		  this.byteOrder=ByteOrder.BIG_ENDIAN;
	   }
	}
	
	public String getFormatDescription() {
	  return "SMLCodec: generic format";
	}
	
	public void open(String filename) throws XMLCodecException {
	  try {
	  	 file=null;
		 file=new RandomAccessFile(filename, "r");
		 init();
	  } catch(IOException e) {
	  	try {
	  	   if(file!=null) file.close();
		} catch(IOException ee) {
			;
		}
		throw new XMLCodecException(e);
	  }
	}
	
	public void close() throws XMLCodecException {
	  try {
		if(file!=null) {
		   file.close();
		   file=null;
		}
	  } catch(IOException e) {
		throw new XMLCodecException(e);
	  }
	}
	
	public String read_String(long offset, int width) throws XMLCodecException {
	   //check();
	   try {
		 byte []buffor =new byte[width];
		 
		 file.seek(offset);
		 file.read(buffor, 0, width);
		 return new String(buffor); 
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	public short read_short(long offset) throws XMLCodecException {
	   //check();
		if( shortBuffer == null ) {
			shortByteArray = new byte[2];
			shortBuffer = ByteBuffer.wrap(shortByteArray).order(byteOrder);
		}
		try {
			shortBuffer.clear();
			file.seek(offset);
			file.read(shortByteArray, 0, 2);   
			return shortBuffer.getShort(); 
		  } catch(IOException e) {
			throw new XMLCodecException(e);
		  }
		/*
	   try {
		  byte []buffor =new byte[2];
		  
		  file.seek(offset);
		  file.read(buffor, 0, 2);   
		  return ByteBuffer.wrap(buffor).order(byteOrder).getShort(); 
		} catch(IOException e) {
		  throw new XMLCodecException(e);
		}
		*/
	}
	
	public short[] read_short_array(long offset, int size) throws XMLCodecException {
		//check();
		try {
		 int	len=2*size;
		 byte []buffor =new byte[len];
		 
		 file.seek(offset);
		 file.read(buffor, 0, len); 
		 short dest[]=new short[size];
		 ByteBuffer.wrap(buffor).order(byteOrder).asShortBuffer().get(dest);
		 return dest;
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	public int read_int(long offset) throws XMLCodecException {
	   //check();
	   try {
		  byte []buffor =new byte[4];
		  
		  file.seek(offset);
		  file.read(buffor, 0, 4);   
		  return ByteBuffer.wrap(buffor).order(byteOrder).getInt(); 
		} catch(IOException e) {
		  throw new XMLCodecException(e);
		}
	}
	
	public int[] read_int_array(long offset, int size) throws XMLCodecException {
	   //check();
	   try {
		 int	len=4*size;
		 byte []buffor =new byte[len];
		 
		 file.seek(offset);
		 file.read(buffor, 0, len); 
		 int dest[]=new int[size];
		 ByteBuffer.wrap(buffor).order(byteOrder).asIntBuffer().get(dest);
		 return dest;
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	public float read_float(long offset) throws XMLCodecException {
	   //check();
	   try {
		  byte []buffor =new byte[4];
		  
		  file.seek(offset);
		  file.read(buffor, 0, 4);   
		  return ByteBuffer.wrap(buffor).order(byteOrder).getFloat(); 
		} catch(IOException e) {
		  throw new XMLCodecException(e);
		}
	}
	
	public float[] read_float_array(long offset, int size) throws XMLCodecException {
	   //check();
	   try {
		 int	len=4*size;
		 byte []buffor =new byte[len];
		 
		 file.seek(offset);
		 file.read(buffor, 0, len); 
		 float dest[]=new float[size];
		 ByteBuffer.wrap(buffor).order(byteOrder).asFloatBuffer().get(dest);
		 return dest;
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	public double read_double(long offset) throws XMLCodecException {
		//check();
		try {
		  byte []buffor =new byte[8];
		  
		  file.seek(offset);
		  file.read(buffor, 0, 8);   
		  return ByteBuffer.wrap(buffor).order(byteOrder).getDouble(); 
		} catch(IOException e) {
		  throw new XMLCodecException(e);
		}
	}
	
	public double[] read_double_array(long offset, int size) throws XMLCodecException {
	   //check();
	   try {
		 int	len=8*size;
		 byte []buffor =new byte[len];
		 
		 file.seek(offset);
		 file.read(buffor, 0, len); 
		 double dest[]=new double[size];
		 ByteBuffer.wrap(buffor).order(byteOrder).asDoubleBuffer().get(dest);
		 return dest;
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	public byte read_byte(long offset) throws XMLCodecException {
	   //check();
	   try {
		 byte []buffor =new byte[1];
		 file.seek(offset);
		 file.read(buffor, 0, 1); 
		 return buffor[0];
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	public byte[] read_byte_array(long offset, int size) throws XMLCodecException {
	   //check();
	   try {
		 byte []buffor =new byte[size];
		 file.seek(offset);
		 file.read(buffor, 0, size); 
		 return buffor;
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	public long read_long(long offset) throws XMLCodecException {
	   //check();
	   try {
		  byte []buffor =new byte[8];
		  
		  file.seek(offset);
		  file.read(buffor, 0, 8);   
		  return ByteBuffer.wrap(buffor).order(byteOrder).getLong(); 
		} catch(IOException e) {
		  throw new XMLCodecException(e);
		}
	}
	
	public long[] read_long_array(long offset, int size) throws XMLCodecException {
	   //check();
	   try {
		 int	len=8*size;
		 byte []buffor =new byte[len];
		 
		 file.seek(offset);
		 file.read(buffor, 0, len); 
		 long dest[]=new long[size];
		 ByteBuffer.wrap(buffor).order(byteOrder).asLongBuffer().get(dest);
		 return dest;
	   } catch(IOException e) {
		 throw new XMLCodecException(e);
	   }
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	public float[] getMultiplexSample_short(long offset, long off, int chn) throws XMLCodecException {
	   short []arr=read_short_array(offset+2*off, chn);
	   int len=arr.length;
	   float []res=new float[len];
	   
	   for(int i=0 ; i<len ; i++)
		   res[i]=(float)arr[i];
	   return res;
	}
	
	public float[] getMultiplexSample_float(long offset, long off, int chn) throws XMLCodecException {
	   return read_float_array(offset+4*off, chn);
	}
	
	public float[] getMultiplexSample_int(long offset, long off, int chn) throws XMLCodecException {
	   int []arr=read_int_array(offset+4*off, chn);
	   int len=arr.length;
	   
	   float []res=new float[len];
	   for(int i=0 ; i<len ; i++)
		   res[i]=(float)arr[i];
	   return res;
	}
	
	public float[] getMultiplexSample_double(long offset,long off, int chn) throws XMLCodecException {
	   double []arr=read_double_array(offset+8*off, chn);
	   int len=arr.length;
	   
	   float []res=new float[len];
	   for(int i=0 ; i<len ; i++)
		   res[i]=(float)arr[i];
	   return res;
	}
	
	public float[] getMultiplexSample_long(long offset,long off, int chn) throws XMLCodecException {
	   long []arr=read_long_array(offset+8*off, chn);
	   int len=arr.length;
	   
	   float []res=new float[len];
	   for(int i=0 ; i<len ; i++)
		   res[i]=(float)arr[i];
	   return res;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	public float getMultiplexChannelSample_short(long offset, long off, int chn, int max_chn) throws XMLCodecException {
	   return (float)read_short(offset+2*(max_chn*off+chn));
	}
	
	public float getMultiplexChannelSample_float(long offset, long off, int chn, int max_chn) throws XMLCodecException {
	   return read_float(offset+4*(max_chn*off+chn));
	}
	
	public float getMultiplexChannelSample_int(long offset, long off, int chn, int max_chn) throws XMLCodecException {
	   return (float)read_int(offset+4*(max_chn*off+chn));
	}
	
	public float getMultiplexChannelSample_double(long offset,long off, int chn, int max_chn) throws XMLCodecException {
	   return (float)read_double(offset+8*(max_chn*off+chn));
	}
	
	public float getMultiplexChannelSample_long(long offset,long off, int chn, int max_chn) throws XMLCodecException {
	   return (float)read_long(offset+8*(max_chn*off+chn));
	}
	
	private int m_edf_record_size;
	private int m_chn_size[];
	private int m_chn_offset[];
	private static final int EDF_INT_SIZE=2;
	
	public int getMaxRecordSamples() {
		if(m_chn_size!=null) {
		   int max=m_chn_size[0];
		   for(int i=1 ; i<m_chn_size.length ; i++)
			   if(m_chn_size[i]>max)
				  max=m_chn_size[i];
			return max;
		}
		return 0;
	}
	
	public void init_edf(int max_chn, int chn[]) throws XMLCodecException {
		if(max_chn!=chn.length) {
		   throw new XMLCodecException("Bad channel size");	   
		}
		
		m_edf_record_size=0;
		m_chn_size=new int[max_chn];
		m_chn_offset=new int[max_chn];
		
		for(int i=0 ; i<max_chn ; i++) {
			m_chn_size[i]=chn[i];
			m_chn_offset[i]=EDF_INT_SIZE*m_edf_record_size;
			m_edf_record_size+=chn[i];
		}
		
		m_edf_record_size*=EDF_INT_SIZE;
  	}
	
	public float getEDFChannelSample(long offset,int chn) throws XMLCodecException {
		try {
		   long rec_offset=m_edf_record_size*(offset/m_chn_size[chn]);
		   long sample_offset=EDF_INT_SIZE*(offset % m_chn_size[chn]);
		   long file_offset=rec_offset+m_chn_offset[chn]+sample_offset;	   
		   byte []buffor =new byte[2];
	  
		   file.seek(file_offset);
		   file.read(buffor, 0, 2);   
		   return (float)ByteBuffer.wrap(buffor).order(ByteOrder.LITTLE_ENDIAN).getShort(); 
		} catch(Exception e) {
			throw new XMLCodecException("getEDFChannelSample: "+e.getMessage());
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	
	public static float to_float(float x) throws XMLCodecException  {
		return x;
	}
	
	public static float to_float(String x) throws XMLCodecException {
		try {
		   return new Float(x).floatValue();
		} catch(NumberFormatException e) {
			throw new XMLCodecException(e.getMessage());
		}
	}
	
	public int []to_int(String arr[]) throws XMLCodecException {
		int out[]=new int[arr.length];
		for(int i=0 ; i<arr.length ; i++) {
			try {
			   out[i]=Integer.parseInt(arr[i].trim());
			} catch(NumberFormatException e) {
			  throw new XMLCodecException(e.getMessage());
			}
		}
		return out;
	}
	
	public static int to_int(String x) throws XMLCodecException {
		try {
		  return Integer.parseInt(x.trim());
		} catch(NumberFormatException e) {
		   throw new XMLCodecException(e.getMessage());
		}
	}
	
	public int get_max_offset() throws XMLCodecException {
		return 4096;
	}
	
	public long getFileLength() throws XMLCodecException {
	  try {	
		if(file!=null) {
		   return file.length(); 
		}
	  } catch(IOException e) {
	 	;
	  }
	  return 0;  
	}
}
