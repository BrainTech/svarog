package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatlabFileWriter {

	protected File file;
	protected Header header;

	protected List<DataElement> dataElements = new ArrayList<DataElement>();

	public MatlabFileWriter(File file) throws FileNotFoundException {
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
		writeData(dataOutputStream);

		dataOutputStream.close();
	}

	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (DataElement dataElement: dataElements) {
			dataElement.write(dataOutputStream);
		}
	}

}
