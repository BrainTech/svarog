/* BookAverageMethodDescriptor.java created 2007-10-22
 *
 */

package org.signalml.app.method.bookaverage;

import org.signalml.app.document.BookDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.view.book.BookView;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.domain.book.StandardBook;
import org.signalml.method.bookaverage.BookAverageData;
import org.signalml.method.bookaverage.BookAverageMethod;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.plugin.export.signal.Document;

/**
 * BookAverageMethodDescriptor
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * (+ fixed by) piotr@develancer.pl
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
		Document document = methodManager.getActionFocusManager().getActiveDocument();
		if (!(document instanceof BookDocument)) {
			OptionPane.showNoActiveBook(methodManager.getDialogParent());
			return null;
		}
		BookDocument bookDocument = (BookDocument) document;
		BookView bookView = (BookView) bookDocument.getDocumentView();
		StandardBook book = bookDocument.getBook();
		if (book.getSegmentCount() == 0 || book.getSegmentAt(0).length == 0) {
			OptionPane.showError(methodManager.getDialogParent(), "Book contains no valid segments!");
			return null;
		}

		int segmentCount = book.getSegmentCount();
		int currentChannel = bookView.getCurrentChannel();

		BookAverageData data = new BookAverageData();
		data.setBook(book);
		data.setPalette(bookView.getPlot().getPalette());
		data.setScaleType(bookView.getPlot().getScaleType());
		data.setMinSegment(0);
		data.setMaxSegment(segmentCount - 1);
		data.setMinFrequency(0.0);
		data.setMaxFrequency(book.getSamplingFrequency() / 2);
		data.setMinPosition(0.0);
		data.setMaxPosition(book.getSegmentAt(0)[0].getSegmentTimeLength());
		data.setWidth(400);
		data.setHeight(400);
		data.addChannel(currentChannel);

		return data;
	}

}
