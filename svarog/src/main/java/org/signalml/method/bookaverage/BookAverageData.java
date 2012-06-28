/* BookAverageData.java created 2008-03-22
 *
 */

package org.signalml.method.bookaverage;

import java.util.LinkedHashSet;

import org.signalml.app.view.book.palette.IWignerMapPalette;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.WignerMapScaleType;

/** BookAverageData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageData {

	private StandardBook book;

	private int width;
	private int height;

	private double minFrequency;
	private double maxFrequency;

	private double minPosition;
	private double maxPosition;

	private int minSegment;
	private int maxSegment;

	private LinkedHashSet<Integer> channels;

	private IWignerMapPalette palette;
	private WignerMapScaleType scaleType;

	public BookAverageData() {
		channels = new LinkedHashSet<Integer>();
	}

	public StandardBook getBook() {
		return book;
	}

	public void setBook(StandardBook book) {
		this.book = book;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getMinFrequency() {
		return minFrequency;
	}

	public void setMinFrequency(double minFrequency) {
		this.minFrequency = minFrequency;
	}

	public double getMaxFrequency() {
		return maxFrequency;
	}

	public void setMaxFrequency(double maxFrequency) {
		this.maxFrequency = maxFrequency;
	}

	public double getMinPosition() {
		return minPosition;
	}

	public void setMinPosition(double minPosition) {
		this.minPosition = minPosition;
	}

	public double getMaxPosition() {
		return maxPosition;
	}

	public void setMaxPosition(double maxPosition) {
		this.maxPosition = maxPosition;
	}

	public int getMinSegment() {
		return minSegment;
	}

	public void setMinSegment(int minSegment) {
		this.minSegment = minSegment;
	}

	public int getMaxSegment() {
		return maxSegment;
	}

	public void setMaxSegment(int maxSegment) {
		this.maxSegment = maxSegment;
	}

	public LinkedHashSet<Integer> getChannels() {
		return channels;
	}

	public void setChannels(LinkedHashSet<Integer> channels) {
		this.channels = channels;
	}

	public void replaceChannels(int[] array) {
		channels.clear();
		for (int i=0; i<array.length; i++) {
			channels.add(array[i]);
		}
	}

	public void addChannel(int channel) {
		channels.add(channel);
	}

	public void removeChannel(int channel) {
		channels.remove(new Integer(channel));
	}

	public IWignerMapPalette getPalette() {
		return palette;
	}

	public void setPalette(IWignerMapPalette palette) {
		this.palette = palette;
	}

	public WignerMapScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(WignerMapScaleType scaleType) {
		this.scaleType = scaleType;
	}

}


