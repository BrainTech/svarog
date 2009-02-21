package eega.util.tag;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This class implemts a set of tags used to mark structures in EEG signal.
 * 
 * @version 2.0 03/03/01
 * @author Michal Dobaczewski
 */
public class TagDataSet {

	private static final int MAX_STYLE_COUNT = 256;

	/**
	 * The length of the hint string.
	 */
	public static final int hintLength = 30;

	TTagHDRCount fHDRCount;
	TTagHDRRec[][] fHDR = new TTagHDRRec[3][MAX_STYLE_COUNT];

	@SuppressWarnings("unchecked")
	ArrayList fPage;

	@SuppressWarnings("unchecked")
	ArrayList fBlk;
	short fChannels = 0;

	@SuppressWarnings("unchecked")
	ArrayList[] fChn = new ArrayList[0];

	class TTagHDRCount {

		int secPP;
		int blkPP;
		int[] count = new int[3];

		public void read(TagReader tr) throws IOException {
			secPP = tr.readUnsignedShortAsInt();
			blkPP = tr.readUnsignedShortAsInt();
			for (int i = 0; i < 3; i++)
				count[i] = tr.readUnsignedShortAsInt();
		}

		public void write(TagWriter tw) throws IOException {
			tw.write2BInt(secPP);
			tw.write2BInt(blkPP);
			for (int i = 0; i < 3; i++)
				tw.write2BInt(count[i]);
		}
	}

	/**
	 * Constructs an empty tag set with the given parameters.
	 * 
	 * @param secPP
	 *            seconds per page
	 * @param blkPP
	 *            blocks per page
	 */
	@SuppressWarnings("unchecked")
	public TagDataSet(int secPP, int blkPP) {
		fPage = new ArrayList();
		fBlk = new ArrayList();
		fHDRCount = new TTagHDRCount();
		fHDRCount.secPP = secPP;
		fHDRCount.blkPP = blkPP;
		for (int i = 0; i < 3; i++)
			fHDRCount.count[i] = 0;
	}

	/**
	 * Constructs an empty tag set with defualt 20 seconds per page and 8 blocks
	 * per page.
	 */
	public TagDataSet() {
		this(20, 8);
	}

	/**
	 * Sets the number of channels in the tag set. This clears all channel tags.
	 * The value is trimmed to a short (16bits), so only 65k channels are
	 * allowed.
	 * 
	 * @param the
	 *            new number of channels
	 */
	@SuppressWarnings("unchecked")
	public void setNoOfChannels(int noc) {
		fChannels = (short) noc;
		fChn = new ArrayList[noc];
		for (int i = 0; i < noc; i++)
			if (fChn[i] == null)
				fChn[i] = new ArrayList();
	}

	/**
	 * Returns the number of channels in the tag set.
	 * 
	 * @return the number of channels
	 */
	public int getNoOfChannels() {
		return ((int) fChannels);
	}

	/**
	 * Adds a new page tag definition to the set. Standard graphical features
	 * are used.
	 * 
	 * @param tag
	 *            tag code
	 * @param hint
	 *            hint string
	 * @see TTagHDRRec#TTagHDRRec(byte,String)
	 */
	public void addPageRec(byte tag, String hint) {
		TTagHDRRec newRec = new TTagHDRRec(tag, hint);
		fHDR[0][fHDRCount.count[0]] = newRec;
		fHDRCount.count[0]++;
	}

	/**
	 * Adds the specified page tag definition to this set.
	 * 
	 * @param newRec
	 *            the new tag type
	 */
	public void addPageRec(TTagHDRRec newRec) {
		fHDR[0][fHDRCount.count[0]] = newRec;
		fHDRCount.count[0]++;
	}

	private void _removePageRec(int index) {
		if (index < 0 || index >= fHDRCount.count[0])
			return;
		for (int i = index + 1; i < fHDRCount.count[0]; i++)
			fHDR[0][i - 1] = fHDR[0][i];
		fHDRCount.count[0]--;
	}

