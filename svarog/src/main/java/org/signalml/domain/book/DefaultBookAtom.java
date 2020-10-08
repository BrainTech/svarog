/* DefaultBookAtom.java created 2008-02-24
 *
 */

package org.signalml.domain.book;

import java.util.Enumeration;
import java.util.Vector;

/** DefaultBookAtom
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultBookAtom implements StandardBookAtom {

	private float samplingFrequency;
	private int baseLength;

	private int type;
	private int iteration;
	private float modulus;
	private float frequency;
	private float position;
	private float scale;
	private float amplitude;
	private float phase;

	protected DefaultBookAtom() {
	}

	public DefaultBookAtom(float samplingFrequency, int baseLength, int type, int iteration, float modulus, float frequency, float position, float scale, float amplitude, float phase) {
		this.samplingFrequency = samplingFrequency;
		this.baseLength = baseLength;
		this.type = type;
		this.iteration = iteration;
		this.modulus = modulus;
		this.frequency = frequency;
		this.position = position;
		this.scale = scale;
		this.amplitude = amplitude;
		this.phase = phase;
	}

	public DefaultBookAtom(StandardBookAtom atom) {
		this.samplingFrequency = atom.getSamplingFrequency();
		this.baseLength = atom.getBaseLength();
		this.iteration = atom.getIteration();
		this.type = atom.getType();
		this.modulus = atom.getModulus();
		this.frequency = (int) atom.getFrequency();
		this.position = atom.getPosition();
		this.scale = atom.getScale();
		this.amplitude = atom.getAmplitude();
		this.phase = atom.getPhase();
	}

	@Override
	public int getIteration() {
		return iteration;
	}

	@Override
	public int getBaseLength() {
		return baseLength;
	}

	@Override
	public float getSamplingFrequency() {
		return samplingFrequency;
	}

	@Override
	public float getAmplitude() {
		return amplitude;
	}

	@Override
	public float getNaturalFrequency() {
		return frequency;
	}

	@Override
	public float getFrequency() {
		return this.frequency;
	}

	@Override
	public float getHzFrequency() {
		return (float)((((double) frequency) / baseLength) * samplingFrequency);
	}

	@Override
	public float getModulus() {
		return modulus;
	}

	@Override
	public float getPhase() {
		return phase;
	}

	@Override
	public float getPosition() {
		return position;
	}

	@Override
	public float getTimePosition() {
		return (position / samplingFrequency);
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public float getTimeScale() {
		return (scale / samplingFrequency);
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("No properties");
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		Vector<String> names = new Vector<>();
		return names.elements();
	}

}
