/**
 * 
 */
package org.signalml.plugin.export.signal;

/**
 * This interface allows to:
 * <ul>
 * <li>get and set basic properties of this tag, such as
 * annotation, starting position, length and the number of a channel</li>
 * <li>get information about the style of this tag and if it is a marker</li>
 * <li>compare tags</li>
 * </ul>
 * 
 * @author Marcin Szumski
 */
public interface ExportedTag extends ExportedSignalSelection, Comparable<ExportedTag> {

	/**
	 * Returns the style of this tagged selection.
	 * @return the style of this tagged selection
	 */
	ExportedTagStyle getStyle();

	/**
	 * Returns the annotation of this tagged selection.
	 * @return the annotation of this tagged selection
	 */
	String getAnnotation();

	/**
	 * Sets the annotation of this tagged selection.
	 * @param annotation the annotation to be set
	 */
	void setAnnotation(String annotation);

	/**
	 * Returns whether this tagged selection is a marker.
	 * @return true if this tagged selection is a marker, false otherwise
	 */
	boolean isMarker();

	/**
     * Compares the current object to given.
     * The comparison uses the following characteristics in turn:
     * starting position, length, the channel number,
     * and the TagStyle of this tag. The first characteristic that
     * doesn't match determines the outcome of the comparison.
     * @param t a tagged selection to be compared with the current object
     * @return &gt; 0 if the current object is greater than given;
     * &lt; 0 if current is smaller than given;
     * 0 if the selections are equal.
     */
	int compareTo(ExportedTag t);

	/**
     * @param position position where selection starts
     */
	void setPosition(double position);
	
	/**
    * @param length length of selection in seconds
    */
	void setLength(double length);
	
	/**
    * @param channel number of selected channel
    * CHANNEL_NULL when no channel is selected
    */
	void setChannel(int channel);
	
	/**
	 * @return a String in format:
	 * "NameOfAStyle : StartingPosition -> EndPosition"
	 */
	String toString();

}