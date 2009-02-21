/* MP5BookWriter.java created 2008-02-24
 * 
 */

package org.signalml.domain.book;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import pl.edu.fuw.MP.Core.BookLibraryV5Writer;
import pl.edu.fuw.MP.Core.SegmentHeaderV5;

/** MP5BookWriter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 			based on code by Dobieslaw Ircha
 */
public class MPv5BookWriter implements IncrementalBookWriter {

	private DataOutputStream out=null;

	// FIXME seems like a book written with this writer has segment index starting from 0, not 1
	// resulting in inability to read a written book

	protected static final Logger logger = Logger.getLogger(MPv5BookWriter.class);

	private BookLibraryV5Writer bookWriter = null;

	private int segmentCount = 0;

	public MPv5BookWriter( StandardBook book, File file ) throws IOException {

		bookWriter = new BookLibraryV5Writer();

		bookWriter.setBookComment( book.getBookComment() );
		bookWriter.setCalibration( book.getCalibration() );
		bookWriter.setDate( book.getDate() );
		bookWriter.setDictionarySize( book.getDictionarySize() );
		bookWriter.setDictionaryType( book.getDictionaryType() );
		bookWriter.setEnergyPercent( book.getEnergyPercent() );
		bookWriter.setMaxIterationCount( book.getMaxIterationCount() );
		bookWriter.setSamplingFrequency( book.getSamplingFrequency() );
		bookWriter.setTextInfo( book.getTextInfo() );
		bookWriter.setWebSiteInfo( book.getWebSiteInfo() );

		bookWriter.Open(file.getAbsolutePath());

	}

	@Override
	public void close() throws IOException {
		if( bookWriter != null ) {
			bookWriter.close();
			bookWriter = null;
		}
	}

	@Override
	public void writeSegment(StandardBookSegment[] segments) throws IOException {

		StandardBookSegmentWriter segmentWriter = new StandardBookSegmentWriterImpl(bookWriter);
		int atomCount;
		StandardBookAtomWriter atomWriter;
		StandardBookAtom atom;

		for( int i=0; i<segments.length; i++ ) {

			segmentWriter.setChannelNumber(i+1);
			segmentWriter.setSegmentNumber(segmentCount+1);
			if( segments[i].hasSignal() ) {
				segmentWriter.setSignalSamples( segments[i].getSignalSamples() );
			} else {
				segmentWriter.setSignalSamples(null);
			}

			// FIXME those below missing in writer, loss of important information (oka: not true after Dobi)
			segments[i].getDecompositionEnergy();
			segments[i].getSignalEnergy();
			segments[i].getSegmentTime();
			segments[i].getSegmentLength();

			atomCount = segments[i].getAtomCount();

			for(int k=1 ; k<atomCount ; k++) {

				atomWriter = new StandardBookAtomWriterImpl();
				atom = segments[i].getAtomAt(k);

				// FIXME no iteration in writer
				atomWriter.setType(atom.getType());
				atomWriter.setModulus(atom.getModulus());
				atomWriter.setAmplitude(atom.getAmplitude());
				atomWriter.setPosition(atom.getPosition());
				atomWriter.setScale(atom.getScale());
				atomWriter.setFrequency(atom.getFrequency());
				atomWriter.setPhase(atom.getPhase());

				segmentWriter.addAtom(atomWriter);

			}
		}


		bookWriter.writeSegment(segmentWriter);

	}

	@Override
	public void writeSegment(StandardBookSegmentWriter segment) throws IOException {
		if (segment != null) {
			((SegmentHeaderV5) segment).Write(out);
			out.flush();
		}
	}


}
