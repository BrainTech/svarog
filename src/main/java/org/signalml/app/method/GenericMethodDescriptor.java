/* GenericMethodDescriptor.java created 2007-10-22
 * 
 */

package org.signalml.app.method;

import org.signalml.method.Method;

/** GenericMethodDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class GenericMethodDescriptor {

	private Method method;
	
	private String nameCode;
	private String iconPath;
	
	private MethodConfigurer configurer;
	private MethodResultConsumer consumer;
	
	private GenericMethodDescriptor(Method method) {
		this.method = method;
	}

	public String getNameCode() {
		return nameCode;
	}

	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public Method getMethod() {
		return method;
	}

	public MethodConfigurer getConfigurer() {
		return configurer;
	}

	public void setConfigurer(MethodConfigurer configurer) {
		this.configurer = configurer;
	}

	public MethodResultConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(MethodResultConsumer consumer) {
		this.consumer = consumer;
	}
		
}
