/* BookDocument.java created 2007-09-12
 *
 */
package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.domain.book.DefaultBookBuilder;
import org.signalml.domain.book.BookFormatException;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.domain.book.filter.AbstractAtomFilter;
import org.signalml.domain.book.filter.AtomFilter;
import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.exception.ResolvableException;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;


/**
 * The document with a {@link StandardBook book}.
 * Contains the book and the {@link AtomFilterChain chain} of {@link AtomFilter
 * filters}, which can be get or set (book only get).
 * Moreover, apart from functions of {@link AbstractFileDocument}, allows to
 * get parameters of the book:
 * <ul>
 * <li>comment,</li>
 * <li>calibration,</li>
 * <li>date,</li>
 * <li>number of channels in the book, and in the signal from which the book
 * was created,</li>
 * <li>energy percent,</li>
 * <li>sampling frequency,</li>
 * <li>number of segments,</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookDocument extends AbstractFileDocument {

	protected static final Logger logger = Logger.getLogger(BookDocument.class);

	public static final String FILTER_CHAIN_PROPERTY = "filterChain";

	/**
	 * a {@link StandardBook book} that is stored in this document
	 */
	private StandardBook book;
	
	/**
	 * the {@link AtomFilterChain chain} of {@link AtomFilter atom filters}
	 * for this document (to filter atoms in the {@link #book})
	 */
	private AtomFilterChain filterChain;

	/**
	 * Constructor. Creates the new, empty {@link AtomFilterChain chain}
	 * of {@link AtomFilter filters}.
	 * @throws SignalMLException TODO never thrown
	 */
	protected BookDocument() throws SignalMLException {
		super();
		filterChain = new AtomFilterChain();
	}

	/**
	 * Constructor. Sets the {@link StandardBook book} stored in this document.
	 * @param book the book stored in this document.
	 * @throws SignalMLException TODO never thrown
	 */
	public BookDocument(StandardBook book) throws SignalMLException {
		this();
		this.book = book;
	}

	/**
	 * Constructor. Sets the file that backs this document and
	 * {@link #openDocument() opens} this document.
	 * Creates the new, empty {@link AtomFilterChain chain}
	 * of {@link AtomFilter filters}.
	 * @param file the file from which this document will be read
	 * @throws SignalMLException if the file is null or
	 * the document stored in the file has invalid format or other
	 * non I/O error occurs while reading a file
	 * @throws IOException if I/O error occurs while reading the file
	 */
	public BookDocument(File file) throws SignalMLException, IOException {
		super(file);
		filterChain = new AtomFilterChain();
	}

	/**
	 * Returns the {@link StandardBook book} that is stored in this document.
	 * @return the book that is stored in this document
	 */
	public StandardBook getBook() {
		return book;
	}

	@Override
	public String getName() {
		return (backingFile != null ? backingFile.getName() : "");
	}

	/**
	 * Reads this document from the {@link #getBackingFile() backing file}.
	 */
	@Override
	public void openDocument() throws SignalMLException, IOException {

		if (backingFile == null) {
			throw new SignalMLException("error.noBackingFile");
		}

		DefaultBookBuilder bookBuilder = DefaultBookBuilder.getInstance();

		try {
			StandardBook readBook = bookBuilder.readBook(backingFile);
			this.book = readBook;
		} catch (BookFormatException ex) {
			throw new SignalMLException(ex);
		}

	}

	@Override
	public String[] getCodes() {
		return new String[] { "bookDocument" };
	}

	@Override
	public Object[] getArguments() {
		return new Object[] {
		               getName()
		       };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}

	/**
	 * Returns the {@link AtomFilterChain chain} of {@link AtomFilter atom
	 * filters} for this document (to filter atoms in the book).
	 * @return the chain of atom filters for this document
	 */
	public AtomFilterChain getFilterChain() {
		return filterChain;
	}

	/**
	 * Sets the {@link AtomFilterChain chain} of {@link AtomFilter atom
	 * filters} for this document.
	 * Tries to {@link AbstractAtomFilter#initialize() initialize}
	 * every enabled filter.
	 * If the initialization fails disables the filter and informs user
	 * about the error.
	 * @param filterChain the chain of atom filters for this document
	 */
	public void setFilterChain(AtomFilterChain filterChain) {
		if (this.filterChain != filterChain) {
			AtomFilterChain oldChain = this.filterChain;
			this.filterChain = filterChain;

			// verify
			int filterCount = filterChain.getFilterCount();
			AbstractAtomFilter filter;
			for (int i=0; i<filterCount; i++) {
				filter = filterChain.getFilterAt(i);
				if (filter.isEnabled()) {
					try {
						filter.initialize();
					} catch (SignalMLException ex) {

						filter.setEnabled(false);

						String filterName = filter.getName();
						logger.warn("Filter [" + filterName + "] failed to initialize", ex);

						MessageSourceAccessor messageSource = ErrorsDialog.getStaticMessageSource();
						String message = "Filter initialization failed";
						if (messageSource != null) {
							String exMessage = messageSource.getMessage(new ResolvableException(ex));
							if (exMessage.length() > 50) {
								exMessage = exMessage.substring(0, 50) + "...";
							}
							if (filterName.length() > 30) {
								filterName = filterName.substring(0, 30) + "...";
							}
							message = messageSource.getMessage("error.filterFailedToInitialize", new Object[] { filterName, exMessage });
						}

						OptionPane.showRawError(null, message);

					}
				}
			}

			pcSupport.firePropertyChange(FILTER_CHAIN_PROPERTY, oldChain, filterChain);
		}
	}

	/**
	 * Returns the {@link StandardBook#getBookComment() comment} of the
	 * {@link StandardBook book} stored in this document.
	 * @return the comment of the book
	 */
	public String getBookComment() {
		String comment = book.getBookComment();
		if (comment.length() > 100) {
			return comment.substring(0, 100) + "...";
		}
		return comment;
	}

	/**
	 * Returns the value of calibration.
	 * @return the value of calibration
	 */
	public float getCalibration() {
		return book.getCalibration();
	}

	/**
	 * Returns the number of channels described by the {@link StandardBook
	 * book}.
	 * @return the number of channels in the signal described
	 * by the book
	 */
	public int getChannelCount() {
		return book.getChannelCount();
	}

	/**
	 * Returns the date when the book was created.
	 * @return the date when the book was created
	 */
	public String getDate() {
		return book.getDate();
	}

	/**
	 * Returns the dictionary size.
	 * @return the dictionary size
	 */
	public int getDictionarySize() {
		return book.getDictionarySize();
	}

	/**
	 * Returns the type of the dictionary.
	 * @return the type of the dictionary
	 */
	public char getDictionaryType() {
		return book.getDictionaryType();
	}

	/**
	 * Returns decomposition energy percent.
	 * @return decomposition energy percent
	 */
	public float getEnergyPercent() {
		return book.getEnergyPercent();
	}

	/**
	 * Returns the maximum number of {@link StandardBookAtom atoms} in the
	 * {@link StandardBookSegment segment}.
	 * @return the maximum number of atoms in the segment
	 */
	public int getMaxIterationCount() {
		return book.getMaxIterationCount();
	}

	/**
	 * Returns the number of samples per second of the signal.
	 * @return the sampling frequency
	 */
	public float getSamplingFrequency() {
		return book.getSamplingFrequency();
	}

	/**
	 * Returns the number of {@link StandardBookSegment segments} in the
	 * {@link StandardBook book}.
	 * @return the number of segments in the book
	 */
	public int getSegmentCount() {
		return book.getSegmentCount();
	}

	/**
	 * Returns the number of channels of the signal based
	 * on which the {@link StandardBook book} was created.
	 * @return the number of channels of the signal based
	 * on which the book was created.
	 */
	public int getSignalChannelCount() {
		return book.getSignalChannelCount();
	}

	/**
	 * Returns the {@link StandardBook#getTextInfo() text info} of the
	 * {@link StandardBook book}.
	 * @return the text info of the book
	 */
	public String getTextInfo() {
		String textInfo = book.getTextInfo();
		if (textInfo.length() > 100) {
			return textInfo.substring(0, 100) + "...";
		}
		return textInfo;
	}

	/**
	 * Returns the {@link StandardBook#getVersion() version} of the
	 * {@link StandardBook book}.
	 * @return the version of the book
	 */
	public String getVersion() {
		return book.getVersion();
	}

	/**
	 * Returns the {@link StandardBook#getWebSiteInfo() website info} of the
	 * {@link StandardBook book}.
	 * @return the website info of the book
	 */
	public String getWebSiteInfo() {
		String webSiteInfo = book.getWebSiteInfo();
		if (webSiteInfo.length() > 100) {
			return webSiteInfo.substring(0, 100) + "...";
		}
		return webSiteInfo;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor("property.bookDocument.version", "version", BookDocument.class, "getVersion", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.channelCount", "channelCount", BookDocument.class, "getChannelCount", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.segmentCount", "segmentCount", BookDocument.class, "getSegmentCount", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.samplingFrequency", "samplingFrequency", BookDocument.class, "getSamplingFrequency", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.calibration", "calibration", BookDocument.class, "getCalibration", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.bookComment", "bookComment", BookDocument.class, "getBookComment", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.date", "date", BookDocument.class, "getDate", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.energyPercent", "energyPercent", BookDocument.class, "getEnergyPercent", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.maxIterationCount", "maxIterationCount", BookDocument.class, "getMaxIterationCount", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.dictionarySize", "dictionarySize", BookDocument.class, "getDictionarySize", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.dictionaryType", "dictionaryType", BookDocument.class, "getDictionaryType", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.signalChannelCount", "signalChannelCount", BookDocument.class, "getSignalChannelCount", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.textInfo", "textInfo", BookDocument.class, "getTextInfo", null));
		list.add(new LabelledPropertyDescriptor("property.bookDocument.webSiteInfo", "webSiteInfo", BookDocument.class, "getWebSiteInfo", null));

		return list;

	}

}
