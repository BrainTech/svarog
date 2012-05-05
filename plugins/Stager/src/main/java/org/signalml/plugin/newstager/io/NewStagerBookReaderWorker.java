package org.signalml.plugin.newstager.io;

import java.io.File;

import org.signalml.plugin.exception.PluginThreadRuntimeException;
import org.signalml.plugin.newstager.data.NewStagerBookData;
import org.signalml.plugin.newstager.data.NewStagerBookReaderWorkerData;
import org.signalml.plugin.newstager.exception.NewStagerBookReaderException;

import pl.edu.fuw.MP.Core.NewBookLibrary;

public class NewStagerBookReaderWorker implements Runnable {

	private final NewStagerBookReaderWorkerData data;

	public NewStagerBookReaderWorker(NewStagerBookReaderWorkerData data) {
		this.data = data;
	}

	@Override
	public void run() {
		INewStagerAtomReader reader;

		if (this.canUseFastV5BookReader()) {
			reader = new NewStagerFastBookV5AtomReader(new File(
					this.data.bookFilePath));
		} else {
			// TODO
			throw new RuntimeException("Old books not supported");
		}

		NewStagerBookData bookData;
		try {
			bookData = reader.read();
		} catch (NewStagerBookReaderException e) {
			throw new PluginThreadRuntimeException(e);
		}

		this.data.bookProvider.setBookData(bookData);
	}

	private boolean canUseFastV5BookReader() {
		return NewBookLibrary.checkFormat(this.data.bookFilePath) == NewBookLibrary.VERSION_V;
	}

}
