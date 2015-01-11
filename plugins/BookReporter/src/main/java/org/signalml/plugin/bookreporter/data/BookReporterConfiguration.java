package org.signalml.plugin.bookreporter.data;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.plugin.data.method.PluginMethodWithWorkDirConfiguration;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerConfiguration)
 */
@XStreamAlias("bookReporterConfiguration")
public class BookReporterConfiguration extends PluginMethodWithWorkDirConfiguration {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "bookReporterConfig";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

}
