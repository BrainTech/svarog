package org.signalml.plugin.bookreporter.data;

import org.signalml.method.AbstractData;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerData)
 */
public class BookReporterData extends AbstractData {

	private BookReporterParameters parameters;

	public BookReporterData() {
		this.parameters = new BookReporterParameters();
	}

	public BookReporterParameters getParameters() {
		return parameters;
	}

	public void setParameters(BookReporterParameters parameters) {
		this.parameters = parameters;
	}
}
