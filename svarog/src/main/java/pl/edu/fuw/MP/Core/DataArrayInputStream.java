package pl.edu.fuw.MP.Core;

import java.io.*;

public class DataArrayInputStream  {
	private byte buffor[]=new byte[4];
	private InputStream source=null;

	DataArrayInputStream(RandomAccessFile file,byte buff[]) throws IOException {
		file.read(buff,0,buff.length);
		source=new ByteArrayInputStream(buff);
	}

	public final int readUnsignedByte() throws IOException {
		return source.read();
	}

	public final byte readByte() throws IOException {
		return (byte)source.read();
	}

	public final int readUnsignedShort() throws IOException {
		source.read(buffor,0,2);
		return ((buffor[0]<<8)|(buffor[1]&0xff));
	}

	public final short readShort() throws IOException {
		source.read(buffor,0,2);
		return (short)((buffor[0]<<8)|(buffor[1]&0xff));
	}

	public final float readFloat() throws IOException {
		source.read(buffor,0,4);
		return Float.intBitsToFloat(((buffor[0]&0xff)<<24)|
									((buffor[1]&0xff)<<16)|
									((buffor[2]&0xff)<<8) |
									(buffor[3]&0xff));
	}

	public final int readInt() throws IOException {
		source.read(buffor,0,4);
		return (((buffor[0]&0xff)<<24) | ((buffor[1]&0xff)<<16) |
				((buffor[2]&0xff)<<8)  | (buffor[3]&0xff));
	}

	public final long skipBytes(long n) throws IOException {
		return source.skip(n);
	}
}





