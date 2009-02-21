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
import org.signalml.domain.book.filter.AbstractAtomFilter;
import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.exception.ResolvableException;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;


/** BookDocument
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookDocument extends AbstractFileDocument {
	
	protected static final Logger logger = Logger.getLogger(BookDocument.class);
	
	public static final String FILTER_CHAIN_PROPERTY = "filterChain";
	
	private StandardBook book;
	private AtomFilterChain filterChain;
	
	protected BookDocument() throws SignalMLException {
		super();
		filterChain = new AtomFilterChain();
	}
	
	public BookDocument(StandardBook book) throws SignalMLException {
		this();
		this.book = book;
	}

	public BookDocument(File file) throws SignalMLException, IOException {
		super(file);
		filterChain = new AtomFilterChain();
	}
	
	public StandardBook getBook() {
		return book;
	}

	@Override
	public String getName() {
		return ( backingFile != null ? backingFile.getName() : "" );
	}
	
	@Override
	public void openDocument() throws SignalMLException, IOException {

		if( backingFile == null ) {
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
	
	public AtomFilterChain getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(AtomFilterChain filterChain) {
		if( this.filterChain != filterChain ) {
			AtomFilterChain oldChain = this.filterChain;
			this.filterChain = filterChain;
			
			// verify
			int filterCount = filterChain.getFilterCount();
			AbstractAtomFilter filter;
			for( int i=0; i<filterCount; i++ ) {
				filter = filterChain.getFilterAt(i);
				if( filter.isEnabled() ) {
					try {
						filter.initialize();
					} catch( SignalMLException ex ) {
				
						filter.setEnabled(false);
						
						String filterName = filter.getName();
						logger.warn( "Filter [" + filterName + "] failed to initialize", ex );
						
						MessageSourceAccessor messageSource = ErrorsDialog.getStaticMessageSource();
						String message = "Filter initialization failed";
						if( messageSource != null ) {
							String exMessage = messageSource.getMessage( new ResolvableException( ex ) );
							if( exMessage.length() > 50 ) {
								exMessage = exMessage.substring(0, 50) + "...";
							}
							if( filterName.length() > 30 ) {
								filterName = filterName.substring(0, 30) + "...";
							}
							message = messageSource.getMessage( "error.filterFailedToInitialize", new Object[] { filterName, exMessage } );
						}
						
						OptionPane.showRawError(null, message);
						
					}
				}
			}
			
			pcSupport.firePropertyChange(FILTER_CHAIN_PROPERTY, oldChain, filterChain);
		}
	}
	
	public String getBookComment() {
		String comment = book.getBookComment();
		if( comment.length() > 100 ) {
			return comment.substring(0, 100) + "...";
		}
		return comment;
	}

	public float getCalibration() {
		return book.getCalibration();
	}

	public int getChannelCount() {
		return book.getChannelCount();
	}

	public String getDate() {
		return book.getDate();
	}

	public int getDictionarySize() {
		return book.getDictionarySize();
	}

	public char getDictionaryType() {
		return book.getDictionaryType();
	}

	public float getEnergyPercent() {
		return book.getEnergyPercent();
	}

	public int getMaxIterationCount() {
		return book.getMaxIterationCount();
	}

	public float getSamplingFrequency() {
		return book.getSamplingFrequency();
	}

	public int getSegmentCount() {
		return book.getSegmentCount();
	}

	public int getSignalChannelCount() {
		return book.getSignalChannelCount();
	}

	public String getTextInfo() {
		String textInfo = book.getTextInfo();
		if( textInfo.length() > 100 ) {
			return textInfo.substring(0, 100) + "...";
		}
		return textInfo;
	}

	public String getVersion() {
		return book.getVersion();
	}

	public String getWebSiteInfo() {
		String webSiteInfo = book.getWebSiteInfo();
		if( webSiteInfo.length() > 100 ) {
			return webSiteInfo.substring(0, 100) + "...";
		}
		return webSiteInfo;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();
		
		list.add( new LabelledPropertyDescriptor("property.bookDocument.version", "version", BookDocument.class, "getVersion", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.channelCount", "channelCount", BookDocument.class, "getChannelCount", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.segmentCount", "segmentCount", BookDocument.class, "getSegmentCount", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.samplingFrequency", "samplingFrequency", BookDocument.class, "getSamplingFrequency", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.calibration", "calibration", BookDocument.class, "getCalibration", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.bookComment", "bookComment", BookDocument.class, "getBookComment", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.date", "date", BookDocument.class, "getDate", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.energyPercent", "energyPercent", BookDocument.class, "getEnergyPercent", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.maxIterationCount", "maxIterationCount", BookDocument.class, "getMaxIterationCount", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.dictionarySize", "dictionarySize", BookDocument.class, "getDictionarySize", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.dictionaryType", "dictionaryType", BookDocument.class, "getDictionaryType", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.signalChannelCount", "signalChannelCount", BookDocument.class, "getSignalChannelCount", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.textInfo", "textInfo", BookDocument.class, "getTextInfo", null) );
		list.add( new LabelledPropertyDescriptor("property.bookDocument.webSiteInfo", "webSiteInfo", BookDocument.class, "getWebSiteInfo", null) );
				
		return list;
		
	}
		
}
