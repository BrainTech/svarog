/* MethodPresetManager.java created 2007-10-28
 *
 */

package org.signalml.app.method;

import org.signalml.app.config.preset.AbstractPresetManager;
import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MethodPresetManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("presets")
public class MethodPresetManager extends AbstractPresetManager {

	private static final long serialVersionUID = 1L;

	private String methodName;
	private Class<?> parameterClass;

	private Preset defaultPreset;

	public MethodPresetManager(String methodName, Class<?> parameterClass) {
		this.methodName = methodName;
		this.parameterClass = parameterClass;
	}

	@Override
	public String getStandardFilename() {
		return methodName + "-method-config.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return parameterClass;
	}

	public Preset getDefaultPreset() {
		return defaultPreset;
	}

	public void setDefaultPreset(Preset defaultPreset) {
		this.defaultPreset = defaultPreset;
	}

}