	/**
	 * Removes the given page tag definition from this set. This will also
	 * remove all page tags of this type.
	 * 
	 * @param index
	 *            the number of the definition to remove
	 * @return an array of page tags that had to be removed
	 */
	public PageTag[] removePageRec(int index) {

		if (index < 0 || index >= fHDRCount.count[0])
			return (null);
		byte tag = fHDR[0][index].tag;

		_removePageRec(index);

		return (removePageTagsByType(tag));
	}

	/**
	 * Removes the given page tag definition from this set. This will also
	 * remove all page tags of this type.
	 * 
	 * @param tag
	 *            the tag code of the definition to remove
	 * @return an array of page tags that had to be removed
	 */
	public PageTag[] removePageRecByType(byte tag) {
		int pos = -1, i;
		for (i = 0; i < fHDRCount.count[0]; i++) {
			if (fHDR[0][i].tag == tag) {
				pos = i;
				break;
			}
		}
		if (pos < 0)
			return (null);

		_removePageRec(pos);

		return (removePageTagsByType(tag));
	}

	/**
	 * Adds a new page tag to this set. Since each page can have only one tag
	 * the previous tag is replaced if it exists.
	 * 
	 * @param tag
	 *            the tag code
	 * @param page
	 *            the page number
	 */
	public void addPageTag(byte tag, int page) {
		PageTag newRec = new PageTag(tag, page);
		addPageTag(newRec);
	}

	/**
	 * Adds a page tag to this set. Since each page can have only one tag the
	 * previous tag is replaced if it exists.
	 * 
	 * @param pt
	 *            the tag to add (contains the code and the page number)
	 */
	@SuppressWarnings("unchecked")
	public void addPageTag(PageTag pt) {
		int pos = Collections.binarySearch(fPage, pt);
		if (pos < 0) {
			pos = (pos + 1) * (-1);
			fPage.add(pos, pt);
		} else {
			fPage.set(pos, pt);
		}
	}

	/**
	 * Adds page tags to this set. Calls {@link #addPageTag(PageTag)} for each
	 * tag.
	 * 
	 * @param pts
	 *            the tag array
	 */
	public void addPageTags(PageTag[] pts) {
		for (int i = 0; i < pts.length; i++) {
			addPageTag(pts[i]);
		}
	}

	/**
	 * Removes the page tag with the given number.
	 * 
	 * @param index
	 *            the tag number
	 * @return the removed tag or null if index wrong
	 */
	public PageTag removePageTag(int index) {
		if (index < 0 || index >= fPage.size())
			return (null);
		return ((PageTag) fPage.remove(index));
	}

	/**
	 * Removes the page tag for the given page.
	 * 
	 * @param page
	 *            the page number
	 * @return the removed tag or null if page wrong or has no tag
	 */
	@SuppressWarnings("unchecked")
	public PageTag removePageTagByPage(int page) {
		int pos = -1;
		PageTag probe = new PageTag((byte) 0, page);

		pos = Collections.binarySearch(fPage, probe);
		if (pos >= 0)
			return ((PageTag) fPage.remove(pos));

		return (null);
	}

	/**
	 * Removes all page tags of the given type from this set.
	 * 
	 * @param tag
	 *            the tag code
	 * @return an array of removed tags
	 */
	@SuppressWarnings("unchecked")
	public PageTag[] removePageTagsByType(byte tag) {

		LinkedList removals = new LinkedList();
		PageTag pt;

		for (int i = 0; i < fPage.size(); i++) {
			pt = (PageTag) fPage.get(i);
			if (pt.tag == tag)
				removals.add(pt);
			fPage.remove(i);
			i--;
		}

		PageTag[] removed = new PageTag[removals.size()];
		removals.toArray(removed);
		return (removed);
	}

	/**
	 * Adds a new block tag definition to the set. Standard graphical features
	 * are used.
	 * 
	 * @param tag
	 *            tag code
	 * @param hint
	 *            hint string
	 * @see TTagHDRRec#TTagHDRRec(byte,String)
	 */
	public void addBlockRec(byte tag, String hint) {
		TTagHDRRec newRec = new TTagHDRRec(tag, hint);
		fHDR[1][fHDRCount.count[1]] = newRec;
		fHDRCount.count[1]++;
	}

