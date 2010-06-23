package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.IOException;

public class SignalInfoV5 extends FormatComponentV5 {
	public float sampling_freq;
	public float points_per_microvolt;
	public int   number_of_chanels_in_file;

	public SignalInfoV5() {
		type=SIGNAL_INFO;
	}

	public void Read(DataArrayInputStream stream) throws IOException {
		sampling_freq=stream.readFloat();
		points_per_microvolt=stream.readFloat();
		number_of_chanels_in_file=stream.readShort();
	}

	 public String toString() {
		 return "SignalInfoV5: "+sampling_freq+" "+points_per_microvolt+" "+number_of_chanels_in_file;
	 }

	 public void Write(DataOutputStream stream) throws IOException {
		writeHeader(stream);
		stream.writeFloat(sampling_freq);
		stream.writeFloat(points_per_microvolt);
		stream.writeShort(number_of_chanels_in_file);
	 }

	public int getSize() {
		return 4+4+2;
	}
}
