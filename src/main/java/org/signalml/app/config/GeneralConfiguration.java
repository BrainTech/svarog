/* GeneralConfiguration.java created 2007-09-13
 *
 */
package org.signalml.app.config;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/** GeneralConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("generalconfig")
public class GeneralConfiguration extends AbstractXMLConfiguration {

	protected static final Logger logger = Logger.getLogger(GeneralConfiguration.class);

	private String locale;

	@XStreamOmitField
	private boolean profileDefault;

	@XStreamOmitField
	private String profilePath;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isProfileDefault() {
		return profileDefault;
	}

	public void setProfileDefault(boolean profileDefault) {
		this.profileDefault = profileDefault;
	}

	public String getProfilePath() {
		return profilePath;
	}

	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}

	public String getStandardFilename() {
		return "general-config.xml";
	}
}
