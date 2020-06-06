package org.signalml.util.matfiles;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.signalml.util.matfiles.elements.DataElement;
import org.signalml.util.matfiles.elements.Header;

/**
 * This class is used to write some data to the MAT file.
 * The written data is uncompressed - if you want to compress
 * it use {@link CompressedMatlabFileWriter} instead.
 *
 * @author Piotr Szachewicz
 */
public class MatlabFileWriter {

	/**
	 * The file to which the data will be written.
	 */
	protected File file;

	/**
	 * MAT file header.
	 */
	protected Header header;

	/**
	 * Elements that will be written to the MAT file.
	 */
	protected List<DataElement> dataElements = new ArrayList<DataElement>();

	/**
	 * Creates this writer.
	 * @param file file to which the data will be written.
	 */
	public MatlabFileWriter(File file) {
		this.file = file;
		this.header = new Header();
	}

	/**
	 * Adds a {@link DataElement} that should be written
	 * to the MAT file.
	 * @param dataElement element to be written.
	 */
	public void addElement(DataElement dataElement) {
		this.dataElements.add(dataElement);
	}

	/**
	 * Writes all elements added using {@link MatlabFileWriter#addElement(DataElement)}
	 * to file.
	 * @throws IOException thrown when an error occurs while writing data
	 * to file.
	 */
	public void write() throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

		header.write(dataOutputStream);
		writeData(dataOutputStream);

		dataOutputStream.close();
	}

	/**
	 * Write all {@link DataElement data elements} to the {@link DataOutputStream}.
	 * @param dataOutputStream the {@link DataOutputStream} to which the data
	 * will be written.
	 * @throws IOException thrown when an error occurs while writing data
	 * to file.
	 */
	protected void writeData(DataOutputStream dataOutputStream) throws IOException {
		for (DataElement dataElement: dataElements) {
			dataElement.write(dataOutputStream);
		}
	}

}
