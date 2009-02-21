/* MontageSampleFilterListener.java created 2008-02-01
 * 
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/** MontageSampleFilterListener
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageSampleFilterListener extends EventListener {

	public void filterAdded( MontageSampleFilterEvent ev );

	public void filterChanged( MontageSampleFilterEvent ev );
	
	public void filterRemoved( MontageSampleFilterEvent ev );
	
	public void filterExclusionChanged( MontageSampleFilterEvent ev );
	
	public void filtersChanged( MontageSampleFilterEvent ev );
	
}
