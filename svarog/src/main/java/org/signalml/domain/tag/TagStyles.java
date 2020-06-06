package org.signalml.domain.tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * This class contains a set of page, block and channel styles.
 * It is most often used inside of a {@link StyledTagSet} to manage
 * the {@link TagStyle tag styles} used.
 *
 * @author Piotr Szachewicz
 */
public class TagStyles {

	/**
	 * Map associating tag styles with KeyStrokes assign to them
	 */
	private HashMap<KeyStroke, TagStyle> stylesByKeyStrokes;
	/**
	 * list of styles of selections of signal pages
	 */
	private List<TagStyle> pageStyles = new ArrayList<>();
	/**
	 * list of styles of selections of signal blocks
	 */
	private List<TagStyle> blockStyles = new ArrayList<>();
	/**
	 * list of styles of custom selections of single channels
	 */
	private List<TagStyle> channelStyles = new ArrayList<>();
	/**
	 * list of listeners associated with the current object
	 */
	private EventListenerList listenerList = new EventListenerList();
	/**
	 * The {@link StyledTagSet} in which this {@link TagStyles} are used.
	 * It is necessary to inform its listeners that the styles have changed.
	 */
	private StyledTagSet styledTagSet;

	/**
	 * Constructor.
	 */
	public TagStyles() {
	}

	/**
	 * Constructor. Creates a
	 * @param styles
	 */
	public TagStyles(Collection<TagStyle> styles) {
		for (TagStyle style : styles) {
			SignalSelectionType type = style.getType();
			if (type == SignalSelectionType.PAGE) {
				pageStyles.add(style);
			} else if (type == SignalSelectionType.BLOCK) {
				blockStyles.add(style);
			} else if (type == SignalSelectionType.CHANNEL) {
				channelStyles.add(style);
			}
		}
	}

	/**
	 * Sets the {@link StyledTagSet} that uses this {@link TagStyles}.
	 * (Please note: only one tag set may use a single {@link TagStyles}
	 * object.)
	 * @param styledTagSet the {@link StyledTagSet} that uses this {@link TagStyles}
	 */
	public void setStyledTagSet(StyledTagSet styledTagSet) {
		this.styledTagSet = styledTagSet;
	}

	/**
	 * Returns the list of all {@link TagStyle tag styles} contained in this
	 * {@link TagStyles}.
	 * @return all tag styles available
	 */
	public List<TagStyle> getAllStyles() {
		ArrayList<TagStyle> allStyles = new ArrayList<>();
		allStyles.addAll(pageStyles);
		allStyles.addAll(blockStyles);
		allStyles.addAll(channelStyles);
		return allStyles;
	}

	/**
	 * Returns {@link TagStyle tag styles} of a given type.
	 *
	 * @param type {@link SignalSelectionType the type} of the styles to
	 * be returned
	 * @param allowMarkers determines whether on the returned list marker
	 * styles may be allowed
	 * @return all styles of the given type if allowMarkers is true,
	 * or all styles of the given type excluding markers if allowMarkers
	 * is false
	 */
	public List<TagStyle> getStyles(SignalSelectionType type, boolean allowMarkers) {
		List<TagStyle> styles = new ArrayList<>();

		if (allowMarkers) {
			styles = getStyles(type);
		} else {
			for (TagStyle style : getStyles(type)) {
				if (!style.isMarker()) {
					styles.add(style);
				}
			}
		}
		return styles;
	}

	/**
	 * Returns the list of styles of a given type.
	 * @param type the type of styles to be returned.
	 * @return the styles available in this {@link TagStyles}
	 */
	public List<TagStyle> getStyles(SignalSelectionType type) {
		if (type == SignalSelectionType.BLOCK) {
			return blockStyles;
		} else if (type == SignalSelectionType.CHANNEL) {
			return channelStyles;
		} else if (type == SignalSelectionType.PAGE) {
			return pageStyles;
		} else {
			return null;
		}
	}

	/**
	 * Returns the style having the given name. (If more than one styles
	 * have equal names, the first on the list is returned).
	 * @param name the name of the style
	 * @return the style having the given name
	 */
	public TagStyle getStyle(String name) {
		return getStyle(null, name);
	}

	/**
	 * Returns the style of a given type and name.
	 * @param type the {@link SignalSelectionType} of the style
	 * @param name the name of the style
	 * @return the style of the given name and type or null if no such
	 * style exists.
	 */
	public TagStyle getStyle(SignalSelectionType type, String name) {
		List<TagStyle> styles;
		if (type == null) {
			styles = getAllStyles();
		} else {
			styles = getStyles(type);
		}

		for (TagStyle style : styles) {
			if (style.getName().compareTo(name) == 0) {
				return style;
			}
		}
		return null;
	}

	/**
	 * Returns the number of all styles available.
	 * @return
	 */
	public int getStylesCount() {
		return pageStyles.size() + blockStyles.size() + channelStyles.size();
	}

	/**
	 * Returns the number of styles of the given type available in this {@link TagStyles}.
	 * @param type the type of the styles to be counted
	 * @return the number of styles of the given type
	 */
	public int getStylesCount(SignalSelectionType type) {
		return getStyles(type).size();
	}

	/**
	 * Returns the style of a given index in an array of styles of a given
	 * type.
	 * @param type the type of styles to be concerned
	 * @param index the index of the style to be returned
	 * @return the style of the given index and the given type
	 */
	public TagStyle getStyleAt(SignalSelectionType type, int index) {
		return getStyles(type).get(index);
	}

	/**
	 * Returns the index of the given style.
	 * @param style the style which index will be returned
	 * @return the index of the given style
	 */
	public int getIndexOf(TagStyle style) {
		return getStyles(style.getType()).indexOf(style);
	}

