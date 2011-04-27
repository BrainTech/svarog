/* SignalMLCodecConfiguration.java created 2007-09-18
 *
 */

package org.signalml.app.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SignalMLCodecConfiguration
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("signalml-codecs")
public class SignalMLCodecConfiguration extends AbstractXMLConfiguration {

	private SignalMLCodecDescriptor[] codecs;

	public SignalMLCodecConfiguration(SignalMLCodecDescriptor[] codecs) {
		super();
		this.codecs = codecs;
	}

	public SignalMLCodecConfiguration() {
	}

	@Override
	public String getStandardFilename() {
		return "signalml-codecs.xml";
	}

	public SignalMLCodecDescriptor[] getCodecs() {
		return codecs;
	}

	public void setCodecs(SignalMLCodecDescriptor[] codecs) {
		this.codecs = codecs;
	}

}
