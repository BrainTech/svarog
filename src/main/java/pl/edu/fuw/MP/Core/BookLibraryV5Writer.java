package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.signalml.domain.book.IncrementalBookWriter;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.domain.book.StandardBookSegmentWriter;
import org.signalml.domain.book.StandardBookWriter;

public class BookLibraryV5Writer implements StandardBookWriter, IncrementalBookWriter {
	private String comment="";
	private FormatComponentV5 fields[];
	private int fieldsCount;
	private DataOutputStream out=null;
	
	public FormatComponentV5 []getFields() {
		return fields;
	}
	
	public BookLibraryV5Writer() {
		  fields=new FormatComponentV5[256];
		  fieldsCount=0;
		  
		  for(int i=0 ; i<256 ; i++) {
			  fields[i]=null;
		  }
	}
	
	public void setBookComment(String comment) {
	   this.comment=comment;
	}

	public void setCalibration(float conv) {
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null && fields[i].type==FormatComponentV5.SIGNAL_INFO) {
				((SignalInfoV5)fields[i]).points_per_microvolt=conv;
				return;
			}
		}
		
		SignalInfoV5 f=new SignalInfoV5();
		f.points_per_microvolt=conv;
		fields[fieldsCount++]=f;
	}

	public void setDate(String text) {
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null && fields[i].type==FormatComponentV5.DATE_INFO) {
				((DateInfoV5)fields[i]).date=text;
				return;
			}
		}
		
		DateInfoV5 f=new DateInfoV5();
		f.date=text;
		fields[fieldsCount++]=f;
	}

	public void setDictionarySize(int size) {
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null && fields[i].type==FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5)fields[i]).dictionary_size=size;
				return;
			}
		}
		
		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.dictionary_size=size;
		fields[fieldsCount++]=f;
	}

	public void setDictionaryType(char type) {
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null && fields[i].type==FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5)fields[i]).dictionary_type=(byte)type;
				return;
			}
		}
		
		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.dictionary_type=(byte)type;
		fields[fieldsCount++]=f;
	}

	public void setEnergyPercent(float eps) {
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null && fields[i].type==FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5)fields[i]).energy_percent=eps;
				return;
			}
		}
		
		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.energy_percent=eps;
		fields[fieldsCount++]=f;
	}

	public void setMaxIterationCount(int max) {
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null && fields[i].type==FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5)fields[i]).max_number_of_iterations=max;
				return;
			}
		}
		
		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.max_number_of_iterations=max;
		fields[fieldsCount++]=f;
	}

	public void setSamplingFrequency(float freq) {
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null && fields[i].type==FormatComponentV5.SIGNAL_INFO) {
				((SignalInfoV5)fields[i]).sampling_freq=freq;
				return;
			}
		}
		
		SignalInfoV5 f=new SignalInfoV5();
		f.sampling_freq=freq;
		fields[fieldsCount++]=f;
	}

	public void setTextInfo(String text) {
		 for(int i=0 ; i<fields.length ; i++) {
				if(fields[i]!=null && fields[i].type==FormatComponentV5.TEXT_INFO) {
				  ((TextInfoV5)fields[i]).text=comment;
				   return;	
				}
			}
			
		 TextInfoV5 f=new TextInfoV5();
		 f.text=comment;
		 fields[fieldsCount++]=f;
	}

	public void setWebSiteInfo(String text) {
		 for(int i=0 ; i<fields.length ; i++) {
				if(fields[i]!=null && fields[i].type==FormatComponentV5.WEB_SITE_INFO) {
				  ((WebSiteInfoV5)fields[i]).www=text;
				   return;	
				}
			}
			
		 WebSiteInfoV5 f=new WebSiteInfoV5();
		 f.www=text;
		 fields[fieldsCount++]=f;
		
	}

	private int getFieldSize() {
		int sum=0;
		for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null) {
			   sum+=2+fields[i].getSize();
			}
		}
		return sum;
	}
	
	public void Open(String fileName) throws IOException {
		 out=new DataOutputStream(new FileOutputStream(fileName));
		 
		 out.writeBytes("MPv5.0");
		 
		 if(comment!=null) {
			 out.writeByte(FormatComponentV5.COMMENT_SEGMENT_IDENTITY);
			 out.writeInt(comment.length());
			 out.writeBytes(comment);
		 }
		 
		 out.writeByte(FormatComponentV5.FILE_HEADER);
		 out.writeInt(getFieldSize());
		 
		 for(int i=0 ; i<fields.length ; i++) {
			if(fields[i]!=null) {
			   fields[i].Write(out);
			}
		 }
		 out.flush();
	}
	
	public void close() {
		if(out!=null) {
		   try {
			out.close();
		   } catch (IOException e) {
			 ;
		   }
		   out=null;
		}
	}

	public void writeSegment(StandardBookSegment[] segments) throws IOException {
		if(segments!=null) {
		   for(int i=0 ; i<segments.length ; i++) {
			   if(segments[i]!=null) {
				   ((SegmentHeaderV5)segments[i]).Write(out);
			   }
		   }
		   out.flush();
		}
	}

	public FormatComponentV5 getFields(int index) {
		return fields[index];
	}

	public void writeSegment(StandardBookSegmentWriter segment) throws IOException {
		if(segment!=null) {
			 ((SegmentHeaderV5)segment).Write(out);
			  out.flush();
		}
	}
}
