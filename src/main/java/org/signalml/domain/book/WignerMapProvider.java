/* WingerMapProvider.java created 2008-03-03
 *
 */

package org.signalml.domain.book;

/** WingerMapProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 			(based on code sent by Dobieslaw Ircha)
 */
public class WignerMapProvider extends AbstractWignerMapProvider {

	private StandardBookSegment segment;

	public WignerMapProvider(float samplingFrequency) {
		super(samplingFrequency);
	}

	public StandardBookSegment getSegment() {
		return segment;
	}

	public void setSegment(StandardBookSegment segment) {
		if (this.segment != segment) {
			this.segment = segment;
			mapDirty = true;
			normalMapDirty = true;
		}
	}

	@Override
	public double[][] getNormalMap() {
		if (normalMap == null) {
			normalMap = new double[width][height];
			normalMapDirty = true;
		}
		if (normalMapDirty) {
			calculateNormalMap(segment, normalMap);
		}
		return normalMap;
	}

}
