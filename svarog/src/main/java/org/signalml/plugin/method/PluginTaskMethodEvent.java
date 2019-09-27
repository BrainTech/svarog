package org.signalml.plugin.method;

import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.task.TaskEvent;
import org.signalml.task.TaskEventListener;

public class PluginTaskMethodEvent implements TaskEventListener {

	private final BaseMethodData methodData;

	public PluginTaskMethodEvent(BaseMethodData methodData) {
		this.methodData = methodData;
	}
	
	@Override
	public void taskStarted(TaskEvent ev) {
		//do nothing
	}

	@Override
	public void taskSuspended(TaskEvent ev) {
		//do nothing
	}

	@Override
	public void taskResumed(TaskEvent ev) {
		//do nothing	
	}

	@Override
	public void taskAborted(TaskEvent ev) {
		this.performClean();		
	}

	@Override
	public void taskFinished(TaskEvent ev) {
		this.performClean();
	}

	@Override
	public void taskTickerUpdated(TaskEvent ev) {
		//do nothing
	}

	@Override
	public void taskMessageSet(TaskEvent ev) {
		//do nothing
	}

	@Override
	public void taskRequestChanged(TaskEvent ev) {
		//do nothing		
	}

	private void performClean() {
		this.methodData.dispose();
	}
	
}
