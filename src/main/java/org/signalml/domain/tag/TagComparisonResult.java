/* TagComparisonResult.java created 2007-11-13
 *
 */

package org.signalml.domain.tag;

import java.io.IOException;
import java.io.Writer;

import org.signalml.app.model.WriterExportableTable;

/**
 * This class represents the result of comparison between two sets of tags
 * of the same type.
 * Contains {@link TagStatistic statistics} for bottom and top tags and
 * an array with lengths of common parts of tags of a specified style.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonResult implements WriterExportableTable {

        /**
         * The statistics of top tags
         */
	private TagStatistic topStatistic;

        /**
         * The statistics of bottom tags
         */
	private TagStatistic bottomStatistic;

        /**
         * A 2D array with lengths of common parts of tags of a specified style.
         * First coordinate represents top tags, second - bottom tags.
         * e.g.
         * arr[x+1][y+1] -> length of common part of top tags of style of index
         * <i>x</i> and bottom tags of style of index <i>y</i>
         * arr[x][0] -> length of a part of top tags of style of index
         * <i>x</i> that is not tagged in bottom tags
         * arr[0][y] -> length of a part of bottom tags of style of index
         * <i>y</i> that is not tagged in top tags
         */
	private float[][] styleOverlayMatrix;

        /**
         * Constructor. Creates a result of tag comparison
         * @param topTagStyles first set of tag styles (top)
         * @param bottomTagStyles second set of tag styles (bottom)
         * @param topTotalLength total length (in seconds) of top tags
         * @param bottomTotalLength total length (in seconds) of bottom tags
         */
	public TagComparisonResult(TagStyle[] topTagStyles, TagStyle[] bottomTagStyles, float topTotalLength, float bottomTotalLength) {

		topStatistic = new TagStatistic(topTagStyles, topTotalLength);
		bottomStatistic = new TagStatistic(bottomTagStyles, bottomTotalLength);

		styleOverlayMatrix = new float[topTagStyles.length+1][bottomTagStyles.length+1];

	}

        /**
         * Returns the statistics of top tags
         * @return the statistics of top tags
         */
	public TagStatistic getTopStatistic() {
		return topStatistic;
	}

        /**
         * Returns the statistics of bottom tags
         * @return the statistics of bottom tags
         */
	public TagStatistic getBottomStatistic() {
		return bottomStatistic;
	}

        /**
         * Returns the number of styles of top tags
         * @return the number of styles of top tags
         */
	public int getTopStyleCount() {
		return topStatistic.getStyleCount();
	}

        /**
         * Returns the top style of a given index
         * @param index the index of a top style to be returned
         * @return the top style of a given index
         */
	public TagStyle getTopStyleAt(int index) {
		return topStatistic.getStyleAt(index);
	}

        /**
         * Returns the number of styles of bottom tags
         * @return the number of styles of bottom tags
         */
	public int getBottomStyleCount() {
		return bottomStatistic.getStyleCount();
	}

        /**
         * Returns the bottom style of a given index
         * @param index the index of a bottom style to be returned
         * @return the bottom style of a given index
         */
	public TagStyle getBottomStyleAt(int index) {
		return bottomStatistic.getStyleAt(index);
	}

        /**
         * Returns the index of a given style in top styles array
         * @param style the style which index is to be returned
         * @return the index of a given style in top styles array
         */
	public int indexOfTopStyle(TagStyle style) {
		return topStatistic.indexOf(style);
	}

        /**
         * Returns the index of a given style in bottom styles array
         * @param style the style which index is to be returned
         * @return the index of a given style in bottom styles array
         */
	public int indexOfBottomStyle(TagStyle style) {
		return bottomStatistic.indexOf(style);
	}

        /**
         * Adds a given value to the length of bottom selections
         * of the given style
         * @param index the index of the style in bottomStatistics
         * @param time the length of bottom selections to be added
         */
	public void addBottomStyleTime(int index, float time) {
		bottomStatistic.addStyleTime(index, time);
	}

        /**
         * Adds a given value to the length of bottom selections
         * of the given style
         * @param style the tag style
         * @param time the length of tagged bottom to be added
         */
	public void addBottomStyleTime(TagStyle style, float time) {
		bottomStatistic.addStyleTime(style, time);
	}

        /**
         * Returns the length of bottom selections of the given style
         * @param index the index of the style in bottomStatistics
         * @return the length of bottom selections of the given style
         */
	public float getBottomStyleTime(int index) {
		return bottomStatistic.getStyleTime(index);
	}

        /**
         * Returns the length of bottom selections of the given style
         * @param style the tag style
         * @return the length of bottom selections of the given style
         */
	public float getBottomStyleTime(TagStyle style) {
		return bottomStatistic.getStyleTime(style);
	}

        /**
         * Sets the length of bottom selections of the given style
         * @param index the index of the style in bottomStatistics
         * @param time the length of bottom selections to be set
         */
	public void setBottomStyleTime(int index, float time) {
		bottomStatistic.setStyleTime(index, time);
	}

        /**
         * Sets the length of bottom selections of the given style
         * @param style the tag style
         * @param time the length of bottom selections to be set
         */
	public void setBottomStyleTime(TagStyle style, float time) {
		bottomStatistic.setStyleTime(style, time);
	}

         /**
         * Adds a given value to the length of top selections
         * of the given style
         * @param index the index of the style in topStatistics
         * @param time the length of top selections to be added
         */
	public void addTopStyleTime(int index, float time) {
		topStatistic.addStyleTime(index, time);
	}

        /**
         * Adds a given value to the length of top selections
         * of the given style
         * @param style the tag style
         * @param time the length of tagged top to be added
         */
	public void addTopStyleTime(TagStyle style, float time) {
		topStatistic.addStyleTime(style, time);
	}

        /**
         * Returns the length of top selections of the given style
         * @param index the index of the style in topStatistics
         * @return the length of top selections of the given style
         */
	public float getTopStyleTime(int index) {
		return topStatistic.getStyleTime(index);
	}

        /**
         * Returns the length of top selections of the given style
         * @param style the tag style
         * @return the length of top selections of the given style
         */
	public float getTopStyleTime(TagStyle style) {
		return topStatistic.getStyleTime(style);
	}

        /**
         * Sets the length of top selections of the given style
         * @param index the index of the style in topStatistics
         * @param time the length of top selections to be set
         */
	public void setTopStyleTime(int index, float time) {
		topStatistic.setStyleTime(index, time);
	}

        /**
         * Sets the length of top selections of the given style
         * @param style the tag style
         * @param time the length of top selections to be set
         */
	public void setTopStyleTime(TagStyle style, float time) {
		topStatistic.setStyleTime(style, time);
	}

        /**
         * Adds the given time to the length of common part of two given tag
         * styles (one of top tags, one of bottom)
         * @param topIndex the index of the first style in topTags;
         * -1 if no tag
         * @param bottomIndex the index of the second style in bottomTags;
         * -1 if no tag
         * @param time the time value to be added in seconds
         */
	public void addStyleOverlay(int topIndex, int bottomIndex, float time) {
		styleOverlayMatrix[topIndex+1][bottomIndex+1] += time;
	}

        /**
         * Adds the given time to the length of common part of two given tag
         * styles (one of top tags, one of bottom)
         * @param topStyle the style of the top tag; null if no tag
         * @param bottomStyle the style of the bottom tag; null if no tag
         * @param time the time value to be added in seconds
         */
	public void addStyleOverlay(TagStyle topStyle, TagStyle bottomStyle, float time) {

		int topIndex;
		int bottomIndex;

		if (topStyle == null) {
			topIndex = -1;
		} else {
			topIndex = topStatistic.indexOf(topStyle);
			if (topIndex < 0) {
				throw new IllegalArgumentException("No such style [" + topStyle.toString() + "] in top tag");
			}
		}

		if (bottomStyle == null) {
			bottomIndex = -1;
		} else {
			bottomIndex = bottomStatistic.indexOf(bottomStyle);
			if (bottomIndex < 0) {
				throw new IllegalArgumentException("No such style [" + bottomStyle.toString() + "] in bottom tag");
			}
		}

		addStyleOverlay(topIndex, bottomIndex, time);

	}

	/**
         * Returns the length of common part of two given tag styles
         * (one of top tags, one of bottom)
         * @param topIndex the index of the first style in topTags;
         * -1 if no tag
         * @param bottomIndex the index of the second style in bottomTags;
         * -1 if no tag
         * @return the length of common part of two given tag styles
         */
	public float getStyleOverlay(int topIndex, int bottomIndex) {
		return styleOverlayMatrix[topIndex+1][bottomIndex+1];
	}

	/**
         * Returns the length of common part of two given tag styles
         * (one of top tags, one of bottom)
         * @param topStyle the style of the top tag; null if no tag
         * @param bottomStyle the style of the bottom tag; null if no tag
         * @return the length of common part of two given tag styles
         */
	public float getStyleOverlay(TagStyle topStyle, TagStyle bottomStyle) {

		int topIndex;
		int bottomIndex;

		if (topStyle == null) {
			topIndex = -1;
		} else {
			topIndex = topStatistic.indexOf(topStyle);
			if (topIndex < 0) {
				throw new IllegalArgumentException("No such style [" + topStyle.toString() + "] in top tag");
			}
		}

		if (bottomStyle == null) {
			bottomIndex = -1;
		} else {
			bottomIndex = bottomStatistic.indexOf(bottomStyle);
			if (bottomIndex < 0) {
				throw new IllegalArgumentException("No such style [" + bottomStyle.toString() + "] in bottom tag");
			}
		}

		return getStyleOverlay(topIndex, bottomIndex);

	}

	/**
         * Sets the length of common part of two given tag styles
         * (one of top tags, one of bottom)
         * @param topIndex the index of the first style in topTags;
         * -1 if no tag
         * @param bottomIndex the index of the second style in bottomTags;
         * -1 if no tag
         * @param time the time value to be set in seconds
         */
	public void setStyleOverlay(int topIndex, int bottomIndex, float time) {
		styleOverlayMatrix[topIndex+1][bottomIndex+1] = time;
	}

	/**
         * Sets the length of common part of two given tag styles
         * (one of top tags, one of bottom)
         * @param topStyle the style of the top tag; null if no tag
         * @param bottomStyle the style of the bottom tag; null if no tag
         * @param time the time value to be set in seconds
         */
	public void setStyleOverlay(TagStyle topStyle, TagStyle bottomStyle, float time) {

		int topIndex;
		int bottomIndex;

		if (topStyle == null) {
			topIndex = -1;
		} else {
			topIndex = topStatistic.indexOf(topStyle);
			if (topIndex < 0) {
				throw new IllegalArgumentException("No such style [" + topStyle.toString() + "] in top tag");
			}
		}

		if (bottomStyle == null) {
			bottomIndex = -1;
		} else {
			bottomIndex = bottomStatistic.indexOf(bottomStyle);
			if (bottomIndex < 0) {
				throw new IllegalArgumentException("No such style [" + bottomStyle.toString() + "] in bottom tag");
			}
		}

		setStyleOverlay(topIndex, bottomIndex, time);

	}

        /**
         * Writes the current object parameters to a given {@link Writer writer}
         * using given column and row separator
         * @param writer the writer to which current object parameters will be
         * written
         * @param columnSeparator a string used to separate columns
         * @param rowSeparator a string used to separate rows
         * @param userObject true if lengths of common parts should be exported
         * as percent of all tags of that style, false otherwise
         * @throws IOException if an I/O error occurs
         */
	@Override
	public void export(Writer writer, String columnSeparator, String rowSeparator, Object userObject) throws IOException {

		boolean exportPercent = false;
		if (userObject != null && (userObject instanceof Boolean)) {
			exportPercent = ((Boolean) userObject).booleanValue();
		}

		int topCnt = topStatistic.getStyleCount();
		int bottomCnt = bottomStatistic.getStyleCount();
		int i,e;
		float divider;

		writer.append(columnSeparator);
		writer.append("(none)");
		for (i=0; i<bottomCnt; i++) {
			writer.append(columnSeparator);
			writer.append(bottomStatistic.getStyleAt(i).getName());
		}
		writer.append(rowSeparator);

		for (i=0; i<topCnt+1; i++) {
			if (i == 0) {
				writer.append("(none)");
			} else {
				writer.append(topStatistic.getStyleAt(i-1).getName());
			}
			for (e=0; e<bottomCnt+1; e++) {
				writer.append(columnSeparator);
				if (exportPercent) {
					divider = topStatistic.getStyleTime(i-1);
					if (divider == 0) {
						writer.append("-");
					} else {
						writer.append(Float.toString(100 *(styleOverlayMatrix[i][e] / divider)));
					}
				} else {
					writer.append(Float.toString(styleOverlayMatrix[i][e]));
				}
			}
			writer.append(rowSeparator);
		}

	}

}
