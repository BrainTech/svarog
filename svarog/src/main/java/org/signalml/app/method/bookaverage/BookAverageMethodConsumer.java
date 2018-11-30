package org.signalml.app.method.bookaverage;

import java.awt.Window;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodResultConsumer;
import org.signalml.method.Method;
import org.signalml.method.bookaverage.BookAverageData;
import org.signalml.method.bookaverage.BookAverageResult;
import org.signalml.method.bookaverage.TimeFrequencyMapPresenter;
import org.signalml.plugin.export.SignalMLException;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * BookAverageMethodConsumer
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * (+ fixed by) piotr@develancer.pl
 */
public class BookAverageMethodConsumer implements InitializingMethodResultConsumer {

	private Window dialogParent;

	@Override
	public void initialize(ApplicationMethodManager manager) {
		dialogParent = manager.getDialogParent();
	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		BookAverageData data = (BookAverageData) methodData;
		BookAverageResult result = (BookAverageResult) methodResult;

		TimeFrequencyMapPresenter presenter = new TimeFrequencyMapPresenter(dialogParent);
		presenter.showResults(_("Average map of MP decomposition"), result.getMap(), data.getMinFrequency(), data.getMaxFrequency(), data.getMaxPosition() - data.getMinPosition());
		return true;
	}
}
