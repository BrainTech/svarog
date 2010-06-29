/* BookAverageResult.java created 2008-03-22
 *
 */

package org.signalml.method.bookaverage;

/** BookAverageResult
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageResult {

	private double[][] map;

	private float[] signal;
	private float[] reconstruction;

	public double[][] getMap() {
		return map;
	}

	public void setMap(double[][] map) {
		this.map = map;
	}

	public float[] getSignal() {
		return signal;
	}

	public void setSignal(float[] signal) {
		this.signal = signal;
	}

	public float[] getReconstruction() {
		return reconstruction;
	}

	public void setReconstruction(float[] reconstruction) {
		this.reconstruction = reconstruction;
	}

}