	/**
	 * Adds the specified block tag definition to this set.
	 * 
	 * @param newRec
	 *            the new tag type
	 */
	public void addBlockRec(TTagHDRRec newRec) {
		fHDR[1][fHDRCount.count[1]] = newRec;
		fHDRCount.count[1]++;
	}

	private void _removeBlockRec(int index) {
		if (index < 0 || index >= fHDRCount.count[1])
			return;
		for (int i = index + 1; i < fHDRCount.count[1]; i++)
			fHDR[1][i - 1] = fHDR[1][i];
		fHDRCount.count[1]--;
	}

	/**
	 * Removes the given block tag definition from this set. This will also
	 * remove all block tags of this type.
	 * 
	 * @param index
	 *            the number of the definition to remove
	 * @return an array of block tags that had to be removed
	 */
	public BlockTag[] removeBlockRec(int index) {
		if (index < 0 || index >= fHDRCount.count[1])
			return (null);
		byte tag = fHDR[1][index].tag;
		_removeBlockRec(index);
		return (removeBlockTagsByType(tag));
	}

	/**
	 * Removes the given block tag definition from this set. This will also
	 * remove all block tags of this type.
	 * 
	 * @param tag
	 *            the tag code of the definition to remove
	 * @return an array of block tags that had to be removed
	 */
	public BlockTag[] removeBlockRecByType(byte tag) {
		int pos = -1, i;
		for (i = 0; i < fHDRCount.count[1]; i++) {
			if (fHDR[1][i].tag == tag) {
				pos = i;
				break;
			}
		}
		if (pos < 0)
			return (null);

		_removeBlockRec(pos);

		return (removeBlockTagsByType(tag));
	}

	/**
	 * Adds a new block tag to this set. Since each block can have only one tag
	 * the previous tag is replaced if it exists.
	 * 
	 * @param tag
	 *            the tag code
	 * @param page
	 *            the page number
	 * @param block
	 *            the block number within the page
	 */
	public void addBlockTag(byte tag, int page, byte block) {
		BlockTag newRec = new BlockTag(tag, page, block);
		addBlockTag(newRec);
	}

	/**
	 * Adds a block tag to this set. Since each block can have only one tag the
	 * previous tag is replaced if it exists.
	 * 
	 * @param pt
	 *            the tag to add (contains the code and the page number and the
	 *            block number)
	 */
	@SuppressWarnings("unchecked")
	public void addBlockTag(BlockTag bt) {
		int pos = Collections.binarySearch(fBlk, bt);
		if (pos < 0) {
			pos = (pos + 1) * (-1);
			fBlk.add(pos, bt);
		} else {
			fBlk.set(pos, bt);
		}
	}

	/**
	 * Adds block tags to this set. Calls {@link #addBlockTag(BlockTag)} for
	 * each tag.
	 * 
	 * @param pts
	 *            the tag array
	 */
	public void addBlockTags(BlockTag[] bts) {
		for (int i = 0; i < bts.length; i++) {
			addBlockTag(bts[i]);
		}
	}

	/**
	 * Removes the block tag with the given number.
	 * 
	 * @param index
	 *            the tag number
	 * @return the removed tag or null if index wrong
	 */
	public BlockTag removeBlockTag(int index) {
		if (index < 0 || index >= fBlk.size())
			return (null);
		return ((BlockTag) fBlk.remove(index));
	}

	/**
	 * Removes the block tag for the given page and the given block.
	 * 
	 * @param page
	 *            the page number
	 * @param block
	 *            the block number within the page
	 * @return the removed tag or null if block wrong or has no tag
	 */
	@SuppressWarnings("unchecked")
	public BlockTag removeBlockTagByBlock(int page, byte block) {
		int pos = -1;
		BlockTag probe = new BlockTag((byte) 0, page, block);

		pos = Collections.binarySearch(fBlk, probe);
		if (pos >= 0)
			return ((BlockTag) fBlk.remove(pos));

		return (null);
	}

