/* RocDataPoint.java created 2008-01-08
 *
 */

package org.signalml.domain.roc;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;

/** RocDataPoint
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RocDataPoint implements PropertyProvider {

	private Object[] parameterValues;

	private int truePositiveCount;
	private int trueNegativeCount;
	private int falsePositiveCount;
	private int falseNegativeCount;

	private double trueRate;
	private double falseRate;

	public RocDataPoint(Object[] parameterValues, int truePositiveCount, int trueNegativeCount, int falsePositiveCount, int falseNegativeCount) {
		this.parameterValues = parameterValues;
		this.truePositiveCount = truePositiveCount;
		this.trueNegativeCount = trueNegativeCount;
		this.falsePositiveCount = falsePositiveCount;
		this.falseNegativeCount = falseNegativeCount;

		int lo;
		lo = truePositiveCount + falseNegativeCount;
		trueRate = (lo != 0 ? ((double) truePositiveCount) / lo : -1);

		lo = falsePositiveCount + trueNegativeCount;
		falseRate = (lo != 0 ? ((double) falsePositiveCount) / lo : -1);
	}

	public Object[] getParameterValues() {
		return parameterValues;
	}

	public int getTruePositiveCount() {
		return truePositiveCount;
	}

	public int getTrueNegativeCount() {
		return trueNegativeCount;
	}

	public int getFalsePositiveCount() {
		return falsePositiveCount;
	}

	public int getFalseNegativeCount() {
		return falseNegativeCount;
	}

	public double getTrueRate() {
		return trueRate;
	}

	public double getFalseRate() {
		return falseRate;
	}

	public double getSensitivity() {
		return trueRate;
	}

	public double getSpecifity() {
		return (falseRate >= 0 ? 1 - falseRate : -1);
	}

	public double getAccuracy() {
		int lo = truePositiveCount + trueNegativeCount + falsePositiveCount + falseNegativeCount;
		return (lo != 0 ? ((double)(truePositiveCount + trueNegativeCount)) / lo : -1);
	}

	public double getPositivePredictiveValue() {
		int lo = truePositiveCount + falsePositiveCount;
		return (lo != 0 ? ((double) truePositiveCount) / lo : -1);
	}

	public double getNegativePredictiveValue() {
		int lo = trueNegativeCount + falseNegativeCount;
		return (lo != 0 ? ((double) trueNegativeCount) / lo : -1);
	}

	public double getFalseDiscoveryRate() {
		int lo = truePositiveCount + falsePositiveCount;
		return (lo != 0 ? ((double) falsePositiveCount) / lo : -1);
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(_("true positive count"), "truePositiveCount", RocDataPoint.class, "getTruePositiveCount", null));
		list.add(new LabelledPropertyDescriptor(_("false positive count"), "falsePositiveCount", RocDataPoint.class, "getFalsePositiveCount", null));
		list.add(new LabelledPropertyDescriptor(_("true negative count"), "trueNegativeCount", RocDataPoint.class, "getTrueNegativeCount", null));
		list.add(new LabelledPropertyDescriptor(_("false negative count"), "falseNegativeCount", RocDataPoint.class, "getFalseNegativeCount", null));
		list.add(new LabelledPropertyDescriptor(_("false rate"), "falseRate", RocDataPoint.class, "getFalseRate", null));
		list.add(new LabelledPropertyDescriptor(_("true rate"), "trueRate", RocDataPoint.class, "getTrueRate", null));
		list.add(new LabelledPropertyDescriptor(_("sensitivity"), "sensitivity", RocDataPoint.class, "getSensitivity", null));
		list.add(new LabelledPropertyDescriptor(_("specifity"), "specifity", RocDataPoint.class, "getSpecifity", null));
		list.add(new LabelledPropertyDescriptor(_("accuracy"), "accuracy", RocDataPoint.class, "getAccuracy", null));
		list.add(new LabelledPropertyDescriptor(_("positive predictive value"), "positivePredictiveValue", RocDataPoint.class, "getPositivePredictiveValue", null));
		list.add(new LabelledPropertyDescriptor(_("negative predictive value"), "negativePredictiveValue", RocDataPoint.class, "getNegativePredictiveValue", null));
		list.add(new LabelledPropertyDescriptor(_("false discovery rate"), "falseDiscoveryRate", RocDataPoint.class, "getFalseDiscoveryRate", null));

		return list;

	}

}
