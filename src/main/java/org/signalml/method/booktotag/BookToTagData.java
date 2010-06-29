/* BookToTagData.java created 2008-03-26
 *
 */

package org.signalml.method.booktotag;

import java.util.LinkedHashSet;

import org.signalml.domain.book.StandardBook;

/** BookToTagData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagData {

	private StandardBook book;

	private LinkedHashSet<Integer> channels;

	private boolean makePageTags;
	private boolean makeBlockTags;
	private boolean makeChannelTags;

	public BookToTagData() {
		channels = new LinkedHashSet<Integer>();
		makePageTags = true;
		makeBlockTags = true;
		makeChannelTags = true;
	}

	public StandardBook getBook() {
		return book;
	}

	public void setBook(StandardBook book) {
		this.book = book;
	}

	public LinkedHashSet<Integer> getChannels() {
		return channels;
	}

	public void setChannels(LinkedHashSet<Integer> channels) {
		this.channels = channels;
	}

	public void replaceChannels(int[] array) {
		channels.clear();
		for (int i=0; i<array.length; i++) {
			channels.add(array[i]);
		}
	}

	public void addChannel(int channel) {
		channels.add(channel);
	}

	public void removeChannel(int channel) {
		channels.remove(new Integer(channel));
	}

	public boolean isMakePageTags() {
		return makePageTags;
	}

	public void setMakePageTags(boolean makePageTags) {
		this.makePageTags = makePageTags;
	}

	public boolean isMakeBlockTags() {
		return makeBlockTags;
	}

	public void setMakeBlockTags(boolean makeBlockTags) {
		this.makeBlockTags = makeBlockTags;
	}

	public boolean isMakeChannelTags() {
		return makeChannelTags;
	}

	public void setMakeChannelTags(boolean makeChannelTags) {
		this.makeChannelTags = makeChannelTags;
	}

}
