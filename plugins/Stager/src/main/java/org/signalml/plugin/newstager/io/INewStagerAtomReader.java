package org.signalml.plugin.newstager.io;

import org.signalml.plugin.newstager.data.NewStagerBookData;
import org.signalml.plugin.newstager.exception.NewStagerBookReaderException;

public interface INewStagerAtomReader {

	public NewStagerBookData read() throws NewStagerBookReaderException;

}
