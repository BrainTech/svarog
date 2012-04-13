package pl.edu.fuw.MP.Core;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import org.signalml.domain.book.BookFormatException;
import org.signalml.domain.book.StandardBookSegment;

class CPair {
	public long x, y;
	public int channel;

	public CPair(long x, long y, int channel) {
		this.x=x;
		this.y=y;
		this.channel=channel;
	}

	public CPair(long x, int channel) {
		this.x=x;
		y=-1L;
		this.channel=channel;
	}
}

class BookLibraryV5IndexElement {
	public int  type;
	public int  channel;
	public int  offset;
	public long filePosition;
	public BookLibraryV5IndexElement parent=null;

	@Override
	public String toString() {
		StringBuffer buf=new StringBuffer();

		buf.append("type=");
		buf.append(type);
		buf.append(" (");
		buf.append(FormatComponentV5.toName(type));
		buf.append(") channel=");
		buf.append(channel);
		buf.append(" offset=");
		buf.append(offset);
		buf.append(" pos=");
		buf.append(filePosition);

		return buf.toString();
	}
}

public class BookLibraryV5 implements BookLibraryInterface {
	public static final int VERSION_NONE=-1,VERSION_III=1,VERSION_IV=2,VERSION_V=3;
	private FormatComponentV5 fields[];
	private int fieldsCount;
	private SegmentHeaderV5 segment=new SegmentHeaderV5(this);
	private RandomAccessFile streamClass=null;
	private int version=VERSION_NONE;
	private Vector<BookLibraryV5IndexElement> index=new Vector<BookLibraryV5IndexElement>();
	private int epochDim;

	public void setFields(FormatComponentV5 fields[]) {
		fieldsCount=0;
		for (int i=0 ; i<fields.length ; i++) {
			if (fields[i]!=null) {
				this.fields[fieldsCount++]=fields[i];
			}
		}
	}

	private void splitIndex() {
		int len=index.size();

		for (int i=0 ; i<len ; i++) {
			BookLibraryV5IndexElement x=index.elementAt(i);

			if (x.parent==null) {
				for (int j=i+1 ; j<len ; j++) {
					BookLibraryV5IndexElement y=index.elementAt(j);

					if (x.channel==y.channel &&
							x.offset==y.offset   &&
							x.type!=y.type) {
						x.parent=y;
						y.parent=x;
						break;
					}
				}
			}
		}
	}

	public final int getChannel() {
		return segment.channelNumber;
	}
	/*
	    public BookAtom []getAtoms() {
			if(segment.type==FormatComponentV5.ATOMS_SEGMENT_IDENTITY) {
			   int len=segment.atoms.size();
			   BookAtom []atoms=new BookAtom[len];

			   for(int i=0 ; i<len ; i++) {
			       AtomV5 stmp=segment.atoms.elementAt(i);

			       atoms[i]=new BookAtom();

			       atoms[i].scale=stmp.scale;
			       atoms[i].frequency=stmp.frequency;
			       atoms[i].position=stmp.position;
			       atoms[i].modulus=stmp.modulus;
			       atoms[i].amplitude=stmp.amplitude;
			       atoms[i].phase=stmp.phase;
			       atoms[i].index=stmp.iteration;
	           }

			   return atoms;
			}
		    return null;
	    }
	*/
	public float []getSignal() {
		if (segment.type==FormatComponentV5.SIGNAL_SEGMENT_IDENTITY) {
			return segment.signal;
		}
		return null;
	}

	public int getSignalSize() {
		return getDimBase();
	}

	public BookLibraryV5() {
		fields=new FormatComponentV5[256];
		fieldsCount=0;
		for (int i=0 ; i<256 ; i++) {
			fields[i]=null;
		}
	}

