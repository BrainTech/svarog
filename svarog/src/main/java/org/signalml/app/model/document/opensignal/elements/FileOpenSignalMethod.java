/* OpenFileSignalMethod.java created 2011-03-12
 *
 */

package org.signalml.app.model.document.opensignal.elements;


/**
 * This class represents a method which should be used to open a file signal
 * determining whether it is a raw signal or a SignalML codec should be used
 * to open it.
 *
 * @author Piotr Szachewicz
 */
public enum FileOpenSignalMethod {

	AUTODETECT,
	ASCII,
	RAW,
	SIGNALML_CODEC;

};
