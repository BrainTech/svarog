/**
 *
 */
package org.signalml.plugin.export.signal;

import java.awt.Color;
import java.awt.Stroke;
import javax.swing.KeyStroke;


/**
 * This interface for a tag style allows to:
 * <ul>
 * <li>get the {@link ExportedSignalSelectionType type} of the selection</li>
 * <li>get the name and description of this style</li>
 * <li>get graphic style of this tag (fill and outline colour, outline width,
 * dashing pattern of the outline)</li>
 * <li>get the key associated with this style</li>
 * <li>get the information if the tag is a marker</li>
 * </ul>
 * @author Marcin Szumski
 */
public interface ExportedTagStyle {

	/**
	 * Returns the {@link ExportedSignalSelectionType type} of a
	 * {@link ExportedTag selection}.
	 * @return the type of a selection
	 */
	ExportedSignalSelectionType getType();

	/**
	 * Returns the name of this style.
	 * @return the name of this style
	 */
	String getName();

	/**
	 * Returns the description of this style.
	 * @return the description of this style
	 */
	String getDescription();

	/**
	 * Returns the description of this style or, if it doesn't exist, the name
	 * @return the description of this style or, if it doesn't exist, the name
	 */
	String getDescriptionOrName();

	/**
	 * Returns the colour of the fill of the {@link ExportedTag selection}.
	 * @return the colour of the fill of the selection
	 */
	Color getFillColor();

	/**
	 * Returns the colour of the outline of the selection
	 * @return the colour of the outline of the selection
	 */
	Color getOutlineColor();

	/**
	 * Returns the width of the outline.
	 * @return the width of the outline
	 */
	float getOutlineWidth();

	/**
	 * Returns the array representing the dashing pattern for the outline.
	 * @return the array representing the dashing pattern for the outline
	 */
	float[] getOutlineDash();

	/**
	 * Returns the stroke for the outline.
	 * @return the stroke for the outline
	 */
	Stroke getOutlineStroke();

	/**
	 * Returns the key that will be used to start creation of a
	 * {@link ExportedTag selection} of this style.
	 * @return the key that will be used to start creation of a selection of
	 * this style
	 */
	KeyStroke getKeyStroke();

	/**
	 * Returns if the {@link ExportedTag selection} is a marker.
	 * @return true if the selection is a marker, false otherwise
	 */
	boolean isMarker();

}