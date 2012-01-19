/* EvokedPotentialResultWrapper.java created 2008-01-14
 *
 */

package org.signalml.app.method.ep;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.signalml.method.ep.EvokedPotentialResult;

/** EvokedPotentialResultWrapper
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialResultWrapper implements PropertyProvider {

	private EvokedPotentialResult result;

	public EvokedPotentialResultWrapper(EvokedPotentialResult result) {
		this.result = result;
	}

	public int getTotalCount() {
		return result.getAveragedCount() + result.getSkippedCount();
	}

	public int getAveragedCount() {
		return result.getAveragedCount();
	}

	public int getSkippedCount() {
		return result.getSkippedCount();
	}

	public int getChannelCount() {
		return result.getChannelCount();
	}

	public int getSampleCount() {
		return result.getSampleCount();
	}

	public float getSamplingFrequency() {
		return result.getSamplingFrequency();
	}

	public double getSecondsAfter() {
		return result.getSecondsAfter();
	}

	public double getSecondsBefore() {
		return result.getSecondsBefore();
	}

	public double getSegmentLength() {
		return result.getSecondsAfter() + result.getSecondsBefore();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.totalCount", "totalCount", EvokedPotentialResultWrapper.class, "getTotalCount", null));
		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.averagedCount", "averagedCount", EvokedPotentialResultWrapper.class, "getAveragedCount", null));
		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.skippedCount", "skippedCount", EvokedPotentialResultWrapper.class, "getSkippedCount", null));

		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.channelCount", "channelCount", EvokedPotentialResultWrapper.class, "getChannelCount", null));
		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.sampleCount", "sampleCount", EvokedPotentialResultWrapper.class, "getSampleCount", null));
		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.samplingFrequency", "samplingFrequency", EvokedPotentialResultWrapper.class, "getSamplingFrequency", null));
		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.segmentLength", "segmentLength", EvokedPotentialResultWrapper.class, "getSegmentLength", null));
		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.secondsBefore", "secondsBefore", EvokedPotentialResultWrapper.class, "getSecondsBefore", null));
		list.add(new LabelledPropertyDescriptor("property.evokedPotentialResult.secondsAfter", "secondsAfter", EvokedPotentialResultWrapper.class, "getSecondsAfter", null));

		return list;

	}

}
