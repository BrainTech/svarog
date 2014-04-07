package org.signalml.plugin.bookreporter.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import org.signalml.app.config.preset.Preset;
import org.signalml.plugin.bookreporter.chart.preset.BookReporterChartPreset;

/**
 * @author piotr@develancer.pl
 * (based on NewStagerParameters)
 */
@XStreamAlias("bookReporterParameters")
public class BookReporterParameters implements Serializable, Preset {

	private String name;
	public String bookFilePath;
	public String outputDirPath;
	public BookReporterChartPreset[] chartPresets;

	public BookReporterParameters() {
		name = "";
		bookFilePath = "";
		outputDirPath = "";
		chartPresets = new BookReporterChartPreset[0];
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
