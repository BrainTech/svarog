package org.signalml.plugin.newstager.data;

public enum NewStagerRules {
	RK (20),
	AASM (30);
	private final int blockLengthInSecINT;
	private NewStagerRules(int blockLengthInSecINT) {
		this.blockLengthInSecINT = blockLengthInSecINT;
	}
	public int getBlockLengthInSecINT() {
		return blockLengthInSecINT;
	}
}
