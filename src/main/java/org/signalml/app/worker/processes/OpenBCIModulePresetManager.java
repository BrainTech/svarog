package org.signalml.app.worker.processes;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.HashMap;
import org.signalml.app.config.preset.AbstractPresetManager;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.util.XMLUtils;

/**
 * OpenBCI Module preset manager.
 *
 * @author Tomasz Sawicki
 */
@XStreamAlias("modules")
public class OpenBCIModulePresetManager extends AbstractPresetManager {

        @Override
        public String getStandardFilename() {
                return "modules.xml";
        }

        @Override
        public Class<?> getPresetClass() {
                return OpenBCIModule.class;
        }

        @Override
	public XStream getStreamer() {

		if (streamer == null)
			streamer = createOpenBCIModulePresetStreamer();
		return streamer;

	}

	private XStream createOpenBCIModulePresetStreamer() {

		XStream streamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForOpenBCIModule(streamer);
		streamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);

		return streamer;
	}

        /**
         * Returns the configuration.
         *
         * @return a HashMap - the key is module's name, value: module's path.
         */
        public HashMap<String, String> getModulesConfiguration() {

                HashMap<String, String> modulesConfiguration = new HashMap<String, String>();

                for (Preset preset : presets) {

                        OpenBCIModule module = (OpenBCIModule) preset;
                        modulesConfiguration.put(module.getName(), module.getPath());
                }

                return modulesConfiguration;
        }

        /**
         * Returns one module path.
         *
         * @param name name of the module
         * @return the path
         * @throws Exception when module's name can't be found
         */
        public String getModulePath(String name) throws Exception {

                for (Preset preset : presets) {

                        OpenBCIModule module = (OpenBCIModule) preset;
                        if (module.getName().equals(name)) {
                                return module.getPath();
                        }
                }

                throw new Exception();
        }
}
