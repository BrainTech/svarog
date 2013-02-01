
/**
 * This is the example plug-in to demonstrate how to use the plug-in interface.
 * It allows to:
 * <ol>
 * <li>show first 100 samples from all channels of the active signal in the
 * property tab (the tab at the bottom),</li>
 * <li>show the active tag and all tags associated with the active signal,</li>
 * <li>add a custom (precise) tag with the use of a dialog window,</li>
 * <li>open a book document,</li>
 * <li>display the events (addition/removal/change) associated with tags and
 * tag styles.</li>
 * </ol>
 * These features are implemented in classes:
 * <ol>
 * <li>{@code SamplesPanel} and {@code SamplesPanelAction},</li>
 * <li>{@code ShowTagAction}, {@code ShowActiveTagAction} and
 * {@code ShowTagsFromActiveSignalAction},</li>
 * <li>{@code PreciseTagAction} and {@code PreciseTagDialog},</li>
 * <li>{@code OpenBookAction} and {@code OpenBookDialog},</li>
 * <li>{@code ExamplePluginListener}.</li>
 * </ol>
 * <p>
 * To create such plug-in it is necessary to include some libraries:
 * <ul>
 * <li>{@code svarog-1.0.4.jar} - to use Svarog classes, for example
 * the plug-in interface ;) (can be found in {@code svarog\target}),</li>
 * <li>{@code log4j.jar} - to use logging interface consistent with that used
 * in Svarog
 * ({@code svarog\target\svarog-1.0.4-full\svarog-1.0.4\lib}),
 * </li>
 * <li>{@code spring-context.jar} - to use classes such as {@code Tag}
 * and {@code TagStyle}
 * ({@code svarog\target\svarog-1.0.4-full\svarog-1.0.4\lib}),
 * </li>
 * </ul>
 * <p>
 * To use this plug-in you need to export this project as a {@code .jar} file
 * and put both {@code .jar} and {@code .xml} file in the {@code plugin}
 * directory in Svarog profile directory.
 */
package org.signalml.plugin.exampleplugin;