	/**
	 * Removes all block tags of the given type from this set.
	 * 
	 * @param tag
	 *            the tag code
	 * @return an array of removed tags
	 */
	@SuppressWarnings("unchecked")
	public BlockTag[] removeBlockTagsByType(byte tag) {

		LinkedList removals = new LinkedList();
		BlockTag bt;

		for (int i = 0; i < fBlk.size(); i++) {
			bt = (BlockTag) fBlk.get(i);
			if (bt.tag == tag)
				removals.add(bt);
			fBlk.remove(i);
			i--;
		}

		BlockTag[] removed = new BlockTag[removals.size()];
		removals.toArray(removed);
		return (removed);
	}

	/**
	 * Adds a new channel tag definition to the set. Standard graphical features
	 * are used.
	 * 
	 * @param tag
	 *            tag code
	 * @param hint
	 *            hint string
	 * @see TTagHDRRec#TTagHDRRec(byte,String)
	 */
	public void addChannelRec(byte tag, String hint) {
		TTagHDRRec newRec = new TTagHDRRec(tag, hint);
		fHDR[2][fHDRCount.count[2]] = newRec;
		fHDRCount.count[2]++;
	}

	/**
	 * Adds the specified channel tag definition to this set.
	 * 
	 * @param newRec
	 *            the new tag type
	 */
	public void addChannelRec(TTagHDRRec newRec) {
		fHDR[2][fHDRCount.count[2]] = newRec;
		fHDRCount.count[2]++;
	}

	private void _removeChannelRec(int index) {
		if (index < 0 || index >= fHDRCount.count[2])
			return;
		for (int i = index + 1; i < fHDRCount.count[2]; i++)
			fHDR[2][i - 1] = fHDR[2][i];
		fHDRCount.count[2]--;
	}

	/**
	 * Removes the given channel tag definition from this set. This will also
	 * remove all channel tags of this type.
	 * 
	 * @param index
	 *            the number of the definition to remove
	 * @return an array of channel tags that had to be removed, divided into
	 *         channels
	 */
	public ChannelTag[][] removeChannelRec(int index) {
		if (index < 0 || index >= fHDRCount.count[2])
			return (null);
		byte tag = fHDR[2][index].tag;
		_removeChannelRec(index);
		return (removeChannelTagsByType(tag));
	}

	/**
	 * Removes the given channel tag definition from this set. This will also
	 * remove all channel tags of this type.
	 * 
	 * @param tag
	 *            the tag code of the definition to remove
	 * @return an array of channel tags that had to be removed, divided into
	 *         channels
	 */
	public ChannelTag[][] removeChannelRecByType(byte tag) {
		int pos = -1, i;
		for (i = 0; i < fHDRCount.count[2]; i++) {
			if (fHDR[2][i].tag == tag) {
				pos = i;
				break;
			}
		}
		if (pos < 0)
			return (null);

		_removeChannelRec(pos);

		return (removeChannelTagsByType(tag));
	}

	/**
	 * Adds a new channel tag to this set.
	 * 
	 * @param channel
	 *            the channel to add to
	 * @param tag
	 *            the tag code
	 * @param offset
	 *            the offset of the beggining of the tag
	 * @param length
	 *            the length of the tag
	 */
	public void addChannelTag(int channel, byte tag, long offset, int length) {
		ChannelTag newRec = new ChannelTag(tag, offset, length);
		addChannelTag(channel, newRec);
	}

	/**
	 * Adds a channel tag to this set.
	 * 
	 * @param channel
	 *            the channel to add to
	 * @param ct
	 *            the tag to add (contains the code, the offset and the length)
	 */
	@SuppressWarnings("unchecked")
	public void addChannelTag(int channel, ChannelTag ct) {
		int pos = Collections.binarySearch(fChn[channel], ct);
		if (pos < 0) {
			pos = (pos + 1) * (-1);
		}
		fChn[channel].add(pos, ct);
	}

