package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatlabFileWriter {

	private File file;
	private Header header;

	private List<DataElement> dataElements = new ArrayList<DataElement>();

	public MatlabFileWriter(File file) {
		this.file = file;
		this.header = new Header();
	}

	public void addElement(DataElement dataElement) {
		this.dataElements.add(dataElement);
	}

	public void write() throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

		header.write(dataOutputStream);
		for (DataElement dataElement: dataElements) {
			dataElement.write(dataOutputStream);
		}

	}

}
