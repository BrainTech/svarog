package org.signalml.util;

import java.io.PrintStream;

/**
 * Synchronized version of java.io.PrintStream.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 *
 */
public class SynchronizedPrintStream {
    private PrintStream stream;

    public SynchronizedPrintStream(PrintStream s) {
        stream = s;
    }

    public synchronized void println(String s) {
        stream.println(s);
    }
    
    // add more methods if you like...
}
