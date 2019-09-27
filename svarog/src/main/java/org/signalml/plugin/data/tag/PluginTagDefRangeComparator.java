package org.signalml.plugin.data.tag;

import java.util.Comparator;

public class PluginTagDefRangeComparator implements Comparator<IPluginTagDef> {

	@Override
	public int compare(IPluginTagDef d1, IPluginTagDef d2) {
		double o1 = d1.getOffset();
		double o2 = d2.getOffset();
		if (o1 == o2) {
			double l1 = d1.getLength();
			double l2 = d2.getLength();
			return l1 < l2 ? -1 : (l1 == l2 ? 0 : 1);
		} else {
			return o1 < o2 ? -1 : 1;
		}
	}

}
