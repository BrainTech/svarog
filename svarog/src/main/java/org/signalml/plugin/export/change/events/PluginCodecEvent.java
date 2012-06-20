/**
 *
 */
package org.signalml.plugin.export.change.events;

/**
 * Interface for an event associated with codec changes.
 * Provides codec format name.
 * @author Marcin Szumski
 */
public interface PluginCodecEvent extends PluginEvent {
	/**
	 * Returns the format name of the changed codec.
	 * @return the format name of the changed codec
	 */
	String getCodecFormatName();
}
