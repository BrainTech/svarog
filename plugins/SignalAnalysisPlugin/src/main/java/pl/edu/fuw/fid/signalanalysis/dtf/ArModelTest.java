package pl.edu.fuw.fid.signalanalysis.dtf;

import java.io.RandomAccessFile;
import pl.edu.fuw.fid.signalanalysis.MultiSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ArModelTest {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ArModelTest.class);

	/**
	 * Debugging procedure, can be deleted.
	 */
	public static void main(String[] args) {
		try {

			RandomAccessFile f = new RandomAccessFile("/home/piotr/ar.raw", "r");
			final int length = (int) (f.getChannel().size() / Float.BYTES);
			final double[] data = new double[length];
			for (int i=0; i<length; ++i) {
				int t = Integer.reverseBytes(f.readInt());
				data[i] = Float.intBitsToFloat(t);
			}
			MultiSignal multi = new MultiSignal() {
				@Override
				public int getChannelCount() {
					return 1;
				}
				@Override
				public int getSampleCount() {
					return length;
				}
				@Override
				public void getSamples(int channel, int start, int length, double[] buffer) {
					System.arraycopy(data, start, buffer, 0, length);
				}
				@Override
				public double getSamplingFrequency() {
					return 100;
				}
			};

			for (int p=1; p<=5; ++p) {
				ArModel model = ArModel.compute(multi, p);
				System.out.println(model.exportCoefficients()+" "+model.getErrorDeterminant());
			}

		} catch (Exception ex) {
			logger.error(ex);
		}
	}

}
