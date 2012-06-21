package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.IOException;

public interface IMatFileElement {

	void write(DataOutputStream dataOutputStream) throws IOException;
	int getTotalSizeInBytes();
}
