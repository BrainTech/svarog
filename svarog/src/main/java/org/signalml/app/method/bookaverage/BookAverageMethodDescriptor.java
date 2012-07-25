/* BookAverageMethodDescriptor.java created 2007-10-22
 *
 */

package org.signalml.app.method.bookaverage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.method.bookaverage.BookAverageMethod;
import org.signalml.plugin.export.method.BaseMethodData;

/** BookAverageMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageMethodDescriptor implements ApplicationMethodDescriptor {

	public static final String RUN_METHOD_STRING = _("Average books");
	public static final String ICON_PATH = "org/signalml/app/icon/runmethod.png";

	private BookAverageMethod method;
	private BookAverageMethodConfigurer configurer;
	private BookAverageMethodConsumer consumer;

	public BookAverageMethodDescriptor(BookAverageMethod method) {
		this.method = method;
	}

	@Override
	public BookAverageMethod getMethod() {
		return method;
	}

	@Override
	public String getName() {
		return RUN_METHOD_STRING;
	}

	@Override
	public String getIconPath() {
		return ICON_PATH;
	}


	@Override
	public MethodPresetManager getPresetManager(ApplicationMethodManager methodManager, boolean existingOnly) {
		return null;
	}

	@Override
	public BookAverageMethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new BookAverageMethodConfigurer();
			configurer.initialize(methodManager);
		}
		return configurer;
	}

	@Override
	public BookAverageMethodConsumer getConsumer(ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new BookAverageMethodConsumer();
		}
		return consumer;
	}

	@Override
	public BaseMethodData createData(ApplicationMethodManager methodManager) {
		// TODO may not be enough
		return method.createData();
	}

}
