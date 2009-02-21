package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.IOException;

public class DecompositionInfoV5 extends FormatComponentV5 {
    public float energy_percent;
    public int   max_number_of_iterations;
    public int   dictionary_size;
    public byte  dictionary_type;

    public DecompositionInfoV5() {
		type=DECOMP_INFO;
	}

    public void Read(DataArrayInputStream stream) throws IOException {
        energy_percent=stream.readFloat();
        max_number_of_iterations=stream.readInt();
        dictionary_size=stream.readInt();
        dictionary_type=stream.readByte();
    }

    public String toString() {
		return "DecompositionInfoV5: "+energy_percent+" "+max_number_of_iterations+" "+dictionary_size+" "+dictionary_type;
	}

	public void Write(DataOutputStream stream) throws IOException {
	    writeHeader(stream);
		stream.writeFloat(energy_percent);
		stream.writeInt(max_number_of_iterations);
		stream.writeInt(dictionary_size);
		stream.writeByte(dictionary_type);
	}

	public int getSize() {
		return 4+4+4+1;
	}
}
