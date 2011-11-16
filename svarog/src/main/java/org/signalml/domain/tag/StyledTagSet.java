/* StyledTagSet.java created 2007-09-28
 *
 */

package org.signalml.domain.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.signalml.app.config.preset.Preset;
import org.signalml.plugin.export.signal.tagStyle.TagAttributeValue;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributeDefinition;

/**
 * This class represents a set of
 * {@link Tag tagged selections} and their {@link TagStyles styles}.
 * Two tagged selections with the same type can not intersect so this class
 * splits, merges and replaces them while adding.
 *
 * This class contains additional information such as a size of a page, a number
 * of blocks per page and a length of a block (in seconds).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("tagFile")
@XStreamConverter(StyledTagSetConverter.class)
public class StyledTagSet implements Serializable, Preset {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(StyledTagSet.class);

        /**
         * page size in seconds
         */
	private float pageSize;
        /**
         * the number of blocks in a page
         */
	private int blocksPerPage;

        /**
         * block size in seconds
         */
	private float blockSize;

	private TagSignalIdentification tagSignalIdentification;

	/**
	 * Contains the lists of all tag styles used in this styled tag set.
	 */
	protected TagStyles styles;

        /**
         * Collection of all tagged selections.
         */
	protected TreeSet<Tag> tags;

        /**
         * Maximal length of a tagged selection in <i>tags</i>.
         * This is just an estimate - may be 10% more than the actual length
         * of the longest tag in the set.
         */
	protected double maxTagLength = 0;

        /**
         * list of tagged selections of signal pages
         */
	private ArrayList<Tag> pageTagsCache = null;
        private ArrayList<Tag> blockTagsCache = null;
        private ArrayList<Tag> channelTagsCache = null;

        /**
         * the description of the tagged set
         */
	private String info;

        /**
         * the description of the montage
         */
	private String montageInfo;

        /**
         * the tagged montage
         */
	private Montage montage;

        /**
         * list of listeners associated with the current object
         */
	private EventListenerList listenerList = new EventListenerList();
	/**
	 * StyledTagSet preset name.
	 */
	private String name; 

        /**
         * Constructor. Creates a default StyledTagSet without any tags or
         * styles.
         */
	public StyledTagSet() {
		this(null, null, SignalParameterDescriptor.DEFAULT_PAGE_SIZE, SignalParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE);
	}

        /**
         * Constructor. Creates a StyledTagSet with given size of a page and
         * given number of blocks per page, but without any tags or styles.
         * @param pageSize a size of a page in seconds
         * @param blocksPerPage a number of blocks per page
         */
	public StyledTagSet(float pageSize, int blocksPerPage) {
		this(null, null, pageSize, blocksPerPage);
	}

        /**
         * Constructor. Creates a StyledTagSet with given styles of selections,
         * but without tagged selections.
         * Default sizes of a page and a block are used.
         * @param styles {@link TagStyle tag styles} to be added to the created object
         */
	public StyledTagSet(TagStyles styles) {
		this(styles, null, SignalParameterDescriptor.DEFAULT_PAGE_SIZE, SignalParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE);
	}

        /**
         * Constructor. Creates a StyledTagSet with given styles of selections,
         * size of a page and number of blocks per page, but without
         * tagged selections.
         * @param styles {@link TagStyle tag styles} to be added to the created object
         * @param pageSize a size of a page in seconds
         * @param blocksPerPage a number of blocks per page
         */
	public StyledTagSet(TagStyles styles, float pageSize, int blocksPerPage) {
		this(styles, null, pageSize, blocksPerPage);
	}

        /**
         * Constructor. Creates a StyledTagSet with given styles of selections
         * and given tagged selections.
         * Default sizes of a page and a block are used.
         * @param styles {@link TagStyle tag styles} to be added to the created object
         * @param tags tagged selections to be added to the created object
         */
	public StyledTagSet(TagStyles styles, TreeSet<Tag> tags) {
		this(styles, tags, SignalParameterDescriptor.DEFAULT_PAGE_SIZE, SignalParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE);
	}

        /**
         * Constructor. Creates a StyledTagSet with given parameters.
         * @param {@link TagStyle tag styles} to be added to the created object
         * @param tags tagged selections to be added to the created object
         * @param pageSize a size of a page in seconds
         * @param blocksPerPage a number of blocks per page
         */
	public StyledTagSet(TagStyles styles, TreeSet<Tag> tags, float pageSize, int blocksPerPage) {
		if (pageSize <= 0) {
			throw new SanityCheckException("Page size must be > 0");
		}
		if (blocksPerPage <= 0) {
			throw new SanityCheckException("Blocks per page must be > 0");   // free-style block tags disabled for now
		}

		this.styles = styles != null? styles: new TagStyles();
		this.styles.setStyledTagSet(this);

		if (tags == null) {
			this.tags = new TreeSet<Tag>();
		} else {
			this.tags = tags;
		}
		this.pageSize = pageSize;
		this.blocksPerPage = blocksPerPage;
		if (blocksPerPage > 0) {
			blockSize = pageSize / blocksPerPage;
		} else {
			blockSize = -1;
		}
		if (!verifyTags()) {
			throw new SanityCheckException("Tags not compatible with settings");
		}
		calculateMaxTagLength();
	}

        /**
         * Returns a size of a page in seconds.
         * @return a size of a page in seconds
         */
	public float getPageSize() {
		return pageSize;
	}

        /**
         * Returns a number of blocks per page.
         * @return a number of blocks per page
         */
	public int getBlocksPerPage() {
		return blocksPerPage;
	}

        /**
         * Returns a size of a block in seconds.
         * @return a size of a block in seconds
         */
	public float getBlockSize() {
		return blockSize;
	}

        /**
         * Returns the {@link Montage montage} tagged by this set.
         * @return the montage tagged by this set
         */
	public Montage getMontage() {
		return montage;
	}

        /**
         * Sets the {@link Montage montage} tagged by this set.
         * @param montage the montage tagged by this set
         */
	public void setMontage(Montage montage) {
		this.montage = montage;
	}

        /**
         * Returns the list of {@link TagStyle styles} of tagged selections.
         * @return the lists of all tag styles
         */
	public List<TagStyle> getListOfStyles() {
		return styles.getAllStyles();
	}

	/**
	 * Returns the object containing all tag styles contained in this
	 * {@link StyledTagSet}.
	 * @return the tag styles object connected with this StylesTagSet
	 */
	public TagStyles getTagStyles() {
		return styles;
	}

        /**
         * Returns the list of {@link TagStyle styles} of
         * {@link Tag tagged selections} for a given
         * {@link SignalSelectionType type} of a selection.
         * @param type the type of a selection
         * @return the list of styles of tagged selections for a given type of
         * a selection
         */
	public List<TagStyle> getStyles(SignalSelectionType type) {
		return styles.getStyles(type);
	}

        /**
         * Returns the list of {@link TagStyle styles} of
         * {@link Tag tagged selections} for a given
         * {@link SignalSelectionType type} of a selection, excluding markers
         * if needed.
         * @param type the type of a selection
         * @param allowMarkers false if markers should be excluded,
         * true otherwise
         * @return the list of styles
         */
	public List<TagStyle> getStyles(SignalSelectionType type, boolean allowMarkers) {
		return styles.getStyles(type, allowMarkers);
	}

        /**
         * Returns the list of {@link TagStyle styles} of tagged page selections.
         * @return the list of styles of tagged page selections
         */
	public List<TagStyle> getPageStyles() {
		return styles.getStyles(SignalSelectionType.PAGE);
	}

        /**
         * Returns the list of {@link TagStyle styles} of tagged block selections.
         * @return the list of styles of tagged block selections
         */
	public List<TagStyle> getBlockStyles() {
		return styles.getStyles(SignalSelectionType.BLOCK);
	}

        /**
         * Returns the list of {@link TagStyle styles} of tagged channel
         * selections.
         * @return the list of styles of tagged channel selections
         */
	public List<TagStyle> getChannelStyles() {
		return styles.getStyles(SignalSelectionType.CHANNEL);
	}

        /**
         * Returns the list of {@link TagStyle styles} of tagged page selections
         * excluding markers.
         * @return the list of styles of tagged page selections without markers
         */
	public List<TagStyle> getPageStylesNoMarkers() {
		return styles.getStyles(SignalSelectionType.PAGE, false);
	}

        /**
         * Returns the list of {@link TagStyle styles} of tagged block selections
         * excluding markers.
         * @return the list of styles of tagged block selections without markers
         */
	public List<TagStyle> getBlockStylesNoMarkers() {
		return styles.getStyles(SignalSelectionType.BLOCK, false);
	}

        /**
         * Returns the list of {@link TagStyle styles} of tagged channel
         * selections excluding markers.
         * @return the list of styles of tagged channel selections without markers
         */
	public List<TagStyle> getChannelStylesNoMarkers() {
		return styles.getStyles(SignalSelectionType.CHANNEL, false);
	}

        /**
         * Returns the {@link TagStyle style} of a given name
	 * with the given type.
	 * @param type the type of the style (page/block/channel)
         * @param name the name of a style
         * @return the style of a given name
         */
	public TagStyle getStyle(SignalSelectionType type, String name) {
		return styles.getStyle(type, name);
	}

        /**
         * Returns the number of {@link TagStyle styles}.
         * @return the number of styles
         */
	public int getTagStyleCount() {
		return styles.getStylesCount();
	}

        /**
         * Returns the number of {@link TagStyle styles} for a given type of
         * a selection.
         * @param type the type of a selection
         * @return the number of styles for a given type of a selection
         */
	public int getTagStyleCount(SignalSelectionType type) {
		return styles.getStylesCount(type);
	}

        /**
         * Returns the number of {@link TagStyle styles} for page selections.
         * @return the number of styles for page selections
         */
	public int getPageStyleCount() {
		return getTagStyleCount(SignalSelectionType.PAGE);
	}

        /**
         * Returns the number of {@link TagStyle styles} for block selections.
         * @return the number of styles for block selections
         */
	public int getBlockStyleCount() {
		return getTagStyleCount(SignalSelectionType.BLOCK);
	}

        /**
         * Returns the number of {@link TagStyle styles} for channel selections.
         * @return the number of styles for channel selections
         */
	public int getChannelStyleCount() {
		return getTagStyleCount(SignalSelectionType.CHANNEL);
	}

        /**
         * Returns the style of a given index in an array of
         * {@link TagStyle styles} for a given type of a selection.
         * @param type the type of a selection
         * @param index the index in an array of styles for a given
         * type of a selection
         * @return the found style
         */
	public TagStyle getStyleAt(SignalSelectionType type, int index) {
		return styles.getStyleAt(type, index);
	}

        /**
         * Returns the {@link TagStyle style} of a given index in an array of
         * page styles.
         * @param index the index in an array of page styles
         * @return the found style
         */
	public TagStyle getPageStyleAt(int index) {
		return styles.getStyleAt(SignalSelectionType.PAGE, index);
	}

        /**
         * Returns the {@link TagStyle style} of a given index in an array
         * of block styles.
         * @param index the index in an array of block styles
         * @return the found style
         */
	public TagStyle getBlockStyleAt(int index) {
		return styles.getStyleAt(SignalSelectionType.BLOCK, index);
	}

        /**
         * Returns the {@link TagStyle style} of a given index in an array
         * of page styles.
         * @param index the index in an array of block styles
         * @return the found style
         */
	public TagStyle getChannelStyleAt(int index) {
		return styles.getStyleAt(SignalSelectionType.CHANNEL, index);
	}

        /**
         * Returns an index of a {@link TagStyle styles} in an appropriate array.
         * @param style the style which index will be checked
         * @return an index of a style in an appropriate array, -1 if style is
         * not in any array
         */
	public int indexOfStyle(TagStyle style) {
		return styles.getIndexOf(style);
	}

        /**
         * Returns an index of a {@link TagStyle style} in an array of
         * page styles.
         * @param style the style which index will be checked
         * @return an index of a style in an array of page styles, -1 if style
         * is not in that array
         */
	public int indexOfPageStyle(TagStyle style) {
		return styles.getIndexOf(style);
	}

        /**
         * Returns an index of a {@link TagStyle styles} in an array of
         * block styles.
         * @param style the style which index will be checked
         * @return an index of a style in an array of block styles, -1 if style
         * is not in that array
         */
	public int indexOfBlockStyle(TagStyle style) {
		return styles.getIndexOf(style);
	}

        /**
         * Returns an index of a {@link TagStyle styles} in an array of channel
         * styles.
         * @param style the style which index will be checked
         * @return an index of a style in an array of channel styles, -1 if style
         * is not in that array
         */
	public int indexOfChannelStyle(TagStyle style) {
		return styles.getIndexOf(style);
	}

        /**
         * Returns all {@link Tag tagged selections}.
         * @return all tagged selections
         */
	public SortedSet<Tag> getTags() {
		return tags;
	}

        /**
         * Returns {@link Tag tagged selections} that may be between two
         * given positions.
         * Always remember that this method returns tags that MAY be between
         * these two positions. It doesn't mean they actually ARE (!!!).
         * You always need to verify this. The only guarantee being made is that
         * ALL the tags that MAY be in this region  are returned.
         * This set is inclusive at both ends!
         * @param start starting position
         * @param end ending position
         * @return set of tagged selections that may be between two
         * given positions.
         */
	public SortedSet<Tag> getTagsBetween(double start, double end) {
		Tag startMarker = new Tag(null, start-maxTagLength, 0);
		Tag endMarker = new Tag(null,end,Float.MAX_VALUE); // note that lengths matter, so that all tags starting at exactly end will be selected
		return tags.subSet(startMarker, true, endMarker, true);
	}

        /**
         * Returns the number of {@link Tag tagged selections}.
         * @return the number of tagged selections
         */
	public int getTagCount() {
		return tags.size();
	}

        /**
         * Returns the number of {@link Tag tagged selections} for a given
         * {@link SignalSelectionType type} of a selection
         * @param type the type of a selection
         * @return the number of tagged selections for a given type of a selection
         */
	public int getTagCount(SignalSelectionType type) {
		if (type == SignalSelectionType.PAGE) {
			return getPageTagCount();
		}
		else if (type == SignalSelectionType.BLOCK) {
			return getBlockTagCount();
		}
		else if (type == SignalSelectionType.CHANNEL) {
			return getChannelTagCount();
		} else {
			return 0;
		}
	}

        /**
         * Returns the number of tagged page {@link Tag selections}.
         * @return the number of tagged page selections
         */
	public int getPageTagCount() {
		if (pageTagsCache == null) {
			makeTagCache();
		}
		return pageTagsCache.size();
	}

        /**
         * Returns the number of tagged block {@link Tag selections}.
         * @return the number of tagged block selections
         */
	public int getBlockTagCount() {
		if (blockTagsCache == null) {
			makeTagCache();
		}
		return blockTagsCache.size();
	}

        /**
         * Returns the number of tagged channel {@link Tag selections}.
         * @return the number of tagged channel selections
         */
	public int getChannelTagCount() {
		if (channelTagsCache == null) {
			makeTagCache();
		}
		return channelTagsCache.size();
	}

        /**
         * Returns the {@link Tag tagged selection} of a given index in an
         * array of selections for a given {@link SignalSelectionType type}
         * of a selection.
         * @param type the type of a selection
         * @param index the index in an array of selections for a given
         * type of a selection
         * @return the found tagged selection
         */
	public Tag getTagAt(SignalSelectionType type, int index) {
		if (type == SignalSelectionType.PAGE) {
			return getPageTagAt(index);
		}
		else if (type == SignalSelectionType.BLOCK) {
			return getBlockTagAt(index);
		}
		else if (type == SignalSelectionType.CHANNEL) {
			return getChannelTagAt(index);
		} else {
			return null;
		}
	}

        /**
         * Returns the {@link Tag selection} of a given index in an array of
         * tagged page selections.
         * @param index the index in an array of tagged page selections
         * @return the found tagged selection
         */
	public Tag getPageTagAt(int index) {
		if (pageTagsCache == null) {
			makeTagCache();
		}
		return pageTagsCache.get(index);
	}

        /**
         * Returns the {@link Tag selection} of a given index in an array of
         * tagged block selections.
         * @param index the index in an array of tagged block selections
         * @return the found tagged selection
         */
	public Tag getBlockTagAt(int index) {
		if (blockTagsCache == null) {
			makeTagCache();
		}
		return blockTagsCache.get(index);
	}

        /**
         * Returns the {@link Tag selection} of a given index in an array of
         * tagged channel selections.
         * @param index the index in an array of tagged channel selections
         * @return the found tagged selection
         */
	public Tag getChannelTagAt(int index) {
		if (channelTagsCache == null) {
			makeTagCache();
		}
		return channelTagsCache.get(index);
	}

        /**
         * Returns an index of a tagged {@link Tag selection} in an appropriate
         * array.
         * @param tag the selection which index will be checked
         * @return an index of a tagged selection in an appropriate array,
         * -1 if tagged selection is not in any array
         */
	public int indexOfTag(Tag tag) {
		SignalSelectionType type = tag.getType();
		if (type == SignalSelectionType.PAGE) {
			return indexOfPageTag(tag);
		}
		else if (type == SignalSelectionType.BLOCK) {
			return indexOfBlockTag(tag);
		}
		else if (type == SignalSelectionType.CHANNEL) {
			return indexOfChannelTag(tag);
		} else {
			return -1;
		}
	}

        /**
         * Returns an index of a {@link Tag selection} in an array of tagged
         * page selections.
         * @param tag the selection which index will be checked
         * @return an index of a selections in an array of tagged page selections,
         * -1 if selection is not in that array
         */
	public int indexOfPageTag(Tag tag) {
		if (pageTagsCache == null) {
			makeTagCache();
		}
		return pageTagsCache.indexOf(tag);
	}

        /**
         * Returns an index of a {@link Tag selection} in an array of tagged
         * block selections.
         * @param tag the selection which index will be checked
         * @return an index of a selections in an array of tagged block selections,
         * -1 if selection is not in that array
         */
	public int indexOfBlockTag(Tag tag) {
		if (blockTagsCache == null) {
			makeTagCache();
		}
		return blockTagsCache.indexOf(tag);
	}

        /**
         * Returns an index of a {@link Tag selection} in an array of tagged
         * channel selections.
         * @param tag the selection which index will be checked
         * @return an index of a selections in an array of tagged channel selections,
         * -1 if selection is not in that array
         */
	public int indexOfChannelTag(Tag tag) {
		if (channelTagsCache == null) {
			makeTagCache();
		}
		return channelTagsCache.indexOf(tag);
	}

        /**
         * Verifies if the length of {@link Tag selection} is a multiple of
         * a block size for block selections and multiple of a page size
         * for page selections.
         * @return true if lengths are valid, false otherwise
         */
	public boolean verifyTags() {
		if (pageTagsCache == null || blockTagsCache == null) {
			makeTagCache();
		}
		if (blockSize > 0) {
			for (Tag tag : blockTagsCache) {
				if (tag.getLength() != blockSize) {
					logger.debug("Tag block size is [" + tag.getLength() + "] should be [" + blockSize + "]");
					return false;
				}
			}
		}
		for (Tag tag : pageTagsCache) {
			if (tag.getLength() != pageSize) {
				logger.debug("Tag page size is [" + tag.getLength() + "] should be [" + pageSize + "]");
				return false;
			}
		}
		return true;
	}

        /**
         * Verifies if the length of a given tagged {@link Tag selection} is
         * a multiple of a block size for block selection or multiple of
         * a page size for page selection.
         * @param tag the tagged selection to be verified
         * @return true if length is valid, false otherwise
         */
	public boolean verifyTag(Tag tag) {
		SignalSelectionType type = tag.getType();
		if (type.isBlock()) {
			if (tag.getLength() != blockSize) {
				logger.debug("Tag block size is [" + tag.getLength() + "] should be [" + blockSize + "]");
				return false;
			}
		} else if (type.isPage()) {
			if (tag.getLength() != pageSize) {
				logger.debug("Tag page size is [" + tag.getLength() + "] should be [" + pageSize + "]");
				return false;
			}
		}
		return true;
	}

        /**
         * Adds a given {@link TagStyle style} to this set.
         * @param style the tag style to be added
         */
	public void addStyle(TagStyle style) {
		styles.addStyle(style);
	}

        /**
         * Removes the {@link TagStyle style} of a given name.
         * @param name the name of a style to be removed
         */
	public void removeStyle(TagStyle style) {
		styles.removeStyle(style);
	}

        /**
         * Sets the {@link TagStyle style} of a given name to a new value.
         * @param name the name of a style
         * @param style new style to be set
         */
	public void updateStyle(String name, TagStyle style) {
		styles.updateStyle(name, style);
	}

        /**
         * Returns whether there are any {@link Tag tagged selections} of
         * a given style.
         * @param name the name of a style
         * @return true if there are any tagged selections of a given style,
         * false otherwise
         */
	public boolean hasTagsWithStyle(TagStyle style) {
		for (Tag tag : tags) {
			if (tag.getStyle() == style) {
				return true;
			}
		}
		return false;
	}

        /**
         * Adds a {@link Tag tagged selection} to this set.
         * @param tag tagged selection to be added
         * @throws SanityCheckException thrown if tag is not valid (invalid
         * length for a given type)
         */
	public void addTag(Tag tag) {
		if (!verifyTag(tag)) {
			throw new SanityCheckException("Tag not compatible");
		}

		TagStyle style = this.getStyle(tag.getStyle().getType(), tag.getStyle().getName());
		if (style != null)
			tag.setStyle(style);

		tags.add(tag);
		invalidateTagCache(tag.getStyle().getType());
		if (maxTagLength < tag.getLength()) {
			maxTagLength = tag.getLength();
		}
		fireTagAdded(tag);
	}

        /**
         * Removes {@link Tag tagged selections} that intersect with
         * a given selection and are of the same type as given.
         * @param selection the selection to which tagged selections will be
         * compared
         */
	public void eraseTags(SignalSelection selection) {

		// erase same type tags from selection

		double selStart = selection.getPosition();
		double selEnd = selStart + selection.getLength();
		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd);
		double confStart;
		double confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();
		SignalSelectionType type = selection.getType();
		boolean calculateLength = false;
		while (it.hasNext()) {
			confTag = it.next();
			if (confTag.getType() == type) {
				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if ((confStart < selEnd) && (confEnd > selStart) && (confTag.getChannel() == selection.getChannel())) {
					it.remove();
					invalidateTagCache(type);
					if (confTag.getLength() > (maxTagLength * 0.75)) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);
				}
			}
		}

		if (calculateLength) {
			calculateMaxTagLength();
		}

	}

        /**
         * Removes {@link Tag tagged selections} that intersect with a given
         * tagged selection and have the same type of a style. Adds the new tag.
         * @param tag the tagged selection to which tagged selections will be
         * compared and which will be added
         */
	public void replaceSameTypeTags(Tag tag) {

		// remove conflicting tags

		double selStart = tag.getPosition();
		double selEnd = selStart + tag.getLength();
		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd);
		TagStyle confStyle;
		double confStart;
		double confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();
		SignalSelectionType type = tag.getStyle().getType();
		boolean calculateLength = false;
		while (it.hasNext()) {
			confTag = it.next();
			confStyle = confTag.getStyle();
			if (confStyle.getType() == type) {
				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if ((confStart < selEnd) && (confEnd > selStart) && (confTag.getChannel() == tag.getChannel())) {
					it.remove();
					invalidateTagCache(confStyle.getType());
					if (confTag.getLength() > (maxTagLength * 0.75)) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);
				}
			}
		}

		if (calculateLength) {
			calculateMaxTagLength();
		}

		addTag(tag);

	}

        /**
         * Adds a given {@link Tag selection} to the collection of tagged
         * selections. If any selection intersects with given and has the same
         * type as given it is changed:
         * 1) if has the same style as given it is merged with it,
         * 2) if has a different style it is shortened so that it won't
         * intersect with given any more.
         * @param tag the tagged selection to be added
         */
	public void splitAndMergeSameTypeTags(Tag tag) {

		// split conflicting tags while merging same type tags

		double selStart = tag.getPosition();
		double selEnd = selStart + tag.getLength();
		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd);
		TagStyle confStyle;
		double confStart;
		double confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();
		SignalSelectionType type = tag.getStyle().getType();
		boolean calculateLength = false;

		LinkedList<Tag> addedTags = new LinkedList<Tag>();
		Tag addedTag;

		double newSelStart = selStart;
		double newSelEnd = selEnd;

		while (it.hasNext()) {
			confTag = it.next();
			confStyle = confTag.getStyle();
			if (confStyle.getType() == type) {
				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if ((confStart <= selEnd) && (confEnd >= selStart) && (confTag.getChannel() == tag.getChannel())) {

					if (confTag.getStyle() == tag.getStyle()) {  // same type, merge

						if (confStart < newSelStart) {
							newSelStart = confStart;
						}
						if (confEnd > newSelEnd) {
							newSelEnd = confEnd;
						}

					} else { // different type, split & replace

						if ((confStart < selEnd) && (confEnd > selStart)) {

							if (confStart < selStart) {  // if the conflicting tag partially precedes this tag
								addedTag = confTag.clone();
								addedTag.setLength(selStart-confStart);
								addedTags.add(addedTag);
							}
							if (confEnd > selEnd) {  // if the conflicting tag partially follows this tag
								addedTag = confTag.clone();
								addedTag.setParameters(selEnd,confEnd-selEnd);
								addedTags.add(addedTag);
							}

						} else {
							continue; // don't change adjacent tags
						}

					}

					it.remove();
					invalidateTagCache(confStyle.getType());
					if (confTag.getLength() > (maxTagLength * 0.75)) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);

				}
			}
		}

		tag.setParameters(newSelStart, newSelEnd-newSelStart);

		if (calculateLength) {
			calculateMaxTagLength();
		}

		for (Tag t : addedTags) {
			addTag(t);
		}
		addTag(tag);

	}

        /**
         * Adds a given channel {@link Tag selection} to the collection of
         * tagged selections. If any channel selection intersects with given
         * and has the same style as given it is merged with it.
         * @param tag the tagged channel selection to be added
         */
	public void mergeSameTypeChannelTags(Tag tag) {

		// merge adjacent channel tags
		TagStyle style = tag.getStyle();
		SignalSelectionType type = style.getType();
		if (type != SignalSelectionType.CHANNEL) {
			addTag(tag);
			return;
		}

		if (style.isMarker()) {
			addTag(tag);
			return;
		}

		double selStart = tag.getPosition();
		double selEnd = selStart + tag.getLength();

		double newSelStart = selStart;
		double newSelEnd = selEnd;

		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd);
		TagStyle confStyle;
		double confStart;
		double confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();

		boolean calculateLength = false;
		while (it.hasNext()) {
			confTag = it.next();
			confStyle = confTag.getStyle();
			if (confStyle.isMarker()) {
				continue;
			}
			if (confStyle.getType() == SignalSelectionType.CHANNEL) {

				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if ((confTag.getStyle() == tag.getStyle()) && (confStart <= selEnd) && (confEnd >= selStart) && (confTag.getChannel() == tag.getChannel())) {
					it.remove();
					invalidateTagCache(confStyle.getType());
					if (confTag.getLength() > (maxTagLength * 0.75)) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);

					if (confStart < newSelStart) {
						newSelStart = confStart;
					}
					if (confEnd > newSelEnd) {
						newSelEnd = confEnd;
					}

				}
			}
		}

		tag.setParameters(newSelStart, newSelEnd-newSelStart);

		if (calculateLength) {
			calculateMaxTagLength();
		}

		addTag(tag);

	}

        /**
         * Removes the {@link Tag tagged selection} from this set.
         * @param tag the tagged selection to be removed
         */
	public void removeTag(Tag tag) {
		boolean removed = tags.remove(tag);
		if (removed) {
			invalidateTagCache(tag.getStyle().getType());
			if (tag.getLength() > (maxTagLength * 0.75)) {
				calculateMaxTagLength();
			}
			fireTagRemoved(tag);
		}
	}

        /**
         * Removes <i>oldTag</i> from {@link Tag tagged selections} list and
         * adds <i>tag</i> to it.
         * @param oldTag the tagged selection to be removed
         * @param tag the tagged selection to be added
         */
	public void updateTag(Tag oldTag, Tag tag) {
		if (!verifyTag(tag)) {
			throw new SanityCheckException("Tag not compatible");
		}
		boolean removed = tags.remove(oldTag);
		if (removed) {
			invalidateTagCache(tag.getStyle().getType());
			if (tag.getLength() != oldTag.getLength() && oldTag.getLength() > (maxTagLength * 0.9)) {
				calculateMaxTagLength();
			}
		}
		tags.add(tag);
		invalidateTagCache(tag.getStyle().getType());
		if (maxTagLength < tag.getLength()) {
			maxTagLength = tag.getLength();
		}
		fireTagChanged(tag, oldTag);
	}

        /**
         * Verifies {@link Tag tag} and invalidates cache.
         * @param tag tag that was edited
         * @throws SanityCheckException if tag has invalid length (for a given
         * type)
         */
	public void editTag(Tag tag) {
		if (!verifyTag(tag)) {
			throw new SanityCheckException("Tag not compatible");
		}
		invalidateTagCache(tag.getStyle().getType());
		fireTagChanged(tag, tag);
	}

        /**
         * Returns the map associating {@link TagStyle tag styles} with
         * KeyStrokes assigned to them.
         * @return the map associating tag styles with KeyStrokes assign to them.
         */
	public HashMap<KeyStroke, TagStyle> getStylesByKeyStrokes() {
		return styles.getStylesByKeyStrokes();
	}

        /**
         * Returns {@link TagStyle tag style} associated with a given key.
         * @param keyStroke key to which tag style is associated
         * @return tag style associated with a given key
         */
	public TagStyle getStyleByKeyStroke(KeyStroke keyStroke) {
		return getStylesByKeyStrokes().get(keyStroke);
	}

        /**
         * Returns the {@link TagSignalIdentification identification} of the
         * signal.
         * @return the identification of the signal
         */
	public TagSignalIdentification getTagSignalIdentification() {
		return tagSignalIdentification;
	}

        /**
         * Sets the {@link TagSignalIdentification identification} of the
         * signal.
         * @param tagSignalIdentification the identification of the signal
         */
	public void setTagSignalIdentification(TagSignalIdentification tagSignalIdentification) {
		this.tagSignalIdentification = tagSignalIdentification;
	}

        /**
         * Returns the description of this tagged set.
         * @return the description of this tagged set
         */
	public String getInfo() {
		return info;
	}

        /**
         * Sets the description of this tagged set.
         * @param info the description of this tagged set
         */
	public void setInfo(String info) {
		this.info = info;
	}

        /**
         * Returns the description of the {@link #montage montage}.
         * @return the description of the montage
         */
	public String getMontageInfo() {
		return montageInfo;
	}

        /**
         * Sets the description of the {@link #montage montage}.
         * @param montageInfo the description of the montage
         */
	public void setMontageInfo(String montageInfo) {
		this.montageInfo = montageInfo;
	}

        /**
         * Returns estimated maximal length of a {@link Tag tagged selection}
         * in <i>tags</i>.
         * Note that this is just an estimate - may be 10% more than the
         * actual length of the longest tag in the set.
         * @return estimated maximal length of a tagged selection in <i>tags</i>.
         */
	public double getMaxTagLength() {
		return maxTagLength;
	}

        /**
         * Adds a {@link TagListener TagListener} to the list of listeners.
         * @param listener the TagListener to be added
         */
	public void addTagListener(TagListener listener) {
		listenerList.add(TagListener.class, listener);
	}

        /**
         * Removes a {@link TagListener TagListener} from the list of listeners.
         * @param listener the TagListener to be removed
         */
	public void removeTagListener(TagListener listener) {
		listenerList.remove(TagListener.class, listener);
	}

        /**
         * Adds a {@link TagStyleListener TagStyleListener} to the list
         * of listeners.
         * @param listener the TagStyleListener to be added
         */
	public void addTagStyleListener(TagStyleListener listener) {
		styles.addTagStyleListener(listener);
	}

        /**
         * Removes a {@link TagStyleListener TagStyleListener} from the list
         * of listeners.
         * @param listener the TagStyleListener to be removed
         */
	public void removeTagStyleListener(TagStyleListener listener) {
		styles.removeTagStyleListener(listener);
	}

        /**
         * {@link TagStyleListener tag style listeners} that a
         * {@link Tag tagged selection} of a given index has been added.
         * @param tag the added tagged selection
         */
	protected void fireTagAdded(Tag tag) {
		Object[] listeners = listenerList.getListenerList();
		TagEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TagListener.class) {
				if (e == null) {
					e = new TagEvent(this,tag,tag.getPosition(),tag.getPosition()+tag.getLength());
				}
				((TagListener)listeners[i+1]).tagAdded(e);
			}
		}
	}

        /**
         * Fires {@link TagStyleListener tag style listeners} that a
         * {@link Tag tagged selection} of a given index has been removed.
         * @param tag the removed tagged selection
         */
	protected void fireTagRemoved(Tag tag) {
		Object[] listeners = listenerList.getListenerList();
		TagEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TagListener.class) {
				if (e == null) {
					e = new TagEvent(this,tag,tag.getPosition(),tag.getPosition()+tag.getLength());
				}
				((TagListener)listeners[i+1]).tagRemoved(e);
			}
		}
	}

        /**
         * Fires {@link TagStyleListener tag style listeners} that a
         * {@link Tag tagged selection} of a given index has been changed.
         * @param tag the new value of the changed tagged selection
         * @param oldTag the old value of the changed tagged selection
         */
	protected void fireTagChanged(Tag tag, Tag oldTag) {
		Object[] listeners = listenerList.getListenerList();
		TagEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TagListener.class) {
				if (e == null) {
					double start;
					double end;
					if (oldTag != null) {
						start = Math.min(tag.getPosition(), oldTag.getPosition());
						end = Math.max(tag.getPosition()+tag.getLength(), oldTag.getPosition()+oldTag.getLength());
					} else {
						start = tag.getPosition();
						end = tag.getPosition() + tag.getLength();
					}
					e = new TagEvent(this,tag,start,end);
				}
				((TagListener)listeners[i+1]).tagChanged(e);
			}
		}
	}

        /**
         * Invalidates the cache of {@link Tag tagged selections} of a given
         * type.
         * If cache is invalidated it will need to be built again before
         * next usage.
         * @param type the type of a selection
         */
	private void invalidateTagCache(SignalSelectionType type) {
		if (type == SignalSelectionType.PAGE) {
			pageTagsCache = null;
		} else if (type == SignalSelectionType.BLOCK) {
			blockTagsCache = null;
		} else if (type == SignalSelectionType.CHANNEL) {
			channelTagsCache = null;
		}
	}

        /**
         * Creates the cache of {@link Tag tagged selections} for all types of
         * selection (if they don't exist or are invalidated).
         */
	private void makeTagCache() {

		if (pageTagsCache != null && blockTagsCache != null && channelTagsCache != null) {
			return;
		}

		ArrayList<Tag> newPageTags = null;
		ArrayList<Tag> newBlockTags = null;
		ArrayList<Tag> newChannelTags = null;

		if (pageTagsCache == null) {
			newPageTags = new ArrayList<Tag>();
		}
		if (blockTagsCache == null) {
			newBlockTags = new ArrayList<Tag>();
		}
		if (channelTagsCache == null) {
			newChannelTags = new ArrayList<Tag>();
		}

		TagStyle style;
		SignalSelectionType type;

		for (Tag tag : tags) {
			style = tag.getStyle();
			if (style != null) {
				type = style.getType();
				if (type == SignalSelectionType.PAGE && pageTagsCache == null) {
					newPageTags.add(tag);
				} else if (type == SignalSelectionType.BLOCK && blockTagsCache == null) {
					newBlockTags.add(tag);
				} else if (type == SignalSelectionType.CHANNEL && channelTagsCache == null) {
					newChannelTags.add(tag);
				}
			}
		}

		if (pageTagsCache == null) {
			pageTagsCache = newPageTags;
		}
		if (blockTagsCache == null) {
			blockTagsCache = newBlockTags;
		}
		if (channelTagsCache == null) {
			channelTagsCache = newChannelTags;
		}

	}

        /**
         * Calculates the maximum length of a {@link Tag tagged selection}.
         */
	private void calculateMaxTagLength() {
		double maxTagLength = 0;
		for (Tag tag : tags) {
			if (maxTagLength < tag.getLength()) {
				maxTagLength = tag.getLength();
			}
		}
		this.maxTagLength = maxTagLength;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public StyledTagSet clone() {
		StyledTagSet newTagSet = new StyledTagSet(styles);
		return newTagSet;
	}

	/**
	 * Copies all styles from the given {@link StyledTagSet} into this tag set.
	 * Deletes all styles that are not defined in the given StyledTagSet
	 * unless some tags exist in this StyledTagSet which use the mentioned
	 * style. In that case the style is not removed.
	 * @param tagSet the tag set from which styles should be removed.
	 * @return the list of tag styles names which couldn't be removed from
	 * this tag set.
	 */
	public List<String> copyStylesFrom(StyledTagSet tagSet) {

		List<String> stylesThatCouldNotBeDeleted = new ArrayList<String>();

		Set<TagStyle> stylesFromBoth = new HashSet<TagStyle>();
		stylesFromBoth.addAll(this.getListOfStyles());
		stylesFromBoth.addAll(tagSet.getListOfStyles());

		//for each tag check if it should be removed, updated or deleted
		for (TagStyle style: stylesFromBoth) {
			SignalSelectionType styleType = style.getType();
			TagStyle newStyle = tagSet.getStyle(styleType, style.getName());
			TagStyle oldStyle = this.getStyle(styleType, style.getName());

			if (newStyle != null && oldStyle != null) {
				updateStyle(oldStyle.getName(), newStyle);
			}
			else if (newStyle == null && oldStyle != null) {
				if (!tryToRemoveStyle(oldStyle)) {
					stylesThatCouldNotBeDeleted.add(oldStyle.getName());
				}
			}
			else if (newStyle != null && oldStyle == null) {
				addStyle(newStyle);
			}
		}

		//invalidate all caches
		invalidateTagCache(SignalSelectionType.PAGE);
		invalidateTagCache(SignalSelectionType.BLOCK);
		invalidateTagCache(SignalSelectionType.CHANNEL);

		//connect tags with appropriate tag styles (after update the reference
		//may be not correct any more
		updateTagAttributesBinding();

		return stylesThatCouldNotBeDeleted;

	}

	/**
	 * Tries to remove the given style. If a tag exists that uses this style,
	 * it cannot be removed and false is returned. If no tag exists that uses
	 * this style, it is removed and 'true' is returned.
	 * @param style the style to be removed
	 * @return true if the style was sucessfully removed, false otherwise
	 */
	protected boolean tryToRemoveStyle(TagStyle style) {
		boolean doTagsWithThisStyleExist = false;
		Iterator<Tag> tagIterator = tags.iterator();
		while(tagIterator.hasNext()) {
			Tag tag = tagIterator.next();
			if (tag.getStyle().getName().equals(style.getName())
				&& tag.getStyle().getType() == style.getType()) {
				doTagsWithThisStyleExist = true;
				break;
			}
		}

		if (!doTagsWithThisStyleExist) {
			styles.removeStyle(style);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Checks if {@link TagStyle tag styles} {@link TagStyleAttributeDefinition}
	 * is properly referenced by each {@link TagAttributeValue} and
	 * - in case it is not - the reference is corrected.
	 * Explanation: each tag style contains {@link TagStyleAttributeDefinition
	 * attributes definitions} which are referenced by each tag that uses
	 * these definitions. But, after copying styles from some other tagSet
	 * (which occurs when applying a preset to a tag set) those references
	 * will be incorrect.
	 */
	protected void updateTagAttributesBinding() {
		Iterator<Tag> tagIterator = tags.iterator();
		while(tagIterator.hasNext()) {
			Tag tag = tagIterator.next();

			String styleName = tag.getStyle().getName();
			SignalSelectionType tagType = tag.getStyle().getType();
			TagStyle style = getStyle(tagType, styleName);
			tag.setStyle(style);

			for (TagAttributeValue value: tag.getAttributes().getAttributesList()) {
				TagStyleAttributeDefinition oldAttributeDefinition = value.getAttributeDefinition();
				TagStyleAttributeDefinition newAttributeDefinition = style.getAttributesDefinitions().getAttributeDefinition(oldAttributeDefinition.getCode());

				if (oldAttributeDefinition != null && newAttributeDefinition == null) {
					style.getAttributesDefinitions().addAttributeDefinition(oldAttributeDefinition);
					value.setAttributeDefinition(oldAttributeDefinition);
				}
				else {
					value.setAttributeDefinition(newAttributeDefinition);
				}
			}
		}
	}
}
