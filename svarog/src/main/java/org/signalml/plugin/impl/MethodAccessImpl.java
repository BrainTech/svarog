package org.signalml.plugin.impl;

import org.signalml.app.view.dialog.TaskStatusDialog;
import org.signalml.plugin.export.method.SvarogAccessMethod;
import org.signalml.plugin.export.method.SvarogMethod;
import org.signalml.plugin.export.method.SvarogMethodConfigurer;
import org.signalml.plugin.export.method.SvarogMethodDescriptor;
import org.signalml.plugin.export.method.SvarogTask;

/**
 * Methods and tasks in Svarog core (facade for Svarog plugins).
 *
 * @author Stanislaw Findeisen
 */
public class MethodAccessImpl extends AbstractAccess implements SvarogAccessMethod {
    
	private MethodAccessImpl() { }

	private static final MethodAccessImpl _instance = new MethodAccessImpl();

	protected static MethodAccessImpl getInstance() {
		return _instance;
	}
    
	@Override
	public SvarogMethodDescriptor getMethodDescriptor(SvarogMethod method) {
		return (SvarogMethodDescriptor) (getViewerElementManager().getMethodManager().getMethodData(method));
	}

	@Override
	public void addTask(SvarogTask task) {
		getViewerElementManager().getTaskManager().addTask(task);
	}

	@Override
	public void startTask(SvarogTask task) {
		getViewerElementManager().getTaskManager().startTask(task);
	}

	@Override
	public TaskStatusDialog getTaskStatusDialog(SvarogTask task) {
		return getViewerElementManager().getTaskManager().getStatusDialogForTask(task);
	}

	@Override
	public void registerMethod(SvarogMethod method) {
		getViewerElementManager().getMethodManager().registerMethod(method);
	}

	@Override
	public void setMethodDescriptor(SvarogMethod method, SvarogMethodDescriptor methodDescriptor) {
		getViewerElementManager().getMethodManager().setMethodData(method, methodDescriptor);
	}

	@Override
	public SvarogMethodConfigurer getConfigurer(SvarogMethodDescriptor descriptor) {
		return (SvarogMethodConfigurer) (descriptor.getConfigurer(getViewerElementManager().getMethodManager()));
	}

	@Override
	public Object createData(SvarogMethodDescriptor descriptor) {
		return descriptor.createData(getViewerElementManager().getMethodManager());
	}
}
