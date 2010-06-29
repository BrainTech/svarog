/* TagStatistic.java created 2007-12-04
 *
 */

package org.signalml.domain.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import org.signalml.app.model.WriterExportableTable;
import org.signalml.util.Util;

/** TagStatistic
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStatistic implements WriterExportableTable {

	protected float totalLength;
	protected TagStyle[] tagStyles;

	protected HashMap<TagStyle,Integer> styleIndices;

	protected float[] styleTimes;

	protected TagStatistic() {

	}

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

	public TagStatistic(TagStyle[] tagStyles, float totalLength) {
		init(tagStyles, totalLength);
	}

	public float getTotalLength() {
		return totalLength;
	}

	public String getTotalLengthPretty() {
		return Util.getPrettyTimeString(totalLength);
	}

	public int getStyleCount() {
		return tagStyles.length;
	}

	public TagStyle getStyleAt(int index) {
		return tagStyles[index];
	}

	public int indexOf(TagStyle style) {
		Integer idx = styleIndices.get(style);
		if (idx == null) {
			return -1;
		}
		return idx.intValue();
	}

	public float getStyleTime(int index) {
		return styleTimes[index+1];
	}

	public void setStyleTime(int index, float time) {
		styleTimes[index+1] = time;
	}

	public void addStyleTime(int index, float time) {
		styleTimes[index+1] += time;
	}

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
