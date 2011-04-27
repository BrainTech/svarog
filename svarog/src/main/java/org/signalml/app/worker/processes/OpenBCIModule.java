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

        /**
         * Module delay in miliseconds.
         */
        private Integer delay;

        /**
         * Module parameters.
         */
        private String parameters;

        public OpenBCIModule() {
                this.path = "";
                this.name = "";
                this.delay = 1000;
                this.parameters = "";
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

        public Integer getDelay() {
                return delay;
        }

        public void setDelay(Integer delay) {
                this.delay = delay;
        }

        public String getParameters() {
                return parameters;
        }

        public void setParameters(String parameters) {
                this.parameters = parameters;
        }

        @Override
        public String toString() {
                return name;
        }
}
