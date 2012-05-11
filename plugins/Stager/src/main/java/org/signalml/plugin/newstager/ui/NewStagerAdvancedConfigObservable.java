package org.signalml.plugin.newstager.ui;

import java.util.Observable;

public class NewStagerAdvancedConfigObservable extends Observable {

	private boolean flag;

	public void setEnabled(boolean flag) {
		if (this.flag != flag) {
			this.flag = flag;
			this.setChanged();
			this.notifyObservers();
		}
	}

	public boolean getEnabled() {
		return this.flag;
	}

}
