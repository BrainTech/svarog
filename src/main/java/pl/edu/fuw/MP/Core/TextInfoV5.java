package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.IOException;

public class TextInfoV5 extends FormatComponentV5 {
	public String text;

	public TextInfoV5() {
		type=TEXT_INFO;
	}

	public void Read(DataArrayInputStream stream,int size) throws IOException {
		StringBuffer buf=new StringBuffer("");
		for(int i=0 ; i<size ; i++) {
			char c=(char)stream.readByte();
			if(c!='\0')
				buf.append(c);
		}
		text=buf.toString();
	}

	public String toString() {
		return "TextInfoV5: "+text;
	}

	public void Write(DataOutputStream stream) throws IOException {
		if(text!=null) {
	   	   writeHeader(stream);
		   stream.writeBytes(text);
		}
	}

	public int getSize() {
		return text!=null ? text.length() : 0;
	}
}
