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

		for (int i=0 ; i<256 ; i++) {
			fields[i]=null;
		}
	}

	public void setBookComment(String comment) {
		this.comment=comment;
	}

	public void setCalibration(float conv) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.SIGNAL_INFO) {
				((SignalInfoV5) field).points_per_microvolt = conv;
				return;
			}
		}

		SignalInfoV5 f=new SignalInfoV5();
		f.points_per_microvolt=conv;
		fields[fieldsCount++]=f;
	}

	public void setDate(String text) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.DATE_INFO) {
				((DateInfoV5) field).date = text;
				return;
			}
		}

		DateInfoV5 f=new DateInfoV5();
		f.date=text;
		fields[fieldsCount++]=f;
	}

	public void setDictionarySize(int size) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5) field).dictionary_size = size;
				return;
			}
		}

		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.dictionary_size=size;
		fields[fieldsCount++]=f;
	}

	public void setDictionaryType(char type) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5) field).dictionary_type = (byte)type;
				return;
			}
		}

		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.dictionary_type=(byte)type;
		fields[fieldsCount++]=f;
	}

	public void setEnergyPercent(float eps) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5) field).energy_percent = eps;
				return;
			}
		}

		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.energy_percent=eps;
		fields[fieldsCount++]=f;
	}

	public void setMaxIterationCount(int max) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.DECOMP_INFO) {
				((DecompositionInfoV5) field).max_number_of_iterations = max;
				return;
			}
		}

		DecompositionInfoV5 f=new DecompositionInfoV5();
		f.max_number_of_iterations=max;
		fields[fieldsCount++]=f;
	}

	public void setSamplingFrequency(float freq) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.SIGNAL_INFO) {
				((SignalInfoV5) field).sampling_freq = freq;
				return;
			}
		}

		SignalInfoV5 f=new SignalInfoV5();
		f.sampling_freq=freq;
		fields[fieldsCount++]=f;
	}

	public void setNumberOfChannels(int numberOfChannels) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.SIGNAL_INFO) {
				((SignalInfoV5) field).number_of_chanels_in_file = numberOfChannels;
				return;
			}
		}

		SignalInfoV5 f=new SignalInfoV5();
		f.number_of_chanels_in_file = numberOfChannels;
		fields[fieldsCount++]=f;
	}

	public void setTextInfo(String text) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.TEXT_INFO) {
				((TextInfoV5) field).text = comment;
				return;
			}
		}

		TextInfoV5 f=new TextInfoV5();
		f.text=comment;
		fields[fieldsCount++]=f;
	}

	public void setWebSiteInfo(String text) {
		for (FormatComponentV5 field : fields) {
			if (field != null && field.type == FormatComponentV5.WEB_SITE_INFO) {
				((WebSiteInfoV5) field).www = text;
				return;
			}
		}

		WebSiteInfoV5 f=new WebSiteInfoV5();
		f.www=text;
		fields[fieldsCount++]=f;

	}

	private int getFieldSize() {
		int sum=0;
		for (FormatComponentV5 field : fields) {
			if (field != null) {
				sum += 2 + field.getSize();
			}
		}
		return sum;
	}

	public void Open(String fileName) throws IOException {
		out=new DataOutputStream(new FileOutputStream(fileName));

		out.writeBytes("MPv5.0");

		if (comment!=null) {
			out.writeByte(FormatComponentV5.COMMENT_SEGMENT_IDENTITY);
			out.writeInt(comment.length());
			out.writeBytes(comment);
		}

		out.writeByte(FormatComponentV5.FILE_HEADER);
		out.writeInt(getFieldSize());

		for (FormatComponentV5 field : fields) {
			if (field != null) {
				field.Write(out);
			}
		}
		out.flush();
	}

	public void close() {
		if (out!=null) {
			try {
				out.close();
			} catch (IOException e) {
				;
			}
			out=null;
		}
	}

	public void writeSegment(StandardBookSegment[] segments) throws IOException {
		if (segments!=null) {
			for (StandardBookSegment segment : segments) {
				if (segment != null) {
					((SegmentHeaderV5) segment).Write(out);
				}
			}
			out.flush();
		}
	}

	public FormatComponentV5 getFields(int index) {
		return fields[index];
	}

	public void writeSegment(StandardBookSegmentWriter segment) throws IOException {
		if (segment!=null) {
			((SegmentHeaderV5)segment).Write(out);
			out.flush();
		}
	}
}