	/**
	 * Adds channel tags to this set.
	 * 
	 * @param channel
	 *            the channel to add to
	 * @param cts
	 *            the tag array
	 */
	@SuppressWarnings("unchecked")
	public void addChannelTags(int channel, ChannelTag[] cts) {
		ArrayList al = new ArrayList(cts.length);
		for (int i = 0; i < cts.length; i++)
			al.add(cts[i]);
		fChn[channel].addAll(al);
		Collections.sort(fChn[channel]);
	}

	/**
	 * Removes the channel tag with the given number.
	 * 
	 * @param index
	 *            the tag number
	 * @return the removed tag or null if index wrong
	 */
	public ChannelTag removeChannelTag(int channel, int index) {
		if (channel < 0 || channel >= fChannels)
			return (null);
		if (index < 0 || index >= fChn[channel].size())
			return (null);
		return ((ChannelTag) fChn[channel].remove(index));
	}

	/**
	 * Removes channel tags that have their offset within the given boundary.
	 * 
	 * @param channel
	 *            the channel to remove from
	 * @param from
	 *            the minimum offset (inclusive)
	 * @param to
	 *            the maximum offset (exclusive)
	 * @return the array of removed tags
	 */
	@SuppressWarnings("unchecked")
	public ChannelTag[] removeChannelTagsByOffset(int channel, long from, long to) {
		if (channel < 0 || channel >= fChannels)
			return (null);
		LinkedList removals = new LinkedList();
		int pos = Collections.binarySearch(fChn[channel], new ChannelTag((byte) 0, from, 10));
		if (pos < 0)
			pos = (pos + 1) * (-1);
		ChannelTag ct;
		int i;
		for (i = pos; i < fChn[channel].size(); i++) {
			ct = (ChannelTag) fChn[channel].get(i);
			if (ct.offset > to)
				break;
			removals.add(ct);
			fChn[channel].remove(i);
			i--;
		}

		ChannelTag[] removed = new ChannelTag[removals.size()];
		removals.toArray(removed);
		return (removed);
	}

	/**
	 * Removes all channel tags of the given type from this set.
	 * 
	 * @param channel
	 *            the channel to remove from
	 * @param tag
	 *            the tag code
	 * @return an array of removed tags
	 */
	@SuppressWarnings("unchecked")
	public ChannelTag[] removeChannelTagsByType(int channel, byte tag) {
		if (channel < 0 || channel >= fChannels)
			return (null);
		LinkedList removals = new LinkedList();
		ChannelTag ct;
		for (int i = 0; i < fChn[channel].size(); i++) {
			ct = (ChannelTag) fChn[channel].get(i);
			if (ct.tag == tag)
				removals.add(ct);
			fChn[channel].remove(i);
			i--;
		}

		ChannelTag[] removed = new ChannelTag[removals.size()];
		removals.toArray(removed);
		return (removed);
	}

	/**
	 * Removes all channel tags of the given type from this set from all
	 * channels.
	 * 
	 * @param tag
	 *            the tag code
	 * @return an array of channel tags that had to be removed, divided into
	 *         channels
	 */
	public ChannelTag[][] removeChannelTagsByType(byte tag) {
		ChannelTag[][] removed = new ChannelTag[fChannels][];
		for (int i = 0; i < fChannels; i++)
			removed[i] = removeChannelTagsByType(i, tag);
		return (removed);
	}

	/**
	 * Returns the seconds per page value of this set.
	 * 
	 * @return seconds per page
	 */
	public int getSecPP() {
		return (fHDRCount.secPP);
	}

	/**
	 * Returns the blocks per page value of this set.
	 * 
	 * @return blocks per page
	 */
	public int getBlkPP() {
		return (fHDRCount.blkPP);
	}

	/**
	 * Returns an array containing the counts of definition types for page,
	 * block and channel tags.
	 * 
	 * @return an array of counts
	 */
	public int[] getRecCounts() {
		return (fHDRCount.count);
	}

	/**
	 * Returns the count of page tag definitions in this set.
	 * 
	 * @return the count
	 */
	public int getPageRecCount() {
		return (fHDRCount.count[0]);
	}

