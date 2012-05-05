package org.signalml.plugin.method.logic;

import java.util.HashMap;
import java.util.Map;

import org.signalml.method.MethodExecutionTracker;

public abstract class AbstractPluginComputationMgrStepTrackerProxy<ComputationProgressPhase>
		implements
		IPluginComputationMgrStepTrackerProxy<ComputationProgressPhase> {

	protected final PluginCheckedThreadGroup threadGroup;
	protected final MethodExecutionTracker tracker;
	
	protected final Map<IPluginComputationMgrStep, Integer> tickerLimits;
	
	protected ComputationProgressPhase phase;

	public AbstractPluginComputationMgrStepTrackerProxy(
			PluginCheckedThreadGroup threadGroup, MethodExecutionTracker tracker) {
		this.threadGroup = threadGroup;
		this.tracker = tracker;
		this.tickerLimits = new HashMap<IPluginComputationMgrStep, Integer>();
		this.phase = null;
	}

	@Override
	public boolean isInterrupted() {
		return this.threadGroup.isShutdownStarted() || Thread.interrupted();
	}

	@Override
	public boolean isRequestingAbort() {
		boolean result = false;
		synchronized (this.tracker) {
			result = this.tracker.isRequestingAbort();
		}
		
		if (result) {
			synchronized (this.tracker) {
				int limits[] = this.tracker.getTickerLimits();
				if (limits.length > 0) {
					this.tracker.setTicker(0, limits[0]);
				}
			}
		}
		
		return result;
	}
	
	@Override
	public void setTickerLimit(IPluginComputationMgrStep step, int limit) {
		int limits[] = this.tracker.getTickerLimits();
		if (limits.length <= 0) {
			return;
		}
		
		Integer prevValue = this.tickerLimits.get(step);
		int diff = prevValue == null ? limit : (limit - prevValue);
		
		this.tickerLimits.put(step, limit);
				
		this.tracker.setTickerLimit(0, limits[0] + diff);
	}
	
	@Override
	public void setProgressPhase(ComputationProgressPhase phase,
			Object... arguments) {
		this.phase = phase;
		String message = this.getMessageForPhase(phase, arguments);
		if (message != null) {
			synchronized (this.tracker) {
				this.tracker.setMessage(message);
			}
		}
	}
	
	protected String getMessageForPhase(ComputationProgressPhase phase, Object ... arguments) {
		return null;
	}
}
