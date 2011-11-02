package org.signalml.app.worker.processes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manager responsible for starting and killing processes.
 * It's a singleton.
 *
 * @author Tomasz Sawicki
 */
public class ProcessManager {

        public static final String PROCESS_ENDED = "processEnded";

        /**
         * Currently running processes.
         */
        private HashMap<String, ProcessSubject> runningProcesses;

        /**
         * Listeners list
         */
        private List<PropertyChangeListener> listeners;

        /**
         * The only instance of this object.
         */
        private static ProcessManager instance;

        /**
         * Static method returning the instance of this object.
         * 
         * @return instance of this object.
         */
        public static ProcessManager getInstance() {
            if (instance == null) {
                synchronized (ProcessManager.class) {
                    if (instance == null)
                        instance = new ProcessManager();
                }
            }

            return instance;
        }

        /**
         * Default constructor. It's private because this class is a singleton.
         */
        private ProcessManager() {

                runningProcesses = new HashMap<String, ProcessSubject>();
                listeners = new ArrayList<PropertyChangeListener>();
        }

        /**
         * Creates a new {@link ProcessSubject} object and runs it.
         *
         * @param id process id
         * @param path path to the executable file
         * @param parameters command line parameters
         */
        public synchronized void runProcess(String id, String path, List<String> parameters) {

                ProcessSubject newProcess = new ProcessSubject(id, path, parameters, this);
                runningProcesses.put(id, newProcess);
                newProcess.startProcess();
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
         * Notifies attached listeners with a "processEnded" property change. The new value
         * is a {@link ProcessExitData} object. If exitCode == null then the process didn't
         * event start (executable file didn't exist).
         *
         * @param id process id
         */
        public void processEnded(String id, Integer exitCode) {

                runningProcesses.remove(id);
                killAll();

                for (PropertyChangeListener listener : listeners)
                        try {
                                listener.propertyChange(new PropertyChangeEvent(this, PROCESS_ENDED, null, new ProcessExitData(id, exitCode)));
                        } catch (Exception ex) {
                        }
        }

        /**
         * Adds a property change listener.
         *
         * @param listener the listener
         */
        public void addPropertyChangeListener(PropertyChangeListener listener) {

                listeners.add(listener);
        }

        /**
         * Removes a property change listner.
         *
         * @param listener the listener
         */
        public void removePropertyChangeListener(PropertyChangeListener listener) {

                listeners.remove(listener);
        }
}