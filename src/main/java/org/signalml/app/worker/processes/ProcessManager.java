package org.signalml.app.worker.processes;

import java.util.HashMap;
import java.util.List;

/**
 * Manager responsible for starting and killing processes.
 *
 * @author Tomasz Sawicki
 */
public class ProcessManager {

        /**
         * Created {@link ProcessSubject} objects.
         */
        private HashMap<String, ProcessSubject> createdProcesses;

        /**
         * Prepared processes.
         */
        private HashMap<String, ProcessSubject> preparedProcesses;

        /**
         * Running processes.
         */
        private HashMap<String, ProcessSubject> runningProcesses;

        /**
         * Default constructor.
         */
        public ProcessManager() {

                createdProcesses = new HashMap<String, ProcessSubject>();
                preparedProcesses = new HashMap<String, ProcessSubject>();
                runningProcesses = new HashMap<String, ProcessSubject>();
        }

        /**
         * Creates a new {@link ProcessSubject} object.
         *
         * @param id process id
         * @param path path to the executable file
         * @param parameters command line parameters
         */
        public void createProcess(String id, String path, List<String> parameters) {

                createdProcesses.put(id, new ProcessSubject(id, path, parameters, this));
        }

        /**
         * Prepares a process.
         *
         * @param id process id
         * @param delay process delay
         */
        public void prepareProcess(String id, long delay) {

                ProcessSubject process = createdProcesses.remove(id);
                process.setDelay(delay);
                preparedProcesses.put(id, process);
        }

        /**
         * Runs all prepared processes.
         */
        public void runProcesses() {

                for (String id : preparedProcesses.keySet()) {

                        preparedProcesses.get(id).startProcess();
                        runningProcesses.put(id, preparedProcesses.get(id));
                }

                preparedProcesses.clear();
        }

        /**
         * Kills the specified process.
         *
         * @param id process id
         */
        public void killProcess(String id) {

                runningProcesses.remove(id).killProcess();
        }

        /**
         * Kills all running processes
         */
        public void killAll() {

                for (String id : runningProcesses.keySet()) {
                        runningProcesses.get(id).killProcess();
                }

                runningProcesses.clear();
        }

        /**
         * Raised when a process ends in a different way then via the {@link #killAll()} of
         * {@link #killProcess(java.lang.String)} methods. Kills all other running processes.
         *
         * @param id process id
         */
        public void processEnded(String id, int exitCode) {

                runningProcesses.remove(id);
                killAll();
        }
}