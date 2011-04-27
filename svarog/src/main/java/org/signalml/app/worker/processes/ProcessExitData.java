package org.signalml.app.worker.processes;

/**
 * Data describing an exiting process.
 *
 * @author Tomasz Sawicki
 */
public class ProcessExitData {

        /**
         * Process id.
         */
        private String id;

        /**
         * The exit code.
         */
        private Integer exitCode;

        public ProcessExitData(String id, Integer exitCode) {
                this.id = id;
                this.exitCode = exitCode;
        }

        public Integer getExitCode() {
                return exitCode;
        }

        public String getId() {
                return id;
        }
}
