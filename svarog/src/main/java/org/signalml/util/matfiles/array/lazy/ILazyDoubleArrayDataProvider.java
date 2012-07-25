package org.signalml.util.matfiles.array.lazy;

/**
 * This interface is used by the classes that are able to provide
 * data to the {@link LazyExportDoubleArray} in a lazy mode, i.e.
 * not as a whole array at once, but divided in fragments.
 *
 * @author Piotr Szachewicz
 */
public interface ILazyDoubleArrayDataProvider {

	/**
	 * Returns a chunk of the data.
	 * @param column the starting column of the array
	 * that will be returned.
	 * @param length the number of columns of data that will
	 * be returned.
	 * @return the fragment of the original double array.
	 */
	double[][] getDataChunk(int column, int length);

	/**
	 * Returns the number of columns that the original double
	 * array contains.
	 *
	 * @return the number of columns.
	 */
	int getNumberOfColumns();

	/**
	 * Returns the number of rows that the original double
	 * array contains.
	 * @return the number of rows.
	 */
	int getNumberOfRows();

}
