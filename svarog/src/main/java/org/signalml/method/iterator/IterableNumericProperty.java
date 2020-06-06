/* IterableNumericProperty.java created 2007-12-05
 *
 */

package org.signalml.method.iterator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.signalml.exception.SanityCheckException;
import org.signalml.method.InputDataException;

/** IterableNumericProperty
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class IterableNumericProperty implements IterableNumericParameter {

	protected static final Logger logger = Logger.getLogger(IterableNumericProperty.class);

	private static HashMap<Class<?>, NumberCaster> casterMap = new HashMap<Class<?>, NumberCaster>();

	static {

		NumberCaster byteCaster = new ToByteNumberCaster();
		NumberCaster shortCaster = new ToShortNumberCaster();
		NumberCaster integerCaster = new ToIntegerNumberCaster();
		NumberCaster longCaster = new ToLongNumberCaster();
		NumberCaster floatCaster = new ToFloatNumberCaster();
		NumberCaster doubleCaster = new ToDoubleNumberCaster();

		casterMap.put(byte.class, byteCaster);
		casterMap.put(Byte.class, byteCaster);
		casterMap.put(short.class, shortCaster);
		casterMap.put(Short.class, shortCaster);
		casterMap.put(int.class, integerCaster);
		casterMap.put(Integer.class, integerCaster);
		casterMap.put(long.class, longCaster);
		casterMap.put(Long.class, longCaster);
		casterMap.put(float.class, floatCaster);
		casterMap.put(Float.class, floatCaster);
		casterMap.put(double.class, doubleCaster);
		casterMap.put(Double.class, doubleCaster);

	}

	private Object subject;
	private String propertyName;
	private Class<?> propertyType;

	private Method getter = null;
	private Method setter = null;

	private Comparable<? extends Number> minimum;
	private Comparable<? extends Number> maximum;
	private Number stepSize;

	private Number defaultStartValue;
	private Number defaultEndValue;

	private NumberCaster caster;

	public IterableNumericProperty(Object subject, String propertyName) throws IntrospectionException {

		if (subject == null) {
			throw new NullPointerException("No subject");
		}

		this.subject = subject;
		this.propertyName = propertyName;

		PropertyDescriptor descriptor = new PropertyDescriptor(propertyName, subject.getClass());
		propertyType = descriptor.getPropertyType();
		if (propertyType.isPrimitive()) {
			if (propertyType.equals(boolean.class) || propertyType.equals(char.class)) {
				throw new IntrospectionException("Not a number primitive");
			}
		} else {
			if (!Number.class.isAssignableFrom(descriptor.getPropertyType())) {
				throw new IntrospectionException("Not a number class");
			}
		}

		getter = descriptor.getReadMethod();
		setter = descriptor.getWriteMethod();

		caster = casterMap.get(propertyType);
		if (caster == null) {
			throw new SanityCheckException("No caster for class [" + propertyType + "]");
		}

	}

	@Override
	public Comparable<? extends Number> getMinimum() {
		return minimum;
	}

	public void setMinimum(Comparable<? extends Number> minimum) {
		this.minimum = minimum;
	}

	@Override
	public Comparable<? extends Number> getMaximum() {
		return maximum;
	}

	public void setMaximum(Comparable<? extends Number> maximum) {
		this.maximum = maximum;
	}

	@Override
	public Number getStepSize() {
		return stepSize;
	}

	public void setStepSize(Number stepSize) {
		this.stepSize = stepSize;
	}

	@Override
	public Number getDefaultStartValue() {
		return defaultStartValue;
	}

	public void setDefaultStartValue(Number defaultStartValue) {
		this.defaultStartValue = defaultStartValue;
	}

	@Override
	public Number getDefaultEndValue() {
		return defaultEndValue;
	}

	public void setDefaultEndValue(Number defaultEndValue) {
		this.defaultEndValue = defaultEndValue;
	}

	@Override
	public String getName() {
		return propertyName;
	}

	@Override
	public Class<?> getValueClass() {
		return propertyType;
	}

	@Override
	public Object getValue() {

		Object value;
		try {
			value = getter.invoke(subject, new Object[0]);
		} catch (Exception ex) {
			logger.error("Failed to get value", ex);
			value = null;
		}

		return value;

	}

	@Override
	public void setValue(Object value) throws InputDataException {

		try {
			setter.invoke(subject, new Object[] { value });
		} catch (Exception ex) {
			InputDataException exception = new InputDataException(subject, "data");
			exception.rejectValue(propertyName, "error.setIteratedValueFailed", new Object[] { ex.getMessage() }, "error.setIteratedValueFailed");
			throw exception;
		}

	}

	@Override
	public Object setIterationValue(Object startValue, Object endValue, int iteration, int totalIterations) {

		double start = ((Number) startValue).doubleValue();
		double end = ((Number) endValue).doubleValue();

		double step = (end-start) / totalIterations;
		double value = start + (step * iteration);

		Object object = caster.cast(value);

		try {
			setValue(object);
		} catch (InputDataException ex) {
			logger.error("Failed to set iterated value", ex);
		}

		return object;

	}

	@Override
	public Object[] getArguments() {
		return new Object[] { propertyName };
	}

	@Override
	public String[] getCodes() {
		return new String[] { "iterableNumericProperty." + propertyName, "iterableNumericProperty" };
	}

	@Override
	public String getDefaultMessage() {
		return propertyName;
	}

	static interface NumberCaster {
		Number cast(double value);
	}

	static class ToByteNumberCaster implements NumberCaster {

		@Override
		public Number cast(double value) {
			return new Byte((byte) Math.round(value));
		}

	}

	static class ToShortNumberCaster implements NumberCaster {

		@Override
		public Number cast(double value) {
			return new Short((short) Math.round(value));
		}

	}

	static class ToIntegerNumberCaster implements NumberCaster {

		@Override
		public Number cast(double value) {
			return new Integer((int) Math.round(value));
		}

	}

	static class ToLongNumberCaster implements NumberCaster {

		@Override
		public Number cast(double value) {
			return new Long(Math.round(value));
		}

	}

	static class ToFloatNumberCaster implements NumberCaster {

		@Override
		public Number cast(double value) {
			return new Float((float) value);
		}

	}

	static class ToDoubleNumberCaster implements NumberCaster {

		@Override
		public Number cast(double value) {
			return new Double(value);
		}

	}


}
