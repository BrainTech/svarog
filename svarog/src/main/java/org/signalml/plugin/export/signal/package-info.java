/**
 * This package allows plug-ins to access logical elements of Svarog, such as
 * signal samples, tags, selections and documents.
 * More detailed description of these functionalities you may find in
 * {@link org.signalml.plugin.export.signal.SvarogAccessSignal}.
 * <p>
 * Passing parameters to and from plug-ins is done with the use of interfaces
 * (for example {@code Exported*},
 * {@link org.signalml.plugin.export.signal.SignalSamples SignalSamples},
 * {@link org.signalml.plugin.export.signal.ChannelSamples ChannelSamples}).
 * However to help developer in the implementation of these interfaces there are also
 * some classes that he may use but are not necessary
 * (for example
 * {@link org.signalml.plugin.export.signal.AbstractDocument AbstractDocument},
 * {@link org.signalml.plugin.export.signal.AbstractSignalTool AbstractSignalTool},
 * {@link org.signalml.plugin.export.signal.Tag Tag}).
 */
package org.signalml.plugin.export.signal;