/* TagStatistic.java created 2007-12-04
 *
 */

package org.signalml.domain.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import org.signalml.app.model.components.WriterExportableTable;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.Util;

/**
 * This class contains for each {@link TagStyle style} the total length of
 * {@link Tag tagged selections} of a this style.
 * Allows to export this statistics to a Writer.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStatistic implements WriterExportableTable {

        /**
         * total length of all {@link Tag tagged selections}
         */
	protected double totalLength;

        /**
         * an array of {@link TagStyle styles} of {@link Tag tagged selections}
         */
	protected TagStyle[] tagStyles;

        /**
         * Hash map associating {@link TagStyle styles} with their indexes
         * in a {@link #tagStyles <i>tagStyles</i> array}
         */
	protected HashMap<TagStyle,Integer> styleIndices;

        /**
         * An array of {@link Tag tagged selections} lengths.
         * For each {@link TagStyle style} (index in a
         * {@link #tagStyles <i>tagStyles</i> array}) holds the length
         * of all tagged selections of this type.
         */
	protected double[] styleTimes;

        /**
         * Constructor. Creates an empty TagStatistic.
         */
	protected TagStatistic() {

	}

        /**
         * Initialises attributes using given values.
         * Creates all necessary structures.
         * @param tagStyles an array of {@link TagStyle tag styles}
         * @param totalLength the total length of all
         * {@link Tag tagged selections}.
         */
	protected void init(TagStyle[] tagStyles, double totalLength) {

		this.tagStyles = tagStyles;
		this.totalLength = totalLength;

		int i;
		styleIndices = new HashMap<TagStyle, Integer>(tagStyles.length);
		for (i=0; i<tagStyles.length; i++) {
			styleIndices.put(tagStyles[i],i);
		}

		styleTimes = new double[tagStyles.length+1];

	}

        /**
         * Constructor. Creates a TagStatistic object and initialises its
         * attributes using given values.
         * @param tagStyles an array of {@link TagStyle tag styles}
         * @param topTotalLength the total length of all
         * {@link Tag tagged selections}.
         */
	public TagStatistic(TagStyle[] tagStyles, double topTotalLength) {
		init(tagStyles, topTotalLength);
	}

        /**
         * Returns the total length of all {@link Tag tagged selections}.
         * @return the total length of all tagged selections
         */
	public double getTotalLength() {
		return totalLength;
	}

        /**
         * Returns the total length of all {@link Tag tagged selections} in
         * the form of strings.
         * @return the total length of all tagged selections in the form of
         * string
         */
	public String getTotalLengthPretty() {
		return Util.getPrettyTimeString(totalLength);
	}

        /**
         * Returns the number of {@link TagStyle tag styles}.
         * @return the number of tag styles
         */
	public int getStyleCount() {
		return tagStyles.length;
	}

        /**
         * Returns the {@link TagStyle tag style} of a given index in
         * the {@link #tagStyles tagStyles array}.
         * @param index the index of a tag style
         * @return the tag style of a given index
         */
	public TagStyle getStyleAt(int index) {
		return tagStyles[index];
	}

        /**
         * Returns the index of a given {@link TagStyle tag style}.
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
         * Returns the length of {@link Tag tagged selections} of the
         * given {@link TagStyle style}.
         * @param index the index of the style
         * @return the length of tagged selections of the given style
         */
	public double getStyleTime(int index) {
		return styleTimes[index+1];
	}

        /**
         * Sets the length of {@link Tag tagged selections} of the
         * given {@link TagStyle style}.
         * @param index the index of the style
         * @param time the length of tagged selections to be set
         */
	public void setStyleTime(int index, double time) {
		styleTimes[index+1] = time;
	}

        /**
         * Adds a given value to the length of {@link Tag tagged selections}
         * of the given {@link TagStyle style}.
         * @param index the index of the style
         * @param time the length of tagged selections to be added
         */
	public void addStyleTime(int index, double time) {
		styleTimes[index+1] += time;
	}

        /**
         * Returns the length of {@link Tag tagged selections} of the
         * given {@link TagStyle style}
         * @param style the tag style
         * @return the length of tagged selections of the given style
         */
	public double getStyleTime(TagStyle style) {
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
         * Sets the length of {@link Tag tagged selections} of the
         * given {@link TagStyle style}.
         * @param style the tag style
         * @param time the length of tagged selections to be set
         */
	public void setStyleTime(TagStyle style, double time) {
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
         * Adds a given value to the length of {@link Tag tagged selections}
         * of the* given {@link TagStyle style}.
         * of the given style
         * @param style the tag style
         * @param time the length of tagged selections to be added
         */
	public void addStyleTime(TagStyle style, double time) {
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
         * Writes parameters of this statistic to a given {@link Writer writer}
         * using given column and row separator.
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
		writer.append(Double.toString(styleTimes[0]));
		writer.append(columnSeparator);
		writer.append(Double.toString(styleTimes[0]/totalLength));
		writer.append(rowSeparator);

		for (int i=0; i<tagStyles.length; i++) {

			writer.append(tagStyles[i].getName());
			writer.append(columnSeparator);
			writer.append(Double.toString(styleTimes[i+1]));
			writer.append(columnSeparator);
			if (totalLength == 0) {
				writer.append("-");
			} else {
				writer.append(Double.toString(100*(styleTimes[i+1]/totalLength)));
			}
			writer.append(rowSeparator);

		}

	}

}
