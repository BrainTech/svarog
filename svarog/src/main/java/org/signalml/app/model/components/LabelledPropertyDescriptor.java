/* LabelledPropertyDescriptor.java created 2007-10-05
 *
 */

package org.signalml.app.model.components;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/** LabelledPropertyDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LabelledPropertyDescriptor extends PropertyDescriptor {

	private String displayName;

	public LabelledPropertyDescriptor(String messageCode, String propertyName, Class<?> beanClass, String readMethodName, String writeMethodName) throws IntrospectionException {
		super(propertyName, beanClass, readMethodName, writeMethodName);
		this.displayName = messageCode;
	}

	public LabelledPropertyDescriptor(String messageCode, String propertyName, Class<?> beanClass) throws IntrospectionException {
		super(propertyName, beanClass);
		this.displayName = messageCode;
	}

	public LabelledPropertyDescriptor(String messageCode, String propertyName, Method readMethod, Method writeMethod) throws IntrospectionException {
		super(propertyName, readMethod, writeMethod);
		this.displayName = messageCode;
	}

	public String getDefaultMessage() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
