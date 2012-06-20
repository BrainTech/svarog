/**
 *
 */
package org.signalml.plugin.export.signal;


/**
 * This interface represents a type of a selection.
 * @see SignalSelectionType
 * @author Marcin Szumski
 */
public interface ExportedSignalSelectionType {

	/**
	 * selections contains whole pages (all channels)
	 */
	String PAGE = "page";

	/**
	 * selection contains whole blocks but not necessarily pages
	 * (all channels)
	 */
	String BLOCK = "block";
	/**
	 * selection contains the custom part of a channel
	 */
	String CHANNEL = "channel";

	/**
	 * Returns the name of this type of a selection.
	 * @return the name of this type
	 */
	String getName();

	/**
	 * Returns if this type is a page.
	 * @return true if this type is a page, false otherwise
	 */
	boolean isPage();

	/**
	 * Returns if this type is a block.
	 * @return true if this type is a block, false otherwise
	 */
	boolean isBlock();

	/**
	 * Returns if this type is a channel.
	 * @return true if this type is a channel, false otherwise
	 */
	boolean isChannel();

}