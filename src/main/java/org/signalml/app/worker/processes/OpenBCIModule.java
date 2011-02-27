package org.signalml.app.worker.processes;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.config.preset.Preset;

/**
 * Contains information about an openbci module.
 *
 * @author Tomasz Sawicki
 */
@XStreamAlias("module")
public class OpenBCIModule implements Preset {

        /**
         * Module name.
         */
        private String name;
        
        /**
         * Module path.
         */
        private String path;

        public OpenBCIModule() {
                this.path = "";
                this.name = "";
        }

        @Override
        public String getName() {
                return name;
        }

        @Override
        public void setName(String name) {
                this.name = name;
        }

        public String getPath() {
                return path;
        }

        public void setPath(String path) {
                this.path = path;
        }

        @Override
        public String toString() {
                return name;
        }
}
