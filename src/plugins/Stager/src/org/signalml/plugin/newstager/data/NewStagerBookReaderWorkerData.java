package org.signalml.plugin.newstager.data;

import org.signalml.plugin.newstager.logic.book.NewStagerBookDataProvider;

public class NewStagerBookReaderWorkerData {

	public final String bookFilePath;
	public final NewStagerBookDataProvider bookProvider;

	public NewStagerBookReaderWorkerData(String bookFilePath, NewStagerBookDataProvider bookProvider) {
		this.bookFilePath = bookFilePath;
		this.bookProvider = bookProvider;
	}

}
