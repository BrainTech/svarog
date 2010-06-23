package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.IOException;

public class WebSiteInfoV5 extends FormatComponentV5 {
	public String www;

	public WebSiteInfoV5() {
	   type=WEB_SITE_INFO;
	}

	public void Read(DataArrayInputStream stream, int size) throws IOException {
		StringBuffer buf=new StringBuffer("");
		for(int i=0 ; i<size ; i++) {
			char c=(char)stream.readByte();
			if(c!='\0') {
				buf.append(c);
			}
		}

		www=buf.toString();
	}

	 public String toString() {
		return "WebSiteInfoV5: "+www;
	}

	public void Write(DataOutputStream stream) throws IOException {
		if(www!=null) {
		   writeHeader(stream);
		   stream.writeBytes(www);
		}
	}

	public int getSize() {
		return www!=null ? www.length() : 0;
	}
}
