package org.signalml.util.matfiles.array.lazy;

public interface ILazyDoubleArrayDataProvider {

	double[][] getDataChunk(int column, int length);
	int getWidth();
	int getHeight();

}
