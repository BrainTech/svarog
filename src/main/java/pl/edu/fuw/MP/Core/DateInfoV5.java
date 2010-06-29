package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.IOException;

public class DateInfoV5 extends FormatComponentV5 {
	public String date;

	public DateInfoV5() {
		type=DATE_INFO;
	}

	public void Read(DataArrayInputStream stream,int size) throws IOException {
		StringBuffer buf=new StringBuffer("");
		for (int i=0 ; i<size ; i++) {
			char c=(char)stream.readByte();
			if (c!='\0') {
				buf.append(c);
			}
		}
		date=buf.toString();
	}

	public String toString() {
		return "DateInfoV5: "+date;
	}

	public void Write(DataOutputStream stream) throws IOException {
		if (date!=null) {
			writeHeader(stream);
			stream.writeBytes(date);
		}
	}

	public int getSize() {
		return date!=null ? date.length() : 0;
	}
}
