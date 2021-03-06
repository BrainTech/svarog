/* GeneralConfiguration.java created 2007-09-13
 *
 */
package org.signalml.app.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.log4j.Logger;

/** GeneralConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("generalconfig")
public class GeneralConfiguration extends AbstractXMLConfiguration {

	protected static final Logger logger = Logger.getLogger(GeneralConfiguration.class);

	private String locale;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getStandardFilename() {
		return "general-config.xml";
	}
}