	/**
	 * Removes the given style from this {@link TagStyles}.
	 * @param style the style to be removed
	 */
	public void removeStyle(TagStyle style) {
		if (style != null) {
			int inTypeIndex = getIndexOf(style);
			getStyles(style.getType()).remove(style);
			fireTagStyleRemoved(style, inTypeIndex);
			invalidateStylesByKeystrokes();
		}
	}

	/**
	 * Updates the style of the given name to the given style.
	 * @param name the name of the style in this {@link TagStyles} to be updated
	 * @param style the style containing the new values for the style
	 * (the type of the style to be updated is also taken from this object)
	 */
	public void updateStyle(String name, TagStyle style) {

		TagStyle existingStyle = getStyle(style.getType(), name);
		if (existingStyle == null) {
			addStyle(style);
			return;
		}
		existingStyle.copyFrom(style);
		if (!style.getName().equals(name)) {
			existingStyle.setName(style.getName());
		}

		// invalidate map
		stylesByKeyStrokes = null;

		fireTagStyleChanged(existingStyle, getIndexOf(existingStyle));

	}

	/**
	 * Adds a new style to this {@link TagStyles}.
	 * @param style the style to be added
	 */
	public void addStyle(TagStyle style) {
		getStyles(style.getType()).add(style);
		KeyStroke keyStroke = style.getKeyStroke();
		if (keyStroke != null) {
			getStylesByKeyStrokes().put(keyStroke, style);
		}
		invalidateStylesByKeystrokes();
		fireTagStyleAdded(style, getIndexOf(style));
	}

	/**
	 * Returns the map associating {@link TagStyle tag styles} with
	 * KeyStrokes assigned to them.
	 * @return the map associating tag styles with KeyStrokes assign to them.
	 */
	public HashMap<KeyStroke, TagStyle> getStylesByKeyStrokes() {
		if (stylesByKeyStrokes == null) {
			stylesByKeyStrokes = new HashMap<>();
			KeyStroke keyStroke;

			for (TagStyle style : getAllStyles()) {
				keyStroke = style.getKeyStroke();
				if (keyStroke != null) {
					stylesByKeyStrokes.put(keyStroke, style);
				}
			}

		}
		return stylesByKeyStrokes;
	}

	/**
	 * Invalidates the hashmap of styles keystrokes.
	 */
	protected void invalidateStylesByKeystrokes() {
		stylesByKeyStrokes = null;
	}

	/**
	 * Adds a {@link TagStyleListener TagStyleListener} to the list
	 * of listeners.
	 * @param listener the TagStyleListener to be added
	 */
	public void addTagStyleListener(TagStyleListener listener) {
		listenerList.add(TagStyleListener.class, listener);
	}

	/**
	 * Removes a {@link TagStyleListener TagStyleListener} from the list
	 * of listeners.
	 * @param listener the TagStyleListener to be removed
	 */
	public void removeTagStyleListener(TagStyleListener listener) {
		listenerList.remove(TagStyleListener.class, listener);
	}

	/**
	 * Fires all {@link TagStyleListener tag style listeners} that a
	 * {@link TagStyle tag style} of a given index has been added.
	 * @param tagStyle the added tag style
	 * @param inTypeIndex an index in an array of tag styles of a given type
	 */
	protected void fireTagStyleAdded(TagStyle tagStyle, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		TagStyleEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TagStyleListener.class) {
				if (e == null) {
					e = new TagStyleEvent(styledTagSet, tagStyle, inTypeIndex);
				}
				((TagStyleListener) listeners[i + 1]).tagStyleAdded(e);
			}
		}
	}

	/**
	 * Fires all {@link TagStyleListener tag style listeners} that a
	 * {@link TagStyle tag style} of a given index has been removed.
	 * @param tagStyle the removed tag style
	 * @param inTypeIndex an index in an array of tag styles of a given type
	 */
	protected void fireTagStyleRemoved(TagStyle tagStyle, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		TagStyleEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TagStyleListener.class) {
				if (e == null) {
					e = new TagStyleEvent(styledTagSet, tagStyle, inTypeIndex);
				}
				((TagStyleListener) listeners[i + 1]).tagStyleRemoved(e);
			}
		}
	}

	/**
	 * Fires {@link TagStyleListener tag style listeners} that a
	 * {@link TagStyle tag style} of a given index has been changed.
	 * @param tagStyle the changed tag style
	 * @param inTypeIndex an index in an array of tag styles of a given type
	 */
	protected void fireTagStyleChanged(TagStyle tagStyle, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		TagStyleEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TagStyleListener.class) {
				if (e == null) {
					e = new TagStyleEvent(styledTagSet, tagStyle, inTypeIndex);
				}
				((TagStyleListener) listeners[i + 1]).tagStyleChanged(e);
			}
		}
	}

	@Override
	public TagStyles clone() {
		TagStyles newStyles = new TagStyles();

		newStyles.pageStyles = copyTagList(pageStyles);
		newStyles.blockStyles = copyTagList(blockStyles);
		newStyles.channelStyles = copyTagList(channelStyles);

		return newStyles;
	}

	/**
	 * Returns a list containing copies of the {@link TagStyle tag styles}
	 * from the sourceList.
	 * @param sourceList the list from which styles are to be copied.
	 * @return the list of tag styles copies
	 */
	protected List<TagStyle> copyTagList(List<TagStyle> sourceList) {
		List<TagStyle> destinationList = new ArrayList<>();
		for (TagStyle style : sourceList) {
			destinationList.add(new TagStyle(style));
		}
		return destinationList;
	}

}
