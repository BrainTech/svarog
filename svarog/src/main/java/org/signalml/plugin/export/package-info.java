/**
 * This package (with the sub-packages) is the only package meant to be used
 * by plug-in developer.
 * Consists mostly of interfaces, that specify the way of communication between
 * plug-ins and Svarog.
 * Contains also a few classes that are not necessary for plug-in developer,
 * but may help him implement some of the interfaces.
 * <p>
 * Directly this package contains two interfaces
 * ({@link org.signalml.plugin.export.Plugin} and
 * {@link org.signalml.plugin.export.SvarogAccess})
 * and two classes
 * (exceptions: {@link org.signalml.plugin.export.SignalMLException} and
 * {@link org.signalml.plugin.export.NoActiveObjectException}).
 */
package org.signalml.plugin.export;