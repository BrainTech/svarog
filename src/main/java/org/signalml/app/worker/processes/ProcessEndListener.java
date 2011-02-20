package org.signalml.app.worker.processes;

/**
 * Interface representing an object waiting for the end
 * of some processes.
 *
 * @author Tomasz Sawicki
 */
public interface ProcessEndListener {

        public void processEnded(String id, int exitCode);
}
