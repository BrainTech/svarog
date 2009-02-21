/* BoundedSpinnerModel.java created 2007-10-04
 * 
 */

package org.signalml.app.model;

import javax.swing.SpinnerModel;

/** BoundedSpinnerModel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface BoundedSpinnerModel extends SpinnerModel {

	Comparable<? extends Number> getMinimum();
	
	Comparable<? extends Number> getMaximum();
	
}
