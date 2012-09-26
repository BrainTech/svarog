/* IterableSensitivity.java created 2007-12-05
 *
 */

package org.signalml.plugin.newartifact.data;

import org.signalml.method.InputDataException;
import org.signalml.method.iterator.IterableNumericParameter;

/** IterableSensitivity
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewIterableSensitivity implements IterableNumericParameter {

	private static final Float STEP_SIZE = new Float(0.1F);
	private static final Float DEFAULT_START = new Float(0F);
	private static final Float DEFAULT_END = new Float(100F);

	private NewArtifactParameters parameters;
	private NewArtifactType artifactType;

	public NewIterableSensitivity(NewArtifactParameters parameters, NewArtifactType artifactType) {
		if (parameters == null) {
			throw new NullPointerException("No parameters");
		}
		if (artifactType == null) {
			throw new NullPointerException("No artifact type");
		}
		this.parameters = parameters;
		this.artifactType = artifactType;
	}

	public NewArtifactParameters getParameters() {
		return parameters;
	}

	public NewArtifactType getArtifactType() {
		return artifactType;
	}

	@Override
	public String getName() {
		return artifactType.toString();
	}

	@Override
	public Class<?> getValueClass() {
		return Float.class;
	}

	@Override
	public Object getValue() {
		return new Float(parameters.getSensitivity(artifactType));
	}

	@Override
	public void setValue(Object value) throws InputDataException {
		if (!(value instanceof Float)) {
			throw new ClassCastException("Bad iterable value class");
		}
		// TODO any validation?
		parameters.setSensitivity(artifactType, ((Float) value).floatValue());
	}

	@Override
	public Object setIterationValue(Object startValue, Object endValue, int iteration, int totalIterations) {

		if (!(startValue instanceof Float)) {
			throw new ClassCastException("Bad iterable value class");
		}
		if (!(endValue instanceof Float)) {
			throw new ClassCastException("Bad iterable value class");
		}

		float start = ((Float) startValue).floatValue();
		float end = ((Float) endValue).floatValue();

		float step = (end-start) / totalIterations;
		float value = start + (step * iteration);

		// TODO any validation?
		parameters.setSensitivity(artifactType, value);

		return new Float(value);

	}

	@Override
	public Object getDefaultStartValue() {
		return DEFAULT_START;
	}

	@Override
	public Object getDefaultEndValue() {
		return DEFAULT_END;
	}

	@Override
	public Comparable<? extends Number> getMaximum() {
		// no max
		return null;
	}

	@Override
	public Comparable<? extends Number> getMinimum() {
		// no min
		return null;
	}

	@Override
	public Number getStepSize() {
		return STEP_SIZE;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
		//return artifactType.getArguments();
	}

	@Override
	public String[] getCodes() {
		return new String[] { "newArtifactMethod.iterableSensitivity." + artifactType.toString() };
	}

	@Override
	public String getDefaultMessage() {
		return ""; //TODO
		//return artifactType.getDefaultMessage();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NewIterableSensitivity) {
			NewIterableSensitivity s = (NewIterableSensitivity) obj;
			return (s.parameters == this.parameters) && (s.artifactType == this.artifactType);
		}
		return false;
	}

}
