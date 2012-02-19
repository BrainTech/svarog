/* AbstractWignerMapProvider.java created 2008-03-11
 *
 */

package org.signalml.domain.book;

import org.signalml.exception.SanityCheckException;
import pl.edu.fuw.MP.WignerMap.WignerMap;


/** AbstractWignerMapProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 			(based on code sent by Dobieslaw Ircha)
 */
public abstract class AbstractWignerMapProvider {

	protected WignerMapScaleType scaleType = WignerMapScaleType.NORMAL;

	protected int width;
	protected int height;

	protected double minFrequency;
	protected double maxFrequency;

	protected double minPosition;
	protected double maxPosition;

	protected double[][] map;
	protected double[][] normalMap;

	protected boolean mapDirty = true;
	protected boolean normalMapDirty = true;

	protected float samplingFrequency;

	public AbstractWignerMapProvider(float samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public WignerMapScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(WignerMapScaleType scaleType) {
		if (this.scaleType != scaleType) {
			this.scaleType = scaleType;
			mapDirty = true;
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if (this.width != width) {
			this.width = width;
			map = null;
			normalMap = null;
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		if (this.height != height) {
			this.height = height;
			map = null;
			normalMap = null;
		}
	}

	public void setSize(int width, int height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			map = null;
			normalMap = null;
		}
	}

	public double getMinFrequency() {
		return minFrequency;
	}

	public void setMinFrequency(double minFrequency) {
		if (this.minFrequency != minFrequency) {
			this.minFrequency = minFrequency;
			mapDirty = true;
			normalMapDirty = true;
		}
	}

	public double getMaxFrequency() {
		return maxFrequency;
	}

	public void setMaxFrequency(double maxFrequency) {
		if (this.maxFrequency != maxFrequency) {
			this.maxFrequency = maxFrequency;
			mapDirty = true;
			normalMapDirty = true;
		}
	}

	public double getMinPosition() {
		return minPosition;
	}

	public void setMinPosition(double minPosition) {
		if (this.minPosition != minPosition) {
			this.minPosition = minPosition;
			mapDirty = true;
			normalMapDirty = true;
		}
	}

	public double getMaxPosition() {
		return maxPosition;
	}

	public void setMaxPosition(double maxPosition) {
		if (this.maxPosition != maxPosition) {
			this.maxPosition = maxPosition;
			mapDirty = true;
			normalMapDirty = true;
		}
	}

	public void setRange(double minFrequency, double maxFrequency, double minPosition, double maxPosition) {

		if (
		        this.minFrequency != minFrequency
		        || this.maxFrequency != maxFrequency
		        || this.minPosition != minPosition
		        || this.maxPosition != maxPosition
		) {

			this.minFrequency = minFrequency;
			this.maxFrequency = maxFrequency;
			this.minPosition = minPosition;
			this.maxPosition = maxPosition;
			mapDirty = true;
			normalMapDirty = true;

		}

	}

	public void setRangeAndSize(double minFrequency, double maxFrequency, double minPosition, double maxPosition, int width, int height) {

		if (this.width != width || this.height != height) {

			map = null;
			normalMap = null;

		} else if (
		        this.minFrequency != minFrequency
		        || this.maxFrequency != maxFrequency
		        || this.minPosition != minPosition
		        || this.maxPosition != maxPosition
		) {

			mapDirty = true;
			normalMapDirty = true;

		} else {
			// all equal
			return;
		}

		this.minFrequency = minFrequency;
		this.maxFrequency = maxFrequency;
		this.minPosition = minPosition;
		this.maxPosition = maxPosition;
		this.width = width;
		this.height = height;

	}

	public double[][] getMap() {

		if (scaleType == WignerMapScaleType.NORMAL) {
			return getNormalMap();
		}
		if (map == null) {
			map = new double[width][height];
			mapDirty = true;
		}
		if (mapDirty) {
			map = scaleMap(map, getNormalMap(), width, height, scaleType);
		}

		return map;

	}

	public abstract double[][] getNormalMap();

	public void calculateNormalMap(StandardBookSegment segment, double[][] map) {

		if (segment == null || segment.getAtomCount() == 0) {
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					map[x][y] = 0;
				}
			}
			return;
		}

		// XXX test Wigner map calculation below, remove when not needed

		/*

		StandardBookAtom atom = segment.getAtomAt(0);

		double stretchX = (maxPosition-minPosition) / segment.getSegmentTimeLength();
		double stretchY = (maxFrequency-minFrequency) / (samplingFrequency/2);

		double pixelPerSecond = (width / segment.getSegmentTimeLength()) / stretchX;
		double pixelPerHz = (height / (samplingFrequency/2) ) / stretchY;

		int cenX = (int) Math.round( (atom.getTimePosition()-minPosition)*pixelPerSecond );
		int cenY = (int) Math.round( (atom.getHzFrequency()-minFrequency)*pixelPerHz );

		for( int x=0; x<width; x++ ) {
			for( int y=0; y<height; y++ ) {
				map[x][y] = (1.0 + Math.sin( (2*Math.PI/300) * Math.sqrt(Util.sqr(stretchX*((double)(x-cenX))) + Util.sqr(stretchY*((double)(y-cenY))) ) ) ) / 2;
			}
		}

		*/

		int pointMinPosition = (int) Math.round(minPosition * samplingFrequency);
		int pointMaxPosition = (int) Math.round(maxPosition * samplingFrequency);

		WignerMap wignerMap = new WignerMap(width, height, pointMinPosition, pointMaxPosition, minFrequency, maxFrequency);
		wignerMap.setBook(segment);

		double[][] normMap = wignerMap.getWignerMap();
		double scaleFactor = 1.0 / (wignerMap.getMaxVal()-wignerMap.getMinVal());

		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				map[x][y] = normMap[x][y] * scaleFactor;
			}
		}

	}

	public double[][] scaleMap(double[][] target, double[][] normalMap, int width, int height, WignerMapScaleType scaleType) {

		if (scaleType == WignerMapScaleType.NORMAL) {
			// doesn't copy to target in this case!!!
			return normalMap;
		}

		double[][] map;
		if (target != null) {
			map = target;
		} else {
			map = new double[width][height];
		}

		double min = Double.MAX_VALUE;
		double max = 0;
		if (scaleType == WignerMapScaleType.LOG) {
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					map[x][y] = Math.log(1.0+normalMap[x][y]);
					if (map[x][y] > max) {
						max = map[x][y];
					}
					if (map[x][y] < min) {
						min = map[x][y];
					}
				}
			}
		}
		else if (scaleType == WignerMapScaleType.SQRT) {
			for (int x=0; x<width; x++) {
				for (int y=0; y<height; y++) {
					map[x][y] = Math.sqrt(normalMap[x][y]);
					if (map[x][y] > max) {
						max = map[x][y];
					}
					if (map[x][y] < min) {
						min = map[x][y];
					}
				}
			}
		} else {
			throw new SanityCheckException("Unsupported scale type [" + scaleType.name() + "]");
		}

		// normalize map
		double factor = 1.0 / (max-min);
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				map[x][y] = (map[x][y] - min) * factor;
			}
		}

		return map;

	}

	public boolean isDirty() {
		if (map == null || normalMap == null || mapDirty || normalMapDirty) {
			return true;
		}
		return false;
	}

}
