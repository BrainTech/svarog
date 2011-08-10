package org.signalml.plugin.data.io;

import org.signalml.app.model.PagingParameterDescriptor;

public class PluginTagWriterConfig {

	public final float pageSize;
	public final int blocksPerPage;

	public PluginTagWriterConfig(float pageSize, int blocksPerPage) {
		this.pageSize = pageSize;
		this.blocksPerPage = blocksPerPage;
	}

	public PluginTagWriterConfig() {
		this.pageSize = PagingParameterDescriptor.DEFAULT_PAGE_SIZE;
		this.blocksPerPage = PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE;
	}
}
