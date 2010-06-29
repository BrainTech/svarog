/* DoublePrecisionTest.java created 2007-12-07
 *
 */

package org.signalml.test;

/** DoublePrecisionTest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DoublePrecisionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int sampleCount = (int) 2.1e9;
		float samplingFrequency = 128F;

		double timeZoomFactor = 1;

		int clipLeft = (int)(sampleCount*timeZoomFactor) - 2000;
		int clipRight = (int)(sampleCount*timeZoomFactor) - 1000;

		double pixelPerSecond = samplingFrequency * timeZoomFactor;

		double startTime = clipLeft / pixelPerSecond;
		double endTime = clipRight / pixelPerSecond;

		int firstSample = (int) Math.floor(clipLeft / timeZoomFactor) - 1;
		int lastSample = (int) Math.ceil(clipRight / timeZoomFactor) + 1;

		double firstRealX = firstSample * timeZoomFactor;
		double lastRealX = lastSample * timeZoomFactor;

		System.out.println("Sample count: " + sampleCount);
		System.out.println("SF: " + samplingFrequency);
		System.out.println("Clip left: " + clipLeft);
		System.out.println("Clip right: " + clipRight);
		System.out.println("TZ factor: " + timeZoomFactor);
		System.out.println("PPS: " + pixelPerSecond);
		System.out.println("Start time: " + startTime);
		System.out.println("End time: " + endTime);
		System.out.println("First sample: " + firstSample);
		System.out.println("Last sample: " + lastSample);
		System.out.println("First RX: " + (int) firstRealX);
		System.out.println("Last RX: " + (int) lastRealX);

	}

}
