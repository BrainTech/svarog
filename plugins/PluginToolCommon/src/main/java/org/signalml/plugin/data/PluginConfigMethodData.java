package org.signalml.plugin.data;

import org.signalml.plugin.method.PluginAbstractMethod;
import org.signalml.plugin.method.PluginAbstractMethodDescriptor;

public class PluginConfigMethodData {

	private String methodName;

	private String iconPath;

	private String runMethodString;

	private PluginAbstractMethodDescriptor methodDescriptor;

	private PluginAbstractMethod method;

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setRunMethodString(String runMethodString) {
		this.runMethodString = runMethodString;
	}

	public String getRunMethodString() {
		return runMethodString;
	}

	public void setMethodDescriptor(PluginAbstractMethodDescriptor methodDescriptor) {
		this.methodDescriptor = methodDescriptor;
	}

	public PluginAbstractMethodDescriptor getMethodDescriptor() {
		return methodDescriptor;
	}

	public void setMethod(PluginAbstractMethod method) {
		this.method = method;
	}

	public PluginAbstractMethod getMethod() {
		return method;
	}
}
