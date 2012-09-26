package org.signalml.plugin.impl.change.events;

import org.signalml.plugin.export.change.events.PluginSignalChangeEvent;
import org.signalml.plugin.export.signal.ExportedSignalDocument;

public class PluginSignalChangeEventImpl implements PluginSignalChangeEvent {

	private ExportedSignalDocument document;

	public PluginSignalChangeEventImpl(ExportedSignalDocument document) {
		this.document = document;
	}

	@Override
	public ExportedSignalDocument getDocument() {
		return document;
	}

}
