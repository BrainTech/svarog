/* ExampleMethodIterationConsumer.java created 2007-12-06
*/

package org.signalml.app.method.example;

import java.util.Random;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.view.signal.roc.RocDialog;
import org.signalml.domain.roc.RocData;
import org.signalml.domain.roc.RocDataPoint;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.MethodIteratorResult;

/** ExampleMethodIterationConsumer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExampleMethodIterationConsumer implements MethodIterationResultConsumer {
	private RocDialog rocDialog;

	private int[][] getSyntheticRocData() {

		int[][] points = new int[30][];

		Random random = new Random();

		double[] A = new double[1000];
		double[] B = new double[1000];

		int i;

		for (i=0; i<A.length; i++) {
			A[i] = random.nextGaussian();
			A[i] = 2*A[i] + 10;
		}
		for (i=0; i<B.length; i++) {
			B[i] = random.nextGaussian();
			B[i] = 3*B[i] + 15;
		}

		int truePositiveCount;
		int trueNegativeCount;
		int falsePositiveCount;
		int falseNegativeCount;

		for (int k=0; k<points.length; k++) {

			truePositiveCount = 0;
			trueNegativeCount = 0;
			falsePositiveCount = 0;
			falseNegativeCount = 0;

			for (i=0; i<A.length; i++) {
				if (A[i] > k) {
					falsePositiveCount++;
				} else {
					trueNegativeCount++;
				}
			}
			for (i=0; i<B.length; i++) {
				if (B[i] > k) {
					truePositiveCount++;
				} else {
					falseNegativeCount++;
				}
			}

			points[k] = new int[] { truePositiveCount, trueNegativeCount, falsePositiveCount, falseNegativeCount };

		}

		return points;

	}

	@Override
	public void consumeIterationResult(IterableMethod method, MethodIteratorData data, MethodIteratorResult result) {

		RocData rocData = RocData.createForMethodIteratorData(data);

		// simulate roc result
		int size = result.size();

		int[][] syntheticRocData = getSyntheticRocData();

		double step = ((double) syntheticRocData.length-1) / (size-1);

		for (int i=0; i<size; i++) {

			int sample = (int) Math.round(step * (size-(1+i)));

			RocDataPoint point = new RocDataPoint(result.getParameterValuesAt(i), syntheticRocData[sample][0], syntheticRocData[sample][1], syntheticRocData[sample][2], syntheticRocData[sample][3]);
			rocData.add(point);

		}

		rocDialog.showDialog(rocData, true);

	}

	public RocDialog getRocDialog() {
		return rocDialog;
	}

	public void setRocDialog(RocDialog rocDialog) {
		this.rocDialog = rocDialog;
	}

}
