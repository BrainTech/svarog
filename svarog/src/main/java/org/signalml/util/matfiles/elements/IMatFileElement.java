package org.signalml.util.matfiles.elements;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This interface specifies the methods that must be implemented by
 * each element that can be written to a MAT file.
 *
 * @author Piotr Szachewicz
 */
public interface IMatFileElement {

	/**
	 * Writes the element to the {@link DataOutputStream}.
	 * @param dataOutputStream the stream to which this element should be written.
	 * @throws IOException thrown when an error while writing occurs.
	 */
	void write(DataOutputStream dataOutputStream) throws IOException;

	/**
	 * Returns the number of bytes that this element will use while writing it
	 * to the MAT file. (More precisely: the number of bytes that will be written
	 * to the {@link DataOutputStream} when {@link IMatFileElement#write(DataOutputStream)}
	 * method will be invoked.
	 *
	 * @return the size of this element.
	 */
	int getTotalSizeInBytes();
}