	public BookLibraryV5(BookLibraryV5Writer lib) {
		fields=new FormatComponentV5[256];
		fieldsCount=0;

		for (int i=0 ; i<256 ; i++) {
			FormatComponentV5 c=lib.getFields(i);
			if (c!=null) {
				fields[fieldsCount++]=c;;
			}
		}
	}

	public void Close() {
		try {
			if (streamClass!=null) {
				streamClass.close();
				streamClass=null;
			}
		} catch (IOException e) {
		}
	}

	private static int checkFormat(RandomAccessFile stream)
	throws IOException {
		byte s[]=new byte[4];

		stream.read(s,0,4);
		String string=new String(s);

		if (string.equals("MPv3")) {
			return VERSION_III;
		} else if (string.equals("MPv4")) {
			return VERSION_IV;
		} else if (string.equals("MPv5")) {
			stream.read(s, 0, 2); /* .0 */
			return VERSION_V;
		}
		return VERSION_NONE;
	}

	public static int checkFormat(String filename) {
		try {
			RandomAccessFile stream=new RandomAccessFile(filename, "r");
			int             ok=checkFormat(stream);

			stream.close();
			return ok;
		} catch (IOException e) {
			Utils.log(e.toString());
			return VERSION_NONE;
		}
	}

	private void readBook(RandomAccessFile stream) throws IOException {
		segment=new SegmentHeaderV5(this);
		segment.read(stream);
	}

	private CPair[] getOffset(int segNo) {
		Vector<CPair> v=new Vector<CPair>();
		int len=index.size();

		for (int i=0 ; i<len ; i++) {
			BookLibraryV5IndexElement e=index.elementAt(i);

			if (e.offset==segNo) {
				if (!exists(v, e.channel)) {
					if (e.parent==null) {
						v.add(new CPair(e.filePosition, e.channel));
					} else {
						v.add(new CPair(e.filePosition, e.parent.filePosition, e.channel));
					}
				}
			}
		}

		CPair []arr=new CPair[v.size()];
		return v.toArray(arr);
	}

	private boolean exists(Vector<CPair> v, int channel) {
		Enumeration<CPair> e=v.elements();

		while (e.hasMoreElements()) {
			CPair p=e.nextElement();
			if (p.channel==channel) {
				return true;
			}
		}
		return false;
	}

	private long getOffset(int segNo, int chnNo, int type) {
		int len=index.size();

		for (int i=0 ; i<len ; i++) {
			BookLibraryV5IndexElement e=index.elementAt(i);
			if (e.offset==segNo && e.channel==chnNo && e.type==type) {
				return e.filePosition;
			}
		}
		return -1;
	}

	private int countBook(RandomAccessFile stream) throws IOException {
		int k=0;
		try {
			SegmentHeaderV5 segHead=new SegmentHeaderV5(this);

			for (; ; k++) {
				long off=stream.getFilePointer();
				segHead.skipSegment(stream);

				BookLibraryV5IndexElement element=new BookLibraryV5IndexElement();

				element.filePosition=off;
				element.channel=segHead.getChannelNumber();
				element.offset=segHead.getSegmentNumber();
				element.type=segHead.type;

				index.add(element);

				Utils.log(element.toString());
			}
		} catch (IOException e) {
			return k;
		} finally {
			splitIndex();
		}
	}

