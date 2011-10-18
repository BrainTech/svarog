package org.signalml.math.geometry;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("polar3dPoint")
public class Polar3dPoint {

	private double theta;
	private double radius;
	private double asimuth;

	public Polar3dPoint() {
	}

	public Polar3dPoint(double theta, double radius, double asimuth) {
		this.theta = theta;
		this.radius = radius;
		this.asimuth = asimuth;
	}

	public double getAsimuth() {
		return asimuth;
	}

	public void setAsimuth(double asimuth) {
		this.asimuth = asimuth;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

}
