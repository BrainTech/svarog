/* BookToTagData.java created 2008-03-26
 *
 */

package org.signalml.method.booktotag;

import java.util.LinkedHashSet;

import org.signalml.domain.book.StandardBook;
import org.signalml.plugin.export.method.BaseMethodData;

/**
 * BookToTagData class provides data to be processed by BookToTagMethod.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagData extends BaseMethodData {

	private StandardBook book;

	private LinkedHashSet<Integer> channels;

	private boolean makePageTags;
	private boolean makeBlockTags;
	private boolean makeChannelTags;

	/**
	 * Creates new instance of BookToTagData class.
	 */
	public BookToTagData() {
		channels = new LinkedHashSet<Integer>();
		makePageTags = true;
		makeBlockTags = true;
		makeChannelTags = true;
	}

	/**
	 * Returns StandardBook.
	 * @return StandardBook
	 */
	public StandardBook getBook() {
		return book;
	}

	/**
	 * Set StandardBook.
	 * @param book StandardBook to set
	 */
	public void setBook(StandardBook book) {
		this.book = book;
	}

	/**
	 * Returns set of channels.
	 * @return set of channels
	 */
	public LinkedHashSet<Integer> getChannels() {
		return channels;
	}

	/**
	 * Sets set of channels.
	 * @param channels channels to be set
	 */
	public void setChannels(LinkedHashSet<Integer> channels) {
		this.channels = channels;
	}

	/**
	 * Replaces channels.
	 * @param array array of channels to replace set of channels
	 */
	public void replaceChannels(int[] array) {
		channels.clear();
		for (int i=0; i<array.length; i++) {
			channels.add(array[i]);
		}
	}

	/**
	 * Add specified channel to set of channels.
	 * @param channel channel to be added to set of channels
	 */
	public void addChannel(int channel) {
		channels.add(channel);
	}

	/**
	 * Removes specified channel from set of channels.
	 * @param channel channel to be removed from set of channels
	 */
	public void removeChannel(int channel) {
		channels.remove(new Integer(channel));
	}

	/**
	 * Checks if adding page tags is supported.
	 * @return true if adding page tags is supported, otherwise false
	 */
	public boolean isMakePageTags() {
		return makePageTags;
	}

	/**
	 * If specified boolean value is true then page tags will be added, otherwise not.
	 * @param makePageTags boolean value saying if page tags will be added
	 */
	public void setMakePageTags(boolean makePageTags) {
		this.makePageTags = makePageTags;
	}

	/**
	 * Checks if adding block tags is supported.
	 * @return true if adding block tags is supported, otherwise false
	 */
	public boolean isMakeBlockTags() {
		return makeBlockTags;
	}


	/**
	 * If specified boolean value is true then block tags will be added, otherwise not.
	 * @param makeBlockTags boolean value saying if block tags will be added
	 */
	public void setMakeBlockTags(boolean makeBlockTags) {
		this.makeBlockTags = makeBlockTags;
	}

	/**
	 * Checks if adding channel tags is supported.
	 * @return true if adding channel tags is supported, otherwise false
	 */
	public boolean isMakeChannelTags() {
		return makeChannelTags;
	}

	/**
	 * If specified boolean value is true then channel tags will be added, otherwise not.
	 * @param makeChannelTags boolean value saying if channel tags will be added
	 */
	public void setMakeChannelTags(boolean makeChannelTags) {
		this.makeChannelTags = makeChannelTags;
	}

}
