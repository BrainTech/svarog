/* EvokedPotentialResultWrapper.java created 2008-01-14
 *
 */

package org.signalml.app.method.ep;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import static org.signalml.app.util.i18n.SvarogI18n._;
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

	public String getAveragedSegmentsCount() {
		return convertListToString(result.getAveragedSegmentsCount());
	}

	public String getUnusableSegmentsCount() {
		return convertListToString(result.getUnusableSegmentsCount());
	}

	public String getArtifactRejectedSegmentsCount() {
		return convertListToString(result.getArtifactRejectedSegmentsCount());
	}

	public String convertListToString(List<Integer> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));

			if (i < list.size()-1)
				sb.append(", ");
		}
		return sb.toString();

	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<>();

		list.add(new LabelledPropertyDescriptor(_("Number of averaged segments"), "averagedCount", EvokedPotentialResultWrapper.class, "getAveragedSegmentsCount", null));
		list.add(new LabelledPropertyDescriptor(_("Number of unusable segments"), "unusableSegments", EvokedPotentialResultWrapper.class, "getUnusableSegmentsCount", null));
		list.add(new LabelledPropertyDescriptor(_("Number of artifact rejected segments"), "artifactRejectedSegments", EvokedPotentialResultWrapper.class, "getArtifactRejectedSegmentsCount", null));

		return list;

	}

}
