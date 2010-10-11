/* SignalSelectionType.java created 2007-09-28
 *
 */

package org.signalml.plugin.export.signal;

import org.springframework.context.MessageSourceResolvable;

/**
 * This class represents the type of a {@link SignalSelection selection}.
 * Contains only the name of this type.
 * Possible types are: page, block, channel.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum SignalSelectionType implements MessageSourceResolvable, ExportedSignalSelectionType {

    /**
     * the type of the {@link SignalSelection selection}. Such selection
     * is composed of whole pages of the signal (all channels).
     * Page consists of blocks.
     */
	PAGE("page"),
    /**
     * the type of the {@link SignalSelection selection}. Such selection
     * is composed of whole blocks (part of the page) of the signal
     * (all channels).
     */
	BLOCK("block"),
    /**
     * the type of the {@link SignalSelection selection}. Such selection
     * is composed of a custom time interval for a single channel.
     */
	CHANNEL("channel");

    /**
     * name of this type of selection
     */
	private String name;

    /**
     * Constructor. Creates the type of a given name.
     * @param name the name the type
     */
	private SignalSelectionType(String name) {
		this.name = name;
	}

    /**
     * Returns the name of this type of {@link SignalSelection selection}.
     * @return the name of this type
     */
	@Override
	public String getName() {
		return name;
	}

    /**
     * Returns if this type is a page.
     * @return true if this type is a page, false otherwise
     */
	@Override
	public boolean isPage() {
		return (this == PAGE);
	}

    /**
     * Returns if this type is a block.
     * @return true if this type is a block, false otherwise
     */
	@Override
	public boolean isBlock() {
		return (this == BLOCK);
	}
	
	/**
	 * Returns the type of a selection of a given name.
	 * @param name the name of the type
	 * @return the type of a selection of a given name
	 */
	public static SignalSelectionType typeByName(String name){
		if (name.equals(ExportedSignalSelectionType.BLOCK)) return BLOCK;
		if (name.equals(ExportedSignalSelectionType.PAGE)) return PAGE;
		if (name.equals(ExportedSignalSelectionType.CHANNEL)) return CHANNEL;
		return null;
	}

    /**
     * Returns if this type is a channel
     * @return true if this type is a channel, false otherwise
     */
	@Override
	public boolean isChannel() {
		return (this == CHANNEL);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalSelectionType." + name };
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

}
