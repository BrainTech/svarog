/* LabelledPropertyDescriptor.java created 2007-10-05
 * 
 */

package org.signalml.app.model;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.context.MessageSourceResolvable;

/** LabelledPropertyDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LabelledPropertyDescriptor extends PropertyDescriptor implements MessageSourceResolvable {

	private String messageCode;
	
	public LabelledPropertyDescriptor(String messageCode, String propertyName, Class<?> beanClass, String readMethodName, String writeMethodName) throws IntrospectionException {
		super(propertyName, beanClass, readMethodName, writeMethodName);
		this.messageCode = messageCode;
	}

	public LabelledPropertyDescriptor(String messageCode, String propertyName, Class<?> beanClass) throws IntrospectionException {
		super(propertyName, beanClass);
		this.messageCode = messageCode;
	}

	public LabelledPropertyDescriptor(String messageCode, String propertyName, Method readMethod, Method writeMethod) throws IntrospectionException {
		super(propertyName, readMethod, writeMethod);
		this.messageCode = messageCode;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { messageCode };
	}

	@Override
	public String getDefaultMessage() {
		return "??? Property " + getName();
	}

}
