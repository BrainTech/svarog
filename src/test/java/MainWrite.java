import java.io.IOException;

import org.signalml.domain.book.BookBuilder;
import org.signalml.domain.book.BookBuilderImpl;
import org.signalml.domain.book.IncrementalBookWriter;
import org.signalml.domain.book.StandardBookAtomWriter;
import org.signalml.domain.book.StandardBookAtomWriterImpl;
import org.signalml.domain.book.StandardBookSegmentWriter;
import org.signalml.domain.book.StandardBookWriter;
import org.signalml.domain.book.impl.StandardBookSegmentWriterImpl;

public class MainWrite {
	public static void main(String args[]) {
		BookBuilder builder = new BookBuilderImpl();
		StandardBookWriter book = builder.createBook();

		book.setCalibration(1.0F);
		book.setEnergyPercent(0.1F);
		book.setSamplingFrequency(1.0F);
		book.setDate("2008/03/11");

		IncrementalBookWriter incBook = null;
		try {
			incBook = builder.writeBookIncremental(book, "/home/oskar/SignalML/Test.b");
			StandardBookSegmentWriter seg = new StandardBookSegmentWriterImpl(book);

			float sample[] = new float[513];
			for (int i = 0; i < sample.length; i++) {
				sample[i] = i;
			}

			seg.setChannelNumber(1);
			seg.setSegmentNumber(1);
			seg.setSignalSamples(sample);

			for (int k = 1; k < 10; k++) {
				StandardBookAtomWriter atom = new StandardBookAtomWriterImpl();

				atom.setType(StandardBookAtomWriterImpl.GABORWAVE_IDENTITY);
				atom.setModulus(1.0F);
				atom.setAmplitude(2.0F * k);
				atom.setPosition(10.0F);
				atom.setScale(256.0F);
				atom.setPhase(0.0F);

				seg.addAtom(atom);
			}

			incBook.writeSegment(seg);

			System.out.println("Done.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (incBook != null) {
				try {
					incBook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