	/**
	 * Returns the count of block tag definitions in this set.
	 * 
	 * @return the count
	 */
	public int getBlockRecCount() {
		return (fHDRCount.count[1]);
	}

	/**
	 * Returns the count of channel tag definitions in this set.
	 * 
	 * @return the count
	 */
	public int getChannelRecCount() {
		return (fHDRCount.count[2]);
	}

	/**
	 * Returns the tag count in an array. Row 0 contains the count of page tags,
	 * row 1 the count of block tags and subsequent rows contain the counts of
	 * channel tags in each channel (the count for the first channel has the
	 * index 2 and so on).
	 * 
	 * @return an array of tag counts
	 */
	public int[] getTagCounts() {
		int[] c = new int[fChannels + 2];
		c[0] = fPage.size();
		c[1] = fBlk.size();
		for (int i = 0; i < fChannels; i++)
			c[i + 2] = fChn[i].size();
		return (c);
	}

	/**
	 * Returns the count of page tags.
	 * 
	 * @return the tag count
	 */
	public int getPageTagCount() {
		return (fPage.size());
	}

	/**
	 * Returns the number of the last page with a page tag.
	 * 
	 * @return the page number
	 */
	public int getMaxPage() {
		if (fPage.isEmpty())
			return -1;
		return (((PageTag) fPage.get(fPage.size() - 1)).page);
	}

	/**
	 * Returns the number of the first page with a page tag.
	 * 
	 * @return the page number
	 */
	public int getMinPage() {
		if (fPage.isEmpty())
			return -1;
		return (((PageTag) fPage.get(0)).page);
	}

	/**
	 * Returns the count of block tags.
	 * 
	 * @return the tag count
	 */
	public int getBlockTagCount() {
		return (fBlk.size());
	}

	/**
	 * Returns the count of channel tags for the specified channel.
	 * 
	 * @param c
	 *            the channel number
	 * @return the tag count (0 if c invalid)
	 */
	public int getChannelTagCount(int c) {
		if (c < 0 || c >= fChannels)
			return (0);
		return (fChn[c].size());
	}

	/**
	 * Returns the page tag definition with the given index.
	 * 
	 * @param index
	 *            the index to retrieve
	 * @return the tag definition
	 */
	public TTagHDRRec getPageRec(int index) {
		if (index >= fHDRCount.count[0])
			throw new IndexOutOfBoundsException();
		return (fHDR[0][index]);
	}

	/**
	 * Returns the block tag definition with the given index.
	 * 
	 * @param index
	 *            the index to retrieve
	 * @return the tag definition
	 */
	public TTagHDRRec getBlockRec(int index) {
		if (index >= fHDRCount.count[1])
			throw new IndexOutOfBoundsException();
		return (fHDR[1][index]);
	}

	/**
	 * Returns the channel tag definition with the given index.
	 * 
	 * @param index
	 *            the index to retrieve
	 * @return the tag definition
	 */
	public TTagHDRRec getChannelRec(int index) {
		if (index >= fHDRCount.count[2])
			throw new IndexOutOfBoundsException();
		return (fHDR[2][index]);
	}

	/**
	 * Returns an array of all page tag definitions.
	 * 
	 * @return an array of tag definitions
	 */
	public TTagHDRRec[] getPageRecArray() {
		TTagHDRRec[] recs = new TTagHDRRec[fHDRCount.count[0]];
		for (int i = 0; i < recs.length; i++)
			recs[i] = fHDR[0][i];
		return (recs);
	}

	/**
	 * Returns an array of all block tag definitions.
	 * 
	 * @return an array of tag definitions
	 */
	public TTagHDRRec[] getBlockRecArray() {
		TTagHDRRec[] recs = new TTagHDRRec[fHDRCount.count[1]];
		for (int i = 0; i < recs.length; i++)
			recs[i] = fHDR[1][i];
		return (recs);
	}

