package org.signalml.domain.book;

public interface StandardBookSegmentWriter {
	void setSegmentNumber(int seg);

	void setChannelNumber(int channel);

	void setSignalSamples(float[] signal);

	void addAtom(StandardBookAtomWriter atom);

	public int getSegmentLength();

	public void setSegmentLength(int segmentLength);
}
