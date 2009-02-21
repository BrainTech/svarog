/* MRUDConfiguration.java created 2007-09-20
 * 
 */

package org.signalml.app.config;

import org.signalml.app.document.MRUDEntry;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MRUDConfiguration
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mrud-list")
public class MRUDConfiguration extends AbstractXMLConfiguration {

	private MRUDEntry[] mruds;
	
	public MRUDConfiguration() {
		super();
	}

	public MRUDConfiguration(MRUDEntry[] mruds) {
		super();
		this.mruds = mruds;
	}

	public MRUDEntry[] getMruds() {
		return mruds;
	}

	public void setMruds(MRUDEntry[] mruds) {
		this.mruds = mruds;
	}

	@Override
	public String getStandardFilename() {
		return "mrud-list.xml";
	}

}