	/**
	 * Returns an array of all channel tag definitions.
	 * 
	 * @return an array of tag definitions
	 */
	public TTagHDRRec[] getChannelRecArray() {
		TTagHDRRec[] recs = new TTagHDRRec[fHDRCount.count[1]];
		for (int i = 0; i < recs.length; i++)
			recs[i] = fHDR[1][i];
		return (recs);
	}

	/**
	 * Returns an array list of all page tags.
	 * 
	 * @return a list of tags
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getPageTagList() {
		return (fPage);
	}

	/**
	 * Returns an array list of all block tags.
	 * 
	 * @return a list of tags
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getBlockTagList() {
		return (fBlk);
	}

	/**
	 * Returns an array of array lists of all channel tags.
	 * 
	 * @return a list of array lists, one per channel
	 */
	@SuppressWarnings("unchecked")
	public ArrayList[] getChannelTagListArray() {
		return (fChn);
	}

	/**
	 * Returns an array list of channel tags for the given channel.
	 * 
	 * @param c
	 *            the channel number
	 * @return a list of tags
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getChannelTagList(int c) {
		return (fChn[c]);
	}

	/**
	 * Returns an array of all page tags.
	 * 
	 * @return an array of tags
	 */
	@SuppressWarnings("unchecked")
	public PageTag[] getPageTagArray() {
		PageTag[] pt = new PageTag[fPage.size()];
		fPage.toArray(pt);
		return (pt);
	}

	/**
	 * Returns an array of all block tags.
	 * 
	 * @return an array of tags
	 */
	@SuppressWarnings("unchecked")
	public BlockTag[] getBlockTagArray() {
		BlockTag[] pt = new BlockTag[fBlk.size()];
		fBlk.toArray(pt);
		return (pt);
	}

	/**
	 * Returns an array of channel tags for the given channel.
	 * 
	 * @param c
	 *            the channel number
	 * @return an array of tags
	 */
	@SuppressWarnings("unchecked")
	public ChannelTag[] getChannelTagArray(int c) {
		ChannelTag[] pt = new ChannelTag[fChn[c].size()];
		fChn[c].toArray(pt);
		return (pt);
	}

	/**
	 * Returns the page tag with the given index.
	 * 
	 * @param index
	 *            the number of the tag
	 * @return the tag
	 */
	public PageTag getPageTag(int index) {
		return ((PageTag) fPage.get(index));
	}

	/**
	 * Returns the block tag with the given index.
	 * 
	 * @param index
	 *            the number of the tag
	 * @return the tag
	 */
	public BlockTag getBlockTag(int index) {
		return ((BlockTag) fBlk.get(index));
	}

	/**
	 * Returns the channel tag with the given index in the given channel.
	 * 
	 * @param c
	 *            the channel number
	 * @param index
	 *            the number of the tag
	 * @return the tag
	 */
	public ChannelTag getChannelTag(int c, int index) {
		return ((ChannelTag) fChn[c].get(index));
	}

