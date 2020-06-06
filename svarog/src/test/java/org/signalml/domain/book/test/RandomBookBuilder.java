/* DefaultBookBuilder.java created 2008-02-24
 *
 */

package org.signalml.domain.book.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import org.signalml.domain.book.BookBuilder;
import org.signalml.domain.book.BookFormatException;
import org.signalml.domain.book.DefaultBookAtom;
import org.signalml.domain.book.DefaultMutableBook;
import org.signalml.domain.book.IncrementalBookWriter;
import org.signalml.domain.book.MPv5BookWriter;
import org.signalml.domain.book.MutableBook;
import org.signalml.domain.book.MutableBookSegment;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookWriter;

/** DefaultBookBuilder
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RandomBookBuilder implements BookBuilder {

	private static RandomBookBuilder sharedInstance = null;

	protected RandomBookBuilder() {
	}

	public static RandomBookBuilder getInstance() {
		if (sharedInstance == null) {
			synchronized (RandomBookBuilder.class) {
				if (sharedInstance == null)
					sharedInstance = new RandomBookBuilder();
			}
		}

		return sharedInstance;
	}

	public MutableBook createBook(int channelCount, float samplingFrequency) {
		return new DefaultMutableBook(channelCount, samplingFrequency);
	}

	@Override
	public StandardBook readBook(File file) throws IOException, BookFormatException {

		int channelCount = 21;
		float samplingFrequency = 128;
		int segmentLength = 20 * 128;
		int i;
		int e;

		Random random = new Random();

		DefaultMutableBook book = new DefaultMutableBook(channelCount, samplingFrequency);

		MutableBookSegment[] segment1 = book.addNewSegment(0, segmentLength);
		for (i=0; i<channelCount; i++) {

			float energy = (float)(1000 * Math.abs(random.nextDouble()));
			segment1[i].setSignalEnergy(energy);
			segment1[i].setDecompositionEnergy(energy - ((float)(100 * Math.abs(random.nextDouble()))));
			float[] samples = new float[segmentLength];
			for (e=0; e<segmentLength; e++) {
				samples[e] = (float)(100 * (random.nextDouble()-0.5));
			}
			segment1[i].setSignalSamples(samples);

			for (e=0; e<20; e++) {
				segment1[i].addAtom(createRandomAtom(random, e, samplingFrequency, segmentLength));
			}

			/*
			segment1[i].addAtom(segment1[i].createAtom(StandardBookAtom.GABORWAVE_IDENTITY, 0, 100, 100, 0, 100, 0, 0) );
			segment1[i].addAtom(segment1[i].createAtom(StandardBookAtom.GABORWAVE_IDENTITY, 0, 100, 100, 2560-1, 100, 0, 0) );
			segment1[i].addAtom(segment1[i].createAtom(StandardBookAtom.GABORWAVE_IDENTITY, 0, 100, 100, 2560-1, 100, 2560/2-1, 0) );
			segment1[i].addAtom(segment1[i].createAtom(StandardBookAtom.GABORWAVE_IDENTITY, 0, 100, 100, 0, 100, 2560/2-1, 0) );
			segment1[i].addAtom(segment1[i].createAtom(StandardBookAtom.GABORWAVE_IDENTITY, 0, 100, 100, 2560/2, 100, 2560/4, 0) );
			 */
		}

		MutableBookSegment[] segment2 = book.addNewSegment(20, segmentLength);
		for (i=0; i<channelCount; i++) {

			float energy = (float)(2000 * Math.abs(random.nextDouble()));
			segment2[i].setSignalEnergy(energy);
			segment2[i].setDecompositionEnergy(energy - ((float)(100 * Math.abs(random.nextDouble()))));
			float[] samples = new float[segmentLength];
			for (e=0; e<segmentLength; e++) {
				samples[e] = (float)(100 * (random.nextDouble()-0.5));
			}
			segment2[i].setSignalSamples(samples);

			for (e=0; e<30; e++) {
				segment2[i].addAtom(createRandomAtom(random, e, samplingFrequency, segmentLength));
			}

		}

		MutableBookSegment[] segment3 = book.addNewSegment(40, segmentLength);
		for (i=0; i<channelCount; i++) {

			float energy = (float)(3000 * Math.abs(random.nextDouble()));
			segment3[i].setSignalEnergy(energy);
			segment3[i].setDecompositionEnergy(energy - ((float)(100 * Math.abs(random.nextDouble()))));
			float[] samples = new float[segmentLength];
			for (e=0; e<segmentLength; e++) {
				samples[e] = (float)(100 * (random.nextDouble()-0.5));
			}
			segment3[i].setSignalSamples(samples);

			for (e=0; e<20; e++) {
				segment3[i].addAtom(createRandomAtom(random, e, samplingFrequency, segmentLength));
			}

		}

		book.setBookComment("Book comment");
		book.setCalibration(1F);
		book.setDate((new Date()).toString());
		book.setDictionarySize(10000);
		book.setEnergyPercent(95);
		book.setMaxIterationCount(50);
		book.setDictionaryType('x');
		book.setSamplingFrequency(samplingFrequency);
		book.setSignalChannelCount(21);
		book.setTextInfo("Text info");
		book.setWebSiteInfo("Web site info");

		return book;
	}

	@Override
	public void writeBookComplete(StandardBook book, File file) throws IOException {
		IncrementalBookWriter bookWriter = writeBookIncremental(book, file);
		int segmentCount = book.getSegmentCount();
		for (int i=0; i<segmentCount; i++) {
			bookWriter.writeSegment(book.getSegmentAt(i));
		}
		bookWriter.close();
	}

	@Override
	public IncrementalBookWriter writeBookIncremental(StandardBook book, File file) throws IOException {
		return new MPv5BookWriter(book, file);
	}

	private StandardBookAtom createRandomAtom(Random random, int iteration, float samplingFrequency, int segmentLength) {

		DefaultBookAtom atom = new DefaultBookAtom(
			samplingFrequency,
			segmentLength,
			StandardBookAtom.GABORWAVE_IDENTITY,
			iteration,
			(float)(100 * Math.abs(random.nextDouble())),
			//(int) ( (segmentLength/2) * Math.abs( random.nextDouble() ) ),
			2560/2-1,
			//(int) (segmentLength * Math.abs( random.nextDouble() ) ),
			2560-1,
			(int)(segmentLength * Math.abs(random.nextDouble())),
			(float)(100 * Math.abs(random.nextDouble())),
			(float)(3.14 * random.nextDouble())
		);

		return atom;

	}

	@Override
	public StandardBookWriter createBook() {
		throw new UnsupportedOperationException("Not implemented: RandomBookBuilder.createBook");
	}

	@Override
	public IncrementalBookWriter writeBookIncremental(StandardBookWriter book, String file) throws IOException {
		throw new UnsupportedOperationException("Not implemented: RandomBookBuilder.writeBookIncremental");
	}

}
