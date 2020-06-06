package org.signalml.plugin.bookreporter.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import org.signalml.app.document.BookDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;
import org.signalml.plugin.bookreporter.exception.BookReporterBookReaderException;
import org.signalml.plugin.export.SignalMLException;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterBookReader {

	private BookDocument bookDocument = null;
	private final int segmentCount;
	private final int channel;  // starting from 1
	private final double segmentTimeLength;
	private int nextSegment = 0;

	public BookReporterBookReader(String bookFilePath, int channel) throws BookReporterBookReaderException {
		try {
			this.bookDocument = new BookDocument(new File(bookFilePath));
			this.bookDocument.openDocument();
			this.segmentCount = this.bookDocument.getSegmentCount();
			this.channel = channel;
			this.segmentTimeLength = (this.segmentCount > 0)
				? this.bookDocument.getBook().getSegmentAt(0,0).getSegmentTimeLength()
				: 0.0;
		} catch (SignalMLException ex) {
			throw new BookReporterBookReaderException(ex.getMessage());
			
		} catch (IOException ex) {
			throw new BookReporterBookReaderException(ex.getMessage());
		}
	}

	public void close() throws BookReporterBookReaderException {
		if (this.bookDocument == null) {
			throw new RuntimeException(_("book is already closed"));
		} else try {
			this.bookDocument.closeDocument();
			this.bookDocument = null;
		} catch (SignalMLException ex) {
			// let's ignore this
		}
	}
	
	public Collection<BookReporterAtom> getAtomsFromNextSegment() {
		if (this.bookDocument == null) {
			throw new RuntimeException(_("book is already closed"));
		} else if (this.nextSegment >= this.segmentCount) {
			return null;
		} else {
			ArrayList<BookReporterAtom> atoms = new ArrayList<>();
			for (StandardBookSegment segment : this.bookDocument.getBook().getSegmentAt(this.nextSegment))
			if (segment.getChannelNumber() == channel) {
				double timeOffset = this.nextSegment * this.segmentTimeLength;
				int atomCount = segment.getAtomCount();
				for (int atomIndex=0; atomIndex<atomCount; ++atomIndex) {
					BookReporterAtom newAtom = BookReporterAtom.createFromStandardBookAtom(
						segment.getAtomAt(atomIndex),
						this.bookDocument.getCalibration(),
						timeOffset,
						channel
					);
					atoms.add(newAtom);
				}
			}
			Collections.sort(atoms, new Comparator<BookReporterAtom>() {
				@Override
				public int compare(BookReporterAtom o1, BookReporterAtom o2) {
					return Double.compare(o1.position, o2.position);
				}
			});
			this.nextSegment++;
			return atoms;
		}
	}
	
	public double getTimeLength() {
		return this.segmentCount * this.segmentTimeLength;
	}
	
	public int getAllSegmentsCount() {
		return this.segmentCount;
	}
	
	public int getProcessedSegmentsCount() {
		return this.nextSegment;
	}
}
