package org.signalml.plugin.newartifact.data;

public class NewArtifactTagWriterConfig {

	public static final float DEFAULT_PAGE_SIZE = 20.0f;
	public static final int DEFAULT_BLOCKS_PER_PAGE = 5;

	public final float pageSize;
	public final int blocksPerPage;

	public NewArtifactTagWriterConfig(float pageSize, int blocksPerPage) {
		this.pageSize = pageSize;
		this.blocksPerPage = blocksPerPage;
	}

	public NewArtifactTagWriterConfig() {
		this.pageSize = NewArtifactTagWriterConfig.DEFAULT_PAGE_SIZE;
		this.blocksPerPage = NewArtifactTagWriterConfig.DEFAULT_BLOCKS_PER_PAGE;
	}
}
