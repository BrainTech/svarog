package org.signalml.math.geometry;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.awt.geom.Point2D;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("polar3dPoint")
public class Polar3dPoint {

	private double theta;
	private double radius;
	private double fi;

	public Polar3dPoint() {
	}

	public Polar3dPoint(double theta, double radius, double fi) {
		this.theta = theta;
		this.radius = radius;
		this.fi = fi;
	}

	public double getFi() {
		return fi;
	}

	public void setFi(double fi) {
		this.fi = fi;
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

	public Point2D convertTo2DPoint(Point2D center, float maxRadius) {
		double radiusProjectionLength= Math.abs(Math.cos(getFi()) * getRadius());

		double x = center.getX() - Math.sin(getTheta()) * radiusProjectionLength * (maxRadius);
		double y = center.getY() - Math.cos(getTheta()) * radiusProjectionLength * (maxRadius);

		return new Point2D.Double(x,y);
	}

}
