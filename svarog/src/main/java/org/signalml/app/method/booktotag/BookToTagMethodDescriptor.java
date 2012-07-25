/* BookToTagMethodDescriptor.java created 2007-10-22
 *
 */

package org.signalml.app.method.booktotag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.document.BookDocument;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.method.booktotag.BookToTagData;
import org.signalml.method.booktotag.BookToTagMethod;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.plugin.export.signal.Document;

/** BookToTagMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagMethodDescriptor implements ApplicationMethodDescriptor {

	public static final String RUN_METHOD_STRING = _("Tag book atoms");
	public static final String ICON_PATH = "org/signalml/app/icon/runmethod.png";

	private BookToTagMethod method;
	private BookToTagMethodConfigurer configurer;
	private BookToTagMethodConsumer consumer;

	public BookToTagMethodDescriptor(BookToTagMethod method) {
		this.method = method;
	}

	@Override
	public BookToTagMethod getMethod() {
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
	public BookToTagMethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new BookToTagMethodConfigurer();
			configurer.initialize(methodManager);
		}
		return configurer;
	}

	@Override
	public BookToTagMethodConsumer getConsumer(ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new BookToTagMethodConsumer();
			consumer.initialize(methodManager);
		}
		return consumer;
	}

	@Override
	public BaseMethodData createData(ApplicationMethodManager methodManager) {

		Document document = methodManager.getActionFocusManager().getActiveDocument();
		if (!(document instanceof BookDocument)) {
			OptionPane.showNoActiveBook(methodManager.getDialogParent());
			return null;
		}
		BookDocument bookDocument = (BookDocument) document;

		BookToTagData data = new BookToTagData();
		data.setBook(bookDocument.getBook());

		return data;

	}

}
