/* StagerMethod.java created 2008-02-08
 *
 */

package org.signalml.plugin.newstager.method;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.app.document.BookDocument;
import org.signalml.domain.book.StandardBook;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.plugin.method.PluginAbstractMethod;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.NewStagerResult;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrData;
import org.signalml.plugin.newstager.logic.mgr.NewStagerComputationMgr;
import org.signalml.plugin.tool.PluginAccessHelper;
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

	protected static final Logger logger = Logger.getLogger(NewStagerMethod.class);

	private static final String UID = "3c5b3e3d-c6b5-467b-8c20-fee30874889c";
	private static final int[] VERSION = new int[] { 1, 0 };

	@Override
	public Object doComputation(Object data, MethodExecutionTracker tracker)
	throws ComputationException {
		NewStagerData stagerData;
		try {
			stagerData = (NewStagerData) data;
		} catch (ClassCastException e) {
			throw new ComputationException(e);
		}


		NewStagerComputationMgr mgr = new NewStagerComputationMgr();

		try {
			return mgr.compute(new NewStagerMgrData(stagerData, this.getStagerConstants(stagerData)), tracker);
		} catch (SignalMLException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
			return null;
		}
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
	public BaseMethodData createData() {
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
		return 1;
	}

	@Override
	public String getTickerLabel(int ticker) {
		if (ticker == 0) {
			return _("Sleep staging");
		} else {
			throw new IndexOutOfBoundsException("No ticker [" + ticker + "]");
		}
	}


	private NewStagerConstants getStagerConstants(NewStagerData stagerData) throws SignalMLException {
		SvarogAccess access = PluginAccessHelper.GetSvarogAccess();
		if (access == null) {
			throw new SignalMLException();
		}

		BookDocument doc;
		try {
			doc = new BookDocument(new File(stagerData.getParameters().bookFilePath));
		} catch (IOException e) {
			throw new SignalMLException(e);
		}
		StandardBook book = doc.getBook();

		return new NewStagerConstants(book.getSamplingFrequency(),
									  (int) access.getSignalAccess().getActiveSignalDocument().getPageSize(),
									  book.getSegmentCount(),
									  NewStagerConstants.DEFAULT_MUSCLE_THRESHOLD,
									  NewStagerConstants.DEFAULT_MUSCLE_THRESHOLD_RATE,
									  NewStagerConstants.DEFAULT_AMPLITUDE_A,
									  NewStagerConstants.DEFAULT_AMPLITUDE_B,
									  NewStagerConstants.DEFAULT_ALPHA_OFFSET,
									  NewStagerConstants.DEFAULT_DELTA_OFFSET,
									  NewStagerConstants.DEFAULT_SPINDLE_OFFSET);
	}

}
