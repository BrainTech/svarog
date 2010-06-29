/* DefaultMutableBookSegment.java created 2008-02-24
 *
 */

package org.signalml.domain.book;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/** DefaultMutableBookSegment
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultMutableBookSegment implements MutableBookSegment {

	private float decompositionEnergy;
	private float signalEnergy;
	private float[] signalSamples;

	private int channelNumber;
	private int segmentNumber;

	private float samplingFrequency;
	private float segmentTime;

	private int segmentLength;

	private ArrayList<StandardBookAtom> atoms;

	public DefaultMutableBookSegment(float samplingFrequency, int channelNumber, int segmentNumber, float segmentTime, int segmentLength) {
		this.samplingFrequency = samplingFrequency;
		this.channelNumber = channelNumber;
		this.segmentNumber = segmentNumber;
		this.segmentTime = segmentTime;
		this.segmentLength = segmentLength;
		atoms = new ArrayList<StandardBookAtom>();
	}

	public DefaultMutableBookSegment(StandardBookSegment segment) {
		this(segment.getSamplingFrequency(), segment.getChannelNumber(), segment.getSegmentNumber(), segment.getSegmentTime(), segment.getSegmentLength());

		this.decompositionEnergy = segment.getDecompositionEnergy();
		this.signalEnergy = segment.getSignalEnergy();
		this.signalSamples = segment.getSignalSamples();

		int atomCnt = segment.getAtomCount();
		atoms.ensureCapacity(atomCnt);
		for (int i=0; i<atomCnt; i++) {
			atoms.add(new DefaultBookAtom(segment.getAtomAt(i)));
		}

	}

	public DefaultMutableBookSegment(StandardBookSegment segment, int targetSegmentNumber, float targetSegmentTime) {
		this(segment);

		this.segmentNumber = targetSegmentNumber;
		this.segmentTime = targetSegmentTime;
	}

	@Override
	public int addAtom(StandardBookAtom atom) {
		int index = atoms.size();
		atoms.add(atom);
		return index;
	}

	@Override
	public void clear() {
		atoms.clear();
	}

	@Override
	public StandardBookAtom createAtom(int type, int iteration, float modulus, float amplitude, int position, int scale, int frequency, float phase) {
		return new DefaultBookAtom(samplingFrequency,segmentLength,type,iteration,modulus,frequency,position,scale,amplitude,phase);
	}

	@Override
	public StandardBookAtom removeAtomAt(int index) {
		return atoms.remove(index);
	}

	@Override
	public void setAtomAt(int index, StandardBookAtom atom) {
		atoms.set(index, atom);
	}

	@Override
	public void setDecompositionEnergy(float decompositionEnergy) {
		this.decompositionEnergy = decompositionEnergy;
	}

	@Override
	public void setSignalEnergy(float signalEnergy) {
		this.signalEnergy = signalEnergy;
	}

	@Override
	public void setSignalSamples(float[] samples) {
		this.signalSamples = samples;
	}

	@Override
	public StandardBookAtom getAtomAt(int index) {
		return atoms.get(index);
	}

	@Override
	public int getAtomCount() {
		return atoms.size();
	}

	@Override
	public int getChannelNumber() {
		return channelNumber;
	}

	@Override
	public float getDecompositionEnergy() {
		return decompositionEnergy;
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("No properties");
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		Vector<String> names = new Vector<String>();
		return names.elements();
	}

	@Override
	public int getSegmentLength() {
		return segmentLength;
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	@Override
	public float getSegmentTimeLength() {
		return (segmentLength / samplingFrequency);
	}

	@Override
	public int getSegmentNumber() {
		return segmentNumber;
	}

	@Override
	public float getSegmentTime() {
		return segmentTime;
	}

	@Override
	public float getSignalEnergy() {
		return signalEnergy;
	}

	@Override
	public float[] getSignalSamples() {
		return signalSamples;
	}

	@Override
	public boolean hasSignal() {
		return (signalSamples != null);
	}

	@Override
	public int indexOfAtom(StandardBookAtom atom) {
		return atoms.indexOf(atom);
	}

}
