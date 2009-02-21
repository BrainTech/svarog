/* 1999 08 20; 1999 09 06; 1999 11 09 */

package pl.edu.fuw.MP.Core;

import java.io.*;

class FormatComponent {
    public static final int TEXT_INFO=1,DATE_INFO=2;
    public static final int SIGNAL_INFO=3,DECOMP_INFO=4;
    public int  type;

    public void Read(DataArrayInputStream stream)
        throws IOException { ; }

    public void Read(DataArrayInputStream stream,int size)
        throws IOException { ; }
}

class DecompositionInfo extends FormatComponent {
    public float energy_percent;
    public int   max_number_of_iterations;
    public int   dictionary_size;
    public char  dictionary_type;

    public void Read(DataArrayInputStream stream) throws IOException {
        energy_percent=stream.readFloat();
        max_number_of_iterations=stream.readInt();
        dictionary_size=stream.readInt();
        dictionary_type=(char)stream.readByte();
        stream.skipBytes(3L);
    }
}

class SignalInfo extends FormatComponent {
    public float sampling_freq;
    public float points_per_microvolt;
    public int   number_of_chanels_in_file;

    public void Read(DataArrayInputStream stream) throws IOException {
        sampling_freq=stream.readFloat();
        points_per_microvolt=stream.readFloat();
        number_of_chanels_in_file=stream.readInt();
    }
}

class TextInfo extends FormatComponent {
    public String text;

    public void Read(DataArrayInputStream stream,int size) throws IOException {
        text="";
        for(int i=0 ; i<size ; i++) {
            char c=(char)stream.readByte();
            if(c!='\0')
                text+=c;
        }
    }
}

class DateInfo extends FormatComponent {
    public String date;

    public void Read(DataArrayInputStream stream,int size) throws IOException {
        date="";
        for(int i=0 ; i<size ; i++) {
            char c=(char)stream.readByte();
            if(c!='\0')
                date+=c;
        }
    }
}

class SegmentHeader {
    public int   channel;
    public int   file_offset;
    public int   book_size;
    public int   signal_size;
    public float signal_energy;
    public float book_energy;

    public void Read(RandomAccessFile stream) throws IOException {
        channel=stream.readInt();
        file_offset=stream.readInt();
        book_size=stream.readInt();
        signal_size=stream.readInt();
        signal_energy=stream.readFloat();
        book_energy=stream.readFloat();
    }
}

public class NewBookLibrary {
    public static final int VERSION_NONE=-1,VERSION_III=1,VERSION_IV=2,VERSION_V=3;
    private FormatComponent fields[];
    private int fieldsCount;
    private SegmentHeader segment=new SegmentHeader();
    private NewAtom atoms[]=null;
    private RandomAccessFile streamClass=null;
    //private int BookCount;
    private int MaxBookNumber;
    private int version=VERSION_NONE;

    public final NewAtom getAtom(int k) {
        return atoms[k];
    }

    public final int getMaxBookNumber() {
        return MaxBookNumber;
    }

    public void export(BookHeader head) {
       head.file_offset=(short)segment.file_offset;
       head.book_size=(short)segment.book_size;
       head.signal_size=getDimBase();
       head.points_per_micro_V=getConvFactor();
       head.FREQUENCY=getSamplingFreq();
       head.signal_energy=segment.signal_energy;
       head.book_energy=segment.book_energy;
    }

    public final void export(BookAtom atom,int k) {
        atoms[k].export(atom);
    }

    public NewBookLibrary() {
        fields=new FormatComponent[256];
        fieldsCount=0;
        for(int i=0 ; i<256 ; i++)
            fields[i]=null;
    }

    private void Close() {
        try {
            if(streamClass!=null) {
                streamClass.close();
                streamClass=null;
            }
        } catch(IOException e) {
        }
    }

    private static int checkFormat(RandomAccessFile stream)
        throws IOException {
        byte s[]=new byte[4];

        stream.read(s,0,4);
        String string=new String(s);

        if(string.equals("MPv3")) {
            return VERSION_III;
        } else if(string.equals("MPv4")) {
            return VERSION_IV;
        } else if(string.equals("MPv5")) {
			return VERSION_V;
		}
        return VERSION_NONE;
    }

    public static int checkFormat(String filename) {
        try {
            RandomAccessFile stream=new RandomAccessFile (filename, "r");
            int              ok=checkFormat(stream);

            stream.close();
            return ok;
        } catch(IOException e) {
			Utils.log(e.toString());
            return VERSION_NONE;
        }
    }

