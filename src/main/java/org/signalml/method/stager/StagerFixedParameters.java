/* StagerFixedParameters.java created 2008-02-08
 *
 */

package org.signalml.method.stager;

/** StagerFixedParameters
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerFixedParameters {

	private double swaWidthCoeff;
	private double alphaPerc1;
	private double alphaPerc2;
	private double corrCoeffRems;
	private double corrCoeffSems;

	public double getSwaWidthCoeff() {
		return swaWidthCoeff;
	}

	public void setSwaWidthCoeff(double swaWidthCoeff) {
		this.swaWidthCoeff = swaWidthCoeff;
	}

	public double getAlphaPerc1() {
		return alphaPerc1;
	}

	public void setAlphaPerc1(double alphaPerc1) {
		this.alphaPerc1 = alphaPerc1;
	}

	public double getAlphaPerc2() {
		return alphaPerc2;
	}

	public void setAlphaPerc2(double alphaPerc2) {
		this.alphaPerc2 = alphaPerc2;
	}

	public double getCorrCoeffRems() {
		return corrCoeffRems;
	}

	public void setCorrCoeffRems(double corrCoeffRems) {
		this.corrCoeffRems = corrCoeffRems;
	}

	public double getCorrCoeffSems() {
		return corrCoeffSems;
	}

	public void setCorrCoeffSems(double corrCoeffSems) {
		this.corrCoeffSems = corrCoeffSems;
	}

	public double[] getFixedParameterArray() {
		return new double[] {
		               swaWidthCoeff,
		               alphaPerc1,
		               alphaPerc2,
		               corrCoeffRems,
		               corrCoeffSems
		       };
	}

}
