/* StagerMethod.java created 2008-02-08
 * 
 */

package org.signalml.plugin.newstager.method;

import static org.signalml.plugin.newstager.NewStagerPlugin._;

import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.method.PluginAbstractMethod;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.NewStagerResult;
import org.signalml.plugin.tool.PluginResourceRepository;
import org.springframework.validation.Errors;

/**
 * StagerMethod
 * 
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z
 *         o.o.
 */

public class NewStagerMethod extends PluginAbstractMethod implements
		TrackableMethod, IterableMethod {

	private static final String UID = "3c5b3e3d-c6b5-467b-8c20-fee30874889c";
	private static final int[] VERSION = new int[] { 1, 0 };

	@Override
	public Object doComputation(Object data, MethodExecutionTracker tracker)
			throws ComputationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object digestIterationResult(int iteration, Object result) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IterableParameter[] getIterableParameters(Object data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void validate(Object dataObj, Errors errors) {
		super.validate(dataObj, errors);
		if (!errors.hasErrors()) {
			NewStagerData data = (NewStagerData) dataObj;
			data.validate(errors);
		}
	}

	@Override
	public String getName() {
		try {
			return ((PluginConfigForMethod) PluginResourceRepository
					.GetResource("config", NewStagerPlugin.class))
					.getMethodConfig().getMethodName();
		} catch (PluginException e) {
			return "";
		}
	}

	@Override
	public Object createData() {
		return null;
	}

	@Override
	public boolean supportsDataClass(Class<?> clazz) {
		return NewStagerData.class.isAssignableFrom(clazz);
	}

	@Override
	public Class<?> getResultClass() {
		return NewStagerResult.class;
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public int[] getVersion() {
		return VERSION;
	}

	@Override
	public int getTickerCount() {
		return 2;
	}

	@Override
	public String getTickerLabel(int ticker) {

		if (ticker == 0) {
			return _("stagerMethod.stepTicker");
		} else if (ticker == 1) {
			return _("stagerMethod.progressTicker");
		} else {
			throw new IndexOutOfBoundsException("No ticker [" + ticker + "]");
		}

	}
}