    private boolean setOffset(RandomAccessFile  stream,int offset) {
        try {
            SegmentHeader segHead=new SegmentHeader();
            for(int i=0 ; i<offset ; i++) {
                segHead.Read(stream);
                stream.skipBytes(segHead.book_size*NewAtom.sizeOf());
            }
        } catch(IOException e) {
            return false;
        }
        return true;
    }

    public boolean SetOffset(String filename,int Offset) {
        try {
            Close();
            streamClass=new RandomAccessFile(filename, "r");
            return setOffset(streamClass,Offset);
        } catch(IOException e) {
            return false;
        }
    }

    private void readBook(RandomAccessFile stream) throws IOException {
        segment.Read(stream);
        atoms=new NewAtom[segment.book_size];
        byte buffor[]=new byte[segment.book_size*NewAtom.sizeOf()];
        DataArrayInputStream block=new DataArrayInputStream(stream,buffor);

        for(int i=0 ; i<segment.book_size ; i++) {
            (atoms[i]=new NewAtom(version)).Read(block);
            atoms[i].index=i;
        }
    }

    private int countBook(RandomAccessFile stream) throws IOException {
        int code;
        if((code=checkFormat(stream))==VERSION_NONE)
            return 0;

        version=code;
        int headSize=stream.readUnsignedShort()-6;
        stream.skipBytes(headSize);
        int k=0;

        try {
            SegmentHeader segHead=new SegmentHeader();
            for(k=0 ; ; k++) {
                segHead.Read(stream);
                stream.skipBytes(segHead.book_size*NewAtom.sizeOf());
            }
        } catch(IOException e) {
            return k;
        }
    }

    private boolean loadBook(RandomAccessFile  stream,int offset)
        throws IOException {
        int code;
        if((code=checkFormat(stream))==VERSION_NONE) {
            return false;
         }

        version=code;
        int headSize=stream.readUnsignedShort()-6;
        stream.skipBytes(headSize);

        if(!setOffset(stream,offset)) {
            return false;
        }

        readBook(stream);
        return true;
    }

    public boolean readNextBook() {
        try {
            readBook(streamClass);
        } catch(IOException e) {
            return false;
        }
        return true;
    }

    public int countBook(String filename) {
        try {
            RandomAccessFile  streamClass=new RandomAccessFile (filename, "r");
            int count=countBook(streamClass);
            streamClass.close();
            return count;
        } catch(IOException e) {
            return 0;
        }
    }

    public boolean loadBook(String filename,int offset) {
        MaxBookNumber=countBook(filename);

        try {
            Close();
            streamClass=new RandomAccessFile(filename, "r");
            return loadBook(streamClass,offset);
        } catch(IOException e) {
            return false;
        }
    }

    public void importBook(Book book) {
        Close();
        int size;

        MaxBookNumber=1;
        segment.channel=0;
        segment.file_offset=0;
        segment.book_size=book.atoms.length;
        segment.signal_size=book.param.DimBase;
        segment.signal_energy=1.0F;
        segment.book_energy=1.0F;

        atoms=new NewAtom[size=book.atoms.length];
        for(int i=0 ; i<size ; i++) {
            NewAtom stmp=book.atoms[i];
            atoms[i]=new NewAtom();
            atoms[i].scale=stmp.scale;
            atoms[i].frequency=stmp.frequency;
            atoms[i].position=stmp.position;
            atoms[i].modulus=stmp.modulus;
            atoms[i].amplitude=stmp.amplitude;
            atoms[i].phase=stmp.phase;
            atoms[i].index=i;
        }
        fieldsCount=0;
    }

    private boolean readFileHeader(RandomAccessFile  stream) throws IOException {
        int code;
        if((code=checkFormat(stream))==VERSION_NONE) {
            return false;
        }

        version=code;
        int   MaxSize=stream.readUnsignedShort(),size=6;
        byte  buff[]=new byte[MaxSize];
        DataArrayInputStream block=new DataArrayInputStream(stream,buff);

        fieldsCount=0;
        while(size<MaxSize) {
            code=     block.readUnsignedByte();
            int fieldSize=block.readUnsignedByte();

            switch(code) {
            case FormatComponent.TEXT_INFO:
                (fields[fieldsCount]=new TextInfo()).Read(block,fieldSize);
                fields[fieldsCount++].type=FormatComponent.TEXT_INFO;
                break;
            case FormatComponent.DATE_INFO:
                (fields[fieldsCount]=new DateInfo()).Read(block,fieldSize);
                fields[fieldsCount++].type=FormatComponent.DATE_INFO;
                break;
            case FormatComponent.SIGNAL_INFO:
                (fields[fieldsCount]=new SignalInfo()).Read(block);
                fields[fieldsCount++].type=FormatComponent.SIGNAL_INFO;
                break;
            case FormatComponent.DECOMP_INFO:
                (fields[fieldsCount]=new DecompositionInfo()).Read(block);
                fields[fieldsCount++].type=FormatComponent.DECOMP_INFO;
                break;
            default:
                block.skipBytes(fieldSize);
                break;
            }
            size+=fieldSize+2;
        }
        return true;
    }

