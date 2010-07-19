/* TagStatistic.java created 2007-12-04
 *
 */

package org.signalml.domain.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import org.signalml.app.model.WriterExportableTable;
import org.signalml.util.Util;

/**
 * This class contains for each style the total length of tagged selections
 * of a given style
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStatistic implements WriterExportableTable {

        /**
         * Total length of all tagged selections
         */
	protected float totalLength;

        /**
         * An array of styles of tagged selections
         */
	protected TagStyle[] tagStyles;

        /**
         * Hash map associating TagStyles with their indexes in a <i>tagStyles</i>
         * array
         */
	protected HashMap<TagStyle,Integer> styleIndices;

        /**
         * An array of tagged selections lengths.
         * For each style (index in a <i>tagStyles</i> array) holds the length
         * of all tagged selections of this type
         */
	protected float[] styleTimes;

        /**
         * Constructor. Creates an empty TagStatistic
         */
	protected TagStatistic() {

	}

        /**
         * Initialises attributes using given values.
         * Creates all necessary structures.
         * @param tagStyles an array of tag styles
         * @param totalLength the total length of all tagged selections
         */
	protected void init(TagStyle[] tagStyles, float totalLength) {

		this.tagStyles = tagStyles;
		this.totalLength = totalLength;

		int i;
		styleIndices = new HashMap<TagStyle, Integer>(tagStyles.length);
		for (i=0; i<tagStyles.length; i++) {
			styleIndices.put(tagStyles[i],i);
		}

		styleTimes = new float[tagStyles.length+1];

	}

        /**
         * Constructor. Creates a TagStatistic object and initialises its
         * attributes using given values.
         * @param tagStyles an array of tag styles
         * @param totalLength the total length of all tagged selections
         */
	public TagStatistic(TagStyle[] tagStyles, float totalLength) {
		init(tagStyles, totalLength);
	}

        /**
         * Returns the total length of all tagged selections
         * @return the total length of all tagged selections
         */
	public float getTotalLength() {
		return totalLength;
	}

        /**
         * Returns the total length of all tagged selections in the form of
         * string
         * @return the total length of all tagged selections in the form of
         * string
         */
	public String getTotalLengthPretty() {
		return Util.getPrettyTimeString(totalLength);
	}

        /**
         * Returns the number of tag styles
         * @return the number of tag styles
         */
	public int getStyleCount() {
		return tagStyles.length;
	}

        /**
         * Returns the tag style of a given index
         * @param index the index of a tag style
         * @return the tag style of a given index
         */
	public TagStyle getStyleAt(int index) {
		return tagStyles[index];
	}

        /**
         * Returns the index of a given tag style
         * @param style the tag style which index is to be found
         * @return the index of a given tag style
         */
	public int indexOf(TagStyle style) {
		Integer idx = styleIndices.get(style);
		if (idx == null) {
			return -1;
		}
		return idx.intValue();
	}

        /**
         * Returns the length of tagged selections of the given style
         * @param index the index of the style
         * @return the length of tagged selections of the given style
         */
	public float getStyleTime(int index) {
		return styleTimes[index+1];
	}

        /**
         * Sets the length of tagged selections of the given style
         * @param index the index of the style
         * @param time the length of tagged selections to be set
         */
	public void setStyleTime(int index, float time) {
		styleTimes[index+1] = time;
	}

        /**
         * Adds a given value to the length of tagged selections
         * of the given style
         * @param index the index of the style
         * @param time the length of tagged selections to be added
         */
	public void addStyleTime(int index, float time) {
		styleTimes[index+1] += time;
	}

        /**
         * Returns the length of tagged selections of the given style
         * @param style the tag style
         * @return the length of tagged selections of the given style
         */
	public float getStyleTime(TagStyle style) {
		if (style == null) {
			return styleTimes[0];
		}
		Integer idx = styleIndices.get(style);
		if (idx == null) {
			return 0F;
		}
		return styleTimes[idx.intValue()+1];
	}

        /**
         * Sets the length of tagged selections of the given style
         * @param style the tag style
         * @param time the length of tagged selections to be set
         */
	public void setStyleTime(TagStyle style, float time) {
		if (style == null) {
			styleTimes[0] = time;
		}
		Integer idx = styleIndices.get(style);
		if (idx == null) {
			throw new IllegalArgumentException("No such style [" + style.toString() + "]");
		}
		styleTimes[idx.intValue()+1] = time;
	}

        /**
         * Adds a given value to the length of tagged selections
         * of the given style
         * @param style the tag style
         * @param time the length of tagged selections to be added
         */
	public void addStyleTime(TagStyle style, float time) {
		if (style == null) {
			styleTimes[0] += time;
		}
		Integer idx = styleIndices.get(style);
		if (idx == null) {
			throw new IllegalArgumentException("No such style [" + style.toString() + "]");
		}
		styleTimes[idx.intValue()+1] += time;
	}

        /**
         * Writes the current object parameters to a given {@link Writer writer}
         * using given column and row separator
         * @param writer the writer to which current object parameters will be
         * written
         * @param columnSeparator a string used to separate columns
         * @param rowSeparator a string used to separate rows
         * @param userObject not used
         * @throws IOException if an I/O error occurs
         */
	public void export(Writer writer, String columnSeparator, String rowSeparator, Object userObject) throws IOException {

		writer.append("(none)");
		writer.append(columnSeparator);
		writer.append(Float.toString(styleTimes[0]));
		writer.append(columnSeparator);
		writer.append(Float.toString(styleTimes[0]/totalLength));
		writer.append(rowSeparator);

		for (int i=0; i<tagStyles.length; i++) {

			writer.append(tagStyles[i].getName());
			writer.append(columnSeparator);
			writer.append(Float.toString(styleTimes[i+1]));
			writer.append(columnSeparator);
			if (totalLength == 0) {
				writer.append("-");
			} else {
				writer.append(Float.toString(100*(styleTimes[i+1]/totalLength)));
			}
			writer.append(rowSeparator);

		}

	}

}
