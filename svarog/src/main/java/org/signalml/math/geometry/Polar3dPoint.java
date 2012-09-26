package org.signalml.math.geometry;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.awt.geom.Point2D;

/**
 * This class represents a point in the 3d polar coordinate system.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("polar3dPoint")
public class Polar3dPoint {

	/**
	 * The magnitude of the line from the origin of the polar coordinate system
	 * to the point representing by this class.
	 */
	private double radius;
	/**
	 * The angle (in degrees) between the projection of the line on the
	 * X-Y axis plane and the X axis.
	 */
	private double theta;
	/**
	 * The angle (in degrees) between the line and the plane defined by the
	 * X and Y axes.
	 */
	private double phi;

	public Polar3dPoint() {
	}

	/**
	 * Creates a new point with the given parameters.
	 * @param radius the magnitude of the line from the origin
	 * of the polar coordinate system to the point representing by this class.
	 * @param theta the angle (in degrees) between the projection of the line on the
	 * X-Y axis plane and the X axis.
	 * @param phi the angle (in degress) between the line and the plane defined by the
	 * X and Y axes.
	 */
	public Polar3dPoint(double radius, double theta, double fi) {
		this.theta = theta;
		this.radius = radius;
		this.phi = fi;
	}

	/**
	 * Returns the magnitude of the line from the origin
	 * of the polar coordinate system to the point representing by this class.
	 * @return
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the magnitude of the line from the origin
	 * of the polar coordinate system to the point representing by this class.
	 * @param radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Returns the angle (in degrees) between the line and the plane defined by the
	 * X and Y axes.
	 * @return
	 */
	public double getPhi() {
		return phi;
	}

	/**
	 * Sets the angle (in degrees) between the line and the plane defined by the
	 * X and Y axes.
	 * @param phi
	 */
	public void setPhi(double phi) {
		this.phi = phi;
	}

	/**
	 * Returns the angle (in degrees) between the projection of the line on the
	 * X-Y axis plane and the X axis.
	 * @return
	 */
	public double getTheta() {
		return theta;
	}

	/**
	 * Sets the angle (in degrees) between the projection of the line on the
	 * X-Y axis plane and the X axis.
	 * @param theta
	 */
	public void setTheta(double theta) {
		this.theta = theta;
	}

	/**
	 * Converts this point to a point in 2d cartesian coordinates.
	 * @param center the position of the origin of the polar coordinates system
	 * defined in the Cartesian system
	 * @param maxRadius the magnitude by which the radius of this {@link Polar3dPoint}
	 * should be multiplied when converting to the Cartesian polar system.
	 * @return the polar point converted to a Cartesian 2D coordianate system
	 */
	public Point2D convertTo2DPoint(Point2D center, float maxRadius) {

		double radius = getRadius() * (90.0 - getPhi()) / 90.0;

		double x = center.getX() - Math.sin(Math.toRadians(getTheta())) * radius * (maxRadius);
		double y = center.getY() - Math.cos(Math.toRadians(getTheta())) * radius * (maxRadius);

		return new Point2D.Double(x, y);

	}
}
