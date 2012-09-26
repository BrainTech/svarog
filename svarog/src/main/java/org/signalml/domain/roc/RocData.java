/* RocData.java created 2007-12-06
 *
 */

package org.signalml.domain.roc;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.signalml.app.model.components.WriterExportableTable;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.ParameterIterationSettings;

/** RocData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RocData implements WriterExportableTable, PropertyProvider {

	private IterableParameter[] parameters;

	private ArrayList<RocDataPoint> rocDataPoints;

	// statistics
	private boolean dirtyStatistics = true;

	private double areaUnderCurve;
	private int maxAccuracyIteration;
	private double maxAccuracy;

	private int pointBelowOSSIteration;
	private double pointBelowOSSDistance;

	private int pointAboveOSSIteration;
	private double pointAboveOSSDistance;

	private double ossIntersectionTP;

	public RocData(IterableParameter[] parameters) {

		if (parameters == null) {
			throw new NullPointerException("No parameters");
		}
		if (parameters.length == 0) {
			throw new IllegalArgumentException("More than one parameter needed");
		}

		this.parameters = parameters;
		this.rocDataPoints = new ArrayList<RocDataPoint>();

	}

	public RocData(IterableParameter[] parameters, RocDataPoint[] rocDataPoints) {

		this(parameters);
		this.rocDataPoints = new ArrayList<RocDataPoint>(this.rocDataPoints);

	}

	public static RocData createForParameterIterationSettings(ParameterIterationSettings[] parameters) {

		LinkedList<IterableParameter> iteratedParameters = new LinkedList<IterableParameter>();
		for (ParameterIterationSettings parameter : parameters) {
			if (parameter.isIterated()) {
				iteratedParameters.add(parameter.getParameter());
			}
		}
		IterableParameter[] iteratedArr = new IterableParameter[iteratedParameters.size()];
		iteratedParameters.toArray(iteratedArr);

		return new RocData(iteratedArr);

	}

	public static RocData createForMethodIteratorData(MethodIteratorData data) {

		return createForParameterIterationSettings(data.getParameters());

	}

	public void add(RocDataPoint rocDataPoint) {
		rocDataPoints.add(rocDataPoint);
		dirtyStatistics = true;
	}

	public int getParameterCount() {
		return parameters.length;
	}

	public IterableParameter getParameterAt(int index) {
		return parameters[index];
	}

	public int getSampleCount() {
		return rocDataPoints.size();
	}

	public RocDataPoint getRocDataPointAt(int sample) {
		return rocDataPoints.get(sample);
	}

	public Object getParameterValueAt(int index, int sample) {
		return rocDataPoints.get(sample).getParameterValues()[index];
	}

	public int getTruePositiveCount(int sample) {
		return rocDataPoints.get(sample).getTruePositiveCount();
	}

	public int getTrueNegativeCount(int sample) {
		return rocDataPoints.get(sample).getTrueNegativeCount();
	}

	public int getFalsePositiveCount(int sample) {
		return rocDataPoints.get(sample).getFalsePositiveCount();
	}

	public int getFalseNegativeCount(int sample) {
		return rocDataPoints.get(sample).getFalseNegativeCount();
	}

	public double getTrueRateAt(int sample) {
		return rocDataPoints.get(sample).getTrueRate();
	}

	public double getFalseRateAt(int sample) {
		return rocDataPoints.get(sample).getFalseRate();
	}

	public double[] getTrueRates() {

		int cnt = rocDataPoints.size();

		double[] trueRates = new double[cnt];

		for (int i=0; i<cnt; i++) {
			trueRates[i] = rocDataPoints.get(i).getTrueRate();
		}

		return trueRates;
	}

	public double[] getFalseRates() {

		int cnt = rocDataPoints.size();

		double[] falseRates = new double[cnt];

		for (int i=0; i<cnt; i++) {
			falseRates[i] = rocDataPoints.get(i).getFalseRate();
		}

		return falseRates;
	}

	private boolean isAboveOSS(RocDataPoint point) {

		return (point.getTrueRate() >= (1 - point.getFalseRate()));

	}

	private double getOSSDistance(RocDataPoint point) {

		return (Math.abs(1 - (point.getFalseRate() + point.getTrueRate())) / Math.sqrt(2));

	}

	private void calculateStatistics() {

		int cnt = rocDataPoints.size();

		if (cnt == 0) {
			areaUnderCurve = 0;
			maxAccuracy = 0;
			maxAccuracyIteration = -1;
			pointAboveOSSDistance = -1;
			pointAboveOSSIteration = -1;
			pointBelowOSSDistance = -1;
			pointBelowOSSIteration = -1;
		} else {

			areaUnderCurve = 0;

			RocDataPoint thisPoint;
			RocDataPoint prevPoint;
			double accuracy;
			double distance;

			thisPoint = rocDataPoints.get(0);

			areaUnderCurve += thisPoint.getFalseRate()*thisPoint.getTrueRate()/2;
			maxAccuracy = thisPoint.getAccuracy();
			maxAccuracyIteration = 0;

			pointAboveOSSIteration = -1;
			pointBelowOSSIteration = -1;

			pointAboveOSSDistance = Double.MAX_VALUE;
			pointBelowOSSDistance = Double.MAX_VALUE;

			if (isAboveOSS(thisPoint)) {
				pointAboveOSSDistance = getOSSDistance(thisPoint);
				pointAboveOSSIteration = 0;
			} else {
				pointBelowOSSDistance = getOSSDistance(thisPoint);
				pointBelowOSSIteration = 0;
			}

			for (int i=1; i<cnt; i++) {
				prevPoint = thisPoint;
				thisPoint = rocDataPoints.get(i);
				areaUnderCurve += (0.5 * (prevPoint.getTrueRate() + thisPoint.getTrueRate()) * (thisPoint.getFalseRate() - prevPoint.getFalseRate()));

				accuracy = thisPoint.getAccuracy();
				if (accuracy > maxAccuracy) {
					maxAccuracy = accuracy;
					maxAccuracyIteration = i;
				}

				distance = getOSSDistance(thisPoint);
				if (isAboveOSS(thisPoint)) {
					if (distance < pointAboveOSSDistance) {
						pointAboveOSSDistance = distance;
						pointAboveOSSIteration = i;
					}
				} else {
					if (distance < pointBelowOSSDistance) {
						pointBelowOSSDistance = distance;
						pointBelowOSSIteration = i;
					}
				}
			}

			areaUnderCurve += (0.5 * (thisPoint.getTrueRate() + 1) * (1 - thisPoint.getFalseRate()));

			if (pointAboveOSSIteration >= 0 && pointBelowOSSIteration >= 0) {
				thisPoint = rocDataPoints.get(pointAboveOSSIteration);
				double xa = thisPoint.getFalseRate();
				double ya = thisPoint.getTrueRate();
				thisPoint = rocDataPoints.get(pointBelowOSSIteration);
				double xb = thisPoint.getFalseRate();
				double yb = thisPoint.getTrueRate();

				ossIntersectionTP = (yb*(1-xa) - ya*(1-xb)) / ((xb+yb) - (xa+ya));
			} else {
				ossIntersectionTP = -1;
			}

			if (pointAboveOSSIteration < 0) {
				pointAboveOSSDistance = -1;
			} else {
				pointAboveOSSIteration++;
			}

			if (pointBelowOSSIteration < 0) {
				pointBelowOSSDistance = -1;
			} else {
				pointBelowOSSIteration++;
			}

			if (maxAccuracyIteration >= 0) {
				maxAccuracyIteration++;
			}

		}

		dirtyStatistics = false;

	}

	public double getAreaUnderCurve() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return areaUnderCurve;
	}

	public double getMaxAccuracy() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return maxAccuracy;
	}

	public int getMaxAccuracyIteration() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return maxAccuracyIteration;
	}

	public int getPointBelowOSSIteration() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return pointBelowOSSIteration;
	}

	public double getPointBelowOSSDistance() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return pointBelowOSSDistance;
	}

	public int getPointAboveOSSIteration() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return pointAboveOSSIteration;
	}

	public double getPointAboveOSSDistance() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return pointAboveOSSDistance;
	}

	public double getOssIntersectionTP() {
		if (dirtyStatistics) {
			calculateStatistics();
		}
		return ossIntersectionTP;
	}

	@Override
	public void export(Writer writer, String columnSeparator, String rowSeparator, Object userObject) throws IOException {

		int i;
		int e;

		for (i=0; i<parameters.length; i++) {
			writer.append(parameters[i].getName());
			writer.append(columnSeparator);
		}
		writer.append("TP");
		writer.append(columnSeparator);
		writer.append("FP");
		writer.append(columnSeparator);
		writer.append("TN");
		writer.append(columnSeparator);
		writer.append("FN");
		writer.append(columnSeparator);
		writer.append("FP rate");
		writer.append(columnSeparator);
		writer.append("TP rate");
		writer.append(columnSeparator);
		writer.append("sensitivity");
		writer.append(columnSeparator);
		writer.append("specifity");
		writer.append(columnSeparator);
		writer.append("accuracy");
		writer.append(columnSeparator);
		writer.append("positive_pred_value");
		writer.append(columnSeparator);
		writer.append("negative_pred_value");
		writer.append(columnSeparator);
		writer.append("false_discovery_rate");
		writer.append(rowSeparator);

		int sampleCount = rocDataPoints.size();
		RocDataPoint thisPoint;

		for (e=0; e<sampleCount ; e++) {

			thisPoint = rocDataPoints.get(e);

			for (i=0; i<parameters.length; i++) {
				writer.append(thisPoint.getParameterValues()[i].toString());
				writer.append(columnSeparator);
			}
			writer.append(Integer.toString(thisPoint.getTruePositiveCount()));
			writer.append(columnSeparator);
			writer.append(Integer.toString(thisPoint.getFalsePositiveCount()));
			writer.append(columnSeparator);
			writer.append(Integer.toString(thisPoint.getTrueNegativeCount()));
			writer.append(columnSeparator);
			writer.append(Integer.toString(thisPoint.getFalseNegativeCount()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getFalseRate()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getTrueRate()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getSensitivity()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getSpecifity()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getAccuracy()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getPositivePredictiveValue()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getNegativePredictiveValue()));
			writer.append(columnSeparator);
			writer.append(Double.toString(thisPoint.getFalseDiscoveryRate()));
			writer.append(rowSeparator);

		}

	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(_("area under curve"), "areaUnderCurve", RocData.class, "getAreaUnderCurve", null));
		list.add(new LabelledPropertyDescriptor(_("maximal accuracy iteration"), "maxAccuracyIteration", RocData.class, "getMaxAccuracyIteration", null));
		list.add(new LabelledPropertyDescriptor(_("maximal accuracy"), "maxAccuracy", RocData.class, "getMaxAccuracy", null));
		list.add(new LabelledPropertyDescriptor(_("point abose OSS iteration"), "pointAboveOSSIteration", RocData.class, "getPointAboveOSSIteration", null));
		list.add(new LabelledPropertyDescriptor(_("point above OSS distance"), "pointAboveOSSDistance", RocData.class, "getPointAboveOSSDistance", null));
		list.add(new LabelledPropertyDescriptor(_("point below OSS iteration"), "pointBelowOSSIteration", RocData.class, "getPointBelowOSSIteration", null));
		list.add(new LabelledPropertyDescriptor(_("point below OSS distance"), "pointBelowOSSDistance", RocData.class, "getPointBelowOSSDistance", null));
		list.add(new LabelledPropertyDescriptor(_("OSS intersection TP"), "ossIntersectionTP", RocData.class, "getOssIntersectionTP", null));

		return list;

	}

}
