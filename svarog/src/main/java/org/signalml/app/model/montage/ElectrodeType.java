package org.signalml.app.model.montage;
import static org.signalml.app.util.i18n.SvarogI18n._;

public enum ElectrodeType {

	STANDARD_SIGNAL_VALUES(_("Standard"), 0, 10000, 15000),
	HIGH_SIGNAL_VALUES(_("High signal values"), 100, 100000, 150000);

	private String name;
	private int min;
	private int limit;
	private int max;

	ElectrodeType(String name, int min, int limit, int max) {
		this.name = name;
		this.min = min;
		this.limit = limit;
		this.max = max;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@Override
	public String toString() {
		return name;
	}

}
