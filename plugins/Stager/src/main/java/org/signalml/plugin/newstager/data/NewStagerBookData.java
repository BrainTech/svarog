package org.signalml.plugin.newstager.data;

public class NewStagerBookData {
	public final NewStagerBookInfo bookInfo;
	public final NewStagerBookAtom atoms[][];

	public NewStagerBookData(NewStagerBookInfo bookInfo,
			NewStagerBookAtom atoms[][]) {
		this.bookInfo = bookInfo;
		this.atoms = atoms;
	}
}