    public final int getDimBase() {
        return segment.signal_size;
    }

    public final int getNumOfAtoms() {
        return segment.book_size;
    }

    public final String getDate() {
        for(int i=0 ; i<fieldsCount ; i++)
            if(fields[i].type==FormatComponent.DATE_INFO)
                return ((DateInfo)fields[i]).date;
        return null;
    }

    public final String getText() {
        for(int i=0 ; i<fieldsCount ; i++)
            if(fields[i].type==FormatComponent.TEXT_INFO)
                return ((TextInfo)fields[i]).text;
        return null;
    }

    public final float getEnergyPercent() {
        for(int i=0 ; i<fieldsCount ; i++)
            if(fields[i].type==FormatComponent.DECOMP_INFO)
                return ((DecompositionInfo)fields[i]).energy_percent;
        return -1.0F;
    }

    public final int getMaxNumberOfIteration() {
        for(int i=0 ; i<fieldsCount ; i++)
            if(fields[i].type==FormatComponent.DECOMP_INFO)
                return ((DecompositionInfo)fields[i]).max_number_of_iterations;
         return -1;
    }

    public final int getDictionarySize() {
        for(int i=0 ; i<fieldsCount ; i++)
            if(fields[i].type==FormatComponent.DECOMP_INFO)
                return ((DecompositionInfo)fields[i]).dictionary_size;
        return -1;
    }

    public final int getMaxChannel() {
        for(int i=0 ; i<fieldsCount ; i++)
            if(fields[i].type==FormatComponent.SIGNAL_INFO)
                return ((SignalInfo)fields[i]).number_of_chanels_in_file;
        return -1;
    }

    public final char getDictionaryType() {
        for(int i=0 ; i<fieldsCount ; i++) {
            if(fields[i].type==FormatComponent.DECOMP_INFO) {
                return ((DecompositionInfo)fields[i]).dictionary_type;
            }
        }
        return '0';
    }

    public final float getConvFactor() {
        for(int i=0 ; i<fieldsCount ; i++) {
            if(fields[i].type==FormatComponent.SIGNAL_INFO) {
                return ((SignalInfo)fields[i]).points_per_microvolt;
            }
        }
        return 1.0F;
    }

    public final float getSamplingFreq() {
        for(int i=0 ; i<fieldsCount ; i++) {
            if(fields[i].type==FormatComponent.SIGNAL_INFO) {
                return ((SignalInfo)fields[i]).sampling_freq;
            }
        }
        return 1.0F;
    }

    public final String getVersion() {
        switch(version) {
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

        if(mydate==null)
            mydate="(null)";
        if(mytext==null)
            mytext="(null)";

        return ("\nSampling frequency: "+getSamplingFreq()+" Hz\n"+
                "Version           : "+getVersion()+"\n"+
                "Conversion        : "+getConvFactor()+" points/uV\n"+
                "Dictionary Type   : "+getDictionaryType()+"\n"+
                "Dictionary Size   : "+getDictionarySize()+"\n"+
                "Energy Percent    : "+getEnergyPercent()+" %\n"+
                "File offset       : "+segment.file_offset+"\n"+
                "Book size         : "+segment.book_size+"\n"+
                "Signal size       : "+segment.signal_size+"\n"+
                "Signal energy     : "+segment.signal_energy+"\n"+
                "Channel           : "+segment.channel+"\n"+
                "Number of channels: "+getMaxChannel()+"\n"+
                "Book energy       : "+segment.book_energy+"\n"+
                "Book number       : "+getMaxBookNumber()+"\n"+
                "Text              : "+mytext+"\n"+
                "Date              : "+mydate+"\n"
                );
    }

    public boolean Open(String filename,int Offset) {
        try {
            Close();
            RandomAccessFile stream=new RandomAccessFile(filename, "r");
            boolean ok=readFileHeader(stream);
            stream.close();
            if(!ok) return false;
            return loadBook(filename,Offset);
        } catch(IOException e) {
            return false;
        }
    }
}