	/**
	 * This static method returns a TagDataSet restored from the given ".tag"
	 * file (binary format).
	 * 
	 * @param s
	 *            the file name of the file to read
	 * @return the parsed set
	 * @exception TagException
	 *                thrown if an I/O error occurs or the contents are invalid
	 */
	@SuppressWarnings("unchecked")
	public static TagDataSet loadTagDataSet(String s) throws TagException {

		int i = 0, e = 0, countChn, count;
		int[] cCount;
		PageTag pagRec;
		BlockTag blkRec;
		ChannelTag chnRec;

		TagDataSet tds = new TagDataSet();
		TagReader tr = null;

		try {

			tr = new TagReader(s);

			tds.fHDRCount.read(tr);
			for (i = 0; i < 3; i++) {
				if( tds.fHDRCount.count[i] > MAX_STYLE_COUNT ) {
					throw new TagException("eega.tooManyStyles");
				}
				for (e = 0; e < tds.fHDRCount.count[i]; e++) {
					tds.fHDR[i][e] = new TTagHDRRec();
					tds.fHDR[i][e].read(tr);
				}
			}

			count = tr.readUnsignedShortAsInt();
			tds.fPage = new ArrayList(count);
			for (i = 0; i < count; i++) {
				pagRec = new PageTag();
				pagRec.read(tr);
				tds.fPage.add(pagRec);
			}
			Collections.sort(tds.fPage);

			count = tr.readUnsignedShortAsInt();
			tds.fBlk = new ArrayList(count);
			for (i = 0; i < count; i++) {
				blkRec = new BlockTag();
				blkRec.read(tr);
				tds.fBlk.add(blkRec);
			}
			Collections.sort(tds.fBlk);

			tds.fChannels = (byte) tr.readUnsignedByteAsShort();
			countChn = tds.fChannels; 
			
			tds.fChn = new ArrayList[countChn];
			cCount = new int[countChn];
			for (i = 0; i < countChn; i++) {
				cCount[i] = tr.readUnsignedShortAsInt();
				tds.fChn[i] = new ArrayList(cCount[i]);
			}
			for (i = 0; i < countChn; i++) {
				for (e = 0; e < cCount[i]; e++) {
					chnRec = new ChannelTag();
					chnRec.read(tr);
					tds.fChn[i].add(chnRec);
				}
				Collections.sort(tds.fChn[i]);
			}

		} catch (EOFException ex) {
			throw new TagException("eega.unexpEndOfFile");
		} catch (IOException ex) {
			throw new TagException("eega.errReadFile");
		} finally {
			tr.close();
		}

		return (tds);

	}

	/**
	 * This method sorts all tags in the tag set.
	 */
	@SuppressWarnings("unchecked")
	public void sort() {
		Collections.sort(fPage);
		Collections.sort(fBlk);
		for (int i = 0; i < fChannels; i++) {
			if (fChn[i] != null) {
				Collections.sort(fChn[i]);
			}
		}
	}

	/**
	 * This method writes a TagDataSet to the given ".tag" file (binary format).
	 * All the tags are written.
	 * 
	 * @param s
	 *            the file name of the file to written
	 * @exception TagException
	 *                thrown if an I/O error occurs
	 */
	public void write(String s) throws TagException {
		write(s, true, true, true);
	}

	/**
	 * This method writes a TagDataSet to the given ".tag" file (binary format).
	 * This methods allows you to specify which types of tags should be written.
	 * 
	 * @param s
	 *            the file name of the file to written
	 * @param wp
	 *            write page tags
	 * @param wb
	 *            write block tags
	 * @param wc
	 *            write channel tags
	 * @exception TagException
	 *                thrown if an I/O error occurs
	 */
	public void write(String s, boolean wp, boolean wb, boolean wc) throws TagException {

		// File file = new File(PathConverter.convertPath(s));
		File file = new File(s);
		int i, e, f;
		TagWriter tw = null;

		try {

			if (file.exists())
				file.delete();
			file.createNewFile();

			tw = new TagWriter(file.getPath());

			fHDRCount.write(tw);
			for (i = 0; i < 3; i++)
				for (e = 0; e < fHDRCount.count[i]; e++)
					fHDR[i][e].write(tw);

			if (wp) {
				tw.write2BInt(e = fPage.size());
				for (i = 0; i < e; i++)
					((PageTag) fPage.get(i)).write(tw);
			} else {
				tw.write2BInt(0);
			}

			if (wb) {
				tw.write2BInt(e = fBlk.size());
				for (i = 0; i < e; i++)
					((BlockTag) fBlk.get(i)).write(tw);
			} else {
				tw.write2BInt(0);
			}

			if (wc) {
				tw.writeByte((byte) fChannels);
				for (i = 0; i < fChannels; i++) {
					tw.write2BInt(fChn[i].size());
				}
				for (i = 0; i < fChannels; i++) {
					e = fChn[i].size();
					for (f = 0; f < e; f++)
						((ChannelTag) fChn[i].get(f)).write(tw);
				}
			} else {
				tw.writeByte((byte) 0);
			}
		} catch (IOException ex) {
			throw new TagException("eega.errWriteFile");
		} finally {
			tw.close();
		}
	}

}
