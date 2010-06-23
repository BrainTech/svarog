/* StyledTagSet.java created 2007-09-28
 * 
 */

package org.signalml.domain.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.exception.SanityCheckException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/** StyledTagSet
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("annotations")
@XStreamConverter(StyledTagSetConverter.class)
public class StyledTagSet implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(StyledTagSet.class);
	
	private static final TagStyleNameComparator tagStyleNameComparator = new TagStyleNameComparator();

	private float pageSize;
	private int blocksPerPage;
	private float blockSize;
	
	private TagSignalIdentification tagSignalIdentification;
			
	private LinkedHashMap<String,TagStyle> styles;
	private HashMap<KeyStroke,TagStyle> stylesByKeyStrokes;
	
	private ArrayList<TagStyle> pageStylesCache = null;
	private ArrayList<TagStyle> blockStylesCache = null;
	private ArrayList<TagStyle> channelStylesCache = null;
	
	private TreeSet<Tag> tags;

	// this is just an estimate - may be 10% more than the actual length of the longest tag in the set
	private float maxTagLength = 0;
		
	private ArrayList<Tag> pageTagsCache = null;
	private ArrayList<Tag> blockTagsCache = null;
	private ArrayList<Tag> channelTagsCache = null;
	
	private String info;
	private String montageInfo;
	
	private Montage montage;
	
	private EventListenerList listenerList = new EventListenerList();

	public StyledTagSet() {
		this( null, null, SignalParameterDescriptor.DEFAULT_PAGE_SIZE, SignalParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE );
	}
	
	public StyledTagSet(float pageSize, int blocksPerPage) {
		this( null, null, pageSize, blocksPerPage );
	}
	
	public StyledTagSet(LinkedHashMap<String,TagStyle> styles) {
		this( styles, null, SignalParameterDescriptor.DEFAULT_PAGE_SIZE, SignalParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE );
	}

	public StyledTagSet(LinkedHashMap<String,TagStyle> styles, float pageSize, int blocksPerPage) {
		this( styles, null, pageSize, blocksPerPage );
	}
	
	public StyledTagSet(LinkedHashMap<String,TagStyle> styles, TreeSet<Tag> tags) {
		this( styles, tags, SignalParameterDescriptor.DEFAULT_PAGE_SIZE, SignalParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE );
	}

	public StyledTagSet(LinkedHashMap<String,TagStyle> styles, TreeSet<Tag> tags, float pageSize, int blocksPerPage) {
		if( pageSize <= 0 ) {
			throw new SanityCheckException( "Page size must be > 0" );
		}
		if( blocksPerPage <= 0 ) {
			throw new SanityCheckException( "Blocks per page must be > 0" ); // free-style block tags disabled for now			
		}
		if( styles == null ) {
			this.styles = new LinkedHashMap<String,TagStyle>();
		} else {
			this.styles = styles;
		}
		if( tags == null ) {
			this.tags = new TreeSet<Tag>();
		} else {
			this.tags = tags;
		}
		this.pageSize = pageSize;
		this.blocksPerPage = blocksPerPage;
		if( blocksPerPage > 0 ) {
			blockSize = pageSize / blocksPerPage;
		} else {
			blockSize = -1;
		}
		if( !verifyTags() ) {
			throw new SanityCheckException( "Tags not compatible with settings" );
		}
		calculateMaxTagLength();
	}
	
	public float getPageSize() {
		return pageSize;
	}

	public int getBlocksPerPage() {
		return blocksPerPage;
	}
	
	public float getBlockSize() {
		return blockSize;
	}
	
	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		this.montage = montage;
	}

	public LinkedHashSet<TagStyle> getStyles() {
		return new LinkedHashSet<TagStyle>( styles.values() );
	}

	public LinkedHashSet<TagStyle> getStyles(SignalSelectionType type) {
		LinkedHashSet<TagStyle> set = new LinkedHashSet<TagStyle>();
		for( TagStyle ts : styles.values() ) {
			if( ts.getType() == type ) {
				set.add(ts);
			}
		}
		return set;
	}

	public LinkedHashSet<TagStyle> getStyles(SignalSelectionType type, boolean allowMarkers) {
		LinkedHashSet<TagStyle> set = new LinkedHashSet<TagStyle>();
		for( TagStyle ts : styles.values() ) {
			if( ts.getType() == type && (allowMarkers || !ts.isMarker()) ) {
				set.add(ts);
			}
		}
		return set;
	}
	
	public LinkedHashSet<TagStyle> getPageStyles() {
		return getStyles(SignalSelectionType.PAGE);
	}

	public LinkedHashSet<TagStyle> getBlockStyles() {
		return getStyles(SignalSelectionType.BLOCK);
	}

	public LinkedHashSet<TagStyle> getChannelStyles() {
		return getStyles(SignalSelectionType.CHANNEL);
	}

	public LinkedHashSet<TagStyle> getPageStylesNoMarkers() {
		return getStyles(SignalSelectionType.PAGE, false);
	}

	public LinkedHashSet<TagStyle> getBlockStylesNoMarkers() {
		return getStyles(SignalSelectionType.BLOCK, false);
	}

	public LinkedHashSet<TagStyle> getChannelStylesNoMarkers() {
		return getStyles(SignalSelectionType.CHANNEL, false);
	}
	
	public TagStyle getStyle(String name) {
		return styles.get(name);		
	}

	public TagStyle getStyleOrDefault(String name) {
		TagStyle ts = styles.get(name);
		if( ts == null ) {
			ts = TagStyle.getDefault();
		}
		return ts;
	}
	
	public int getTagStyleCount() {
		return styles.size();
	}
	
	public int getTagStyleCount( SignalSelectionType type ) {
		if( type == SignalSelectionType.PAGE ) {
			return getPageStyleCount();
		}
		else if( type == SignalSelectionType.BLOCK ) {
			return getBlockStyleCount();
		}
		else if( type == SignalSelectionType.CHANNEL ) {
			return getChannelStyleCount();
		} else {
			return 0;
		}		
	}
	
	public int getPageStyleCount() {
		if( pageStylesCache == null ) {
			makeStyleCache();			
		}
		return pageStylesCache.size();
	}

	public int getBlockStyleCount() {
		if( blockStylesCache == null ) {
			makeStyleCache();			
		}
		return blockStylesCache.size();
	}
	
	public int getChannelStyleCount() {
		if( channelStylesCache == null ) {
			makeStyleCache();			
		}
		return channelStylesCache.size();
	}
	
	public TagStyle getStyleAt( SignalSelectionType type, int index ) {
		if( type == SignalSelectionType.PAGE ) {
			return getPageStyleAt(index);
		}
		else if( type == SignalSelectionType.BLOCK ) {
			return getBlockStyleAt(index);
		}
		else if( type == SignalSelectionType.CHANNEL ) {
			return getChannelStyleAt(index);
		} else {
			return null;
		}			
	}
	
	public TagStyle getPageStyleAt( int index ) {
		if( pageStylesCache == null ) {
			makeStyleCache();			
		}
		return pageStylesCache.get(index);
	}

	public TagStyle getBlockStyleAt( int index ) {
		if( blockStylesCache == null ) {
			makeStyleCache();			
		}
		return blockStylesCache.get(index);
	}
	
	public TagStyle getChannelStyleAt( int index ) {
		if( channelStylesCache == null ) {
			makeStyleCache();			
		}
		return channelStylesCache.get(index);
	}
	
	public int indexOfStyle( TagStyle style ) {
		SignalSelectionType type = style.getType();
		if( type == SignalSelectionType.PAGE ) {
			return indexOfPageStyle(style);
		}
		else if( type == SignalSelectionType.BLOCK ) {
			return indexOfBlockStyle(style);
		}
		else if( type == SignalSelectionType.CHANNEL ) {
			return indexOfChannelStyle(style);
		} else {
			return -1;
		}				
	}
	
	public int indexOfPageStyle( TagStyle style ) {
		if( pageStylesCache == null ) {
			makeStyleCache();			
		}
		return pageStylesCache.indexOf(style);
	}

	public int indexOfBlockStyle( TagStyle style ) {
		if( blockStylesCache == null ) {
			makeStyleCache();			
		}
		return blockStylesCache.indexOf(style);
	}

	public int indexOfChannelStyle( TagStyle style ) {
		if( channelStylesCache == null ) {
			makeStyleCache();			
		}
		return channelStylesCache.indexOf(style);
	}
	
	public SortedSet<Tag> getTags() {
		return tags;
	}
	
	// Always remember that this method returns tags that MAY be between these two
	// positions. It doesn't mean they actually ARE (!!!), you always need to verify this.
	// The only guarantee being made is that ALL the tags that MAY be in this region
	// are returned.
	//
	// this set is inclusive at both ends!	
	public SortedSet<Tag> getTagsBetween( float start, float end ) {
		Tag startMarker = new Tag(null, start-maxTagLength, 0);
		Tag endMarker = new Tag(null,end,Float.MAX_VALUE); // note that lengths matter, so that all tags starting at exactly end will be selected 
		return tags.subSet(startMarker, true, endMarker, true);
	}
	
	public int getTagCount() {
		return tags.size();
	}

	public int getTagCount( SignalSelectionType type ) {
		if( type == SignalSelectionType.PAGE ) {
			return getPageTagCount();
		}
		else if( type == SignalSelectionType.BLOCK ) {
			return getBlockTagCount();
		}
		else if( type == SignalSelectionType.CHANNEL ) {
			return getChannelTagCount();
		} else {
			return 0;
		}
	}
	
	public int getPageTagCount() {
		if( pageTagsCache == null ) {
			makeTagCache();
		}
		return pageTagsCache.size();
	}

	public int getBlockTagCount() {
		if( blockTagsCache == null ) {
			makeTagCache();
		}
		return blockTagsCache.size();
	}
	
	public int getChannelTagCount() {
		if( channelTagsCache == null ) {
			makeTagCache();
		}
		return channelTagsCache.size();
	}
	
	public Tag getTagAt( SignalSelectionType type, int index ) {
		if( type == SignalSelectionType.PAGE ) {
			return getPageTagAt(index);
		}
		else if( type == SignalSelectionType.BLOCK ) {
			return getBlockTagAt(index);
		}
		else if( type == SignalSelectionType.CHANNEL ) {
			return getChannelTagAt(index);
		} else {
			return null;
		}
	}
	
	public Tag getPageTagAt( int index ) {
		if( pageTagsCache == null ) {
			makeTagCache();
		}
		return pageTagsCache.get(index);
	}
	
	public Tag getBlockTagAt( int index ) {
		if( blockTagsCache == null ) {
			makeTagCache();
		}
		return blockTagsCache.get(index);
	}
	
	public Tag getChannelTagAt( int index ) {
		if( channelTagsCache == null ) {
			makeTagCache();
		}
		return channelTagsCache.get(index);
	}
	
	public int indexOfTag(Tag tag) {
		SignalSelectionType type = tag.getType();
		if( type == SignalSelectionType.PAGE ) {
			return indexOfPageTag(tag);
		}
		else if( type == SignalSelectionType.BLOCK ) {
			return indexOfBlockTag(tag);
		}
		else if( type == SignalSelectionType.CHANNEL ) {
			return indexOfChannelTag(tag);
		} else {
			return -1;
		}						
	}
	
	public int indexOfPageTag(Tag tag) {
		if( pageTagsCache == null ) {
			makeTagCache();
		}
		return pageTagsCache.indexOf(tag);
	}

	public int indexOfBlockTag(Tag tag) {
		if( blockTagsCache == null ) {
			makeTagCache();
		}
		return blockTagsCache.indexOf(tag);
	}
	
	public int indexOfChannelTag(Tag tag) {
		if( channelTagsCache == null ) {
			makeTagCache();
		}
		return channelTagsCache.indexOf(tag);
	}
	
	public boolean verifyTags() {
		if( pageTagsCache == null || blockTagsCache == null ) {
			makeTagCache();
		}		
		if( blockSize > 0 ) {
			for( Tag tag : blockTagsCache ) {
				if( tag.getLength() != blockSize ) {
					logger.debug( "Tag block size is [" + tag.getLength() + "] should be [" + blockSize + "]" );
					return false;
				}
			}
		}
		for( Tag tag : pageTagsCache ) {
			if( tag.getLength() != pageSize ) {
				logger.debug( "Tag page size is [" + tag.getLength() + "] should be [" + pageSize + "]" );
				return false;
			}
		}
		return true;
	}

	public boolean verifyTag( Tag tag ) {
		SignalSelectionType type = tag.getType();
		if( type.isBlock() ) { 
			if( tag.getLength() != blockSize ) {
				logger.debug( "Tag block size is [" + tag.getLength() + "] should be [" + blockSize + "]" );
				return false;
			}
		} else if( type.isPage() ) {
			if( tag.getLength() != pageSize ) {
				logger.debug( "Tag page size is [" + tag.getLength() + "] should be [" + pageSize + "]" );
				return false;
			}
		}
		return true;
	}
		
	public void addStyle( TagStyle style ) {
		styles.put(style.getName(), style);
		KeyStroke keyStroke = style.getKeyStroke();
		if( keyStroke != null ) {
			getStylesByKeyStrokes().put( keyStroke, style );
		}
		invalidateStyleCache( style.getType() );
		fireTagStyleAdded(style, indexOfStyle(style));
	}
	
	public void removeStyle( String name ) {
		TagStyle style = styles.get(name);
		if( style != null ) {
			int inTypeIndex = indexOfStyle(style);
			styles.remove( name );
			// invalidate map
			stylesByKeyStrokes = null;
			invalidateStyleCache( style.getType() );
			fireTagStyleRemoved(style,inTypeIndex);
		}
	}

	public void updateStyle( String name, TagStyle style ) {
		
		TagStyle existingStyle = styles.get(name);
		if( existingStyle == null ) {
			addStyle(style);
			return;
		}
		if( style.getType() != existingStyle.getType() ) {
			removeStyle(name);
			addStyle(style);
			return;
		}
		existingStyle.copyFrom( style );
		if( !style.getName().equals( name ) ) {
			styles.remove(name);
			styles.put(style.getName(), existingStyle);
			invalidateStyleCache(existingStyle.getType());
		}

		// invalidate map
		stylesByKeyStrokes = null;
		
		fireTagStyleChanged(existingStyle, indexOfStyle(existingStyle));
		
	}
	
	public boolean hasTagsWithStyle( String name ) {
		for( Tag tag : tags ) {
			if( name.equals( tag.getStyle().getName() ) ) {
				return true;
			}
		}
		return false;
	}
	
	public void addTag( Tag tag ) {
		if( !verifyTag( tag ) ) {
			throw new SanityCheckException( "Tag not compatible" );
		}
		tags.add(tag);
		invalidateTagCache( tag.getStyle().getType() );
		if( maxTagLength < tag.getLength() ) {
			maxTagLength = tag.getLength();
		}
		fireTagAdded(tag);
	}
	
	public void eraseTags( SignalSelection selection ) {
		
		// erase same type tags from selection
		
		float selStart = selection.getPosition();
		float selEnd = selStart + selection.getLength();
		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd); 
		float confStart;
		float confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();
		SignalSelectionType type = selection.getType();
		boolean calculateLength = false;
		while( it.hasNext() ) {
			confTag = it.next();
			if( confTag.getType() == type ) {
				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if( (confStart < selEnd) && (confEnd > selStart) && (confTag.getChannel() == selection.getChannel()) ) {
					it.remove();
					invalidateTagCache( type );
					if( confTag.getLength() > (maxTagLength * 0.75) ) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);
				}
			}
		}
		
		if( calculateLength ) {
			calculateMaxTagLength();				
		}
				
	}
	
	public void replaceSameTypeTags( Tag tag ) {
		
		// remove conflicting tags
		
		float selStart = tag.getPosition();
		float selEnd = selStart + tag.getLength();
		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd); 
		TagStyle confStyle;
		float confStart;
		float confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();
		SignalSelectionType type = tag.getStyle().getType();
		boolean calculateLength = false;
		while( it.hasNext() ) {
			confTag = it.next();
			confStyle = confTag.getStyle();
			if( confStyle.getType() == type ) {
				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if( (confStart < selEnd) && (confEnd > selStart) && (confTag.getChannel() == tag.getChannel()) ) {
					it.remove();
					invalidateTagCache( confStyle.getType() );
					if( confTag.getLength() > (maxTagLength * 0.75) ) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);
				}
			}
		}
		
		if( calculateLength ) {
			calculateMaxTagLength();				
		}
		
		addTag(tag);
		
	}

	public void splitAndMergeSameTypeTags( Tag tag ) {
		
		// split conflicting tags while merging same type tags
		
		float selStart = tag.getPosition();
		float selEnd = selStart + tag.getLength();
		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd); 
		TagStyle confStyle;
		float confStart;
		float confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();
		SignalSelectionType type = tag.getStyle().getType();
		boolean calculateLength = false;
		
		LinkedList<Tag> addedTags = new LinkedList<Tag>();
		Tag addedTag;
		
		float newSelStart = selStart;
		float newSelEnd = selEnd;
		
		while( it.hasNext() ) {
			confTag = it.next();
			confStyle = confTag.getStyle();
			if( confStyle.getType() == type ) {
				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if( (confStart <= selEnd) && (confEnd >= selStart) && (confTag.getChannel() == tag.getChannel()) ) {
					
					if( confTag.getStyle() == tag.getStyle() ) { // same type, merge
					
						if( confStart < newSelStart ) {
							newSelStart = confStart;
						}
						if( confEnd > newSelEnd ) {
							newSelEnd = confEnd;
						}					
												
					} else { // different type, split & replace

						if( (confStart < selEnd) && (confEnd > selStart) ) { 
						
							if( confStart < selStart ) { // if the conflicting tag partially precedes this tag
								addedTag = confTag.clone();
								addedTag.setLength(selStart-confStart);
								addedTags.add(addedTag);
							}
							if( confEnd > selEnd ) { // if the conflicting tag partially follows this tag
								addedTag = confTag.clone();
								addedTag.setParameters(selEnd,confEnd-selEnd);
								addedTags.add(addedTag);						
							}
							
						} else {
							continue; // don't change adjacent tags
						}
						
					}
					
					it.remove();
					invalidateTagCache( confStyle.getType() );
					if( confTag.getLength() > (maxTagLength * 0.75) ) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);
					
				}
			}
		}
		
		tag.setParameters(newSelStart, newSelEnd-newSelStart);
		
		if( calculateLength ) {
			calculateMaxTagLength();				
		}
		
		for( Tag t : addedTags ) {
			addTag(t);
		}
		addTag(tag);
		
	}
	
	public void mergeSameTypeChannelTags( Tag tag ) {
		
		// merge adjacent channel tags
		TagStyle style = tag.getStyle();
		SignalSelectionType type = style.getType();
		if( type != SignalSelectionType.CHANNEL ) {
			addTag(tag);
			return;
		}
		
		if( style.isMarker() ) {
			addTag(tag);
			return;
		}
		
		float selStart = tag.getPosition();
		float selEnd = selStart + tag.getLength();
		
		float newSelStart = selStart;
		float newSelEnd = selEnd;
		
		SortedSet<Tag> conflicts = getTagsBetween(selStart, selEnd); 
		TagStyle confStyle;
		float confStart;
		float confEnd;
		Tag confTag;
		Iterator<Tag> it = conflicts.iterator();
		
		boolean calculateLength = false;
		while( it.hasNext() ) {
			confTag = it.next();
			confStyle = confTag.getStyle();
			if( confStyle.isMarker() ) {
				continue;
			}
			if( confStyle.getType() == SignalSelectionType.CHANNEL ) {

				// border condition needs recheck due to inclusivness difference
				confStart = confTag.getPosition();
				confEnd = confStart + confTag.getLength();
				if( (confTag.getStyle() == tag.getStyle()) && (confStart <= selEnd) && (confEnd >= selStart) && (confTag.getChannel() == tag.getChannel()) ) {
					it.remove();
					invalidateTagCache( confStyle.getType() );
					if( confTag.getLength() > (maxTagLength * 0.75) ) {
						calculateLength = true;
					}
					fireTagRemoved(confTag);
					
					if( confStart < newSelStart ) {
						newSelStart = confStart;
					}
					if( confEnd > newSelEnd ) {
						newSelEnd = confEnd;
					}					
					
				}
			}
		}
		
		tag.setParameters(newSelStart, newSelEnd-newSelStart);
		
		if( calculateLength ) {
			calculateMaxTagLength();				
		}
		
		addTag(tag);
		
	}
	
	public void removeTag( Tag tag ) {
		boolean removed = tags.remove(tag);
		if( removed ) {
			invalidateTagCache( tag.getStyle().getType() );
			if( tag.getLength() > (maxTagLength * 0.75) ) {
				calculateMaxTagLength();				
			}
			fireTagRemoved(tag);
		}		
	}
	
	public void updateTag( Tag oldTag, Tag tag ) {		
		if( !verifyTag( tag ) ) {
			throw new SanityCheckException( "Tag not compatible" );
		}
		boolean removed = tags.remove(oldTag);
		if( removed ) {
			invalidateTagCache( tag.getStyle().getType() );
			if( tag.getLength() != oldTag.getLength() && oldTag.getLength() > (maxTagLength * 0.9) ) {
				calculateMaxTagLength();				
			}			
		}
		tags.add(tag);
		invalidateTagCache( tag.getStyle().getType() );
		if( maxTagLength < tag.getLength() ) {
			maxTagLength = tag.getLength();
		}
		fireTagChanged(tag, oldTag);
	}	

	public void editTag( Tag tag ) {		
		if( !verifyTag( tag ) ) {
			throw new SanityCheckException( "Tag not compatible" );
		}
		invalidateTagCache( tag.getStyle().getType() );
		fireTagChanged(tag, tag);
	}
		
	public HashMap<KeyStroke, TagStyle> getStylesByKeyStrokes() {
		if( stylesByKeyStrokes == null ) {
			stylesByKeyStrokes = new HashMap<KeyStroke, TagStyle>();
			KeyStroke keyStroke;
			
			for( TagStyle style : styles.values() ) {
				keyStroke = style.getKeyStroke();
				if( keyStroke != null ) {
					stylesByKeyStrokes.put( keyStroke, style );
				}
			}
			
		}
		return stylesByKeyStrokes;
	}
	
	public TagStyle getStyleByKeyStroke( KeyStroke keyStroke ) {
		return getStylesByKeyStrokes().get( keyStroke );
	}

	public TagSignalIdentification getTagSignalIdentification() {
		return tagSignalIdentification;
	}

	public void setTagSignalIdentification(TagSignalIdentification tagSignalIdentification) {
		this.tagSignalIdentification = tagSignalIdentification;
	}
		
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}	
	
	public String getMontageInfo() {
		return montageInfo;
	}

	public void setMontageInfo(String montageInfo) {
		this.montageInfo = montageInfo;
	}

	public float getMaxTagLength() {
		return maxTagLength;
	}

	public void addTagListener(TagListener listener) {
		listenerList.add(TagListener.class, listener);
	}

	public void removeTagListener(TagListener listener) {
		listenerList.remove(TagListener.class, listener);
	}

	public void addTagStyleListener(TagStyleListener listener) {
		listenerList.add(TagStyleListener.class, listener);
	}

	public void removeTagStyleListener(TagStyleListener listener) {
		listenerList.remove(TagStyleListener.class, listener);
	}
	
	protected void fireTagStyleAdded(TagStyle tagStyle, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		TagStyleEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TagStyleListener.class) {
				 if( e == null ) { 
					 e = new TagStyleEvent(this,tagStyle,inTypeIndex);
				 }
				 ((TagStyleListener)listeners[i+1]).tagStyleAdded(e);
			 }
		 }
	}
	
	protected void fireTagStyleRemoved(TagStyle tagStyle, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		TagStyleEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TagStyleListener.class) {
				 if( e == null ) { 
					 e = new TagStyleEvent(this,tagStyle,inTypeIndex);
				 }
				 ((TagStyleListener)listeners[i+1]).tagStyleRemoved(e);
			 }
		 }
	}
	
	protected void fireTagStyleChanged(TagStyle tagStyle,int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		TagStyleEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TagStyleListener.class) {
				 if( e == null ) { 
					 e = new TagStyleEvent(this,tagStyle,inTypeIndex);
				 }
				 ((TagStyleListener)listeners[i+1]).tagStyleChanged(e);
			 }
		 }
	}
	
	protected void fireTagAdded(Tag tag) {
		Object[] listeners = listenerList.getListenerList();
		TagEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TagListener.class) {
				 if( e == null ) { 
					 e = new TagEvent(this,tag,tag.getPosition(),tag.getPosition()+tag.getLength());
				 }
				 ((TagListener)listeners[i+1]).tagAdded(e);
			 }
		 }
	}

	protected void fireTagRemoved(Tag tag) {
		Object[] listeners = listenerList.getListenerList();
		TagEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TagListener.class) {
				 if( e == null ) { 
					 e = new TagEvent(this,tag,tag.getPosition(),tag.getPosition()+tag.getLength());
				 }
				 ((TagListener)listeners[i+1]).tagRemoved(e);
			 }
		 }
	}
	
	protected void fireTagChanged(Tag tag, Tag oldTag) {
		Object[] listeners = listenerList.getListenerList();
		TagEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==TagListener.class) {
				 if( e == null ) {
					 float start;
					 float end;
					 if( oldTag != null ) {
						 start = Math.min( tag.getPosition(), oldTag.getPosition() );
						 end = Math.max( tag.getPosition()+tag.getLength(), oldTag.getPosition()+oldTag.getLength());
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
	
	
	private void invalidateStyleCache( SignalSelectionType type ) {
		if( type == SignalSelectionType.PAGE ) {
			pageStylesCache = null;
		} else if( type == SignalSelectionType.BLOCK ) {
			blockStylesCache = null;
		} else if( type == SignalSelectionType.CHANNEL ) {
			channelStylesCache = null;
		}							
	}
	
	private void makeStyleCache() {
		
		if( pageStylesCache != null && blockStylesCache != null && channelStylesCache != null ) {
			return;
		}
		
		ArrayList<TagStyle> newPageStyles = null;
		ArrayList<TagStyle> newBlockStyles = null;
		ArrayList<TagStyle> newChannelStyles = null;
		
		if( pageStylesCache == null ) {
			newPageStyles = new ArrayList<TagStyle>();
		}
		if( blockStylesCache == null ) {
			newBlockStyles = new ArrayList<TagStyle>();
		}
		if( channelStylesCache == null ) {
			newChannelStyles = new ArrayList<TagStyle>();
		}
		
		SignalSelectionType type;
		for( TagStyle style : styles.values() ) {
			type = style.getType();
			if( type == SignalSelectionType.PAGE && pageStylesCache == null ) {
				newPageStyles.add(style);
			} else if( type == SignalSelectionType.BLOCK && blockStylesCache == null ) {
				newBlockStyles.add(style);
			} else if( type == SignalSelectionType.CHANNEL && channelStylesCache == null ) {
				newChannelStyles.add(style);
			}			
		}
		
		if( pageStylesCache == null ) {
			pageStylesCache = newPageStyles;
			Collections.sort(pageStylesCache,tagStyleNameComparator);
		}
		if( blockStylesCache == null ) {
			blockStylesCache = newBlockStyles;
			Collections.sort(blockStylesCache,tagStyleNameComparator);
		}
		if( channelStylesCache == null ) {
			channelStylesCache = newChannelStyles;
			Collections.sort(channelStylesCache,tagStyleNameComparator);
		}
				
	}
	
	private void invalidateTagCache( SignalSelectionType type ) {
		if( type == SignalSelectionType.PAGE ) {
			pageTagsCache = null;
		} else if( type == SignalSelectionType.BLOCK ) {
			blockTagsCache = null;
		} else if( type == SignalSelectionType.CHANNEL ) {
			channelTagsCache = null;
		}							
	}

	private void makeTagCache() {
		
		if( pageTagsCache != null && blockTagsCache != null && channelTagsCache != null ) {
			return;
		}
		
		ArrayList<Tag> newPageTags = null;
		ArrayList<Tag> newBlockTags = null;
		ArrayList<Tag> newChannelTags = null;
		
		if( pageTagsCache == null ) {
			newPageTags = new ArrayList<Tag>();
		}
		if( blockTagsCache == null ) {
			newBlockTags = new ArrayList<Tag>();
		}
		if( channelTagsCache == null ) {
			newChannelTags = new ArrayList<Tag>();
		}
		
		TagStyle style;
		SignalSelectionType type;
		
		for( Tag tag : tags ) {
			style = tag.getStyle();
			if( style != null ) {
				type = style.getType();
				if( type == SignalSelectionType.PAGE && pageTagsCache == null ) {
					newPageTags.add(tag);
				} else if( type == SignalSelectionType.BLOCK && blockTagsCache == null ) {
					newBlockTags.add(tag);
				} else if( type == SignalSelectionType.CHANNEL && channelTagsCache == null ) {
					newChannelTags.add(tag);
				}
			}
		}
		
		if( pageTagsCache == null ) {
			pageTagsCache = newPageTags;
		}
		if( blockTagsCache == null ) {
			blockTagsCache = newBlockTags;
		}
		if( channelTagsCache == null ) {
			channelTagsCache = newChannelTags;
		}
				
	}
	
	private void calculateMaxTagLength() {
		float maxTagLength = 0;
		for( Tag tag : tags ) {
			if( maxTagLength < tag.getLength() ) {
				maxTagLength = tag.getLength();
			}
		}
		this.maxTagLength = maxTagLength;
	}
	
}