	public boolean NextBook() {
		try {
			readBook(streamClass);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private boolean readFileHeader(RandomAccessFile stream) throws IOException {
		int code, s, size;

		if ((version=checkFormat(stream))==VERSION_NONE) {
			return false;
		}

		code=(int)stream.readByte();
		size=(int)stream.readInt();

		Utils.log("code: "+code+" "+FormatComponentV5.toName(code)+" size: "+size);

		if (code==FormatComponentV5.COMMENT_SEGMENT_IDENTITY) {
			stream.skipBytes(size);

			code=(int)stream.readByte();
			size=(int)stream.readInt();

			Utils.log("Skip commnet, code: "+code+" "+FormatComponentV5.toName(code)+" size: "+size);
		} else if (code!=FormatComponentV5.FILE_HEADER) {
			;
		}

		byte  buff[]=new byte[size];
		DataArrayInputStream block=new DataArrayInputStream(stream, buff);

		fieldsCount=0;
		s=0;

		while (s<size) {
			code=     block.readUnsignedByte();
			int fieldSize=block.readUnsignedByte();

			Utils.log("code: "+code+" "+FormatComponentV5.toName(code)+" size: "+fieldSize);

			switch (code) {
			case FormatComponentV5.TEXT_INFO:
				(fields[fieldsCount++]=new TextInfoV5()).Read(block, fieldSize);
				break;
			case FormatComponentV5.DATE_INFO:
				(fields[fieldsCount++]=new DateInfoV5()).Read(block, fieldSize);
				break;
			case FormatComponentV5.SIGNAL_INFO:
				(fields[fieldsCount++]=new SignalInfoV5()).Read(block);
				break;
			case FormatComponentV5.DECOMP_INFO:
				(fields[fieldsCount++]=new DecompositionInfoV5()).Read(block);
				break;
			case FormatComponentV5.WEB_SITE_INFO:
				(fields[fieldsCount++]=new WebSiteInfoV5()).Read(block, fieldSize);
				break;
			default:
				block.skipBytes(fieldSize);
				break;
			}
			s+=fieldSize+2;
		}
		return true;
	}

	public void setDimBase(int dim) {
		this.epochDim=dim;
	}

	public int getDimBase() {
//       if(segment.signal!=null) {
//		   return segment.signal.length;
//		}
		return epochDim;
	}

	public int getNumOfAtoms() {
		return segment.atoms.size();
	}

	public String getDate() {
		for (int i=0 ; i<fieldsCount ; i++)
			if (fields[i].type==FormatComponent.DATE_INFO) {
				return ((DateInfoV5)fields[i]).date;
			}
		return null;
	}

	public String getText() {
		for (int i=0 ; i<fieldsCount ; i++)
			if (fields[i].type==FormatComponentV5.TEXT_INFO) {
				return ((TextInfoV5)fields[i]).text;
			}
		return null;
	}

	public float getEnergyPercent() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.DECOMP_INFO) {
				return ((DecompositionInfoV5)fields[i]).energy_percent;
			}
		}
		return -1.0F;
	}

	public int getMaxNumberOfIteration() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.DECOMP_INFO) {
				return ((DecompositionInfoV5)fields[i]).max_number_of_iterations;
			}
		}
		return -1;
	}

	public int getDictionarySize() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.DECOMP_INFO) {
				return ((DecompositionInfoV5)fields[i]).dictionary_size;
			}
		}
		return -1;
	}

	public int getMaxChannel() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.SIGNAL_INFO) {
				return ((SignalInfoV5)fields[i]).number_of_chanels_in_file;
			}
		}
		return -1;
	}

	public char getDictionaryType() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.DECOMP_INFO) {
				return (char)((DecompositionInfoV5)fields[i]).dictionary_type;
			}
		}
		return '0';
	}

	public float getConvFactor() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.SIGNAL_INFO) {
				return ((SignalInfoV5)fields[i]).points_per_microvolt;
			}
		}
		return 1.0F;
	}

	public float getSamplingFreq() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.SIGNAL_INFO) {
				return ((SignalInfoV5)fields[i]).sampling_freq;
			}
		}
		return 1.0F;
	}

	public String getWebSize() {
		for (int i=0 ; i<fieldsCount ; i++) {
			if (fields[i].type==FormatComponentV5.WEB_SITE_INFO) {
				return ((WebSiteInfoV5)fields[i]).www;
			}
		}
		return null;
	}


	public String getVersion() {
		switch (version) {
		case VERSION_III:
			return "III";
		case VERSION_IV:
			return "IV";
		case VERSION_V:
			return "V";

		}
		return "UNKNOWN";
	}

	public String getString() {
		String mydate=getDate(),mytext=getText();

		if (mydate==null)
			mydate="(null)";
		if (mytext==null)
			mytext="(null)";

		return ("\nSampling frequency: "+getSamplingFreq()+" Hz\n"+
				"Version           : "+getVersion()+"\n"+
				"Conversion        : "+getConvFactor()+" points/uV\n"+
				"Dictionary Type   : "+getDictionaryType()+"\n"+
				"Dictionary Size   : "+getDictionarySize()+"\n"+
				"Energy Percent    : "+getEnergyPercent()+" %\n"+
				"Number of channels: "+getMaxChannel()+"\n"+
				"Text              : "+mytext+"\n"+
				"Date              : "+mydate+"\n"
			   );
	}

	public boolean Open(String filename, int off) throws BookFormatException {
		try {
			Close();
			streamClass=new RandomAccessFile(filename, "r");

			readFileHeader(streamClass);
			long pos=streamClass.getFilePointer();
			countBook(streamClass);
			streamClass.seek(pos);

			readBook(streamClass);
		} catch (IOException e) {
			throw new BookFormatException(e);
		}
		return true;
	}

	public int getSegmentCount() {
		HashSet<Integer> set=new HashSet<Integer>();
		int len=index.size();

		for (int i=0 ; i<len ; i++) {
			set.add(new Integer(index.elementAt(i).offset));
		}

		return set.size();
	}

	public StandardBookSegment []getCurrentSegment(int segmentIndex) {
		CPair off[]=getOffset(segmentIndex);

		if (off!=null) {
			int len=off.length;
			SegmentHeaderV5 []arr=new SegmentHeaderV5[len];

			for (int i=0 ; i<len ; i++) {
				try {
					CPair p=off[i];
					SegmentHeaderV5 seg1=null, seg2=null;

					Utils.log("off= "+p.x+" "+p.y);

					if (p.x>=0L) {
						streamClass.seek(p.x);
						readBook(streamClass);
						seg1=segment;
					}
					int offSetDimension = segment.offsetDimension;
					int offsetNumber = segment.offsetNumber;

					if (p.y>=0L) {
						streamClass.seek(p.y);
						readBook(streamClass);
						seg2=segment;
						segment.offsetDimension = offSetDimension;
						segment.offsetNumber = offsetNumber;
					}

					if (seg1!=null && seg2!=null) {
						if (seg1.hasSignal()) {
							seg2.setSignalSamples(seg1.getSignalSamples());
							arr[i]=seg2;
						} else {
							seg1.setSignalSamples(seg2.getSignalSamples());
							arr[i]=seg1;
						}
					} else {
						if (seg1!=null) {
							arr[i]=seg1;
						} else if (seg2!=null) {
							arr[i]=seg2;
						}
					}
				} catch (IOException e) {
					return null;
				}
			}
			return arr;
		}
		return null;
	}

	public StandardBookSegment getCurrentSegment(int segmentIndex, int channelIndex) {
		long off=getOffset(segmentIndex, channelIndex, FormatComponentV5.ATOMS_SEGMENT_IDENTITY);
		SegmentHeaderV5 seg=null;

		if (off>=0L) {
			try {
				streamClass.seek(off);
				readBook(streamClass);
			} catch (IOException e) {
				return null;
			}

			seg=segment;
		} else {
			Utils.log("Books not found!");
		}

		off=getOffset(segmentIndex, channelIndex, FormatComponentV5.SIGNAL_SEGMENT_IDENTITY);
		if (off>=0L) {
			try {
				streamClass.seek(off);
				readBook(streamClass);
			} catch (IOException e) {
				return null;
			}
			if (seg!=null) {
				seg.setSignalSamples(segment.getSignalSamples());
			} else {
				seg=segment;
			}
		} else {
			Utils.log("Signals not found!");
		}
		return seg;
	}
}
